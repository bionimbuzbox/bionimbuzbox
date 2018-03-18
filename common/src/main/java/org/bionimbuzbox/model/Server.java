package org.bionimbuzbox.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import org.bionimbuzbox.model.GenericModel;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Server extends GenericModel implements Serializable {

	private static final long serialVersionUID = -8307460091358296518L;
	
	private String name;
	
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
	@JsonIdentityReference(alwaysAsId=true)
	private Cluster cluster;
	
	private Key key;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	@JsonProperty("cluster")
	public void setCluster(String id) {
		this.cluster = new Cluster();
		this.cluster.setId(id);
	}
	
	public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  @Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Server{ ");
		buffer.append( String.format("%s=%s, ", "ID", this.getId()) );
		buffer.append( String.format("%s=%s, ", "Name", this.name) );
		buffer.append( String.format("%s=%s", "Meta", this.getMeta()) );
		buffer.append(" }");
		
		return buffer.toString();
	}
	
}
