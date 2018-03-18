
package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "units"
})
public class Systemd {

    @JsonProperty("units")
    public List<Unit> units = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Systemd() {
    }

    /**
     * 
     * @param units
     */
    public Systemd(List<Unit> units) {
        super();
        this.units = units;
    }

    public Systemd withUnits(List<Unit> units) {
        return addUnits(units);
    }
    
    public Systemd addUnit(Unit unit) {
      if (this.units == null) {
        this.units = new ArrayList<>();
      }
      this.units.add(unit);
      return this;
    }
    
    public Systemd addUnits(List<Unit> units) {
      if (this.units == null) {
        this.units = new ArrayList<>();
      }
      this.units.addAll(units);
      return this;
    }
}
