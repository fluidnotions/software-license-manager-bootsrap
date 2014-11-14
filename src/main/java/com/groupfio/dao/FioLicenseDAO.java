package com.groupfio.dao;

import java.util.List;

import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;

public interface FioLicenseDAO {

	public void create(FioLicense fioLicense);

	public void update(FioLicense fioLicense);

	public void delete(String serialnumber);

	public FioLicense getFioLicence(String serialnumber);

	public List getAllFioLicence();
	
	public void updateFioLicenseForAgentActionResult(Message message);
}
