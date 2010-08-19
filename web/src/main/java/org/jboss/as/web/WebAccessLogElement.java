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
 * The virtual server access log configuration. 
 * 
 * @author Emanuel Muckenhuber
 */
public class WebAccessLogElement extends AbstractModelElement<WebAccessLogElement> {

    private String directory;
    private String pattern;
    private String prefix;
    private boolean resolveHosts;
    private boolean extended;
    private boolean rotate;
    
    protected WebAccessLogElement(XMLExtendedStreamReader reader) throws XMLStreamException {
        super(reader);
    }
    
    public String getDirectory() {
        return directory;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public boolean isExtended() {
        return extended;
    }
    
    public boolean isResolveHosts() {
        return resolveHosts;
    }
    
    public boolean isRotate() {
        return rotate;
    }
    
    /** {@inheritDoc} */
    public long elementHash() {
        // FIXME elementHash
        return 0;
    }

    /** {@inheritDoc} */
    protected void appendDifference(Collection<AbstractModelUpdate<WebAccessLogElement>> target, WebAccessLogElement other) {
        // FIXME appendDifference
        
    }

    /** {@inheritDoc} */
    protected Class<WebAccessLogElement> getElementClass() {
        return WebAccessLogElement.class;
    }

    /** {@inheritDoc} */
    public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {
        // FIXME writeContent
        
    }

}

