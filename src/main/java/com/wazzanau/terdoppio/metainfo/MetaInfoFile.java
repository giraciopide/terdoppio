package com.wazzanau.terdoppio.metainfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.wazzanau.terdoppio.bencode.BEDictionary;
import com.wazzanau.terdoppio.bencode.BEString;
import com.wazzanau.terdoppio.bencode.BEValue;
import com.wazzanau.terdoppio.bencode.BEncoding;
import com.wazzanau.terdoppio.bencode.DecodingException;

public class MetaInfoFile {
	private static final String KEY_ANNOUNCE = "announce";
	private static final String KEY_INFO = "info";
	
	private String announce;
	private InfoDictionary info;
		
	public String getAnnounce() {
		return announce;
	}

	public void setAnnounce(String announce) {
		this.announce = announce;
	}

	public InfoDictionary getInfo() {
		return info;
	}

	public void setInfo(InfoDictionary info) {
		this.info = info;
	}
	
	public BEDictionary asDict() {
		BEDictionary dict = new BEDictionary();
		dict.put(KEY_ANNOUNCE, new BEString(announce));
		dict.put(KEY_INFO, info.asDict());
		return dict;
	}

	public static MetaInfoFile fromFile(File file) throws DecodingException, IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		
		MetaInfoFile metaInfo = new MetaInfoFile();
		BEDictionary dict = BEncoding.decodeBEDictionary(bis);
		
		// announce
		BEValue announceValue = dict.get(KEY_ANNOUNCE);
		if (announceValue != null) {
			metaInfo.announce = announceValue.asString().get();
		} else {
			bis.close();
			throw new DecodingException("File " + file + " is not a valid metainfo file: missing key [" + KEY_ANNOUNCE + "]"); 
		}
		
		// info 
		BEValue infoValue = dict.get(KEY_INFO);
		if (infoValue != null) {
			metaInfo.info = InfoDictionary.fromDict(infoValue.asDict());
			
		} else {
			bis.close();
			throw new DecodingException("File " + file + " is not a valid metainfo file: missing key [" + KEY_INFO + "]"); 
		}
		
		return metaInfo;
	}

	@Override
	public String toString() {
		return "MetaInfoFile [announce=" + announce + ", info=" + info + "]";
	}
}
