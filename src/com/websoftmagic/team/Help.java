package com.websoftmagic.team;

import com.websoftmagic.team.R;


import android.view.View.OnClickListener;
import com.websoftmagic.team.Login.TeamPost;
import com.websoftmagic.team.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
 
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
 
public class Help extends Activity {
	public  ArrayList<HelpResults> results = new ArrayList<HelpResults>();
	private static final String TAG = "Help";
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        results = new ArrayList<HelpResults>();
        HelpResults sr = new HelpResults();
        
        sr = new HelpResults();
    	sr.sethelp_menu_text("TEAM Overview");
    	results.add(sr);
    	
    	sr = new HelpResults();
     	sr.sethelp_menu_text("Contact TEAM Support");
     	results.add(sr);

    	sr = new HelpResults();
     	sr.sethelp_menu_text("Login/Logout and Passwords");
     	results.add(sr); 
     	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("All About Patients");
     	results.add(sr); 
     	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("Patient Response and Reports");
     	results.add(sr); 
     	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("How to add a New Doctor");
     	results.add(sr); 
     	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("How to add a New Clinical Study/Trial");
     	results.add(sr); 
       	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("Managing Patient Scans");
     	results.add(sr); 
     	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("How to Delete a Patient/Scan/Lesion");
     	results.add(sr); 
     	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("Patient Scan Images Ð Online");
     	results.add(sr);  
     	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("Scan & Internal Study Reports");
     	results.add(sr); 
     	
     	sr = new HelpResults();
     	sr.sethelp_menu_text("Patient and Study Graphs");
     	results.add(sr); 
     	
      	sr = new HelpResults();
     	sr.sethelp_menu_text("RECIST Web site");
     	results.add(sr); 
     	
      	sr = new HelpResults();
     	sr.sethelp_menu_text("Adverse Events Web site");
     	results.add(sr); 
     	
        final ListView lv = (ListView) findViewById(R.id.helpListView);
    	lv.setAdapter(new HelpAdapter(this, results));
    	lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lv.getItemAtPosition(position);
                HelpResults fullObject = (HelpResults)o;
                //Log.i(TAG,"Clicked on " + position);
                String filename = "overview.html";
                switch (position) {
                    case 0:  filename = "overview.html";
                             break;
                    case 1:  filename = "contact.html";
                    		break;
                    case 2:  filename = "login.html";
            				break;
                    case 3:  filename = "patient-add.html";
    					break;
                    case 4:  filename = "patient-response.html";
    					break;
                    case 5:  filename = "doctor-add.html";
    					break;
                    case 6:  filename = "study-add.html";
    					break;
                    case 7:  filename = "scan-add.html";
     					break; 
                    case 8:  filename = "delete.html";
 						break;
                    case 9:  filename = "image-online.html";
						break;
                    case 10:  filename = "reports-online.html";
						break;	
                    case 11:  filename = "graph-online.html";
					break;
                    case 12:  
                    	String url = "http://www.recist.com";
                    	Intent i = new Intent(Intent.ACTION_VIEW);
                    	i.setData(Uri.parse(url));
                    	startActivity(i); 
                    	return;
                    case 13:  
                		String url2 = "http://ctep.cancer.gov/protocolDevelopment/electronic_applications/ctc.htm";
                		Intent i2 = new Intent(Intent.ACTION_VIEW);
                		i2.setData(Uri.parse(url2));
                		startActivity(i2);
                		return;
                    default: filename = "overview.html";
                             break;
                }
                
                Intent myIntent = new Intent();
                myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.ViewHtml");
                myIntent.putExtra("filename", filename);
                startActivity(myIntent);
                /*
                //Toast.makeText(Patients.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
                //Log.i(TAG,"saving patient_id->" + fullObject.getPatient_id());
                //MySingleton.getInstance().setPatientID(fullObject.getPatient_id());
                //MySingleton.getInstance().setPatientName(fullObject.getPatient_name());
                // go to PatientEdit
                Intent myIntent = new Intent();
    	    	//myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
    	    	//myIntent.setClassName(this, "com.websoftmagic.team.Patients.Patients");
    	    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.DoctorEdit");
    	    	myIntent.putExtra("doctor_name", fullObject.getdoctor_name());
    	    	myIntent.putExtra("doctor_info", fullObject.getsdoctor_info());
    	    	startActivity(myIntent); 
    	    	*/ 
                
            }
        });
	}
	
	public void onResume() {
		super.onResume();
	}
	public void GoToPatients (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
    	startActivity(myIntent);
	}
	public void GoToDoctors (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Doctors");
    	startActivity(myIntent);
	}
	public void GoToStudies (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Studies");
    	startActivity(myIntent);
	}
	public void LogOff (View view){
		MySingleton.getInstance().setImageDocServer("");
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Login");
    	startActivity(myIntent);
	}
	public void SendEmail (View view){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"tho@websoftmagic.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Team Support Question");
		i.putExtra(Intent.EXTRA_TEXT   , "Enter your Question...");
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(Help.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.activity_help, menu);
	    return true;
	}
}




