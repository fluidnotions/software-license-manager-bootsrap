package com.groupfio.controller;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.groupfio.message.pojo.LicFileMessage;
import com.groupfio.service.VerifyLicFileService;

@Controller
public class StompController {

	private static final Log log = LogFactory.getLog(StompController.class);

	private final VerifyLicFileService verifyLicFileService;

	@Autowired
	public StompController(VerifyLicFileService verifyLicFileService) {
		this.verifyLicFileService = verifyLicFileService;

	}

	@MessageMapping("/licfileverify")
	public void executeVerify(LicFileMessage licFileMessage, Principal principal) {
		licFileMessage.setSerialNumber(principal.getName());
		log.debug("LicFileMessage: " + licFileMessage);
		this.verifyLicFileService.executeVerify(licFileMessage);
	}

	@MessageExceptionHandler
	@SendToUser("/queue/errors")
	public String handleException(Throwable exception) {
		return exception.getMessage();
	}

}
