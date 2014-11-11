package com.groupfio.controller;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.groupfio.pojo.LicFile;
import com.groupfio.service.VerifyLicFileService;

@Controller
public class StompController {

	private static final Log logger = LogFactory.getLog(StompController.class);

	private final VerifyLicFileService verifyLicFileService;

	@Autowired
	public StompController(VerifyLicFileService verifyLicFileService) {
		this.verifyLicFileService = verifyLicFileService;

	}

	@MessageMapping("/licfileverify")
	public void executeVerify(LicFile licFile, Principal principal) {
		licFile.setSerialnumber(principal.getName());
		logger.debug("LicFile: " + licFile);
		this.verifyLicFileService.executeVerify(licFile);
	}

	@MessageExceptionHandler
	@SendToUser("/queue/errors")
	public String handleException(Throwable exception) {
		return exception.getMessage();
	}

}
