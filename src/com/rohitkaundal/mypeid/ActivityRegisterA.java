package com.rohitkaundal.mypeid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.VoicemailContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActivityRegisterA extends Activity {

	String strMobileNumber, strRequestId;
	MyPeidWebAPI myPeidApi;
	EditText txtMobile;
	Button btnGetOTP;

	private class TaskGetOtp extends AsyncTask<String, String, Boolean> {
		ProgressDialog pdProgress = null;
		
		void onStepToNext(){
			Intent i = new Intent(ActivityRegisterA.this, ActivityRegisterB.class);
			i.putExtra("mobile", strMobileNumber);
			i.putExtra("request_id", strRequestId);
			
			startActivity(i);
			finish();
			
		}
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
			pdProgress = new ProgressDialog(ActivityRegisterA.this);
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
				Toast.makeText(ActivityRegisterA.this, "OTP Sent !",
						Toast.LENGTH_SHORT).show();
				
				onStepToNext();
				
			} else {
				Toast.makeText(ActivityRegisterA.this, "Error getting otp..",
						Toast.LENGTH_SHORT).show();
				
			}
		}

	}

	/**
	 * 
	 * @author Rohit Kaundal
	 *
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_parta);
		getActionBar().setTitle("");

		myPeidApi = new MyPeidWebAPI();

		txtMobile = (EditText) findViewById(R.id.txtPhoneNumberRegister);
		

		btnGetOTP = (Button) findViewById(R.id.btnGetOTPRegister);
		
		findViewById(R.id.tvAgreeURL).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = "https://www.mypeid.com/mail/tc.php";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				
			}
		});
		
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
		
		
	}

	public String getStrMobileNumber() {
		return strMobileNumber;
	}

	public void setStrMobileNumber(String strMobileNumber) {
		this.strMobileNumber = strMobileNumber;
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

	
	public Button getBtnGetOTP() {
		return btnGetOTP;
	}

	public void setBtnGetOTP(Button btnGetOTP) {
		this.btnGetOTP = btnGetOTP;
	}

	
}
