
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "users"
})
public class Passwd {

    @JsonProperty("users")
    public List<User> users = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Passwd() {
    }

    /**
     * 
     * @param users
     */
    public Passwd(List<User> users) {
        super();
        this.users = users;
    }

    public Passwd withUsers(List<User> users) {
        return addUsers(users);
    }
    
    public Passwd addUsers(List<User> users) {
      if (this.users == null) {
        this.users = new ArrayList<>();
      }
      this.users = users;
      return this;
    }
    
    public Passwd addUser(User user) {
      if (this.users == null) {
        this.users = new ArrayList<>();
      }
      this.users.add(user);
      return this;
    }

}
