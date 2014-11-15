package com.groupfio.service;

import java.util.List;

import com.groupfio.model.FioLicense;

public interface FioLicenseService {
	
	public void create(FioLicense fioLicense);

	public void update(FioLicense fioLicense);

	public void delete(String serialnumber);

	public FioLicense getFioLicense(String serialnumber);

	public List getAllFioLicense();
	
	public List getAllFioLicenseAdminEventsForSerialNumber(String serialNumber);
}
