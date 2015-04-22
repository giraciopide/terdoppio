package com.wazzanau.terdoppio.bencode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class BEList implements BEValue, List<BEValue> {
	
	private final List<BEValue> list = new ArrayList<BEValue>();

	@Override
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(getEncodedLength());
		encode(buf);
		return buf.array();
	}

	@Override
	public void encode(ByteBuffer buf) {
		buf.put(BEncoding.DELIM_LIST_START);
		for (BEValue item: list) {
			item.encode(buf);
		}
		buf.put(BEncoding.DELIM_LIST_END);
	}

	@Override
	public int getEncodedLength() {
		int len = 2; // DELIM_START + DELIM_END len
		for (BEValue item: list) {
			len += item.getEncodedLength();
		}
		return len;
	}

	@Override
	public BEType getType() {
		return BEType.LIST;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<BEValue> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(BEValue e) {
		return list.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends BEValue> c) {
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends BEValue> c) {
		return list.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public BEValue get(int index) {
		return list.get(index);
	}

	@Override
	public BEValue set(int index, BEValue element) {
		return list.set(index, element);
	}

	@Override
	public void add(int index, BEValue element) {
		list.add(index, element);
	}

	@Override
	public BEValue remove(int index) {
		return list.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<BEValue> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<BEValue> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public List<BEValue> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
	
	//
	// Value coercion interface
	// 
	
	@Override
	public BEString asString() {
		throw new ClassCastException("Cannot bring a BEList to a BEString");
	}

	@Override
	public BEDictionary asDict() {
		throw new ClassCastException("Cannot bring a BEList to a BEDictionary");
	}

	@Override
	public BEList asList() {
		return this;
	}

	@Override
	public BEInteger asInt() {
		throw new ClassCastException("Cannot bring a BEList to a BEInteger");
	}
	
	public List<String> asListOfStrings() {
		List<String> out = new ArrayList<String>();
		for (BEValue item: this) {
			out.add(item.toString());
		}
		return out;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder("[");
		Iterator<BEValue> iter = list.iterator();
		while (iter.hasNext()) {
			BEValue item = iter.next();
			out.append(item.toString());
			if (iter.hasNext()) {
				out.append(", ");
			}
		}
		out.append("]");
		return out.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BEList other = (BEList) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}
}
