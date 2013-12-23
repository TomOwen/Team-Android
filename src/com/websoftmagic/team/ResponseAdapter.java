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
 
public class ResponseAdapter extends BaseAdapter {
    private static final String TAG = "ResponseAdapter";

	private static ArrayList<ResponseResults2> searchArrayList;
 
    private LayoutInflater mInflater;
 
    public ResponseAdapter(Context context, ArrayList<ResponseResults2> results2) {
        searchArrayList = results2;
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
        	
            convertView = mInflater.inflate(R.layout.response_lesion_row_view, null);
            holder = new ViewHolder();
            holder.txtLine1 = (TextView) convertView.findViewById(R.id.lesionLine1);
            holder.txtLine2 = (TextView) convertView.findViewById(R.id.lesionLine2);
            holder.txtLine3 = (TextView) convertView.findViewById(R.id.lesionLine3);
            //holder.txtDescription = (TextView) convertView.findViewById(R.id.lesionDescription);
           // holder.txtPatientID = (TextView) convertView.findViewById(R.id.lesionLine1);
            //holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);
 
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            
        }

        // build line 1 - Target #1 Current 0 mm
        String lesion_number = searchArrayList.get(position).getlesion_number();
        String target = searchArrayList.get(position).getlesion_target();
        String lesionType = "Target #";
        if (target.equalsIgnoreCase("N")) lesionType = "Non Target #"; 
        String lesion_size = searchArrayList.get(position).getcurrent_size();
        // New and/or Lymph - "" or "(New)" or "(Lymph)" or "(New Lymph)"
        String new_lesion = searchArrayList.get(position).getnew_lesion();
        String lesion_node = searchArrayList.get(position).getlesion_node();
        String newornode = "";
        if ((new_lesion.equalsIgnoreCase("Y")) && (lesion_node.equalsIgnoreCase("N"))){
        	newornode = "(New)";
        }
        if ((new_lesion.equalsIgnoreCase("Y")) && (lesion_node.equalsIgnoreCase("Y"))){
        	newornode = "(New Lymph)";
        }
        if ((new_lesion.equalsIgnoreCase("N")) && (lesion_node.equalsIgnoreCase("Y"))){
        	newornode = "(Lymph)";
        }
        String line1 = lesionType + lesion_number + " Current " + lesion_size + " mm " + newornode;
        holder.txtLine1.setText(line1);
        // build line 2 Baseline on mm/dd/yy was 11 mm (now -100%)
        String baseline = searchArrayList.get(position).getbaseline_date();
        String baselinesize  = searchArrayList.get(position).getbaseline_size();
        String pct = searchArrayList.get(position).getbaseline_percent_change();
        String line2 = "Baseline on " + baseline + " was " + baselinesize + " mm (now " + pct + "%)";
        holder.txtLine2.setText(line2);
        // build line 3 Baseline on mm/dd/yy was 11 mm (now -100%)
        String smalldate = searchArrayList.get(position).getsmallest_date();
        String smallsize  = searchArrayList.get(position).getsmallest_size();
        String pct2 = searchArrayList.get(position).getsmallest_percent_change();
        String line3 = "Smallest on " + smalldate + " was " + smallsize + " mm (now " + pct2 + "%)";
        //Log.i(TAG,"line3->"+line3);
        holder.txtLine3.setText(line3);
        //holder.txtLine1.setBackgroundColor(0xBABABA);
 
        return convertView;
    }
 
    static class ViewHolder {
        TextView txtLine1;
        TextView txtLine2;
        TextView txtLine3;
        //TextView txtDescription;
        //TextView txtPatientID;
        //TextView txtPhone; 
    }
}


