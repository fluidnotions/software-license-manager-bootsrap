package com.groupfio.dao;

import java.util.List;

import com.groupfio.model.FioLicense;
import com.groupfio.pojo.ActionResult;

public interface FioLicenseDAO {

	public void add(FioLicense fioLicense);

	public void edit(FioLicense fioLicense);

	public void delete(String serialnumber);

	public FioLicense getFioLicence(String serialnumber);

	public List getAllFioLicence();
	
	public void updateFioLicenseForAgentActionResult(ActionResult actionResult);
}
