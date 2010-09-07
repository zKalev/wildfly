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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emanuel Muckenhuber
 * @author Jean-Frederic Clere
 */
enum Attribute {
    UNKNOWN(null),

    BINDING("binding"),
    
    ENABLED("enabled"),
    EXECUTOR("executor"),
    
    NAME("name"),
    
    PROTOCOL("protocol"),
    
    SCHEME("scheme"),
    
    LISTINGS("listings"),
    SENDFILE("sendfile"),
    FILE_ENCONDING("file-encoding"),
    READ_ONLY("read-only"),
    WEBDAV("webdav"),
    SECRET("secret"),
    MAX_DEPTH("max-depth"),
    DISABLED("disabled"),
    
    DEVELOPMENT("development"),
    KEEP_GENERATED("keep-generated"),
    TRIM_SPACES("trim-spaces"),
    TAG_POOLING("tag-pooling"),
    MAPPED_FILE("mapped-file"),
    CHECK_INTERVAL("check-interval"),
    MODIFIFICATION_TEST_INTERVAL("modification-test-interval"),
    RECOMPILE_ON_FAIL("recompile-on-fail"),
    SMAP("smap"),
    DUMP_SMAP("dump-smap"),
    GENERATE_STRINGS_AS_CHAR_ARRAYS("generate-strings-as-char-arrays"),
    ERROR_ON_USE_BEAN_INVALID_CLASS_ATTRIBUT("error-on-use-bean-invalid-class-attribute"),
    SCRATCH_DIR("scratch-dir"),
    SOURCE_VM("source-vm"),
    TARGET_VM("target-vm"),
    JAVA_ENCODING("java-encoding"),
    X_POWERED_BY("x-powered-by"),
    DISPLAY_SOOURCE_FRAGMENT("display-source-fragment")
    ;
    private final String name;

    Attribute(final String name) {
        this.name = name;
    }

    /**
     * Get the local name of this attribute.
     *
     * @return the local name
     */
    public String getLocalName() {
        return name;
    }

    private static final Map<String, Attribute> MAP;

    static {
        final Map<String, Attribute> map = new HashMap<String, Attribute>();
        for (Attribute element : values()) {
            final String name = element.getLocalName();
            if (name != null) map.put(name, element);
        }
        MAP = map;
    }

    public static Attribute forName(String localName) {
        final Attribute element = MAP.get(localName);
        return element == null ? UNKNOWN : element;
    }

    public String toString() {
        return getLocalName();
    }
}

