package org.bionimbuzbox.test;

import java.util.logging.Logger;

import org.bionimbuzbox.model.Datacenter;
import org.bionimbuzbox.model.Provider;

public class JSONTest {
	private static final Logger LOGGER = Logger.getLogger(JSONTest.class.getName());
	
	public static void main(String[] args) {
		Provider provider = new Provider();
		provider.setName("Provider Test");
		LOGGER.info(provider.toJSONString());
		
		Datacenter datacenter = new Datacenter();
		datacenter.setName("Datacenter Test");
		datacenter.setProvider(provider);
		LOGGER.info(datacenter.toJSONString());
	}
}
