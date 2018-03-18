package org.bionimbuzbox.orchestration.driver;

import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.model.Server;

public interface OrchestrationDriver {
	
  public void join(Server server);
	public void build() throws OrchestrationDriverException;
	
	public Resource start(Resource resource);
	public Job start(Job job);
	
	public boolean clean(Resource resource);
  public boolean clean(Job job);
	
	public Resource.Status status(Resource resource);
	public Job.Status status(Job job);
	
	public String logs(String id);
	
	public Cluster cluster();
	
	public void installBioNimbuzBox();
	
	public boolean isValid();
	
	public static OrchestrationDriver initializeDriver(Cluster cluster) {
    return AbstractOrchestrationDriver.initializeDriver(cluster, false);
  }
	
	public static OrchestrationDriver initializeDriver(Cluster cluster, boolean recreate) {
    return AbstractOrchestrationDriver.initializeDriver(cluster, recreate);
  }
}
