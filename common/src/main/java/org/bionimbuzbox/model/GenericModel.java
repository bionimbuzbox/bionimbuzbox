package org.bionimbuzbox.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class GenericModel implements Model {
	
  private static final long serialVersionUID = 3992090361968426215L;
  private static final Logger LOGGER = Logger.getLogger(GenericModel.class.getName());
	private ObjectMapper mapper = Model.createObjectMapper();
	
	private Map<String, String> meta = new ConcurrentHashMap<String, String>();
	
	//@JsonProperty(access = Access.READ_ONLY)
	private String id;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Map<String, String> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, String> meta) {
		this.meta.putAll(meta);;
	}
	
	public void addMetaInfo(String key, String value) {
		this.meta.put(key, value);
	}
	
	public String getMetaInfo(String key, String defaultValue) {
		return this.meta.containsKey(key) ? this.meta.get(key) : defaultValue;
	}
	
	public String getMetaInfo(String key) {
    return this.meta.containsKey(key) ? this.meta.get(key) : null;
  }
	
	public Map<String,String> filterMetaByKeyPrefix(String prefix) {
	  Map<String,String> filteredMeta = new HashMap<>();
	  for (String key : this.meta.keySet()) {
	    if (key.startsWith(prefix)) {
	      filteredMeta.put(key, this.meta.get(key));
	    }
	  }
	  return filteredMeta;
	}
	
	public Collection<String> filterMetaInfoByKeyPrefix(String prefix) {
    return filterMetaByKeyPrefix(prefix).values();
  }
	
	public void removeMetaInfo(String key) {
	  if (this.meta.containsKey(key)) {
	    this.meta.remove(key);
	  }
	}
	
	public byte[] toJSONBytes() {
		try {
			return this.mapper.writeValueAsBytes(this);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public String toJSONString() {
		return new String(this.toJSONBytes());
	}

  
	public void configure(Properties configs) {
	  
	}
    
}
