package com.rohitkaundal.mypeid;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MessageHolder implements Serializable {
	public String from,to,subject,message,date,folder;
	int imsgNumber;
	List<File> lstAttachments;
	
	public MessageHolder()
	{
		
	}
	
	public MessageHolder(int inMessageNumber, String from, String to, String subject, String message, String date, String folder)
	{
		this.imsgNumber = inMessageNumber;
		this.from=from;
		this.to=to;
		this.subject=subject;
		this.message=message;
		this.date=date;
		this.folder = folder;
		this.lstAttachments = new ArrayList<File>();
	}
	
	public void setAttachments(List<File> lstFiles)
	{
		this.lstAttachments = lstFiles;
	}
	
	public List<File> getAttachments(){
		return this.lstAttachments;
	}
	
	
}
