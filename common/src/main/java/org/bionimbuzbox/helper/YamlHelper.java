package org.bionimbuzbox.helper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.ArrayList;
import java.util.List;
import org.bionimbuzbox.helper.YamlHelper.Test.Gateway;

public class YamlHelper {

  public YamlHelper() {
    // TODO Auto-generated constructor stub
  }
  
  public static String toYAMLString(Object obj) {
    try {
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      //mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
      //mapper.setSerializationInclusion(Include.NON_NULL);
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  static class Test {
    List<String> binds = new ArrayList<>();
    List<Gateway> gateway = new ArrayList<>();
    
    public List<String> getBinds() {
      return binds;
    }
    public void setBinds(List<String> binds) {
      this.binds = binds;
    }
   
    
    
    public List<Gateway> getGateway() {
      return gateway;
    }
    public void setGateway(List<Gateway> gateway) {
      this.gateway = gateway;
    }



    static class Gateway {
      String from;
      String to;
      
      public String getFrom() {
        return from;
      }
      public void setFrom(String from) {
        this.from = from;
      }
      public String getTo() {
        return to;
      }
      public void setTo(String to) {
        this.to = to;
      }
    }
    
  }
  
  public static void main(String[] args) {
    Test t = new Test();
    t.binds.add("0.0.0.0:8080");
    t.binds.add("0.0.0.0:8443");
    
    Gateway g = new Gateway();
    g.setFrom("(http|https)://api.io.etc.br/v1/(provision)");
    g.setTo("https://provision.api.io.etc.br/");
    t.gateway.add(g);
    
    Gateway g1 = new Gateway();
    g1.setFrom("(http|https)://api.io.etc.br/v1/(provision)");
    g1.setTo("https://provision.api.io.etc.br/");
    t.gateway.add(g1);
    
    String output = YamlHelper.toYAMLString(t);
    System.out.println(output);
  }
}
