package com.rohitkaundal.mypeid;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityComposeEmail extends Activity {

	EditText txtEmailTo, txtSubject, txtEmailBody, txtEmailFrom, txtCC, txtBcc;
	Button btnSendMail;
	String username, password;
	FileDialog fileDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_activity_composemail);
		Bundle bd = getIntent().getExtras();

		ActionBar ab = getActionBar();
		
		ab.setTitle(Html.fromHtml("<font color='#2E7FC8'>Compose</font>"));
		
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setHomeButtonEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		
		username = bd.getString("user_name");
		password = bd.getString("user_password");

		txtEmailTo = (EditText) findViewById(R.id.txtEmailTo);
		txtSubject = (EditText) findViewById(R.id.txtEmailSubject);
		txtEmailBody = (EditText) findViewById(R.id.txtEmailBody);
		btnSendMail = (Button) findViewById(R.id.btnSendMail);
		txtEmailFrom = (EditText) findViewById(R.id.txtEmailFrom);
		txtCC = (EditText) findViewById(R.id.txtEmailToCC);
		txtBcc = (EditText) findViewById(R.id.txtEmailToBcc);

		txtEmailTo.setText(bd.getString("email_to", ""));
		txtEmailFrom.setText(username);

		btnSendMail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (txtEmailTo.getText().toString().isEmpty()) {
					txtEmailTo.setError("Enter email address !");
					return;
				}
				if (txtSubject.getText().toString().isEmpty()) {
					txtSubject.setError("Enter subject ! ");
					return;
				}
				if (txtEmailBody.getText().toString().isEmpty()) {
					txtEmailBody.setError("Enter message!");
					return;
				}

				TaskSendMail sndMail = new TaskSendMail();
				sndMail.execute();

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		MenuInflater mn = getMenuInflater();
		mn.inflate(R.menu.menu_compose_mail, menu);
		
		
		TextView tvSend = new TextView(this);
		//tvSend.setId(R.id.action_send);
		tvSend.setText("\uf1d9");
		tvSend.setTextSize(16);
		tvSend.setTextColor(getResources().getColor(R.color.button_bg));
		tvSend.setPadding(10, 2, 8, 2);
		Typeface font = Typeface.createFromAsset(getAssets(),
				"fontawesome-webfont.ttf");
		tvSend.setTypeface(font);
		MenuItem i = menu.findItem(R.id.action_send);
		tvSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (txtEmailTo.getText().toString().isEmpty()) {
					txtEmailTo.setError("Enter email address !");
					return;
				}
				if (txtSubject.getText().toString().isEmpty()) {
					txtSubject.setError("Enter subject ! ");
					return;
				}
				if (txtEmailBody.getText().toString().isEmpty()) {
					txtEmailBody.setError("Enter message!");
					return;
				}
				TaskSendMail sndMail = new TaskSendMail();
				sndMail.execute();
				return ;
				
			}
		});
		i.setActionView(tvSend);
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_send:
			// NavUtils.navigateUpFromSameTask(this);
			if (txtEmailTo.getText().toString().isEmpty()) {
				txtEmailTo.setError("Enter email address !");
				return true;
			}
			if (txtSubject.getText().toString().isEmpty()) {
				txtSubject.setError("Enter subject ! ");
				return true;
			}
			if (txtEmailBody.getText().toString().isEmpty()) {
				txtEmailBody.setError("Enter message!");
				return true;
			}

			TaskSendMail sndMail = new TaskSendMail();
			sndMail.execute();
			return true;

		case R.id.action_attachfile:
			attachFile();
			return true;

		case R.id.action_addccbcc:
			item.setVisible(false);
			if (txtCC != null && txtBcc != null) {
				txtCC.setVisibility(View.VISIBLE);
				txtBcc.setVisibility(View.VISIBLE);
			}
			return true;

		case R.id.action_savedraft:

			return true;

		case R.id.action_discard:
			Toast.makeText(ActivityComposeEmail.this, "Mail discarded !",
					Toast.LENGTH_SHORT).show();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void attachFile() {

		/*File mPath = new File(Environment.getExternalStorageDirectory()
				+ "//DIR//");
		fileDialog = new FileDialog(this, mPath);
		fileDialog.setFileEndsWith(".jpg");
		fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
			public void fileSelected(File file) {
				Log.d(getClass().getName(), "selected file " + file.toString());
				Toast.makeText(getApplicationContext(), file.toString(),
						Toast.LENGTH_SHORT).show();
			}
		});
		// fileDialog.addDirectoryListener(new
		// FileDialog.DirectorySelectedListener() {
		// public void directorySelected(File directory) {
		// Log.d(getClass().getName(), "selected dir " + directory.toString());
		// }
		// });
		// fileDialog.setSelectDirectoryOption(false);
		fileDialog.showDialog();*/
		showFileChooser();
	}
	
	
	private static final int FILE_SELECT_CODE = 0;

	private void showFileChooser() {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("*/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);

	    try {
	        startActivityForResult(
	                Intent.createChooser(intent, "Select a File to Upload"),
	                FILE_SELECT_CODE);
	    } catch (android.content.ActivityNotFoundException ex) {
	        // Potentially direct the user to the Market with a Dialog
	        Toast.makeText(this, "Please install a File Manager.", 
	                Toast.LENGTH_SHORT).show();
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        case FILE_SELECT_CODE:
	        if (resultCode == RESULT_OK) {
	            // Get the Uri of the selected file 
	            Uri uri = data.getData();
	/*            Log.d(TAG, "File Uri: " + uri.toString());*/
	            // Get the path
	            try {
					String path = this.getPath(this, uri);
					addFiles(path);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            /*Log.d(TAG, "File Path: " + path);*/
	            // Get the file instance
	            // File file = new File(path);
	            // Initiate the upload
	        }
	        break;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void addFiles(String path) {
		// TODO Auto-generated method stub
		LinearLayout llv = (LinearLayout)findViewById(R.id.llFiles);
		TextView tvTmpFile = new TextView(this);
		tvTmpFile.setPadding(10, 5, 10, 5);
		tvTmpFile.setBackground(null);
		tvTmpFile.setTag(path);
		tvTmpFile.setText(path.substring(path.lastIndexOf("/")+1));
		
		llv.addView(tvTmpFile);
		
		
	}

	public  String getPath(Context context, Uri uri) throws URISyntaxException {
	    if ("content".equalsIgnoreCase(uri.getScheme())) {
	        String[] projection = { "_data" };
	        Cursor cursor = null;

	        try {
	            cursor = context.getContentResolver().query(uri, projection, null, null, null);
	            int column_index = cursor.getColumnIndexOrThrow("_data");
	            if (cursor.moveToFirst()) {
	                return cursor.getString(column_index);
	            }
	        } catch (Exception e) {
	            // Eat it
	        }
	    }
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	} 
	
	
	
	
	
	
	

	private class TaskSendMail extends AsyncTask<Void, String, String> {
		ProgressDialog pd = null;

		@Override
		protected String doInBackground(Void... arg0) {

			try {
				String[] strToMail = txtEmailTo.getText().toString().split(",");
				/* String strToMail = txtEmailTo.getText().toString(); */
				/*
				 * ArrayList<String> toMail = new ArrayList<String>();
				 * toMail.add(strToMail);
				 */
				String subject = txtSubject.getText().toString();
				String emailbody = txtEmailBody.getText().toString();
				String[] cc = txtCC != null ? txtCC.getText().toString().trim()
						.split(",") : null;
				String[] bcc = txtBcc != null ? txtBcc.getText().toString()
						.trim().split(",") : null;

				Mail cm = new Mail(username, password);

				cm.setTo(strToMail/* toMail.toArray(new String[toMail.size()]) */);

				if (cc != null && !cc[0].isEmpty())
					cm.setCC(cc);
				if (bcc != null && !bcc[0].isEmpty())
					cm.setBCC(bcc);
				emailbody = Html.toHtml(txtEmailBody.getText());
				//emailbody = emailbody.replace("", "<br>");
				cm.setBody(emailbody);
				cm.setFrom(username);
				cm.setSubject(subject);

				// Log.d("MyPeid",toMail.toString());

				cm.send();
				/*
				 * txtEmailBody.setText(""); txtEmailTo.setText("");
				 * txtSubject.setText("");
				 */

				return "Mail Sent !!!";

			} catch (Exception e) {
				String message = "Error: " + e.getLocalizedMessage();

				return message;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.setMessage(result);
			pd.dismiss();
			Toast.makeText(ActivityComposeEmail.this, result, Toast.LENGTH_LONG)
					.show();
			finish();

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ActivityComposeEmail.this);
			pd.setCancelable(false);
			pd.setIndeterminate(false);
			pd.setMessage("Sending mail...");
			pd.show();
		}

	}
}
