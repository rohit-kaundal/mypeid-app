package com.rohitkaundal.mypeid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActivityRegisterB extends Activity {

	String strMobileNumber, strPassword, strVerifyPassword, strOTP,
			strRequestId, str_username, str_password;
	MyPeidWebAPI myPeidApi;
	EditText txtPwd, txtVPwd, txtOTP;
	Button btnRegister;

	void doLogin() {
		str_username = strMobileNumber.trim() + "@mypeid.com";
		str_password = strPassword;
		new LoginTask().execute(str_username, str_password);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		getMenuInflater().inflate(R.menu.menu_register, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_register:
			doRegister();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private class LoginTask extends AsyncTask<Object, String, String> {

		Mail ml;

		@Override
		protected String doInBackground(Object... arg0) {
			// TODO Auto-generated method stub

			ml = new Mail((String) arg0[0], (String) arg0[1]);

			boolean status = ml.authenticate();

			if (status) {

				return "Done";
			} else {
				return "ERROR";
			}

		}

		@Override
		protected void onPostExecute(String data) {
			super.onPostExecute(data);

			// Toast.makeText(ActivityPassword.this, data,
			// Toast.LENGTH_LONG).show();

			if (data.equalsIgnoreCase("Done")) {

				storeData();
				// Goto home page without drawer
				// Intent in = new Intent(ActivityPassword.this,
				// ActivityHomePage.class);
				// Goto home page with drawer
				Intent in = new Intent(ActivityRegisterB.this,
						ActivityHomePageWithDrawer.class);
				in.putExtra("username", str_username);
				in.putExtra("password", str_password);
				startActivity(in);
				finish();

			} else {

				Intent in = new Intent(ActivityRegisterB.this,
						ActivityPassword.class);
				startActivity(in);
				finish();

			}

		}

		void storeData() {
			SharedPreferences preferences = getBaseContext()
					.getSharedPreferences("MyPrefs", MODE_PRIVATE);
			SharedPreferences.Editor ed = preferences.edit();
			ed.putString("username", str_username);
			ed.putString("password", str_password);
			ed.apply();

		}

	}

	/**
	 * 
	 * @author Rohit Kaundal
	 * 
	 */
	private class TaskRegister extends AsyncTask<Void, String, Boolean> {
		ProgressDialog pdProgress = null;

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			try {
				Boolean success = myPeidApi.RegisterUser(strMobileNumber,
						strOTP, strPassword, strVerifyPassword, strRequestId);
				return success;
			} catch (Exception e) {
				return false;
			}

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pdProgress = new ProgressDialog(ActivityRegisterB.this);
			pdProgress.setCancelable(false);
			pdProgress.setIndeterminate(false);
			pdProgress.setMessage("Registering...");
			pdProgress.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (result) {
				pdProgress.setMessage("Re-directing...");

				doLogin();
			} else {
				Toast.makeText(ActivityRegisterB.this, "Error!",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_partb);
		getActionBar().setTitle("");
		Intent i = getIntent();

		myPeidApi = new MyPeidWebAPI();
		strMobileNumber = i.getStringExtra("mobile");
		strRequestId = i.getStringExtra("request_id");

		txtPwd = (EditText) findViewById(R.id.txtRegisterPassword);
		txtVPwd = (EditText) findViewById(R.id.txtRegisterVerifyPassword);
		txtOTP = (EditText) findViewById(R.id.txtRegisterOTP);

		btnRegister = (Button) findViewById(R.id.btnRegister);

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				doRegister();
			}
		});

	}

	protected void doRegister() {
		// TODO Auto-generated method stub
		strPassword = txtPwd.getText().toString();
		strVerifyPassword = txtVPwd.getText().toString();
		strOTP = txtOTP.getText().toString();

		if (!strMobileNumber.isEmpty() && !strPassword.isEmpty()
				&& !strVerifyPassword.isEmpty() && !strOTP.isEmpty()) {
			new TaskRegister().execute();
		} else {
			Toast.makeText(getApplicationContext(),
					"Fill all the correct details...", Toast.LENGTH_LONG)
					.show();
		}

	}

	public String getStrMobileNumber() {
		return strMobileNumber;
	}

	public void setStrMobileNumber(String strMobileNumber) {
		this.strMobileNumber = strMobileNumber;
	}

	public String getStrPassword() {
		return strPassword;
	}

	public void setStrPassword(String strPassword) {
		this.strPassword = strPassword;
	}

	public String getStrVerifyPassword() {
		return strVerifyPassword;
	}

	public void setStrVerifyPassword(String strVerifyPassword) {
		this.strVerifyPassword = strVerifyPassword;
	}

	public String getStrOTP() {
		return strOTP;
	}

	public void setStrOTP(String strOTP) {
		this.strOTP = strOTP;
	}

	public String getStrRequestId() {
		return strRequestId;
	}

	public void setStrRequestId(String strRequestId) {
		this.strRequestId = strRequestId;
	}

	public MyPeidWebAPI getMyPeidApi() {
		return myPeidApi;
	}

	public void setMyPeidApi(MyPeidWebAPI myPeidApi) {
		this.myPeidApi = myPeidApi;
	}

	public EditText getTxtPwd() {
		return txtPwd;
	}

	public void setTxtPwd(EditText txtPwd) {
		this.txtPwd = txtPwd;
	}

	public EditText getTxtVPwd() {
		return txtVPwd;
	}

	public void setTxtVPwd(EditText txtVPwd) {
		this.txtVPwd = txtVPwd;
	}

	public EditText getTxtOTP() {
		return txtOTP;
	}

	public void setTxtOTP(EditText txtOTP) {
		this.txtOTP = txtOTP;
	}

	public Button getBtnRegister() {
		return btnRegister;
	}

	public void setBtnRegister(Button btnRegister) {
		this.btnRegister = btnRegister;
	}

}
