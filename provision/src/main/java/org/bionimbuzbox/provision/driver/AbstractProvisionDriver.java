package org.bionimbuzbox.provision.driver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bionimbuzbox.model.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProvisionDriver implements ProvisionDriver {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProvisionDriver.class);
  private static final ConcurrentMap<String, ProvisionDriver> initializedDrivers = new ConcurrentHashMap<>();
  
  static ProvisionDriver initializeDriver(Provider provider) throws Exception {
    return initializeDriver(provider, false);
  }
  
  static ProvisionDriver initializeDriver(Provider provider, boolean recreate) throws Exception {
    try {
      ProvisionDriver driver;
      
      if (!recreate && initializedDrivers.containsKey(provider.getId())) {
        driver = initializedDrivers.get(provider.getId());
        if (driver.isValid()) {
          LOGGER.info("Returning provision driver {}-{} for {} from cache.", driver.getDriverName(), driver.hashCode(), provider.getId() );
          return driver;
        }
      }
      
      String driverName = provider.getType();
      String packageName = ProvisionDriver.class.getPackage().getName() + "." + driverName;
      String className = ProvisionDriver.class.getSimpleName() + "Impl";
      
      driver = ProvisionDriver.class.cast(
          Class.forName(packageName + "." + className).newInstance());
      driver.configure(provider);
      
      LOGGER.info("Creating driver {}-{} for {}", driver.getDriverName(), driver.hashCode(), provider.getId() );
      initializedDrivers.put(provider.getId(), driver);
      LOGGER.info("Initialized provision drivers: {}", initializedDrivers);
      
      return driver;
      
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new Exception(ex.getMessage());
    }
  }

}
