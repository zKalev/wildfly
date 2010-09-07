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

import javax.xml.stream.XMLStreamException;

import org.jboss.as.model.AbstractModelElement;
import org.jboss.as.model.AbstractModelUpdate;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * The web resource serving configuration.
 * 
 * @author Emanuel Muckenhuber
 * @author Jean-Frederic Clere
 */
public class WebResourceServingElement extends AbstractModelElement<WebResourceServingElement> {

    /** The serialVersionUID */
    private static final long serialVersionUID = 7112890068879082292L;

    private boolean listings;
    private int sendfile;
    private String fileEncoding;
    private boolean readOnly;
    private boolean webDav;
    private String secret;
    private int maxDepth;
    private boolean disabled = false;
    
    protected WebResourceServingElement(XMLExtendedStreamReader reader) throws XMLStreamException {
        super(reader);
        String listings = null;
        String sendfile = null;
        String fileEncoding = null;
        String readOnly = null;
        String webDav = null;
        String secret = null;
        String maxDepth = null;
        String disabled = null;
        final int count = reader.getAttributeCount();
        for (int i = 0; i < count; i ++) {
            final String value = reader.getAttributeValue(i);
            if (reader.getAttributeNamespace(i) != null) {
                throw unexpectedAttribute(reader, i);
            } else {
                final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
                switch (attribute) {
                    case LISTINGS:
                    	listings = value;
                        break;
                    case SENDFILE:
                        sendfile = value;
                        break;
                    case FILE_ENCONDING:
                    	fileEncoding = value;
                    case READ_ONLY:
                    	readOnly = value;
                    	break;
                    case WEBDAV:
                    	webDav = value;
                		break;
                    case SECRET:
                    	secret= value;
                		break;
                    case MAX_DEPTH:
                    	maxDepth = value;
                		break;
                    case DISABLED:
                    	disabled = value;
                		break;
                    default: unexpectedAttribute(reader, i);
                }
                this.listings = listings == null ? true : Boolean.valueOf(listings);
                this.sendfile = sendfile == null ? 49152 : Integer.valueOf(listings);
                this.fileEncoding = fileEncoding;
                this.readOnly = readOnly  == null ? true : Boolean.valueOf(readOnly);
                this.webDav = webDav == null ? false : Boolean.valueOf(webDav);
                this.secret = secret;
                this.maxDepth = maxDepth == null ? 3 : Integer.valueOf(maxDepth);
                this.disabled = disabled == null ? false : Boolean.valueOf(disabled);
            }
        }

        requireNoContent(reader);
    }
    
    public boolean isListings() {
        return listings;
    }
    
    public boolean isReadOnly() {
        return readOnly;
    }
    
    public boolean isWebDav() {
        return webDav;
    }
    
    public boolean isDisabled() {
        return disabled;
    }
    
    public String getFileEncoding() {
        return fileEncoding;
    }
    
    public int getMaxDepth() {
        return maxDepth;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public int getSendfileSize() {
        return sendfile;
    }
    
    @Override
    public long elementHash() {
        // FIXME elementHash
        return 0;
    }

    @Override
    protected void appendDifference(Collection<AbstractModelUpdate<WebResourceServingElement>> target, WebResourceServingElement other) {
        // FIXME appendDifference
        
    }

    @Override
    protected Class<WebResourceServingElement> getElementClass() {
        return WebResourceServingElement.class;
    }

    @Override
    public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {
        // FIXME writeContent
    }

}

