package net.minecraft.network;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;

import cc.unknown.Haru;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.utils.player.rotation.RotationManager;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.event.CompressionReorderEvent;
import de.florianmichael.viamcp.MCPVLBPipeline;
import de.florianmichael.viamcp.ViaMCP;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.chat.ChatComponentTranslation;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {
	private static final Logger logger = LogManager.getLogger();
	public static final Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
	public static final Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
	public static final AttributeKey<ConnectionState> attrKeyConnectionState = AttributeKey
			.<ConnectionState>valueOf("protocol");
	public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
		protected NioEventLoopGroup load() {
			return new NioEventLoopGroup(0,
					(new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
		}
	};
	public static final LazyLoadBase<EpollEventLoopGroup> field_181125_e = new LazyLoadBase<EpollEventLoopGroup>() {
		protected EpollEventLoopGroup load() {
			return new EpollEventLoopGroup(0,
					(new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
		}
	};
	public static final LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
		protected LocalEventLoopGroup load() {
			return new LocalEventLoopGroup(0,
					(new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
		}
	};
	private final PacketDirection direction;
	public final Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues
			.<InboundHandlerTuplePacketListener>newConcurrentLinkedQueue();
	public final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	/** The active channel */
	private Channel channel;

	/** The address of the remote party */
	private SocketAddress socketAddress;

	/** The INetHandler instance responsible for processing received packets */
	private INetHandler packetListener;

	/** A String indicating why the network has shutdown. */
	private IChatComponent terminationReason;
	private boolean isEncrypted;
	private boolean disconnected;

	public NetworkManager(PacketDirection packetDirection) {
		direction = packetDirection;
	}

	public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
		super.channelActive(p_channelActive_1_);
		channel = p_channelActive_1_.channel();
		socketAddress = channel.remoteAddress();

		try {
			setConnectionState(ConnectionState.HANDSHAKING);
		} catch (Throwable throwable) {
			logger.fatal((Object) throwable);
		}
	}

	/**
	 * Sets the new connection state and registers which packets this channel may
	 * send and receive
	 */
	public void setConnectionState(ConnectionState newState) {
		channel.attr(attrKeyConnectionState).set(newState);
		channel.config().setAutoRead(true);
		logger.debug("Enabled auto read");
	}

	public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception {
		closeChannel(new ChatComponentTranslation("disconnect.endOfStream", new Object[0]));
	}

	public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_)
			throws Exception {
		ChatComponentTranslation chat;

		if (p_exceptionCaught_2_ instanceof TimeoutException) {
			chat = new ChatComponentTranslation("disconnect.timeout", new Object[0]);
		} else {
			chat = new ChatComponentTranslation("disconnect.genericReason",
					new Object[] { "Internal Exception: " + p_exceptionCaught_2_ });
		}

		closeChannel(chat);
	}

	/**
	 * Sets the NetHandler for this NetworkManager, no checks are made if this
	 * handler is suitable for the particular connection state (protocol)
	 */
	public void setNetHandler(INetHandler handler) {
		Validate.notNull(handler, "packetListener", new Object[0]);
		logger.debug("Set listener of {} to {}", new Object[] { this, handler });
		packetListener = handler;
	}

	public void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_) throws Exception {
		if (channel.isOpen()) {
			try {
				PacketEvent e = new PacketEvent(PacketDirection.Inbound, p_channelRead0_2_);
				Haru.instance.getEventBus().post(e);
				if (!e.isCancelled())
					e.getPacket().processPacket(packetListener);
			} catch (ThreadQuickExitException thread) {
			}
		}
	}

	public void receivePacketSilent(final Packet packet) {
		if (channel.isOpen()) {
			try {
				packet.processPacket(packetListener);
			} catch (final ThreadQuickExitException var4) {
			}
		}
	}

	public void sendPacket(Packet packetIn) {
		PacketEvent e = new PacketEvent(PacketDirection.Outbound, packetIn);
		Haru.instance.getEventBus().post(e);
		if (e.isCancelled())
			return;

		if (isChannelOpen()) {
			flushOutboundQueue();
			dispatchPacket(packetIn, (GenericFutureListener<? extends Future<? super Void>>[]) null);
		} else {
			readWriteLock.writeLock().lock();
			try {
				outboundPacketsQueue
						.add(new InboundHandlerTuplePacketListener(e.getPacket(), (GenericFutureListener[]) null));
			} finally {
				readWriteLock.writeLock().unlock();
			}
		}
	}

	public void sendPacketSilent(Packet packetIn) {
		if (isChannelOpen()) {
			flushOutboundQueue();
			dispatchPacketSilent(packetIn, null);
		} else {
			readWriteLock.writeLock().lock();

			try {
				outboundPacketsQueue
						.add(new InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener[]) null));
			} finally {
				readWriteLock.writeLock().unlock();
			}
		}
	}

	public void sendPacket(Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener,
			GenericFutureListener<? extends Future<? super Void>>... listeners) {
		if (isChannelOpen()) {
			flushOutboundQueue();
			dispatchPacket(packetIn, (GenericFutureListener[]) ArrayUtils.add(listeners, 0, listener));
		} else {
			readWriteLock.writeLock().lock();

			try {
				outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packetIn,
						(GenericFutureListener[]) ArrayUtils.add(listeners, 0, listener)));
			} finally {
				readWriteLock.writeLock().unlock();
			}
		}
	}

	/**
	 * Will commit the packet to the channel. If the current thread 'owns' the
	 * channel it will write and flush the packet, otherwise it will add a task for
	 * the channel eventloop thread to do that.
	 */

	private void dispatchPacketSilent(final Packet packetIn,
			final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
		final ConnectionState enumconnectionstate = ConnectionState.getFromPacket(packetIn);
		final ConnectionState enumconnectionstate1 = this.channel.attr(attrKeyConnectionState).get();

		if (packetIn instanceof CPacketPlayer) {
			if (RotationManager.isEnabled) {
				CPacketPlayer wrapper = (CPacketPlayer) packetIn;
				wrapper.yaw = RotationManager.clientRotation[0];
				wrapper.pitch = RotationManager.clientRotation[1];
			}
		}

		if (enumconnectionstate1 != enumconnectionstate) {
			logger.debug("Disabled auto read");
			this.channel.config().setAutoRead(false);
		}

		if (this.channel.eventLoop().inEventLoop()) {
			if (enumconnectionstate != enumconnectionstate1) {
				this.setConnectionState(enumconnectionstate);
			}

			ChannelFuture channelfuture = this.channel.writeAndFlush(packetIn);

			if (futureListeners != null) {
				channelfuture.addListeners(futureListeners);
			}

			channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			this.channel.eventLoop().execute(new Runnable() {
				public void run() {
					if (enumconnectionstate != enumconnectionstate1) {
						NetworkManager.this.setConnectionState(enumconnectionstate);
					}

					ChannelFuture channelfuture1 = NetworkManager.this.channel.writeAndFlush(packetIn);

					if (futureListeners != null) {
						channelfuture1.addListeners(futureListeners);
					}

					channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
				}
			});
		}
	}

	public void dispatchPacket(Packet packetIn,
			final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
		final ConnectionState connectionstate = ConnectionState.getFromPacket(packetIn);
		final ConnectionState connectionstate1 = (ConnectionState) channel.attr(attrKeyConnectionState).get();
		if (packetIn instanceof CPacketPlayer) {
			if (RotationManager.isEnabled) {
				CPacketPlayer wrapper = (CPacketPlayer) packetIn;
				wrapper.yaw = RotationManager.clientRotation[0];
				wrapper.pitch = RotationManager.clientRotation[1];
			}
		}
		if (connectionstate1 != connectionstate) {
			logger.debug("Disabled auto read");
			channel.config().setAutoRead(false);
		}
		if (channel.eventLoop().inEventLoop()) {
			if (connectionstate != connectionstate1)
				setConnectionState(connectionstate);
			ChannelFuture channelfuture = channel.writeAndFlush(packetIn);
			if (futureListeners != null)
				channelfuture.addListeners((GenericFutureListener[]) futureListeners);
			channelfuture.addListener((GenericFutureListener) ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			channel.eventLoop().execute(new Runnable() {
				public void run() {
					if (connectionstate != connectionstate1)
						setConnectionState(connectionstate);
					ChannelFuture channelfuture1 = channel.writeAndFlush(packetIn);
					if (futureListeners != null)
						channelfuture1.addListeners(futureListeners);
					channelfuture1.addListener((GenericFutureListener) ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
				}
			});
		}
	}

	/**
	 * Will iterate through the outboundPacketQueue and dispatch all Packets
	 */
	public void flushOutboundQueue() {
		if (channel != null && channel.isOpen()) {
			readWriteLock.readLock().lock();

			try {
				while (!outboundPacketsQueue.isEmpty()) {
					InboundHandlerTuplePacketListener tuplePacket = (InboundHandlerTuplePacketListener) outboundPacketsQueue
							.poll();
					dispatchPacket(tuplePacket.packet, tuplePacket.futureListeners);
				}
			} finally {
				readWriteLock.readLock().unlock();
			}
		}
	}

	/**
	 * Checks timeouts and processes all packets received
	 */
	public void processReceivedPackets() {
		flushOutboundQueue();

		if (packetListener instanceof ITickable) {
			((ITickable) packetListener).update();
		}

		channel.flush();
	}

	/**
	 * Returns the socket address of the remote side. Server-only.
	 */
	public SocketAddress getRemoteAddress() {
		return socketAddress;
	}

	/**
	 * Closes the channel, the parameter can be used for an exit message (not
	 * certain how it gets sent)
	 */
	public void closeChannel(IChatComponent message) {
		if (channel.isOpen()) {
			channel.close().awaitUninterruptibly();
			terminationReason = message;
		}
	}

	/**
	 * True if this NetworkManager uses a memory connection (single player game).
	 * False may imply both an active TCP connection or simply no active connection
	 * at all
	 */
	public boolean isLocalChannel() {
		return channel instanceof LocalChannel || channel instanceof LocalServerChannel;
	}

	public static NetworkManager func_181124_a(InetAddress p_181124_0_, int p_181124_1_, boolean p_181124_2_) {
		final NetworkManager networkmanager = new NetworkManager(PacketDirection.Outbound);
		Class<? extends SocketChannel> oclass;
		LazyLoadBase<? extends EventLoopGroup> lazyloadbase;

		if (Epoll.isAvailable() && p_181124_2_) {
			oclass = EpollSocketChannel.class;
			lazyloadbase = field_181125_e;
		} else {
			oclass = NioSocketChannel.class;
			lazyloadbase = CLIENT_NIO_EVENTLOOP;
		}

		((Bootstrap) ((Bootstrap) ((Bootstrap) (new Bootstrap()).group((EventLoopGroup) lazyloadbase.getValue())).handler(new ChannelInitializer<Channel>() {
			protected void initChannel(Channel p_initChannel_1_) throws Exception {
				try {
					p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
					} catch (ChannelException var3) {
						;	
					}
				p_initChannel_1_.pipeline().addLast((String)"timeout", (ChannelHandler)(new ReadTimeoutHandler(30))).addLast((String)"splitter", (ChannelHandler)(new MessageDeserializer2())).addLast((String)"decoder", (ChannelHandler)(new MessageDeserializer(PacketDirection.Outbound))).addLast((String)"prepender", (ChannelHandler)(new MessageSerializer2())).addLast((String)"encoder", (ChannelHandler)(new MessageSerializer(PacketDirection.Inbound))).addLast((String)"packet_handler", (ChannelHandler)networkmanager);
				
				if (p_initChannel_1_ instanceof SocketChannel && ViaLoadingBase.getInstance().getTargetVersion().getVersion() != ViaMCP.NATIVE_VERSION) {
				    final UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
				    new ProtocolPipelineImpl(user);
				    
				    p_initChannel_1_.pipeline().addLast(new MCPVLBPipeline(user));
				}
			}
		})).channel(oclass)).connect(p_181124_0_, p_181124_1_).syncUninterruptibly();
		
		return networkmanager;
	}

	/**
	 * Prepares a clientside NetworkManager: establishes a connection to the socket
	 * supplied and configures the channel pipeline. Returns the newly created
	 * instance.
	 */
	public static NetworkManager provideLocalClient(SocketAddress address) {
		final NetworkManager networkmanager = new NetworkManager(PacketDirection.Outbound);
		((Bootstrap) ((Bootstrap) ((Bootstrap) (new Bootstrap())
				.group((EventLoopGroup) CLIENT_LOCAL_EVENTLOOP.getValue())).handler(new ChannelInitializer<Channel>() {
					protected void initChannel(Channel p_initChannel_1_) throws Exception {
						p_initChannel_1_.pipeline().addLast((String) "packet_handler", (ChannelHandler) networkmanager);
					}
				})).channel(LocalChannel.class)).connect(address).syncUninterruptibly();
		return networkmanager;
	}

	/**
	 * Adds an encoder+decoder to the channel pipeline. The parameter is the secret
	 * key used for encrypted communication
	 */
	public void enableEncryption(SecretKey key) {
		isEncrypted = true;
		channel.pipeline().addBefore("splitter", "decrypt",
				new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
		channel.pipeline().addBefore("prepender", "encrypt",
				new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
	}

	public boolean getIsencrypted() {
		return isEncrypted;
	}

	/**
	 * Returns true if this NetworkManager has an active channel, false otherwise
	 */
	public boolean isChannelOpen() {
		return channel != null && channel.isOpen();
	}

	public boolean hasNoChannel() {
		return channel == null;
	}

	/**
	 * Gets the current handler for processing packets
	 */
	public INetHandler getNetHandler() {
		return packetListener;
	}

	/**
	 * If this channel is closed, returns the exit message, null otherwise.
	 */
	public IChatComponent getExitMessage() {
		return terminationReason;
	}

	/**
	 * Switches the channel to manual reading modus
	 */
	public void disableAutoRead() {
		channel.config().setAutoRead(false);
	}

	public void setCompressionTreshold(int treshold) {
		if (treshold >= 0) {
			if (channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
				((NettyCompressionDecoder) channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
			} else {
				channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(treshold));
			}

			if (channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
				((NettyCompressionEncoder) channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
			} else {
				channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(treshold));
			}
		} else {
			if (channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
				channel.pipeline().remove("decompress");
			}

			if (channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
				channel.pipeline().remove("compress");
			}
		}
		this.channel.pipeline().fireUserEventTriggered(new CompressionReorderEvent());
	}

	public void checkDisconnected() {
		if (channel != null && !channel.isOpen()) {
			if (!disconnected) {
				disconnected = true;

				if (getExitMessage() != null) {
					getNetHandler().onDisconnect(getExitMessage());
				} else if (getNetHandler() != null) {
					getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
				}
			} else {
				logger.warn("handleDisconnection() called twice");
			}
		}
	}

	public static class InboundHandlerTuplePacketListener {
		private final Packet packet;
		private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

		public Packet getPacket() {
			return packet;
		}

		public GenericFutureListener<? extends Future<? super Void>>[] getFutureListeners() {
			return futureListeners;
		}

		public InboundHandlerTuplePacketListener(Packet inPacket,
				GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
			packet = inPacket;
			futureListeners = inFutureListeners;
		}
	}
}
