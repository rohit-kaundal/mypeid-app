package com.rohitkaundal.mypeid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActivityRegister extends Activity {

	String strMobileNumber, strPassword, strVerifyPassword, strOTP,
			strRequestId;
	MyPeidWebAPI myPeidApi;
	EditText txtMobile, txtPwd, txtVPwd, txtOTP;
	Button btnGetOTP, btnRegister;

	private class TaskGetOtp extends AsyncTask<String, String, Boolean> {
		ProgressDialog pdProgress = null;
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String strMobileNumber = arg0.length > 0 ? arg0[0] : "";
			try {
				strRequestId = myPeidApi.getOTP(strMobileNumber);
				if (!strRequestId.isEmpty()) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pdProgress = new ProgressDialog(ActivityRegister.this);
			pdProgress.setCancelable(false);
			pdProgress.setIndeterminate(false);
			pdProgress.setMessage("Sending OTP...");
			pdProgress.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pdProgress.dismiss();
			if (result) {
				Toast.makeText(ActivityRegister.this, "OTP Sent !",
						Toast.LENGTH_SHORT).show();
				btnGetOTP.setEnabled(false);
			} else {
				Toast.makeText(ActivityRegister.this, "Error getting otp..",
						Toast.LENGTH_SHORT).show();
				btnGetOTP.setEnabled(true);
			}
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
			pdProgress = new ProgressDialog(ActivityRegister.this);
			pdProgress.setCancelable(false);
			pdProgress.setIndeterminate(false);
			pdProgress.setMessage("Registering...");
			pdProgress.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pdProgress.dismiss();
			if (result) {
				Toast.makeText(ActivityRegister.this, "User registered!",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(ActivityRegister.this, "Error!",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getActionBar().hide();

		myPeidApi = new MyPeidWebAPI();

		txtMobile = (EditText) findViewById(R.id.txtPhoneNumberRegister);
		txtPwd = (EditText) findViewById(R.id.txtRegisterPassword);
		txtVPwd = (EditText) findViewById(R.id.txtRegisterVerifyPassword);
		txtOTP = (EditText) findViewById(R.id.txtRegisterOTP);

		btnGetOTP = (Button) findViewById(R.id.btnGetOTPRegister);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		btnGetOTP.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				strMobileNumber = txtMobile.getText().toString();
				if(strMobileNumber.isEmpty())
				{
					txtMobile.setError("Enter valid mobile number");
					txtMobile.requestFocus();
					return;
				}
				
				new TaskGetOtp().execute(strMobileNumber);
				
			}
		});
		
		btnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				strMobileNumber = txtMobile.getText().toString();
				strPassword = txtPwd.getText().toString();
				strVerifyPassword = txtVPwd.getText().toString();
				strOTP = txtOTP.getText().toString();
				
				if(!strMobileNumber.isEmpty() && !strPassword.isEmpty()&& !strVerifyPassword.isEmpty() && !strOTP.isEmpty()){
					new TaskRegister().execute();
				}else{
					Toast.makeText(ActivityRegister.this, "Fill all the correct details...", Toast.LENGTH_LONG).show();
				}
			}
		});

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

	public EditText getTxtMobile() {
		return txtMobile;
	}

	public void setTxtMobile(EditText txtMobile) {
		this.txtMobile = txtMobile;
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

	public Button getBtnGetOTP() {
		return btnGetOTP;
	}

	public void setBtnGetOTP(Button btnGetOTP) {
		this.btnGetOTP = btnGetOTP;
	}

	public Button getBtnRegister() {
		return btnRegister;
	}

	public void setBtnRegister(Button btnRegister) {
		this.btnRegister = btnRegister;
	}

}
