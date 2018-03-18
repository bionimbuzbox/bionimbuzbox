package org.bionimbuzbox.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bionimbuzbox.helper.PropertiesHelper;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class APIServerDemo extends APIServer {
	
	private final Properties config;
	private final Properties httpConfig;
	private final Properties httpsConfig;
	
	public APIServerDemo() throws IOException {
		InputStream inStream = APIServerDemo.class.getResourceAsStream("/config.properties");
		config = new Properties();
		config.load(inStream);
		httpConfig = PropertiesHelper.matchingSubset(config, "common.server.connector.http", false);
		httpsConfig = PropertiesHelper.matchingSubset(config, "common.server.connector.https", false);
	}
	
	public Connector[] getConnectors(Server server) {
		List<Connector> connectors = new ArrayList<>();
		
		if ( httpsConfig.getProperty("enabled", "false").equals("true") ) {
			connectors.add(this.getHTTPSConnector(server, httpsConfig));
		}
		
		connectors.add(this.getHTTPConnector(server, httpConfig));
		
		return connectors.toArray(new Connector[connectors.size()]);
	}
	
	
 	public HandlerList getHandlerList() {

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
		resourceHandler.setResourceBase("web/");
		
		HandlerList handlers = new HandlerList();
		handlers.addHandler(resourceHandler);
		
		return handlers;
	}
}
