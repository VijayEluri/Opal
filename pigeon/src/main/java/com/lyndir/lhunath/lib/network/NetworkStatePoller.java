/*
 *   Copyright 2005-2007 Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.lib.network;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.lyndir.lhunath.lib.system.Poller;


/**
 * A poller that collects state changes from the network.
 *
 * @author lhunath
 */
public class NetworkStatePoller extends Poller<NetworkStatePoller.State, SocketChannel>
        implements NetworkServerStateListener, NetworkConnectionStateListener {

    /**
     * Create a new {@link NetworkStatePoller} instance. Register it on the network you're interested in.
     */
    public NetworkStatePoller() {

    }

    /**
     * Create a new {@link NetworkStatePoller} instance that listens on the given network.
     *
     * @param network
     *            The network whose messages we should be polling.
     */
    public NetworkStatePoller(Network network) {

        network.registerServerStateListener( this );
        network.registerConnectionStateListener( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bound(ServerSocketChannel serverSocket) {

    // Not supported.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accepted(ServerSocketChannel serverSocket, SocketChannel connectionSocket) {

        offer( State.ACCEPTED, connectionSocket );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(SocketChannel connectionSocket) {

        offer( State.CONNECTED, connectionSocket );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closed(SocketChannel connectionSocket, boolean resetByPeer) {

        offer( State.CLOSED, connectionSocket );
    }


    /**
     * States that a network socket can progress into.
     */
    public enum State {

        /**
         * A new connection has been accepted by a listening socket. The element is the new connection's socket.
         */
        ACCEPTED,

        /**
         * A connection has been established to a remote server. The element is the connection's socket.
         */
        CONNECTED,

        /**
         * An active connection has been terminated. The element is the connection's socket.
         */
        CLOSED;
    }
}
