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
public class FioLicenseDAOImpl implements FioLicenseDAO {

	@Autowired
	private SessionFactory session;

	/*
	 * private SessionFactory session;
	 * 
	 * public void setSessionFactory(SessionFactory sf){ this.session = sf; }
	 */

	@Override
	public void create(FioLicense fioLicense) {
		session.getCurrentSession().save(fioLicense);

	}

	@Override
	public void update(FioLicense fioLicense) {
		session.getCurrentSession().update(fioLicense);

	}

	@Override
	public void delete(String serialnumber) {
		session.getCurrentSession().delete(getFioLicence(serialnumber));
		;

	}

	@Override
	public FioLicense getFioLicence(String serialnumber) {
		return (FioLicense) session.getCurrentSession().get(FioLicense.class,
				serialnumber);
	}

	@Override
	public List getAllFioLicence() {
		return session.getCurrentSession().createQuery("from FioLicense")
				.list();
	}

	@Override
	public void updateFioLicenseForAgentActionResult(Message message) {
		FioLicense fioLicense = getFioLicence(message.getSerialNumber());
		fioLicense.setLastAgentComTime(new Timestamp(message.getTimestamp()));
		fioLicense.setLastAgentComAction(message.getAction());
		fioLicense.setLastAgentComActionResult(message.getActionMsg());
		session.getCurrentSession().update(fioLicense);
		
	}
	
	@Override
	public Timestamp getFioLicenceCurrentExpiryDate(String serialNumber){//statelessSession needs to be used here else 
		Query q = session.openStatelessSession().createQuery("from FioLicense where serialNumber = :serialNumber");
		q.setString("serialNumber", serialNumber);
		FioLicense fioLicense = (FioLicense) q.uniqueResult();
		return fioLicense.getExpirationDate();
	}
	
	@Override
	public boolean getFioLicenceCurrentIsEnabled(String serialNumber){//statelessSession needs to be used here else 
		Query q = session.openStatelessSession().createQuery("from FioLicense where serialNumber = :serialNumber");
		q.setString("serialNumber", serialNumber);
		FioLicense fioLicense = (FioLicense) q.uniqueResult();
		return fioLicense.isEnabled();
	}

}
