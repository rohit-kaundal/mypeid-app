package com.rohitkaundal.mypeid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;

import org.apache.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

public class MyPeidWebAPI {

	/**
	 * Class Vairables URL
	 */

	InputStream is = null;
	JSONObject jobj = null;
	String json = "";

	public final String register_request_otp_url = "https://www.mypeid.com/mypeservices/public/mypeuser/";
	public final String register_newuser_url = "https://www.mypeid.com/mypeservices/public/mypeuser";
	public final String forgotpassword_getotp_url = "https://www.mypeid.com/mypeservices/public/forgotpassword/";
	public final String forgotpassword_updatepassword_url = "https://www.mypeid.com/mypeservices/public/forgotpassword";
	public final String authenticate_url = "https://www.mypeid.com/mypeservices/public/authenticate";
	public final String addressbook_viewcontacts_url = "https://www.mypeid.com/mypeservices/public/addressbook/919492389969?token=XXX";
	public final String addressbook_editcontatcs_url = "https://www.mypeid.com/mypeservices/public/addressbook/2/edit?name=ram&email=ram&firstname=ram&surname=ram&token=xxxx";
	public final String addressbook_addcontatcs_url = "https://www.mypeid.com/mypeservices/public/addressbook/";

	public String register_request_id = "";
	public String forgotpassword_request_id = "";
	public String _token = "";
	public String OTP_Registeration = "";
	public String OTP_forgotpassword = "";

	/**
	 * 
	 */
	public MyPeidWebAPI() {
		SSLUtilities.trustAllHostnames();
		SSLUtilities.trustAllHttpsCertificates();

	}

	// this method returns json object.
	public JSONObject makePostHttpRequest(String url, List<NameValuePair> params) {
		// http client helps to send and receive data

		// our request method is post

		try {
			DefaultHttpClient httpclient = buildhttpClient();
			// get the response
			HttpPost httpost = new HttpPost(url);
			httpost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse httpresponse = httpclient.execute(httpost);
			HttpEntity httpentity = httpresponse.getEntity();
			is = httpentity.getContent();

			// get the content and store it into inputstream object.

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			// convert byte-stream to character-stream.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				// close the input stream
				is.close();
				json = sb.toString();

				try {
					jobj = new JSONObject(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobj;

		// return null;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */

	private DefaultHttpClient buildhttpClient() throws Exception {
		final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};
		/*
		 * try { SSLContext ctx = SSLContext.getInstance("TLS");
		 * X509TrustManager tm = new X509TrustManager() {
		 * 
		 * @Override public X509Certificate[] getAcceptedIssuers() { return
		 * _AcceptedIssuers; }
		 * 
		 * @Override public void checkServerTrusted(X509Certificate[] chain,
		 * String authType) { }
		 * 
		 * @Override public void checkClientTrusted(X509Certificate[] chain,
		 * String authType) { } }; ctx.init(null, new TrustManager[] { tm }, new
		 * SecureRandom()); // SSLSocketFactory ssf = new SSLSocketFactory(ctx,
		 * // SSLSocketFactory.this.); //ClientConnectionManager ccm =
		 * httpClient.getConnectionManager(); //SchemeRegistry sr =
		 * ccm.getSchemeRegistry(); //sr.register(new Scheme("https", 443,
		 * ssf)); return new DefaultHttpClient(); } catch (Exception e) { throw
		 * e; }
		 */

		return new DefaultHttpClient();
	}

	// this method returns json object.
	public JSONObject makeGetHttpRequest(String url) {
		// http client helps to send and receive data
		DefaultHttpClient httpclient = new DefaultHttpClient();
		// our request method is post

		try {
			// get the response
			HttpGet httpost = new HttpGet(url);
			HttpResponse httpresponse = httpclient.execute(httpost);
			// System.out.println(httpresponse.getStatusLine().toString());
			HttpEntity httpentity = httpresponse.getEntity();
			is = httpentity.getContent();

			// get the content and store it into inputstream object.
			// convert byte-stream to character-stream.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				// close the input stream
				is.close();
				json = sb.toString();

				try {
					jobj = new JSONObject(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());

		}

		return jobj;

		// return null;
	}

	/**
	 * 
	 * @param strMobileNumber
	 *            Mobile number to get otp on
	 * @return returns unique request id on success and null on failure
	 */
	public String getOTP(String strMobileNumber) {

		String url = this.register_request_otp_url + strMobileNumber;

		try {
			JSONObject jobj = makeGetHttpRequest(url);
			this.setRegister_request_id(jobj.getString("request_id"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return getRegister_request_id();
	}

	/**
	 * 
	 * @param strMobileNumber
	 * @param strOTP
	 * @param strPassword
	 * @param strVerifyPassword
	 * @param strRequestID
	 * @return
	 */
	public boolean RegisterUser(String strMobileNumber, String strOTP,
			String strPassword, String strVerifyPassword, String strRequestID) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("mobile", strMobileNumber));
		params.add(new BasicNameValuePair("otp", strOTP));
		params.add(new BasicNameValuePair("password", strPassword));
		params.add(new BasicNameValuePair("verifypassword", strVerifyPassword));
		params.add(new BasicNameValuePair("request_id", strRequestID));

		JSONObject jobj = makePostHttpRequest(this.register_newuser_url, params);
		if (null != jobj) {
			System.out.println(jobj.toString());
			return true;
		} else
			return false;
	}

	/**
	 * Returns request id for forgot password for mobile number
	 * 
	 * @param strMobileNumber
	 *            mobile number whose password to reset
	 * @return
	 */
	public String getOTPForgotPassword(String strMobileNumber) {

		String url = this.forgotpassword_getotp_url + strMobileNumber;

		try {
			JSONObject jobj = makeGetHttpRequest(url);
			this.forgotpassword_request_id = jobj.getString("request_id");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return getForgotpassword_request_id();
	}

	public Boolean boolVerifyPassword(String strMobileNumber, String strOtp,
			String strPassword, String strvPassword, String strRequestId) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("mobile", strMobileNumber));
		params.add(new BasicNameValuePair("otp", strOtp));
		params.add(new BasicNameValuePair("password", strPassword));
		params.add(new BasicNameValuePair("verifypassword", strvPassword));
		params.add(new BasicNameValuePair("request_id", strRequestId));
		
		try
		{
			JSONObject jobj = makePostHttpRequest(this.forgotpassword_updatepassword_url, params);
			if (null != jobj) {

				return true;
			} else
				return false;

		}catch(Exception e)
		{
			return false;
		}

	}

	/**
	 * 
	 * @param strMobileNumber
	 * @return
	 */
	public String forgotPassword(String strMobileNumber) {

		return null;
	}

	public InputStream getIs() {
		return is;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	public JSONObject getJobj() {
		return jobj;
	}

	public void setJobj(JSONObject jobj) {
		this.jobj = jobj;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getRegister_request_id() {
		return register_request_id;
	}

	public void setRegister_request_id(String register_request_id) {
		this.register_request_id = register_request_id;
	}

	public String get_token() {
		return _token;
	}

	public void set_token(String _token) {
		this._token = _token;
	}

	/**
	 * This function returns a valid token on successful authentication else
	 * return null on invalid user name and password
	 * 
	 * @param strUsername
	 * @param strPassword
	 * @return
	 */
	public String authenticateUser(String strUsername, String strPassword) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("username", strUsername));
		params.add(new BasicNameValuePair("password", strPassword));
		
		
		try
		{
			JSONObject jobj = makePostHttpRequest(this.authenticate_url, params);
			if (null != jobj) {
				String strToken = jobj.getString("token");
				return strToken;
			} else
				return null;

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String getForgotpassword_request_id() {
		return forgotpassword_request_id;
	}

	public void setForgotpassword_request_id(String forgotpassword_request_id) {
		this.forgotpassword_request_id = forgotpassword_request_id;
	}

	public String getOTP_Registeration() {
		return OTP_Registeration;
	}

	public void setOTP_Registeration(String oTP_Registeration) {
		OTP_Registeration = oTP_Registeration;
	}

	public String getOTP_forgotpassword() {
		return OTP_forgotpassword;
	}

	public void setOTP_forgotpassword(String oTP_forgotpassword) {
		OTP_forgotpassword = oTP_forgotpassword;
	}

}
