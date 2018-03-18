
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "contents"
})
public class Dropin {

    @JsonProperty("name")
    public String name;
    @JsonProperty("contents")
    public String contents;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Dropin() {
    }

    /**
     * 
     * @param contents
     * @param name
     */
    public Dropin(String name, String contents) {
        super();
        this.name = name;
        this.contents = contents;
    }

    public Dropin withName(String name) {
        this.name = name;
        return this;
    }

    public Dropin withContents(String contents) {
        this.contents = contents;
        return this;
    }

}
