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
package org.jboss.as.web.deployment;

import org.apache.catalina.Context;
import org.jboss.as.web.WebServer;
import org.jboss.msc.inject.InjectionException;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.value.Value;
import org.jboss.msc.value.Values;

/**
 * {@code Injector} registering a web context at the web server.
 *
 * @author Emanuel Muckenhuber
 */
class WebContextInjector implements Injector<WebServer> {

    private WebServer server;
    private final String hostName;
    private final Value<Context> context;

    public WebContextInjector(String hostName, Value<Context> context) {
        this.context = context;
        this.hostName = hostName;
    }

    public WebContextInjector(String hostName, Context context) {
        this.context = Values.immediateValue(context);
        this.hostName = hostName;
    }

    public void inject(WebServer server) throws InjectionException {
        server.addContext(hostName, context.getValue());
        this.server = server;
    }

    public void uninject() {
        if (server != null) {
            server.removeContext(hostName, context.getValue());
        }
    }

}
