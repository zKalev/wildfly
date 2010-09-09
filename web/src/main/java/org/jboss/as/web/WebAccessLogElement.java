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

    /** The serialVersionUID */
    private static final long serialVersionUID = 7372525549178383501L;
    private String directory;
    private String pattern;
    private String prefix;
    private boolean resolveHosts;
    private boolean extended;
    private boolean rotate;

    protected WebAccessLogElement(XMLExtendedStreamReader reader) throws XMLStreamException {
        super(reader);
        String resolveHosts = null;
        String extended = null;
        String rotate = null;
        final int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            final String value = reader.getAttributeValue(i);
            if (reader.getAttributeNamespace(i) != null) {
                throw unexpectedAttribute(reader, i);
            } else {
                final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
                switch (attribute) {
                case DIRECTORY:
                    this.directory = value;
                    break;
                case PATTERN:
                    this.pattern = value;
                    break;
                case PREFIX:
                    this.prefix = value;
                    break;
                case RESOLVE_HOSTS:
                    resolveHosts = value;
                    break;
                case EXTENDED:
                    extended = value;
                    break;
                case ROTATE:
                    rotate = value;
                    break;
                default:
                    unexpectedAttribute(reader, i);
                }
            }
        }
        this.resolveHosts = resolveHosts == null ? false : Boolean.valueOf(resolveHosts);
        this.extended = extended == null ? false : Boolean.valueOf(extended);
        this.rotate = rotate == null ? true : Boolean.valueOf(rotate);
        requireNoContent(reader);
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
    @Override
    public long elementHash() {
        // FIXME
        return 0L;
    }

    /** {@inheritDoc} */
    @Override
    protected void appendDifference(Collection<AbstractModelUpdate<WebAccessLogElement>> target, WebAccessLogElement other) {
        // FIXME appendDifference

    }

    /** {@inheritDoc} */
    @Override
    protected Class<WebAccessLogElement> getElementClass() {
        return WebAccessLogElement.class;
    }

    /** {@inheritDoc} */
    @Override
    public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {
        if (directory != null) {
            streamWriter.writeAttribute(Attribute.DIRECTORY.getLocalName(), directory);
        }
        if (pattern != null) {
            streamWriter.writeAttribute(Attribute.PATTERN.getLocalName(), pattern);
        }
        if (prefix != null) {
            streamWriter.writeAttribute(Attribute.PREFIX.getLocalName(), prefix);
        }
        streamWriter.writeAttribute(Attribute.EXTENDED.getLocalName(), String.valueOf(extended));
        streamWriter.writeAttribute(Attribute.RESOLVE_HOSTS.getLocalName(), String.valueOf(resolveHosts));
        streamWriter.writeAttribute(Attribute.ROTATE.getLocalName(), String.valueOf(rotate));
        streamWriter.writeEndElement();
    }

}
