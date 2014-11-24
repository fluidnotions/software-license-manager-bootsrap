package com.groupfio.dao;

import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;

public interface FioLicenseActionUpdateDAO {

	void updateFioLicenseForAgentActionResult(Message message);

	FioLicense getFioLicence(String serialnumber);

}
