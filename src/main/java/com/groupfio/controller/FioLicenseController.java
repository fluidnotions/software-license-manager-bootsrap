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
import com.groupfio.model.FioLicenseFormBean;
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
		FioLicenseFormBean fioLicense = new FioLicenseFormBean();
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
	public String doActions(@ModelAttribute FioLicenseFormBean fioLicenseFormBean,
			BindingResult result, @RequestParam String action,
			Map<String, Object> map, Principal principal) {
		
		FioLicenseFormBean fioLicenseFormBeanResult = new FioLicenseFormBean();
		//since this field doubles as extend input we don't 
		//want to have it in the result where it can be resubmitted
		//by mistake - except this doesn't help the issue - o well
		fioLicenseFormBeanResult.setValidityPeriodString("NONE");
		//this a transient field to be used in logging admin evnent
		if(principal!=null){
			fioLicenseFormBean.setByUsername(principal.getName().trim());
		}else log.error("doActions principal is null!");
		
		switch (action.toLowerCase()) { // only in Java7 you can put String in
										// switch
		case "create":
			fioLicenseService.create(fioLicenseFormBean);
			fioLicenseFormBeanResult = fioLicenseFormBean;
			break;
		case "update":
			fioLicenseService.update(fioLicenseFormBean);
			
			fioLicenseFormBeanResult = fioLicenseFormBean;
			break;
		case "delete":
			fioLicenseService.delete(fioLicenseFormBean.getSerialNumber());
			break;
		case "search":
			FioLicenseFormBean searchedFioLicenseFormBean = fioLicenseService
					.getFioLicenseFormBean(fioLicenseFormBean.getSerialNumber());
			fioLicenseFormBeanResult = searchedFioLicenseFormBean != null ? searchedFioLicenseFormBean 
					: fioLicenseFormBeanResult;
			break;
		}
		//get associated FioLicenseAdminEvent entities and add as list if there are any
		map.put("fioLicenseAdminEvents", fioLicenseService.getAllFioLicenseAdminEventsForSerialNumber(fioLicenseFormBean.getSerialNumber()));
		map.put("validityPeriodOptionList", validityPeriodOptionList);
		map.put("fioLicense", fioLicenseFormBeanResult);
		
		return "fiolicense";
	}

	
	
	

}
