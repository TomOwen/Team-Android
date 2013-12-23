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
import android.widget.ImageView;
import android.widget.TextView;
 
public class HelpAdapter extends BaseAdapter {
    private static final String TAG = "HelpAdapter";

	private static ArrayList<HelpResults> searchArrayList;
 
    private LayoutInflater mInflater;
 
    public HelpAdapter(Context context, ArrayList<HelpResults> results) {
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
        	
            convertView = mInflater.inflate(R.layout.help_row_view, null);
            holder = new ViewHolder();
            holder.txtLine1 = (TextView) convertView.findViewById(R.id.helpLine1);
            holder.imgLine1 = (ImageView) convertView.findViewById(R.id.imageView1);
           
            //holder.txtDescription = (TextView) convertView.findViewById(R.id.lesionDescription);
           // holder.txtPatientID = (TextView) convertView.findViewById(R.id.lesionLine1);
            //holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);
 
            convertView.setTag(holder); 
        } else {
            holder = (ViewHolder) convertView.getTag();
            
        }

        // menu line1
        String menu_text = searchArrayList.get(position).gethelp_menu_text();
        holder.txtLine1.setText(menu_text);
 
        switch (position) {
        	case 0: holder.imgLine1.setImageResource(R.drawable.team114114);
        	break;
        	case 1: holder.imgLine1.setImageResource(R.drawable.contact);
        	break;
        	case 2: holder.imgLine1.setImageResource(R.drawable.icon_login);
        	break;
        	case 3: holder.imgLine1.setImageResource(R.drawable.patients);
        	break;
        	case 4: holder.imgLine1.setImageResource(R.drawable.icon_reports);
        	break;
        	case 5: holder.imgLine1.setImageResource(R.drawable.docicon);
        	break; 
        	case 6: holder.imgLine1.setImageResource(R.drawable.study);
        	break; 
        	case 7: holder.imgLine1.setImageResource(R.drawable.scans);
        	break;  
        	case 8: holder.imgLine1.setImageResource(R.drawable.edit);
        	break;
        	case 9: holder.imgLine1.setImageResource(R.drawable.scans);
        	break;
        	case 10: holder.imgLine1.setImageResource(R.drawable.icon_reports);
        	break;
        	case 11: holder.imgLine1.setImageResource(R.drawable.icon_graph);
        	break;
        	case 12: holder.imgLine1.setImageResource(R.drawable.team114114);
        	break;
        	case 13: holder.imgLine1.setImageResource(R.drawable.adverse);
        	break;
        	default: holder.imgLine1.setImageResource(R.drawable.team114114);
        	break;
        }
        return convertView;
    }
 
    static class ViewHolder {
        TextView txtLine1;
        ImageView imgLine1;
    }
}



