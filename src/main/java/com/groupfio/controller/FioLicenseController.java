package com.groupfio.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.groupfio.model.FioLicense;
import com.groupfio.service.FioLicenseService;

@Controller
public class FioLicenseController {
	
	private static final Log log = LogFactory
			.getLog(FioLicenseController.class);
	private final String[] validityPeriodOptionList = new String[]{"6 months", "1 year", "2 years"};

	@Autowired
	private FioLicenseService fioLicenseService;

	@RequestMapping("/fiolicense")
	public String setupForm(Map<String, Object> map) {
		map.put("validityPeriodOptionList", validityPeriodOptionList);
		FioLicense fioLicense = new FioLicense();
		map.put("fioLicense", fioLicense);
		return "fiolicense";
	}
	
	@RequestMapping(value ="/validityPeriodOptionList", method = RequestMethod.GET)
	public @ResponseBody List<String> getValidityPeriodOptionList(Map<String, Object> map) {
		return Arrays.asList(validityPeriodOptionList);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAllFioLicense.json", method = RequestMethod.GET)
	public @ResponseBody List<FioLicense> getAllFioLicenseAsAjax() {
		List<FioLicense> list = fioLicenseService.getAllFioLicense();
		log.debug("getAllFioLicenseAsAjax: list.size: "+list.size());
	    return list;
	}

	@RequestMapping(value = "/fioLicense.do", method = RequestMethod.POST)
	public String doActions(@ModelAttribute FioLicense fioLicense,
			BindingResult result, @RequestParam String action,
			Map<String, Object> map, Principal principal) {
		
		FioLicense fioLicenseResult = new FioLicense();
		//this a transient field to be used in logging admin evnent
		fioLicenseResult.setByUsername(principal.getName());
		switch (action.toLowerCase()) { // only in Java7 you can put String in
										// switch
		case "create":
			fioLicenseService.create(fioLicense);
			fioLicenseResult = fioLicense;
			break;
		case "update":
			fioLicenseService.update(fioLicense);
			fioLicenseResult = fioLicense;
			break;
		case "delete":
			fioLicenseService.delete(fioLicense.getSerialNumber());
			fioLicenseResult = new FioLicense();
			break;
		case "search":
			FioLicense searchedFioLicense = fioLicenseService
					.getFioLicense(fioLicense.getSerialNumber());
			fioLicenseResult = searchedFioLicense != null ? searchedFioLicense
					: new FioLicense();
			break;
		}
		//get associated FioLicenseAdminEvent entities and add as list if there are any
		map.put("fioLicenseAdminEvents", fioLicenseService.getAllFioLicenseAdminEventsForSerialNumber(fioLicense.getSerialNumber()));
		map.put("validityPeriodOptionList", validityPeriodOptionList);
		map.put("fioLicense", fioLicenseResult);
		
		return "fiolicense";
	}

	
	
	

}
