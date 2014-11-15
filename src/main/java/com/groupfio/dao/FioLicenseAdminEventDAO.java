package com.groupfio.dao;

import java.sql.Timestamp;

public interface FioLicenseAdminEventDAO {

	public void logAdminEvent(String serialNumber, 
			String eventType,
			String eventDetails, 
			String eventComment,
			Timestamp applicationTimestamp,
			String byUsername, 
			boolean hasBeenApplied);

	void deleteAnyExistingFutureSuspendAdminEvents();
}
