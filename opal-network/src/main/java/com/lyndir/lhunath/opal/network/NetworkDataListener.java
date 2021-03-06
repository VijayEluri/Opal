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
package com.lyndir.lhunath.opal.network;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


/**
 * This listener should be implemented by classes that wish to be notified of incoming network messages.<br>
 *
 * @author lhunath
 */
public interface NetworkDataListener {

    /**
     * Data has been received over the network.
     *
     * @param dataBuffer A byte buffer that contains the available data. It has been flipped and is ready to be read from. To read the
     *                   data from it multiple times, flip it between complete read operations.
     * @param channel    The channel over which the message has arrived.
     */
    void received(ByteBuffer dataBuffer, SocketChannel channel);
}
