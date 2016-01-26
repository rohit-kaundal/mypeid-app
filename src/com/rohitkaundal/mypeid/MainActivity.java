package com.rohitkaundal.mypeid;

import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	String userName, password;
	Account mypeidAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		findViewById(R.id.btnLogin).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// Toast.makeText(MainActivity.this,
						// "login clicked",Toast.LENGTH_SHORT).show();
						Intent in = new Intent(MainActivity.this,
								ActivityPassword.class);

						/* String strUsername = txtPhone.getText().toString(); */

						/*
						 * if( !strUsername.isEmpty()){ in.putExtra("username",
						 * strUsername+"@mypeid.com");
						 */
						startActivity(in);
						/*
						 * }else{ //Toast.makeText(MainActivity.this,
						 * "Please enter valid phone number",
						 * Toast.LENGTH_SHORT).show();
						 * txtPhone.setError("Please enter phone number");
						 * txtPhone.requestFocus(); }
						 */

					}
				});

		findViewById(R.id.btnRegister).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startActivity(new Intent(MainActivity.this,
								ActivityRegisterA.class));
					}
				});

		doLogin();

	}

	public void doLogin() {
		/*SharedPreferences preferences = getBaseContext().getSharedPreferences(
				"MyPrefs", MODE_PRIVATE);

		userName = preferences.getString("username", null);
		password = preferences.getString("password", null);*/
		
		try {
			Log.e("mypeid-am", "initing..");
			Account mypeidAccount;
			AccountManager am = AccountManager.get(MainActivity.this);
			
			Account[] tmp  = am.getAccountsByType(LoginAuth.ARG_ACCOUNT_TYPE);
			
			if(tmp.length > 0){
				mypeidAccount = tmp[0];
				userName = mypeidAccount.name;
				password = am.getPassword(mypeidAccount);
				Log.e("mypeid-am", userName);
				Log.e("mypeid-am", password);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

		// Then you want to query preferences for values such as username and
		// password like
		if ((userName != null) && (password != null)) {
			// **** Log your user into your application auto**Magically**
			// ********

			// ------------------- Option 1 - web request -------------------

			// First i Would make the initial Web Request before trying to send
			// my User into
			// a new Activity.
			// Run an `AsynchTask` against your webservice on the server if this
			// is something
			// you need to do to see if the username and password, are correct
			// and registered

			new LoginTask().execute(userName, password);

			// ---------- Option 2 - Check the SQLite Database(if you are using
			// one) ---------

			// Otherwise you can use this info to read from an SQLiteDatabase on
			// your device.
			// To see if they are registered

			// This is where you would create a new intent to start
			// and Login your user, so that when your application is launched
			// it will check if there are a username and password associated to
			// the
			// Application, if there is and these are not null do something like

			// Create a new Intent

		} else {
			// Show the Layout for the current Activity
		}
	}

	private class LoginTask extends AsyncTask<Object, String, String> {

		Mail ml;
		ProgressDialog pd;

		@Override
		protected String doInBackground(Object... arg0) {
			
			// TODO Auto-generated method stub
			/*MyPeidWebAPI api = new MyPeidWebAPI();
			AccountManager am = AccountManager.get(MainActivity.this);
			Bundle bd = new Bundle();
			try {
				bd = am.getAuthToken(mypeidAccount, LoginAuth.ARG_AUTH_TYPE, null, null, null, null).getResult();
			} catch (OperationCanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String token = bd.getString(AccountManager.KEY_AUTHTOKEN,"");
			//String token = api.authenticateUser(userName, password);
			if( token == null || token.isEmpty() ){
				return "ERROR";
			}else
			{
				return "Done";
			}*/
			

			/*ml = new Mail((String) arg0[0], (String) arg0[1]);
			

			boolean status = ml.authenticate();

			if (status) {

				return "Done";
			} else {
				return "ERROR";
			}*/
			
			return "Done";
			

		}

		@Override
		protected void onPostExecute(String data) {
			super.onPostExecute(data);

			// Toast.makeText(ActivityPassword.this, data,
			// Toast.LENGTH_LONG).show();
			pd.dismiss();
			if (data.equalsIgnoreCase("Done")) {

				// Goto home page without drawer
				// Intent in = new Intent(ActivityPassword.this,
				// ActivityHomePage.class);
				// Goto home page with drawer

				Intent automagicLoginIntent = new Intent(getBaseContext(),
						ActivityHomePageWithDrawer.class);

				// Pass the Bundle to the New Activity, if you need to reuse
				// them to make additional calls

				automagicLoginIntent.putExtra("username", userName);
				automagicLoginIntent.putExtra("password", password);

				// Launch the Activity
				startActivity(automagicLoginIntent);
				finish();

			} else {

			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(MainActivity.this);
			pd.setCancelable(false);
			pd.setIndeterminate(false);
			pd.setMessage("Initializing...");
			pd.show();

			// Log.d("LoginTask", "Started...");

		}

		@Override
		protected void onProgressUpdate(String... values) {

		}

	}
}

