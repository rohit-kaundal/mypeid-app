package com.rohitkaundal.mypeid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.transition.Visibility;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.*;

public class ActivitySingleInboxMessage extends Activity {

	private MessageHolder single_msg = new MessageHolder();
	TextView txtfrom, txtsubject, txtFoldername, txtto, txtdate, txtReply;
	Button btnReplyto;

	String[] strRecipients;

	Mail ml = new Mail();
	WebView wb;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_single_inbox_message);

		Intent i = getIntent();
		getActionBar().setTitle("");


		single_msg = (MessageHolder) i.getExtras().getSerializable(
				"MessageHolder");
		ml = new Mail(i.getExtras().getString("user_name", ""), i.getExtras()
				.getString("user_password", ""));

		if (single_msg != null) {

			txtfrom = (TextView) findViewById(R.id.txtsinglefrom);
			txtsubject = (TextView) findViewById(R.id.txtsinglesubject);
			txtFoldername = (TextView) findViewById(R.id.tvFolderName);
			txtto = (TextView) findViewById(R.id.txtsingleto);
			txtdate = (TextView) findViewById(R.id.txtsingledate);
			txtReply = (EditText) findViewById(R.id.etReplyText);
			btnReplyto = (Button) findViewById(R.id.btnSendMail);

			wb = (WebView) findViewById(R.id.webView1);

			txtfrom.setText(single_msg.from);
			txtsubject.setText(single_msg.subject);
			txtFoldername.setText(single_msg.folder);
			txtto.setText(single_msg.to);
			txtdate.setText(single_msg.date);
			// txtmessage.setText(single_msg.message);
			wb.getSettings().setDefaultFontSize(12);
			wb.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

			wb.loadData(single_msg.message, "text/html; charset=UTF-8", null);

			btnReplyto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new TaskReply().execute();
				}
			});

		}

	}

	class TaskReply extends AsyncTask<Void, String, Boolean> {

		ProgressDialog pd;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				if (ml != null) {
					ml.setTo(strRecipients);
					ml.setFrom(ml.getUser());
					ml.setSubject("Reply: " + single_msg.subject);
					ml.setBody(txtReply.getText().toString());
					ml.send();
					return true;
				}
				return false;
			} catch (Exception e) {
				return false;
			}
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			if(result){
				Toast.makeText(ActivitySingleInboxMessage.this, "Replied !!!", Toast.LENGTH_SHORT).show();
				finish();
			}else{
				Toast.makeText(ActivitySingleInboxMessage.this, "Error sending mail !!!", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ActivitySingleInboxMessage.this);
			pd.setCancelable(false);
			pd.setIndeterminate(false);
			pd.setMessage("Replying...");
			pd.show();
		}
		

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_delete:
			new TaskDeleteEmail().execute();
			return true;
		case R.id.action_archive:
			new TaskArchiveEmail().execute();
			return true;
		case R.id.action_reply:
			doReply();
			return false;
		case R.id.action_forward:
			forwardMail();
			return true;
		case R.id.action_replyall:
			doReplyAll();
			return false;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	void forwardMail(){
		Intent i = new Intent(ActivitySingleInboxMessage.this, ActivityReplyEmail.class);
		i.putExtra("user_name", ml.getUser());
		i.putExtra("user_password", ml.getPass());
		
		i.putExtra("subject","Fwd:"+single_msg.subject );
		String body = "\r---------Forwarded message---------";
		body += "\rFrom: " + single_msg.from;
		body += "\rDate: " + single_msg.date;
		body += "\rSubject: " + single_msg.subject;
		body += "\rTo: " + single_msg.to;
		
		 body += Jsoup.parse(single_msg.message.toString()).toString();
		//body = body.replaceAll("\n", "\n>");
		i.putExtra("body", body);
		startActivity(i);
		finish();
	}
	private void doReplyAll() {
		// TODO Auto-generated method stub
		
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add(single_msg.from);
		String[] tmp2 = single_msg.to.split(",");
		if (tmp2 != null && tmp2.length > 0) {
			for (String s : tmp2) {
				tmp.add(s);
			}
		}
		strRecipients = tmp.toArray(new String[tmp.size()]);
		startReplyMail();
		

	}
	
	void startReplyMail(){
		Intent i = new Intent(ActivitySingleInboxMessage.this, ActivityReplyEmail.class);
		i.putExtra("user_name", ml.getUser());
		i.putExtra("user_password", ml.getPass());
		String _to = "";
		for(String s: strRecipients ){
			_to += s + ",";
		}
		_to = _to.substring(0, _to.length()-1);

		i.putExtra("email_to", _to);
		i.putExtra("subject","Re:"+single_msg.subject );
		String body = "\r\nOn "+single_msg.date+" "+single_msg.from+" wrote:\r\n>";
		 body += Jsoup.parse(single_msg.message.toString()).toString();
		//body = body.replaceAll("\n", "\n>");
		i.putExtra("body", body);
		startActivity(i);
		finish();
	}

	private void doReply() {
		// TODO Auto-generated method stub
		
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add(single_msg.from);
		strRecipients = tmp.toArray(new String[tmp.size()]);
		startReplyMail();
	}

	// Creating menu
	@Override
	public boolean onCreateOptionsMenu(Menu mnu) {
		MenuInflater mn = getMenuInflater();
		mn.inflate(R.menu.single_msg_ab_menu, mnu);
		return true;
	}

	private class TaskDeleteEmail extends AsyncTask<Void, String, Boolean> {

		ProgressDialog pd;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int msgId = single_msg.imsgNumber;
			String strfoldr = single_msg.folder;
			if (msgId > 0) {
				Boolean success = ml.boolDeleteMail(msgId, strfoldr);
				return success;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			if (result) {
				Toast.makeText(ActivitySingleInboxMessage.this,
						"Message Deleted !", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(ActivitySingleInboxMessage.this,
						"Error deleting message!", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ActivitySingleInboxMessage.this);
			pd.setIndeterminate(false);
			pd.setCancelable(false);
			pd.setMessage("Deleting message...");
			pd.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

	private class TaskArchiveEmail extends AsyncTask<Void, String, Boolean> {
		ProgressDialog pd;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int msgId = single_msg.imsgNumber;
			String strfoldr = single_msg.folder;
			if (msgId > 0) {
				Boolean success = ml.boolArchiveMail(msgId, strfoldr);
				return success;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			if (result) {
				Toast.makeText(ActivitySingleInboxMessage.this,
						"Message Achived !", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(ActivitySingleInboxMessage.this,
						"Error archiving message!", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ActivitySingleInboxMessage.this);
			pd.setIndeterminate(false);
			pd.setCancelable(false);
			pd.setMessage("Archiving message...");
			pd.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

}
