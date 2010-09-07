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
import org.jboss.as.deployment.chain.DeploymentChainProcessorInjector;
import org.jboss.as.deployment.chain.DeploymentChainProvider;
import org.jboss.as.deployment.chain.DeploymentChainProviderInjector;
import org.jboss.as.deployment.chain.DeploymentChainProviderService;
import org.jboss.as.deployment.chain.DeploymentChainService;
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
import org.jboss.as.deployment.unit.DeploymentUnitProcessor;
import org.jboss.as.deployment.unit.DeploymentUnitProcessorService;
import org.jboss.as.web.deployment.WarDeploymentChainSelector;
import org.jboss.as.web.deployment.WebAnnotationDeploymentProcessor;
import org.jboss.as.web.deployment.WebClassloadingDependencyProcessor;
import org.jboss.as.web.deployment.WebMetaDataDeploymentProcessor;
import org.jboss.as.web.deployment.WebParsingDeploymentProcessor;
import org.jboss.msc.service.BatchBuilder;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.value.Value;
import org.jboss.msc.value.Values;

/**
 * Activator of the web deployment system. 
 * 
 * @author Emanuel Muckenhuber
 */
class WebDeploymentActivator implements ServiceActivator {
    public static final long WAR_DEPLOYMENT_CHAIN_PRIORITY = 1000L;
    public static final ServiceName WAR_DEPLOYMENT_CHAIN_SERVICE_NAME = DeploymentChain.SERVICE_NAME.append("war");

    /**
     * Activate the services required for service deployments.
     * 
     * @param context The service activator context
     */
    public void activate(final ServiceActivatorContext context) {
        final BatchBuilder batchBuilder = context.getBatchBuilder();
        batchBuilder.addServiceValueIfNotExist(DeploymentChainProviderService.SERVICE_NAME, new DeploymentChainProviderService());

        final Value<DeploymentChain> deploymentChainValue = Values.immediateValue((DeploymentChain)new DeploymentChainImpl(WAR_DEPLOYMENT_CHAIN_SERVICE_NAME.toString()))   ;
        final DeploymentChainService deploymentChainService = new DeploymentChainService(deploymentChainValue);
        batchBuilder.addService(WAR_DEPLOYMENT_CHAIN_SERVICE_NAME, deploymentChainService)
            .addDependency(DeploymentChainProviderService.SERVICE_NAME, DeploymentChainProvider.class, new DeploymentChainProviderInjector<DeploymentChain>(deploymentChainValue, new WarDeploymentChainSelector(), WAR_DEPLOYMENT_CHAIN_PRIORITY));

        // Jar deployment processors ....
        addDeploymentProcessor(batchBuilder, new AnnotationIndexProcessor(), AnnotationIndexProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ManagedBeanDependencyProcessor(), ManagedBeanDependencyProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ModuleDependencyProcessor(), ModuleDependencyProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ModuleConfigProcessor(), ModuleConfigProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new DeploymentModuleLoaderProcessor(), DeploymentModuleLoaderProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ModuleDeploymentProcessor(), ModuleDeploymentProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ManagedBeanAnnotationProcessor(), ManagedBeanAnnotationProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ServiceDeploymentParsingProcessor(), ServiceDeploymentParsingProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ModuleContextProcessor(), ModuleContextProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ParsedServiceDeploymentProcessor(), ParsedServiceDeploymentProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new ManagedBeanDeploymentProcessor(), ManagedBeanDeploymentProcessor.PRIORITY);
        
        // Web specific deployment processors ....
        addDeploymentProcessor(batchBuilder, new WebParsingDeploymentProcessor(), WebParsingDeploymentProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new WebClassloadingDependencyProcessor(), WebClassloadingDependencyProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new WebAnnotationDeploymentProcessor(), WebAnnotationDeploymentProcessor.PRIORITY);
        addDeploymentProcessor(batchBuilder, new WebMetaDataDeploymentProcessor(), WebMetaDataDeploymentProcessor.PRIORITY);

    }

    private <T extends DeploymentUnitProcessor> void addDeploymentProcessor(final BatchBuilder batchBuilder, final T deploymentUnitProcessor, final long priority) {
        final DeploymentUnitProcessorService<T> deploymentUnitProcessorService = new DeploymentUnitProcessorService<T>(deploymentUnitProcessor);
        batchBuilder.addService(WAR_DEPLOYMENT_CHAIN_SERVICE_NAME.append(deploymentUnitProcessor.getClass().getName()), deploymentUnitProcessorService)
            .addDependency(WAR_DEPLOYMENT_CHAIN_SERVICE_NAME, DeploymentChain.class, new DeploymentChainProcessorInjector<T>(deploymentUnitProcessorService, priority));
    }
}
