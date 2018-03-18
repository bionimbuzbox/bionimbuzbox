package org.bionimbuzbox.helper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.logging.Logger;

public class JSONHelper {

  private static final Logger LOGGER = Logger.getLogger(JSONHelper.class.getName());
  
  private JSONHelper() {
    // TODO Auto-generated constructor stub
  }
  
  static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper;
  }
  
  public static String toJSON(Object obj) {
    try {
      return createObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }
  
  public static <T> T fromJSON(String json, Class<T> type) {
    try {
      return createObjectMapper().readValue(json, type);
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }
  
  public static <T> T fromJSON(String json, TypeReference<T> type) {
    try {
      return createObjectMapper().readValue(json, type);
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

}
