package com.groupfio.service;

import com.groupfio.message.pojo.LicFileMessage;

public interface VerifyLicFileService {

	public void executeVerify(LicFileMessage licFileMessage);
	/*public void sendVerificationResultNotifications();*/

}
