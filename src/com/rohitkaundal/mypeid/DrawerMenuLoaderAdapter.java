package com.rohitkaundal.mypeid;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrawerMenuLoaderAdapter extends ArrayAdapter<MailBoxFolder> {

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	private Context _ctx = null;
	private ArrayList<MailBoxFolder> mbFolders = null;
	
	public DrawerMenuLoaderAdapter(Context context, int resource, ArrayList<MailBoxFolder> mbfolders) {
		super(context, resource, mbfolders);
		// TODO Auto-generated constructor stub
		this.set_ctx(context);
		this.setMbFolders(mbfolders);
	}

	public Context get_ctx() {
		return _ctx;
	}

	public void set_ctx(Context _ctx) {
		this._ctx = _ctx;
	}

	public ArrayList<MailBoxFolder> getMbFolders() {
		return mbFolders;
	}

	public void setMbFolders(ArrayList<MailBoxFolder> mbFolders) {
		this.mbFolders = mbFolders;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		// First let's verify the convertView is not null
	    if (convertView == null) {
	        // This a new view we inflate the new layout
	        LayoutInflater inflater = (LayoutInflater) this.get_ctx().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.drawer_menu_item, parent, false);
	    }
	    
	    TextView txtFolderTitle = (TextView)convertView.findViewById(R.id.txtFolderTitle);
	    
	    TextView txtFolderCount = (TextView)convertView.findViewById(R.id.txtCount);
	    
	    MailBoxFolder mb_folder = getItem(position);
	    
	    try{
	    	txtFolderTitle.setText(mb_folder.get_title());
	    	txtFolderCount.setText(Integer.toString(mb_folder.get_count()));
	    	
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    return convertView;
	    
		
	}

}
