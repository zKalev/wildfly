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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.tomcat.util.net.ServerSocketFactory;
import org.jboss.as.services.net.SocketBinding;

/**
 * Server socket factory creating managed socket bindings. This factory ignores
 * port and inetAddress arguments, since come from the domain model. TODO check
 * if that makes sense!
 *
 * @author Emanuel Muckenhuber
 */
class WebServerSocketFactory extends ServerSocketFactory {

    private final SocketBinding socketBinding;

    public WebServerSocketFactory(final SocketBinding socketBinding) {
        this.socketBinding = socketBinding;
    }

    /** {@inheritDoc} */
    public Socket acceptSocket(ServerSocket server) throws IOException {
        return server.accept();
    }

    /** {@inheritDoc} */
    public ServerSocket createSocket(int arg0) throws IOException, InstantiationException {
        return socketBinding.createServerSocket();
    }

    /** {@inheritDoc} */
    public ServerSocket createSocket(int arg0, int arg1) throws IOException, InstantiationException {
        return socketBinding.createServerSocket(arg1);
    }

    /** {@inheritDoc} */
    public ServerSocket createSocket(int arg0, int arg1, InetAddress arg2) throws IOException, InstantiationException {
        return socketBinding.createServerSocket(arg1);
    }

    /** {@inheritDoc} */
    public void handshake(Socket arg0) throws IOException {
        // ??
    }

}
