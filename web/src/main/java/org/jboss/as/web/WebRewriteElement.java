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
 * @author Emanuel Muckenhuber
 */
public class WebRewriteElement extends AbstractModelElement<WebRewriteElement> {

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    protected WebRewriteElement(XMLExtendedStreamReader reader) throws XMLStreamException {
        super(reader);
        // FIXME
        requireNoContent(reader);
    }

    /** {@inheritDoc} */
    public long elementHash() {
        return 0;
    }

    /** {@inheritDoc} */
    protected void appendDifference(Collection<AbstractModelUpdate<WebRewriteElement>> target, WebRewriteElement other) {

    }

    /** {@inheritDoc} */
    protected Class<WebRewriteElement> getElementClass() {
        return WebRewriteElement.class;
    }

    /** {@inheritDoc} */
    public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {

    }

}
