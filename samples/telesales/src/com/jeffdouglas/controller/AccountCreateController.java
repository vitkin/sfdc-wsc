package com.jeffdouglas.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.soap.partner.SaveResult; 
import com.sforce.soap.partner.sobject.SObject;

import com.jeffdouglas.model.*;
import com.jeffdouglas.service.ConnectionManager;

public class AccountCreateController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
	protected ModelAndView onSubmit(HttpServletRequest request, 
			HttpServletResponse response, Object command,
			BindException errors) throws Exception {
		
		// cast the posted command data as an account
		Account cmd = (Account) command;
		SaveResult[] results = null;
		
        // populate the new account
        SObject a = new SObject();
        a.setType("Account");
        a.setField("Name", cmd.getName());
        a.setField("BillingCity", cmd.getCity());
        a.setField("BillingState", cmd.getState());
        a.setField("Phone", cmd.getPhone());
        a.setField("Website", cmd.getWebsite());
        
        SObject[] accounts = {a};
        
		// get a reference to the connection
		PartnerConnection connection = ConnectionManager.getConnectionManager().getConnection();

		try {
			results = connection.create(accounts);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		// TODO Check for errors in results
		
		ModelAndView modelAndView = new ModelAndView(getSuccessView());
		modelAndView.addObject("accountId", results[0].getId());

        return modelAndView;
    }
}