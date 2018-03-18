package org.bionimbuzbox.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bionimbuzbox.model.GenericModel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Provider extends GenericModel implements Serializable {

	private static final long serialVersionUID = 1381410180577766351L;
	
	private String name;
	
	private String type;
		
	@JsonBackReference(value="datacenters")
	//@JsonIgnore
	private List<Datacenter> datacenters = new ArrayList<Datacenter>();
	
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

	public List<Datacenter> getDatacenters() {
		return datacenters;
	}

	public void setDatacenters(List<Datacenter> datacenters) {
		this.datacenters = datacenters;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Provider{ ");
		buffer.append( String.format("%s=%s, ", "ID", this.getId()) );
		buffer.append( String.format("%s=%s, ", "Name", this.name) );
		buffer.append( String.format("%s=%s, ", "Type", this.type) );
		buffer.append( String.format("%s=%s, ", "Meta", this.getMeta()) );
		buffer.append( String.format("%s=%s", "Datacenters", this.getDatacenters()) );
		buffer.append(" }");
		
		return buffer.toString();
	}

	
}
