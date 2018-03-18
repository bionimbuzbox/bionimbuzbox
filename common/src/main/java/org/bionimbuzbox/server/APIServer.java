package org.bionimbuzbox.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class APIServer {

  private static final Logger log = LoggerFactory.getLogger(APIServer.class);

  protected abstract Connector[] getConnectors(Server server); 

  protected abstract HandlerList getHandlerList(); 

  protected Connector getHTTPConnector(Server server, Properties config) {
    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setSecureScheme(config.getProperty("secureScheme","https"));
    httpConfig.setSecurePort(Integer.parseInt(config.getProperty("securePort","443")));
    httpConfig.setOutputBufferSize(Integer.parseInt(config.getProperty("outputBufferSize","32768")));

    ServerConnector http = new ServerConnector(server,
        new HttpConnectionFactory(httpConfig));
    http.setPort(Integer.parseInt(config.getProperty("port","80")));
    http.setIdleTimeout(Long.parseLong(config.getProperty("idleTimeout","30000")));

    return http;
  }

  protected Connector getHTTPSConnector(Server server, Properties config) {
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
    https.setPort(Integer.parseInt(config.getProperty("port","443")));
    https.setIdleTimeout(Long.parseLong(config.getProperty("idle.timeout","500000")));

    return https;
  }

  protected void onFailure() {}
  protected void onStarting() {}
  protected void onStarted() {}
  protected void onStopping() {}
  protected void onStopped() {}


  public void runServer() {
    Server server = new Server();

    server.setConnectors(this.getConnectors(server));
    server.setHandler(this.getHandlerList());
    server.addLifeCycleListener(new APIServerListener(this));

    try {
      server.start();
      server.join();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      server.destroy();
    }
  }

  protected static Options getCommandLineOptions() {
    Options options = new Options();
    options.addOption("c", "config", true, "Config file");
    return options;
  }

  protected static Properties loadConfigFromCommandLine(String[] args) {
    Properties config = new Properties();
    CommandLineParser parser = new DefaultParser();

    try {
      InputStream inStream;
      // parse the command line arguments
      CommandLine line = parser.parse(getCommandLineOptions(), args);
      if (line.hasOption("config")) {
        log.info(new File(".").getAbsolutePath());
        inStream = new FileInputStream(line.getOptionValue("config"));
        config.load(inStream);
      }  
    } catch( ParseException exp ) {
      // oops, something went wrong
      System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
    } catch (IOException e) {
      log.warn(e.getMessage(), e);
    }
    return config;
  }

  protected static class APIServerListener implements LifeCycle.Listener {

    private final APIServer server;

    public APIServerListener(APIServer server) {
      this.server = server;
    }

    @Override
    public void lifeCycleFailure(LifeCycle arg0, Throwable arg1) {
      log.info("Calling onFailure ...");
      server.onFailure();
    }

    @Override
    public void lifeCycleStarted(LifeCycle arg0) {
      log.info("Calling onStarted ...");
      server.onStarted();
    }

    @Override
    public void lifeCycleStarting(LifeCycle arg0) {
      log.info("Calling onStarting ...");
      server.onStarting();
    }

    @Override
    public void lifeCycleStopped(LifeCycle arg0) {
      log.info("Calling onStopped ...");
      server.onStopped();
    }

    @Override
    public void lifeCycleStopping(LifeCycle arg0) {
      log.info("Calling onStopping ...");
      server.onStopping();
    }
  }
}
