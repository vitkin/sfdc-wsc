package com.jeffdouglas.controller;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jeffdouglas.service.ConnectionManager;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.ws.ConnectionException;
import com.sforce.soap.partner.sobject.SObject;

public class AccountDisplayController implements Controller {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

		// add the account id to the array to be retrieved
		String[] accountIds = { request.getParameter("accountId") };
		
		SObject[] accounts = null;
		QueryResult result = null;
		
		// get a reference to the connection
		PartnerConnection connection = ConnectionManager.getConnectionManager().getConnection();
		
		try {
			accounts = connection.retrieve("Id, Name, Phone, BillingCity, BillingState, Website","Account", accountIds);
			result = connection.query("Select id, name, stagename, amount, closeDate, probability, ordernumber__c from Opportunity where AccountId = '"+request.getParameter("accountId")+"'");
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 	
    	
		ModelAndView modelAndView = new ModelAndView("accountDisplay");
		modelAndView.addObject("account", accounts[0]);
		modelAndView.addObject("opportunities", result.getRecords());		

        return modelAndView;
    }
}