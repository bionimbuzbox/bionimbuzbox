package org.bionimbuzbox.model;

import java.util.Date;

public class Event extends GenericModel implements Model {
	private static final long serialVersionUID = -4668201992744222011L;
	
	private String ID;
	private String eventName;
	private Date creationDate; 
	
	public Event() {
		super();
	}
	
	public Event(String eventName) {
		super();
		this.eventName = eventName;
	}

	public String getID() {
		return ID;
	}

	public void setID(String id) {
		ID = id;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	} 
	
	public String toString() {
		return String.format("[id=%s, eventName=%s, creationDate=%tY-%tm-%td %tH:%tM:%tS]", 
				this.ID, 
				this.eventName, 
				this.creationDate
		);
	}
}
