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
public class Datacenter extends GenericModel implements Model {

	private static final long serialVersionUID = -5816426027690174558L;

	private String name;	

	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
	@JsonIdentityReference(alwaysAsId=true)
	private Provider provider;
	
    @JsonBackReference(value="clusters")
	private List<Cluster> clusters = new ArrayList<Cluster>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	@JsonProperty("provider")
    public void setProvider(String id) {
		this.provider = new Provider();
		this.provider.setId(id);
    }

	public List<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}

	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Datacenter{ ");
		buffer.append( String.format("%s=%s, ", "ID", this.getId()) );
		buffer.append( String.format("%s=%s, ", "Name", this.name) );
		buffer.append( String.format("%s=%s, ", "Meta", this.getMeta()) );
		buffer.append( String.format("%s=%s", "Clusters", this.getClusters()) );
		buffer.append(" }");
		
		return buffer.toString();
	}
	
}
