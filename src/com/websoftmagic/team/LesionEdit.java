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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;

public class LesionEdit extends Activity{
	private static final Set<String> fileTypes = new HashSet<String>(Arrays.asList(
		     new String[] {"jpg","png","doc","pdf","html"}
		));
	
	private static String succeed[] = null;
	
	private static String drop_teamid[] = null;
	private static String drop_type[] = null;
	private static String drop_string[] = null;
	List<String> doctors = new ArrayList<String>();
	List<String> studies = new ArrayList<String>();
	private static final String TAG = "Lesion Edit";
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
        setContentView(R.layout.lesion_edit2);
        String test = MySingleton.getInstance().teamid;
        String patient_id = getIntent().getExtras().getString("patient_id");
        String patient_name = getIntent().getExtras().getString("patient_name");
        String scan_date = getIntent().getExtras().getString("scan_date");
        String lesion_number = getIntent().getExtras().getString("lesion_number");
        String lesion_size = getIntent().getExtras().getString("lesion_size");
        String lesion_comment = getIntent().getExtras().getString("lesion_comment");
        String lesion_target = getIntent().getExtras().getString("lesion_target");
        String lesion_media_type = getIntent().getExtras().getString("lesion_media_type");
        String lesion_node = getIntent().getExtras().getString("lesion_node");
        String lesion_media_online = getIntent().getExtras().getString("lesion_media_online");
        
        TextView patientName = (TextView)findViewById(R.id.patientName);
        patientName.setText(patient_name);
        TextView scanDate = (TextView)findViewById(R.id.scanDate);
        scanDate.setText(scan_date.substring(5,7) + "/" + scan_date.substring(8,10) + "/" + scan_date.substring(2,4));
        TextView lesionNumber = (TextView)findViewById(R.id.lesionNumber);
        lesionNumber.setText("Lesion #" + lesion_number);
        TextView lesionDescription = (TextView)findViewById(R.id.lesionDescription);
        lesionDescription.setText(lesion_comment);
        TextView lesionSize = (TextView)findViewById(R.id.lesionSize);
        lesionSize.setText(lesion_size);
        ToggleButton tb = (ToggleButton) findViewById(R.id.tb_target);
        if (lesion_target.equalsIgnoreCase("Y")) {
        	tb.setChecked(true);
        } else { tb.setChecked(false);}
        
        
        ToggleButton tb2 = (ToggleButton) findViewById(R.id.tb_lymph);
        if (lesion_node.equalsIgnoreCase("Y")) {
        	tb2.setChecked(true);
        } else { tb2.setChecked(false);}
        
        ToggleButton tb3 = (ToggleButton) findViewById(R.id.tb_imageOnline);
        if (lesion_media_online.equalsIgnoreCase("Y")) {
        	tb3.setChecked(true);
        } else { tb3.setChecked(false);}

		ImageView mybutton = (ImageView) findViewById(R.id.viewImage);
        if (tb3.isChecked()) {
        	// set viewImage button visible
        	mybutton.setVisibility(View.VISIBLE);
        } else { mybutton.setVisibility(View.INVISIBLE); }

        TextView fileType = (TextView)findViewById(R.id.fileType);
        //Log.i(TAG,"ftype =" + fileType);
        fileType.setText(lesion_media_type);

        TextView imageFileName = (TextView)findViewById(R.id.imageFileName);

        String year = scan_date.substring(2,4);
        String month = scan_date.substring(5,7);
        String day = scan_date.substring(8,10);
        String fileName = "Image/File Name " + patient_id + "-"  + month + day  + year + "-" + lesion_number + "." + lesion_media_type;

        imageFileName.setText(fileName);
	} 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (MySingleton.getInstance().userAdmin.equalsIgnoreCase("Y")) {
			getMenuInflater().inflate(R.menu.activity_lesionedit, menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_lesionedit_v, menu);
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

	public void UpdateLesionClicked (MenuItem item){
		
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		EditText myEditText= (EditText) findViewById(R.id.lesionSize);
    	//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	//imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
    	String lsize = myEditText.getText().toString();
    	if (lsize.length() < 1 ) {
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
    		alertbox.setTitle("Lesion size must be entered").setMessage("Please Correct Size").setNeutralButton("OK", null).show();
    		return;
    	} 
    	// check media type for valid suffix
    	EditText ftype= (EditText) findViewById(R.id.fileType);
    	String ft = ftype.getText().toString();
    	if (fileTypes.contains(ft)) {
    		// normal
    	} else {
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
    		alertbox.setTitle("File type must ba a valid type").setMessage("Choose, jpg,png,doc,pdf,html").setNeutralButton("OK", null).show();
    		return;
    	}
    	TextView message = (TextView)findViewById(R.id.makeChanges); 
        message.setText("Updating Lesion");
		new UpdateLesion().execute();
		
	
		
	}
	public void DeleteLesionClicked (MenuItem item){
		
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
		        	new DeleteLesion().execute();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Delete this Lesion?\nAre you sure?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
		
	}
	public void ViewLesion(View view) {
		
		Intent myIntent = new Intent();
    	myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.LesionView");
    	myIntent.putExtra("patient_id", MySingleton.getInstance().patientID);
    	myIntent.putExtra("patient_name", MySingleton.getInstance().patientName);
    	myIntent.putExtra("scan_date", getIntent().getExtras().getString("scan_date"));
    	String test = MySingleton.getInstance().teamid;
    	myIntent.putExtra("lesion_number", MySingleton.getInstance().lesionNumber);
    	myIntent.putExtra("lesion_media_type", MySingleton.getInstance().mediaType);
    	startActivity(myIntent);
    	
	}
	public void OnlineClicked(View view) {
		
		ToggleButton tb = (ToggleButton) findViewById(R.id.tb_imageOnline);
		ImageView mybutton = (ImageView) findViewById(R.id.viewImage);
        if (tb.isChecked()) {
        	// set viewImage button visible
        	mybutton.setVisibility(View.VISIBLE);
        } else { mybutton.setVisibility(View.INVISIBLE); }
		
	}
	public void AddPatient (MenuItem item){
		//Log.i(TAG,"add patient clicked");
		
		//Log.i(TAG, "Selected ->" + updated_PatientDoctor);
	}
	public void studyClicked(View view) {
		
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Studies");
    	startActivity(myIntent);
	}
	public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
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

	   class UpdateLesion extends AsyncTask<Object, Object, Object> {
	    	protected void onPreExecute() {
	    		
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
	    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
	    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
	    	 }
	    	 @Override
	    	 protected void onPostExecute(Object content) {
	    	    if (error) {
	    	    	Log.i(TAG, "Error occurred"); 
	    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
	    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
	    	     } else {
	    	        //Log.i(TAG, "Post exexs content=" + content); 
	    	        String s = content.toString();
	    	        try {
						processXMLLesion(s);
						// check succeed for Y
						
						int myint = succeed.length;
						
						if (myint< 1) {
							AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
				    		alertbox.setTitle("TEAM Server Problem").setMessage("Please Contact Tom").setNeutralButton("OK", null).show();
				    		return;
						}
						if (succeed[0].equals("1")) {
							// all ok
						} else {
							AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
				    		alertbox.setTitle("Could not update this Lesion").setMessage("Lesion may have been deleted").setNeutralButton("OK", null).show();
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
				String lesion_number = getIntent().getExtras().getString("lesion_number");
				
				EditText myEditText= (EditText) findViewById(R.id.lesionSize);
		    	String lesion_size = myEditText.getText().toString(); 
		    	
		    	EditText myEditText3= (EditText) findViewById(R.id.lesionDescription);
		    	String ldesc = myEditText3.getText().toString(); 
		    	if (ldesc.length() < 1 ) {
		    		myEditText3.setText("n/a");
		    	}  
		    	 
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
				String URL = "http://" + dbserver + "/teamupdatelesion.php?teamid=" 
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
		       // Log.i(TAG, "content=" + content);
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
		   class DeleteLesion extends AsyncTask<Object, Object, Object> {
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
		    		AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
		    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
		    	 }
		    	 @Override
		    	 protected void onPostExecute(Object content) {
		    	    if (error) {
		    	    	Log.i(TAG, "Error occurred"); 
		    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
		    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
		    	     } else {
		    	        //Log.i(TAG, "Post exexs content=" + content); 
		    	        String s = content.toString();
		    	        try {
							processXMLDelete(s);
							// check succeed for Y
							
							int myint = succeed.length;
							
							if (myint< 1) {
								AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
					    		alertbox.setTitle("TEAM Server Problem").setMessage("Please Contact Tom").setNeutralButton("OK", null).show();
					    		return;
							}
							if (succeed[0].equals("1")) {
								// all ok
								finish(); // goes back to patient screen
							} else {
								AlertDialog.Builder alertbox = new AlertDialog.Builder(LesionEdit.this);
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
					
					String scan_date = getIntent().getExtras().getString("scan_date");
					String lesion_number = getIntent().getExtras().getString("lesion_number");
					String username = MySingleton.getInstance().userName;
					String URL = "http://" + dbserver + "/teamdeletelesion.php?teamid=" 
					+ teamid + "&patient_id=" + getIntent().getExtras().getString("patient_id")
					+ "&scan_date=" + scan_date
					+ "&lesion_number=" + lesion_number
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
			       // Log.i(TAG, "content=" + content);
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

