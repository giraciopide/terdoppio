package com.wazzanau.terdoppio.peerclient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class WireProtocolMessagDecoder extends ByteToMessageDecoder {
	
	private boolean expectHandshake;
	
	public WireProtocolMessagDecoder(boolean expectHandshake) {
		this.expectHandshake = expectHandshake;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

	}
	

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO log something here...
		ctx.close();
	}

}
