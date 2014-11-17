package com.groupfio.service;

import java.util.List;

import com.groupfio.model.FioLicenseFormBean;

public interface FioLicenseService {
	
	public void create(FioLicenseFormBean fioLicense);

	public void update(FioLicenseFormBean fioLicense);

	public void delete(String serialnumber);

	public FioLicenseFormBean getFioLicenseFormBean(String serialnumber);

	public List getAllFioLicense();
	
	public List getAllFioLicenseAdminEventsForSerialNumber(String serialNumber);
}
