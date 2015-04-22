package com.wazzanau.terdoppio.metainfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.wazzanau.terdoppio.bencode.BEString;
import com.wazzanau.terdoppio.bencode.BEncoding;

public class Pieces implements List<Piece> {
	private final List<Piece> pieces = new ArrayList<Piece>();

	@Override
	public int size() {
		return pieces.size();
	}

	@Override
	public boolean isEmpty() {
		return pieces.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return pieces.contains(o);
	}

	@Override
	public Iterator<Piece> iterator() {
		return pieces.iterator();
	}

	@Override
	public Object[] toArray() {
		return pieces.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return pieces.toArray(a);
	}

	@Override
	public boolean add(Piece e) {
		return pieces.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return pieces.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return pieces.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Piece> c) {
		return pieces.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Piece> c) {
		return pieces.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return pieces.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return pieces.retainAll(c);
	}

	@Override
	public void clear() {
		pieces.clear();
	}

	@Override
	public boolean equals(Object o) {
		return pieces.equals(o);
	}

	@Override
	public int hashCode() {
		return pieces.hashCode();
	}

	@Override
	public Piece get(int index) {
		return pieces.get(index);
	}

	@Override
	public Piece set(int index, Piece element) {
		return pieces.set(index, element);
	}

	@Override
	public void add(int index, Piece element) {
		pieces.add(index, element);
	}

	@Override
	public Piece remove(int index) {
		return pieces.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return pieces.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return pieces.lastIndexOf(o);
	}

	@Override
	public ListIterator<Piece> listIterator() {
		return pieces.listIterator();
	}

	@Override
	public ListIterator<Piece> listIterator(int index) {
		return pieces.listIterator(index);
	}

	@Override
	public List<Piece> subList(int fromIndex, int toIndex) {
		return pieces.subList(fromIndex, toIndex);
	}
	
	@Override
	public String toString() {
		return "Pieces [pieces count="  +  pieces.size() +  "]";
	}

	/**
	 * Convenience method to convert to a BEString (which is encodable)
	 * @return
	 */
	public BEString asBEString() {
		ByteBuffer buf = ByteBuffer.allocate(BEncoding.PIECE_LEN * pieces.size());
		for (Piece piece: pieces) {
			buf.put(piece.getPiece());
		}
		return new BEString(buf.array());
	}
}
