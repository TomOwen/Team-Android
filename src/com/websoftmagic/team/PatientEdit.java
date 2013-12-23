package com.websoftmagic.team;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
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

import com.websoftmagic.team.Patients.GetPatients;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PatientEdit extends Activity{
	
	private static String succeed[] = null;
	
	private static String drop_teamid[] = null;
	private static String drop_type[] = null;
	private static String drop_string[] = null;
	List<String> doctors = new ArrayList<String>();
	List<String> studies = new ArrayList<String>();
	private static final String TAG = "Patient Edit";
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
 
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
    String up_PatientID = "";
    String updated_PatientName = "";
    String updated_PatientDoctor = "";
    String updated_PatientStudy = "";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.patient_edit2);
        String test = MySingleton.getInstance().teamid;
         
        String patient_id = getIntent().getExtras().getString("patient_id");
        String patient_name = getIntent().getExtras().getString("patient_name");
        String patient_doctor = getIntent().getExtras().getString("patient_doctor");
        String patient_study_id = getIntent().getExtras().getString("patient_study_id");
        TextView message = (TextView)findViewById(R.id.TextView01); 
        message.setText("Enter Changes..."); 
        //Log.i(TAG,"intent" + patient_id +"," + patient_name);
        TextView t = (TextView)findViewById(R.id.patient_id);
        t.setText(patient_id);
        TextView n = (TextView)findViewById(R.id.patient_name);
        n.setText(patient_name);
        if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
        new GetDrsAndStudies().execute(); 
        
        
	} 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("Y")) {
			getMenuInflater().inflate(R.menu.activity_patients_edit, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_patients_edit_v, menu);
		}
	    return true;
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
	public void createStudyDropdown(){
		Spinner spinnerS = (Spinner) findViewById(R.id.spinnnerStudy);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, studies);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerS.setAdapter(dataAdapter);
		
		int index = studies.indexOf(getIntent().getExtras().getString("patient_study_id"));
		// if index is -1 then add study_id to drop down
		if (index == -1) {   
			studies.add(getIntent().getExtras().getString("patient_study_id"));
		}
		index = studies.indexOf(getIntent().getExtras().getString("patient_study_id"));
		if (index != -1) spinnerS.setSelection(index);
		spinnerS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        /**
	         * Called when a new item is selected (in the Spinner)
	         */
	         public void onItemSelected(AdapterView<?> parent, View view, 
	                    int pos, long id) {
	                // An spinnerItem was selected. You can retrieve the selected item using
	                // parent.getItemAtPosition(pos)
	        	 	String mystudy = (String) parent.getItemAtPosition(pos);
	        	 	updated_PatientStudy = mystudy;

	            }

	            public void onNothingSelected(AdapterView<?> parent) {
	                // Do nothing, just another required interface callback
	            }

	    }); // (optional)
	}
	public void createDoctorDropdown(){
		Spinner spinnerS = (Spinner) findViewById(R.id.spinnerDoctor);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, doctors);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerS.setAdapter(dataAdapter);
		
		int index = doctors.indexOf(getIntent().getExtras().getString("patient_doctor"));
		// if index is -1 then add study_id to drop down
				if (index == -1) {   
					doctors.add(getIntent().getExtras().getString("patient_doctor"));
				}
		index = doctors.indexOf(getIntent().getExtras().getString("patient_doctor"));
		if (index != -1) spinnerS.setSelection(index);
		spinnerS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        /**
	         * Called when a new item is selected (in the Spinner)
	         */
	         public void onItemSelected(AdapterView<?> parent, View view, 
	                    int pos, long id) {
	                // An spinnerItem was selected. You can retrieve the selected item using
	                // parent.getItemAtPosition(pos)
	        	 	String mydoctor = (String) parent.getItemAtPosition(pos);
	        	 	updated_PatientDoctor = mydoctor;
	            }

	            public void onNothingSelected(AdapterView<?> parent) {
	                // Do nothing, just another required interface callback
	            }

	    }); // (optional)
	}
	public void UpdatePatientClicked (MenuItem item){
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		
		EditText myEditText= (EditText) findViewById(R.id.patient_name);
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
		// name must have 1 or more characters
		EditText editText2 = (EditText) findViewById(R.id.patient_name);
    	String tname = editText2.getText().toString();
    	if (tname.length() < 1 ) {
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
    		alertbox.setTitle("Name Field must be entered").setMessage("Please Correct Name").setNeutralButton("OK", null).show();
    		return;
    	} 
    	TextView message = (TextView)findViewById(R.id.TextView01); 
        message.setText("Updating Patient");
		new UpdatePatient().execute();
		
	
		
	}
	public void DeletePatientClicked (MenuItem item){
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		
		// check with user before deleting
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
		        	new DeletePatient().execute();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Delete this patient and all his/her scans?\nAre you sure?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
		
	}
	public void PatientResponse (View  view){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Response");
    	startActivity(myIntent);
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
	public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
    	startActivity(myIntent);
	}
	public void studyClicked(View view) {
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Studies");
    	startActivity(myIntent);
	}
	public void doctorClicked(View view) {
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Doctors");
    	startActivity(myIntent);
	}
	public void GoToPatients (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
    	startActivity(myIntent);
		
	}
	public void ScansClicked (View view){ 
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Scans");
    	startActivity(myIntent);
		
	}
	
	public void ChooseImages (View view){ 
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.LesionChoose");
    	startActivity(myIntent);
	}
	
	class GetDrsAndStudies extends AsyncTask<Object, Object, Object> {
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
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
    	     } else {
    	        //Log.i(TAG, "Post exexs content=" + content); 
    	        String s = content.toString();
    	        try {
					processXML(s);
					// all studies and doctors now in DrAndStudyResults class
					for (int i = 0; i < drop_teamid.length; i++) {
						//Log.i(TAG, "Type->" + drop_type[i] + "String->" + drop_string[i]);
						if (drop_type[i].equals("1")) {
							doctors.add(drop_string[i]);
						} else {
							studies.add(drop_string[i]);
						}
					}
					/*
					for (int i = 0; i < doctors.size(); i++) {
						Log.i(TAG, doctors.get(i));
					}
					for (int i = 0; i < studies.size(); i++) {
						Log.i(TAG, studies.get(i));
					}
					*/
					createStudyDropdown();
					createDoctorDropdown();
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
			String URL = "http://" + dbserver + "/teamdocandstudy.php?teamid=" + teamid;
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
		    
	    	
	    	
	    	
	    	try {
	    		//Log.i(TAG,"Content =" + content);
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("docandstudy");

	    		/** Assign String array length by arraylist size */
	    		
	    		drop_teamid = new String[nodeList.getLength()];
	    		drop_type = new String[nodeList.getLength()];
	    		drop_string = new String[nodeList.getLength()];
	    		

	    		for (int i = 0; i < nodeList.getLength(); i++) {

	    		Node node = nodeList.item(i);

	    		
	    		drop_teamid[i] = new String("");
	    		drop_type[i] = new String("");
	    		drop_string[i] = new String("");
	    		
	    		
	    		Element fstElmnt = (Element) node;
	    		
	    		NodeList drop_teamidList = fstElmnt.getElementsByTagName("teamid");
	    		Element drop_teamidElement = (Element) drop_teamidList.item(0);
	    		drop_teamidList = drop_teamidElement.getChildNodes();
	    		drop_teamid[i] =  ((Node) drop_teamidList.item(0)).getNodeValue();
	    		
	    		NodeList drop_typeList = fstElmnt.getElementsByTagName("type");
	    		Element drop_typeElement = (Element) drop_typeList.item(0);
	    		drop_typeList = drop_typeElement.getChildNodes();
	    		drop_type[i] = ((Node) drop_typeList.item(0)).getNodeValue();
	    		
	    		NodeList drop_stringList = fstElmnt.getElementsByTagName("string");
	    		Element drop_stringElement = (Element) drop_stringList.item(0);
	    		drop_stringList = drop_stringElement.getChildNodes();
	    		drop_string[i] = ((Node) drop_stringList.item(0)).getNodeValue();
	    		
	    	
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
	    	    
	    	    
	    }
	   class UpdatePatient extends AsyncTask<Object, Object, Object> {
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
	    		AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
	    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
	    	 }
	    	 @Override
	    	 protected void onPostExecute(Object content) {
	    	    if (error) {
	    	    	Log.i(TAG, "Error occurred"); 
	    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
	    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
	    	     } else {
	    	        //Log.i(TAG, "Post exexs content=" + content); 
	    	        String s = content.toString();
	    	        try {
						processXMLPatients(s);
						// check succeed for Y
						
						int myint = succeed.length;
						
						if (myint< 1) {
							AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
				    		alertbox.setTitle("TEAM Server Problem").setMessage("Please Contact Tom").setNeutralButton("OK", null).show();
				    		return;
						}
						if (succeed[0].equals("1")) {
							// all ok
							EditText editText2 = (EditText) findViewById(R.id.patient_name);
					    	String tname = editText2.getText().toString();
					    	MySingleton.getInstance().setPatientName(tname);
						} else {
							AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
				    		alertbox.setTitle("Could not update this Patient").setMessage("Patient may have been deleted").setNeutralButton("OK", null).show();
				    		return;
						}
						TextView message = (TextView)findViewById(R.id.TextView01); 
				        message.setText("Enter Changes...");
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
				EditText editText2 = (EditText) findViewById(R.id.patient_name);
		    	String tname = editText2.getText().toString();
		    	String name = new String(tname.replace("'", " "));
		    	String username = MySingleton.getInstance().userName;
				String URL = "http://" + dbserver + "/teamupdatepatient.php?teamid=" 
				+ teamid + "&patient_id=" + getIntent().getExtras().getString("patient_id") 
				+ "&patient_name=" + name + "&patient_study_id=" + updated_PatientStudy 
				+ "&patient_doctor=" + updated_PatientDoctor
				+ "&user_name=" + username;
	        	// build the URL string
				//Log.i(TAG,"URL="+URL);
				String NewURL = new String(URL.replace(" ", "%20"));
		        try {
		        	
		            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
		            HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
		            ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
		            HttpPost httpPost = new HttpPost(NewURL);
		            
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
		   public void processXMLPatients(String content) throws ClientProtocolException, IOException {
	    	
		    	try {
		    		//Log.i(TAG,"Content =" + content);
		    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    		DocumentBuilder db = dbf.newDocumentBuilder();
		    		Document doc = db.parse(new InputSource(new StringReader(content)));
		    		doc.getDocumentElement().normalize();

		    		NodeList nodeList = doc.getElementsByTagName("results");

		    		/** Assign String array length by arraylist size */
		    		
		    		succeed = new String[nodeList.getLength()];
	
		    		for (int i = 0; i < nodeList.getLength(); i++) {

		    		Node node = nodeList.item(i);
	
		    		Element fstElmnt = (Element) node;
		    		
		    		NodeList succeedList = fstElmnt.getElementsByTagName("succeed");
		    		Element succeedElement = (Element) succeedList.item(0);
		    		succeedList = succeedElement.getChildNodes();
		    		succeed[i] =  ((Node) succeedList.item(0)).getNodeValue();
		    	
		    		}
		    		} catch (Exception e) {
		    		System.out.println("XML Pasing Excpetion = " + e);
		    		}
		    	    
		    }
		   class DeletePatient extends AsyncTask<Object, Object, Object> {
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
		    		AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
		    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
		    	 }
		    	 @Override
		    	 protected void onPostExecute(Object content) {
		    	    if (error) {
		    	    	Log.i(TAG, "Error occurred"); 
		    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
		    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
		    	     } else {
		    	        //Log.i(TAG, "Post exexs content=" + content); 
		    	        String s = content.toString();
		    	        try {
							processXMLPatients(s);
							// check succeed for Y
							
							int myint = succeed.length;
							
							if (myint< 1) {
								AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
					    		alertbox.setTitle("TEAM Server Problem").setMessage("Please Contact Tom").setNeutralButton("OK", null).show();
					    		return;
							}
							if (succeed[0].equals("1")) {
								// all ok
								finish(); // goes back to patient screen
							} else {
								AlertDialog.Builder alertbox = new AlertDialog.Builder(PatientEdit.this);
					    		alertbox.setTitle("Could not delete this Patient").setMessage("Patient may have been deleted").setNeutralButton("OK", null).show();
					    		return;
							}
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
					EditText editText2 = (EditText) findViewById(R.id.patient_name);
			    	String tname = editText2.getText().toString();
			    	String name = new String(tname.replace("'", " "));
			    	String username = MySingleton.getInstance().userName;
					String URL = "http://" + dbserver + "/teamdeletepatient.php?teamid=" 
					+ teamid + "&patient_id=" + getIntent().getExtras().getString("patient_id") + "&user_name=" + username;
					
		        	// build the URL string
					//Log.i(TAG,"URL="+URL);
					String NewURL = new String(URL.replace(" ", "%20"));
			        try {
			        	
			            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
			            HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
			            ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
			            HttpPost httpPost = new HttpPost(NewURL);
			            
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
			   public void processXMLDelete(String content) throws ClientProtocolException, IOException {
		    	
			    	try {
			    		//Log.i(TAG,"Content =" + content);
			    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			    		DocumentBuilder db = dbf.newDocumentBuilder();
			    		Document doc = db.parse(new InputSource(new StringReader(content)));
			    		doc.getDocumentElement().normalize();

			    		NodeList nodeList = doc.getElementsByTagName("results");

			    		/** Assign String array length by arraylist size */
			    		
			    		succeed = new String[nodeList.getLength()];
		
			    		for (int i = 0; i < nodeList.getLength(); i++) {

			    		Node node = nodeList.item(i);
		
			    		Element fstElmnt = (Element) node;
			    		
			    		NodeList succeedList = fstElmnt.getElementsByTagName("succeed");
			    		Element succeedElement = (Element) succeedList.item(0);
			    		succeedList = succeedElement.getChildNodes();
			    		succeed[i] =  ((Node) succeedList.item(0)).getNodeValue();
			    	
			    		}
			    		} catch (Exception e) {
			    		System.out.println("XML Pasing Excpetion = " + e);
			    		}
			    	    
			    }
}
