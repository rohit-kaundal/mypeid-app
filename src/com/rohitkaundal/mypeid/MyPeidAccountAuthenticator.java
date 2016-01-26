package com.rohitkaundal.mypeid;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class MyPeidAccountAuthenticator extends AbstractAccountAuthenticator {

	Context mContext;
	MyPeidWebAPI api;

	public MyPeidAccountAuthenticator(Context context) {
		super(context);
		mContext = context;
		api = new MyPeidWebAPI();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse arg0, String accountType,
			String authTokenType, String[] arg3, Bundle arg4)
			throws NetworkErrorException {
		// TODO Auto-generated method stub

		final Bundle bundle = new Bundle();
		final Intent intent = new Intent(mContext, LoginAuth.class);

		intent.putExtra(LoginAuth.ARG_ACCOUNT_TYPE, accountType);
		intent.putExtra(LoginAuth.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(LoginAuth.ARG_IS_ADDING_NEW_ACCOUNT, true);

		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, arg0);

		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, Bundle arg2) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
            Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {

        // We can add rejection of a request for a token type we
        // don't support here

        // Get the instance of the AccountManager that's making the
        // request
        final AccountManager am = AccountManager.get(mContext);

        // See if there is already an authentication token stored
        String authToken = am.peekAuthToken(account, authTokenType);

        // If we have no token, use the account credentials to fetch
        // a new one, effectively another logon
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                authToken = fetchTokenFromCredentials(account.name, password, authTokenType);
            }
        }

        // If we either got a cached token, or fetched a new one, hand
        // it back to the client that called us.
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we don't have a token, and we don't have
        // a password that will let us get a new one (or we weren't able
        // to use the password we do have).  We need to fetch
        // information from the user, we do that by creating an Intent
        // to an Activity child class.
        final Intent intent = new Intent(mContext, LoginAuth.class);

        // We want to give the Activity the information we want it to
        // return to the AccountManager.  We'll cover that with the
        // KEY_ACCOUNT_AUTHENTICATOR_RESPONSE parameter.
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        // We'll also give it the parameters we've already looked up, or
        // were given.
        intent.putExtra(LoginAuth.ARG_IS_ADDING_NEW_ACCOUNT, false);
        intent.putExtra(LoginAuth.ARG_ACCOUNT_NAME, account.name);
        intent.putExtra(LoginAuth.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(LoginAuth.ARG_AUTH_TYPE, authTokenType);

        // Remember that we have to return a Bundle, not an Intent, but
        // we can tell the caller to run our intent to get its
        // information with the KEY_INTENT parameter in the returned
        // Bundle
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }


	private String fetchTokenFromCredentials(String name, String password,
			String authTokenType) {
		// TODO Auto-generated method stub
		return api.authenticateUser(name, password);
	}

	@Override
	public String getAuthTokenLabel(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse arg0, Account arg1,
			String[] arg2) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, String arg2, Bundle arg3)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

}
