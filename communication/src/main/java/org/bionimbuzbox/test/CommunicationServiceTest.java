package org.bionimbuzbox.test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.EventHandlerDAO;
import org.bionimbuzbox.dao.elasticsearch.ElasticSearchDriver;
import org.bionimbuzbox.model.EventHandler;

public class CommunicationServiceTest {
	private static final Logger LOGGER = Logger.getLogger(CommunicationServiceTest.class.getName());
	
	public static void main(String[] args) throws Exception {
		CommunicationServiceTest.testJSONSerialization();
		
		Properties config = new Properties();
		config.setProperty("cluster.name", "elasticsearch_tiago");
		config.setProperty("transport.host", "127.0.0.1");
		
		DAODriver driver = new ElasticSearchDriver();
		driver.configure(config);
		driver.initialize();
		
		DAOFactory factory = DAOFactory.create(driver);

		CommunicationServiceTest.testElasticSearchGet(factory);
		CommunicationServiceTest.testElasticSearchIndex(factory);
		CommunicationServiceTest.testElasticSearchGet(factory);
		
		driver.destroy();
	}
	
	public static void testJSONSerialization() throws Exception {
		EventHandler eventHandler = new EventHandler("onStartUP", "http://www.google.com", 10);
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		String json = objectMapper.writeValueAsString(eventHandler);
		
		LOGGER.info(json);
	}
	
	public static void testElasticSearchGet(DAOFactory factory) throws Exception {
		EventHandlerDAO DAO = factory.getEventHandlerDAO();
		EventHandler eventHandler = DAO.get("AVp3csMEzIh2tkS2VraG");
		
		LOGGER.info("" + eventHandler);
	}
	
	public static void testElasticSearchIndex(DAOFactory factory) throws Exception {
		
		EventHandler eventHandler = new EventHandler("onStartUP", "http://uol.com.br");
		eventHandler.setId("AVp3csMEzIh2tkS2VraG");
		eventHandler.setCreationDate(new Date());
		
		EventHandlerDAO DAO = factory.getEventHandlerDAO();
		DAO.save(eventHandler);
		
		LOGGER.info(eventHandler.toString());
	
	}
}
