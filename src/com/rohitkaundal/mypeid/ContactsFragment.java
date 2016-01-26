package com.rohitkaundal.mypeid;

import java.io.File;
import java.io.FileOutputStream;



import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ContactsFragment extends Fragment implements LoaderCallbacks<Cursor>{
	SimpleCursorAdapter mAdapter;
	MatrixCursor mMatrixCursor;
	Cursor cursorContactList = null;
	Loader loader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@Override
	public View onCreateView(LayoutInflater lf, ViewGroup vg, Bundle icicle) {
		return lf.inflate(R.layout.lv_layout_contacts, vg, false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.menu_inbox_messages, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_refresh:
			/*ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

			// Starting the AsyncTask process to retrieve and load listview with
			// contacts
			listViewContactsLoader.execute();*/
			loader.forceLoad();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityCreated(Bundle icicle) {
		super.onActivityCreated(icicle);
		ListView lstContacts = (ListView) getActivity().findViewById(
				R.id.lst_contacts);
		
		

		// The contacts from the contacts content provider is stored in this
		// cursor
		mMatrixCursor = new MatrixCursor(new String[] { "_id", "name", "photo",
				"details" });

		// Adapter to set data in the listview
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.lv_layout_contact_single, cursorContactList, new String[] { "name",
						"photo", "details" }, new int[] { R.id.tv_name,
						R.id.iv_photo, R.id.tv_details }, 0);

		lstContacts.setAdapter(mAdapter);
		

		// Setting the adapter to listview
		
		
	
		if(mAdapter.getCursor() == null)
		{
			// Creating an AsyncTask object to retrieve and load listview with
			// contacts
			/*ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();
			// Starting the AsyncTask process to retrieve and load listview with
			// contacts
			listViewContactsLoader.execute();*/
			
			
			//Loader kro refresh
			
			loader = getActivity().getLoaderManager().initLoader(0, icicle, this);
			loader.forceLoad();

		}
		
		
		

	}
	
	/*
	 * Loads contacts into given cursor
	 */
	class ContactsAsyncLoader extends AsyncTaskLoader<Cursor>{

		public ContactsAsyncLoader(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Cursor loadInBackground() {
			// TODO Auto-generated method stub
			Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

			// Querying the table ContactsContract.Contacts to retrieve all the
			// contacts
			Cursor contactsCursor = getActivity().getContentResolver().query(
					contactsUri, null, null, null,
					ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

			if (contactsCursor.moveToFirst()) {
				do {
					long contactId = contactsCursor.getLong(contactsCursor
							.getColumnIndex("_ID"));

					Uri dataUri = ContactsContract.Data.CONTENT_URI;

					// Querying the table ContactsContract.Data to retrieve
					// individual items like
					// home phone, mobile phone, work email etc corresponding to
					// each contact
					Cursor dataCursor = getActivity().getContentResolver()
							.query(dataUri,
									null,
									ContactsContract.Data.CONTACT_ID + "="
											+ contactId, null, null);

					String displayName = "";
					String nickName = "";
					String homePhone = "";
					String mobilePhone = "";
					String workPhone = "";
					String photoPath = "";
					byte[] photoByte = null;
					String homeEmail = "";
					String workEmail = "";
					String companyName = "";
					String title = "";

					if (dataCursor.moveToFirst()) {
						// Getting Display Name
						displayName = dataCursor
								.getString(dataCursor
										.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
						do {

							// Getting NickName
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
								nickName = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));

							// Getting Phone numbers
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
								switch (dataCursor.getInt(dataCursor
										.getColumnIndex("data2"))) {
								case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
									homePhone = dataCursor.getString(dataCursor
											.getColumnIndex("data1"));
									break;
								case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
									mobilePhone = dataCursor
											.getString(dataCursor
													.getColumnIndex("data1"));
									break;
								case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
									workPhone = dataCursor.getString(dataCursor
											.getColumnIndex("data1"));
									break;
								}
							}

							// Getting EMails
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
								switch (dataCursor.getInt(dataCursor
										.getColumnIndex("data2"))) {
								case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
									homeEmail = dataCursor.getString(dataCursor
											.getColumnIndex("data1"));
									break;
								case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
									workEmail = dataCursor.getString(dataCursor
											.getColumnIndex("data1"));
									break;
								}
							}

							// Getting Organization details
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
								companyName = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));
								title = dataCursor.getString(dataCursor
										.getColumnIndex("data4"));
							}

							// Getting Photo
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
								photoByte = dataCursor.getBlob(dataCursor
										.getColumnIndex("data15"));

								if (photoByte != null) {
									Bitmap bitmap = BitmapFactory
											.decodeByteArray(photoByte, 0,
													photoByte.length);

									// Getting Caching directory
									File cacheDirectory = getActivity()
											.getBaseContext().getCacheDir();

									// Temporary file to store the contact image
									File tmpFile = new File(
											cacheDirectory.getPath() + "/wpta_"
													+ contactId + ".png");

									// The FileOutputStream to the temporary
									// file
									try {
										FileOutputStream fOutStream = new FileOutputStream(
												tmpFile);

										// Writing the bitmap to the temporary
										// file as png file
										bitmap.compress(
												Bitmap.CompressFormat.PNG, 100,
												fOutStream);

										// Flush the FileOutputStream
										fOutStream.flush();

										// Close the FileOutputStream
										fOutStream.close();

									} catch (Exception e) {
										e.printStackTrace();
									}
									photoPath = tmpFile.getPath();
								}
							}
						} while (dataCursor.moveToNext());
						String details = "";

						// Concatenating various information to single string
						if (homePhone != null && !homePhone.equals(""))
							details = "HomePhone : " + homePhone + "\n";
						if (mobilePhone != null && !mobilePhone.equals(""))
							details += "MobilePhone : " + mobilePhone + "\n";
						if (workPhone != null && !workPhone.equals(""))
							details += "WorkPhone : " + workPhone + "\n";
						if (nickName != null && !nickName.equals(""))
							details += "NickName : " + nickName + "\n";
						if (homeEmail != null && !homeEmail.equals(""))
							details += "HomeEmail : " + homeEmail + "\n";
						if (workEmail != null && !workEmail.equals(""))
							details += "WorkEmail : " + workEmail + "\n";
						if (companyName != null && !companyName.equals(""))
							details += "CompanyName : " + companyName + "\n";
						if (title != null && !title.equals(""))
							details += "Title : " + title + "\n";

						// Adding id, display name, path to photo and other
						// details to cursor
						mMatrixCursor.addRow(new Object[] {
								Long.toString(contactId), displayName,
								photoPath, details });
					}
				} while (contactsCursor.moveToNext());
			}
			cursorContactList = mMatrixCursor;
			return mMatrixCursor;
		}
		
	}
	

	/** An AsyncTask class to retrieve and load listview with contacts */
	/*private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor> {

		ProgressDialog pd;

		@Override
		protected Cursor doInBackground(Void... params) {
			Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

			// Querying the table ContactsContract.Contacts to retrieve all the
			// contacts
			Cursor contactsCursor = getActivity().getContentResolver().query(
					contactsUri, null, null, null,
					ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

			if (contactsCursor.moveToFirst()) {
				do {
					long contactId = contactsCursor.getLong(contactsCursor
							.getColumnIndex("_ID"));

					Uri dataUri = ContactsContract.Data.CONTENT_URI;

					// Querying the table ContactsContract.Data to retrieve
					// individual items like
					// home phone, mobile phone, work email etc corresponding to
					// each contact
					Cursor dataCursor = getActivity().getContentResolver()
							.query(dataUri,
									null,
									ContactsContract.Data.CONTACT_ID + "="
											+ contactId, null, null);

					String displayName = "";
					String nickName = "";
					String homePhone = "";
					String mobilePhone = "";
					String workPhone = "";
					String photoPath = "";
					byte[] photoByte = null;
					String homeEmail = "";
					String workEmail = "";
					String companyName = "";
					String title = "";

					if (dataCursor.moveToFirst()) {
						// Getting Display Name
						displayName = dataCursor
								.getString(dataCursor
										.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
						do {

							// Getting NickName
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
								nickName = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));

							// Getting Phone numbers
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
								switch (dataCursor.getInt(dataCursor
										.getColumnIndex("data2"))) {
								case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
									homePhone = dataCursor.getString(dataCursor
											.getColumnIndex("data1"));
									break;
								case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
									mobilePhone = dataCursor
											.getString(dataCursor
													.getColumnIndex("data1"));
									break;
								case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
									workPhone = dataCursor.getString(dataCursor
											.getColumnIndex("data1"));
									break;
								}
							}

							// Getting EMails
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
								switch (dataCursor.getInt(dataCursor
										.getColumnIndex("data2"))) {
								case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
									homeEmail = dataCursor.getString(dataCursor
											.getColumnIndex("data1"));
									break;
								case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
									workEmail = dataCursor.getString(dataCursor
											.getColumnIndex("data1"));
									break;
								}
							}

							// Getting Organization details
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
								companyName = dataCursor.getString(dataCursor
										.getColumnIndex("data1"));
								title = dataCursor.getString(dataCursor
										.getColumnIndex("data4"));
							}

							// Getting Photo
							if (dataCursor
									.getString(
											dataCursor
													.getColumnIndex("mimetype"))
									.equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
								photoByte = dataCursor.getBlob(dataCursor
										.getColumnIndex("data15"));

								if (photoByte != null) {
									Bitmap bitmap = BitmapFactory
											.decodeByteArray(photoByte, 0,
													photoByte.length);

									// Getting Caching directory
									File cacheDirectory = getActivity()
											.getBaseContext().getCacheDir();

									// Temporary file to store the contact image
									File tmpFile = new File(
											cacheDirectory.getPath() + "/wpta_"
													+ contactId + ".png");

									// The FileOutputStream to the temporary
									// file
									try {
										FileOutputStream fOutStream = new FileOutputStream(
												tmpFile);

										// Writing the bitmap to the temporary
										// file as png file
										bitmap.compress(
												Bitmap.CompressFormat.PNG, 100,
												fOutStream);

										// Flush the FileOutputStream
										fOutStream.flush();

										// Close the FileOutputStream
										fOutStream.close();

									} catch (Exception e) {
										e.printStackTrace();
									}
									photoPath = tmpFile.getPath();
								}
							}
						} while (dataCursor.moveToNext());
						String details = "";

						// Concatenating various information to single string
						if (homePhone != null && !homePhone.equals(""))
							details = "HomePhone : " + homePhone + "\n";
						if (mobilePhone != null && !mobilePhone.equals(""))
							details += "MobilePhone : " + mobilePhone + "\n";
						if (workPhone != null && !workPhone.equals(""))
							details += "WorkPhone : " + workPhone + "\n";
						if (nickName != null && !nickName.equals(""))
							details += "NickName : " + nickName + "\n";
						if (homeEmail != null && !homeEmail.equals(""))
							details += "HomeEmail : " + homeEmail + "\n";
						if (workEmail != null && !workEmail.equals(""))
							details += "WorkEmail : " + workEmail + "\n";
						if (companyName != null && !companyName.equals(""))
							details += "CompanyName : " + companyName + "\n";
						if (title != null && !title.equals(""))
							details += "Title : " + title + "\n";

						// Adding id, display name, path to photo and other
						// details to cursor
						mMatrixCursor.addRow(new Object[] {
								Long.toString(contactId), displayName,
								photoPath, details });
					}
				} while (contactsCursor.moveToNext());
			}
			cursorContactList = mMatrixCursor;
			return mMatrixCursor;
		}

		@Override
		protected void onPostExecute(Cursor result) {
			// Setting the cursor containing contacts to listview
			pd.setProgress(100);
			pd.setMessage("Done");
			pd.dismiss();
			mAdapter.swapCursor(result);
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(getActivity());
			pd.setCancelable(false);
			pd.setMessage("Loading...");
			pd.setMax(100);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setProgress(0);
			pd.show();
		}
	}*/


	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new ContactsAsyncLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(arg1);
		mAdapter.notifyDataSetChanged();
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
