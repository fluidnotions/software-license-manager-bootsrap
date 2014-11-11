package com.groupfio.controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
	
	private static final Log logger = LogFactory
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
		logger.debug("getAllFioLicenseAsAjax: list.size: "+list.size());
	    return list;
	}

	@RequestMapping(value = "/fioLicense.do", method = RequestMethod.POST)
	public String doActions(@ModelAttribute FioLicense fioLicense,
			BindingResult result, @RequestParam String action,
			Map<String, Object> map) {
		
		FioLicense fioLicenseResult = new FioLicense();
		switch (action.toLowerCase()) { // only in Java7 you can put String in
										// switch
		case "add":
			//convert form types to entity types
			convertFormDataToEntityValues(fioLicense);
			fioLicenseService.add(fioLicense);
			fioLicenseResult = fioLicense;
			break;
		case "edit":
			//convert form types to entity types
			convertFormDataToEntityValues(fioLicense);
			fioLicenseService.edit(fioLicense);
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
		map.put("validityPeriodOptionList", validityPeriodOptionList);
		map.put("fioLicense", fioLicenseResult);
		
		return "fiolicense";
	}

	private void convertFormDataToEntityValues(FioLicense fioLicense) {
		String activationDateString = fioLicense.getActivationDateString();
		if (activationDateString != null) {
			logger.debug("activationDateString: " + activationDateString);
			// INFO : com.groupfio.controller.FioLicenseController -
			// activationDateString: 10/28/2014
			// s - timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]. The
			// fractional seconds may be omitted. The leading zero for mm and dd
			// may also be omitted.
			fioLicense
					.setActivationDate(covertFormFieldStringToTimestamp(activationDateString));
			String validityPeriodString = fioLicense.getValidityPeriodString();
			logger.debug("validityPeriodString: " + validityPeriodString);
			switch (validityPeriodString) {
			case "6 months":
				fioLicense.setExpirationDate(addMonthsToTimestamp(6,
						fioLicense.getActivationDate()));
				break;
			case "1 year":
				fioLicense.setExpirationDate(addMonthsToTimestamp(12,
						fioLicense.getActivationDate()));
				break;
			case "2 years":
				fioLicense.setExpirationDate(addMonthsToTimestamp(24,
						fioLicense.getActivationDate()));
				break;
			}
		} else {
			logger.debug("activationDateString is null");
		}

	}

	private Timestamp addMonthsToTimestamp(int months, Timestamp ts) {
		logger.debug("months to add: "+months);
		int years = 0;
		if(months>12){
			years = months/12;
			logger.debug("months/12: "+years);
			months = months%12;
			logger.debug("remainder months: "+months);
			
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getDefault());
		cal.setTime(ts);
		cal.add(Calendar.MONTH, months);
		if(years>0){
			cal.add(Calendar.YEAR, years);
		}
		Timestamp adjusted = new Timestamp(cal.getTime().getTime());
		logger.debug("initial: "+ts.toString()+", adjusted: "+adjusted.toString());
		return adjusted;
	}

	private Timestamp covertFormFieldStringToTimestamp(String activationDateString){
		//INFO : com.groupfio.controller.FioLicenseController - activationDateString: 10/30/2014
		//INFO : com.groupfio.controller.FioLicenseController - std timestamp formatted date: 2014-01-30 00:10:00.000
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Timestamp ts = null;
		try {
			Date d = formatter.parse(activationDateString);
			((SimpleDateFormat) formatter).applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
			String newDateString = formatter.format(d);
			logger.debug("std timestamp formatted date: "+newDateString);

			ts = Timestamp.valueOf(newDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ts;
	}
	
	

}
