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

import javax.xml.stream.XMLStreamException;

import org.apache.catalina.connector.Connector;
import org.jboss.as.model.AbstractModelElement;
import org.jboss.as.model.AbstractModelUpdate;
import org.jboss.as.services.net.SocketBinding;
import org.jboss.msc.service.BatchServiceBuilder;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * The web connector configuration element.
 * 
 * @author Emanuel Muckenhuber
 */
public class WebConnectorElement extends AbstractModelElement<WebConnectorElement> implements ServiceActivator {

    /** The serialVersionUID */
    private static final long serialVersionUID = 4884228320917906019L;
    
    private final String name;
    private final String protocol;
    private final String bindingRef;
    private final String scheme;
    private final String executorRef;
    private final boolean enabled;
    
    protected WebConnectorElement(XMLExtendedStreamReader reader) throws XMLStreamException {
        super(reader);
        String name = null;
        String protocol = null;
        String bindingRef = null;
        String scheme = null;
        String executorRef = null;
        String enabled = null;
        final int count = reader.getAttributeCount();
        for (int i = 0; i < count; i ++) {
            final String value = reader.getAttributeValue(i);
            if (reader.getAttributeNamespace(i) != null) {
                throw unexpectedAttribute(reader, i);
            } else {
                final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
                switch (attribute) {
                    case NAME:
                        name = value;
                        break;
                    case BINDING:
                        bindingRef = value;
                        break;
                    case SCHEME: 
                        scheme = value;
                        break;
                    case PROTOCOL:
                        protocol = value;
                        break;
                    case EXECUTOR:
                        executorRef = value;
                        break;
                    case ENABLED:
                        enabled = value;
                        break;
                    default: unexpectedAttribute(reader, i);
                }
            }
        }
        if(name == null) {
            missingRequired(reader, Collections.singleton(Attribute.NAME));
        }
        this.name = name;
        this.protocol = protocol;
        this.bindingRef = bindingRef;
        this.scheme = scheme;
        this.executorRef = executorRef;
        this.enabled = enabled == null ? true : Boolean.valueOf(enabled);;
        // Handle elements
        requireNoContent(reader);
    }

    public String getName() {
        return name;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public String getBindingRef() {
        return bindingRef;
    }
    
    public String getExecutorRef() {
        return executorRef;
    }
    
    public String getScheme() {
        return scheme;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    /** {@inheritDoc} */
    public long elementHash() {
        long cksum = 0L;
        return cksum;
    }

    /** {@inheritDoc} */
    protected void appendDifference(Collection<AbstractModelUpdate<WebConnectorElement>> target, WebConnectorElement other) {
        // FIXME appendDifference
        
    }

    /** {@inheritDoc} */
    protected Class<WebConnectorElement> getElementClass() {
        return WebConnectorElement.class;
    }

    /** {@inheritDoc} */
    public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {
        streamWriter.writeAttribute(Attribute.NAME.getLocalName(), name);
        if(protocol != null) {
            streamWriter.writeAttribute(Attribute.PROTOCOL.getLocalName(), protocol);    
        }
        if(bindingRef != null) {
            streamWriter.writeAttribute(Attribute.BINDING.getLocalName(), bindingRef);
        }
        if(scheme != null) {
            streamWriter.writeAttribute(Attribute.SCHEME.getLocalName(), scheme);
        }
        if(executorRef != null) {
            streamWriter.writeAttribute(Attribute.EXECUTOR.getLocalName(), executorRef);
        }
        if(enabled == false) {
            streamWriter.writeAttribute(Attribute.ENABLED.getLocalName(), "false");
        }
        // FIXME
        streamWriter.writeEndElement();
    }

    /** {@inheritDoc} */
    public void activate(ServiceActivatorContext context) {
        if(enabled) {
            final ServiceName name = WebSubsystemElement.JBOSS_WEB_CONNECTOR.append(getName());
            final WebConnectorService connector = new WebConnectorService(this);
            BatchServiceBuilder<Connector> service = context.getBatchBuilder().addService(name, connector);
            service.addDependency(WebSubsystemElement.JBOSS_WEB_SERVER, WebServer.class, connector.getServer());
            service.addDependency(SocketBinding.JBOSS_BINDING_NAME.append(getBindingRef()), SocketBinding.class, connector.getBinding());
            // TODO dependency on AbstractExecutorElement  getExecutorRef()
            service.setLocation(getLocation());
            service.setInitialMode(Mode.IMMEDIATE);
        }
    }
    
}

