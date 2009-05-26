package com.stratus.estore.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.stratus.estore.CatalogEntry;
import com.stratus.estore.salesforce.SalesforceSessionMgr;

public class CatalogServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(CatalogServlet.class.getName());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher;
		
		try {
			SalesforceSessionMgr ssm = new SalesforceSessionMgr();
			ssm.setGlobalUsername(getServletContext().getInitParameter("salesforce.username"));
			ssm.setGlobalPassword(getServletContext().getInitParameter("salesforce.password"));
			PartnerConnection conn = ssm.getGlobalConnection();
			
			QueryResult qr = conn.query("SELECT Id, Description__c, ImageUrl__c FROM CatalogEntry__c ORDER BY CreatedDate");
			int numRecords = qr.getSize();
			SObject[] records = qr.getRecords();
			CatalogEntry[] entries = new CatalogEntry[numRecords];
			for (int i=0; i<numRecords; i++) {
				entries[i] = CatalogEntry.fromSObject(records[i]);
			}
			
			dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/catalog.jsp");
			request.setAttribute("entries", entries);
			dispatcher.include(request, response);
			
		} catch (ConnectionException e) {
			dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/error.jsp");
			request.setAttribute("errorMsg", "Failed to connect to Salesforce: " + e.getMessage());
			dispatcher.include(request, response);
		}
	}
}
