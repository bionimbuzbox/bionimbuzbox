
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "sshAuthorizedKeys",
    "passwordHash",
    "create"
})
public class User {

    @JsonProperty("name")
    public String name;
    @JsonProperty("sshAuthorizedKeys")
    public List<String> sshAuthorizedKeys = null;
    @JsonProperty("passwordHash")
    public String passwordHash;
    @JsonProperty("create")
    public Create create;


    /**
     * No args constructor for use in serialization
     * 
     */
    public User() {
    }

    /**
     * 
     * @param sshAuthorizedKeys
     * @param name
     * @param create
     * @param passwordHash
     */
    public User(String name, List<String> sshAuthorizedKeys, String passwordHash, Create create) {
        super();
        this.name = name;
        this.sshAuthorizedKeys = sshAuthorizedKeys;
        this.passwordHash = passwordHash;
        this.create = create;
    }

    public User withName(String name) {
        this.name = name;
        return this;
    }

    public User withSshAuthorizedKeys(List<String> sshAuthorizedKeys) {
        this.sshAuthorizedKeys = sshAuthorizedKeys;
        return this;
    }

    public User withPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public User withCreate(Create create) {
        this.create = create;
        return this;
    }

}
