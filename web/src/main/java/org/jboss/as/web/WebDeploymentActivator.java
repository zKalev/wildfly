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

import org.jboss.as.deployment.chain.DeploymentChain;
import org.jboss.as.deployment.chain.DeploymentChainImpl;
import org.jboss.as.deployment.chain.DeploymentChainProvider;
import org.jboss.as.web.deployment.WarDeploymentChainSelector;
import org.jboss.as.web.deployment.WebParsingDeploymentProcessor;
import org.jboss.as.deployment.managedbean.ManagedBeanAnnotationProcessor;
import org.jboss.as.deployment.managedbean.ManagedBeanDependencyProcessor;
import org.jboss.as.deployment.managedbean.ManagedBeanDeploymentProcessor;
import org.jboss.as.deployment.module.DeploymentModuleLoaderProcessor;
import org.jboss.as.deployment.module.ModuleConfigProcessor;
import org.jboss.as.deployment.module.ModuleDependencyProcessor;
import org.jboss.as.deployment.module.ModuleDeploymentProcessor;
import org.jboss.as.deployment.naming.ModuleContextProcessor;
import org.jboss.as.deployment.processor.AnnotationIndexProcessor;
import org.jboss.as.deployment.service.ParsedServiceDeploymentProcessor;
import org.jboss.as.deployment.service.ServiceDeploymentParsingProcessor;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;

/**
 * Activator of the web deployment system. 
 * 
 * @author Emanuel Muckenhuber
 */
class WebDeploymentActivator implements ServiceActivator {

    public static final long WAR_DEPLOYMENT_CHAIN_PRIORITY = 3000000L;
    
    public void activate(ServiceActivatorContext serviceActivatorContext) {
        final DeploymentChain deploymentChain = new DeploymentChainImpl("deployment.chain.war");
        deploymentChain.addProcessor(new AnnotationIndexProcessor(), AnnotationIndexProcessor.PRIORITY);
        deploymentChain.addProcessor(new ManagedBeanDependencyProcessor(), ManagedBeanDependencyProcessor.PRIORITY);
        deploymentChain.addProcessor(new ModuleDependencyProcessor(), ModuleDependencyProcessor.PRIORITY);
        deploymentChain.addProcessor(new ModuleConfigProcessor(), ModuleConfigProcessor.PRIORITY);
        deploymentChain.addProcessor(new DeploymentModuleLoaderProcessor(), DeploymentModuleLoaderProcessor.PRIORITY);
        deploymentChain.addProcessor(new ModuleDeploymentProcessor(), ModuleDeploymentProcessor.PRIORITY);
        deploymentChain.addProcessor(new ManagedBeanAnnotationProcessor(), ManagedBeanAnnotationProcessor.PRIORITY);
        deploymentChain.addProcessor(new ServiceDeploymentParsingProcessor(), ServiceDeploymentParsingProcessor.PRIORITY);
        deploymentChain.addProcessor(new ModuleContextProcessor(), ModuleContextProcessor.PRIORITY);
        deploymentChain.addProcessor(new ParsedServiceDeploymentProcessor(), ParsedServiceDeploymentProcessor.PRIORITY);
        deploymentChain.addProcessor(new ManagedBeanDeploymentProcessor(), ManagedBeanDeploymentProcessor.PRIORITY);
        
        
        deploymentChain.addProcessor(new WebParsingDeploymentProcessor(),  WebParsingDeploymentProcessor.PRIORITY);
        
        
        DeploymentChainProvider.INSTANCE.addDeploymentChain(deploymentChain, new WarDeploymentChainSelector(), WAR_DEPLOYMENT_CHAIN_PRIORITY);
    }

}

