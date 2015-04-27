package com.wazzanau.bencoding;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.wazzanau.terdoppio.bencode.BEDictionary;
import com.wazzanau.terdoppio.bencode.BEncoding;
import com.wazzanau.terdoppio.bencode.DecodingException;
import com.wazzanau.terdoppio.metainfo.MetaInfoFile;
import com.wazzanau.terdoppio.trackerconnection.http.ByteUtils;

public class BEDictionaryTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testDecodeEncodeCycle1() throws DecodingException, IOException {
			
		Iterator<File> iter = FileUtils.iterateFiles(new File("src/test/resources/torrents"), new String[] {"torrent"}, false);
		while (iter.hasNext()) {
			// reading and decoding from a torrent file.
			File inFile = iter.next();
			System.out.println("Testing decode - encode for: " + inFile.getAbsolutePath());
			BufferedInputStream inFileBufStream = new BufferedInputStream(new FileInputStream(inFile));
			BEDictionary dict1 = BEncoding.decodeBEDictionary(inFileBufStream);
			System.out.println(dict1.get("announce").getType());
			inFileBufStream.close();
			
			// enconding onto an output stream
			File outFile = folder.newFile();
			FileOutputStream outStream = new FileOutputStream(outFile);
			outStream.write(dict1.encode());
			outStream.close();
			
			// read back
			BufferedInputStream inFileBufStream2 = new BufferedInputStream(new FileInputStream(outFile));
			BEDictionary dict2 = BEncoding.decodeBEDictionary(inFileBufStream2);
			inFileBufStream2.close();
			
			Assert.assertEquals(dict1, dict2);
		}
	}

	@Test
	public void testDecodeEncodeCycle2() throws DecodingException, IOException {
		Iterator<File> iter = FileUtils.iterateFiles(new File("src/test/resources/torrents"), new String[] {"torrent"}, false);
		while (iter.hasNext()) {
			
			File inFile = iter.next();
			byte[] bytesRead = FileUtils.readFileToByteArray(inFile);

			// reading and decoding from a torrent file.
			System.out.println("Testing decode - encode for: " + inFile.getAbsolutePath());
			BufferedInputStream inFileBufStream = new BufferedInputStream(new FileInputStream(inFile));
			BEDictionary dict1 = BEncoding.decodeBEDictionary(inFileBufStream);
			inFileBufStream.close();
			
			byte[] bytesEncodedBack = dict1.encode();
			Assert.assertArrayEquals(bytesRead, bytesEncodedBack);
		}
	}
	
	@Test
	public void testInfoHashLength() throws IOException, DecodingException {
		Iterator<File> iter = FileUtils.iterateFiles(new File("src/test/resources/torrents"), new String[] {"torrent"}, false);
		while (iter.hasNext()) {
			File inFile = iter.next();
			MetaInfoFile torrent = MetaInfoFile.fromFile(inFile);
			byte[] bytes = ByteUtils.computeSHA1(torrent.getInfo().asDict().encode());
			Assert.assertEquals(bytes.length, 20);
		}
	}
}
