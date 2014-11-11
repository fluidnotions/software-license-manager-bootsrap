package com.groupfio.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groupfio.dao.FioLicenseDAO;
import com.groupfio.model.FioLicense;

@Service
public class FioLicenseServiceImpl implements FioLicenseService {

	@Autowired
	private FioLicenseDAO fioLicenseDAO;

	@Transactional
	public void add(FioLicense fioLicense) {
		fioLicenseDAO.add(fioLicense);

	}

	@Transactional
	public void edit(FioLicense fioLicense) {
		fioLicenseDAO.edit(fioLicense);

	}

	@Transactional
	public void delete(String serialnumber) {
		fioLicenseDAO.delete(serialnumber);

	}

	@Transactional
	public FioLicense getFioLicense(String serialnumber) {
		return fioLicenseDAO.getFioLicence(serialnumber);
	}

	@Transactional
	public List getAllFioLicense() {
		return fioLicenseDAO.getAllFioLicence();
	}

}
