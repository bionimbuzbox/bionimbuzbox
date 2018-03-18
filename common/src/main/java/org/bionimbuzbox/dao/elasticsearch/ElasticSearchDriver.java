package org.bionimbuzbox.dao.elasticsearch;

import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Logger;

import org.bionimbuzbox.dao.DAODriver;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ElasticSearchDriver implements DAODriver {
	
	private static final Logger LOGGER = Logger.getLogger(ElasticSearchDriver.class.getName());
	private static final String DRIVER_NAME = "elasticsearch";
	
	private Settings settings;
	private TransportClient transportClient;
	
	
	public ElasticSearchDriver() {
	} 
	
	public ElasticSearchDriver(Properties properties) {
		this.configure(properties);
	} 
	
	public String getDriverName() {
		return DRIVER_NAME;
	}
	
	public ElasticSearchDriver configure(Properties properties) {
		LOGGER.info("Configuring ElasticSearchDriver ...");
		this.settings = Settings.builder()
			.put(properties)
			.build();
		return this;
	}

	@Override
	public DAODriver initialize() {
		LOGGER.info("Initializig ElasticSearchDriver ...");
		try {
			TransportAddress transportAddress = new InetSocketTransportAddress(
					InetAddress.getByName(settings.get("transport.host", "127.0.0.1")), settings.getAsInt("transport.port", 9300));
			
			LOGGER.info("Creating new PreBuiltTransportClient ..");
			this.transportClient = new PreBuiltTransportClient(settings)
					.addTransportAddress(transportAddress);

		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return this;
	}
	
	public TransportClient getTransportClient() {
		return this.transportClient;
	}


	@Override
	public void destroy() {
		LOGGER.info("Destroying ElasticSearchCommunicationDriver ...");
		this.transportClient.close();
	}
}
