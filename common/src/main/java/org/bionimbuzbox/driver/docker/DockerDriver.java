package org.bionimbuzbox.driver.docker;

import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.RegistryAuth;

public class DockerDriver {
	
	private static final Logger LOGGER = Logger.getLogger(DockerDriver.class.getName());
	
	private DockerClient dockerClient;
	
	
	public DockerDriver() throws Exception {
		this.configure(new Properties());
	}
	
	public DockerDriver(String host, String certificatesPath) throws Exception {
		Properties config = new Properties();
		config.setProperty("host", host);
		config.setProperty("certificatesPath", certificatesPath);
		this.configure(config);
	}
	
	public DockerDriver(Properties config) throws Exception {
		this.configure(config);
	}
	
	private void configure(Properties config) throws Exception {
		DefaultDockerClient.Builder dockerClientBuilder = DefaultDockerClient.builder();
		RegistryAuth.Builder registryBuilder = RegistryAuth.builder();
		
		if (config.containsKey("registry.username")) {
			registryBuilder.email(config.getProperty("registry.email"));
			registryBuilder.username(config.getProperty("registry.username"));
			registryBuilder.password(config.getProperty("registry.password"));
		}
		
		if (config.containsKey("registry.server")) {
			registryBuilder.serverAddress(config.getProperty("registry.server"));
		}
		
		RegistryAuth registryAuth = registryBuilder.build();	
		
		dockerClientBuilder.registryAuth(registryAuth);
		dockerClientBuilder.uri(config.getProperty("host", "unix:///var/run/docker.sock"));	
		
		if (config.containsKey("certificatesPath")) {
			try {
				dockerClientBuilder.dockerCertificates(
						new DockerCertificates(
							Paths.get(config.getProperty("certificatesPath", System.getProperty("user.home") + "/.docker/"))
						)
				);
				
			} catch (DockerCertificateException ex) {
				ex.printStackTrace();
				throw new Exception(ex);
			}
		}
		
		this.dockerClient = dockerClientBuilder.build();
		
		LOGGER.info(this.dockerClient.info().name());
	}
	
	public void pullImage(String image) throws Exception {
		try {
			this.dockerClient.pull(image);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void createService() throws Exception {
		
	}
	
	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		config.setProperty("host", "http://core-02:2375");

		DockerDriver driver = new DockerDriver(config);
		driver.pullImage("alpine:latest");
		
		while(true) {
			LOGGER.info("Sleeping ...");
			Thread.sleep(10000);
		}
	}
}
