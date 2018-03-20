package org.bionimbuzbox.provision.driver.digitalocean;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.common.DropletStatus;
import com.myjeeva.digitalocean.pojo.Droplet;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanUtils;
public class DropletInfoRunner implements Runnable {
  
  private static final Logger LOGGER = Logger.getLogger(DropletInfoRunner.class.getName());
  
  private DigitalOcean client;
  private Droplet droplet;
  
  private int ATTEMPTS = 30;
  private static final int INTERVAL = 3000;

  public DropletInfoRunner(DigitalOcean client, Droplet droplet) {
    this.client = client;
    this.droplet = droplet;
  }
  
  @Override
  public void run() {
    DropletStatus desiredStatus = DropletStatus.ACTIVE;
    DropletStatus currentStatus = null;
    
    try {
      do {
        ATTEMPTS--;
        
        Droplet dropletInfo = this.client.getDropletInfo(this.droplet.getId());
        currentStatus = dropletInfo.getStatus();
        
        LOGGER.info(
            String.format("Droplet %s, Status: %s, Attempts Left: %d", 
                dropletInfo.getName(),
                dropletInfo.getStatus().toString(),
                ATTEMPTS));
        
        if (desiredStatus.equals(currentStatus)) {
          BeanUtils.copyProperties(this.droplet, dropletInfo);
          break;
        }
        Thread.sleep(INTERVAL);
      } while (ATTEMPTS > 0 );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
