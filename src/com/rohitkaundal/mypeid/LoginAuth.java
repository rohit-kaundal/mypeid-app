package com.rohitkaundal.mypeid;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginAuth extends AccountAuthenticatorActivity {
	public static final String ARG_ACCOUNT_TYPE = "account.mypeid.com";
	public static final String ARG_AUTH_TYPE = "login";
	public static final String ARG_IS_ADDING_NEW_ACCOUNT = "adding_new_account";
	public static final String ARG_ACCOUNT_NAME = "mypeid.com";
	protected static final String PARAM_USER_PASS = "param_user_pass";
	
	private AccountManager mAccountManager;
	private String mAccountName;
	private String mAccountType;
	private String mAuthType;
	private MyPeidWebAPI myApi;
	private EditText txtpass;
	private EditText txtPhone;
	private String mAuthTokenType ="mypeid-token";
	
	
	
	private void doSubmit() {
		// TODO Auto-generated method stub
		final String userName = txtPhone.getText().toString().trim() + "@mypeid.com";
		
	    final String userPass = txtpass.getText().toString();
	    
	    
	    new AsyncTask<Void, Void, Intent>() {
	    	
	    	@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd.setCancelable(false);
				pd.setIndeterminate(false);
				pd.setMessage("Logging in...");
				pd.show();
			}
			ProgressDialog pd = new ProgressDialog(LoginAuth.this);
	        @Override
	        protected Intent doInBackground(Void... params) {
	        	MyPeidWebAPI api = new MyPeidWebAPI();
	            String authtoken = api.authenticateUser(userName, userPass);
	            final Intent res = new Intent();
	            res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
	            res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, LoginAuth.ARG_ACCOUNT_TYPE);
	            res.putExtra(AccountManager.KEY_AUTHTOKEN, authtoken);
	            res.putExtra(PARAM_USER_PASS, userPass);
	            return res;
	        }
	        @Override
	        protected void onPostExecute(Intent intent) {
	        	pd.dismiss();
	            finishLogin(intent);
	        }
	    }.execute();
	}
	
	
	private void finishLogin(Intent intent) {
	    String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	    String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
	    final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
	    if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
	        String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
	        String authtokenType = LoginAuth.ARG_AUTH_TYPE;
	        // Creating the account on the device and setting the auth token we got
	        // (Not setting the auth token will cause another call to the server to authenticate the user)
	        mAccountManager.addAccountExplicitly(account, accountPassword, null);
	        mAccountManager.setAuthToken(account, authtokenType, authtoken);
	    } else {
	        mAccountManager.setPassword(account, accountPassword);
	    }
	    setAccountAuthenticatorResult(intent.getExtras());
	    setResult(RESULT_OK, intent);
	    finish();
	}

	
	
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.activity_password);
		ActionBar ab = getActionBar(); 
		ab.setIcon(R.drawable.ic_action_tranparent_1);
		ab.setTitle("");


	    mAccountManager = AccountManager.get(getBaseContext());
	    mAccountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
	    mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
	    mAuthType = getIntent().getStringExtra(ARG_AUTH_TYPE);
	    // and your UI fields for password, and account name editing

	    // ... get here in response to a login click ...
	    
	    
		myApi = new MyPeidWebAPI();
		
		

		txtpass = (EditText) findViewById(R.id.txtPassword);
		txtPhone = (EditText)findViewById(R.id.txtPhone);

	    findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				doSubmit();
			}

			
		});
	    
	    
	    //showProgress(true);
	    

	    // ... elsewhere ...
	   
		
	}
	
	
}
