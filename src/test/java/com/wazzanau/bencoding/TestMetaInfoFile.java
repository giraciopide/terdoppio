package com.wazzanau.bencoding;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.wazzanau.terdoppio.bencode.DecodingException;
import com.wazzanau.terdoppio.metainfo.MetaInfoFile;

public class TestMetaInfoFile {

	@Test
	public void test() throws DecodingException, IOException {
			
		Iterator<File> iter = FileUtils.iterateFiles(new File("src/test/resources/torrents"), new String[] {"torrent"}, false);
		while (iter.hasNext()) {
			// reading and decoding from a torrent file.
			File inFile = iter.next();
			System.out.println("Testing decode for: " + inFile.getAbsolutePath());
			MetaInfoFile metaInfo = MetaInfoFile.fromFile(inFile);
			System.out.println(metaInfo);
		}
	}
}
