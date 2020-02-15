package com.wazzanau.terdoppio.bencode;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Dictionaries are encoded as a 'd' followed by a list of alternating keys and their corresponding values 
 * followed by an 'e'. For example, d3:cow3:moo4:spam4:eggse corresponds to {'cow': 'moo', 'spam': 'eggs'} 
 * and d4:spaml1:a1:bee corresponds to {'spam': ['a', 'b']}. Keys must be strings and appear in sorted order 
 * (sorted as raw strings, not alphanumerics).
 */
public class BEDictionary implements BEValue, Map<BEString, BEValue> {
	
	private final static String DBL_QUOTE = "\"";
	
	/**
	 * The inner dictionary is a Tree map because of the requirement of key ordering in the protocol.
	 * 
	 * The supplied Comparator sorts dict key as "raw strings" as mandated by the bit torrent spec.
	 * Probably the constraint on key ordering is to guarantee a unique encoded representation of dictionaries
	 * so that calculating hashes on top of it makes sense.
	 */
	private final Map<BEString, BEValue> dictionary = new TreeMap<>(BEString.RAW_BYTES_COMPARATOR);
	
	public BEValue get(BEString key) {
		return dictionary.get(key);
	}
		
	public BEValue get(byte[] key) {
		BEString beKey = new BEString(key);
		return dictionary.get(beKey);
	}
	
	public BEValue get(String key) {
		BEString beKey = new BEString(key);
		return dictionary.get(beKey);
	}
	
	public BEValue put(byte[] key, BEValue value) {
		return dictionary.put(new BEString(key), value);
	}
	
	public BEValue put(String key, BEValue value) {
		return dictionary.put(new BEString(key), value);
	}

	@Override
	public BEType getType() {
		return BEType.DICTIONARY;
	}

	@Override
	public byte[] encode() {
		ByteBuffer buf = ByteBuffer.allocate(getEncodedLength());
		encode(buf);
		return buf.array();
	}

	@Override
	public void encode(ByteBuffer buf) {
		buf.put(BEncoding.DELIM_DICT_START);
		for (Map.Entry<BEString, BEValue> entry: dictionary.entrySet()) {
			entry.getKey().encode(buf);
			entry.getValue().encode(buf);
		}
		buf.put(BEncoding.DELIM_DICT_END);
	}

	@Override
	public int getEncodedLength() {
		int len = 2; // DELIM_START + DELIM_END len
		for (Map.Entry<BEString, BEValue> entry: dictionary.entrySet()) {
			len += entry.getKey().getEncodedLength();
			len += entry.getValue().getEncodedLength();
		}
		return len;
	}

	@Override
	public int size() {
		return dictionary.size();
	}

	@Override
	public boolean isEmpty() {
		return dictionary.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return dictionary.containsKey(key);
	}
	
	public boolean containsKey(String key) {
		return dictionary.containsKey(new BEString(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return dictionary.containsValue(value);
	}

	@Override
	public BEValue get(Object key) {
		return dictionary.get(key);
	}

	@Override
	public BEValue put(BEString key, BEValue value) {
		return dictionary.put(key, value);
	}

	@Override
	public BEValue remove(Object key) {
		return dictionary.remove(key);
	}

	@Override
	public void putAll(Map<? extends BEString, ? extends BEValue> m) {
		dictionary.putAll(m);
	}

	@Override
	public void clear() {
		dictionary.clear();
	}

	@Override
	public Set<BEString> keySet() {
		return dictionary.keySet();
	}

	@Override
	public Collection<BEValue> values() {
		return dictionary.values();
	}

	@Override
	public Set<Entry<BEString, BEValue>> entrySet() {
		return dictionary.entrySet();
	}
		
	//
	// Value coercion interface
	// 
	
	@Override
	public BEString asString() {
		throw new ClassCastException("Cannot transform BEDictionary to BEString");
	}

	@Override
	public BEDictionary asDict() {
		return this;
	}

	@Override
	public BEList asList() {
		throw new ClassCastException("Cannot transform a BEDictionary to BEList");
	}

	@Override
	public BEInteger asInt() {
		throw new ClassCastException("Cannot transform a BEDictionary to BEInteger");
	}
	
	//
	// java.lang.object stuff
	// 

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("{ ");
		Iterator<Map.Entry<BEString, BEValue>> iter = entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<BEString, BEValue> entry = iter.next();
			out.append(DBL_QUOTE).append(entry.getKey().toString()).append(DBL_QUOTE);
			out.append(": ");
			out.append(entry.getValue().toString());
			if (iter.hasNext()) {
				out.append(", ");
			}
		}
		out.append(" }");
		return out.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dictionary == null) ? 0 : dictionary.hashCode());
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
		BEDictionary other = (BEDictionary) obj;
		if (dictionary == null) {
			if (other.dictionary != null)
				return false;
		} else if (!dictionary.equals(other.dictionary))
			return false;
		return true;
	}


}
