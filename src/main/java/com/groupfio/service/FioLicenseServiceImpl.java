package com.groupfio.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import com.groupfio.dao.FioLicenseDAO;
import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;

@Service
public class FioLicenseServiceImpl implements FioLicenseService {
	
	private static final Log log = LogFactory
			.getLog(FioLicenseServiceImpl.class);

	private final SimpMessageSendingOperations messagingTemplate;

	@Autowired
	public FioLicenseServiceImpl(
			SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;

	}
	
	@Autowired
	private FioLicenseDAO fioLicenseDAO;

	@Transactional
	public void create(FioLicense fioLicense) {
		//convert form types to entity types
		convertFormDateToEntityValues(fioLicense);
		fioLicenseDAO.create(fioLicense);

	}

	@Transactional
	public void update(FioLicense fioLicense) {
		//convert form types to entity types
		convertFormDateToEntityValues(fioLicense);
		Message terminate = null;
		if(fioLicense.isTerminate()){
			fioLicense.setIsEnabled("FALSE");
			terminate = new Message();
			terminate.setSerialNumber(fioLicense.getSerialNumber());
			terminate.setAction("Terminate");
			terminate.setActionDescription("User initialed terminate");
			terminate.setActionMsg("Terminate Immediately");
		}else{
			fioLicense.setIsEnabled("TRUE");
		}
		fioLicenseDAO.update(fioLicense);
		
		//send action to user
		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

		log.debug("Sending action: " + terminate.toString());
		this.messagingTemplate.convertAndSendToUser(
				terminate.getSerialNumber(), "/queue/results", terminate, map);

	}

	@Transactional
	public void delete(String serialnumber) {
		fioLicenseDAO.delete(serialnumber);

	}

	@Transactional
	public FioLicense getFioLicense(String serialnumber) {
		FioLicense fl = fioLicenseDAO.getFioLicence(serialnumber);
		if(fl.getIsEnabled().equals("FALSE")){
			fl.setTerminate(true);
		}else{
			fl.setTerminate(false);
		}
		return fl;
	}

	@Transactional
	public List getAllFioLicense() {
		return fioLicenseDAO.getAllFioLicence();
	}
	
	private void convertFormDateToEntityValues(FioLicense fioLicense) {
		String activationDateString = fioLicense.getActivationDateString();
		if (activationDateString != null && !activationDateString.isEmpty()) {
			log.debug("activationDateString: " + activationDateString);
			// INFO : com.groupfio.controller.FioLicenseController -
			// activationDateString: 10/28/2014
			// s - timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]. The
			// fractional seconds may be omitted. The leading zero for mm and dd
			// may also be omitted.
			fioLicense
					.setActivationDate(covertFormFieldStringToTimestamp(activationDateString));
			String validityPeriodString = fioLicense.getValidityPeriodString();
			log.debug("validityPeriodString: " + validityPeriodString);
			switch (validityPeriodString) {
			case "6 months":
				fioLicense.setExpirationDate(addMonthsToTimestamp(6,
						fioLicense.getActivationDate()));
				break;
			case "1 year":
				fioLicense.setExpirationDate(addMonthsToTimestamp(12,
						fioLicense.getActivationDate()));
				break;
			case "2 years":
				fioLicense.setExpirationDate(addMonthsToTimestamp(24,
						fioLicense.getActivationDate()));
				break;
			}
		} else {
			log.debug("activationDateString is null");
		}

	}

	private Timestamp addMonthsToTimestamp(int months, Timestamp ts) {
		log.debug("months to add: "+months);
		int years = 0;
		if(months>12){
			years = months/12;
			log.debug("months/12: "+years);
			months = months%12;
			log.debug("remainder months: "+months);
			
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getDefault());
		cal.setTime(ts);
		cal.add(Calendar.MONTH, months);
		if(years>0){
			cal.add(Calendar.YEAR, years);
		}
		Timestamp adjusted = new Timestamp(cal.getTime().getTime());
		log.debug("initial: "+ts.toString()+", adjusted: "+adjusted.toString());
		return adjusted;
	}

	private Timestamp covertFormFieldStringToTimestamp(String activationDateString){
		//INFO : com.groupfio.controller.FioLicenseController - activationDateString: 10/30/2014
		//INFO : com.groupfio.controller.FioLicenseController - std timestamp formatted date: 2014-01-30 00:10:00.000
		
		Timestamp ts = null;
		if (activationDateString!=null && !activationDateString.isEmpty()) {
			try {
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				Date d = formatter.parse(activationDateString);
				((SimpleDateFormat) formatter)
						.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
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

}
