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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class Scans extends Activity {
	List<String> scan_dates = new ArrayList<String>();
	List<String> formatted_dates = new ArrayList<String>();
	private String scanDateToPass;
	List<String> reportOnline = new ArrayList<String>();
	
	
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
 
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
	private static final String TAG = "Scans";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.scans);
        String test = MySingleton.getInstance().teamid;
        String pname = MySingleton.getInstance().patientName;
        String pid = MySingleton.getInstance().patientID;
        String title = pname + " / " + pid;
        TextView message = (TextView)findViewById(R.id.patientNameAndID); 
        message.setText(title); 
        
        //handleIntent(getIntent()); 
        if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
        reportOnline.clear();
		formatted_dates.clear();
        ReadScans();
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
		reportOnline.clear();
		formatted_dates.clear();
		ReadScans();
		//Log.i(TAG,"resume");
	}
	public void AddScan (MenuItem item){
		
		Intent myIntent = new Intent();
    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.ScanAdd");
    	startActivity(myIntent);
	}
	public void AddPatient (MenuItem item){
		Log.i(TAG,"add patient clicked");
	}
	/*
	@Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
	private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          search = intent.getStringExtra(SearchManager.QUERY);
          option = "2";
          Log.i("oncreate query=" , search);
          ReadPatients();
        }
    }
    
    */
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("Y")) {
			getMenuInflater().inflate(R.menu.activity_scans, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_scans_v, menu);
		}
	    //Log.i(TAG,"Here in create options");
	    return true;
	}



	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
	//Log.i(TAG,"Here in selected click");
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
    	onSearchRequested();
    }
	
	public void ReadScans () {
		
		GetSearchResults();
        
        
	}
	private void GetSearchResults(){
	  
	     
	     // async in background to get content
	     // xml parse content and add to  sr Patient results array
	     new GetScans().execute();
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
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(Scans.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	//Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Scans.this);
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
			/*
			for (int i = 0; i < formatted_dates.size(); i++) {
    	    	Log.i(TAG, "scan date = " + formatted_dates.get(i));
    	    }
    	    */
			String teamid = MySingleton.getInstance().teamid;
			String dbserver = MySingleton.getInstance().dbServer;
			String dbuser = MySingleton.getInstance().dbUser;
			String dbpassword = MySingleton.getInstance().dbPassword;
			String patient_id = MySingleton.getInstance().patientID;
			String username = MySingleton.getInstance().userName;
			String URL = "http://" + dbserver + "/teamgetscans.php?teamid=" + teamid + "&scan_patient_id=" + patient_id + "&user_name=" + username;
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
	       // Log.i(TAG, "content=" + content);
	        return content;
		}


    	
    }
	   public void processXML(String content) throws ClientProtocolException, IOException {
	    
		    String scan_teamid[] = null;
	    	String scan_patient_id[] = null;
	    	String scan_date[] = null;
	    	String scan_report_online[] = null;
	    	
	    	
	    	try {
	    		//Log.i(TAG,"Content =" + content);
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("scan");

	    		/** Assign String array length by arraylist size */
	    		scan_teamid = new String[nodeList.getLength()];
	    		scan_patient_id = new String[nodeList.getLength()];
	    	    scan_date = new String[nodeList.getLength()];
	    		scan_report_online = new String[nodeList.getLength()];
	    		
	    		scan_dates = new ArrayList<String>();
		        formatted_dates = new ArrayList<String>(); 
	    		for (int i = 0; i < nodeList.getLength(); i++) {

	    		Node node = nodeList.item(i);

	    		scan_teamid[i] = new String("");
	    		scan_patient_id[i] = new String("");
	    		scan_date[i] = new String("");
	    		scan_report_online[i] = new String("");
	    		
	    		
	    		Element fstElmnt = (Element) node;
	    		
	    		NodeList scan_teamidList = fstElmnt.getElementsByTagName("scan_teamid");
	    		Element scan_teamidElement = (Element) scan_teamidList.item(0);
	    		scan_teamidList = scan_teamidElement.getChildNodes();
	    		scan_teamid[i] =  ((Node) scan_teamidList.item(0)).getNodeValue();
	    		
	    		NodeList scan_patient_idList = fstElmnt.getElementsByTagName("scan_patient_id");
	    		Element scan_patient_idElement = (Element) scan_patient_idList.item(0);
	    		scan_patient_idList = scan_patient_idElement.getChildNodes();
	    		scan_patient_id[i] = ((Node) scan_patient_idList.item(0)).getNodeValue();
	    		
	    		NodeList scan_dateList = fstElmnt.getElementsByTagName("scan_date");
	    		Element scan_dateElement = (Element) scan_dateList.item(0);
	    		scan_dateList = scan_dateElement.getChildNodes();
	    		scan_date[i] = ((Node) scan_dateList.item(0)).getNodeValue();
	    		scan_dates.add(scan_date[i]);
	    		// now add a formatted date for listview
	    		String dtStart = scan_date[i];  
            	SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");  
            	try {  
            	    java.util.Date mydate = format.parse(dtStart); 
            	    SimpleDateFormat postFormater = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            	    String newDateStr = postFormater.format(mydate);
            	    //System.out.println(newDateStr); 
            	    formatted_dates.add(newDateStr); 
            	} catch (ParseException e) {  
            	    // TODO Auto-generated catch block  
            		Log.i(TAG,"Invalid Date->" + scan_date[i]); 
            		formatted_dates.add("Invalid Date...." + scan_date[i]);
            	}
            	
	    		
	    		
	    		
	    		
	    		
	    		NodeList scan_report_onlineList = fstElmnt.getElementsByTagName("scan_report_online");
	    		Element scan_report_onlineElement = (Element) scan_report_onlineList.item(0);
	    		scan_report_onlineList = scan_report_onlineElement.getChildNodes();
	    		scan_report_online[i] = ((Node) scan_report_onlineList.item(0)).getNodeValue();
	    		reportOnline.add(scan_report_online[i]);
	    		//Log.i(TAG,"report="+scan_report_online[i]);

	    		
	    	
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
	    	    
	    	    final ListView lv = (ListView) findViewById(R.id.scanListView);
	    	    
	    	    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,scan_date);
	    	   // ArrayAdapter<String> adapter = new ArrayAdapter<String>(Scans.this,R.layout.simple_list_center,scan_date);
	    	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Scans.this,R.layout.simple_list_center_scans,formatted_dates);
	    	    
	    	    lv.setAdapter(adapter);
	    	    /*
	    	    for (int i = 0; i < formatted_dates.size(); i++) {
	    	    	Log.i(TAG, "scan date = " + formatted_dates.get(i));
	    	    }
	    	    */
	    	    
	            //lv.setAdapter(new PatientAdapter(this, results)); 
	     
	            lv.setOnItemClickListener(new OnItemClickListener() {
	                @Override
	                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	                    /*
	                	Object o = lv.getItemAtPosition(position);
	                    PatientResults fullObject = (PatientResults)o;
	                    //Toast.makeText(Patients.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
	                    //Log.i(TAG,"saving patient_id->" + fullObject.getPatient_id());
	                    MySingleton.getInstance().setPatientID(fullObject.getPatient_id());
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
	                    */
	                	//Log.i(TAG,"XXXXXXrow clicked->" + scan_dates.get(position));
	                	// go to scan edit
	                	//Log.i(TAG,"position="+position);
	                	//Log.i(TAG,"report 0="+reportOnline.get(0));
	                	//Log.i(TAG,"report 1="+reportOnline.get(1));
	                	//Log.i(TAG,"report 2="+reportOnline.get(2));
	                	
	                	String myreport = reportOnline.get(position);
	                	String dtStart = scan_dates.get(position);  
	                	SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");  
	                	try {  
	                	    java.util.Date mydate = format.parse(dtStart); 
	                	    SimpleDateFormat postFormater = new SimpleDateFormat("EEEEEEEE, MMMM dd, yyyy");
	                	    String newDateStr = postFormater.format(mydate);
	                	    scanDateToPass = dtStart;
	                	    //System.out.println(newDateStr);  
	                	} catch (ParseException e) {  
	                	    // TODO Auto-generated catch block  
	                		Log.i(TAG,"Invalid Date->" + scan_dates.get(position));
	                		return;
	                	}
	                	// go to ScanEdit passing scan date
	                	Intent myIntent = new Intent();
	                	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.ScanEdit");
	                	myIntent.putExtra("scan_date",scanDateToPass );
	                	myIntent.putExtra("report_online",myreport );
	                	MySingleton.getInstance().setScanOnline(myreport);
	                	//Log.i(TAG,"myreport="+ myreport);
	                	//Log.i(TAG,"scanonline="+ MySingleton.getInstance().scanOnline);
	                	startActivity(myIntent);
	                }
	            });
	    	    
	    }
}

