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
import com.sforce.ws.ConnectionException;
import com.stratus.estore.CatalogEntry;
import com.stratus.estore.salesforce.SalesforceSessionMgr;

public class MoreInfoServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(CatalogServlet.class.getName());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher;
		
		String entryId = request.getParameter("id");
		try {
			SalesforceSessionMgr ssm = new SalesforceSessionMgr();
			ssm.setGlobalUsername(getServletContext().getInitParameter("salesforce.username"));
			ssm.setGlobalPassword(getServletContext().getInitParameter("salesforce.password"));
			PartnerConnection conn = ssm.getGlobalConnection();
			
			String soql = String.format("SELECT Id, Description__c, ImageUrl__c FROM CatalogEntry__c WHERE Id = '%s'", entryId);
			QueryResult qr = conn.query(soql);
			CatalogEntry entry = CatalogEntry.fromSObject(qr.getRecords()[0]);
			
			dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/moreinfo.jsp");
			request.setAttribute("entry", entry);
			dispatcher.include(request, response);
			
		} catch (ConnectionException e) {
			dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsp/error.jsp");
			request.setAttribute("errorMsg", "Failed to connect to Salesforce: " + e.getMessage());
			dispatcher.include(request, response);
		}
	}
}
