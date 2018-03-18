
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "files"
})
public class Storage {

    @JsonProperty("files")
    public List<File> files = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Storage() {
    }

    /**
     * 
     * @param files
     */
    public Storage(List<File> files) {
        super();
        this.files = files;
    }

    public Storage withFiles(List<File> files) {
        this.files = files;
        return this;
    }
    
    public Storage addFiles(List<File> files) {
      if (this.files == null) {
        this.files = new ArrayList<>();
      }
      this.files.addAll(files);
      return this;
    }
    
    public Storage addFile(File file) {
      if (this.files == null) {
        this.files = new ArrayList<>();
      }
      this.files.add(file);
      return this;
    }

}
