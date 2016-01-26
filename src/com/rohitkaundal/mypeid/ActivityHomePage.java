package com.rohitkaundal.mypeid;

import android.R;
import android.app.ActionBar.Tab;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class ActivityHomePage extends Activity implements TabListener {
	
	protected ContactsFragment fmContacts = null;
	protected ActivityInbox actInbox = null;
	
	
	String strusername = "dummyusr";
	String strpassword = "dummypwd";
	
	
	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		
		Intent i = getIntent();
		
		strusername = i.getStringExtra("username");
		strpassword = i.getStringExtra("password");
		
		ActionBar ab = getActionBar();
		
		ab.setTitle("MyPEID");
		ab.setDisplayHomeAsUpEnabled(true);
		
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ab.addTab(ab.newTab().setText("Inbox").setTabListener(this));
		ab.addTab(ab.newTab().setText("Messages").setTabListener(this));
		ab.addTab(ab.newTab().setText("Contacts").setTabListener(this));
		
		
		
		/*
		
		*/
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		int pos = tab.getPosition();
		
		if(pos == 0){
			
				actInbox = new ActivityInbox();
				Bundle bdInbox = new Bundle();
				bdInbox.putString("username", strusername);
				bdInbox.putString("password", strpassword);
				actInbox.setArguments(bdInbox);
			
			
			ft.replace(android.R.id.content, actInbox);
			
		}else if (pos == 2)
		{
			if(fmContacts == null){
				fmContacts = new ContactsFragment();
			}
			ft.replace(R.id.content, fmContacts);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
	  switch (item.getItemId()) {
	  		case android.R.id.home:
	  		/*	Intent intent = new Intent(this, MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	  			return true;
	  			*/
	  		default:
	  			return super.onOptionsItemSelected(item); 
	    }
	}

	

}
