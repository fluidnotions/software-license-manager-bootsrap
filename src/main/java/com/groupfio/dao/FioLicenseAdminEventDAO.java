package com.groupfio.dao;

import java.sql.Timestamp;
import java.util.List;

import com.groupfio.model.FioLicense;
import com.groupfio.model.FioLicenseAdminEvent;

public interface FioLicenseAdminEventDAO {

	public void logAdminEvent(String serialNumber, 
			String eventType,
			String eventDetails, 
			String eventComment,
			Timestamp applicationTimestamp,
			String byUsername, 
			boolean hasBeenApplied);

	public List getAllFioLicenseAdminEventsForSerialNumber(String serialNumber);

	public void deleteAnyExistingFutureSuspendAdminEvents(String serialNumber);

	public void deleteAllAdminEvents(String serialNumber);

	public List<FioLicenseAdminEvent> getHasntBeenAppliedSuspendFioLicenseAdminEvents(
			Timestamp applicationTimestamp);
	
	public void update(FioLicenseAdminEvent fioLicense);

}
