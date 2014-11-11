package com.groupfio.dao;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.groupfio.model.FioLicense;
import com.groupfio.pojo.ActionResult;

@Repository
public class FioLicenseDAOImpl implements FioLicenseDAO {

	@Autowired
	private SessionFactory session;

	/*
	 * private SessionFactory session;
	 * 
	 * public void setSessionFactory(SessionFactory sf){ this.session = sf; }
	 */

	@Override
	public void add(FioLicense fioLicense) {
		session.getCurrentSession().save(fioLicense);

	}

	@Override
	public void edit(FioLicense fioLicense) {
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
	public void updateFioLicenseForAgentActionResult(ActionResult actionResult) {
		FioLicense fioLicense = getFioLicence(actionResult.getSerialNumber());
		fioLicense.setLastAgentComTime(new Timestamp(actionResult.getTimestamp()));
		fioLicense.setLastAgentComAction(actionResult.getAction());
		fioLicense.setLastAgentComActionResult(actionResult.getActionResultMsg());
		session.getCurrentSession().update(fioLicense);
		
	}

}
