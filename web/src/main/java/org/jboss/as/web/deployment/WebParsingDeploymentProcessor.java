package org.jboss.as.web.deployment;

import org.jboss.as.deployment.attachment.VirtualFileAttachment;
import org.jboss.as.deployment.unit.DeploymentUnitContext;
import org.jboss.as.deployment.unit.DeploymentUnitProcessingException;
import org.jboss.as.deployment.unit.DeploymentUnitProcessor;
import org.jboss.logging.Logger;
import org.jboss.vfs.VirtualFile;

public class WebParsingDeploymentProcessor implements DeploymentUnitProcessor {
	public static final long PRIORITY = 300L;
	@Override
	public void processDeployment(DeploymentUnitContext context) throws DeploymentUnitProcessingException {
		// JFC need to add stuff here...
		Logger.getLogger("org.jboss.web").info("war: " + context.getName());
		final VirtualFile deploymentRoot = VirtualFileAttachment.getVirtualFileAttachment(context);
		Logger.getLogger("org.jboss.web").info("war: " + deploymentRoot.getName());
		Logger.getLogger("org.jboss.web").info("war: " + deploymentRoot.getPathName());
		
	}

}
