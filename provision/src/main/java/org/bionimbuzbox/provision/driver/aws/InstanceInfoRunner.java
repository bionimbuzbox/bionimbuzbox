package org.bionimbuzbox.provision.driver.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Reservation;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class InstanceInfoRunner implements Runnable {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(InstanceInfoRunner.class);
  
  private AmazonEC2 client;
  private Instance instance;
  
  private int ATTEMPTS = 30;
  private static final int INTERVAL = 3000;

  public InstanceInfoRunner(AmazonEC2 client, Instance instance) {
    this.client = client;
    this.instance = instance;
  }
  
  @Override
  public void run() {
    InstanceStateName desiredStatus = InstanceStateName.Running;
    InstanceStateName currentStatus = InstanceStateName.Pending;
    
    try {
      do {
        ATTEMPTS--;
        
        final DescribeInstancesRequest request = new DescribeInstancesRequest()
            .withInstanceIds(instance.getInstanceId());
        
        final DescribeInstancesResult result = this.client.describeInstances(request);

        for (Reservation reservation : result.getReservations()) {
          for (Instance instance : reservation.getInstances()) {
            currentStatus = InstanceStateName.fromValue(instance.getState().getName());
            
            LOGGER.info("Instance {}, Status: {}, Attempts Left: {}", 
                this.instance.getInstanceId(),
                currentStatus.name(),
                ATTEMPTS);
            
            if (desiredStatus.equals(currentStatus)) {
              BeanUtils.copyProperties(this.instance, instance);
              ATTEMPTS = 0;
              break;
            } 
          }
        }
        Thread.sleep(INTERVAL);
      } while (ATTEMPTS > 0 );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
