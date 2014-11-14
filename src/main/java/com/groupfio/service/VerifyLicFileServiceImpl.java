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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import com.groupfio.dao.FioLicenseDAO;
import com.groupfio.message.pojo.ActionMessageConstants;
import com.groupfio.message.pojo.LicFileMessage;
import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;

@Service
public class VerifyLicFileServiceImpl implements VerifyLicFileService {

	private static final Log log = LogFactory
			.getLog(VerifyLicFileServiceImpl.class);

	@Autowired
	private FioLicenseDAO fioLicenseDAO;

	private final SimpMessageSendingOperations messagingTemplate;

	private final List<Message> messages = new CopyOnWriteArrayList<>();

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
	public void executeVerify(LicFileMessage licFileMessage) {
		String serialnumber = licFileMessage.getSerialNumber();

		FioLicense fioLicense = fioLicenseDAO.getFioLicence(serialnumber);
		Message result = null;
		if (fioLicense == null) {
			String payload = "Rejected licFile " + licFileMessage
					+ ". Reason [serialnumber=" + serialnumber
					+ " not found in database!]";
			log.error(payload);
			this.messagingTemplate.convertAndSendToUser(
					licFileMessage.getSerialNumber(), "/queue/errors", payload);
			return;
		}
		if (licFileMessage.getAction() == ActionMessageConstants.LIC_FILE_ACTION_MSG) {
			String verified = LicFileMessage.VFAIL;
			long timestamp = System.currentTimeMillis();

			// check values from agent against stored values
			if (fioLicense.getLicfileCheckSum().trim()
					.equals(licFileMessage.getLicfileCheckSum())
					&& fioLicense.getLicfileByteSize() == licFileMessage
							.getLicfileByteSize()) {
				verified = LicFileMessage.VPASS;
			}

			 result = new Message(licFileMessage.getSerialNumber(),
					timestamp, licFileMessage.getAction(), verified);
			Map<String, Object> map = new HashMap<>();
			map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

			log.debug("Sending verified result: " + verified);
			this.messagingTemplate.convertAndSendToUser(
					result.getSerialNumber(), "/queue/results", result, map);

		} else {
			String payload = "LicFileAction not set or not implemented! ("
					+ licFileMessage.getAction() + ")";
			log.error(payload);
			this.messagingTemplate.convertAndSendToUser(
					licFileMessage.getSerialNumber(), "/queue/errors", payload);
		}

		if(result!=null)fioLicenseDAO.updateFioLicenseForAgentActionResult(result);
	}
}
