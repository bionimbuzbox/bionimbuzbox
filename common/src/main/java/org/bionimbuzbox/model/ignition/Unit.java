
package org.bionimbuzbox.model.ignition;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "enable",
    "mask",
    "dropins",
    "contents"
})
public class Unit {

    @JsonProperty("name")
    public String name;
    @JsonProperty("enable")
    public boolean enable;
    @JsonProperty("mask")
    public boolean mask;
    @JsonProperty("dropins")
    public List<Dropin> dropins = null;
    @JsonProperty("contents")
    public String contents;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Unit() {
    }

    /**
     * 
     * @param contents
     * @param name
     * @param enable
     * @param dropins
     */
    public Unit(String name, boolean enable, boolean mask, List<Dropin> dropins, String contents) {
        super();
        this.name = name;
        this.enable = enable;
        this.mask = mask;
        this.dropins = dropins;
        this.contents = contents;
    }

    public Unit withName(String name) {
        this.name = name;
        return this;
    }

    public Unit withEnable(boolean enable) {
        this.enable = enable;
        return this;
    }
    
    public Unit withMask(boolean mask) {
      this.mask = mask;
      return this;
  }

    public Unit withDropins(List<Dropin> dropins) {
        this.dropins = dropins;
        return this;
    }

    public Unit withContents(String contents) {
        this.contents = contents;
        return this;
    }

}
