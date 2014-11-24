package com.groupfio.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
@SelectBeforeUpdate 
public class FioLicense {

	@Id
	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "contact_name")
	private String contactName;

	@Column(name = "current_status")
	private String currentStatus;

	@Column(name = "is_enabled")
	private boolean isEnabled;

	@Column(name = "app_version")
	private String appVersion;

	@Column(name = "email")
	private String email;

	@Column(name = "activation_date")
	private Timestamp activationDate;

	@Transient
	private String activationDateString;

	@Column(name = "expiration_date")
	private Timestamp expirationDate;

	@Column(name = "lic_file_byte_size")
	private int licfileByteSize;

	@Column(name = "lic_file_checksum")
	private String licfileCheckSum;

	@Column(name = "last_agent_com_time")
	private Timestamp lastAgentComTime;

	@Column(name = "last_agent_com_action")
	private String lastAgentComAction;

	@Column(name = "last_agent_com_action_result")
	private String lastAgentComActionResult;

	public FioLicense() {
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

}
