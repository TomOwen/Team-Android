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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

public class DoctorEdit extends Activity{
	private static String succeed[] = null;
	private static final String TAG = "Doctor Edit";
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.doctor_edit2);
        String test = MySingleton.getInstance().teamid;
        String doctor_name = getIntent().getExtras().getString("doctor_name");
        String doctor_info = getIntent().getExtras().getString("doctor_info");
        // fill in study_edit form
        TextView dr_name = (TextView)findViewById(R.id.doctor_name);
        dr_name.setText(doctor_name);
        EditText dr_info = (EditText)findViewById(R.id.doctor_info);
        dr_info.setText(doctor_info);
	} 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("Y")) {
			getMenuInflater().inflate(R.menu.activity_doctoredit, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_doctoredit_v, menu);
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
	public void UpdateDoctorClicked (MenuItem item){
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		// check all fields
		EditText dri = (EditText) findViewById(R.id.doctor_info);
    	String dr_info = dri.getText().toString();
    	String temp1 = new String(dr_info.replace("'", " "));
    	dri.setText(temp1);
		new UpdateDoctor().execute();	
	}
	public void DeleteDoctorClicked (MenuItem item){
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
		        	new DeleteDoctor().execute();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Delete this Doctor?\nAre you sure?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
		
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

	   class UpdateDoctor extends AsyncTask<Object, Object, Object> {
	    	protected void onPreExecute() {
	    		//Log.i(TAG, "test in pre execute");
	    		setProgressBarIndeterminateVisibility(true);
	    	 }
	    	 protected void onCancelled() {
	    		Log.i(TAG, "Cancelled");
	    		setProgressBarIndeterminateVisibility(false);
	    		String message = "Got cancelled - " + content;
	    		Toast msg = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
	    		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2); 
	    		//Displaying the alert
	    		msg.show();
	    		AlertDialog.Builder alertbox = new AlertDialog.Builder(DoctorEdit.this);
	    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
	    	 }
	    	 @Override
	    	 protected void onPostExecute(Object content) {
	    	    if (error) {
	    	    	//Log.i(TAG, "Error occurred"); 
	    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(DoctorEdit.this);
	    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
	    	     } else {
	    	       // Log.i(TAG, "Post exexs content=" + content); 
	    	        String s = content.toString();
	    	        try {
						processXMLDoctor(s);
						// check succeed for Y
						
						int myint = succeed.length;
						
						if (myint< 1) {
							AlertDialog.Builder alertbox = new AlertDialog.Builder(DoctorEdit.this);
				    		alertbox.setTitle("TEAM Server Problem").setMessage("Please Contact Tom").setNeutralButton("OK", null).show();
				    		return;
						}
						if (succeed[0].equals("1")) {
							// all ok
						} else {
							AlertDialog.Builder alertbox = new AlertDialog.Builder(DoctorEdit.this);
				    		alertbox.setTitle("Could not update this Doctor").setMessage("Doctor may have been deleted").setNeutralButton("OK", null).show();
				    		return;
						}
						TextView message = (TextView)findViewById(R.id.makeChanges); 
				        message.setText("Enter Changes...then hit save icon");
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
				
				// get all the values for the URL
				String doctor_name = getIntent().getExtras().getString("doctor_name");
				EditText doctor_info = (EditText) findViewById(R.id.doctor_info);
				String dr_info = doctor_info.getText().toString();
				String username = MySingleton.getInstance().userName;
				String URL = "http://" + dbserver + "/teamupdatedoctor.php?teamid=" 
				+ teamid 
				+ "&doctor_name=" + doctor_name
				+ "&doctor_info=" + dr_info
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
		   public void processXMLDoctor(String content) throws ClientProtocolException, IOException {
	    	
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
		   class DeleteDoctor extends AsyncTask<Object, Object, Object> {
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
		    		AlertDialog.Builder alertbox = new AlertDialog.Builder(DoctorEdit.this);
		    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
		    	 }
		    	 @Override
		    	 protected void onPostExecute(Object content) {
		    	    if (error) {
		    	    	//Log.i(TAG, "Error occurred"); 
		    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(DoctorEdit.this);
		    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
		    	     } else {
		    	       // Log.i(TAG, "Post exexs content=" + content); 
		    	        String s = content.toString();
		    	        try {
							processXMLDelete(s);
							// check succeed for Y
							
							int myint = succeed.length;
							
							if (myint< 1) {
								AlertDialog.Builder alertbox = new AlertDialog.Builder(DoctorEdit.this);
					    		alertbox.setTitle("TEAM Server Problem").setMessage("Please Contact Tom").setNeutralButton("OK", null).show();
					    		return;
							}
							if (succeed[0].equals("1")) {
								// all ok
								finish(); // goes back to patient screen
							} else {
								AlertDialog.Builder alertbox = new AlertDialog.Builder(DoctorEdit.this);
					    		alertbox.setTitle("Could not delete this Doctor").setMessage("Doctor may have already been deleted").setNeutralButton("OK", null).show();
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
					String doctor_name = getIntent().getExtras().getString("doctor_name");
					String username = MySingleton.getInstance().userName;
					String URL = "http://" + dbserver + "/teamdeletedoctor.php?teamid=" 
					+ teamid + "&doctor_name=" + doctor_name
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


