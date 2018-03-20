package org.bionimbuzbox.provision.driver.test;

import org.bionimbuzbox.model.Provider;
import org.bionimbuzbox.provision.driver.ProvisionDriver;
import org.bionimbuzbox.provision.driver.mock.ProvisionDriverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrencyTest implements Runnable {

  private final ProvisionDriver driver;
  private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrencyTest.class);
  private final long interval;
  
  public ConcurrencyTest(ProvisionDriver driver, long interval) {
    this.driver = driver;
    this.interval = interval;
  }

  @Override
  public void run() {
    LOGGER.info("Before Sleep: Thread {} Driver HASH {}", hashCode(), driver.hashCode());
    try {
      Thread.sleep(interval);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    LOGGER.info("After Sleep: Thread {} Driver HASH {}", hashCode(), driver.hashCode());
  }
  
  public static void main(String[] args) throws Exception {
    test2();
  }
  
  public static void test1() throws Exception {
    
    Provider p1 = new Provider();
    p1.setId("p1");
    p1.setType("mock");
    
    Provider p2 = new Provider();
    p2.setId("p1");
    p2.setType("mock");
    
    ProvisionDriver d1 = ProvisionDriver.initializeDriver(p1);
    ProvisionDriver d2 = ProvisionDriver.initializeDriver(p2);
    
    
    
    if (d1.equals(d2)) {
      LOGGER.info("d1 == d2");
      
    }
  }
  
  public static void test2() throws Exception {
    ProvisionDriverImpl mock1 = new ProvisionDriverImpl();
    ProvisionDriverImpl mock2 = new ProvisionDriverImpl();
    
    LOGGER.info("mock1 hash: {}", mock1.hashCode());
    LOGGER.info("mock2 hash: {}", mock2.hashCode());
    LOGGER.info("mock1 == mock2 ? {}", mock1 == mock2);
    
    Provider p1 = new Provider();
    p1.setId("p1");
    p1.setType("mock");
    
    ProvisionDriver d1 = ProvisionDriver.initializeDriver(p1);
    
    new Thread(new ConcurrencyTest(d1, 5000)).start();
    Thread.sleep(1000);
    
    Provider p2 = new Provider();
    p2.setId("p1");
    p2.setType("mock");
    
    ProvisionDriver d2 = ProvisionDriver.initializeDriver(p2);
    Thread.sleep(1000);
    new Thread(new ConcurrencyTest(d2, 5000)).start();
    
    Thread.sleep(5000);
    
    new Thread(new ConcurrencyTest(d2, 5000)).start();
    
  }

}
