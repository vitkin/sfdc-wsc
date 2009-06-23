package com.jeffdouglas.model;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Random;

public class Opportunity {

	private String name;
	private double amount;
	private String stageName;
	private int probability;
	private String closeDate;
	private int orderNumber;
	private String accountId;
	
	public Opportunity() {
		// generate a random order number
		orderNumber = new Random().nextInt(1000);
		closeDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	public String getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	
}
