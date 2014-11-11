package com.groupfio.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import com.groupfio.dao.FioLicenseDAO;
import com.groupfio.model.FioLicense;
import com.groupfio.pojo.LicFile;
import com.groupfio.pojo.ActionResult;
import com.groupfio.pojo.LicFile.LicFileAction;

@Service
public class VerifyLicFileServiceImpl implements VerifyLicFileService {

	private static final Log logger = LogFactory
			.getLog(VerifyLicFileServiceImpl.class);

	@Autowired
	private FioLicenseDAO fioLicenseDAO;

	private final SimpMessageSendingOperations messagingTemplate;

	private final List<ActionResult> actionResults = new CopyOnWriteArrayList<>();

	@Autowired
	public VerifyLicFileServiceImpl(
			SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;

	}

	/**
	 * In real application a trade is probably executed in an external system,
	 * i.e. asynchronously.
	 */
	@Override
	@Transactional
	public void executeVerify(LicFile licFile) {
		String serialnumber = licFile.getSerialnumber();

		FioLicense fioLicense = fioLicenseDAO.getFioLicence(serialnumber);
		ActionResult result = null;
		if (fioLicense == null) {
			String payload = "Rejected licFile " + licFile
					+ ". Reason [serialnumber=" + serialnumber
					+ " not found in database!]";
			logger.error(payload);
			this.messagingTemplate.convertAndSendToUser(
					licFile.getSerialnumber(), "/queue/errors", payload);
			return;
		}
		if (licFile.getAction() == LicFileAction.ChecksumAndFileSize) {
			String verified = LicFile.VFAIL;
			long timestamp = System.currentTimeMillis();

			// check values from agent against stored values
			if (fioLicense.getLicfileCheckSum().trim()
					.equals(licFile.getLicfileCheckSum())
					&& fioLicense.getLicfileByteSize() == licFile
							.getLicfileByteSize()) {
				verified = LicFile.VPASS;
			}

			 result = new ActionResult(licFile.getSerialnumber(),
					timestamp, licFile.getAction().name(), verified);
			Map<String, Object> map = new HashMap<>();
			map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

			logger.debug("Sending verified result: " + verified);
			this.messagingTemplate.convertAndSendToUser(
					result.getSerialNumber(), "/queue/results", result, map);

		} else {
			String payload = "LicFileAction not set or not implemented! ("
					+ licFile.getAction() + ")";
			logger.error(payload);
			this.messagingTemplate.convertAndSendToUser(
					licFile.getSerialnumber(), "/queue/errors", payload);
		}

		if(result!=null)fioLicenseDAO.updateFioLicenseForAgentActionResult(result);
	}
}
