package com.rohitkaundal.mypeid;

import android.widget.*;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityPassword extends Activity {

	
	
	TextView txtforgotpassword;
	EditText txtpass, txtPhone;
	protected String str_username = null;
	protected String str_password = null;
	String strMobileNumber, strRequestId, strOtp, strPassword, strvPassword;
	Mail ml;
	LoginTask tsk;
	MyPeidWebAPI myApi;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);
		ActionBar ab = getActionBar(); 
		ab.setIcon(R.drawable.ic_action_tranparent_1);
		ab.setTitle("");

		myApi = new MyPeidWebAPI();
		
		

		txtpass = (EditText) findViewById(R.id.txtPassword);
		txtPhone = (EditText)findViewById(R.id.txtPhone);

		txtforgotpassword = (TextView) findViewById(R.id.txtForgotPassword);

		txtforgotpassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Dialog forgotpwd = new Dialog(ActivityPassword.this);
				forgotpwd.setContentView(R.layout.fragment_forgotpassword);
				forgotpwd.setTitle("Verify");

				// Verify Buton code
				forgotpwd.findViewById(R.id.btnVerify).setOnClickListener(
						new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								try {
									strMobileNumber = ((EditText) forgotpwd
											.findViewById(R.id.txtMobileNumber))
											.getText().toString();
									strOtp = ((EditText) forgotpwd
											.findViewById(R.id.txtOTP))
											.getText().toString();
									strPassword = strvPassword = txtpass.getText().toString();
									
									new TaskVerify().execute();

									finish();
								} catch (Exception e) {

								}

							}
						});

				// GetOTp Code
				forgotpwd.findViewById(R.id.btnGetOtp).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						try{
							strMobileNumber = ((EditText) forgotpwd
									.findViewById(R.id.txtMobileNumber))
									.getText().toString();
							
						}catch(Exception e){
							
						}
					}
				});

				// Show dialog
				forgotpwd.show();

			}
		});

		findViewById(R.id.btnLogin).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						/*
						 * if( ml.authenticate()){ Intent in = new
						 * Intent(ActivityPassword.this,
						 * ActivityHomePage.class); startActivity(in); }else {
						 * Toast.makeText(getBaseContext(),
						 * "Invalid user id/password !",
						 * Toast.LENGTH_LONG).show(); }
						 */
						tsk = new LoginTask();
						// Toast.makeText(getBaseContext(), str_username,
						// Toast.LENGTH_SHORT).show();
						str_username = txtPhone.getText().toString().trim() ;
						str_password = txtpass.getText().toString().trim();
						if(!str_username.isEmpty()){
							if(!str_password.isEmpty()){
								str_username += "@mypeid.com";
								//Toast.makeText(getApplicationContext(), "Username:" + str_username + " Password:" + str_password, Toast.LENGTH_SHORT).show();
								tsk.execute(str_username, str_password);
							}else{
								txtpass.setError("Enter valid password!");
							}
						}else{
							txtPhone.setError("Enter valid number!");
						}
						
						// Toast.makeText(getBaseContext(),
						// str_password,Toast.LENGTH_SHORT).show();

						

					}
				});

	}

	
	private class TaskGetOTP extends AsyncTask<Void, String, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				strRequestId = myApi.forgotPassword(strMobileNumber);
				if(strRequestId.isEmpty())
				{
					return false;
				}
				return true;
				
			}catch(Exception e)
			{
				return false;
			}
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result)
			{
				Toast.makeText(ActivityPassword.this, "OTP Sent !", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(ActivityPassword.this, "OTP Error pls try again !", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
	}
	
	private class TaskVerify extends AsyncTask<Void, String, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			try{
				Boolean success = myApi.boolVerifyPassword(strMobileNumber, strOtp, strPassword, strvPassword, strRequestId);
				return success;
				
			}catch(Exception e){
				return false;
			}
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result){
				Toast.makeText(ActivityPassword.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(ActivityPassword.this, "Error changing password !", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
	}
	
	
	/**
	 * Login async task
	 * @author Rohit Kaundal
	 *
	 */
	private class LoginTask extends AsyncTask<Object, String, String> {

		protected ProgressDialog pd;
		Mail ml;

		@Override
		protected String doInBackground(Object... arg0) {
			// TODO Auto-generated method stub

			publishProgress("Connecting...");
			ml = new Mail((String) arg0[0], (String) arg0[1]);
			publishProgress("Authenticating...");
			boolean status = ml.authenticate();

			if (status) {
				publishProgress("Success !");
				return "Done";
			} else {
				return "ERROR";
			}

		}

		@Override
		protected void onPostExecute(String data) {
			super.onPostExecute(data);
			pd.dismiss();
			// Toast.makeText(ActivityPassword.this, data,
			// Toast.LENGTH_LONG).show();

			if (data.equalsIgnoreCase("Done")) {
				pd.dismiss();
				storeData();
				// Goto home page without drawer
				// Intent in = new Intent(ActivityPassword.this,
				// ActivityHomePage.class);
				// Goto home page with drawer
				Intent in = new Intent(ActivityPassword.this,
						ActivityHomePageWithDrawer.class);
				in.putExtra("username", str_username);
				in.putExtra("password", str_password);
				startActivity(in);
				finish();

			} else {
				pd.cancel();
				Toast.makeText(ActivityPassword.this, "Invalid User/Password",
						Toast.LENGTH_LONG).show();
			}

		}
		
		void storeData(){
			/*SharedPreferences preferences = getBaseContext().getSharedPreferences(
					"MyPrefs", MODE_PRIVATE);
			SharedPreferences.Editor ed = preferences.edit();
			ed.putString("username", str_username);
			ed.putString("password", str_password);
			ed.apply();*/
			AccountManager am = AccountManager.get(getApplicationContext());
			try{
				final Account acc = new Account(str_username, LoginAuth.ARG_ACCOUNT_TYPE);
				am.addAccountExplicitly(acc, str_password, null);
			}catch(Exception e){
				
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(ActivityPassword.this);
			pd.setCancelable(false);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			;
			pd.setMessage("Logging in...");
			pd.show();
			// Log.d("LoginTask", "Started...");

		}

		@Override
		protected void onProgressUpdate(String... values) {
			pd.setMessage(values[0]);
		}

	}
}
