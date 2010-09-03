package org.jboss.as.web.deployment;

import java.io.IOException;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.jboss.as.deployment.attachment.VirtualFileAttachment;
import org.jboss.as.deployment.unit.DeploymentUnitContext;
import org.jboss.as.deployment.unit.DeploymentUnitProcessingException;
import org.jboss.as.deployment.unit.DeploymentUnitProcessor;
import org.jboss.as.web.WebServer;
import org.jboss.logging.Logger;
import org.jboss.msc.value.InjectedValue;
import org.jboss.vfs.VirtualFile;

public class WebParsingDeploymentProcessor implements DeploymentUnitProcessor {
	public static final long PRIORITY = 300L;
	private InjectedValue<WebServer> server = new InjectedValue<WebServer>();
	@Override
	public void processDeployment(DeploymentUnitContext context) throws DeploymentUnitProcessingException {
		// JFC need to add stuff here...
		Logger.getLogger("org.jboss.web").info("war: " + context.getName());
		final VirtualFile deploymentRoot = VirtualFileAttachment.getVirtualFileAttachment(context);
		Logger.getLogger("org.jboss.web").info("war: " + deploymentRoot.getName());
		Logger.getLogger("org.jboss.web").info("war: " + deploymentRoot.getPathName());
		// deploymentRoot.getPhysicalFile();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
        // Add the default Servlet org.apache.catalina.servlets.DefaultServlet.class
        ContextConfig config = new ContextConfig();
        Context rootContext = new StandardContext();
        try {
			rootContext.setDocBase(deploymentRoot.getPhysicalFile().getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        rootContext.setPath("");
        ((Lifecycle) rootContext).addLifecycleListener(config);
         
        Wrapper wrapper = rootContext.createWrapper();
        wrapper.setName("DefaultServlet");
        wrapper.setLoadOnStartup(1);
        wrapper.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        wrapper.addInitParameter("debug","99");
        wrapper.addInitParameter("listings", "true");
        rootContext.addChild(wrapper);
        
        rootContext.addServletMapping("/", "DefaultServlet");
        rootContext.setIgnoreAnnotations(true);
        rootContext.setPrivileged(true);
        rootContext.addWelcomeFile("index.html");
        server.getValue().addContext(rootContext);
	}

	public InjectedValue<WebServer> getServer() {
		return server;
	}
	
}
