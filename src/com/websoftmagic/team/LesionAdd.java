package com.websoftmagic.team;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import android.graphics.Color;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

public class LesionAdd extends Activity{
	
	private static final Set<String> fileTypes = new HashSet<String>(Arrays.asList(
		     new String[] {"jpg","png","doc","pdf","html"}
		));
	
	private static String succeed[] = null;
	
	private static final String TAG = "Lesion Add";
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
        setContentView(R.layout.lesion_add2);
        String test = MySingleton.getInstance().teamid;
        String patient_id = getIntent().getExtras().getString("patient_id");
        String patient_name = getIntent().getExtras().getString("patient_name");
        String scan_date = getIntent().getExtras().getString("scan_date");
        
        TextView patientName = (TextView)findViewById(R.id.patientName);
        patientName.setText(patient_name);
        TextView scanDate = (TextView)findViewById(R.id.scanDate);
        
        scanDate.setText(scan_date.substring(5,7) + "/" + scan_date.substring(8,10) + "/" + scan_date.substring(2,4));
        
		ImageView mybutton = (ImageView) findViewById(R.id.viewImage);
        mybutton.setVisibility(View.INVISIBLE);
        TextView fileT = (TextView)findViewById(R.id.fileType);
        String ft = MySingleton.getInstance().mediaType;
        fileT.setText(ft);
        
        String year = scan_date.substring(2,4);
        String month = scan_date.substring(5,7);
        String day = scan_date.substring(8,10);
        String fileName = "Image/File Name " + patient_id + "-"  + month + day  + year + "-" + "X" + "." + ft;
        TextView fn = (TextView)findViewById(R.id.imageFileName);
        fn.setText(fileName);
        /*
        String year = scan_date.substring(2,4);
        String month = scan_date.substring(5,7);
        String day = scan_date.substring(8,10);
        String fileName = "Image/File Name " + patient_id + "-"  + month + day  + year + "-" + lesion_number + "." + lesion_media_type;
        TextView imageFileName = (TextView)findViewById(R.id.imageFileName);
        imageFileName.setText(fileName);
        */
	} 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.acitvity_lesionadd, menu);
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

	public void AddLesionClicked (MenuItem item){
		
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		EditText myEditText= (EditText) findViewById(R.id.lesionNumberInput);
    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	//imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
		String lnum = myEditText.getText().toString();
    	if (lnum.length() < 1 ) {
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionAdd.this);
    		alertbox.setTitle("Lesion number must be entered").setMessage("Please enter lesion number").setNeutralButton("OK", null).show();
    		return;
    	} 
    	EditText myEditText2= (EditText) findViewById(R.id.lesionSize);
    	String lsize = myEditText2.getText().toString();
    	if (lsize.length() < 1 ) {
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionAdd.this);
    		alertbox.setTitle("Lesion size must be entered").setMessage("Please Correct Size").setNeutralButton("OK", null).show();
    		return;
    	}
    	EditText myEditText3= (EditText) findViewById(R.id.lesionDescription);
    	String ldesc = myEditText3.getText().toString();
    	if (ldesc.length() < 1 ) {
    		myEditText3.setText("n/a");
    	}   
    	// check media type for valid suffix
    	EditText ftype= (EditText) findViewById(R.id.fileType);
    	String ft = ftype.getText().toString();
    	if (fileTypes.contains(ft)) {
    		// normal
    	} else {
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionAdd.this);
    		alertbox.setTitle("File type must ba a valid type").setMessage("Choose, jpg,png,doc,pdf,html").setNeutralButton("OK", null).show();
    		return;
    	}
    	
    	TextView message = (TextView)findViewById(R.id.makeChanges); 
        message.setText("Adding Lesion");
		new AddLesion().execute();
		
	
		
	}
	public void OnlineClicked(View view) {
		
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		ToggleButton tb = (ToggleButton) findViewById(R.id.tb_imageOnline);
		ImageView mybutton = (ImageView) findViewById(R.id.viewImage);
        if (tb.isChecked()) {
        	// set viewImage button visible
        	mybutton.setVisibility(View.VISIBLE);
        } else { mybutton.setVisibility(View.INVISIBLE); }
		
	}
	public void AddPatient (MenuItem item){
		//Log.i(TAG,"add patient clicked");
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

	   class AddLesion extends AsyncTask<Object, Object, Object> {
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
	    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionAdd.this);
	    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
	    	 }
	    	 @Override
	    	 protected void onPostExecute(Object content) {
	    	    if (error) {
	    	    	Log.i(TAG, "Error occurred"); 
	    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionAdd.this);
	    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
	    	     } else {
	    	        //Log.i(TAG, "Post exexs content=" + content); 
	    	        String s = content.toString();
	    	        try {
						processXMLLesion(s);
						// check succeed for Y
						
						int myint = succeed.length;
						
						if (myint< 1) {
							AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionAdd.this);
				    		alertbox.setTitle("TEAM Server Problem").setMessage("Please Contact Tom").setNeutralButton("OK", null).show();
				    		return;
						}
						if (succeed[0].equals("1")) {
							// all ok
							
					        String ft = MySingleton.getInstance().mediaType;
					        String patient_id = getIntent().getExtras().getString("patient_id");
					 
					        String scan_date = getIntent().getExtras().getString("scan_date");
					        
					        String year = scan_date.substring(2,4);
					        String month = scan_date.substring(5,7);
					        String day = scan_date.substring(8,10);
					        EditText editText2 = (EditText) findViewById(R.id.lesionNumberInput);
					    	String ln = editText2.getText().toString();
					    	
					        String fileName = "Image/File Name " + patient_id + "-"  + month + day  + year + "-" + ln + "." + ft;
					        TextView fn = (TextView)findViewById(R.id.imageFileName);
					        fn.setText(fileName);
						} else {
							AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionAdd.this);
				    		alertbox.setTitle("This lesion already exists").setMessage("Check the lesion number and re-add").setNeutralButton("OK", null).show();
				    		setProgressBarIndeterminateVisibility(false); 
				    		return;
						}
						TextView message = (TextView)findViewById(R.id.makeChanges); 
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
				
				String scan_date = getIntent().getExtras().getString("scan_date");
				
				EditText myEditText= (EditText) findViewById(R.id.lesionNumberInput);
		    	String lesion_number = myEditText.getText().toString(); 
				
				EditText lsize= (EditText) findViewById(R.id.lesionSize);
		    	String lesion_size = lsize.getText().toString(); 
		    	
				EditText editText2 = (EditText) findViewById(R.id.lesionDescription);
				String lesion_description = editText2.getText().toString();
				String lesion_comment = new String(lesion_description.replace("'", " "));
				
				EditText editText3 = (EditText) findViewById(R.id.fileType);
				String lesion_mediat = editText3.getText().toString();
				String lesion_media_type = new String(lesion_mediat.replace("'", " "));
				
				String lesion_target = "N";
				ToggleButton tb = (ToggleButton) findViewById(R.id.tb_target);
		        if (tb.isChecked()) { lesion_target = "Y"; }
		        
		        String lesion_node = "N";
				ToggleButton tb2 = (ToggleButton) findViewById(R.id.tb_lymph);
		        if (tb2.isChecked()) { lesion_node = "Y"; }
		        
		        String lesion_media_online = "N";
				ToggleButton tb3 = (ToggleButton) findViewById(R.id.tb_imageOnline);
		        if (tb3.isChecked()) { lesion_media_online = "Y"; }
		        String username = MySingleton.getInstance().userName;
				String URL = "http://" + dbserver + "/teamaddlesion.php?teamid=" 
				+ teamid + "&patient_id=" + getIntent().getExtras().getString("patient_id") 
				+ "&scan_date=" + scan_date + "&lesion_number=" + lesion_number 
				+ "&lesion_size=" + lesion_size
				+ "&lesion_comment=" + lesion_comment
				+ "&lesion_target=" + lesion_target
				+ "&lesion_media_type=" + lesion_media_type
				+ "&lesion_media_online=" + lesion_media_online
				+ "&lesion_node=" + lesion_node
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
		        Log.i(TAG, "content=" + content);
		        return content;
			}


	    	
	    }
		   public void processXMLLesion(String content) throws ClientProtocolException, IOException {
	    	
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


