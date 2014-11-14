package com.groupfio.service;

import com.groupfio.message.pojo.LicFileMessage;
import com.groupfio.message.pojo.Message;

public interface CheckActionService {

	public void executeVerify(LicFileMessage licFileMessage);

	public void executeCheckIsEnabled(Message message);

}
