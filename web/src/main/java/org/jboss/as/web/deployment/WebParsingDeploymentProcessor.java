package org.jboss.as.web.deployment;

import org.jboss.as.deployment.DeploymentPhases;
import org.jboss.as.deployment.unit.DeploymentUnitContext;
import org.jboss.as.deployment.unit.DeploymentUnitProcessingException;
import org.jboss.as.deployment.unit.DeploymentUnitProcessor;
import org.jboss.logging.Logger;

public class WebParsingDeploymentProcessor implements DeploymentUnitProcessor {
	public static final long PRIORITY = DeploymentPhases.INSTALL_SERVICES.plus(200L);
	@Override
	public void processDeployment(DeploymentUnitContext context) throws DeploymentUnitProcessingException {
		// JFC need to add stuff here...
		Logger.getLogger("org.jboss.web").info("war: " + context.getName());
	}

}
