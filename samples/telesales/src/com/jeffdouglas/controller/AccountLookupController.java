package com.jeffdouglas.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jeffdouglas.model.*;
import com.jeffdouglas.service.ConnectionManager;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.ws.ConnectionException;

public class AccountLookupController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

	protected ModelAndView onSubmit(HttpServletRequest request, 
			HttpServletResponse response, Object command,
			BindException errors) throws Exception {
		
		Account cmd = (Account) command;
				
		// get a reference to the connection
		PartnerConnection connection = ConnectionManager.getConnectionManager().getConnection();
		
		QueryResult result = null;
		try {
			result = connection.query("Select Id, Name, Phone, BillingCity, BillingState from Account Where Name = '"+cmd.getName()+"'");
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("Records found for "+cmd.getName()+": "+result.getSize());
		
		ModelAndView modelAndView = new ModelAndView("accountLookup");
		modelAndView.addObject("accounts", result.getRecords());

        return modelAndView;
    }
}