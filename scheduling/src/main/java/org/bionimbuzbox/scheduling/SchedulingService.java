package org.bionimbuzbox.scheduling;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.servlet.DispatcherType;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.elasticsearch.ElasticSearchDriver;
import org.bionimbuzbox.helper.PropertiesHelper;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Workflow;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;
import org.bionimbuzbox.scheduling.runner.WorkflowRunner;
import org.bionimbuzbox.scheduling.scheduler.AllTogetherScheduler;
import org.bionimbuzbox.scheduling.scheduler.RandomScheduler;
import org.bionimbuzbox.scheduling.scheduler.Scheduler;
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

public class SchedulingService extends APIServer {

  private static final Logger log = LoggerFactory.getLogger(SchedulingService.class);

  private static final List<OrchestrationDriver> drivers = new ArrayList<>();
  
  private static final Properties defaults = new Properties();
  private final Properties config = new Properties();
  private final Properties daoDriverConfig = new Properties();
  private final Properties httpConfig = new Properties();
  private final Properties httpsConfig = new Properties();

  private final ExecutorService workflowPool;

  static {
    try {
      defaults.load(SchedulingService.class.getResourceAsStream("/scheduling.defaults"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public SchedulingService() {
    this(defaults);
  }

  public SchedulingService(Properties c) {
    config.putAll(defaults);

    config.putAll(c);

    httpConfig.putAll(
        PropertiesHelper.matchingSubset(
            config, "scheduling.server.connector.http", false));
    httpsConfig.putAll(
        PropertiesHelper.matchingSubset(
            config, "scheduling.server.connector.https", false));

    daoDriverConfig.putAll(
        PropertiesHelper.matchingSubset(
            config, 
            "scheduling.dao.driver." + config.getProperty("scheduling.dao.driver", "elasticsearch"), false ));

    workflowPool = Executors.newFixedThreadPool(
        Integer.parseInt(config.getProperty("scheduling.pool.size")));
  }

  public void execute(Workflow workflow, List<Cluster> clusters) {
    Scheduler scheduler = buildScheduler(clusters);
    WorkflowRunner runner = new WorkflowRunner(workflow, scheduler);
    workflowPool.execute(runner);
  }

  private List<OrchestrationDriver> buildOrchestrationDriver(List<Cluster> clusters) {
    drivers.clear();
    for (Cluster cluster : clusters) {
      OrchestrationDriver driver = OrchestrationDriver.initializeDriver(cluster);
      drivers.add(driver);
    }
    return drivers;
  }

  private Scheduler buildScheduler(List<Cluster> clusters) {
    return new RandomScheduler(buildOrchestrationDriver(clusters));
    //return new AllTogetherScheduler(buildOrchestrationDriver(clusters));
  }


  @Override
  protected Connector[] getConnectors(Server server) {
    List<Connector> connectors = new ArrayList<>();

    if ( httpsConfig.getProperty("enabled", "false").equals("true") ) {
      connectors.add(this.getHTTPSConnector(server, httpsConfig));
    }

    connectors.add(this.getHTTPConnector(server, httpConfig));

    return connectors.toArray(new Connector[connectors.size()]);
  }

  @Override
  protected HandlerList getHandlerList() {
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

  protected ResourceConfig getResourceConfig() {
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
        bind(SchedulingService.this).to(SchedulingService.class);
      }
    });

    resourceConfig.packages("org.bionimbuzbox.scheduling.api");
    resourceConfig.register(JacksonFeature.class); 

    return resourceConfig;
  }

  @Override
  protected void onStopping() {
    if (workflowPool != null) {
      workflowPool.shutdown();
      try {
        workflowPool.awaitTermination(30, TimeUnit.SECONDS);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }	
    }
  }
  
  public static void main(String[] args) {
    new SchedulingService(loadConfigFromCommandLine(args)).runServer();
  }
}

