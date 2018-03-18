package org.bionimbuzbox.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bionimbuzbox.model.GenericModel;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Key extends GenericModel {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
