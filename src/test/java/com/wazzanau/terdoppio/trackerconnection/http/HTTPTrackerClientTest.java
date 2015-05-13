package com.wazzanau.terdoppio.trackerconnection.http;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wazzanau.terdoppio.bencode.DecodingException;
import com.wazzanau.terdoppio.metainfo.MetaInfoFile;
import com.wazzanau.terdoppio.trackerconnection.AnnounceProtocol;
import com.wazzanau.terdoppio.trackerconnection.AnnounceResponse;
import com.wazzanau.terdoppio.trackerconnection.PeerIdGenerator;
import com.wazzanau.terdoppio.trackerconnection.TrackerClient;
import com.wazzanau.terdoppio.trackerconnection.TrackerEventListener;
import com.wazzanau.terdoppio.trackerconnection.udp.ScrapeResponse;

public class HTTPTrackerClientTest {
	
	private static final Logger logger = LoggerFactory.getLogger(HTTPTrackerClientTest.class);

	@Test
	public void test() throws DecodingException, IOException, URISyntaxException, InterruptedException {
		MetaInfoFile torrentFile = MetaInfoFile.fromFile(new File("src/test/resources/torrents/Linux%20Mint%208%20Helena.torrent"));
		System.out.println(torrentFile);

		if (torrentFile.getAnnounceProtocol() != AnnounceProtocol.HTTP) {
			Assert.fail("Tracker is not HTTP");
		}

		// creating the tracker client
		URI url = new URI(torrentFile.getAnnounce());
		String peerId = PeerIdGenerator.generateReadablePeerIdString();
		byte[] ip = Inet4Address.getLocalHost().getAddress();
		int port = 3000;
		TrackerClient client = new HTTPTrackerClient(url, peerId, torrentFile.getInfoHash(), ip, port);
		
		client.addTrackerConnectionListener(new TrackerEventListener() {
			
			@Override
			public void onScrapeResponse(ScrapeResponse response) {
				// we don't scrape
			}
			
			@Override
			public void onException(Exception e) {
				logger.info("Got exception");
			}
			
			@Override
			public void onAnnounceResponse(AnnounceResponse response) {
				logger.info("got announce reponse");
				logger.info(response.toString());
			}
		});

		client.start();
		
		Thread.sleep(1000);
		client.stop();
	}

}
