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
import org.jboss.msc.service.Location;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * Web jsp container configuration. 
 * 
 * @author Emanuel Muckenhuber
 */
public class WebJspConfigurationElement extends AbstractModelElement<WebJspConfigurationElement> {

    /** The serialVersionUID */
    private static final long serialVersionUID = -2092852855302799521L;

    private boolean development = false;
    private boolean keepGenerated = true;
    private boolean trimSpaces = false;
    private boolean tagPooling = true;
    private boolean mappedFile = true;
    private int checkInterval = 0;
    private int modificationTestInterval = 4;
    private boolean recompileOnFail = false;
    private boolean smap = true;
    private boolean dumpSmap = false;
    private boolean generateStringsAsCharArrays = false;
    private boolean errorOnInvalidClassAttribute = false;
    private String scratchDir;
    private String sourceVM = "1.5";
    private String targetVM = "1.5";
    private String javaEncoding = "UTF-8";
    private boolean XPoweredBy = true;
    private boolean displaySourceFragment = true;
    private boolean disabled = false;
    
    protected WebJspConfigurationElement(Location location) {
        super(location);
    }
    
    protected WebJspConfigurationElement(XMLExtendedStreamReader reader) throws XMLStreamException {
        super(reader);
        // FIXME parsing
        requireNoContent(reader);
    }

    public boolean isDevelopment() {
        return development;
    }

    public boolean isDisabled() {
        return disabled;
    }
    
    public boolean isKeepGenerated() {
        return keepGenerated;
    }

    public boolean isTrimSpaces() {
        return trimSpaces;
    }

    public boolean isTagPooling() {
        return tagPooling;
    }

    public boolean isMappedFile() {
        return mappedFile;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public int getModificationTestInterval() {
        return modificationTestInterval;
    }

    public boolean isRecompileOnFail() {
        return recompileOnFail;
    }

    public boolean isSmap() {
        return smap;
    }

    public boolean isDumpSmap() {
        return dumpSmap;
    }

    public boolean isGenerateStringsAsCharArrays() {
        return generateStringsAsCharArrays;
    }

    public boolean isErrorOnInvalidClassAttribute() {
        return errorOnInvalidClassAttribute;
    }

    public String getScratchDir() {
        return scratchDir;
    }

    public String getSourceVM() {
        return sourceVM;
    }

    public String getTargetVM() {
        return targetVM;
    }

    public String getJavaEncoding() {
        return javaEncoding;
    }

    public boolean isXPoweredBy() {
        return XPoweredBy;
    }

    public boolean isDisplaySourceFragment() {
        return displaySourceFragment;
    }

    @Override
    public long elementHash() {
        // FIXME elementHash
        return 0;
    }

    @Override
    protected void appendDifference(Collection<AbstractModelUpdate<WebJspConfigurationElement>> target,
            WebJspConfigurationElement other) {
        // FIXME appendDifference
        
    }

    @Override
    protected Class<WebJspConfigurationElement> getElementClass() {
        return WebJspConfigurationElement.class;
    }

    @Override
    public void writeContent(XMLExtendedStreamWriter streamWriter) throws XMLStreamException {
        // FIXME writeContent
        
    }

}

