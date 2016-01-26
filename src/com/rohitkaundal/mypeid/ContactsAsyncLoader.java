package com.rohitkaundal.mypeid;
import java.io.File;
import java.io.FileOutputStream;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactsAsyncLoader extends AsyncTaskLoader<Cursor>{

	Cursor Data;
	protected MatrixCursor mMatrixCursor;
	public ContactsAsyncLoader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub
		mMatrixCursor = new MatrixCursor(new String[] { "_id", "name", "photo",
		"details" });
		Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

		// Querying the table ContactsContract.Contacts to retrieve all the
		// contacts
		Cursor contactsCursor = this.getContext().getContentResolver().query(
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
				Cursor dataCursor = this.getContext().getContentResolver()
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
								File cacheDirectory = this.getContext().getApplicationContext()
										.getCacheDir();

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
					/*if (homePhone != null && !homePhone.equals(""))
						details = "HomePhone : " + homePhone + "\n";*/
					if (mobilePhone != null && !mobilePhone.equals(""))
						details += /*"MobilePhone : " +*/ mobilePhone + "\n";
					if (workPhone != null && !workPhone.equals(""))
						details += /*"WorkPhone : " + */workPhone + "\n";
					/*if (nickName != null && !nickName.equals(""))
						details += "NickName : " + nickName + "\n";
					if (homeEmail != null && !homeEmail.equals(""))
						details += "HomeEmail : " + homeEmail + "\n";
					if (workEmail != null && !workEmail.equals(""))
						details += "WorkEmail : " + workEmail + "\n";
					if (companyName != null && !companyName.equals(""))
						details += "CompanyName : " + companyName + "\n";
					if (title != null && !title.equals(""))
						details += "Title : " + title + "\n";*/

					// Adding id, display name, path to photo and other
					// details to cursor
					mMatrixCursor.addRow(new Object[] {
							Long.toString(contactId), displayName,
							photoPath, details });
				}
				dataCursor.close();
			} while (contactsCursor.moveToNext());
			contactsCursor.close();
		}
		//cursorContactList = mMatrixCursor;
		return mMatrixCursor;
	}

	@Override
	public void deliverResult(Cursor data) {
		// TODO Auto-generated method stub
		
		if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (data != null) {
                onReleaseResources(data);
            }
        }
        Cursor oldData = data;
        Data = data;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldData != null) {
            onReleaseResources(oldData);
        }
	}

	protected void onReleaseResources(Cursor data2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onReset() {
		// TODO Auto-generated method stub
		super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (Data != null) {
            onReleaseResources(Data);
            Data = null;
        }
	}

	@Override
	protected void onStartLoading() {
		// TODO Auto-generated method stub
		if (Data != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(Data);
        }


        if (takeContentChanged() || Data == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
	}

	@Override
	protected void onStopLoading() {
		// TODO Auto-generated method stub
		cancelLoad();
	}
}