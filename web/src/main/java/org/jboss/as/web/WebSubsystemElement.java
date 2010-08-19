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
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.model.AbstractModelUpdate;
import org.jboss.as.model.AbstractSubsystemElement;
import org.jboss.msc.service.BatchServiceBuilder;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * @author Emanuel Muckenhuber
 */
public class WebSubsystemElement extends AbstractSubsystemElement<WebSubsystemElement> {

	/** The serialVersionUID */
	private static final long serialVersionUID = 7033428860620649612L;
	
	/** The base name for jboss.web services. */
    public static final ServiceName JBOSS_WEB = ServiceName.JBOSS.append("web");
    /** The jboss.web server name, there can only be one. */
    public static final ServiceName JBOSS_WEB_SERVER = JBOSS_WEB.append("server");
    /** The base name for jboss.web connector services. */
    public static final ServiceName JBOSS_WEB_CONNECTOR = JBOSS_WEB.append("connector");
    
	/** The web container config. */
    private WebContainerConfigElement config;
    
    /** The web connectors. */
    private final NavigableMap<String, WebConnectorElement> connectors = new TreeMap<String, WebConnectorElement>();
    
    /** The virtual web servers. */
    private final NavigableMap<String, WebVirtualServerElement> virtuals = new TreeMap<String, WebVirtualServerElement>();
	
	protected WebSubsystemElement(XMLExtendedStreamReader reader) throws XMLStreamException {
		super(reader);
        // no attributes
        if (reader.getAttributeCount() > 0) {
            throw unexpectedAttribute(reader, 0);
        }
        // elements
        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            switch (Namespace.forUri(reader.getNamespaceURI())) {
            	case WEB_1_0: {
            		final Element element = Element.forName(reader.getLocalName());
            		switch(element) {
            			case CONNECTOR: {
            			    final WebConnectorElement connector = new WebConnectorElement(reader);
            			    final String name = connector.getName();
            			    if(this.connectors.containsKey(name)) {
            			        duplicateNamedElement(reader, name);
            			    }
            			    this.connectors.put(name, connector);
            				break;
            			}
            			case VIRTUAL_SERVER: {
            			    final WebVirtualServerElement virtual = new WebVirtualServerElement(reader);
            			    final String name = virtual.getName();
            			    if(this.virtuals.containsKey(name)) {
            			        duplicateNamedElement(reader, name);
            			    }
            			    this.virtuals.put(name, virtual);
            			    break;
            			}
            			default: throw unexpectedElement(reader);
            		}
            		break;
            	}
            	default: throw unexpectedElement(reader);
            }
        }
	}

	/** {@inheritDoc} */
	public Collection<String> getReferencedSocketBindings() {
		return Collections.emptySet();
	}

	/** {@inheritDoc} */
	public long elementHash() {
        long cksum = 0L;
        cksum = calculateElementHashOf(connectors.values(), cksum);
        cksum = calculateElementHashOf(virtuals.values(), cksum);
        if (config != null) cksum = Long.rotateLeft(cksum, 1) ^ config.elementHash();
        return cksum;
	}

	/** {@inheritDoc} */
	protected void appendDifference(Collection<AbstractModelUpdate<WebSubsystemElement>> target, WebSubsystemElement other) {
		// FIXME appendDifference
		
	}

	/** {@inheritDoc} */
	protected Class<WebSubsystemElement> getElementClass() {
		return WebSubsystemElement.class;
	}

	/** {@inheritDoc} */
	public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {
	    if(config != null) {
	        streamWriter.writeStartElement(Element.CONTAINER_CONFIG.getLocalName());
	        config.writeContent(streamWriter);
	    }
	    for(final WebConnectorElement connector : connectors.values()) {
	        streamWriter.writeStartElement(Element.CONNECTOR.getLocalName());
	        connector.writeContent(streamWriter);
	    }
       for(final WebVirtualServerElement virtual : virtuals.values()) {
            streamWriter.writeStartElement(Element.VIRTUAL_SERVER.getLocalName());
            virtual.writeContent(streamWriter);
        }
	    streamWriter.writeEndElement();
	}

	/** {@inheritDoc} */
	public void activate(ServiceActivatorContext context) {

	    final BatchServiceBuilder<WebServer> service = context.getBatchBuilder()
	        .addService(JBOSS_WEB_SERVER, new WebServerService(this.virtuals.values()));
	    // TODO dependency on MBeanServer 
	    service.setLocation(getLocation());
	    service.setInitialMode(Mode.ON_DEMAND);
	    // Activate connector
	    for(final WebConnectorElement connector : this.connectors.values()) {
	        connector.activate(context);
	    }
	}
}

