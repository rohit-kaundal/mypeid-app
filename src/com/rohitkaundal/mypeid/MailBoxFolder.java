package com.rohitkaundal.mypeid;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MailBoxFolder implements Serializable {
	private String _title;
	private int _count;

	public String get_title() {
		return _title;
	}

	public void set_title(String _title) {
		this._title = _title;
	}

	public int get_count() {
		return _count;
	}

	public void set_count(int _count) {
		this._count = _count;
	}

	public MailBoxFolder(String _title, int _count) {
		super();
		this._title = _title;
		this._count = _count;
	}

	public MailBoxFolder() {

	}
	
	private String _fullname; 
	public void set_fullname(String fullName) {
		// TODO Auto-generated method stub
		this._fullname = fullName;
	}
	
	public String get_fullname() {
		return this._fullname;
	}

}
