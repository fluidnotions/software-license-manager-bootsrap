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
import org.springframework.util.MimeTypeUtils;

import com.groupfio.dao.FioLicenseAdminEventDAO;
import com.groupfio.dao.FioLicenseDAO;
import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;
import com.groupfio.model.FioLicenseAdminEvent;
import com.groupfio.model.FioLicenseFormBean;

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

	
	public void create(FioLicenseFormBean formBean) {
		FioLicense fioLicenseEntity = FioLicenseFormBean
				.entityFromBackingBean(formBean);
		// convert form types to entity types
		log.debug("create: ActivationDateString: "
				+ formBean.getActivationDateString());
		Timestamp activationDate = covertFormFieldStringToTimestamp(formBean
				.getActivationDateString());
		log.debug("create: activationDate: " + activationDate);
		String validityPeriodString = formBean.getValidityPeriodString();
		log.debug("create: validityPeriodString: " + validityPeriodString);
		Timestamp expirationDate = calculateExpirationTimestampEntityValueFromFormData(
				activationDate, validityPeriodString);
		log.debug("create: expirationDate: " + expirationDate.toString());
		fioLicenseEntity.setActivationDate(activationDate);
		fioLicenseEntity.setExpirationDate(expirationDate);
		fioLicenseEntity.setEnabled(true);

		fioLicenseDAO.create(fioLicenseEntity);

		// TODO this would better be implemented on a application/persistance
		// listener on fioLicense create
		fioLicenseAdminEventDAO.logAdminEvent(formBean.getSerialNumber(),
				FioLicenseAdminEvent.EventType.LICENSE_CREATE.name(),
				"inital manual creation of fio License (inital period "
						+ formBean.getValidityPeriodString() + ")", null, null,
				formBean.getByUsername(), true);

		fioLicenseAdminEventDAO.logAdminEvent(formBean.getSerialNumber(),
				FioLicenseAdminEvent.EventType.SUSPEND.name(),
				"future suspend instruction", null,
				fioLicenseEntity.getExpirationDate(), formBean.getByUsername(),
				false);

	}

	
	public void update(FioLicenseFormBean formBean) {
		FioLicense fioLicenseEntity = FioLicenseFormBean
				.entityFromBackingBean(formBean);
		FioLicense retainDates = fioLicenseDAO.getFioLicence(formBean.getSerialNumber());
		fioLicenseEntity.setActivationDate(retainDates.getActivationDate());
		fioLicenseEntity.setExpirationDate(retainDates.getExpirationDate());
		// convert form types to entity types
		Message terminate = null;

		// immediate vs future termination is important here
		// future terminations will be assumed is this update
		// has a non null validityPeriodString
		log.debug("formBeanFioLicense: " + formBean.toString());
		boolean extensionUpdate = false;
		if (!"NONE".equals(formBean.getValidityPeriodString())) {
			// calculate newExpirationDate given selected period set into entity
			// log 2 admin Event types extension and type terminate
			// a scheduled job runs at midnight daily searching the
			// fioLicenseAdminEvent
			// table for SUSPEND, hasBeenApplied=false, application date
			// match & then sends terminate instruction to the client
			Timestamp oldExpirationDate = fioLicenseDAO
					.getFioLicenceCurrentExpiryDate(formBean.getSerialNumber());

			Timestamp newExpirationDate = calculateExpirationTimestampEntityValueFromFormData(
					oldExpirationDate, formBean.getValidityPeriodString());

			fioLicenseEntity.setExpirationDate(newExpirationDate);

			fioLicenseAdminEventDAO
					.logAdminEvent(
							formBean.getSerialNumber(),
							FioLicenseAdminEvent.EventType.PERIOD_EXTENSION
									.name(),
							"period has been extended by "
									+ formBean.getValidityPeriodString(),
							"a suspend admin event was created with a future application date",
							null, formBean.getByUsername(), true);
			
			extensionUpdate = true;
			// when adding a future suspend event we need to delete any existing
			// future suspend event
			fioLicenseAdminEventDAO
					.deleteAnyExistingFutureSuspendAdminEvents(formBean
							.getSerialNumber());
			fioLicenseAdminEventDAO.logAdminEvent(formBean.getSerialNumber(),
					FioLicenseAdminEvent.EventType.SUSPEND.name(),
					"future suspend event", null, newExpirationDate,
					formBean.getByUsername(), false);

		}

		if (formBean.isSuspend()) {
			fioLicenseEntity.setEnabled(false);
			terminate = new Message();
			terminate.setSerialNumber(formBean.getSerialNumber());
			terminate.setAction("Terminate");
			terminate.setActionDescription("User initialized terminate");
			terminate.setActionMsg("Terminate Immediately");

			fioLicenseAdminEventDAO.logAdminEvent(formBean.getSerialNumber(),
					FioLicenseAdminEvent.EventType.SUSPEND.name(),
					"immediate suspend event", null, null,
					formBean.getByUsername(), true);
		} else {
			fioLicenseEntity.setEnabled(true);
			//having an extend and immediate suspend in the same update makes no sense
			if(!extensionUpdate && !fioLicenseDAO.getFioLicenceCurrentIsEnabled(formBean.getSerialNumber())){
				fioLicenseAdminEventDAO.logAdminEvent(formBean.getSerialNumber(),
						FioLicenseAdminEvent.EventType.UNSUSPEND.name(),
						"immediate unsuspend event", null, null,
						formBean.getByUsername(), true);
			}
		}

		fioLicenseDAO.update(fioLicenseEntity);

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

	
	public List getAllFioLicenseAdminEventsForSerialNumber(String serialNumber) {
		return fioLicenseAdminEventDAO
				.getAllFioLicenseAdminEventsForSerialNumber(serialNumber);
	}

	
	public void delete(String serialnumber) {
		fioLicenseAdminEventDAO.deleteAllAdminEvents(serialnumber);
		fioLicenseDAO.delete(serialnumber);

	}

	
	public FioLicenseFormBean getFioLicenseFormBean(String serialnumber) {
		FioLicense fl = fioLicenseDAO.getFioLicence(serialnumber);
		FioLicenseFormBean formBean = FioLicenseFormBean.newFormBackingBean(fl);
		if (formBean != null) {
			formBean.setActivationDateString(covertFormTimestampToFieldString(fl
					.getActivationDate()));
			if (!fl.isEnabled()) {
				formBean.setSuspend(true);
			} else {
				formBean.setSuspend(false);
			}
		}
		return formBean;
	}

	
	public List getAllFioLicense() {
		return fioLicenseDAO.getAllFioLicence();
	}

	private Timestamp calculateExpirationTimestampEntityValueFromFormData(
			Timestamp date, String validityPeriodString) {
		Timestamp expirationDate = null;

		log.debug("validityPeriodString: " + validityPeriodString);
		switch (validityPeriodString) {
		case "6 months":
			expirationDate = addMonthsToTimestamp(6 * 30, date);
			break;
		case "1 year":
			expirationDate = addMonthsToTimestamp(12 * 30, date);
			break;
		case "2 years":
			expirationDate = addMonthsToTimestamp(24 * 30, date);
			break;
		}

		return expirationDate;

	}

	private Timestamp addMonthsToTimestamp(int days, Timestamp ts) {
		Timestamp adjusted = null;
		if (ts != null) {
			log.debug("days to add: " + days);
			DateTime dt = new DateTime(ts);
			adjusted = new Timestamp(dt.plusDays(days).toDate().getTime());
			log.debug("initial: " + ts.toString() + ", adjusted: "
					+ adjusted.toString());
		} else {
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
		if (ts != null) {
			dateString = new SimpleDateFormat(STANDARD_DATE_STRING_FORMAT)
					.format(ts);
		}
		return dateString;
	}

}
