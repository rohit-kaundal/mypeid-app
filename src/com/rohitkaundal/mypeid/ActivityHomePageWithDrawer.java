package com.rohitkaundal.mypeid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityHomePageWithDrawer extends Activity implements
		TabListener, LoaderCallbacks<ArrayList<MailBoxFolder>> {

	private String mTitle = "";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList<MailBoxFolder> mbFolderList = new ArrayList<MailBoxFolder>();
	DrawerMenuLoaderAdapter drawer_adapter = null;
	Loader drawerLoader;
	Mail ml = new Mail();
	
	/*public ArrayList<MessageHolder> inboxfolder = new ArrayList<MessageHolder>();
	public InboxReaderAdapter msgAdapter;*/

	protected Fragment fmContacts = null;
	protected Fragment actInbox = null;
	
	

	String strusername = "dummyusr";
	String strpassword = "dummypwd";

	public Mail getMailObject() {
		return this.ml;
	}

	void refreshDrawer() {
		// DrawerMenuLoaderAdapter madapt =
		// (DrawerMenuLoaderAdapter)mDrawerList.getAdapter();

		drawer_adapter.notifyDataSetChanged();
		mDrawerList.invalidateViews();

	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(com.rohitkaundal.mypeid.R.layout.activity_homepage_withdrawer);

		getActionBar().setTitle("");

		Intent i = getIntent();

		strusername = i.getStringExtra("username");
		strpassword = i.getStringExtra("password");

		ml = new Mail(strusername, strpassword);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mbFolderList.add(new MailBoxFolder("Dummy Folder", 20));
		// Set the adapter for the list view
		drawer_adapter = new DrawerMenuLoaderAdapter(
				ActivityHomePageWithDrawer.this, R.layout.drawer_menu_item,
				mbFolderList);

		/*
		 * mAdapter.sort(new Comparator<MailBoxFolder>() {
		 * 
		 * @Override public int compare(MailBoxFolder arg0, MailBoxFolder arg1)
		 * { // TODO Auto-generated method stub return
		 * arg0.get_title().compareTo(arg1.get_title()); }
		 * 
		 * });
		 */

		View footerView = this.getLayoutInflater().inflate(
				R.layout.drawer_footer_menu, null);
		
		footerView.findViewById(R.id.tvContactSupportItem).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				contactSupportClicked();
			}
		});
		
		footerView.findViewById(R.id.tvSettingsItem).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				settingClicked();
			}
		});
		
		footerView.findViewById(R.id.tvLogoutItem).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logoutClicked();
				finish();
			}
		});
		
		
		mDrawerList.setSelector(android.R.drawable.list_selector_background);
		mDrawerList.addFooterView(footerView);
		mDrawerList.setAdapter(drawer_adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		/*
		 * mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		 * R.layout.drawer_list_item, mPlanetTitles));
		 */

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(drawerView);

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);

			}

			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
				// TODO Auto-generated method stub
				/*
				 * Toast.makeText(ActivityHomePageWithDrawer.this,
				 * item.getTitle(), Toast.LENGTH_SHORT).show();
				 */
				return super.onOptionsItemSelected(item);
			}

		};

		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Set the list's click listener
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);

		ActionBar ab = getActionBar();

		ab.setTitle("");

		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ab.addTab(ab.newTab().setText("Mails").setTabListener(this));
		// ab.addTab(ab.newTab().setText("Messages").setTabListener(this));
		ab.addTab(ab.newTab().setText("Contacts").setTabListener(this));

		/*
		
		*/
		/*
		 * drawerLoader = getLoaderManager().initLoader(0, icicle, this);
		 * drawerLoader.forceLoad();
		 */
		new DrawerMenuLoader().execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// new DrawerMenuLoader().execute();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		int pos = tab.getPosition();

		if (pos == 0) {

			if (actInbox == null) {

				actInbox = new ActivityMailsFromFolder();

				Bundle bdInbox = new Bundle();
				bdInbox.putString("username", strusername);
				bdInbox.putString("password", strpassword);
				bdInbox.putString("folder_name", "inbox");
				actInbox.setArguments(bdInbox);
			}
			ft.replace(android.R.id.tabcontent, actInbox);

		} else if (pos == 1) {
			if (fmContacts == null) {
				fmContacts = new ContactListFragment();
			}
			ft.replace(android.R.id.tabcontent, fmContacts);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Toast.makeText(ActivityHomePageWithDrawer.this, item.getTitle(),
		// Toast.LENGTH_SHORT).show();

		if (mDrawerToggle.onOptionsItemSelected(item)) {

			return true;
		}
		switch (item.getItemId()) {

		case android.R.id.home:
			/*
			 * Intent intent = new Intent(this, MainActivity.class);
			 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(intent); return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	/*
	 * class DrawerLoader extends AsyncTaskLoader<ArrayList<MailBoxFolder>>{
	 * 
	 * public DrawerLoader(Context context) { super(context); // TODO
	 * Auto-generated constructor stub }
	 * 
	 * @Override public ArrayList<MailBoxFolder> loadInBackground() { // TODO
	 * Auto-generated method stub
	 * 
	 * ArrayList<MailBoxFolder> tmpMailbox = new ArrayList<MailBoxFolder>(); try
	 * { tmpMailbox = ml.getFolderList(); } catch (Exception e) {
	 * Toast.makeText(ActivityHomePageWithDrawer.this, e.getLocalizedMessage(),
	 * Toast.LENGTH_LONG).show(); mbFolderList.add(new
	 * MailBoxFolder("Custom Inbox", 20)); }
	 * 
	 * if (!tmpMailbox.isEmpty()) { mbFolderList.clear(); mbFolderList =
	 * tmpMailbox; } else { mbFolderList.add(new MailBoxFolder("Custom Inbox",
	 * 20)); } return mbFolderList; }
	 * 
	 * }
	 */
	private class DrawerMenuLoader extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			ArrayList<MailBoxFolder> tmpMailbox = new ArrayList<MailBoxFolder>();
			try {
				tmpMailbox = ml.getFolderList();
			} catch (Exception e) {
				Toast.makeText(ActivityHomePageWithDrawer.this,
						e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
				mbFolderList.add(new MailBoxFolder("Custom Inbox", 20));
			}

			if (!tmpMailbox.isEmpty()) {
				mbFolderList.clear();
				mbFolderList = tmpMailbox;
			} else {
				mbFolderList.add(new MailBoxFolder("Custom Inbox", 20));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {

				Collections.sort(mbFolderList, new Comparator<MailBoxFolder>() {

					@Override
					public int compare(MailBoxFolder arg0, MailBoxFolder arg1) {
						//
						return arg0.get_title().compareTo(arg1.get_title());
					}

				});

				refreshDrawer();

				drawer_adapter = new DrawerMenuLoaderAdapter(
						ActivityHomePageWithDrawer.this,
						R.layout.drawer_menu_item, mbFolderList);
				mDrawerList.setAdapter(drawer_adapter);
				/*
				 * TextView tvHelp = new
				 * TextView(ActivityHomePageWithDrawer.this);
				 * tvHelp.setText("Help"); mDrawerList.addView(tvHelp);
				 */
			} catch (Exception e) {
				// TODO: handle exception

				Toast.makeText(ActivityHomePageWithDrawer.this,
						e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {

			selectItem(position, view);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position, View v) {

		MailBoxFolder mbFolder = drawer_adapter.getItem(position);
		String strFolderName = mbFolder.get_fullname();
		// Toast.makeText(ActivityHomePageWithDrawer.this, strFolderName,
		// Toast.LENGTH_SHORT).show();
		// Create a new fragment and specify the planet to show based on
		// position
		

		if (mbFolder.get_count() > 0) {
			try {
				Fragment fragment = new ActivityMailsFromFolder();
				Bundle args = new Bundle();
				args.putString("username", strusername);
				args.putString("password", strpassword);
				args.putString("folder_name", strFolderName);
				fragment.setArguments(args);

				// Insert the fragment by replacing any existing fragment
				android.app.FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(android.R.id.tabcontent, fragment).commit();

				// get text
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			Toast.makeText(ActivityHomePageWithDrawer.this, "Empty folder...",
					Toast.LENGTH_SHORT).show();
		}

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		// setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title.toString();
		getActionBar().setTitle(mTitle);
	}

	@Override
	public Loader<ArrayList<MailBoxFolder>> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		// return new DrawerLoader(ActivityHomePageWithDrawer.this);
		return null;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<MailBoxFolder>> arg0,
			ArrayList<MailBoxFolder> arg1) {
		// TODO Auto-generated method stub
		drawer_adapter.clear();
		drawer_adapter.addAll(arg1);
		drawer_adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<MailBoxFolder>> arg0) {
		// TODO Auto-generated method stub

	}

	// Methods for drawer menu footer custom

	void settingClicked() {
		Toast.makeText(this, "Clicked on settings...", Toast.LENGTH_SHORT)
				.show();
	}

	void logoutClicked() {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		finish();
	}

	void contactSupportClicked() {
		Intent i = new Intent(this, ActivityComposeEmail.class);
		i.putExtra("user_name", ml.getUser());
		i.putExtra("user_password", ml.getPass());
		i.putExtra("email_to", "support@mypeid.com");
		startActivity(i);
	}
	
	
	
}
