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
        String development = null;
        String disabled = null;
        String keep_generated = null;
        String trim_spaces = null;
        String tag_pooling = null;
        String mapped_file = null;
        String check_interval = null;
        String modification_test_interval = null;
        String recompile_on_fail = null;
        String smap = null;
        String dump_smap = null;
        String generate_strings_as_char_arrays = null;
        String error_on_use_bean_invalid_class_attribute = null;
        String scratch_dir = null;
        String source_vm = null;
        String target_vm = null;
        String java_encoding = null;
        String x_powered_by = null;
        String display_source_fragment = null;
        final int count = reader.getAttributeCount();
        for (int i = 0; i < count; i ++) {
            final String value = reader.getAttributeValue(i);
            if (reader.getAttributeNamespace(i) != null) {
                throw unexpectedAttribute(reader, i);
            } else {
                final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
                switch (attribute) {
                    case DEVELOPMENT:
                    	development = value;
                   		break;
                    case DISABLED:
                    	disabled = value;
                    	break;
                    case KEEP_GENERATED:
                    	keep_generated = value;
                    	break;
                    case TRIM_SPACES:
                    	trim_spaces = value;
                    	break;
                    case TAG_POOLING:
                    	tag_pooling = value;
                    	break;
                    case MAPPED_FILE:
                    	mapped_file = value;
                    	break;
                    case CHECK_INTERVAL:
                    	check_interval = value;
                    	break;
                    case MODIFIFICATION_TEST_INTERVAL:
                    	modification_test_interval = value;
                    	break;
                    case RECOMPILE_ON_FAIL:
                    	recompile_on_fail = value;
                    case SMAP:
                    	smap = value;
                    	break;
                    case DUMP_SMAP:
                    	dump_smap = value;
                    case GENERATE_STRINGS_AS_CHAR_ARRAYS:
                    	generate_strings_as_char_arrays = value;
                    	break;
                    case ERROR_ON_USE_BEAN_INVALID_CLASS_ATTRIBUT:
                    	error_on_use_bean_invalid_class_attribute = value;
                    	break;
                    case SCRATCH_DIR:
                    	scratch_dir = value;
                        break;
                    case SOURCE_VM:
                    	source_vm = value;
                    	break;
                    case TARGET_VM:
                    	target_vm = value;
                    	break;
                    case JAVA_ENCODING:
                    	java_encoding = value;
                    	break;
                    case X_POWERED_BY:
                    	x_powered_by = value;
                    	break;
                    case DISPLAY_SOOURCE_FRAGMENT:
                    	display_source_fragment = value;
                    	break;
                    default: unexpectedAttribute(reader, i);
                }
            }
        }
        this.development = development == null ? false : Boolean.valueOf(development);
        this.disabled = disabled == null ? false : Boolean.valueOf(disabled);
        this.keepGenerated = keep_generated == null ? true : Boolean.valueOf(keep_generated);
        this.trimSpaces = trim_spaces == null ? false : Boolean.valueOf(trim_spaces);
        this.tagPooling = tag_pooling == null ? true : Boolean.valueOf(tag_pooling);
        this.mappedFile = mapped_file == null ? true : Boolean.valueOf(mapped_file);
        this.checkInterval = check_interval == null ? 0 : Integer.valueOf(check_interval);
        this.modificationTestInterval = modification_test_interval == null ? 4 : Integer.valueOf(modification_test_interval);
        this.recompileOnFail = recompile_on_fail == null ? false : Boolean.valueOf(recompile_on_fail);
        this.smap = smap == null ? true : Boolean.valueOf(smap);
        this.dumpSmap = dump_smap == null ? false : Boolean.valueOf(dump_smap);
        this.generateStringsAsCharArrays = generate_strings_as_char_arrays == null? false :Boolean.valueOf( generate_strings_as_char_arrays);
        this.errorOnInvalidClassAttribute = error_on_use_bean_invalid_class_attribute == null ? false :Boolean.valueOf(error_on_use_bean_invalid_class_attribute);
        this.scratchDir = scratch_dir;
        this.sourceVM = source_vm == null ? "1.5" : source_vm;
        this.targetVM = target_vm == null ? "1.5" : target_vm;
        this.javaEncoding = java_encoding  == null ? "UTF8" : java_encoding;
        this.XPoweredBy = x_powered_by == null ? true : Boolean.valueOf(x_powered_by);
        this.displaySourceFragment = display_source_fragment == null ? true : Boolean.valueOf(display_source_fragment);
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

