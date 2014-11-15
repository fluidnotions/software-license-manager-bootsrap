package com.groupfio.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import com.groupfio.dao.FioLicenseAdminEventDAO;
import com.groupfio.dao.FioLicenseDAO;
import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;
import com.groupfio.model.FioLicenseAdminEvent;

@Service
public class FioLicenseServiceImpl implements FioLicenseService {

	private static final String STANDARD_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String STANDARD_DATE_STRING_FORMAT = "MM/dd/yyyy";

	private static final Log log = LogFactory
			.getLog(FioLicenseServiceImpl.class);

	private final SimpMessageSendingOperations messagingTemplate;

	@Autowired
	public FioLicenseServiceImpl(SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;

	}

	@Autowired
	private FioLicenseDAO fioLicenseDAO;

	@Autowired
	private FioLicenseAdminEventDAO fioLicenseAdminEventDAO;

	@Transactional
	public void create(FioLicense fioLicense) {
		// convert form types to entity types
		Timestamp activationDate = covertFormFieldStringToTimestamp(fioLicense.getActivationDateString());
		log.debug("create: activationDate: "+activationDate);
		String validityPeriodString = fioLicense.getValidityPeriodString();
		log.debug("create: validityPeriodString: "+validityPeriodString);
		Timestamp expirationDate = calculateExpirationTimestampEntityValueFromFormData(activationDate, validityPeriodString);
		log.debug("create: expirationDate: "+expirationDate.toString());
		fioLicense.setActivationDate(activationDate);
		fioLicense.setExpirationDate(expirationDate);
		
		fioLicenseDAO.create(fioLicense);
		
		fioLicenseAdminEventDAO.logAdminEvent(fioLicense.getSerialNumber(),
				FioLicenseAdminEvent.EventType.LICENSE_CREATE.name(),
				"initail manual creation of fio License", null, null,
				fioLicense.getByUsername(), true);
		
		fioLicenseAdminEventDAO.logAdminEvent(fioLicense.getSerialNumber(),
				FioLicenseAdminEvent.EventType.SUSPEND.name(),
				"future suspend instruction", null, fioLicense.getExpirationDate(),
				fioLicense.getByUsername(), false);

	}

	@Transactional
	public void update(FioLicense fioLicense) {
		// convert form types to entity types
		Message terminate = null;

		// immediate vs future termination is important here
		// future terminations will be assumed is this update
		// has a non null validityPeriodString

		if (fioLicense.getValidityPeriodString() != null) {
			// calculate newExpirationDate given selected period set into entity
			// log 2 admin Event types extension and type terminate
			// a scheduled job runs at midnight daily searching the
			// fioLicenseAdminEvent
			// table for SUSPEND, hasBeenApplied=false, application date
			// match & then sends terminate instruction to the client
			Timestamp newExpirationDate = calculateExpirationTimestampEntityValueFromFormData(
					fioLicense.getExpirationDate(),
					fioLicense.getValidityPeriodString());
			
			fioLicense.setExpirationDate(newExpirationDate);

			fioLicenseAdminEventDAO
					.logAdminEvent(
							fioLicense.getSerialNumber(),
							FioLicenseAdminEvent.EventType.PERIOD_EXTENSION
									.name(),
							"period has been extended by "+fioLicense.getValidityPeriodString(),
							"a suspend admin event was created with a future application date",
							null, fioLicense.getByUsername(), true);
			//when adding a future suspend event we need to delete any existing future suspend event
			fioLicenseAdminEventDAO.deleteAnyExistingFutureSuspendAdminEvents();
			fioLicenseAdminEventDAO.logAdminEvent(fioLicense.getSerialNumber(),
					FioLicenseAdminEvent.EventType.SUSPEND.name(),
					"future suspend event", null, newExpirationDate,
					fioLicense.getByUsername(), false);

		}

		if (fioLicense.isSuspend()) {
			fioLicense.setEnabled(false);
			terminate = new Message();
			terminate.setSerialNumber(fioLicense.getSerialNumber());
			terminate.setAction("Terminate");
			terminate.setActionDescription("User initialized terminate");
			terminate.setActionMsg("Terminate Immediately");

			fioLicenseAdminEventDAO.logAdminEvent(fioLicense.getSerialNumber(),
					FioLicenseAdminEvent.EventType.SUSPEND.name(),
					"immediate suspend event", null, null,
					fioLicense.getByUsername(), true);
		} else {
			fioLicense.setEnabled(true);
		}

		fioLicenseDAO.update(fioLicense);

		if (terminate != null) {
			// send action to user
			Map<String, Object> map = new HashMap<>();
			map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
			log.debug("Sending action: " + terminate.toString());
			this.messagingTemplate.convertAndSendToUser(
					terminate.getSerialNumber(), "/queue/results", terminate,
					map);
		}

	}
	
	@Transactional
	public List getAllFioLicenseAdminEventsForSerialNumber(String serialNumber) {
		return fioLicenseAdminEventDAO.getAllFioLicenseAdminEventsForSerialNumber(serialNumber);
	}

	@Transactional
	public void delete(String serialnumber) {
		fioLicenseDAO.delete(serialnumber);

	}

	@Transactional
	public FioLicense getFioLicense(String serialnumber) {
		FioLicense fl = fioLicenseDAO.getFioLicence(serialnumber);
		fl.setActivationDateString(covertFormTimestampToFieldString(fl.getActivationDate()));
		if (!fl.isEnabled()) {
			fl.setSuspend(true);
		} else {
			fl.setSuspend(false);
		}
		return fl;
	}

	@Transactional
	public List getAllFioLicense() {
		return fioLicenseDAO.getAllFioLicence();
	}
	
	private Timestamp calculateExpirationTimestampEntityValueFromFormData(
			Timestamp date, String validityPeriodString) {
		Timestamp expirationDate = null;

		log.debug("validityPeriodString: " + validityPeriodString);
		switch (validityPeriodString) {
		case "6 months":
			expirationDate = addMonthsToTimestamp(6, date);
			break;
		case "1 year":
			expirationDate = addMonthsToTimestamp(12, date);
			break;
		case "2 years":
			expirationDate = addMonthsToTimestamp(24, date);
			break;
		}

		return expirationDate;

	}

	private Timestamp addMonthsToTimestamp(int months, Timestamp ts) {
		Timestamp adjusted = null;
		if (ts!=null) {
			log.debug("months to add: " + months);
			DateTime dt = new DateTime(ts);
			adjusted = new Timestamp(dt.plusMonths(months).toDate().getTime());
			log.debug("initial: " + ts.toString() + ", adjusted: "
					+ adjusted.toString());
		}else{
			log.error("addMonthsToTimestamp Timestamp is null");
		}
		return adjusted;
	}

	private Timestamp covertFormFieldStringToTimestamp(
			String activationDateString) {
		// INFO : com.groupfio.controller.FioLicenseController -
		// activationDateString: 10/30/2014
		// INFO : com.groupfio.controller.FioLicenseController - std timestamp
		// formatted date: 2014-01-30 00:10:00.000

		Timestamp ts = null;
		if (activationDateString != null && !activationDateString.isEmpty()) {
			try {
				DateFormat formatter = new SimpleDateFormat(
						STANDARD_DATE_STRING_FORMAT);
				Date d = formatter.parse(activationDateString);
				((SimpleDateFormat) formatter)
						.applyPattern(STANDARD_TIMESTAMP_FORMAT);
				String newDateString = formatter.format(d);
				log.debug("std timestamp formatted date: " + newDateString);

				ts = Timestamp.valueOf(newDateString);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ts;
	}

	private String covertFormTimestampToFieldString(Timestamp ts) {
		String dateString = "";
		if(ts!=null){
			dateString = new SimpleDateFormat(STANDARD_DATE_STRING_FORMAT).format(ts);
		}
		return dateString;
	}

}
