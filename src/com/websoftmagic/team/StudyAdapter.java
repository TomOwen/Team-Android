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
 
public class StudyAdapter extends BaseAdapter {
    private static final String TAG = "StudyAdapter";

	private static ArrayList<StudyResults> searchArrayList;
 
    private LayoutInflater mInflater;
 
    public StudyAdapter(Context context, ArrayList<StudyResults> results) {
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
        	
            convertView = mInflater.inflate(R.layout.studies_row_view, null);
            holder = new ViewHolder();
            holder.txtLine1 = (TextView) convertView.findViewById(R.id.lesionLine1);
            holder.txtLine2 = (TextView) convertView.findViewById(R.id.lesionLine2);
            //holder.txtDescription = (TextView) convertView.findViewById(R.id.lesionDescription);
           // holder.txtPatientID = (TextView) convertView.findViewById(R.id.lesionLine1);
            //holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);
 
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            
        }

        // build line 1
        String study_id = searchArrayList.get(position).getstudy_id();
        holder.txtLine1.setText(study_id);
        
        // build line 2
        String study_name = searchArrayList.get(position).getstudy_name();
        String study_owner = searchArrayList.get(position).getstudy_owner();
        holder.txtLine2.setText(study_name + "(" + study_owner + ")");
        
 
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

