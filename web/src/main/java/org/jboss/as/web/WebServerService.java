/*
* JBoss, Home of Professional Open Source
* Copyright 2010, Red Hat Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.as.web;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.management.MBeanServer;
import javax.naming.NamingException;
import javax.servlet.Servlet;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.JasperListener;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.ContextConfig;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.http.mapper.Mapper;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;


/**
 * Service configuring and starting the web container. {@code Connector}s are started 
 * separate, in their own service {@link ConnectorService}. 
 * 
 * @author Emanuel Muckenhuber
 */
class WebServerService implements WebServer, Service<WebServer> {

    private final static String JBOSS_WEB = "jboss.web";
    
    private Catalina catalina;
    private StandardService service;
    
    private final Collection<WebVirtualServerElement> virtualServers;
    private InjectedValue<MBeanServer> mbeanServer = new InjectedValue<MBeanServer>();

    WebServerService(final Collection<WebVirtualServerElement> virtualServers) {
        this.virtualServers = virtualServers;
    }
    
    /** {@inheritDoc} */
    public synchronized void start(StartContext context) throws StartException {
        
        // TODO do we really need the MBeanServer ? 
        // getRegistry().setMBeanServer(mbeanServer.getValue());
                
        final Catalina catalina = new Catalina();
        this.catalina = catalina;
        // TODO environment service, instead of sys prop?
        catalina.setCatalinaHome(System.getProperty("jboss.server.base.dir"));
        Logger.getLogger("org.jboss.web").info("CatalinaHome: " + catalina.getCatalinaHome());
        // getRegistry().registerComponent(catalina, new ObjectName(JBOSS_WEB_NAME + ":type=Catalina"),
        // "org.apache.catalina.startup.Catalina");
        
        final StandardServer server = new StandardServer();
        catalina.setServer(server);
        // getRegistry().registerComponent(server, new ObjectName(JBOSS_WEB_NAME + ":type=Server"),
        // "org.apache.catalina.startup.StandardServer");

        final StandardService service = new StandardService();
        this.service = service;
        service.setName(JBOSS_WEB);
        service.setServer(server);
        server.addService(service);
       
        final Engine engine = new StandardEngine();
        engine.setName(JBOSS_WEB);
        engine.setService(service);
        engine.setDefaultHost("localhost"); // HACK
        
        service.setContainer(engine);
        
        final AprLifecycleListener apr = new AprLifecycleListener();
        apr.setSSLEngine("on");
        
        server.addLifecycleListener(apr);
        server.addLifecycleListener(new JasperListener());

        // Create the virtual hosts
        for(final WebVirtualServerElement virtual : virtualServers) {
            engine.addChild(createHost(virtual));
        }

        try {
            catalina.create();
            server.initialize();
            catalina.start();
        } catch (Exception e) {
            throw new StartException(e);
        }
    }

    /** {@inheritDoc} */
    public synchronized void stop(StopContext context) {
        catalina.stop();
        catalina.destroy();
        catalina = null;
        service = null;
    }

    /** {@inheritDoc} */
    public synchronized WebServer getValue() throws IllegalStateException {
        return this;
    }
    
    /** {@inheritDoc} */
    public synchronized void addConnector(Connector connector) {
        this.service.addConnector(connector);
    }
    
    /** {@inheritDoc} */
    public synchronized void removeConnector(Connector connector) {
        this.service.removeConnector(connector);
    }
    
    /** {@inheritDoc} */
    public void addContext(Context context) {
    	
    }
    
    /** {@inheritDoc} */
    public void removeContext(Context context) {
    	// TODO Auto-generated method stub
    	
    }
    
    InjectedValue<MBeanServer> getMbeanServer() {
        return mbeanServer;
    }
    
    Registry getRegistry() {
        return Registry.getRegistry(null, null);
    }
    
    /**
     * Create a {@code Host} based on the web configuration.
     * 
     * @param element the virtual server configuration
     * @return the host
     */
    Host createHost(WebVirtualServerElement element) {
        Logger.getLogger("org.jboss.web").info("createHost");
        final StandardHost host = new StandardHost();
        host.setName(element.getName());
        host.setAppBase("/tmp");
        for(final String alias : element.getAliases()) {
            host.addAlias(alias);
        }
        // Access log
        final WebAccessLogElement logConfiguration = element.getAccessLog();
        if(logConfiguration != null) {
            host.addValve(WebServerUtil.createAccessLogValve(null, logConfiguration));
        }
        // Rewrite valve
        final WebRewriteElement rewriteConfiguration = element.getRewrite();
        if(rewriteConfiguration != null) {
            host.addValve(WebServerUtil.createRewriteValve(rewriteConfiguration));
        }
        // Add the default Servlet org.apache.catalina.servlets.DefaultServlet.class
        ContextConfig config = new ContextConfig();
        Context rootContext = catalina.createContext("", "/tmp", config);
        host.addChild(rootContext);
        Mapper map = rootContext.getMapper();
        map.setDefaultHostName(element.getName());
        Logger.getLogger("org.jboss.web").info("createHost: " + element.getName());
        
        Wrapper wrapper = rootContext.createWrapper();
        wrapper.setName("DefaultServlet");
        wrapper.setLoadOnStartup(1);
        wrapper.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        wrapper.addInitParameter("debug","99");
        wrapper.addInitParameter("listings", "true");
        rootContext.addChild(wrapper);
        
        rootContext.addServletMapping("/*", "DefaultServlet");
        rootContext.setIgnoreAnnotations(true);
        rootContext.setPrivileged(true);
        // Hacks...
        rootContext.setInstanceManager(new HackInstanceManager());
        Loader loader = new HackLoader();
        loader.setContainer(host);
        rootContext.setLoader(loader);
        
        Logger.getLogger("org.jboss.web").info("createHost: Done");
        return host;
    }
    
    class HackInstanceManager implements InstanceManager {

    	ClassLoader getClassLoader() {
    		return WebServer.class.getClassLoader();
    	}
    	
		@Override
		public void destroyInstance(Object arg0) throws IllegalAccessException,
				InvocationTargetException {
			
		}

		@Override
		public Object newInstance(String arg0) throws IllegalAccessException,
				InvocationTargetException, NamingException,
				InstantiationException, ClassNotFoundException {
			return getClassLoader().loadClass(arg0).newInstance();
		}

		@Override
		public Object newInstance(Class<?> arg0) throws IllegalAccessException,
				InvocationTargetException, NamingException,
				InstantiationException {
			return arg0.newInstance();
		}

		@Override
		public void newInstance(Object arg0) throws IllegalAccessException,
				InvocationTargetException, NamingException {

			// nada
		}

		@Override
		public Object newInstance(String arg0, ClassLoader arg1)
				throws IllegalAccessException, InvocationTargetException,
				NamingException, InstantiationException, ClassNotFoundException {
			return arg1.loadClass(arg0).newInstance();
		}
    	
    }
    
    class HackLoader implements Loader {

    	private Container container;
    	
		@Override
		public void addPropertyChangeListener(PropertyChangeListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addRepository(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void backgroundProcess() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ClassLoader getClassLoader() {
			return WebServer.class.getClassLoader();
		}

		@Override
		public Container getContainer() {
			return this.container;
		}

		@Override
		public String getInfo() {
			return "toto";
		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setContainer(Container arg0) {
			this.container = arg0;
		}
    	
    }
    
}

