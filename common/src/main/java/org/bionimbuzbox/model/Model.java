package org.bionimbuzbox.model;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.Serializable;
import java.util.logging.Logger;

public interface Model extends Identifiable, Configurable, Serializable {
  
  public byte[] toJSONBytes();
  
  public String toJSONString();
  
  static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper;
  }
  
  public static <T> T buildFromJSONBytes(byte[] json, Class<T> type) {
    Logger LOGGER = Logger.getLogger(Model.class.getName());
    try {
      return createObjectMapper().readValue(json, type);
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }
  
  public static <T> T buildFromJSONString(String json, Class<T> type) {
    return buildFromJSONBytes(json.getBytes(), type);
  }
}
