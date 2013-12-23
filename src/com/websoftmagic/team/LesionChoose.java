package com.websoftmagic.team;
import com.websoftmagic.team.R;


import com.websoftmagic.team.Login.TeamPost;
import com.websoftmagic.team.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.ProgramFragmentFixedFunction.Builder.Format;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class LesionChoose extends Activity {
	List<String> scan_dates = new ArrayList<String>();
	List<String> formatted_dates = new ArrayList<String>();
	private String scanDateToPass;
	List<String> reportOnline = new ArrayList<String>();
	
	List<String> save_lesion_scan_date = new ArrayList<String>();
	List<String> save_lesion_number = new ArrayList<String>();
	List<String> save_lesion_size = new ArrayList<String>();
	List<String> save_lesion_media_type = new ArrayList<String>();
	
	List<String> lesion_listview = new ArrayList<String>();
	
	private String beforeFilename;
	private String afterFilename; 
	private String beforeDateAndNumber;
	private String afterDateAndNumber;
	
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
 
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
	private static final String TAG = "LesionChoose";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.lesion_choose2);
        String pname = MySingleton.getInstance().patientName;
        String title = "Images for " + pname;
        TextView message = (TextView)findViewById(R.id.patient); 
        message.setText(title); 
        beforeFilename = "";
        afterFilename = "";
        if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
        new GetScans().execute();
	} 
	public void onResume() {
		super.onResume();
		//new GetScans().execute();
	}
	public void displayBothClicked (View  view){
		Intent myIntent = new Intent();
		if (beforeFilename.length() < 2) {
			AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionChoose.this);
    		alertbox.setTitle("Please select a BEFORE image from the list").setMessage("Thank you").setNeutralButton("OK", null).show();
    		return;
		}
		if (afterFilename.length() < 2) {
			AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionChoose.this);
    		alertbox.setTitle("Please select an AFTER image from the list").setMessage("Thank you").setNeutralButton("OK", null).show();
    		return;
		}
		//Log.i(TAG, "display " + beforeFilename + " and " + afterFilename);
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.LesionCompare");
    	myIntent.putExtra("beforeFilename", beforeFilename);
    	myIntent.putExtra("afterFilename", afterFilename);
    	myIntent.putExtra("afterDateAndNumber", afterDateAndNumber);
    	myIntent.putExtra("beforeDateAndNumber", beforeDateAndNumber);
    	startActivity(myIntent);
	}
	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) 
	      getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    String server = MySingleton.getInstance().imageDocServer;
		if (server == null) {
			Toast msg = Toast.makeText(getBaseContext(), "Lost server connection, need to login again!", Toast.LENGTH_SHORT);
			msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2); 
			msg.show();
			Intent myIntent = new Intent();
	    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Login");
	    	startActivity(myIntent);
		}
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	public void GoToPatients (MenuItem item){
		Intent myIntent = new Intent();
    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
    	startActivity(myIntent);
	}
	public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.activity_lesionchoose, menu);
	    return true;
	}
	class GetScans extends AsyncTask<Object, Object, Object> {
    	protected void onPreExecute() {
    		//Log.i(TAG, "test in pre execute");
    		setProgressBarIndeterminateVisibility(true);
    	 }
    	 protected void onCancelled() {
    		//Log.i(TAG, "Cancelled");
    		setProgressBarIndeterminateVisibility(false);
    		String message = "Got cancelled - " + content;
    		Toast msg = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
    		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2); 
    		//Displaying the alert
    		msg.show();
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionChoose.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionChoose.this);
    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
    	     } else {
    	        //Log.i(TAG, "Post exexs content=" + content); 
    	        String s = content.toString();
    	        try {
					processXML(s);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	    setProgressBarIndeterminateVisibility(false); 
    	  }
		@Override
		protected Object doInBackground(Object... paramsx) {
			String teamid = MySingleton.getInstance().teamid;
			String dbserver = MySingleton.getInstance().dbServer;
			String dbuser = MySingleton.getInstance().dbUser;
			String dbpassword = MySingleton.getInstance().dbPassword;
			String patient_id = MySingleton.getInstance().patientID;
			String URL = "http://" + dbserver + "/teamgetonlinelesions.php?teamid=" + teamid + "&patient_id=" + patient_id;
        	// build the URL string
	        try {
	        	
	            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
	            HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
	            ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
	            HttpPost httpPost = new HttpPost(URL);
	            
	            String authString = (dbuser + ":" + dbpassword);
	            httpPost.addHeader("Authorization", "Basic " + 
	            Base64.encodeToString(authString.getBytes(),Base64.NO_WRAP));
	            
	            //Response from the Http Request
	            response = httpclient.execute(httpPost);
	  
	            StatusLine statusLine = response.getStatusLine();
	            //Check the Http Request for success
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                content = out.toString();
	            }
	            else{
	                //Closes the connection.
	                Log.w("HTTP1:",statusLine.getReasonPhrase());
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            Log.w("HTTP2:",e );
	            content = e.getMessage();
	            error = true;
	            cancel(true);
	        } catch (IOException e) {
	            Log.w("HTTP3:",e );
	            content = e.getMessage();
	            error = true;
	            cancel(true);
	        }catch (Exception e) {
	            Log.w("HTTP4:",e );
	            content = e.getMessage();
	            error = true;
	            cancel(true);
	        }
	        //Log.i(TAG, "content=" + content);
	        return content;
		}


    	
    }
	   public void processXML(String content) throws ClientProtocolException, IOException {
	    /*
		    String scan_teamid[] = null;
	    	String scan_patient_id[] = null;
	    	String scan_date[] = null;
	    	String scan_report_online[] = null;
	    */	
	    	
	    	String lesion_scan_date[] = null;
	    	String lesion_number[] = null;
	    	String lesion_size[] = null;
	    	String lesion_media_type[] = null;
	    	
	    	
	    	
	    	
	    	try {
	    		//Log.i(TAG,"Content =" + content);
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("lesion");

	    		/** Assign String array length by arraylist size */
	    		/*
	    		scan_teamid = new String[nodeList.getLength()];
	    		scan_patient_id = new String[nodeList.getLength()];
	    	    scan_date = new String[nodeList.getLength()];
	    		scan_report_online = new String[nodeList.getLength()];
	    		*/
	    		lesion_scan_date = new String[nodeList.getLength()];
	    		lesion_number = new String[nodeList.getLength()];
	    		lesion_size = new String[nodeList.getLength()];
	    		lesion_media_type = new String[nodeList.getLength()];
	    		
	    		/*
	    		scan_dates = new ArrayList<String>();
		        formatted_dates = new ArrayList<String>(); 
		        */
		        save_lesion_scan_date = new ArrayList<String>();
		        save_lesion_number = new ArrayList<String>();
		        save_lesion_size = new ArrayList<String>();
		        save_lesion_media_type = new ArrayList<String>();
		        lesion_listview = new ArrayList<String>();
		        
		        
	    		for (int i = 0; i < nodeList.getLength(); i++) {
	    			//Log.i(TAG, "here in nodelist - length is " + nodeList.getLength());
	    		Node node = nodeList.item(i);
	    		/*
	    		scan_teamid[i] = new String("");
	    		scan_patient_id[i] = new String("");
	    		scan_date[i] = new String("");
	    		scan_report_online[i] = new String("");
	    		*/
	    		lesion_scan_date[i] = new String("");
	    		lesion_number[i] = new String("");
	    		lesion_size[i] = new String("");
	    		lesion_media_type[i] = new String("");
	    		
	    		
	    		Element fstElmnt = (Element) node;
	    		
	    		NodeList lesion_scan_dateList = fstElmnt.getElementsByTagName("lesion_scan_date");
	    		Element lesion_scan_dateElement = (Element) lesion_scan_dateList.item(0);
	    		lesion_scan_dateList = lesion_scan_dateElement.getChildNodes();
	    		lesion_scan_date[i] =  ((Node) lesion_scan_dateList.item(0)).getNodeValue();
	    		
	    		save_lesion_scan_date.add(lesion_scan_date[i]);
	    		
	    		NodeList lesion_numberList = fstElmnt.getElementsByTagName("lesion_number");
	    		Element lesion_numberElement = (Element) lesion_numberList.item(0);
	    		lesion_numberList = lesion_numberElement.getChildNodes();
	    		lesion_number[i] = ((Node) lesion_numberList.item(0)).getNodeValue();
	    		
	    		save_lesion_number.add(lesion_number[i]);
	    		
	    		NodeList lesion_sizeList = fstElmnt.getElementsByTagName("lesion_size");
	    		Element lesion_sizeElement = (Element) lesion_sizeList.item(0);
	    		lesion_sizeList = lesion_sizeElement.getChildNodes();
	    		lesion_size[i] = ((Node) lesion_sizeList.item(0)).getNodeValue();
	    		
	    		save_lesion_size.add(lesion_size[i]);
	    		
	    		NodeList lesion_media_typeList = fstElmnt.getElementsByTagName("lesion_media_type");
	    		Element lesion_media_typeElement = (Element) lesion_media_typeList.item(0);
	    		lesion_media_typeList = lesion_media_typeElement.getChildNodes();
	    		lesion_media_type[i] = ((Node) lesion_media_typeList.item(0)).getNodeValue();
	    		
	    		save_lesion_media_type.add(lesion_media_type[i]);
	    		
	    		String temp_date = lesion_scan_date[i];
	    		String dateToPick = temp_date.substring(5,7) + "/" + temp_date.substring(8,10) + "/" + temp_date.substring(2,4)
	    		+ " #" + lesion_number[i] + " (" + lesion_size[i] + "mm)";
	    		lesion_listview.add(dateToPick);
	    		
	    		}
	    		} catch (Exception e) {
	    		System.out.println("XML Pasing Excpetion = " + e);
	    		}
	    	/*
	    	    for (int i = 0; i < patient_teamid.length; i++) {
	    	    	Log.i(TAG, "teamid = " + patient_teamid[i]);
	    	    }
	    	    for (int i = 0; i < patient_id.length; i++) {
	    	    	Log.i(TAG, "patient_id = " + patient_id[i]);
	    	    }
	    	    for (int i = 0; i < patient_name.length; i++) {
	    	    	Log.i(TAG, "patient_name = " + patient_name[i]);
	    	    }
	    	    for (int i = 0; i < patient_study_id.length; i++) {
	    	    	Log.i(TAG, "patient_study_id = " + patient_study_id[i]);
	    	    }
	    	    for (int i = 0; i < patient_doctor.length; i++) {
	    	    	Log.i(TAG, "patient_doctor = " + patient_doctor[i]);
	    	    }
	    	  */
	    		/*
	    	    Log.i(TAG,"begin add to sr"); 
	    	    
	    	   //build results and
	    	    results = new ArrayList<PatientResults>();
	    	   
	    	    PatientResults sr = new PatientResults();
	    	    for (int i = 0; i < patient_teamid.length; i++) {
	    	    	// if teamid is 0 then no matching patients were found
	    	    	String myid = patient_teamid[i];
	    	    	if (myid.equalsIgnoreCase("0")) {
	    	    		AlertDialog.Builder alertbox = new AlertDialog.Builder(Patients.this);
		        		alertbox.setTitle("Sorry, No Matches").setMessage("Please try again").setNeutralButton("OK", null).show();
		        		return;
	    	    	}
	    	    	//Log.i(TAG,"begin sr");
	    	    	sr = new PatientResults();
	   	     		sr.setName(patient_name[i] + "- ID# " + patient_id[i]);
	   	     		sr.setCityState(patient_doctor[i] + " / " + patient_study_id[i]);
	   	     		sr.setPatient_id(patient_id[i]);
	   	     		sr.setPatient_name(patient_name[i]);
	   	     		sr.setPatient_study_id(patient_study_id[i]);
	   	     		sr.setPatient_doctor(patient_doctor[i]);
	   	     		//sr.setPhone(patient_study_id[i]);
	   	     		//Log.i(TAG,"add sr");
	   	     		//Log.i(TAG,"name=" + patient_name[i]);
	   	     		results.add(sr);
	    	    }
	    	    */
	    	    
	    	    final ListView lv = (ListView) findViewById(R.id.listView1);
	    	    final ListView lv2 = (ListView) findViewById(R.id.listView2);
	    	    
	    	    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,scan_date);
	    	   // ArrayAdapter<String> adapter = new ArrayAdapter<String>(Scans.this,R.layout.simple_list_center,scan_date);
	    	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(LesionChoose.this,R.layout.simple_list_lesions,lesion_listview);
	    	    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(LesionChoose.this,R.layout.simple_list_lesions,lesion_listview);
	    	    
	    	    lv.setAdapter(adapter);
	    	    lv2.setAdapter(adapter2);
	    	    /*
	    	    for (int i = 0; i < save_lesion_scan_date.size(); i++) {
	    	    	Log.i(TAG, "scan date = " + save_lesion_scan_date.get(i));
	    	    }
	    	    */
	    	    
	            //lv.setAdapter(new PatientAdapter(this, results)); 
	     
	            lv.setOnItemClickListener(new OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	                	TextView b4message = (TextView)findViewById(R.id.beforeMessage); 
	                    b4message.setText(lesion_listview.get(position)); 
	                    String temp_date = save_lesion_scan_date.get(position);
	    	    		beforeFilename = MySingleton.getInstance().patientID + "-" + temp_date.substring(5,7) + temp_date.substring(8,10) + temp_date.substring(2,4)
	    	    		+ "-" + save_lesion_number.get(position) + "." + save_lesion_media_type.get(position);
	    	    		beforeDateAndNumber = temp_date.substring(5,7) + "/" + temp_date.substring(8,10) + "/" + temp_date.substring(2,4)
	    	    				+ " (Lesion #" + save_lesion_number.get(position) + ")";
	                }
	            });
	            lv2.setOnItemClickListener(new OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	                	TextView aftermessage = (TextView)findViewById(R.id.afterMessage); 
	                    aftermessage.setText(lesion_listview.get(position)); 
	                    String temp_date = save_lesion_scan_date.get(position);
	    	    		afterFilename = MySingleton.getInstance().patientID + "-" + temp_date.substring(5,7) + temp_date.substring(8,10) + temp_date.substring(2,4)
	    	    		+ "-" + save_lesion_number.get(position) + "." + save_lesion_media_type.get(position);
	    	    		afterDateAndNumber = temp_date.substring(5,7) + "/" + temp_date.substring(8,10) + "/" + temp_date.substring(2,4)
	    	    				+ " (Lesion #" + save_lesion_number.get(position) + ")";
	                }
	            });
	    	    
	    }
}

