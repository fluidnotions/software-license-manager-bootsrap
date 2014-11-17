package com.groupfio.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FioLicenseAdminEvent {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "pk")
	private int pk;
	
	@Column(name = "eventtimestamp")
	private Timestamp eventTimestamp;
	
	@Column(name = "serialnumber")
	private String serialNumber;

	@Column(name = "eventtype")
	private String eventType;
	
	@Column(name = "eventdetails")
	private String eventDetails;
	
	@Column(name = "eventcomment")
	private String eventComment;
	
	@Column(name = "applicationtimestamp")
	private Timestamp applicationTimestamp;
	
	@Column(name = "hasbeenapplied")
	private boolean hasBeenApplied;
	
	@Column(name = "byusername")
	private String byUsername;
	
	public FioLicenseAdminEvent(){}
	
	public FioLicenseAdminEvent(String serialNumber, 
			String eventType,
			String eventDetails, 
			String eventComment,
			Timestamp applicationTimestamp,
			String byUsername, 
			boolean hasBeenApplied) {
		super();
		this.serialNumber = serialNumber;
		this.eventType = eventType;
		this.eventDetails = eventDetails;
		this.eventComment = eventComment;
		this.applicationTimestamp = applicationTimestamp;
		this.hasBeenApplied = hasBeenApplied;
		this.byUsername = byUsername;
	}

	public Timestamp getEventTimestamp() {
		return eventTimestamp;
	}



	public void setEventTimestamp(Timestamp eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}



	public String getEventType() {
		return eventType;
	}



	public void setEventType(String eventType) {
		this.eventType = eventType;
	}



	public String getEventDetails() {
		return eventDetails;
	}



	public void setEventDetails(String eventDetails) {
		this.eventDetails = eventDetails;
	}



	public String getEventComment() {
		return eventComment;
	}



	public void setEventComment(String eventComment) {
		this.eventComment = eventComment;
	}



	public Timestamp getApplicationTimestamp() {
		return applicationTimestamp;
	}



	public void setApplicationTimestamp(Timestamp applicationTimestamp) {
		this.applicationTimestamp = applicationTimestamp;
	}



	public boolean isHasBeenApplied() {
		return hasBeenApplied;
	}



	public void setHasBeenApplied(boolean hasBeenApplied) {
		this.hasBeenApplied = hasBeenApplied;
	}



	public String getByUsername() {
		return byUsername;
	}



	public void setByUsername(String byUsername) {
		this.byUsername = byUsername;
	}



	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}



	public int getPk() {
		return pk;
	}

	public void setPk(int pk) {
		this.pk = pk;
	}



	public enum EventType{
		PERIOD_EXTENSION,
		LICENSE_CREATE,
		SUSPEND,
		UNSUSPEND
		
	}
	
	
	/*event type : extension
	event details: time period(one of the time period options) + new activation date + new expiry date
	application date : (the timestamp of when the event was applied - to the primary entity ie: FioLicense)
	user : the logged in user who made the change
	
	event type : creation
	event details: time period(one of the time period options) + activation date + expiry date
	application date : (the timestamp of when the event was applied - to the primary entity ie: FioLicense)
	user : the logged in user who created the record
	
	event type : suspension/termination
	event details: the reason if applicatable
	application date : (the timestamp of when the event was applied - to the primary entity ie: FioLicense)
	user : the logged in user. (this could also be automatic on expiry date or under other conditions)*/
}


