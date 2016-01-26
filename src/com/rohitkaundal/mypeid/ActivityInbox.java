package com.rohitkaundal.mypeid;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;

import javax.mail.*;

public class ActivityInbox extends Fragment {

	ImageButton fab;
	protected String strUsername = "test";
	protected String strPass = "empty";
	Mail ml;
	ListView lv;
	ArrayList<MessageHolder> inboxfolder = new ArrayList<MessageHolder>();
	InboxReaderAdapter msgAdapter;

	@Override
	public void onActivityCreated(Bundle icicle) {
		super.onActivityCreated(icicle);

		lv = (ListView) getActivity().findViewById(R.id.listInbox);
		fab = (ImageButton) getActivity().findViewById(R.id.fab_image_button);

		fab.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), ActivityComposeEmail.class);
				i.putExtra("user_name", strUsername);
				i.putExtra("user_password", strPass);
				startActivity(i);

			}
		});

		Bundle bundle = getArguments();

		strUsername = bundle.getString("username");
		strPass = bundle.getString("password");

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg0 != null) {
					// Get Message from array and forward it to single inbox
					// activity
					MessageHolder single_message = (inboxfolder.isEmpty() ? new MessageHolder()
							: inboxfolder.get(arg2));

					Intent i = new Intent(
							getActivity().getApplicationContext(),
							ActivitySingleInboxMessage.class);
					i.putExtra("user_name", strUsername);
					i.putExtra("user_password", strPass);
					i.putExtra("MessageHolder", single_message);
					startActivity(i);
				}
			}

		});

		ml = new Mail(strUsername, strPass);

		
			InboxLoder ibloader = new InboxLoder(ml);
			ibloader.execute();
		

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		Bundle bundle = getArguments();

		strUsername = bundle.getString("username");
		strPass = bundle.getString("password");

		ml = new Mail(strUsername, strPass);

	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle icicle) {
		View v = li.inflate(R.layout.activity_inbox, vg, false);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.menu_inbox_messages, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_refresh:
			InboxLoder ibloader = new InboxLoder(ml);
			ibloader.execute();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class InboxLoder extends AsyncTask<Object, String, Boolean> {
		ProgressDialog pd;
		ProgressBar pb;

		Mail ml;

		// ArrayList<MessageHolder> inboxfolder = new
		// ArrayList<MessageHolder>();

		public InboxLoder(Mail mailbox) {
			ml = mailbox;
		}

		@Override
		protected void onPreExecute() {
			pb = new ProgressBar(getActivity());
			pb.setIndeterminate(true);
			lv.addHeaderView(pb);
			/*
			 * pd = new ProgressDialog(getActivity()); pd.setCancelable(false);
			 * pd.setIndeterminate(false); pd.setMessage("Loading Mails...");
			 * pd.show();
			 */
			Log.v("MyPeidLog", ml.getUser());
			Log.v("MyPeidLog", ml.getPass());

		}

		@Override
		protected void onProgressUpdate(String... values) {
			//pd.setMessage(values[0]);
			Log.v("MyPeidLog", values[0]);

		}

		@Override
		protected Boolean doInBackground(Object... params) {
			// TODO Auto-generated method stub
			publishProgress("Getting mails...");
			ArrayList<MessageHolder> inbox = new ArrayList<MessageHolder>();

			try {
				inbox = ml.getMailsFromInbox();

			} catch (Exception e) {
				publishProgress(e.getMessage());

			}
			publishProgress("Mails Fetched...");
			// publishProgress("Total Messages:"+String.valueOf(inbox.size()));
			// MessageHolder tmp = new
			// MessageHolder("rohit","kiru","love u","i love u","1-2-3");
			inboxfolder = inbox;

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				// publishProgress(msgs[1].getSubject().toString());
				lv.removeHeaderView(pb);
				if (result) {
					// Log.v("MyPeidLog", msgs.toString());
					publishProgress("Setting Adapter...");

					// publishProgress(messages.get(0).getSubject());
					Collections.sort(inboxfolder,
							new Comparator<MessageHolder>() {

								@Override
								public int compare(MessageHolder lhs,
										MessageHolder rhs) {
									// TODO Auto-generated method stub

									return rhs.imsgNumber - lhs.imsgNumber;
								}

							});

					msgAdapter = new InboxReaderAdapter(getActivity(),
							R.layout.row_inbox_single_message, inboxfolder);
					lv.setAdapter(msgAdapter);

				} else {
					publishProgress("Messages Empty");
				}
				// Log.v("MyPeidLog", "Done!");
			} catch (Exception e) {
				publishProgress(e.getMessage());
				// e.printStackTrace();
			}

			pd.dismiss();
		}
	}
}
