
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "filesystem",
    "path",
    "contents",
    "mode",
    "user",
    "group"
})
public class File {

    @JsonProperty("filesystem")
    public String filesystem;
    @JsonProperty("path")
    public String path;
    @JsonProperty("contents")
    public Contents contents;
    @JsonProperty("mode")
    public int mode;
    @JsonProperty("user")
    public User user;
    @JsonProperty("group")
    public Group group;

    /**
     * No args constructor for use in serialization
     * 
     */
    public File() {
    }

    /**
     * 
     * @param contents
     * @param path
     * @param group
     * @param user
     * @param filesystem
     * @param mode
     */
    public File(String filesystem, String path, Contents contents, int mode, User user, Group group) {
        super();
        this.filesystem = filesystem;
        this.path = path;
        this.contents = contents;
        this.mode = mode;
        this.user = user;
        this.group = group;
    }

    public File withFilesystem(String filesystem) {
        this.filesystem = filesystem;
        return this;
    }

    public File withPath(String path) {
        this.path = path;
        return this;
    }

    public File withContents(Contents contents) {
        this.contents = contents;
        return this;
    }

    public File withMode(int mode) {
        this.mode = mode;
        return this;
    }

    public File withUser(User user) {
        this.user = user;
        return this;
    }

    public File withGroup(Group group) {
        this.group = group;
        return this;
    }

}
