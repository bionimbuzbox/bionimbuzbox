package org.bionimbuzbox.orchestration.driver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOrchestrationDriver implements OrchestrationDriver {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOrchestrationDriver.class);
  private static final ConcurrentMap<String, OrchestrationDriver> initializedDrivers = new ConcurrentHashMap<>();

  private static boolean isClusterValid(Cluster cluster) {
    OrchestrationDriver driver = initializedDrivers.get(cluster.getId());
    boolean isValid = driver.isValid();

    if (cluster.getServers().size() != driver.cluster().getServers().size()) {
      LOGGER.warn("Cluster size was changed! New orchestration driver {}-{} for {}({}) will be created.", 
          cluster.getType(), driver.hashCode(), cluster.getName(), cluster.getId() );
      isValid = false;
    } else {
      for (Server driverServer : driver.cluster().getServers()) {
        boolean found = false;
        for (Server clusterServer : cluster.getServers()) {
          if (driverServer.getId()
              .equals(clusterServer.getId())) {
            break;
          }
        }

        if (!found) {
          LOGGER.warn("Cluster servers was changed! New orchestration driver {}-{} for {}({}) will be created.", 
              cluster.getType(), driver.hashCode(), cluster.getName(), cluster.getId() );
          isValid = false;
          break;
        }

      }
    }

    return isValid;
  }

  static OrchestrationDriver initializeDriver(Cluster cluster, boolean recreate) throws OrchestrationDriverException {
    try {
      OrchestrationDriver driver;

      if (!recreate && initializedDrivers.containsKey(cluster.getId())) {
        if (isClusterValid(cluster)) {
          driver = initializedDrivers.get(cluster.getId());
          LOGGER.info("Returning orchestration driver {}-{} for {}({}) from cache.", 
              cluster.getType(), driver.hashCode(), cluster.getName(), cluster.getId() );
          return driver;
        }
      }

      String driverName = cluster.getType();
      String packageName = OrchestrationDriver.class.getPackage().getName() + "." + driverName;
      String className = OrchestrationDriver.class.getSimpleName() + "Impl";

      driver = OrchestrationDriver.class.cast(
          Class.forName(packageName + "." + className)
          .getConstructor(Cluster.class)
          .newInstance(cluster));

      driver.build();

      LOGGER.info("Creating orchestration driver {}-{} for {}({})", 
          cluster.getType(), driver.hashCode(), cluster.getName(), cluster.getId() );
      initializedDrivers.put(cluster.getId(), driver);
      LOGGER.info("Initialized orchestration drivers: {}", initializedDrivers);

      return driver;

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new OrchestrationDriverException(e.getMessage());
    }
  }
}
