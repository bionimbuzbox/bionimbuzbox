package org.bionimbuzbox.provision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import javax.servlet.DispatcherType;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.elasticsearch.ElasticSearchDriver;
import org.bionimbuzbox.helper.PropertiesHelper;
import org.bionimbuzbox.server.APIServer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisionService extends APIServer {
	
	private static final Logger log = LoggerFactory.getLogger(ProvisionService.class);
	
	private static final Properties defaults = new Properties();
  private final Properties config = new Properties();
	private Properties daoDriverConfig;
	private Properties httpConfig;
	private Properties httpsConfig;
	
	static {
    try {
      defaults.load(ProvisionService.class.getResourceAsStream("/provision.defaults"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
	
	public ProvisionService() throws IOException{
	  this(defaults);
	}
	
	public ProvisionService(Properties c) {
	  config.putAll(defaults);
    config.putAll(c);

		httpConfig = PropertiesHelper.matchingSubset(
		    config, "provision.server.connector.http", false);
		
    httpsConfig = PropertiesHelper.matchingSubset(
        config, "provision.server.connector.https", false);
    
    daoDriverConfig = PropertiesHelper.matchingSubset(
        config, 
        "provision.dao.driver." + config.getProperty("provision.dao.driver", "elasticsearch"), 
        false
    );
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
	
	@Override
	public HandlerList getHandlerList() {
		ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(this.getResourceConfig()));

		FilterHolder holder = new FilterHolder(CrossOriginFilter.class);
		holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		holder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,DELETE,PUT");
		holder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
		holder.setName("cross-origin");
		
		ServletContextHandler servletContextHandler = new ServletContextHandler();
		servletContextHandler.setContextPath("/api/v1");
		servletContextHandler.addServlet(jerseyServlet, "/*");
		servletContextHandler.addFilter(holder, "*", EnumSet.of(DispatcherType.REQUEST));
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { servletContextHandler });
		
		return handlers;
	}
	
	public ResourceConfig getResourceConfig() {
		ResourceConfig resourceConfig = new ResourceConfig();
		
		resourceConfig.register(new AbstractBinder() {
			@Override
			public void configure() {	
				//TODO: instantiate driver from config.properties
				DAODriver driver = new ElasticSearchDriver();
				driver.configure(daoDriverConfig);
				driver.initialize();
				
				DAOFactory factory = DAOFactory.create(driver);
				
				bind(factory).to(DAOFactory.class);
			}
		});
		
		resourceConfig.packages("org.bionimbuzbox.provision.api");
		resourceConfig.register(JacksonFeature.class); 
		
		return resourceConfig;
	}
	
	public static void main(String[] args) {
    new ProvisionService(loadConfigFromCommandLine(args)).runServer();
  }
}

