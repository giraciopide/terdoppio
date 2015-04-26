package com.wazzanau.terdoppio.peerclient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handler that consumes the handshake message and notifies listener when the handshake message has been completely received.
 * @author marco.nicolini@gmail.com
 *
 */
public class HandshakeHandler extends ChannelInboundHandlerAdapter {

	private String protocolString;
	private byte[] reservedBytes = new byte[8];
	private byte[] infoHashBytes = new byte[20];
	private String infoHash;
	private byte[] peerIdBytes = new byte[20];
	private String peerId;

	// state
	private boolean handshaking;
	private boolean readingHandshakeBody;
	private short pstrlen;
	
	private List<HandshakeReceived> listeners = new CopyOnWriteArrayList<HandshakeReceived>();

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		handshaking = true;
		readingHandshakeBody = false;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf in = ((ByteBuf) msg).order(ByteOrder.BIG_ENDIAN);
		
		if (handshaking) {
			if (!readingHandshakeBody) { // we're not reading the pstr string yet.
				pstrlen = in.readUnsignedByte();
				readingHandshakeBody = true;
			}

			if (in.readableBytes() >= pstrlen + 8 + 20 + 20) { // 8 reserved bytes, 20 info_hash bytes, 20 peer_id bytes
				// we can read all the remaining part of the handshake.
				byte[] protocolStringBytes = new byte[pstrlen];
				in.readBytes(protocolStringBytes);
				protocolString = new String(protocolStringBytes);

				in.readBytes(reservedBytes);
				in.readBytes(infoHashBytes);
				infoHash = new String(infoHashBytes);
				in.readBytes(peerIdBytes);
				peerId = new String(peerIdBytes);

				// finished reading the handshake
				handshaking = false;
				
				notifyHandshakeReceived();
				
				// if there is other data to be consumed, we need to pass the buffer on to the next handlers.
				if (in.readableBytes() > 0) {
					ctx.fireChannelRead(msg);
				} else { 
					// if by chance, we have fully consumed the buf with the handshake message, we release it.
					in.release();
				}
			}
		} else { // if we're not handshaking, we do nothing except forward to the next handler.
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
		cause.printStackTrace();
		ctx.close();
	}
	
	private void notifyHandshakeReceived() {
		// TODO fire the netty user event here.
		
		for (HandshakeReceived listener: listeners) {
			try {
				listener.onHandshakeDone(protocolString, reservedBytes, infoHashBytes, peerIdBytes);
			} catch (Exception e) { // don't want bad listeners to mess us.
				// TODO Log here.
			}
		}
	}

	public String getProtocolString() {
		return protocolString;
	}

	public byte[] getReservedBytes() {
		return reservedBytes;
	}

	public byte[] getInfoHashBytes() {
		return infoHashBytes;
	}

	public String getInfoHash() {
		return infoHash;
	}

	public byte[] getPeerIdBytes() {
		return peerIdBytes;
	}

	public String getPeerId() {
		return peerId;
	}


}

