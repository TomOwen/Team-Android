package com.websoftmagic.team;

import java.io.BufferedReader;
import android.app.SearchableInfo;




import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import android.app.AlertDialog.Builder;
import android.widget.CheckBox;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



import android.widget.LinearLayout;
import android.widget.TextView;


public class Login extends Activity {
	public boolean rememeberChecked = true;
	private static String email = "";
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
 
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private boolean error = false;
	protected MyApp app;
	private static final String TAG = "Login";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login2);
        // Get the application instance - will initialize MySingleton for rest of App
		app = (MyApp)getApplication();
		getPreferences();
		
		
    }
    public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
    	startActivity(myIntent);
		
	}
    public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) 
	      getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
    public void demoClicked (View view) {
    	EditText myEditText= (EditText) findViewById(R.id.userName);
		myEditText.setText("demo");
		EditText myEditText2= (EditText) findViewById(R.id.userPassword);
		myEditText2.setText("demo"); 
		checkLogin(null);
    } 
    public void getPreferences () {
    	SharedPreferences preferences = getPreferences(MODE_PRIVATE);
    	String remember = preferences.getString("remember", "Y");
    	String userName = preferences.getString("user", "Y");
    	String userPassword = preferences.getString("password", "Y");
    	//Log.i(TAG, "user=" + userName + "password=" + userPassword + "rememeber=" + remember); 
    	if (remember.equals("Y")) {
    		EditText myEditText= (EditText) findViewById(R.id.userName);
    		myEditText.setText(userName);
    		EditText myEditText2= (EditText) findViewById(R.id.userPassword);
    		myEditText2.setText(userPassword);  
    	} else {
    		CheckBox checkBox = (CheckBox)findViewById(R.id.remember);
    		checkBox.setChecked(false);
        	checkBox.toggle();
    	}
    	
    }
    public void rememeberClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        CheckBox checkBox = (CheckBox)findViewById(R.id.remember);
        if (checked) {
        	checkBox.setChecked(false);
        	checkBox.toggle();
        	rememeberChecked = true;
        } else {
        	checkBox.setChecked(true);
        	checkBox.toggle();
        	rememeberChecked = false;
        }
    }  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
    public void checkLogin(View view) {
    	// check if user wants to save name/password
    	if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
    	CheckBox checkBox = (CheckBox)findViewById(R.id.remember);
		/*
    	if (checkBox.isChecked()) {
			Log.i(TAG, "Checked");
		} else {
			Log.i(TAG, "Un Checked");
		}
		*/
    	
    	EditText myEditText= (EditText) findViewById(R.id.userName);
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
    	// check if username and password > 0
    	EditText editText = (EditText) findViewById(R.id.userName);
    	String userName = editText.getText().toString();
    	EditText editText2 = (EditText) findViewById(R.id.userPassword);
    	String userPassword = editText2.getText().toString();
    	if ((userName.length() < 1) || (userPassword.length() < 1)) {
    		new AlertDialog.Builder(this).setTitle("Both User Name and Password must be entered.").setMessage("Please try again").setNeutralButton("OK", null).show();
    	    return;
    	}
    	new TeamPost().execute();
    }
    class TeamPost extends AsyncTask<Object, Object, Object> {
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
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(Login.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Login.this);
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
			EditText editText = (EditText) findViewById(R.id.userName);
        	String userName = editText.getText().toString();
        	EditText editText2 = (EditText) findViewById(R.id.userPassword);
        	String userPassword = editText2.getText().toString();
        	//Log.i(TAG, "background userName/Password is " + userName + "/" + userPassword);
        	String URL = "http://www.websoftmagic.com/teamquery/team.php?search1=" + userName + "&search2=" + userPassword;
			// build the URL string
	        try {
	        	
	            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
	            HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
	            ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
	            HttpPost httpPost = new HttpPost(URL);
	            
	            String authString = ("team_admin:Teamdb143143");
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
    	String teamid[] = null;
    	String db_server[] = null;
    	String patientID[] = null;
    	String patientName[] = null;
    	String scanDate[] = null;
    	String scanDate2[] = null;
    	String lesionNumber[] = null;
    	String lesionNumber2[] = null;
    	String userAdmin[] = null;
    	String mediaType[] = null;
    	String docType[] = null;
    	String dbUser[] = null;
    	String dbPassword[] = null;
    	String imagedoc_server[] = null;
    	String imagedocUser[] = null;
    	String imagedocPassword[] = null;
    	String companyName[] = null;
    	String imageExt1[] = null;
    	String imageExt2[] = null;
    	
    	try {
    		//Log.i(TAG,"Content =" + content);
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		Document doc = db.parse(new InputSource(new StringReader(content)));
    		doc.getDocumentElement().normalize();

    		NodeList nodeList = doc.getElementsByTagName("settings");

    		/** Assign String array length by arraylist size */
    		teamid = new String[nodeList.getLength()];
    		db_server = new String[nodeList.getLength()];
    		patientID = new String[nodeList.getLength()];
    		patientName = new String[nodeList.getLength()];
    		scanDate = new String[nodeList.getLength()];
    		scanDate2 = new String[nodeList.getLength()];
    		lesionNumber = new String[nodeList.getLength()];
    		lesionNumber2 = new String[nodeList.getLength()];
    		userAdmin = new String[nodeList.getLength()];
    		mediaType = new String[nodeList.getLength()];
    		docType = new String[nodeList.getLength()];
    		dbUser = new String[nodeList.getLength()];
    		dbPassword = new String[nodeList.getLength()];
    		imagedoc_server = new String[nodeList.getLength()];
    		imagedocUser = new String[nodeList.getLength()];
    		imagedocPassword = new String[nodeList.getLength()];
    		companyName = new String[nodeList.getLength()];
    		imageExt1 = new String[nodeList.getLength()];
    		imageExt2 = new String[nodeList.getLength()];

    		for (int i = 0; i < nodeList.getLength(); i++) {

    		Node node = nodeList.item(i);

    		teamid[i] = new String("");
    		db_server[i] = new String("");
    		patientID[i] = new String("");
    		patientName[i] = new String("");
    		scanDate[i] = new String("");
    		scanDate2[i] = new String("");
    		lesionNumber[i] = new String("");
    		lesionNumber2[i] = new String("");
    		userAdmin[i] = new String("");
    		mediaType[i] = new String("");
    		docType[i] = new String("");
    		dbUser[i] = new String("");
    		dbPassword[i] = new String("");
    		imagedoc_server[i] = new String("");
    		imagedocUser[i] = new String("");
    		imagedocPassword[i] = new String("");
    		companyName[i] = new String("");
    		imageExt1[i] = new String("");
    		imageExt2[i] = new String("");
    		
    		Element fstElmnt = (Element) node;
    		
    		NodeList teamidList = fstElmnt.getElementsByTagName("teamid");
    		Element teamidElement = (Element) teamidList.item(0);
    		teamidList = teamidElement.getChildNodes();
    		teamid[i] =  ((Node) teamidList.item(0)).getNodeValue();
    		
    		NodeList dbserverList = fstElmnt.getElementsByTagName("db_server");
    		Element dbserverElement = (Element) dbserverList.item(0);
    		dbserverList = dbserverElement.getChildNodes();
    		db_server[i] = ((Node) dbserverList.item(0)).getNodeValue();
    		

    		NodeList dbUserList = fstElmnt.getElementsByTagName("db_user");
    		Element dbUserElement = (Element) dbUserList.item(0);
    		dbUserList = dbUserElement.getChildNodes();
    		dbUser[i] = ((Node) dbUserList.item(0)).getNodeValue();


    		NodeList dbPasswordList = fstElmnt.getElementsByTagName("db_password");
    		Element dbPasswordElement = (Element) dbPasswordList.item(0);
    		dbPasswordList = dbPasswordElement.getChildNodes();
    		dbPassword[i] = ((Node) dbPasswordList.item(0)).getNodeValue();
    		
    		NodeList imagedoc_serverList = fstElmnt.getElementsByTagName("imagedoc_server");
    		Element imagedoc_serverElement = (Element) imagedoc_serverList.item(0);
    		imagedoc_serverList = imagedoc_serverElement.getChildNodes();
    		imagedoc_server[i] = ((Node) imagedoc_serverList.item(0)).getNodeValue();

    		NodeList imagedocUserList = fstElmnt.getElementsByTagName("imagedoc_user");
    		Element imagedocUserElement = (Element) imagedocUserList.item(0);
    		imagedocUserList = imagedocUserElement.getChildNodes();
    		imagedocUser[i] = ((Node) imagedocUserList.item(0)).getNodeValue();

    		NodeList imagedocPasswordList = fstElmnt.getElementsByTagName("imagedoc_password");
    		Element imagedocPasswordElement = (Element) imagedocPasswordList.item(0);
    		imagedocPasswordList = imagedocPasswordElement.getChildNodes();
    		imagedocPassword[i] = ((Node) imagedocPasswordList.item(0)).getNodeValue();
    		
    		NodeList userAdminList = fstElmnt.getElementsByTagName("user_admin_access");
    		Element userAdminElement = (Element) userAdminList.item(0);
    		userAdminList = userAdminElement.getChildNodes();
    		userAdmin[i] = ((Node) userAdminList.item(0)).getNodeValue(); 
    		
    		NodeList mediaTypeList = fstElmnt.getElementsByTagName("image_type");
    		Element mediaTypeElement = (Element) mediaTypeList.item(0);
    		mediaTypeList = mediaTypeElement.getChildNodes();
    		mediaType[i] = ((Node) mediaTypeList.item(0)).getNodeValue();

    		NodeList docTypeList = fstElmnt.getElementsByTagName("doc_type");
    		Element docTypeElement = (Element) docTypeList.item(0);
    		docTypeList = docTypeElement.getChildNodes();
    		docType[i] = ((Node) docTypeList.item(0)).getNodeValue();
    		
    		NodeList companyNameList = fstElmnt.getElementsByTagName("company_name");
    		Element companyNameElement = (Element) companyNameList.item(0);
    		companyNameList = companyNameElement.getChildNodes();
    		companyName[i] = ((Node) companyNameList.item(0)).getNodeValue();
    		
    		// none below this line will process. a parsing exeption occurs
    		/*
    		NodeList patientIDList = fstElmnt.getElementsByTagName("patientID");
    		Element patientIDElement = (Element) patientIDList.item(0);
    		patientIDList = patientIDElement.getChildNodes();
    		patientID[i] = ((Node) patientIDList.item(0)).getNodeValue();
    		
    		NodeList patientNameList = fstElmnt.getElementsByTagName("patientName");
    		Element patientNameElement = (Element) patientNameList.item(0);
    		patientNameList = patientNameElement.getChildNodes();
    		patientName[i] = ((Node) patientNameList.item(0)).getNodeValue();

    		NodeList scanDateList = fstElmnt.getElementsByTagName("scanDate");
    		Element scanDateElement = (Element) scanDateList.item(0);
    		scanDateList = scanDateElement.getChildNodes();
    		scanDate[i] = ((Node) scanDateList.item(0)).getNodeValue();

    		NodeList lesionNumberList = fstElmnt.getElementsByTagName("lesionNumber");
    		Element lesionNumberElement = (Element) lesionNumberList.item(0);
    		lesionNumberList = lesionNumberElement.getChildNodes();
    		lesionNumber[i] = ((Node) lesionNumberList.item(0)).getNodeValue();

    		NodeList lesionNumber2List = fstElmnt.getElementsByTagName("lesionNumber2");
    		Element lesionNumber2Element = (Element) lesionNumber2List.item(0);
    		lesionNumber2List = lesionNumber2Element.getChildNodes();
    		lesionNumber2[i] = ((Node) lesionNumber2List.item(0)).getNodeValue();
    	
    		NodeList imageExt1List = fstElmnt.getElementsByTagName("imageExt1");
    		Element imageExt1Element = (Element) imageExt1List.item(0);
    		imageExt1List = imageExt1Element.getChildNodes();
    		imageExt1[i] = ((Node) imageExt1List.item(0)).getNodeValue();
    		
    		NodeList imageExt2List = fstElmnt.getElementsByTagName("imageExt2");
    		Element imageExt2Element = (Element) imageExt2List.item(0);
    		imageExt2List = imageExt2Element.getChildNodes();
    		imageExt2[i] = ((Node) imageExt2List.item(0)).getNodeValue();
    		
    		// end 
    		*/
    	
    		}
    		} catch (Exception e) {
    		System.out.println("XML Pasing Excpetion = " + e);
    		}
    	    for (int i = 0; i < teamid.length; i++) {
    	    	MySingleton.getInstance().setTeamid(teamid[i]);
    	    	//Log.i(TAG, "teamid = " + teamid[i]);
    	    }
    	    for (int i = 0; i < db_server.length; i++) {
    	    	MySingleton.getInstance().setDbserver(db_server[i]);
    	    	//Log.i(TAG, "db_server = " + db_server[i]);
    	    }
    	    for (int i = 0; i < dbUser.length; i++) {
    	    	MySingleton.getInstance().setDbUser(dbUser[i]);
    	    	//Log.i(TAG, "dbUser = " + dbUser[i]);
    	    }
    	    for (int i = 0; i < dbPassword.length; i++) {
    	    	MySingleton.getInstance().setDbPassword(dbPassword[i]);
    	    	//Log.i(TAG, "dbPassword = " + dbPassword[i]);
    	    }
    	    for (int i = 0; i < imagedoc_server.length; i++) {
    	    	MySingleton.getInstance().setImageDocServer(imagedoc_server[i]);
    	    	//Log.i(TAG, "imagedoc_server = " + imagedoc_server[i]);
    	    }
    	    for (int i = 0; i < imagedocUser.length; i++) {
    	    	MySingleton.getInstance().setImageDocUser(imagedocUser[i]);
    	    	//Log.i(TAG, "imagedocUser = " + imagedocUser[i]);
    	    }
    	    for (int i = 0; i < imagedocPassword.length; i++) {
    	    	MySingleton.getInstance().setImageDocPassword(imagedocPassword[i]);
    	    	//Log.i(TAG, "imagedocPassword = " + imagedocPassword[i]);
    	    }
    	    for (int i = 0; i < userAdmin.length; i++) {
    	    	MySingleton.getInstance().setUserAdmin(userAdmin[i]);
    	    	//Log.i(TAG, "userAdmin = " + userAdmin[i]);
    	    }
    	    for (int i = 0; i < mediaType.length; i++) {
    	    	MySingleton.getInstance().setMediaType(mediaType[i]);
    	    	//Log.i(TAG, "mediaType = " + mediaType[i]);
    	    }
    	    for (int i = 0; i < docType.length; i++) {
    	    	MySingleton.getInstance().setDocType(docType[i]);
    	    	//Log.i(TAG, "docType = " + docType[i]);
    	    }
    	    
    	    for (int i = 0; i < companyName.length; i++) {
    	    	MySingleton.getInstance().setCompanyName(companyName[i]);
    	    	//Log.i(TAG, "companyName = " + companyName[i]);
    	    }
    	    EditText editText3 = (EditText) findViewById(R.id.userName);
        	String userName3 = editText3.getText().toString();
        	MySingleton.getInstance().setUserName(userName3);
        	
    	    // now check teamid for non zero signifying a good teamid login
    	    if (MySingleton.getInstance().teamid.length() > 1) {
    	    	// go to patient display
    	    	SharedPreferences preferences = getPreferences(MODE_PRIVATE);
    	    	SharedPreferences.Editor editor = preferences.edit();
    	    	
    	    	CheckBox checkBox = (CheckBox)findViewById(R.id.remember);
    	    	String ischeck = "N";
    			if (checkBox.isChecked()) {
    				ischeck = "Y";
    			}
    	    	EditText editText = (EditText) findViewById(R.id.userName);
    	    	String userName = editText.getText().toString();
    	    	EditText editText2 = (EditText) findViewById(R.id.userPassword);
    	    	String userPassword = editText2.getText().toString();
    	    	editor.putString("remember",ischeck); // value to store
    	    	editor.putString("user", userName);
    	    	editor.putString("password", userPassword); 
    	    	editor.commit();
    	    	Intent myIntent = new Intent();
    	    	//myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Patients");
    	    	//myIntent.setClassName(this, "com.websoftmagic.team.Patients.Patients");
    	    	myIntent.setClassName(this, "com.websoftmagic.team.Patients");
    	    	startActivity(myIntent);
    	    } else {
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Login.this);
        		alertbox.setTitle("Alert!").setMessage("Sorry\n That user name/password is incorrect!").setNeutralButton("OK", null).show();

    	    }
    }
    public void forgotClicked (View view){
    	if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
    	email = "tho@cox.net";
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Change/Reset Your Password");
    	alert.setMessage("Please enter your email");
    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	alert.setView(input);
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	    email = input.getText().toString();
    	    new ForgotPassword().execute();
    	    }
    	});
    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int whichButton) {
    	   // Canceled.
    	   }
    	});
    	alert.show();
    	
    	//new ForgotPassword().execute();
    }
    class ForgotPassword extends AsyncTask<Object, Object, Object> {
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
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(Login.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Login.this);
    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
    	     } else {
    	        //Log.i(TAG, "Post exexs content=" + content); 
    	        String s = content.toString(); 
				processForgotXML(s);
    	    }
    	    setProgressBarIndeterminateVisibility(false); 
    	  }
    	 @Override
 		protected Object doInBackground(Object... paramsx) {
 			//Log.i(TAG, "test in background");
 			EditText editText = (EditText) findViewById(R.id.userName);
         	String userName = editText.getText().toString();
         	EditText editText2 = (EditText) findViewById(R.id.userPassword);
         	String userPassword = editText2.getText().toString();
         	//Log.i(TAG, "background userName/Password is " + userName + "/" + userPassword);
         	String URL = "http://www.websoftmagic.com/login/teamsendemail.php?user_email=" + email;
 			// build the URL string
 	        try {
 	        	
 	            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
 	            HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
 	            ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);
 	            
 	            HttpPost httpPost = new HttpPost(URL);
 	            //HttpGet httpPost = new HttpGet(URL);
 	            
 	            String authString = ("team_admin:Teamdb143143");
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
    public void processForgotXML(String s) {
    	//Log.i(TAG, "Ok check=" + s);
    	int pos = s.indexOf( "<succeed>Y" );
    	if (pos > 0) {
    		// alert that email has been sent
    		new AlertDialog.Builder(this).setTitle("Success! Found " + email).setMessage("Check your email for instructions.").setNeutralButton("OK", null).show();
    	} else {
    		// alert that email was not found
    		new AlertDialog.Builder(this).setTitle("Sorry, I couldn't find " + email).setMessage("Please try again").setNeutralButton("OK", null).show();
    	}
    }
    
}
