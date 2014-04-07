package de.shoppinglist.android.bean;

import java.sql.Timestamp;
import java.util.Date;

public class Shoppinglist extends BusinessBean {

	private Timestamp finishedTime;

	private Timestamp createdTime;
	
	public Shoppinglist(){
		Date creationDate = new Date();
		this.createdTime = new Timestamp(creationDate.getTime());
	}
	
	public void finish(){
		Date finishedDate = new Date();
		this.finishedTime = new Timestamp(finishedDate.getTime());
	}

	public Timestamp getFinishedTime() {
		return finishedTime;
	}

	public void setFinishedTime(Timestamp finishedTime) {
		this.finishedTime = finishedTime;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
}
