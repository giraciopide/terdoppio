package com.wazzanau.terdoppio.metainfo;

public class Piece {
	private final int index;
	private final byte[] piece;
	
	public Piece(int index, byte[] piece) {
		this.index = index;
		this.piece = piece;
	}
	
	public int getIndex() {
		return index;
	}

	public byte[] getPiece() {
		return piece;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder("BEPiece [");
		for (int i = 0; i < piece.length; ++i) {
			String hex = Integer.toHexString(piece[i] & 0xFF);
			if (hex.length() < 2) {
				out.append("0");
			}
			out.append(hex);
			
			// spacing
			if (i != piece.length - 1) {
				out.append(" ");
			}
		}
		out.append("]");
		return out.toString();
	}
}
