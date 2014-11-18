package com.groupfio.dao;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.groupfio.model.FioLicense;
import com.groupfio.model.FioLicenseAdminEvent;
import com.groupfio.service.FioLicenseServiceImpl;

@Repository
public class FioLicenseAdminEventDAOImpl implements FioLicenseAdminEventDAO {
	
	private static final Log log = LogFactory
			.getLog(FioLicenseAdminEventDAOImpl.class);

	@Autowired
	private SessionFactory session;

	@Override
	public void logAdminEvent(String serialNumber, String eventType, String eventDetails,
			String eventComment, Timestamp applicationTimestamp,
			String byUsername, boolean hasBeenApplied) {
		log.debug("logAdminEvent: applicationTimestamp: "+applicationTimestamp);
		
		//my convention is use null for now
		if(applicationTimestamp==null){
			applicationTimestamp = new Timestamp(System.currentTimeMillis());
		}
		FioLicenseAdminEvent adminEvent = new FioLicenseAdminEvent(serialNumber, eventType,
				eventDetails, eventComment, applicationTimestamp, byUsername,
				hasBeenApplied);
		//ids for this class must be manually assigned before calling save(): com.groupfio.model.FioLicenseAdminEvent
		adminEvent.setEventTimestamp(new Timestamp(System.currentTimeMillis()));

		
		session.getCurrentSession().save(adminEvent);

	}
	
	@Override
	public void deleteAnyExistingFutureSuspendAdminEvents(String serialNumber){
		Query q = session.getCurrentSession().createQuery("delete FioLicenseAdminEvent where serialNumber = :serialNumber and eventType = :eventType and hasBeenApplied = false");
		q.setString("serialNumber", serialNumber);
		q.setString("eventType", FioLicenseAdminEvent.EventType.SUSPEND.name());
		
		q.executeUpdate();
	}
	
	@Override
	public void deleteAllAdminEvents(String serialNumber){
		Query q = session.getCurrentSession().createQuery("delete FioLicenseAdminEvent where serialNumber = :serialNumber");
		q.setString("serialNumber", serialNumber);
		q.executeUpdate();
	}

	@Override
	public List getAllFioLicenseAdminEventsForSerialNumber(String serialNumber) {
		Query q = session.getCurrentSession().createQuery("from FioLicenseAdminEvent where serialNumber = :serialNumber");
		q.setString("serialNumber", serialNumber);
		return q.list();
	}

	@Override
	public List<FioLicenseAdminEvent> getHasntBeenAppliedSuspendFioLicenseAdminEvents(Timestamp applicationTimestamp){
		Query q = session.openStatelessSession().createQuery("from FioLicenseAdminEvent where applicationTimestamp = :applicationTimestamp and hasBeenApplied = false");
		q.setTimestamp("applicationTimestamp", applicationTimestamp);
		return q.list();
	}

	@Override
	public void update(FioLicenseAdminEvent fioLicenseAdminEvent) {
		session.openStatelessSession().update(fioLicenseAdminEvent);
		
	}
	
	
}
