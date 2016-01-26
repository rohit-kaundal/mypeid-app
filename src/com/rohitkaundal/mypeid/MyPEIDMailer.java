package com.rohitkaundal.mypeid;

public class MyPEIDMailer {
	
	private String username = "";
	private String userpass = "";
	
	private final String protocol = "imaps";
	
	public MyPEIDMailer( String username, String password )
	{
		this.username = username;
		this.userpass = password;
	}
	
	private void initMail()
	{
		Mail m = new Mail();
		
		return;
	}
}
