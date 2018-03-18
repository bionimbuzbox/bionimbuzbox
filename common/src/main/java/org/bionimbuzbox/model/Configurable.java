package org.bionimbuzbox.model;

import java.util.Map;
import java.util.Properties;

public interface Configurable { 
	public void configure(Properties configs);
	
	public void addMetaInfo(String key, String value);
	public String getMetaInfo(String key);
	public void removeMetaInfo(String key);
	public Map<String, String> getMeta();
}
