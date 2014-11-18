package com.groupfio.scheduled;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateMidnight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import com.groupfio.dao.FioLicenseAdminEventDAO;
import com.groupfio.dao.FioLicenseDAO;
import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;
import com.groupfio.model.FioLicenseAdminEvent;

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
	@Scheduled(cron="0 0/1 * 1/1 * ? *")
	public void suspendOnExpiryTest() {
		log.debug("Scheduled: suspendOnExpiryTest running");
		suspendOnExpiryTask();
	}
	
	//This will fire at midnight
	@Scheduled(cron="0 0 0 * * ?")
	public void suspendOnExpiry() {
		log.debug("Scheduled: suspendOnExpiry running");
		suspendOnExpiryTask();
	}
	
	@Async
	private void suspendOnExpiryTask() {
		@SuppressWarnings("deprecation")
		DateMidnight md = new DateMidnight(); 
		List<FioLicenseAdminEvent> relaventAdminEventsUpdated = new ArrayList<>();
		Timestamp applicationDate = new Timestamp(md.toDate().getTime());
	    List<FioLicenseAdminEvent> relaventAdminEvents = fioLicenseAdminEventDAO.getHasntBeenAppliedSuspendFioLicenseAdminEvents(applicationDate);
	    log.debug("Scheduled: relaventAdminEvents list with timestamp: "+applicationDate.toString()+", list.size: "+relaventAdminEvents.size()); 
	    for(FioLicenseAdminEvent ev: relaventAdminEvents){
	    	sendSuspendInstructionToAgent(ev.getSerialNumber());
	    	ev.setHasBeenApplied(true);
	    	relaventAdminEventsUpdated.add(ev);
	    }
	    log.debug("Scheduled: relaventAdminEventsUpdated list.size: "+relaventAdminEventsUpdated.size()); 
	    updateRelaventAdminEvents(relaventAdminEventsUpdated);
	}
	
	@Async
	@Transactional
	private void updateRelaventAdminEvents(List<FioLicenseAdminEvent> relaventAdminEventsUpdated){
		log.debug("Scheduled: updateRelaventAdminEvents: relaventAdminEventsUpdated.size: "+relaventAdminEventsUpdated.size());
		for(FioLicenseAdminEvent e: relaventAdminEventsUpdated){
			fioLicenseAdminEventDAO.update(e);
			FioLicense fl = fioLicenseDAO.getFioLicence(e.getSerialNumber());
			fl.setEnabled(false);
			fioLicenseDAO.update(fl);
			log.debug("Scheduled: updateRelaventAdminEvents: associated FioLicense updated: "+fl.toString());
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
