package org.bionimbuzbox.communication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.elasticsearch.ElasticSearchDriver;
import org.bionimbuzbox.helper.PropertiesHelper;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class CommunicationService {
	
	public void runServer() {
		Server server = new Server();

        server.setConnectors(this.getConnectors(server));
		server.setHandler(this.getHandlers());

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.destroy();
		}
	}
	
	public Connector getHTTPConnector(Server server) {
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecureScheme("https");
		httpConfig.setSecurePort(9443);
		httpConfig.setOutputBufferSize(32768);

		ServerConnector http = new ServerConnector(server,
				new HttpConnectionFactory(httpConfig));
		http.setPort(9080);
		http.setIdleTimeout(30000);
		
		return http;
	}
	
	public Connector getHTTPSConnector(Server server) {
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(new File("ssl/keystore").getAbsolutePath());
		sslContextFactory.setKeyStorePassword("OBF:1vn21ugu1saj1v9i1v941sar1ugw1vo0");
		//sslContextFactory.setKeyManagerPassword("OBF:1vn21ugu1saj1v9i1v941sar1ugw1vo0");
		sslContextFactory.setNeedClientAuth(true);
		sslContextFactory.setTrustStorePath(new File("ssl/truststore").getAbsolutePath());
		sslContextFactory.setTrustStorePassword("OBF:1vn21ugu1saj1v9i1v941sar1ugw1vo0");

		HttpConfiguration httpsConfig = new HttpConfiguration();
		SecureRequestCustomizer src = new SecureRequestCustomizer();
		httpsConfig.addCustomizer(src);

		ServerConnector https = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
				new HttpConnectionFactory(httpsConfig));
		https.setPort(9443);
		https.setIdleTimeout(500000);
		
		return https;
	}
	
	public Connector[] getConnectors(Server server) {
		return new Connector[] { this.getHTTPConnector(server), this.getHTTPSConnector(server) };
	}
	
	public ResourceConfig getResourceConfig() {
		ResourceConfig resourceConfig = new ResourceConfig();
		
		resourceConfig.register(new AbstractBinder() {
			@Override
			public void configure() {
				Properties config = new Properties();
				config.setProperty("cluster.name", "elasticsearch_tiago");
				config.setProperty("transport.host", "127.0.0.1");
				DAODriver driver = new ElasticSearchDriver();
				driver.configure(config);
				driver.initialize();
				
				bind( DAOFactory.create(driver)).to(DAOFactory.class);
			}
		});
		
		resourceConfig.packages("org.bionimbuzbox.communication.api");
		
		return resourceConfig;
	}
	
 	public HandlerList getHandlers() {
		ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(this.getResourceConfig()));

		ServletContextHandler servletContextHandler = new ServletContextHandler();
		servletContextHandler.setContextPath("/api/v1");
		servletContextHandler.addServlet(jerseyServlet, "/*");

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
		resourceHandler.setResourceBase("web/");
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, servletContextHandler });
		
		return handlers;
	}
	
	public void loadProperties() throws IOException {
		InputStream inputStream = CommunicationService.class.getResourceAsStream("/config.properties");
		
		Properties config = new Properties();
		config.load(inputStream);
		
		Properties apiProperties = PropertiesHelper.matchingSubset(config, "communication.api.", false);
		Properties daoProperties = PropertiesHelper.matchingSubset(config, "communication.dao.driver." + config.getProperty("communication.dao.driver"), false);
		
		System.out.println(config);
		System.out.println(apiProperties);
		System.out.println(daoProperties);
	}
	
	public static void main(String[] args) throws Exception {
		CommunicationService service = new CommunicationService();
		service.loadProperties();
		service.runServer();
	}
}
