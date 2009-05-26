package com.stratus.estore.salesforce;

import java.util.logging.Logger;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.stratus.estore.servlet.CatalogServlet;

public class SalesforceSessionMgr {
	private static final Logger log = Logger.getLogger(CatalogServlet.class.getName());
	
	private static final int DEFAULT_CACHE_LENGTH = 100; // 100 minutes
	
	private int cacheLength = DEFAULT_CACHE_LENGTH;
	
	public int getCacheLength() {
		return cacheLength;
	}

	public void setCacheLength(int cacheLength) {
		this.cacheLength = cacheLength;
	}

	private String globalUsername;
	
	public String getGlobalUsername() {
		return globalUsername;
	}

	public void setGlobalUsername(String globalUsername) {
		this.globalUsername = globalUsername;
	}

	private String globalPassword;

	public String getGlobalPassword() {
		return globalPassword;
	}

	public void setGlobalPassword(String globalPassword) {
		this.globalPassword = globalPassword;
	}

	public PartnerConnection getGlobalConnection() throws ConnectionException {
		return getConnection(globalUsername, globalPassword);
	}
	
	public PartnerConnection getConnection(String username, String password) throws ConnectionException {
		SalesforceSessionCacheEntry cacheEntry = getSessionFromCache(username, password);
		
		PartnerConnection conn = null;
		ConnectorConfig config = new ConnectorConfig();
		
		if (cacheEntry != null) {
			config.setSessionId(cacheEntry.getSessionId());
			config.setServiceEndpoint(cacheEntry.getServerUrl());
			conn = Connector.newConnection(config);
		} else {
			config.setUsername(username);
			config.setPassword(password);
			conn = Connector.newConnection(config);
			storeSessionInCache(conn, username, password);
		}
		
		return conn;
	}
	
	private SalesforceSessionCacheEntry getSessionFromCache(String username, String password) {
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		
		String key = createCacheKey(username, password);
		
		if (memcache.contains(key)) {
			return (SalesforceSessionCacheEntry) memcache.get(key);
		} else {
			return null;
		}
	}
	
	private void storeSessionInCache(PartnerConnection conn, String username, String password) {
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		
		String key = createCacheKey(username, password);
		
		SalesforceSessionCacheEntry cacheEntry = new SalesforceSessionCacheEntry();
		cacheEntry.setSessionId(conn.getConfig().getSessionId());
		cacheEntry.setServerUrl(conn.getConfig().getServiceEndpoint());

		memcache.put(key, cacheEntry, Expiration.byDeltaSeconds(cacheLength * 60));
	}
	
	private String createCacheKey(String username, String password) {
		return username + password;
	}
}
