package com.websoftmagic.team;
import java.util.ArrayList;



import com.websoftmagic.team.R;
import com.websoftmagic.team.R.id;
import com.websoftmagic.team.R.layout;
 
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class DoctorAdapter extends BaseAdapter {
    private static final String TAG = "DoctorAdapter";
    

	private static ArrayList<DoctorResults> searchArrayList;
 
    private LayoutInflater mInflater;
 
    public DoctorAdapter(Context context, ArrayList<DoctorResults> results) {
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
        	
            convertView = mInflater.inflate(R.layout.doctors_row_view, null);
            holder = new ViewHolder();
            holder.txtLine1 = (TextView) convertView.findViewById(R.id.doctorLine1);
            holder.txtLine2 = (TextView) convertView.findViewById(R.id.doctorLine2);
            //holder.txtDescription = (TextView) convertView.findViewById(R.id.lesionDescription);
           // holder.txtPatientID = (TextView) convertView.findViewById(R.id.lesionLine1);
            //holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);
 
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            
        }

        // build line 1
        String doctor_name = searchArrayList.get(position).getdoctor_name();
        holder.txtLine1.setText(doctor_name);
        
        // build line 2
        String doctor_info = searchArrayList.get(position).getsdoctor_info();
        if (doctor_info.equalsIgnoreCase("n/a")) {
        	doctor_info = "";
        }
        holder.txtLine2.setText(doctor_info);
        
        //labelTV.setGravity(Gravity.CENTER
        //holder.txtLine1.setGravity(Gravity.CENTER_HORIZONTAL);
 
        return convertView;
    }
 
    static class ViewHolder {
        TextView txtLine1;
        TextView txtLine2;
        //TextView txtDescription;
        TextView txtPatientID;
        //TextView txtPhone; 
    }
}

