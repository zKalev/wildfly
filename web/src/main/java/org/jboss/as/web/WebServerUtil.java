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

import java.io.File;

import org.apache.catalina.Valve;
import org.apache.catalina.valves.AccessLogValve;
import org.jboss.web.rewrite.RewriteValve;

/**
 * @author Emanuel Muckenhuber
 */
class WebServerUtil {

    private WebServerUtil() {}
    
    /**
     * Create a access log valve.
     * 
     * @param logDirectory the jboss as log directory.
     * @param element the log configuration
     * @return the access log valve
     */
    static Valve createAccessLogValve(final File logDirectory, final WebAccessLogElement element) {
        final AccessLogValve log = new AccessLogValve();
        // TODO directory should be relative to jboss.log dir ?
        log.setDirectory(new File(logDirectory, element.getDirectory()).getAbsolutePath());
        log.setResolveHosts(element.isResolveHosts());
        log.setRotatable(element.isRotate());
        log.setPattern(element.getPattern());
        log.setPrefix(element.getPrefix());
        // TODO extended?
        return log;
    }
    
    /**
     * Create rewrite value.
     * 
     * @param element the rewrite configuration
     * @return a rewrite valve
     */
    static Valve createRewriteValve(WebRewriteElement element) {        
        final RewriteValve rewrite = new RewriteValve();
        // TODO
        return rewrite;
    }
    
}

