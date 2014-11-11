package com.groupfio.pojo;

public class ActionResult {

	private String serialNumber;
	private long timestamp;
	
	private String action;
	private String actionResultMsg;
	
	public ActionResult(String serialnumber, long timestamp, String action,
			String actionResultMsg) {
		super();
		this.serialNumber = serialnumber;
		this.timestamp = timestamp;
		this.action = action;
		this.actionResultMsg = actionResultMsg;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialnumber) {
		this.serialNumber = serialnumber;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getActionResultMsg() {
		return actionResultMsg;
	}
	public void setActionResultMsg(String actionResultMsg) {
		this.actionResultMsg = actionResultMsg;
	}
	
	@Override
	public String toString() {
		return "ActionResult [serialNumber=" + serialNumber + ", timestamp="
				+ timestamp + ", action=" + action + ", actionResultMsg="
				+ actionResultMsg + "]";
	}
	
	

	

}