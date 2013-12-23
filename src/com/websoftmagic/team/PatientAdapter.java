package com.websoftmagic.team;

	import java.util.ArrayList;


	 
	import com.websoftmagic.team.R;
import com.websoftmagic.team.R.id;
import com.websoftmagic.team.R.layout;
	 
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
	 
	public class PatientAdapter extends BaseAdapter {
	    private static ArrayList<PatientResults> searchArrayList;
	 
	    private LayoutInflater mInflater;
	 
	    public PatientAdapter(Context context, ArrayList<PatientResults> results) {
	        searchArrayList = results;
	        mInflater = LayoutInflater.from(context);
	    }
	 
	    public int getCount() {
	        return searchArrayList.size();
	    }
	 
	    public Object getItem(int position) {
	        return searchArrayList.get(position);
	    }
	 
	    public long getItemId(int position) {
	        return position;
	    }
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder;
	        if (convertView == null) {
	        	
	            convertView = mInflater.inflate(R.layout.patients_row_view, null);
	            holder = new ViewHolder();
	            holder.txtName = (TextView) convertView.findViewById(R.id.name);
	            holder.txtCityState = (TextView) convertView.findViewById(R.id.cityState);
	            //holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);
	 
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	            
	        }

	        holder.txtName.setText(searchArrayList.get(position).getName());
	        holder.txtCityState.setText(searchArrayList.get(position).getCityState());
	        //holder.txtPhone.setText(searchArrayList.get(position).getPhone());
	 
	        return convertView;
	    }
	 
	    static class ViewHolder {
	        TextView txtName;
	        TextView txtCityState;
	        TextView txtPatientID;
	        //TextView txtPhone; 
	    }
	}

