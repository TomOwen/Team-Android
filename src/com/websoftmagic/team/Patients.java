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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class Patients extends Activity {

	public  ArrayList<PatientResults> results = new ArrayList<PatientResults>();
	
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
 
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
	private static final String TAG = "Patients";
	private static  String option = "1";
	private static  String search = "";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.patients2);
        String test = MySingleton.getInstance().teamid;
        
        handleIntent(getIntent()); 
        if (isNetworkAvailable()) {
        	ReadPatients();
        } else {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(Patients.this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
        }
	}  
	public void onResume() {
		//Log.i(TAG,"Resuming Patients");
		super.onResume();
		
		if(search.length() > 0) return;
		if (isNetworkAvailable()) {
        	ReadPatients();
        } else {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(Patients.this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
        }
        
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
	public void GoToStudies (MenuItem item){
		Intent myIntent = new Intent();
    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Studies");
    	startActivity(myIntent);
	}
	public void GoToDoctors (MenuItem item){
		Intent myIntent = new Intent();
    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Doctors");
    	startActivity(myIntent);
	}
	public void AddPatient (MenuItem item){
	
		Intent myIntent = new Intent();
    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.PatientAdd");
    	startActivity(myIntent);
	}
	public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
    	startActivity(myIntent);
	}
	@Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
	private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          search = intent.getStringExtra(SearchManager.QUERY);
          option = "2";
          //Log.i("oncreate query=" , search);
          ReadPatients();
        }
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("Y")) {
			getMenuInflater().inflate(R.menu.activity_patients, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_patients_v, menu);
		}
	    //Log.i(TAG,"Here in create options");
	    return true;
	}



	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
	Log.i(TAG,"Here in selected click");
     switch(item.getItemId())
     {
     case 1:
      Log.i(TAG,"you clicked on item "+item.getTitle());
      return true;
     case 2:
    	 Log.i(TAG,"you clicked on item "+item.getTitle());
      return true;
     case 3:
    	 Log.i(TAG,"you clicked on item "+item.getTitle());
      return true;

     }
     return super.onOptionsItemSelected(item);

    }
	public void Search (View view) {
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
    	onSearchRequested();
    }
	public void ByNameClicked (View view) {
		option = "1";
		
		if (isNetworkAvailable()) {
        	ReadPatients();
        } else {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(Patients.this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
        }
	}
	public void ByDoctorClicked (View view) {
		option = "3";
		if (isNetworkAvailable()) {
        	ReadPatients();
        } else {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(Patients.this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
        }
	}
	public void ByStudyClicked (View view) {
		option = "4";
		if (isNetworkAvailable()) {
        	ReadPatients();
        } else {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(Patients.this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
        }
	}
	public void ReadPatients () {
		GetSearchResults();
        
        
	}
	private void GetSearchResults(){
	  
	     
	     // async in background to get content
	     // xml parse content and add to  sr Patient results array
	     new GetPatients().execute();
	 /*
	     PatientResults sr = new PatientResults();
	     sr.setName("Justin Schultz");
	     sr.setCityState("San Francisco, CA");
	     sr.setPhone("415-555-1234");
	     results.add(sr);
	 
	     sr = new PatientResults();
	     sr.setName("Jane Doe");
	     sr.setCityState("Las Vegas, NV");
	     sr.setPhone("702-555-1234");
	     results.add(sr);
	 
	     sr = new PatientResults();
	     sr.setName("Lauren Sherman");
	     sr.setCityState("San Francisco, CA");
	     sr.setPhone("415-555-1234");
	     results.add(sr);
	  
	     sr = new PatientResults();
	     sr.setName("Fred Jones");
	     sr.setCityState("Minneapolis, MN");
	     sr.setPhone("612-555-8214");
	     results.add(sr);
	 
	     sr = new PatientResults();
	     sr.setName("Bill Withers");
	     sr.setCityState("Los Angeles, CA");
	     sr.setPhone("424-555-8214");
	     results.add(sr);
	 
	     sr = new PatientResults();
	     sr.setName("Donald Fagen");
	     sr.setCityState("Los Angeles, CA");
	     sr.setPhone("424-555-1234");
	     results.add(sr);
	 
	     sr = new PatientResults();
	     sr.setName("Steve Rude");
	     sr.setCityState("Oakland, CA");
	     sr.setPhone("515-555-2222");
	     results.add(sr);
	 
	     sr = new PatientResults();
	     sr.setName("Roland Bloom");
	     sr.setCityState("Chelmsford, MA");
	     sr.setPhone("978-555-1111");
	     results.add(sr);
	 
	     sr = new PatientResults();
	     sr.setName("Sandy Baguskas");
	     sr.setCityState("Chelmsford, MA");
	     sr.setPhone("978-555-2222");
	     results.add(sr);
	 
	     sr = new PatientResults();
	     sr.setName("Scott Taylor");
	     sr.setCityState("Austin, TX");
	     sr.setPhone("512-555-2222");
	     results.add(sr);
	 */
	     return;
	    }
	class GetPatients extends AsyncTask<Object, Object, Object> {
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
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(Patients.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Patients.this);
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
			String URL = "http://" + dbserver + "/teamgetpatients.php?teamid=" + teamid + "&option=" + option + "&search=" + search + "&user_name=" + username;
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
	    
		    String patient_teamid[] = null;
	    	String patient_id[] = null;
	    	String patient_name[] = null;
	    	String patient_study_id[] = null;
	    	String patient_doctor[] = null;
	    	
	    	
	    	try {
	    		//Log.i(TAG,"Content =" + content);
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("patient");

	    		/** Assign String array length by arraylist size */
	    		patient_teamid = new String[nodeList.getLength()];
	    		patient_id = new String[nodeList.getLength()];
	    		patient_name = new String[nodeList.getLength()];
	    		patient_study_id = new String[nodeList.getLength()];
	    		patient_doctor = new String[nodeList.getLength()];
	    		

	    		for (int i = 0; i < nodeList.getLength(); i++) {

	    		Node node = nodeList.item(i);

	    		patient_teamid[i] = new String("");
	    		patient_id[i] = new String("");
	    		patient_name[i] = new String("");
	    		patient_study_id[i] = new String("");
	    		patient_doctor[i] = new String("");
	    		
	    		
	    		Element fstElmnt = (Element) node;
	    		
	    		NodeList patient_teamidList = fstElmnt.getElementsByTagName("patient_teamid");
	    		Element patient_teamidElement = (Element) patient_teamidList.item(0);
	    		patient_teamidList = patient_teamidElement.getChildNodes();
	    		patient_teamid[i] =  ((Node) patient_teamidList.item(0)).getNodeValue();
	    		
	    		NodeList patient_idList = fstElmnt.getElementsByTagName("patient_id");
	    		Element patient_idElement = (Element) patient_idList.item(0);
	    		patient_idList = patient_idElement.getChildNodes();
	    		patient_id[i] = ((Node) patient_idList.item(0)).getNodeValue();
	    		
	    		NodeList patient_nameList = fstElmnt.getElementsByTagName("patient_name");
	    		Element patient_nameElement = (Element) patient_nameList.item(0);
	    		patient_nameList = patient_nameElement.getChildNodes();
	    		patient_name[i] = ((Node) patient_nameList.item(0)).getNodeValue();
	    		
	    		NodeList patient_study_idList = fstElmnt.getElementsByTagName("patient_study_id");
	    		Element patient_study_idElement = (Element) patient_study_idList.item(0);
	    		patient_study_idList = patient_study_idElement.getChildNodes();
	    		patient_study_id[i] = ((Node) patient_study_idList.item(0)).getNodeValue();
	    		
	    		NodeList patient_doctorList = fstElmnt.getElementsByTagName("patient_doctor");
	    		Element patient_doctorElement = (Element) patient_doctorList.item(0);
	    		patient_doctorList = patient_doctorElement.getChildNodes();
	    		patient_doctor[i] = ((Node) patient_doctorList.item(0)).getNodeValue();

	    		
	    	
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
	    	   // Log.i(TAG,"begin add to sr"); 
	    	    
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
	    	    
	    	    
	    	    final ListView lv = (ListView) findViewById(R.id.patientListView);
	            lv.setAdapter(new PatientAdapter(this, results));
	     
	            lv.setOnItemClickListener(new OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	                    Object o = lv.getItemAtPosition(position);
	                    PatientResults fullObject = (PatientResults)o;
	                    //Toast.makeText(Patients.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
	                    //Log.i(TAG,"saving patient_id->" + fullObject.getPatient_id());
	                    MySingleton.getInstance().setPatientID(fullObject.getPatient_id());
	                    MySingleton.getInstance().setPatientName(fullObject.getPatient_name());
	                    // go to PatientEdit
	                    Intent myIntent = new Intent();
	        	    	//myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
	        	    	//myIntent.setClassName(this, "com.websoftmagic.team.Patients.Patients");
	        	    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.PatientEdit");
	        	    	myIntent.putExtra("patient_id", fullObject.getPatient_id());
	        	    	myIntent.putExtra("patient_name", fullObject.getPatient_name());
	        	    	myIntent.putExtra("patient_doctor", fullObject.getPatient_doctor());
	        	    	myIntent.putExtra("patient_study_id", fullObject.getPatient_study_id());
	        	    	startActivity(myIntent);
	                    
	                }
	            }); 
	    	    
	    }
}

