package org.bionimbuzbox.test;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;
import org.bionimbuzbox.dao.ClusterDAO;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.DatacenterDAO;
import org.bionimbuzbox.dao.ProviderDAO;
import org.bionimbuzbox.dao.ServerDAO;
import org.bionimbuzbox.dao.elasticsearch.ElasticSearchDriver;
import org.bionimbuzbox.helper.PropertiesHelper;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Datacenter;
import org.bionimbuzbox.model.Provider;
import org.bionimbuzbox.model.Server;

public class ProvisionDAOFactoryTest {
	private static final Logger LOGGER = Logger.getLogger(ProvisionDAOFactoryTest.class.getName());
	Properties driverConfig;
	ElasticSearchDriver driver;
	DAOFactory factory;

	ProviderDAO providerDAO;
	DatacenterDAO datacenterDAO;
	ClusterDAO clusterDAO;
	ServerDAO serverDAO;

	public static void main(String[] args) throws Exception {
		ProvisionDAOFactoryTest test = new ProvisionDAOFactoryTest();
		test.initialize();
		test.save();
		//test.getAll();
		test.destroy();
	}

	public void initialize() throws Exception {
		Properties config = new Properties();
		config.load(ProvisionDAOFactoryTest.class.getResourceAsStream("/config.properties"));
		this.driverConfig = PropertiesHelper.matchingSubset(config, "provision.dao.driver.elasticsearch.", false);

		this.driver = new ElasticSearchDriver(new Properties());
		this.driver.initialize();

		this.factory = DAOFactory.create(driver);

		this.providerDAO = factory.getProviderDAO();
		this.datacenterDAO = factory.getDatacenterDAO();
		this.clusterDAO = factory.getClusterDAO();
		this.serverDAO = factory.getServerDAO();
	}

	public void destroy() throws Exception {
		this.driver.destroy();
	}

	private String getRandom(String ... list) {
		Random r = new Random();
		return list[r.nextInt(list.length)];
	}

	public void save() throws Exception {
		for(int p=1; p<=3; p++) {
			Provider provider = new Provider();
			provider.setType(this.getRandom("digitalocean","gce","ssh","aws"));
			provider.setName(provider.getType() + p);
			providerDAO.save(provider);
			LOGGER.info(provider.toString());

			for(int d=1; d<=3; d++) {
				Datacenter datacenter = new Datacenter();
				datacenter.setName( String.format("datacenter-%d.%s", d, provider.getName()) );
				datacenter.setProvider(provider);
				datacenterDAO.save(datacenter);
				LOGGER.info(datacenter.toString());

				for(int c=1; c<=3; c++) {
					Cluster cluster = new Cluster();
					cluster.setName(String.format("cluster-%d.%s", c, datacenter.getName()));
					cluster.setType("swarm");
					cluster.setDatacenter(datacenter);
					clusterDAO.save(cluster);
					LOGGER.info(cluster.toString());
/*
					for(int s=1; s<=5; s++) {
						Server server = new Server();
						server.setName("Server " + s);
						server.setCluster(cluster);
						serverDAO.save(server);
						LOGGER.info(server.toString());
					}
*/
				}

			}
		}
	}

	public void getAll() throws Exception {
		int count;

		List<Provider> providers = providerDAO.getAll();
		count = 0;
		for(Provider provider : providers) {
			count++;
			LOGGER.info(provider.toString());
			LOGGER.info("get(id)" + providerDAO.get(provider.getId()).toString());
		}
		Thread.sleep(5000);

		LOGGER.info("providers: " + count);

		List<Datacenter> datacenters = datacenterDAO.getAll();
		count = 0;
		for(Datacenter datacenter : datacenters) {
			count++;
			//LOGGER.info(datacenter.toString());
			//LOGGER.info("get(id)" + datacenterDAO.get(datacenter.getId()).toString());
		}
		Thread.sleep(5000);

		LOGGER.info("datacenters: " + count);

		List<Cluster> clusters = clusterDAO.getAll();
		count = 0;
		for(Cluster cluster : clusters) {
			count++;
			//LOGGER.info(cluster.toString());
			//LOGGER.info("get(id)" + clusterDAO.get(cluster.getId()).toString());
			LOGGER.info(cluster.toJSONString());
		}
		Thread.sleep(5000);

		LOGGER.info("clusters: " + count);

		List<Server> servers = serverDAO.getAll();
		count = 0;
		for(Server server : servers) {
			count++;
			//LOGGER.info(server.toString());
			//LOGGER.info("get(id)" + serverDAO.get(server.getId()).toString());
		}
		Thread.sleep(5000);

		LOGGER.info("servers: " + count);
	}
}
