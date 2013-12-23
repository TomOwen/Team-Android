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
 
public class LesionAdapter extends BaseAdapter {
    private static final String TAG = "LesionAdapter";

	private static ArrayList<LesionResults> searchArrayList;
 
    private LayoutInflater mInflater;
 
    public LesionAdapter(Context context, ArrayList<LesionResults> results) {
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
        	
            convertView = mInflater.inflate(R.layout.lesions_row_view, null);
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
        String lesion_number = searchArrayList.get(position).getlesion_number();
        String target = searchArrayList.get(position).getlesion_target();
        String lesion_size = searchArrayList.get(position).getlesion_size();
        String lesionType = "";
        if (target.equalsIgnoreCase("N")) lesionType = "N";  
        String line1 = "Lesion " + lesionType + "T" + lesion_number + " Size of Tumor " + lesion_size + " mm";
        holder.txtLine1.setText(line1);
        //Log.i(TAG,"line1=" + line1);
        String line2 = searchArrayList.get(position).getlesion_comment();
        holder.txtLine2.setText(line2);
        //Log.i(TAG,"after comment");
         
        //holder.txtPhone.setText(searchArrayList.get(position).getPhone());
 
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

