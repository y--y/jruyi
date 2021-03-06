/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jruyi.io.udpserver;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.jruyi.common.StrUtil;
import org.jruyi.io.channel.IChannel;
import org.jruyi.io.channel.IIoWorker;
import org.jruyi.io.channel.ISelectableChannel;
import org.jruyi.io.channel.ISelector;
import org.jruyi.io.udp.UdpChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class UdpServerChannel implements ISelectableChannel, Runnable {

	private static final Logger c_logger = LoggerFactory.getLogger(UdpServerChannel.class);

	// Preserved (-100, 0] for UdpServerChannel
	private static long s_sequence = -100L;

	private final Long m_id;
	private final UdpServer m_udpServer;
	private final DatagramChannel m_datagramChannel;
	private final SocketAddress m_localAddr;
	private SelectionKey m_selectionKey;
	private IIoWorker m_ioWorker;

	public UdpServerChannel(UdpServer udpServer, DatagramChannel datagramChannel, SocketAddress localAddr) {
		m_id = ++s_sequence;
		m_udpServer = udpServer;
		m_datagramChannel = datagramChannel;
		m_localAddr = localAddr;
	}

	@Override
	public Long id() {
		return m_id;
	}

	// runs on read
	@Override
	public void run() {
		final UdpServer server = m_udpServer;
		try {
			final ByteBuffer bb = server.getChannelAdmin().recvDirectBuffer();
			final SocketAddress remoteAddr = m_datagramChannel.receive(bb);

			IChannel channel = server.getChannel(remoteAddr);
			if (channel == null) {
				DatagramChannel datagramChannel = DatagramChannel.open();
				DatagramSocket socket = datagramChannel.socket();
				socket.setReuseAddress(true);
				socket.bind(m_localAddr);
				channel = new UdpChannel(server, datagramChannel, remoteAddr);
				channel.connect(-1);
			}

			bb.flip();
			channel.receive(bb);
			channel.onReadRequired();
		} catch (Throwable t) {
			c_logger.error(StrUtil.join(server, " failed to receive message"), t);
			close();
		}
	}

	@Override
	public void close() {
		m_udpServer.stop();
	}

	@Override
	public void onConnect() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onAccept() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void ioWorker(IIoWorker ioWorker) {
		m_ioWorker = ioWorker;
	}

	@Override
	public void onRead() {
		m_ioWorker.execute(this);
	}

	@Override
	public void onWrite() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onException(Throwable t) {
		c_logger.error(StrUtil.join(m_udpServer, " got an error"), t);
		close();
	}

	@Override
	public void register(ISelector selector, int ops) throws Throwable {
		m_selectionKey = m_datagramChannel.register(selector.selector(), ops, this);
	}

	@Override
	public void interestOps(int ops) {
		final SelectionKey selectionKey = m_selectionKey;
		selectionKey.interestOps(selectionKey.interestOps() | ops);
	}
}
