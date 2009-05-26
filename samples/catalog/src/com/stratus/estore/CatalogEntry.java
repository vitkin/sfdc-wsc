package com.stratus.estore;

import com.sforce.soap.partner.sobject.SObject;

public class CatalogEntry {
	private String id;
	private String imageUrl;
	private String description;
	
	public static CatalogEntry fromSObject(SObject sobj) {
		CatalogEntry ce = new CatalogEntry();
		ce.setId((String) sobj.getField("Id"));
		ce.setImageUrl((String) sobj.getField("ImageUrl__c"));
		ce.setDescription((String) sobj.getField("Description__c"));
		return ce;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
