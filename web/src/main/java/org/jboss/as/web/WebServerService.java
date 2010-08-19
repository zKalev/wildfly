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

import java.util.Collection;

import javax.management.MBeanServer;

import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.JasperListener;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Catalina;
import org.apache.tomcat.util.modeler.Registry;
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
        // TODO environment service, instead of sys prop?
        catalina.setCatalinaHome(System.getProperty("jboss.server.base.dir"));
        // getRegistry().registerComponent(catalina, new ObjectName(JBOSS_WEB_NAME + ":type=Catalina"),
        // "org.apache.catalina.startup.Catalina");
        
        final StandardServer server = new StandardServer();
        catalina.setServer(server);
        // getRegistry().registerComponent(server, new ObjectName(JBOSS_WEB_NAME + ":type=Server"),
        // "org.apache.catalina.startup.StandardServer");

        final StandardService service = new StandardService();
        service.setName(JBOSS_WEB);
        service.setServer(server);
        server.addService(service);
       
        final Engine engine = new StandardEngine();
        engine.setName(JBOSS_WEB);
        engine.setService(service);
        // engine.setDefaultHost(engineMetaData.getDefaultHost());
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
        this.catalina = catalina;
        this.service = service;
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
        final StandardHost host = new StandardHost();
        host.setName(element.getName());
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
        return host;
    }
    
}

