
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ignition",
    "storage",
    "systemd",
    "networkd",
    "passwd"
})
public class IgnitionBuilder {

    @JsonProperty("ignition")
    public Ignition ignition;
    @JsonProperty("storage")
    public Storage storage;
    @JsonProperty("systemd")
    public Systemd systemd;
    @JsonProperty("networkd")
    public Networkd networkd;
    @JsonProperty("passwd")
    public Passwd passwd;

    /**
     * No args constructor for use in serialization
     * 
     */
    public IgnitionBuilder() {
    }

    /**
     * 
     * @param passwd
     * @param systemd
     * @param networkd
     * @param ignition
     * @param storage
     */
    public IgnitionBuilder(Ignition ignition, Storage storage, Systemd systemd, Networkd networkd, Passwd passwd) {
        super();
        this.ignition = ignition;
        this.storage = storage;
        this.systemd = systemd;
        this.networkd = networkd;
        this.passwd = passwd;
    }

    public IgnitionBuilder withIgnition(Ignition ignition) {
        this.ignition = ignition;
        return this;
    }

    public IgnitionBuilder withStorage(Storage storage) {
        this.storage = storage;
        return this;
    }

    public IgnitionBuilder withSystemd(Systemd systemd) {
        this.systemd = systemd;
        return this;
    }

    public IgnitionBuilder withNetworkd(Networkd networkd) {
        this.networkd = networkd;
        return this;
    }

    public IgnitionBuilder withPasswd(Passwd passwd) {
        this.passwd = passwd;
        return this;
    }

}
