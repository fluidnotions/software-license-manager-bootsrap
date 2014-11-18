package com.groupfio.scheduled;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import com.groupfio.dao.FioLicenseAdminEventDAO;
import com.groupfio.dao.FioLicenseDAO;
import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;
import com.groupfio.model.FioLicenseAdminEvent;

@Component
public class ScheduledSuspendOnExpiry {
	
	private static final Log log = LogFactory
			.getLog(ScheduledSuspendOnExpiry.class);

	private final SimpMessageSendingOperations messagingTemplate;

	@Autowired
	public ScheduledSuspendOnExpiry(SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;

	}

	@Autowired
	private FioLicenseAdminEventDAO fioLicenseAdminEventDAO;
	
	@Autowired
	private FioLicenseDAO fioLicenseDAO;

	//every min
	//@Scheduled(fixedDelay=60000)
	public void suspendOnExpiryTest() {
		log.debug("Scheduled: suspendOnExpiryTest running");
		String appLicationDateTestString = "2015-05-17";
		Timestamp applicationDate = covertStringToTimestamp(appLicationDateTestString);
		suspendOnExpiryTask(applicationDate);
	}
	
	//for testing purposes only
	private Timestamp covertStringToTimestamp(
			String string) {
		Timestamp ts = null;
		if (string != null && !string.isEmpty()) {
			try {
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date d = formatter.parse(string);
				((SimpleDateFormat) formatter)
						.applyPattern("yyyy-MM-dd HH:mm:ss");
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
	
	//This will fire at midnight
	@Async
	@Scheduled(cron="0 0 0 * * ?")
	public void suspendOnExpiry() {
		log.debug("Scheduled: suspendOnExpiry running");
		suspendOnExpiryTask();
	}
	
	
	private void suspendOnExpiryTask() {
		@SuppressWarnings("deprecation")
		DateMidnight md = new DateMidnight(); 
		Timestamp applicationDate = new Timestamp(md.toDate().getTime());
		suspendOnExpiryTask(applicationDate);
	}
	
	
	
	private void suspendOnExpiryTask(Timestamp applicationDate) {

	    List<FioLicenseAdminEvent> relaventAdminEvents = fioLicenseAdminEventDAO.getHasntBeenAppliedSuspendFioLicenseAdminEvents(applicationDate);
	    log.debug("Scheduled: relaventAdminEvents list with timestamp: "+applicationDate.toString()+", list.size: "+relaventAdminEvents.size()); 
	    if (relaventAdminEvents.size()>0) {
			for (FioLicenseAdminEvent ev : relaventAdminEvents) {
				
				ev.setHasBeenApplied(true);
				fioLicenseAdminEventDAO.update(ev);
				FioLicense fl = fioLicenseDAO.getFioLicence(ev.getSerialNumber());
				fl.setEnabled(false);
				fioLicenseDAO.update(fl);
				log.debug("Scheduled: updateRelaventAdminEvents: associated FioLicense updated: "+fl.toString());
				
				sendSuspendInstructionToAgent(ev.getSerialNumber());
			}
			
		}
	}
	
	@Async
	private void sendSuspendInstructionToAgent(String serialNumber){
		// send action to user
		Message terminate = new Message();
		terminate.setSerialNumber(serialNumber);
		terminate.setAction("Terminate");
		terminate.setActionDescription("Scheduled terminate");
		terminate.setActionMsg("Terminate Immediately");
					Map<String, Object> map = new HashMap<>();
					map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
					log.debug("Scheduled: sendSuspendInstructionToAgent: Sending action: " + terminate.toString());
					this.messagingTemplate.convertAndSendToUser(
							terminate.getSerialNumber(), "/queue/results", terminate,
							map);
	}
}
