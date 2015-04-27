package com.wazzanau.terdoppio.peerconnection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;
import java.util.List;

import com.wazzanau.terdoppio.bencode.DecodingException;
import com.wazzanau.terdoppio.peerclient.messages.BitField;
import com.wazzanau.terdoppio.peerclient.messages.Cancel;
import com.wazzanau.terdoppio.peerclient.messages.Choke;
import com.wazzanau.terdoppio.peerclient.messages.Have;
import com.wazzanau.terdoppio.peerclient.messages.Interested;
import com.wazzanau.terdoppio.peerclient.messages.KeepAlive;
import com.wazzanau.terdoppio.peerclient.messages.NotInterested;
import com.wazzanau.terdoppio.peerclient.messages.PeerMessage;
import com.wazzanau.terdoppio.peerclient.messages.PeerMessageType;
import com.wazzanau.terdoppio.peerclient.messages.Piece;
import com.wazzanau.terdoppio.peerclient.messages.Port;
import com.wazzanau.terdoppio.peerclient.messages.Request;
import com.wazzanau.terdoppio.peerclient.messages.UnChoke;

public class PeerProtocolMessagDecoder extends ByteToMessageDecoder {
	
	private boolean readingLen = true;
	private int msgLen = 0;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		ByteBuf buf = in.order(ByteOrder.BIG_ENDIAN);
		
		if (readingLen && in.readableBytes() >= 4) {
			msgLen = readIntOrFail(buf, "reading message length");
			readingLen = false;
			
			// special case for keep alive which is just len 0, without message id.
			if (msgLen == 0) { 
				out.add(KeepAlive.MESSAGE);
				readingLen = true;
			}
		} 
		
		if (!readingLen && in.readableBytes() >= msgLen) {
			PeerMessage msg = decodeMessage(buf, msgLen);
			out.add(msg);
			readingLen = true;
		}
	}
	
	/**
	 * Decods a message with the given msgLen from the ByteBuf.
	 * @param buf
	 * @param msgLen - including the message id.
	 * @return
	 * @throws DecodingException 
	 */
	private static PeerMessage decodeMessage(ByteBuf buf, int msgLen) throws DecodingException {
		int messageType = buf.readUnsignedByte();
		
		PeerMessage msg = null;
		
		switch (messageType) {
		
		case PeerMessageType.MSG_ID_CHOKE:
			validateMsgLenEquals(msgLen, 1);
			msg = Choke.MESSAGE;
			break;
			
		case PeerMessageType.MSG_ID_UNCHOKE:
			validateMsgLenEquals(msgLen, 1);
			msg = UnChoke.MESSAGE;
			break;
			
		case PeerMessageType.MSG_ID_INTERESTED:
			validateMsgLenEquals(msgLen, 1);
			msg = Interested.MESSAGE;
			break;
			
		case PeerMessageType.MSG_ID_NOT_INTERESTED:
			validateMsgLenEquals(msgLen, 1);
			msg = NotInterested.MESSAGE;
			break;
			
		case PeerMessageType.MSG_ID_HAVE:
			validateMsgLenEquals(msgLen, 5);
			long pieceIndex = buf.readUnsignedInt();
			msg = new Have(pieceIndex);
			break;
			
		case PeerMessageType.MSG_ID_BITFIELD:
			validateMsgLenEqualOrGreaterThan(msgLen, 1);
			int bitFieldLen = msgLen - 1;
			byte[] bitField = new byte[bitFieldLen];
			for (int i = 0; i < bitFieldLen; ++i) {
				bitField[i] = buf.readByte();
			}
			msg = new BitField(bitField);
			break;
			
		case PeerMessageType.MSG_ID_REQUEST: {
			validateMsgLenEquals(msgLen, 13);
			int index = readIntOrFail(buf, "reading index field from request message");
			int begin = readIntOrFail(buf, "reading begin field from request message");
			int length = readIntOrFail(buf, "reading length field from request message");
			msg = new Request(index, begin, length);
			break;
		}
			
		case PeerMessageType.MSG_ID_PIECE: {
			validateMsgLenEqualOrGreaterThan(msgLen, 9);
			int index = readIntOrFail(buf, "reading index from piece message");
			int begin = readIntOrFail(buf, "reading index from piece message");
			int blockLen = msgLen - 1 - 4 - 4; // id, index, begin
			byte[] block = new byte[blockLen];
			buf.readBytes(block);
			msg = new Piece(index, begin, block);
			break;
		}
			
		case PeerMessageType.MSG_ID_CANCEL: {
			int index = readIntOrFail(buf, "reading index field from request message");
			int begin = readIntOrFail(buf, "reading begin field from request message");
			int length = readIntOrFail(buf, "reading length field from request message");
			msg = new Cancel(index, begin, length);
			break;
		}
			
		case PeerMessageType.MSG_ID_PORT: 
			validateMsgLenEquals(msgLen, 3);
			int dhtListenPort = buf.readUnsignedShort();
			msg = new Port(dhtListenPort);
			break;

		default:
			throw new DecodingException("Unrecognized message id: hex [" + Integer.toHexString(messageType) + "]");
		}
		
		return msg;
	}
	
	private static int readIntOrFail(ByteBuf buf, String errorReason) throws DecodingException {
		long value = buf.readUnsignedInt();
		if (value > Integer.MAX_VALUE) {
			throw new DecodingException("Read value to big for Java int while " + errorReason + " was: " + value);
		}
		return (int)value;
	}
	
	/**
	 * Helper to throw a decoding exception if the msg length is not the expected.
	 * Used where message len is fixed
	 * @param expectedMsgLen
	 * @param actualMsgLen
	 * @throws DecodingException 
	 */
	private static void validateMsgLenEquals(long actualMsgLen, long expectedMsgLen) throws DecodingException {
		if (expectedMsgLen != actualMsgLen) {
			throw new DecodingException("Unexpected msg len: expected [" + expectedMsgLen + "] actual [" + actualMsgLen + "]");
		}
	}
	
	/**
	 * Helper to throw a decoding exception if the msg length not equal or bigger than what is expected
	 * @param expectedMsgLen
	 * @param actualMsgLen
	 * @throws DecodingException 
	 */
	private static void validateMsgLenEqualOrGreaterThan(long actualMsgLen, long minExpectedLen) throws DecodingException {
		if (actualMsgLen < minExpectedLen) {
			throw new DecodingException("Unexpected msg len: expected [" + actualMsgLen + "] should be greater than [" + minExpectedLen + "]");
		}
	}

}