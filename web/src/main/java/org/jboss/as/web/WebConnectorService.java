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

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import org.apache.catalina.connector.Connector;
import org.jboss.as.services.net.SocketBinding;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * Service creating and starting a web connector. TODO we maybe need different
 * implementation for different connector types ?
 *
 * @author Emanuel Muckenhuber
 */
class WebConnectorService implements Service<Connector> {

    private Connector connector;
    private final WebConnectorElement configuration;

    private InjectedValue<Executor> executor = new InjectedValue<Executor>();
    private InjectedValue<SocketBinding> binding = new InjectedValue<SocketBinding>();
    private InjectedValue<WebServer> server = new InjectedValue<WebServer>();

    WebConnectorService(final WebConnectorElement configuration) {
        this.configuration = configuration;
    }

    /**
     * Start, register and bind the web connector.
     *
     * @param context
     *            the start context
     * @throws StartException
     *             if the connector cannot be started
     */
    public synchronized void start(StartContext context) throws StartException {
        final SocketBinding binding = this.binding.getValue();

        final InetSocketAddress address = binding.getSocketAddress();
        try {
            // Create connector
            final Connector connector = new Connector();

            connector.setPort(address.getPort());
            connector.setProtocol(configuration.getProtocol());
            // connector.setScheme(configuration.getScheme());
            connector.setScheme("http"); // HACK!!!
            // connector.setProxyName("localhost"); // HACK !!!
            // connector.setProxyPort(8080); //HACK !!!
            // TODO set Executor on ProtocolHandler

            // TODO use server socket factory - or integrate with {@code
            // ManagedBinding}
            final WebServerSocketFactory socketFacatory = new WebServerSocketFactory(binding);

            // Register connector, starts the connector automatically
            // TODO start after deployments are complete ?
            server.getValue().addConnector(connector);
            this.connector = connector;
        } catch (Exception e) {
            throw new StartException(e, configuration.getLocation());
        }
    }

    /** {@inheritDoc} */
    public synchronized void stop(StopContext context) {
        final Connector connector = this.connector;
        server.getValue().removeConnector(connector);
        this.connector = null;
    }

    /** {@inheritDoc} */
    public synchronized Connector getValue() throws IllegalStateException {
        final Connector connector = this.connector;
        if (connector == null) {
            throw new IllegalStateException();
        }
        return connector;
    }

    InjectedValue<Executor> getExecutor() {
        return executor;
    }

    InjectedValue<SocketBinding> getBinding() {
        return binding;
    }

    InjectedValue<WebServer> getServer() {
        return server;
    }

}
