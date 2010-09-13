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
package org.jboss.as.web.deployment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.catalina.Loader;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.tomcat.InstanceManager;
import org.jboss.as.deployment.DeploymentPhases;
import org.jboss.as.deployment.attachment.VirtualFileAttachment;
import org.jboss.as.deployment.module.ModuleDeploymentProcessor;
import org.jboss.as.deployment.unit.DeploymentUnitContext;
import org.jboss.as.deployment.unit.DeploymentUnitProcessingException;
import org.jboss.as.deployment.unit.DeploymentUnitProcessor;
import org.jboss.as.web.WebServer;
import org.jboss.as.web.WebSubsystemElement;
import org.jboss.metadata.web.spec.ServletMappingMetaData;
import org.jboss.metadata.web.spec.ServletMetaData;
import org.jboss.metadata.web.spec.ServletsMetaData;
import org.jboss.metadata.web.spec.WebMetaData;
import org.jboss.logging.Logger;
import org.jboss.modules.Module;
import org.jboss.msc.service.BatchBuilder;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.vfs.VirtualFile;

/**
 * The web metadata deployment processor, creating a web context service
 * starting and registering the web application.
 *
 * @author Emanuel Muckenhuber
 */
public class WebMetaDataDeploymentProcessor implements DeploymentUnitProcessor {

    /** The deployment processor priority. */
    public static final long PRIORITY = DeploymentPhases.INSTALL_SERVICES.plus(300L);

    public void processDeployment(DeploymentUnitContext context) throws DeploymentUnitProcessingException {
        // This should be the merged, processed web metadata
        final WebMetaData metaData = context.getAttachment(WebParsingDeploymentProcessor.ATTACHMENT_KEY);
        if (metaData == null) {
            Logger.getLogger("org.jboss.web").warn("no web.xml found for " + context.getName());
            return; // nothing to do...
        }
        final VirtualFile deploymentRoot = VirtualFileAttachment.getVirtualFileAttachment(context);
        final Module module = context.getAttachment(ModuleDeploymentProcessor.MODULE_ATTACHMENT_KEY);
        if (module == null) {
            throw new DeploymentUnitProcessingException("failed to resolve module for deployment " + deploymentRoot);
        }
        final ClassLoader cl = module.getClassLoader();
        // Hack to find the class files.
        ClassLoader classLoader;
        try {
            classLoader = new WebClassLoader(deploymentRoot.getChild("WEB-INF/classes").getPhysicalFile(),cl);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            classLoader = cl;
        }

        // FIXME create context per host
        final String hostName = null;
        // The deployment name
        final String deploymentName = context.getName();
        // Create the context
        final StandardContext webContext = new StandardContext();
        final ContextConfig config = new ContextConfig();
        // Set the deployment root
        try {
            webContext.setDocBase(deploymentRoot.getPhysicalFile().getAbsolutePath());
        } catch (IOException e) {
            throw new DeploymentUnitProcessingException(e);
        }
        webContext.addLifecycleListener(config);

        //
        String pathName = context.getName();
        if (pathName.equals("ROOT_war")) {
            pathName = "";
        } else {
            pathName = "/" + pathName.substring(0, pathName.length()-4);
        }
        webContext.setPath(pathName); // Set the name too.
        webContext.setIgnoreAnnotations(true);
        webContext.setPrivileged(true);
        //
        webContext.addWelcomeFile("index.html");

        //
        final Loader loader = new WebCtxLoader(classLoader);
        final InstanceManager manager = new WebInjectionContainer(classLoader);

        webContext.setInstanceManager(manager);
        webContext.setLoader(loader);

        // Add the servlet mapping from the metadata
        // Servlets
        ServletsMetaData servlets = metaData.getServlets();
        if (servlets != null) {
           for (ServletMetaData value : servlets) {
              org.apache.catalina.Wrapper wrapper = webContext.createWrapper();
              wrapper.setName(value.getName());
              wrapper.setServletClass(value.getServletClass());
              // Hacks
              wrapper.setLoadOnStartup(1);
              wrapper.setEnabled(true);
              webContext.addChild(wrapper);
           }
        }

        // Servlet mappings
        List<ServletMappingMetaData> smappings = metaData.getServletMappings();
        if (smappings != null) {
           for (ServletMappingMetaData value : smappings) {
              List<String> urlPatterns = value.getUrlPatterns();
              if (urlPatterns != null) {
                 for (String pattern : urlPatterns)
                    webContext.addServletMapping(pattern, value.getServletName());
              }
           }
        }


        // Add the context service
        final BatchBuilder builder = context.getBatchBuilder();
        builder.addService(WebSubsystemElement.JBOSS_WEB.append(deploymentName), new WebDeploymentService(webContext)).addDependency(WebSubsystemElement.JBOSS_WEB_SERVER, WebServer.class, new WebContextInjector(hostName, webContext)).setInitialMode(
                Mode.IMMEDIATE);
        // TODO war module injection dependencies
    }

}
