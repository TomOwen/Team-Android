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

public class ScanEdit extends Activity {
	public String saveFileName = "";
	ToggleButton tb;
	public String isReportOnline;
	public  ArrayList<LesionResults> results = new ArrayList<LesionResults>();
	
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
 
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
	private static final String TAG = "Scan Edit";
	private static  String option = "1";
	private static  String search = "";
	String report_online = "";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        report_online = MySingleton.getInstance().scanOnline;
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.scan_edit);
        if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("N")) {
        	tb = (ToggleButton) findViewById(R.id.toggleButton1);
        	tb.setVisibility(View.INVISIBLE);
        	TextView filen = (TextView)findViewById(R.id.textView2);
        	filen.setVisibility(View.INVISIBLE);
        	TextView onlinelabel = (TextView)findViewById(R.id.textView1);
        	onlinelabel.setVisibility(View.INVISIBLE);
        }
        String test = MySingleton.getInstance().teamid;
        //Log.i(TAG,"on create report=" + report_online);
        addListenerOnButton();
        setButtonState();
        // get scan file name from patient_id and scan_date
        String scan_date = getIntent().getExtras().getString("scan_date");
        String patient_id = MySingleton.getInstance().patientID;
        String patient_name = MySingleton.getInstance().patientName;
        String doc_type = MySingleton.getInstance().docType;
        String year = scan_date.substring(2,4);
        String month = scan_date.substring(5,7);
        String day = scan_date.substring(8,10);
        String fileName = "File Name is " + patient_id + "-"  + month + day  + year + "." + doc_type;
        saveFileName = patient_id + "-"  + month + day  + year + "." + doc_type;
        TextView n = (TextView)findViewById(R.id.textView3);
        n.setText(patient_name + " - " + month + "/" + day + "/" + year);
        TextView nfilename = (TextView)findViewById(R.id.textView2);
        nfilename.setText(fileName);
        if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
        ReadLesions();
        setButtonState();
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
	public void setButtonState() {
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		//String report_online = getIntent().getExtras().getString("report_online");
		report_online = MySingleton.getInstance().scanOnline;
        tb = (ToggleButton) findViewById(R.id.toggleButton1);
        
        if (report_online.equalsIgnoreCase("Y")) {
        	tb.setChecked(true);      	
        	Button xx = (Button)findViewById(R.id.button1);
        	xx.setVisibility(View.VISIBLE);
        } else {
        	tb.setChecked(false);
        	Button xx = (Button)findViewById(R.id.button1);
        	xx.setVisibility(View.INVISIBLE);	
        }
		
	}
	public void addListenerOnButton() {
		//MySingleton.getInstance().setScanOnline("N");
		tb = (ToggleButton) findViewById(R.id.toggleButton1);
		tb.setOnClickListener(new OnClickListener()
		{
		public void onClick(View v)
		{
			if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("N")) {
				return;
			}
			Boolean checked = tb.isChecked();
		if (checked) {
			Button xx = (Button)findViewById(R.id.button1);
        	xx.setVisibility(View.VISIBLE);
        	MySingleton.getInstance().setScanOnline("Y");
        	isReportOnline = "Y";
		} else {
			Button xx = (Button)findViewById(R.id.button1);
        	xx.setVisibility(View.INVISIBLE);
        	MySingleton.getInstance().setScanOnline("N");
        	isReportOnline = "N";
		}
			//Log.i(TAG,"scanonline singleton =" + MySingleton.getInstance().scanOnline);
			new SetReportOnline().execute();
		
		}
	   });
	}
	public void onResume() {
		//Log.i(TAG,"Resuming Scan Edit");
		super.onResume();
		//Log.i(TAG,"on resume report=" + report_online);
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		
		ReadLesions();
		setButtonState();
	}
	public void AddLesionClicked (MenuItem item) {
		Intent myIntent = new Intent();
    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.LesionAdd");
    	myIntent.putExtra("patient_id", MySingleton.getInstance().patientID);
    	myIntent.putExtra("patient_name", MySingleton.getInstance().patientName);
    	myIntent.putExtra("scan_date", getIntent().getExtras().getString("scan_date"));
    	//Log.i(TAG,"New lesion clicked");
    	startActivity(myIntent); 
		
	}
	public void AddPatient (MenuItem item){
		Log.i(TAG,"add patient clicked");
	}
	public void ViewScanReport (View view){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.ScanReportView");
		myIntent.putExtra("patient_name", MySingleton.getInstance().patientName);
		myIntent.putExtra("filename", saveFileName);
    	startActivity(myIntent);
	}
	public void DeleteScanClicked (MenuItem item){
		//Log.i(TAG," delete scan clicked");
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
				        	new DeleteScan().execute();
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Delete this scan and all lesions?\nAre you sure?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("Y")) {
			getMenuInflater().inflate(R.menu.activity_scanedit, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_scanedit_v, menu);
		}
	    return true;
	}
	public void ReadLesions () {
		GetSearchResults();
        
        
	}
	private void GetSearchResults(){
	     // async in background to get content
	     // xml parse content and add to  sr Patient results array
	     new GetLesions().execute();
	     return;
	    }
	class GetLesions extends AsyncTask<Object, Object, Object> {
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
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(ScanEdit.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(ScanEdit.this);
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
			String scan_date = getIntent().getExtras().getString("scan_date");
			String patient_id = MySingleton.getInstance().patientID;
			String username = MySingleton.getInstance().userName;
			String URL = "http://" + dbserver + "/teamgetlesions.php?teamid=" + teamid + "&patient_id=" + patient_id + "&scan_date=" + scan_date + "&user_name=" + username;
        	// build the URL string
			Log.i(TAG, "URL = " + URL);
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
		 String lesion_teamid[] = null;
	     String lesion_number[] = null;
	     String lesion_size[] = null;
	     String lesion_comment[] = null;
	     String lesion_target[] = null;
	     String lesion_media_type[] = null;
	     String lesion_media_online[] = null;
	     String lesion_node[] = null;
	    	
	    	try {
	    		//Log.i(TAG,"Content =" + content);
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("lesion");

	    		/** Assign String array length by arraylist size */
	    		lesion_teamid = new String[nodeList.getLength()];
	    		lesion_number = new String[nodeList.getLength()];
	    		lesion_size = new String[nodeList.getLength()];
	    		lesion_comment = new String[nodeList.getLength()];
	    		lesion_target = new String[nodeList.getLength()];
	    		lesion_media_type = new String[nodeList.getLength()];
	    		lesion_media_online = new String[nodeList.getLength()];
	    		lesion_node = new String[nodeList.getLength()];
	    		

	    		for (int i = 0; i < nodeList.getLength(); i++) {

	    		Node node = nodeList.item(i);

	    		lesion_teamid[i] = new String("");
	    		lesion_number[i] = new String("");
	    		lesion_size[i] = new String("");
	    		lesion_comment[i] = new String("");
	    		lesion_target[i] = new String("");
	    		lesion_media_type[i] = new String("");
	    		lesion_media_online[i] = new String("");
	    		lesion_node[i] = new String("");
	    		
	    		
	    		Element fstElmnt = (Element) node;
	    		
	    		NodeList lesion_teamidList = fstElmnt.getElementsByTagName("lesion_teamid");
	    		Element lesion_teamidElement = (Element) lesion_teamidList.item(0);
	    		lesion_teamidList = lesion_teamidElement.getChildNodes();
	    		lesion_teamid[i] =  ((Node) lesion_teamidList.item(0)).getNodeValue();
	    		
	    		NodeList lesion_numberList = fstElmnt.getElementsByTagName("lesion_number");
	    		Element lesion_numberElement = (Element) lesion_numberList.item(0);
	    		lesion_numberList = lesion_numberElement.getChildNodes();
	    		lesion_number[i] =  ((Node) lesion_numberList.item(0)).getNodeValue();
	    		
	    		NodeList lesion_sizeList = fstElmnt.getElementsByTagName("lesion_size");
	    		Element lesion_sizeElement = (Element) lesion_sizeList.item(0);
	    		lesion_sizeList = lesion_sizeElement.getChildNodes();
	    		lesion_size[i] = ((Node) lesion_sizeList.item(0)).getNodeValue();
	    		
	    		NodeList lesion_commentList = fstElmnt.getElementsByTagName("lesion_comment");
	    		Element lesion_commentElement = (Element) lesion_commentList.item(0);
	    		lesion_commentList = lesion_commentElement.getChildNodes();
	    		lesion_comment[i] = ((Node) lesion_commentList.item(0)).getNodeValue();
	    		
	    		NodeList lesion_targetList = fstElmnt.getElementsByTagName("lesion_target");
	    		Element lesion_targetElement = (Element) lesion_targetList.item(0);
	    		lesion_targetList = lesion_targetElement.getChildNodes();
	    		lesion_target[i] = ((Node) lesion_targetList.item(0)).getNodeValue();
	    		
	    		NodeList lesion_media_typeList = fstElmnt.getElementsByTagName("lesion_media_type");
	    		Element lesion_media_typeElement = (Element) lesion_media_typeList.item(0);
	    		lesion_media_typeList = lesion_media_typeElement.getChildNodes();
	    		lesion_media_type[i] = ((Node) lesion_media_typeList.item(0)).getNodeValue();
	    		
	    		NodeList lesion_media_onlineList = fstElmnt.getElementsByTagName("lesion_media_online");
	    		Element lesion_media_onlineElement = (Element) lesion_media_onlineList.item(0);
	    		lesion_media_onlineList = lesion_media_onlineElement.getChildNodes();
	    		lesion_media_online[i] = ((Node) lesion_media_onlineList.item(0)).getNodeValue();
	    		
	    		NodeList lesion_nodeList = fstElmnt.getElementsByTagName("lesion_node");
	    		Element lesion_nodeElement = (Element) lesion_nodeList.item(0);
	    		lesion_nodeList = lesion_nodeElement.getChildNodes();
	    		lesion_node[i] = ((Node) lesion_nodeList.item(0)).getNodeValue();

	    		
	    	
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
	    	    results = new ArrayList<LesionResults>();
	    	   
	    	    LesionResults sr = new LesionResults();
	    	    for (int i = 0; i < lesion_teamid.length; i++) {
	    	    	// if teamid is 0 then no matching patients were found
	    	    	String myid = lesion_teamid[i];
	    	    	if (myid.equalsIgnoreCase("0")) {
	    	    		// no lesions to display
		        		return;
	    	    	}
	    	    	//Log.i(TAG,"begin sr");
	    	 
	    	    	sr = new LesionResults();
	    	    	sr.setlesion_teamid(lesion_teamid[i]);
	    	    	sr.setlesion_number(lesion_number[i]);
	    	    	sr.setlesion_size(lesion_size[i]);
	    	    	sr.setlesion_comment(lesion_comment[i]);
	    	    	sr.setlesion_target(lesion_target[i]);
	    	    	sr.setlesion_media_type(lesion_media_type[i]);
	    	    	sr.setlesion_media_online(lesion_media_online[i]);
	    	    	sr.setlesion_node(lesion_node[i]);
	    	    
	   	     		//sr.setCityState(patient_doctor[i] + " / " + patient_study_id[i]);
	   	     		results.add(sr);
	    	    }
	    	    setButtonState();
	    	    
	    	    final ListView lv = (ListView) findViewById(R.id.lesionListView);
	            lv.setAdapter(new LesionAdapter(this, results));
	     
	            lv.setOnItemClickListener(new OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	                    Object o = lv.getItemAtPosition(position);
	                    LesionResults fullObject = (LesionResults)o;
	                    //Toast.makeText(Patients.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
	                    //Log.i(TAG,"saving patient_id->" + fullObject.getPatient_id());
	                    //MySingleton.getInstance().setPatientID(fullObject.getPatient_id());
	                    //MySingleton.getInstance().setPatientName(fullObject.getPatient_name());
	                    // go to PatientEdit
	                    Intent myIntent = new Intent();
	        	    	//myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
	        	    	//myIntent.setClassName(this, "com.websoftmagic.team.Patients.Patients");
	        	    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.LesionEdit");
	        	    	myIntent.putExtra("patient_id", MySingleton.getInstance().patientID);
	        	    	myIntent.putExtra("patient_name", MySingleton.getInstance().patientName);
	        	    	myIntent.putExtra("scan_date", getIntent().getExtras().getString("scan_date"));
	        	    	myIntent.putExtra("lesion_number", fullObject.getlesion_number());
	        	    	myIntent.putExtra("lesion_size", fullObject.getlesion_size());
	        	    	myIntent.putExtra("lesion_comment", fullObject.getlesion_comment());
	        	    	myIntent.putExtra("lesion_target", fullObject.getlesion_target());
	        	    	myIntent.putExtra("lesion_media_type", fullObject.getlesion_media_type());
	        	    	myIntent.putExtra("lesion_media_online", fullObject.getlesion_media_online());
	        	    	myIntent.putExtra("lesion_node", fullObject.getlesion_node());
	        	    	// store lesion number and type in singleton
	        	    	MySingleton.getInstance().setMediaType(fullObject.getlesion_media_type());
	        	    	MySingleton.getInstance().setLesionNumber(fullObject.getlesion_number());
	        	    	
	        	    	startActivity(myIntent);  
	                    
	                }
	            });
	    	    
	    }
		class SetReportOnline extends AsyncTask<Object, Object, Object> {
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
	    		AlertDialog.Builder alertbox = new AlertDialog.Builder(ScanEdit.this);
	    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
	    	 }
	    	 @Override
	    	 protected void onPostExecute(Object content) {
	    	    if (error) {
	    	    	//Log.i(TAG, "Error occurred"); 
	    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(ScanEdit.this);
	    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
	    	     } else {
	    	        //Log.i(TAG, "Post exexs content=" + content); 
	    	        String s = content.toString();
	    	        try {
						processXMLsetrpt(s);
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
				String scan_date = getIntent().getExtras().getString("scan_date");
				String patient_id = MySingleton.getInstance().patientID;
				String username = MySingleton.getInstance().userName;
				String URL = "http://" + dbserver + "/teamupdatereport.php?teamid=" + teamid + "&patient_id=" + patient_id + "&scan_date=" + scan_date
						+ "&scan_report_online=" + isReportOnline + "&user_name=" + username;
	        	// build the URL string
				//Log.i(TAG,"URL=" + URL);
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
		       // Log.i(TAG, "content=" + content);
		        return content;
			}


	    	
	    }
		   public void processXMLsetrpt(String content) throws ClientProtocolException, IOException {
			 String succeed[] = null;
		    	
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

		    		succeed[i] = new String("");
		    		
		    		Element fstElmnt = (Element) node;
		    		
		    		NodeList succeedList = fstElmnt.getElementsByTagName("succeed");
		    		Element succeedElement = (Element) succeedList.item(0);
		    		succeedList = succeedElement.getChildNodes();
		    		succeed[i] =  ((Node) succeedList.item(0)).getNodeValue();
		    		
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
		    	    
		    	    for (int i = 0; i < succeed.length; i++) {
		    	    	// if teamid is 0 then no matching patients were found
		    	    	String myid = succeed[i];
		    	    	if (myid.equalsIgnoreCase("1")) {
		    	    		// all was ok
			        		return;
		    	    	}
		    	    	// display error occurred
		    	    	Log.i(TAG, "Error occurred"); 
		    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(ScanEdit.this);
		        		alertbox.setTitle("Alert!").setMessage("TEAM was unable to update the onlin scan report flag").setNeutralButton("OK", null).show();
		    	    }
		    	    
		    	    
		    	    
		    }
			class DeleteScan extends AsyncTask<Object, Object, Object> {
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
		    		AlertDialog.Builder alertbox = new AlertDialog.Builder(ScanEdit.this);
		    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
		    	 }
		    	 @Override
		    	 protected void onPostExecute(Object content) {
		    	    if (error) {
		    	    	Log.i(TAG, "Error occurred"); 
		    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(ScanEdit.this);
		    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
		    	     } else {
		    	        //Log.i(TAG, "Post exexs content=" + content); 
		    	        String s = content.toString();
		    	        try {
							processXMLdelete(s); 
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
					String scan_date = getIntent().getExtras().getString("scan_date");
					String patient_id = MySingleton.getInstance().patientID;
					String username = MySingleton.getInstance().userName;
					String URL = "http://" + dbserver + "/teamdeletescan.php?teamid=" + teamid + "&scan_patient_id=" + patient_id + "&scan_date=" + scan_date + "&user_name=" + username;
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
			   public void processXMLdelete(String content) throws ClientProtocolException, IOException {
				 String succeed[] = null;
			    	
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

			    		succeed[i] = new String("");
			    		
			    		Element fstElmnt = (Element) node;
			    		
			    		NodeList succeedList = fstElmnt.getElementsByTagName("succeed");
			    		Element succeedElement = (Element) succeedList.item(0);
			    		succeedList = succeedElement.getChildNodes();
			    		succeed[i] =  ((Node) succeedList.item(0)).getNodeValue();
			    		
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
			    	    
			    	    for (int i = 0; i < succeed.length; i++) {
			    	    	// if teamid is 0 then no matching patients were found
			    	    	String myid = succeed[i];
			    	    	if (myid.equalsIgnoreCase("1")) {
			    	    		// all was ok
			    	    		finish();  
				        		return;
			    	    	}
			    	    	// display error occurred
			    	    	Log.i(TAG, "Error occurred"); 
			    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(ScanEdit.this);
			        		alertbox.setTitle("Alert!").setMessage("TEAM was unable to delete that scan date").setNeutralButton("OK", null).show();
			    	    }
			    	    
			    	    
			    	    
			    }
}


