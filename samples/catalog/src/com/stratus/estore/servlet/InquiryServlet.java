package com.stratus.estore.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.stratus.estore.salesforce.SalesforceSessionMgr;

public class InquiryServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(CatalogServlet.class.getName());
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher;
		
		try {
			SObject inquiry = new SObject();
			inquiry.setType("Inquiry__c");
			inquiry.setField("Name", request.getParameter("leadName"));
			inquiry.setField("Email__c", request.getParameter("leadEmail"));
			inquiry.setField("Product__c", request.getParameter("productId"));
			inquiry.setField("Brochure__c", request.getParameter("brochure") != null);
			inquiry.setField("Pricing__c", request.getParameter("pricing") != null);
			inquiry.setField("Reviews__c", request.getParameter("reviews") != null);
			SalesforceSessionMgr ssm = new SalesforceSessionMgr();
			ssm.setGlobalUsername(getServletContext().getInitParameter("salesforce.username"));
			ssm.setGlobalPassword(getServletContext().getInitParameter("salesforce.password"));
			PartnerConnection conn = ssm.getGlobalConnection();
			
			SaveResult[] results = conn.create(new SObject[]{ inquiry });
			if (results[0].getSuccess() == false) {
				dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/error.jsp");
				request.setAttribute("errorMsg", "Failed to publish inquiry to salesforce: " + results[0].getErrors()[0].getMessage());
				dispatcher.include(request, response);
				return;
			}
			
			dispatcher = getServletContext().getRequestDispatcher("/thanks.html");
			dispatcher.include(request, response);
			
		} catch (ConnectionException e) {
			dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/error.jsp");
			request.setAttribute("errorMsg", "Failed to connect to Salesforce: " + e.getMessage());
			dispatcher.include(request, response);
		}
	}
}
