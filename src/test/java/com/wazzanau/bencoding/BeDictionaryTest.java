package com.wazzanau.bencoding;

import dev.dimlight.terdoppio.api.Be;
import dev.dimlight.terdoppio.api.BeDecoder;
import dev.dimlight.terdoppio.api.BeDecodingException;
import dev.dimlight.terdoppio.api.BeDictionary;
import dev.dimlight.terdoppio.api.BeEncoder;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;

public class BeDictionaryTest {

	private static final Logger log = LoggerFactory.getLogger(BeDictionaryTest.class);

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testCodecRoundtrip1() throws IOException {

		Iterator<File> iter = FileUtils.iterateFiles(new File("src/test/resources/torrents"), new String[] {"torrent"}, false);
		while (iter.hasNext()) {
			// reading and decoding from a torrent file.
			File inFile = iter.next();
			System.out.println("Testing decode - encode for: " + inFile.getAbsolutePath());

			final BeDictionary dict1;
			try (final FileInputStream fis = new FileInputStream(inFile);
				 final BufferedInputStream bis = new BufferedInputStream(fis);
				 final BeDecoder decoder = Be.newDecoder(bis)) {

				dict1 = decoder.decodeDictionary();
				System.out.println(dict1.get("announce").orElseThrow().getValueType());

			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}

			// enconding onto an output stream
			File outFile = folder.newFile();

			try (final FileOutputStream fos = new FileOutputStream(outFile);
				 final BeEncoder encoder = Be.newEncoder(fos)) {

				encoder.encode(dict1);
			}

			// read back
			final BeDictionary dict2;
			try (final FileInputStream fis = new FileInputStream(outFile);
				 final BufferedInputStream bis = new BufferedInputStream(fis);
				 final BeDecoder decoder = Be.newDecoder(bis)) {

				dict2 = decoder.decodeDictionary();
				System.out.println(dict2.get("announce").orElseThrow().getValueType());

			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}

			Assert.assertEquals(dict1, dict2);
		}
	}

	@Test
	public void testDecodeEncodeCycle2() throws BeDecodingException, IOException {
		Iterator<File> iter = FileUtils.iterateFiles(new File("src/test/resources/torrents"), new String[] {"torrent"}, false);
		while (iter.hasNext()) {

			final File inFile = iter.next();
			log.info("Testing file [{}]", inFile);
			final byte[] originalEncodedContent = FileUtils.readFileToByteArray(inFile);


			try (final ByteArrayInputStream bais = new ByteArrayInputStream(originalEncodedContent);
				 final BeDecoder decoder = Be.newDecoder(bais);
				 final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 final BeEncoder encoder = Be.newEncoder(baos)) {

				final BeDictionary dict = decoder.decodeDictionary();

				System.out.println(dict.toString());

				encoder.encode(dict);
				final byte[] reEncodedContent = baos.toByteArray();

				Assert.assertArrayEquals(originalEncodedContent, reEncodedContent);
			}
		}
	}
}
