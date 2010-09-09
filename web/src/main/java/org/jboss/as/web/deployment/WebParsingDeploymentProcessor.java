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

import java.io.InputStream;

import org.jboss.as.deployment.AttachmentKey;
import org.jboss.as.deployment.DeploymentPhases;
import org.jboss.as.deployment.attachment.VirtualFileAttachment;
import org.jboss.as.deployment.unit.DeploymentUnitContext;
import org.jboss.as.deployment.unit.DeploymentUnitProcessingException;
import org.jboss.as.deployment.unit.DeploymentUnitProcessor;
import org.jboss.logging.Logger;
import org.jboss.metadata.web.spec.Web30MetaData;
import org.jboss.metadata.web.spec.WebMetaData;
import org.jboss.vfs.VirtualFile;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.builder.JBossXBBuilder;

/**
 * @author Jean-Frederic Clere
 */
public class WebParsingDeploymentProcessor implements DeploymentUnitProcessor {

    public static final long PRIORITY = DeploymentPhases.PARSE_DESCRIPTORS.plus(300L);
    public static final AttachmentKey<WebMetaData> ATTACHMENT_KEY = new AttachmentKey<WebMetaData>(WebMetaData.class);

    private static final String WEB_XML = "WEB-INF/web.xml";

    public void processDeployment(DeploymentUnitContext context) throws DeploymentUnitProcessingException {
        System.err.println("processDeployment: " + Thread.currentThread().getContextClassLoader());

        final VirtualFile deploymentRoot = VirtualFileAttachment.getVirtualFileAttachment(context);
        final VirtualFile webXml = deploymentRoot.getChild(WEB_XML);
        if (webXml.exists()) {
            Logger.getLogger("org.jboss.web").info("found web.xml " + webXml.getPathName());
            try {
                // Parse web.xml
               // Thread.currentThread().getContextClassLoader().loadClass("org.jboss.metadata.javaee.spec.DescriptionsImpl");
                WebMetaData webMetaData = parse(webXml.openStream(), Web30MetaData.class);
                context.putAttachment(ATTACHMENT_KEY, webMetaData);
            } catch (Exception e) {
                throw new DeploymentUnitProcessingException("failed to parse " + webXml, e);
            }
        }
    }

    <T> T parse(InputStream is, Class<T> clazz) throws Exception {
        Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
        return clazz.cast(unmarshaller.unmarshal(is, JBossXBBuilder.build(clazz)));
    }

}
