package com.groupfio.dao;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.groupfio.message.pojo.Message;
import com.groupfio.model.FioLicense;

@Repository
@Transactional
public class FioLicenseActionUpdateDAOImpl implements FioLicenseActionUpdateDAO {

	@Autowired
	private SessionFactory session;

	@Override
	public FioLicense getFioLicence(String serialnumber) {
		return (FioLicense) session.getCurrentSession().get(FioLicense.class,
				serialnumber);
	}

	@Override
	public void updateFioLicenseForAgentActionResult(Message message) {
		FioLicense fioLicense = getFioLicence(message.getSerialNumber());
		fioLicense.setLastAgentComTime(new Timestamp(message.getTimestamp()));
		fioLicense.setLastAgentComAction(message.getAction());
		fioLicense.setLastAgentComActionResult(message.getActionMsg());
		session.getCurrentSession().update(fioLicense);
		
	}
	
	

}
