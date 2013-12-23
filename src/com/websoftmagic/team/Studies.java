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

public class Studies extends Activity {
	ToggleButton tb;
	public String isReportOnline;
	public  ArrayList<StudyResults> results = new ArrayList<StudyResults>();
	
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
 
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
	private static final String TAG = "Studies";
	private static  String option = "1";
	private static  String search = "";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.studies);
        String test = MySingleton.getInstance().teamid;
        
        if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
        ReadStudies();
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
	public void onResume() {
		
		super.onResume();
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		ReadStudies();
	}
	public void AddPatient (MenuItem item){
		Log.i(TAG,"add patient clicked");
	}
	public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
    	startActivity(myIntent);
	}
	public void AddStudyClicked (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.StudyAdd");
    	startActivity(myIntent);
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("Y")) {
			getMenuInflater().inflate(R.menu.activity_studies, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_studies_v, menu);
		}
	    return true;
	}
	public void ReadStudies () {
		GetSearchResults();
        
        
	}
	private void GetSearchResults(){
	     // async in background to get content
	     // xml parse content and add to  sr Patient results array
	     new GetStudies().execute();
	     return;
	    }
	class GetStudies extends AsyncTask<Object, Object, Object> {
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
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(Studies.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Studies.this);
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
			//Log.i(TAG, "test in background");
			String teamid = MySingleton.getInstance().teamid;
			String dbserver = MySingleton.getInstance().dbServer;
			String dbuser = MySingleton.getInstance().dbUser;
			String dbpassword = MySingleton.getInstance().dbPassword;
			String username = MySingleton.getInstance().userName;

			String URL = "http://" + dbserver + "/teamgetstudies.php?teamid=" + teamid + "&user_name=" + username;
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
		 String study_teamid[] = null;
	     String study_id[] = null;
	     String study_owner[] = null;
	     String study_name[] = null;
	     String study_url[] = null;
	     String study_percentpr[] = null;
	     String study_percentpd[] = null;
	     String study_seats[] = null;
	     
	    	 
	    	try {
	    		//Log.i(TAG,"Content =" + content);
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("studies");

	    		/** Assign String array length by arraylist size */
	    		study_teamid = new String[nodeList.getLength()];
	    		study_id = new String[nodeList.getLength()];
	    		study_owner = new String[nodeList.getLength()];
	    		study_name = new String[nodeList.getLength()];
	    		study_url = new String[nodeList.getLength()];
	    		study_percentpr = new String[nodeList.getLength()];
	    		study_percentpd = new String[nodeList.getLength()];
	    		study_seats = new String[nodeList.getLength()];
	    		
	    		for (int i = 0; i < nodeList.getLength(); i++) {

	    		Node node = nodeList.item(i);

	    		study_teamid[i] = new String("");
	    		study_id[i] = new String("");
	    		study_owner[i] = new String("");
	    		study_name[i] = new String("");
	    		study_url[i] = new String("");
	    		study_percentpr[i] = new String("");
	    		study_percentpd[i] = new String("");
	    		study_seats[i] = new String("");
	    		
	    		Element fstElmnt = (Element) node;
	    		
	    		NodeList study_teamidList = fstElmnt.getElementsByTagName("study_teamid");
	    		Element study_teamidElement = (Element) study_teamidList.item(0);
	    		study_teamidList = study_teamidElement.getChildNodes();
	    		study_teamid[i] =  ((Node) study_teamidList.item(0)).getNodeValue();
	    		
	    		NodeList study_idList = fstElmnt.getElementsByTagName("study_id");
	    		Element study_idElement = (Element) study_idList.item(0);
	    		study_idList = study_idElement.getChildNodes();
	    		study_id[i] =  ((Node) study_idList.item(0)).getNodeValue();
	    		
	    		NodeList study_ownerList = fstElmnt.getElementsByTagName("study_owner");
	    		Element study_ownerElement = (Element) study_ownerList.item(0);
	    		study_ownerList = study_ownerElement.getChildNodes();
	    		study_owner[i] = ((Node) study_ownerList.item(0)).getNodeValue();
	    		
	    		NodeList study_nameList = fstElmnt.getElementsByTagName("study_name");
	    		Element study_nameElement = (Element) study_nameList.item(0);
	    		study_nameList = study_nameElement.getChildNodes();
	    		study_name[i] = ((Node) study_nameList.item(0)).getNodeValue();
	    		
	    		NodeList study_urlList = fstElmnt.getElementsByTagName("study_url");
	    		Element study_urlElement = (Element) study_urlList.item(0);
	    		study_urlList = study_urlElement.getChildNodes();
	    		study_url[i] = ((Node) study_urlList.item(0)).getNodeValue();
	    		
	    		NodeList study_percentprList = fstElmnt.getElementsByTagName("study_percentpr");
	    		Element study_percentprElement = (Element) study_percentprList.item(0);
	    		study_percentprList = study_percentprElement.getChildNodes();
	    		study_percentpr[i] = ((Node) study_percentprList.item(0)).getNodeValue();
	    		
	    		NodeList study_percentpdList = fstElmnt.getElementsByTagName("study_percentpd");
	    		Element study_percentpdElement = (Element) study_percentpdList.item(0);
	    		study_percentpdList = study_percentpdElement.getChildNodes();
	    		study_percentpd[i] = ((Node) study_percentpdList.item(0)).getNodeValue();
	    	
	    		NodeList study_seatsList = fstElmnt.getElementsByTagName("study_seats");
	    		Element study_seatsElement = (Element) study_seatsList.item(0);
	    		study_seatsList = study_seatsElement.getChildNodes();
	    		study_seats[i] = ((Node) study_seatsList.item(0)).getNodeValue();
	    	
	    		}
	    		} catch (Exception e) {
	    		System.out.println("XML Pasing Excpetion = " + e);
	    		}
	    	/*
	    	    for (int i = 0; i < patient_teamid.length; i++) {
	    	    	Log.i(TAG, "teamid = " + patient_teamid[i]);
	    	    }
	    	  */
	    	   // Log.i(TAG,"begin add to sr"); 
	    	    
	    	   //build results and
	    	    results = new ArrayList<StudyResults>();
	    	   
	    	    StudyResults sr = new StudyResults();
	    	    for (int i = 0; i < study_teamid.length; i++) {
	    	    	// if teamid is 0 then no matching patients were found
	    	    	String myid = study_teamid[i];
	    	    	if (myid.equalsIgnoreCase("0")) {
	    	    		// no studies to display
		        		return;
	    	    	}
	    	    	//Log.i(TAG,"begin sr");
	    	 
	    	    	sr = new StudyResults();
	    	    	sr.setstudy_teamid(study_teamid[i]);
	    	    	sr.setstudy_id(study_id[i]);
	    	    	sr.setstudy_owner(study_owner[i]);
	    	    	sr.setstudy_name(study_name[i]);
	    	    	sr.setstudy_url(study_url[i]);
	    	    	sr.setstudy_percentpr(study_percentpr[i]);
	    	    	sr.setstudy_percentpd(study_percentpd[i]);
	    	    	sr.setstudy_seats(study_seats[i]);

	    	    
	   	     		//sr.setCityState(patient_doctor[i] + " / " + patient_study_id[i]);
	   	     		results.add(sr);
	    	    }
	    	    
	    	    
	    	    final ListView lv = (ListView) findViewById(R.id.studyListView);
	            lv.setAdapter(new StudyAdapter(this, results));
	     
	            lv.setOnItemClickListener(new OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	                    Object o = lv.getItemAtPosition(position);
	                    StudyResults fullObject = (StudyResults)o;
	                    //Toast.makeText(Patients.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
	                    //Log.i(TAG,"saving patient_id->" + fullObject.getPatient_id());
	                    //MySingleton.getInstance().setPatientID(fullObject.getPatient_id());
	                    //MySingleton.getInstance().setPatientName(fullObject.getPatient_name());
	                    // go to PatientEdit
	                    Intent myIntent = new Intent();
	        	    	//myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
	        	    	//myIntent.setClassName(this, "com.websoftmagic.team.Patients.Patients");
	        	    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.StudyEdit");
	        	    	myIntent.putExtra("study_id", fullObject.getstudy_id());
	        	    	myIntent.putExtra("study_owner", fullObject.getstudy_owner());
	        	    	myIntent.putExtra("study_name", fullObject.getstudy_name());
	        	    	myIntent.putExtra("study_url", fullObject.getstudy_url());
	        	    	myIntent.putExtra("study_percentpr", fullObject.getstudy_percentpr());
	        	    	myIntent.putExtra("study_percentpd", fullObject.getstudy_percentpd());
	        	    	myIntent.putExtra("study_seats", fullObject.getstudy_seats());
	        	    	startActivity(myIntent);  
	                    
	                }
	            });
	    	    
	    }
}



