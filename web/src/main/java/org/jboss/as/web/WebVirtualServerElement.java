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
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.model.AbstractModelElement;
import org.jboss.as.model.AbstractModelUpdate;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * The virtual server configuration element.
 * 
 * @author Emanuel Muckenhuber
 */
public class WebVirtualServerElement extends AbstractModelElement<WebVirtualServerElement> {

    /** The serialVersionUID */
    private static final long serialVersionUID = 1901406325489257078L;
    
    private String name;
    private final Set<String> aliases = new TreeSet<String>();
    
    private WebAccessLogElement accessLog;
    private WebRewriteElement rewrite;

    protected WebVirtualServerElement(XMLExtendedStreamReader reader) throws XMLStreamException {
        super(reader);
        String name = null;
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
                    default: unexpectedAttribute(reader, i);
                }
            }
        }
        // Virtual host name 
        this.name = name == null ? "localhost" : name;
        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            switch (Namespace.forUri(reader.getNamespaceURI())) {
                case WEB_1_0: {
                    final Element element = Element.forName(reader.getLocalName());
                    switch(element) {
                        case ALIAS: 
                            final String alias = reader.getElementText();
                            this.aliases.add(alias.trim());
                            break;
                        case ACCESS_LOG:
                            this.accessLog = new WebAccessLogElement(reader);
                            break;
                        case REWRITE: 
                            this.rewrite = new WebRewriteElement(reader);
                            break;
                        default: throw unexpectedElement(reader);
                    }
                    break;
                }
                default: throw unexpectedElement(reader);
            }
        }
    }

    public String getName() {
        return name;
    }
    
    public Set<String> getAliases() {
        return aliases;
    }
    
    public WebAccessLogElement getAccessLog() {
        return accessLog;
    }
    
    public WebRewriteElement getRewrite() {
        return rewrite;
    }
    
    /** {@inheritDoc} */
    public long elementHash() {
        long hash = name.hashCode() & 0xFFFFFFFFL;
        synchronized(aliases) {
            for(final String alias : aliases) {
                hash = Long.rotateLeft(hash, 1) ^ alias.hashCode() & 0xFFFFFFFFL;                
            }            
        }
        if(accessLog != null) hash = Long.rotateLeft(hash, 1) ^ accessLog.elementHash();
        if(rewrite != null) hash = Long.rotateLeft(hash, 1) ^ rewrite.elementHash();
        return hash;
    }

    /** {@inheritDoc} */
    protected void appendDifference(Collection<AbstractModelUpdate<WebVirtualServerElement>> target, WebVirtualServerElement other) {
        // FIXME appendDifference
    }

    /** {@inheritDoc} */
    protected Class<WebVirtualServerElement> getElementClass() {
        return WebVirtualServerElement.class;
    }

    /** {@inheritDoc} */
    public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {
        streamWriter.writeAttribute("name", name);
        synchronized(aliases) {
            for(final String alias : aliases) {
                streamWriter.writeStartElement(Element.ALIAS.getLocalName());
                streamWriter.writeCharacters(alias);
                streamWriter.writeEndElement();
            }
        }
        if(accessLog != null) {
            streamWriter.writeStartElement(Element.ACCESS_LOG.getLocalName());
            accessLog.writeContent(streamWriter);
        }
        if(rewrite != null) {
            streamWriter.writeStartElement(Element.REWRITE.getLocalName());
            rewrite.writeContent(streamWriter);
        }
        streamWriter.writeEndElement();
    }

}

