
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "version",
    "config"
})
public class Ignition {

    @JsonProperty("version")
    public String version;
    @JsonProperty("config")
    public Config config;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Ignition() {
    }

    /**
     * 
     * @param config
     * @param version
     */
    public Ignition(String version, Config config) {
        super();
        this.version = version;
        this.config = config;
    }

    public Ignition withVersion(String version) {
        this.version = version;
        return this;
    }

    public Ignition withConfig(Config config) {
        this.config = config;
        return this;
    }

}
