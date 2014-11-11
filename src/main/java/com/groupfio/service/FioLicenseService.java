package com.groupfio.service;

import java.util.List;

import com.groupfio.model.FioLicense;

public interface FioLicenseService {
	public void add(FioLicense fioLicense);

	public void edit(FioLicense fioLicense);

	public void delete(String serialnumber);

	public FioLicense getFioLicense(String serialnumber);

	public List getAllFioLicense();
}
