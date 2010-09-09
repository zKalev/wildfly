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
import java.util.TreeSet;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.model.AbstractModelElement;
import org.jboss.as.model.AbstractModelUpdate;
import org.jboss.as.model.PropertiesElement;
import org.jboss.msc.service.Location;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * The web container configuration.
 *
 * @author Jean-Frederic Clere
 */
public class WebContainerConfigElement extends AbstractModelElement<WebContainerConfigElement> {

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** The resource serving configuration. */
    private WebStaticResourcesElement resourceServing;
    private WebJspConfigurationElement jspConfiguration;
    private PropertiesElement mimeMappings;
    private Collection<String> welcomeFiles = new TreeSet<String>();

    protected WebContainerConfigElement(Location location) {
        super(location);
    }

    protected WebContainerConfigElement(XMLExtendedStreamReader reader) throws XMLStreamException {
        super(reader);
        // no attributes
        if (reader.getAttributeCount() > 0) {
            throw unexpectedAttribute(reader, 0);
        }
        // elements
        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            final Element element = Element.forName(reader.getLocalName());
            switch (element) {
            case STATIC_RESOURCES: {
                final WebStaticResourcesElement resourceServing = new WebStaticResourcesElement(reader);
                if (this.resourceServing != null) {
                    throw new XMLStreamException("An element of this type named 'resource-serving' has already been declared", reader.getLocation());
                }
                this.resourceServing = resourceServing;
                break;
            }
            case JSP_CONFIGURATION: {
                final WebJspConfigurationElement jspConfiguration = new WebJspConfigurationElement(reader);
                if (this.jspConfiguration != null) {
                    throw new XMLStreamException("An element of this type named 'jsp-configuration' has already been declared", reader.getLocation());
                }
                this.jspConfiguration = jspConfiguration;
                break;
            }
            case MIME_MAPPING: {
                final PropertiesElement mimeMappings = new PropertiesElement(reader);
                if (this.mimeMappings != null) {
                    throw new XMLStreamException("An element of this type named 'mime-mapping' has already been declared", reader.getLocation());
                }
                this.mimeMappings = mimeMappings;
                break;
            }
            case WELCOME_FILE: {
                final String welcomeFile = reader.getElementText();
                this.welcomeFiles.add(welcomeFile);
                break;
            }
            default:
                throw unexpectedElement(reader);
            }
        }
    }

    public WebStaticResourcesElement getResourceServing() {
        return resourceServing;
    }

    public WebJspConfigurationElement getJspConfiguration() {
        return jspConfiguration;
    }

    public PropertiesElement getMimeMappings() {
        return mimeMappings;
    }

    public Collection<String> getWelcomeFiles() {
        return welcomeFiles;
    }

    /** {@inheritDoc} */
    public long elementHash() {
        return 0;
    }

    /** {@inheritDoc} */
    protected void appendDifference(Collection<AbstractModelUpdate<WebContainerConfigElement>> target, WebContainerConfigElement other) {
        // FIXME appendDifference
    }

    /** {@inheritDoc} */
    protected Class<WebContainerConfigElement> getElementClass() {
        return WebContainerConfigElement.class;
    }

    /** {@inheritDoc} */
    public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {
        // FIXME writeContent
        streamWriter.writeEndElement();
    }

}
