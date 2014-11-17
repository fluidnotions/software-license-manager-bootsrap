package com.groupfio.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//using a form backing been since we do have some fields which are transient 
//& can more easily ad  validation annotations here
public class FioLicenseFormBean {

	private String serialNumber;
	private String contactName;
	private String currentStatus;
	private boolean isEnabled;
	private String appVersion;
	private String validityPeriodString;
	private boolean suspend;
	private Timestamp activationDate;
	private String activationDateString;
	private Timestamp expirationDate;
	private int licfileByteSize;
	private String licfileCheckSum;
	private Timestamp lastAgentComTime;
	private String lastAgentComAction;
	private String lastAgentComActionResult;
	private String byUsername;
	private String email;

	public static FioLicenseFormBean newFormBackingBean(FioLicense ent) {
		FioLicenseFormBean form = new FioLicenseFormBean();
		
		form.setActivationDate(ent.getActivationDate());
		form.setActivationDateString(ent.getActivationDateString());
		form.setAppVersion(ent.getAppVersion());
		//form.setByUsername(ent.getByUsername());
		form.setContactName(ent.getContactName());
		form.setCurrentStatus(ent.getCurrentStatus());
		form.setEmail(ent.getEmail());
		form.setEnabled(ent.isEnabled());
		form.setExpirationDate(ent.getExpirationDate());
		form.setLastAgentComAction(ent.getLastAgentComAction());
		form.setLastAgentComActionResult(ent.getLastAgentComActionResult());
		form.setLastAgentComTime(ent.getLastAgentComTime());
		form.setLicfileByteSize(ent.getLicfileByteSize());
		form.setLicfileCheckSum(ent.getLicfileCheckSum());
		form.setSerialNumber(ent.getSerialNumber());
		//form.setSuspend(ent.isSuspend());
		//form.setValidityPeriodString(ent.getValidityPeriodString());
		
		return form;
	}
	
	public static FioLicense entityFromBackingBean(FioLicenseFormBean form) {
		FioLicense ent = new FioLicense();
		
		ent.setActivationDate(form.getActivationDate());
		ent.setActivationDateString(form.getActivationDateString());
		ent.setAppVersion(form.getAppVersion());
		ent.setContactName(form.getContactName());
		ent.setCurrentStatus(form.getCurrentStatus());
		ent.setEmail(form.getEmail());
		ent.setEnabled(form.isEnabled());
		ent.setExpirationDate(form.getExpirationDate());
		ent.setLastAgentComAction(form.getLastAgentComAction());
		ent.setLastAgentComActionResult(form.getLastAgentComActionResult());
		ent.setLastAgentComTime(form.getLastAgentComTime());
		ent.setLicfileByteSize(form.getLicfileByteSize());
		ent.setLicfileCheckSum(form.getLicfileCheckSum());
		ent.setSerialNumber(form.getSerialNumber());
		//form.setSuspend(ent.isSuspend());
		//form.setValidityPeriodString(ent.getValidityPeriodString());
		
		return ent;
	}
	//unlikely to use any time soon
	public static List<FioLicenseFormBean> newFormBackingBeanList(List<FioLicense> list){
		List<FioLicenseFormBean> beanList = new ArrayList<FioLicenseFormBean>();
		for(FioLicense fl: list){
			beanList.add(newFormBackingBean(fl));
		}
		return beanList;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getValidityPeriodString() {
		return validityPeriodString;
	}

	public void setValidityPeriodString(String validityPeriodString) {
		this.validityPeriodString = validityPeriodString;
	}

	public Timestamp getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Timestamp activationDate) {
		this.activationDate = activationDate;
	}

	public String getActivationDateString() {
		return activationDateString;
	}

	public void setActivationDateString(String activationDateString) {
		this.activationDateString = activationDateString;
	}

	public Timestamp getExpirationDate() {
		return expirationDate;
	}

	public boolean isSuspend() {
		return suspend;
	}

	public void setSuspend(boolean suspend) {
		this.suspend = suspend;
	}

	public void setExpirationDate(Timestamp expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getLicfileByteSize() {
		return licfileByteSize;
	}

	public void setLicfileByteSize(int licfileByteSize) {
		this.licfileByteSize = licfileByteSize;
	}

	public String getLicfileCheckSum() {
		return licfileCheckSum;
	}

	public void setLicfileCheckSum(String licfileCheckSum) {
		this.licfileCheckSum = licfileCheckSum;
	}

	public Timestamp getLastAgentComTime() {
		return lastAgentComTime;
	}

	public void setLastAgentComTime(Timestamp lastAgentComTime) {
		this.lastAgentComTime = lastAgentComTime;
	}

	public String getLastAgentComAction() {
		return lastAgentComAction;
	}

	public void setLastAgentComAction(String lastAgentComAction) {
		this.lastAgentComAction = lastAgentComAction;
	}

	public String getLastAgentComActionResult() {
		return lastAgentComActionResult;
	}

	public void setLastAgentComActionResult(String lastAgentComActionResult) {
		this.lastAgentComActionResult = lastAgentComActionResult;
	}

	public String getByUsername() {
		return byUsername;
	}

	public void setByUsername(String byUsername) {
		this.byUsername = byUsername;
	}

}
