
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "source",
    "verification"
})
public class Contents {

    @JsonProperty("source")
    public String source;
    @JsonProperty("verification")
    public Verification verification;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Contents() {
    }

    /**
     * 
     * @param source
     * @param verification
     */
    public Contents(String source, Verification verification) {
        super();
        this.source = source;
        this.verification = verification;
    }

    public Contents withSource(String source) {
        this.source = source;
        return this;
    }

    public Contents withVerification(Verification verification) {
        this.verification = verification;
        return this;
    }

}
