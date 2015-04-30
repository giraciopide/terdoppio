package com.wazzanau.terdoppio.trackerconnection.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class UdpTrackerConnection implements Runnable {

	private final String host; 
	private final int port;

	public UdpTrackerConnection(String host, int port) {
		this.host = host;
		this.port = port;
	}

	private volatile boolean running = false;
	private volatile EventLoopGroup group;
	private volatile Bootstrap bootstrap;
	private volatile ChannelFuture bindChannelFuture;
	private volatile Channel channel;

	public synchronized void start() {
		if (!running) {
			Thread t = new Thread(this);
			t.setName("UDP tracker client " + host + ":" + port);
			t.start();
		}
	}

	@Override
	public void run() {
		try {
			running = true;
			group = new NioEventLoopGroup();
			bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioDatagramChannel.class).handler(new LoggingHandler());

			// start the server
			bindChannelFuture = bootstrap.bind(port).sync();

			// save the channel for later when we want to send messages
			channel = bindChannelFuture.channel();

			// wait for the the server to be shutdown
			bindChannelFuture.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			// TODO log something here. 

		} finally {
			running = false;
			group.shutdownGracefully();
		}

	}

	public synchronized void stop() {
		if (running) {
			group.shutdownGracefully();
		}
	}

	public boolean isRunning() {
		return running;
	}

	//
	// 
	//

	public void announce(AnnounceRequest request, TrackerResponseHandler<UdpAnnounceResponse> responseHandler) {
		ByteBuf requestBytes = Unpooled.copiedBuffer(request.encode());

		channel.writeAndFlush(new DatagramPacket(requestBytes, new InetSocketAddress(host, port)));

		ResponseHandler<UdpAnnounceResponse> handler = new ResponseHandler<UdpAnnounceResponse>(request.getTransactionId(), responseHandler);
		channel.pipeline().addLast(handler);
	}
		

	/**
	 * A netty handler that reads a reply to its own transaction and subsequently
	 * remove itself from the pipeline.
	 * 
	 * @author marco.nicolini@gmail.com
	 *
	 * @param <T>
	 */
	private static class ResponseHandler<T> extends SimpleChannelInboundHandler<Response> {

		private final int transactionId;
		private final TrackerResponseHandler<T> userHandler;

		public ResponseHandler(int transactionId, TrackerResponseHandler<T> userHandler) {
			this.transactionId = transactionId;
			this.userHandler = userHandler;
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
			try {
				if (msg.getTransactionId() == transactionId) { // it's the response to our request!
					try {
						@SuppressWarnings("unchecked") // if the transaction id is the same as the request, it's a legit response to that request, so the type is not an issue.
						T response = (T)msg;
						userHandler.onResponse(response);
					} catch (Exception e) { // we don't want bad handlers to mess up with us
						// TODO log here.
						e.printStackTrace();
					}
				}
			} finally {
				// remove ourself from the handlers on the pipeline.
				ctx.channel().pipeline().remove(this);
			}
		}

	}
}
