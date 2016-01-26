package com.rohitkaundal.mypeid;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;

import org.jsoup.Jsoup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class InboxReaderAdapter extends ArrayAdapter<MessageHolder> {
	Context ctx;
	ArrayList<MessageHolder> msgs;

	public InboxReaderAdapter(Context context, int resource,
			ArrayList<MessageHolder> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.ctx = context;
		this.msgs = objects;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// First let's verify the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_inbox_single_message,
					parent, false);
		}
		// Now we can fill the layout with the right values
		TextView txtfrom = (TextView) convertView.findViewById(R.id.txtFrom);
		TextView txtsubject = (TextView) convertView
				.findViewById(R.id.txtSubject);
		TextView txtmsg = (TextView) convertView.findViewById(R.id.txtMsg);

		MessageHolder msg = msgs.get(position);

		try {

			String strfrom = "from";
			String stremailmsg = "msg";
			String strsubject = "subject";
			String plaintxtmsg = "msg";

			if (msg.folder.toLowerCase().equalsIgnoreCase("sent")) {
				strfrom = msg.to;
			} else {
				strfrom = msg.from;
			}

			
			stremailmsg = Jsoup.parse(msg.message).text();
			strsubject = msg.subject;

			plaintxtmsg = (stremailmsg.length() < 50 ? stremailmsg.trim()
					: stremailmsg.substring(0, 50) + "...");

			txtfrom.setText(strfrom);
			txtsubject.setText(strsubject);

			txtmsg.setText(plaintxtmsg);

			Log.v("MyPeidLog", strsubject);
		} catch (Exception e) {
			Log.v("MyPeidLog", e.getMessage());
		}
		
		

		return convertView;
	}

}
