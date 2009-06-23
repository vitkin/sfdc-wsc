package com.jeffdouglas.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.soap.partner.SaveResult; 
import com.sforce.soap.partner.sobject.SObject;

import com.jeffdouglas.model.*;
import com.jeffdouglas.service.ConnectionManager;

public class OpportunityCreateController extends SimpleFormController {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    protected ModelAndView showForm(HttpServletRequest request, 
    		HttpServletResponse response, 
    		BindException errors) throws Exception {
    	
		// add the account id to the array to be retrieved
		String[] accountIds = { request.getParameter("accountId") };
		SObject[] accounts = null;
		
		// get a reference to the connection
		PartnerConnection connection = ConnectionManager.getConnectionManager().getConnection();
		
		try {
			accounts = connection.retrieve("Id, Name, Phone, BillingCity, BillingState, Website","Account", accountIds);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    	
    	ModelAndView modelAndView = super.showForm(request, response, errors);
        modelAndView.addObject("accountName", accounts[0].getField("Name"));
        return modelAndView;   
     } 

    
	protected ModelAndView onSubmit(HttpServletRequest request, 
			HttpServletResponse response, Object command,
			BindException errors) throws Exception {
		
		Opportunity cmd = (Opportunity) command;
		
		SaveResult[] results = null;
			        		
        // populate the new opportunity
        SObject opp = new SObject();
        opp.setType("Opportunity");
        opp.setField("Name", cmd.getName());
        opp.setField("Amount", new Double(cmd.getAmount()));
        opp.setField("StageName", cmd.getStageName());
        opp.setField("Probability", new Double(cmd.getProbability()));
        opp.setField("CloseDate", new SimpleDateFormat("yyyy-MM-dd").parse(cmd.getCloseDate()));
        opp.setField("OrderNumber__c", String.valueOf(cmd.getOrderNumber()));
        opp.setField("AccountId", cmd.getAccountId());
        
        SObject[] opportunities = {opp};
        
		// get a reference to the connection
		PartnerConnection connection = ConnectionManager.getConnectionManager().getConnection();
		
		try {
			results = connection.create(opportunities);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		// TODO Check for errors in results
		
		ModelAndView modelAndView = new ModelAndView(getSuccessView());
		modelAndView.addObject("accountId", request.getParameter("accountId"));

        return modelAndView;
    }
}