package org.bionimbuzbox.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Cluster extends GenericModel implements Model {

	private static final long serialVersionUID = -5816426027690174558L;
	
	private String name;
	private String type;

	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
	@JsonIdentityReference(alwaysAsId=true)
	private Datacenter datacenter;
	
  @JsonBackReference(value="servers")
	private List<Server> servers = new ArrayList<Server>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Datacenter getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(Datacenter datacenter) {
		this.datacenter = datacenter;
	}
	
	@JsonProperty("datacenter")
	private void setDatacenter(String id) {
		this.datacenter = new Datacenter();
		this.datacenter.setId(id);
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}
	
	public void addServer(Server server) {
	  this.servers.add(server);
	}
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Cluster{ ");
		buffer.append( String.format("%s=%s, ", "ID", this.getId()) );
		buffer.append( String.format("%s=%s, ", "Name", this.name) );
		buffer.append( String.format("%s=%s, ", "Meta", this.getMeta()) );
		buffer.append( String.format("%s=%s", "Servers", this.getServers()) );
		buffer.append(" }");
		
		return buffer.toString();
	}
	
}
