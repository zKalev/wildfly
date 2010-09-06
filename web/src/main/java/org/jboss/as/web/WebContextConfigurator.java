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

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;

/**
 * Internal helper for common context configurations. Sort of a replacement for the 
 * shared web.xml, aggregating common configuration and operations from the domain model.
 * 
 * @author Emanuel Muckenhuber
 */
class WebContextConfigurator {

    /** The common container config. */
    private WebContainerConfigElement containerConfig;

    public WebContextConfigurator(final WebContainerConfigElement containerConfig) {
        this.containerConfig = containerConfig;
    }
    
    /**
     * Process a web context.
     * 
     * @param context the web context.
     */
    void process(Context context) {
        // Enable resource serving, in case there is no existing "/" mapping
        if(context.findServletMapping("/") != null) {
            enableResourceService(context);    
        }
    }
    
    /**
     * Enable resource serving by adding the {@code DefaultServlet}, using
     * the domain resource serving configuration.
     * 
     * @param context the web context
     */
    void enableResourceService(final Context context) {
        final WebResourceServing resourcesConfig = containerConfig.getResourceServing();
        // Check disabled
        if(resourcesConfig != null && resourcesConfig.isDisabled()) {
            return; 
        }
        final Wrapper wrapper = context.createWrapper();
        wrapper.setName("DefaultServlet");
        wrapper.setLoadOnStartup(1);
        wrapper.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        if(resourcesConfig != null) {
            wrapper.addInitParameter("listings", String.valueOf(resourcesConfig.isListings()));
            wrapper.addInitParameter("readonly", String.valueOf(resourcesConfig.isReadOnly()));
            wrapper.addInitParameter("sendfileSize", String.valueOf(resourcesConfig.getSendfileSize()));
            wrapper.addInitParameter("fileEncoding", resourcesConfig.getFileEncoding());
            // TODO ...
        }
        // FIXME remove
        wrapper.addInitParameter("debug","99");
        wrapper.addInitParameter("listings", "true");
        
        context.addChild(wrapper);
        context.addServletMapping("/", "DefaultServlet");
    }
    
}

