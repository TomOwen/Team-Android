package com.websoftmagic.team;

import com.websoftmagic.team.R;


import android.view.View.OnClickListener;
import com.websoftmagic.team.Login.TeamPost;
import com.websoftmagic.team.PatientEdit.DeletePatient;
import com.websoftmagic.team.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class Doctors extends Activity {
	ToggleButton tb;
	public String isReportOnline;
	public  ArrayList<DoctorResults> results = new ArrayList<DoctorResults>();
	
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
 
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
	private static final String TAG = "Doctors";
	private static  String option = "1";
	private static  String search = "";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.doctors);
        String test = MySingleton.getInstance().teamid;
        //Log.i(TAG,"teamid=" + test);
        ReadDoctors();
	} 
	
	public void onResume() {
		//Log.i(TAG,"Resuming doctors");
		super.onResume();
		ReadDoctors();
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
	public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
    	startActivity(myIntent);
	}
	public void AddDoctorClicked (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.DoctorAdd");
    	startActivity(myIntent);
		
	}
	public void GoToPatients (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
    	startActivity(myIntent);
		
	}
	public void GoToStudies (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Studies");
    	startActivity(myIntent);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("Y")) {
			getMenuInflater().inflate(R.menu.activity_doctors, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_doctors_v, menu);
		}
	    return true;
	}
	public void ReadDoctors () {
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		GetSearchResults();
        
        
	}
	private void GetSearchResults(){
	     // async in background to get content
	     // xml parse content and add to  sr Patient results array
	     new GetDoctors().execute();
	     return;
	    }
	class GetDoctors extends AsyncTask<Object, Object, Object> {
    	protected void onPreExecute() {
    		
    		setProgressBarIndeterminateVisibility(true);
    	 }
    	 protected void onCancelled() {
    		
    		setProgressBarIndeterminateVisibility(false);
    		String message = "Got cancelled - " + content;
    		Toast msg = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
    		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2); 
    		//Displaying the alert
    		msg.show();
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(Doctors.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Doctors.this);
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
			String username = MySingleton.getInstance().userName;
			String URL = "http://" + dbserver + "/teamgetdoctors.php?teamid=" + teamid + "&user_name=" + username;
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
		 
		 String doctor_teamid[] = null;
	     String doctor_name[] = null;
	     String doctor_info[] = null;
	     
	     
	    	 
	    	try {
	    		
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("doctor");

	    		/** Assign String array length by arraylist size */
	    		doctor_teamid = new String[nodeList.getLength()];
	    		doctor_name = new String[nodeList.getLength()];
	    		doctor_info = new String[nodeList.getLength()];
	    		
	    		for (int i = 0; i < nodeList.getLength(); i++) {

	    		Node node = nodeList.item(i);

	    		doctor_teamid[i] = new String("");
	    		doctor_name[i] = new String("");
	    		doctor_info[i] = new String("");
	    		
	    		Element fstElmnt = (Element) node;
	    		
	    		NodeList doctor_teamidList = fstElmnt.getElementsByTagName("doctor_teamid");
	    		Element doctor_teamidElement = (Element) doctor_teamidList.item(0);
	    		doctor_teamidList = doctor_teamidElement.getChildNodes();
	    		doctor_teamid[i] =  ((Node) doctor_teamidList.item(0)).getNodeValue();
	    		
	    		NodeList doctor_nameList = fstElmnt.getElementsByTagName("doctor_name");
	    		Element doctor_nameElement = (Element) doctor_nameList.item(0);
	    		doctor_nameList = doctor_nameElement.getChildNodes();
	    		doctor_name[i] =  ((Node) doctor_nameList.item(0)).getNodeValue();
	    		
	    		NodeList doctor_infoList = fstElmnt.getElementsByTagName("doctor_info");
	    		Element doctor_infoElement = (Element) doctor_infoList.item(0);
	    		doctor_infoList = doctor_infoElement.getChildNodes();
	    		doctor_info[i] = ((Node) doctor_infoList.item(0)).getNodeValue();
	    		
	    		}
	    		} catch (Exception e) {
	    		System.out.println("XML Pasing Excpetion = " + e);
	    		}
	    	/*
	    	    for (int i = 0; i < patient_teamid.length; i++) {
	    	    	Log.i(TAG, "teamid = " + patient_teamid[i]);
	    	    }
	    	  */
	    	    //Log.i(TAG,"begin add to sr"); 
	    	    
	    	   //build results and
	    	    results = new ArrayList<DoctorResults>();
	    	   
	    	    DoctorResults sr = new DoctorResults();
	    	    for (int i = 0; i < doctor_teamid.length; i++) {
	    	    	// if teamid is 0 then no matching patients were found
	    	    	String myid = doctor_teamid[i];
	    	    	if (myid.equalsIgnoreCase("0")) {
	    	    		// no studies to display
		        		return;
	    	    	}
	    	    	//Log.i(TAG,"begin sr");
	    	 
	    	    	sr = new DoctorResults();
	    	    	sr.setdoctor_teamid(doctor_teamid[i]);
	    	    	sr.setdoctor_name(doctor_name[i]);
	    	    	sr.setdoctor_info(doctor_info[i]);

	    	    
	   	     		//sr.setCityState(patient_doctor[i] + " / " + patient_study_id[i]);
	   	     		results.add(sr);
	    	    }
	    	    
	    	    
	    	    final ListView lv = (ListView) findViewById(R.id.doctorListView);
	            lv.setAdapter(new DoctorAdapter(this, results));
	     
	            lv.setOnItemClickListener(new OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	                    Object o = lv.getItemAtPosition(position);
	                    DoctorResults fullObject = (DoctorResults)o;
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
	                    
	                }
	            });
	    	    
	    }
}




