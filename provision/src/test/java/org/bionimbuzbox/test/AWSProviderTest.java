package org.bionimbuzbox.test;

import java.io.InputStream;
import java.util.Properties;

import org.bionimbuzbox.provision.driver.aws.ProvisionDriverImpl;

public class AWSProviderTest {
	public static void main(String[] args) throws Exception {
		InputStream inStream = AWSProviderTest.class.getResourceAsStream("/config.properties");
		Properties config = new Properties();
		config.load(inStream);
		System.out.println(config);
		
		//AWSProvisionDriver driver = new AWSProvisionDriver();
		//driver.configure(config);
		//driver.createServers(null);
	}
}
