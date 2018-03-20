package org.bionimbuzbox.server;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.bionimbuzbox.helper.PropertiesHelper;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIServer extends APIServer {
	
  private static final Logger log = LoggerFactory.getLogger(UIServer.class);
  
  private static final Properties defaults = new Properties();
  private final Properties config = new Properties();
	private final Properties httpConfig;
	private final Properties httpsConfig;
	
	static {
    try {
      defaults.load(UIServer.class.getResourceAsStream("/web-ui.defaults"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
	
	public UIServer() throws IOException {
		this(defaults);
	}
	
	public UIServer(Properties c) throws IOException {
	  this.config.putAll(defaults);
	  this.config.putAll(c);
    
    httpConfig = PropertiesHelper.matchingSubset(
        config, "web-ui.server.connector.http", false);
    
    httpsConfig = PropertiesHelper.matchingSubset(
        config, "web-ui.server.connector.https", false);
  }
	
	@Override
	public HandlerList getHandlerList() {
		
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
		try {
		  resourceHandler.setBaseResource(Resource.newResource("./web"));		
		} catch (Exception e) {
      log.error(e.getMessage(), e);
    }
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler });
		
		return handlers;
	}

	@Override
	public Connector[] getConnectors(Server server) {
		List<Connector> connectors = new ArrayList<>();
		
		if ( httpsConfig.getProperty("enabled", "false").equals("true") ) {
			connectors.add(this.getHTTPSConnector(server, httpsConfig));
		}
		
		connectors.add(this.getHTTPConnector(server, httpConfig));
		
		return connectors.toArray(new Connector[connectors.size()]);
	}
	
	public static void main(String[] args) throws Exception {
		new UIServer(loadConfigFromCommandLine(args)).runServer();
	}
}
