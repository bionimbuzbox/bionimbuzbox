package org.bionimbuzbox.model;

import java.util.Date;
import java.util.logging.Logger;

public class EventHandler extends GenericModel implements Model  {

	private static final Logger LOGGER = Logger.getLogger(EventHandler.class.getName());
	
	private static final long serialVersionUID = -2637809607096387326L;
	private static final int DEFAULT_PRIORITY = 10;
	private static final long DEFAULT_TTL = 0;
	
	private String eventName;
	private Integer priority = DEFAULT_PRIORITY;
	private String handler;
	private Date creationDate;
	private Long TTL;
	
	public EventHandler() {
		
	}
	
	public EventHandler(String eventName, String handler) {
		this(eventName, handler, DEFAULT_PRIORITY, DEFAULT_TTL);
	}
	
	public EventHandler(String eventName, String handler, Integer priority) {
		this(eventName, handler, priority, DEFAULT_TTL);
	}
	
	public EventHandler(String eventName, String handler, Integer priority, Long ttl) {
		this.eventName = eventName;
		this.handler = handler;
		this.priority = priority;
		this.TTL = ttl;
	}
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Long getTTL() {
		return TTL;
	}
	public void setTTL(Long ttl) {
		TTL = ttl;
	}
	public Date getExpirationDate() {
		if (this.creationDate == null || this.TTL == 0) {
			return null;
		}
		return new Date(this.creationDate.getTime() + this.TTL);
	}
	
	
	public String toString() {
		return String.format("[id=%s, eventName=%s, handler=%s, priority=%d, creationDate=%s, expirationDate=%s, TTL=%d]", 
				this.getId(), 
				this.eventName, 
				this.handler,
				this.priority,
				this.creationDate,
				this.getExpirationDate(),
				this.TTL
		);
	}
}
