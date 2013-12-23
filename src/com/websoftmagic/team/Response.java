package com.websoftmagic.team;

import com.websoftmagic.team.R;


import android.view.View.OnClickListener;
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
import android.util.DisplayMetrics;
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
import android.content.res.Configuration;
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

public class Response extends Activity {
	public  ArrayList<ResponseResults> results = new ArrayList<ResponseResults>();
	public  ArrayList<ResponseResults1> results1 = new ArrayList<ResponseResults1>();
	public  ArrayList<ResponseResults2> results2 = new ArrayList<ResponseResults2>();
	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private final HttpClient httpclient = new DefaultHttpClient();
    final HttpParams params = httpclient.getParams();
    HttpResponse response;
    private String content =  null;
    private String responseContent = null;
    private String responseLesions = null;
    private boolean error = false;
	private static final String TAG = "Response";
	private boolean tablet = true;
	private boolean showingLesions = false;
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //setLayout();
        setContentView(R.layout.response_phone1);
        /*
        String test = MySingleton.getInstance().teamid;
        Log.i(TAG,"teamid=" + test);
        // get scan file name from patient_id and scan_date
        String scan_date = getIntent().getExtras().getString("scan_date");
        String patient_id = MySingleton.getInstance().patientID;
        String patient_name = MySingleton.getInstance().patientName;
        String doc_type = MySingleton.getInstance().docType;
        String year = scan_date.substring(2,4);
        String month = scan_date.substring(5,7);
        String day = scan_date.substring(8,10);
        String fileName = "File Name is " + patient_id + "-"  + month + day  + year + "." + doc_type;
        TextView n = (TextView)findViewById(R.id.textView3);
        n.setText(patient_name + " - " + month + "/" + day + "/" + year);
        TextView nfilename = (TextView)findViewById(R.id.textView2);
        nfilename.setText(fileName);
        */
     
        TextView line1 = (TextView)findViewById(R.id.line1);
        line1.setText("Initializing");
        if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
        line1.setText("Retrieving Patient Response...");
        ReadLesions();
	} 
	public void setLayout() {
		if (isTabletDevice(this)) {
			setContentView(R.layout.response_tablet);
			//Log.i(TAG,"set to tablet");
			tablet = true;
		} else {
			setContentView(R.layout.response_phone1);
			//Log.i(TAG,"set to phone");
			tablet = false;
		}
	}
	/**
	 * Checks if the device is a tablet or a phone
	 * 
	 * @param activityContext
	 *            The Activity Context.
	 * @return Returns true if the device is a Tablet
	 */
	public static boolean isTabletDevice(Context activityContext) {
	    // Verifies if the Generalized Size of the device is XLARGE to be
	    // considered a Tablet
	    boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout & 
	                        Configuration.SCREENLAYOUT_SIZE_MASK) == 
	                        Configuration.SCREENLAYOUT_SIZE_XLARGE);

	    // If XLarge, checks if the Generalized Density is at least MDPI
	    // (160dpi)
	    if (xlarge) {
	        DisplayMetrics metrics = new DisplayMetrics();
	        Activity activity = (Activity) activityContext;
	        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

	        // MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
	        // DENSITY_TV=213, DENSITY_XHIGH=320
	        if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
	                || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
	                || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
	                || metrics.densityDpi == DisplayMetrics.DENSITY_TV
	                || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

	            // Yes, this is a tablet!
	            return true;
	        }
	    }

	    // No, this is not a tablet!
	    return false;
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
		//Log.i(TAG,"Resuming Response");
		super.onResume();
		if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
		//setLayout();
		setContentView(R.layout.response_phone1);
		showingLesions = false;
		ReadLesions();
	}
	public void showHideLesions(View view){
		if (showingLesions) {
			showingLesions = false;
			setContentView(R.layout.response_phone1);
		} else {
			showingLesions = true;
			setContentView(R.layout.response_phone2);
		}
		setResponseLabels();
	}
	public void DetailReport(View view) {
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.DetailReport");
    	startActivity(myIntent);
	}  
	public void DetailGraph(View view) {
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.DetailGraph");
    	startActivity(myIntent);
	}
	public void setResponseLabels() {
		String overall_response_code = results1.get(0).getoverall_response_code();
		
		// line 1 Catherine Reed - Current Scans on mm/dd/yy
		String pname = MySingleton.getInstance().patientName;
		String currentscandate = " - No scans Yet";
		if (results2.size() > 0) {
			currentscandate = " - Current Scans on " + results2.get(0).getcurrent_date();
		}
		TextView line1 = (TextView)findViewById(R.id.line1); 
        line1.setText(pname + currentscandate);
        
        // line 2 Overall Response
        TextView line2 = (TextView)findViewById(R.id.line2); 
        line2.setText(overall_response_code);
        if (overall_response_code.equalsIgnoreCase("CR")) {
        	line2.setText("Overall Response - Complete Response");
        	line2.setTextColor(getResources().getColor(R.color.green)); 
        }
        if (overall_response_code.equalsIgnoreCase("PR")) {
        	line2.setText("Overall Response - Partial Response");
        	line2.setTextColor(getResources().getColor(R.color.yellow)); 
        }
        if (overall_response_code.equalsIgnoreCase("SD")) {
        	line2.setText("Overall Response - Stable Disease");
        	line2.setTextColor(getResources().getColor(R.color.white)); 
        }
        if (overall_response_code.equalsIgnoreCase("PD")) {
        	line2.setText("Overall Response - Progressive Disease");
        	line2.setTextColor(getResources().getColor(R.color.red)); 
        }
        
        // set layout only showing 2 lines and the lesion viewlist - set by button click
        if (showingLesions) {
        	final ListView lv = (ListView) findViewById(R.id.lesionListView);
        	lv.setAdapter(new ResponseAdapter(this, results2));
        	return;
        }
        
        // line 3 Target Response: CR
        String target_response_code = results1.get(0).gettarget_response_code();
        TextView line3 = (TextView)findViewById(R.id.line3); 
        line3.setText(target_response_code);
        if (target_response_code.equalsIgnoreCase("CR")) {
        	line3.setText("Target Response - Complete Response");
        	line3.setTextColor(getResources().getColor(R.color.green)); 
        }
        if (target_response_code.equalsIgnoreCase("PR")) {
        	line3.setText("Target Response - Partial Response");
        	line3.setTextColor(getResources().getColor(R.color.yellow)); 
        }
        if (target_response_code.equalsIgnoreCase("SD")) {
        	line3.setText("Target Response - Stable Disease");
        	line3.setTextColor(getResources().getColor(R.color.white)); 
        }
        if (target_response_code.equalsIgnoreCase("PD")) {
        	line3.setText("Target Response - Progressive Disease");
        	line3.setTextColor(getResources().getColor(R.color.red)); 
        }
        
        // line 4 Current 0 mm Baseline 37 mm -100% CR
        String tc_total_size = results1.get(0).gettc_total_size();
        String tb_total_size = results1.get(0).gettb_total_size();
        String tb_percent_change = results1.get(0).gettb_percent_change();
        String tb_response = results1.get(0).gettb_response();
        TextView line4 = (TextView)findViewById(R.id.line4); 
        line4.setText("Current " + tc_total_size + " mm Baseline " + tb_total_size + " mm " + tb_percent_change + "% " + tb_response);
        
        // line 5 Current 0 mm Smallest 0 mm -100% CR
        String ts_total_size = results1.get(0).getts_total_size();
        String ts_percent_change = results1.get(0).getts_percent_change();
        String ts_response = results1.get(0).getts_response();
        TextView line5 = (TextView)findViewById(R.id.line5); 
        line5.setText("Current " + tc_total_size + " mm Smallest " + ts_total_size + " mm " + ts_percent_change + "% " + ts_response);
        
     // line 6 Non Target Response: CR
        String non_target_response_code = results1.get(0).getnon_target_response_code();
        TextView line6 = (TextView)findViewById(R.id.line6); 
        line6.setText(non_target_response_code);
        if (non_target_response_code.equalsIgnoreCase("CR")) {
        	line6.setText("Non Target Response - Complete Response");
        	line6.setTextColor(getResources().getColor(R.color.green)); 
        }
        if (non_target_response_code.equalsIgnoreCase("PR")) {
        	line6.setText("Non Target Response - Partial Response");
        	line6.setTextColor(getResources().getColor(R.color.yellow)); 
        }
        if (non_target_response_code.equalsIgnoreCase("SD")) {
        	line6.setText("Non Target Response - Stable Disease");
        	line6.setTextColor(getResources().getColor(R.color.white)); 
        }
        if (non_target_response_code.equalsIgnoreCase("PD")) {
        	line6.setText("Non Target Response - Progressive Disease");
        	line6.setTextColor(getResources().getColor(R.color.red)); 
        }
        
        // line 7 Current 0 mm Baseline 37 mm -100% CR
        String ntc_total_size = results1.get(0).getntc_total_size();
        String ntb_total_size = results1.get(0).getntb_total_size();
        String ntb_percent_change = results1.get(0).getntb_percent_change();
        String ntb_response = results1.get(0).getntb_response();
        TextView line7 = (TextView)findViewById(R.id.line7); 
        line7.setText("Current " + ntc_total_size + " mm Baseline " + ntb_total_size + " mm " + ntb_percent_change + "% " + ntb_response);
        
        // line 8 Current 0 mm Smallest 0 mm -100% CR
        String nts_total_size = results1.get(0).getnts_total_size();
        String nts_percent_change = results1.get(0).getnts_percent_change();
        String nts_response = results1.get(0).getnts_response();
        TextView line8 = (TextView)findViewById(R.id.line8); 
        line8.setText("Current " + ntc_total_size + " mm Smallest " + nts_total_size + " mm " + nts_percent_change + "% " + nts_response);
	}
	public void splitResponseContent() {
		// create responseContent and responseLesions from content
		//Log.i(TAG,"content to split->" + content);
		//</nts_response>
		int pos = content.indexOf( "</nts_response>" );
		String sub = content.substring( 0, pos + 15 );
		responseContent = sub + "</lesion></team>";
		//Log.i(TAG,"responseContent->" + responseContent);
		String sub2 = content.substring( pos + 23 );
		responseLesions = "<team>" + sub2;
		//Log.i(TAG,"responseLesions->" + responseLesions);
		
	}
	public void ViewResponseReport (View view){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.ResponseReportView");
		myIntent.putExtra("patient_name", MySingleton.getInstance().patientName);
    	//startActivity(myIntent);
	}
	public void ShowHideLesions (View view){
		
	}
	public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
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
	public void GoToStudies (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Studies");
    	startActivity(myIntent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.activity_response, menu);
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
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(Response.this);
    		alertbox.setTitle("Alert!").setMessage(message).setNeutralButton("OK", null).show();
    	 }
    	 @Override
    	 protected void onPostExecute(Object content) {
    	    if (error) {
    	    	Log.i(TAG, "Error occurred"); 
    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(Response.this);
    	    	alertbox.setTitle("An internet error occurred").setMessage("Try going to another TEAM screen via the menu to reset the connection.").setNeutralButton("OK", null).show();
    	     } else {
    	        //Log.i(TAG, "Post exexs content=" + content); 
    	        String s = content.toString();
    	        try {
    	        	//Log.i(TAG,"processXML");
    	        	splitResponseContent();
					processXML(responseContent); 
					processXML2(responseLesions);
					setResponseLabels();
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
			String patient_id = MySingleton.getInstance().patientID;
			String URL = "http://" + dbserver + "/teamresponse.php?xmloption=Y&teamid=" + teamid + "&patient_id=" + patient_id;
			//Log.i(TAG, "URL=" + URL);
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
		 String overall_response_code[] = null;
	     String target_response_code[] = null;
	     String tc_total_size[] = null;
	     String tb_total_size[] = null;
	     String tb_percent_change[] = null;
	     String tb_response[] = null;
	     String ts_total_size[] = null;
	     String ts_percent_change[] = null;
	     String ts_response[] = null;
	     String non_target_response_code[] = null;
	     String ntc_total_size[] = null;
	     String ntb_total_size[] = null;
	     String ntb_percent_change[] = null;
	     String ntb_response[] = null;
	     String nts_total_size[] = null;
	     String nts_percent_change[] = null;
	     String nts_response[] = null;
	     /*
	     String lesion_number[] = null;
	     String lesion_target[] = null;
	     String current_date[] = null;
	     String baseline_date[] = null;
	     String current_size[] = null;
	     String baseline_size[] = null;
	     String baseline_percent_change[] = null;
	     String smallest_date[] = null;
	     String smallest_size[] = null;
	     String smallest_percent_change[] = null;
	     String new_lesion[] = null;
	     String lesion_node[] = null;
	     */
	    	
	    	try {
	    		//Log.i(TAG,"XML Processing");
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("lesion"); 

	    		/** Assign String array length by arraylist size */
	    		overall_response_code = new String[nodeList.getLength()];
	    		target_response_code = new String[nodeList.getLength()];
	    		tc_total_size = new String[nodeList.getLength()];
	    		tb_total_size = new String[nodeList.getLength()];
	    		tb_percent_change = new String[nodeList.getLength()];
	    		tb_response = new String[nodeList.getLength()];
	    		ts_total_size = new String[nodeList.getLength()];
	    		ts_percent_change = new String[nodeList.getLength()];
	    		ts_response = new String[nodeList.getLength()];
	    		non_target_response_code = new String[nodeList.getLength()];
	    		ntc_total_size = new String[nodeList.getLength()];
	    		ntb_total_size = new String[nodeList.getLength()];
	    		ntb_percent_change = new String[nodeList.getLength()];
	    		ntb_response = new String[nodeList.getLength()];
	    		nts_total_size = new String[nodeList.getLength()];
	    		nts_percent_change = new String[nodeList.getLength()];
	    		nts_response = new String[nodeList.getLength()];
	    		/*
	    		lesion_number = new String[nodeList.getLength()];
	    		lesion_target = new String[nodeList.getLength()];
	    		current_date = new String[nodeList.getLength()];
	    		baseline_date = new String[nodeList.getLength()];
	    		current_size = new String[nodeList.getLength()];
	    		baseline_size = new String[nodeList.getLength()];
	    		baseline_percent_change = new String[nodeList.getLength()];
	    		smallest_date = new String[nodeList.getLength()];
	    		smallest_size = new String[nodeList.getLength()];
	    		smallest_percent_change = new String[nodeList.getLength()];
	    		new_lesion = new String[nodeList.getLength()];
	    		lesion_node = new String[nodeList.getLength()];
	    		*/
	    		//Log.i(TAG,"XML End Processing");

	    		for (int i = 0; i < nodeList.getLength(); i++) {
	    			//Log.i(TAG, "Processing->" + i);
	    		Node node = nodeList.item(i);

	    		overall_response_code[i] = new String("");
	    		target_response_code[i] = new String("");
	    		tc_total_size[i] = new String("");
	    		tb_total_size[i] = new String("");
	    		tb_percent_change[i] = new String("");
	    		tb_response[i] = new String("");
	    		ts_total_size[i] = new String("");
	    		ts_percent_change[i] = new String("");
	    		ts_response[i] = new String("");
	    		non_target_response_code[i] = new String("");
	    		ntc_total_size[i] = new String("");
	    		ntb_total_size[i] = new String("");
	    		ntb_percent_change[i] = new String("");
	    		ntb_response[i] = new String("");
	    		nts_total_size[i] = new String("");
	    		nts_percent_change[i] = new String("");
	    		nts_response[i] = new String("");
	    		/*
	    		lesion_number[i] = new String("");
	    		lesion_target[i] = new String("");
	    		current_date[i] = new String("");
	    		baseline_date[i] = new String("");
	    		current_size[i] = new String("");
	    		baseline_size[i] = new String("");
	    		baseline_percent_change[i] = new String("");
	    		smallest_date[i] = new String("");
	    		smallest_size[i] = new String("");
	    		smallest_percent_change[i] = new String("");
	    		new_lesion[i] = new String("");
	    		lesion_node[i] = new String("");
	    		*/
	    		
	    		
	    		Element fstElmnt = (Element) node;
	    		
	    		NodeList overall_response_codeList = fstElmnt.getElementsByTagName("overall_response_code");
	    		Element overall_response_codeElement = (Element) overall_response_codeList.item(0);
	    		overall_response_codeList = overall_response_codeElement.getChildNodes();
	    		overall_response_code[i] =  ((Node) overall_response_codeList.item(0)).getNodeValue();

	    		NodeList target_response_codeList = fstElmnt.getElementsByTagName("target_response_code");
	    		Element target_response_codeElement = (Element) target_response_codeList.item(0);
	    		target_response_codeList = target_response_codeElement.getChildNodes();
	    		target_response_code[i] =  ((Node) target_response_codeList.item(0)).getNodeValue();

	    		NodeList tc_total_sizeList = fstElmnt.getElementsByTagName("tc_total_size");
	    		Element tc_total_sizeElement = (Element) tc_total_sizeList.item(0);
	    		tc_total_sizeList = tc_total_sizeElement.getChildNodes();
	    		tc_total_size[i] =  ((Node) tc_total_sizeList.item(0)).getNodeValue();

	    		NodeList tb_total_sizeList = fstElmnt.getElementsByTagName("tb_total_size");
	    		Element tb_total_sizeElement = (Element) tb_total_sizeList.item(0);
	    		tb_total_sizeList = tb_total_sizeElement.getChildNodes();
	    		tb_total_size[i] =  ((Node) tb_total_sizeList.item(0)).getNodeValue();

	    		NodeList tb_percent_changeList = fstElmnt.getElementsByTagName("tb_percent_change");
	    		Element tb_percent_changeElement = (Element) tb_percent_changeList.item(0);
	    		tb_percent_changeList = tb_percent_changeElement.getChildNodes();
	    		tb_percent_change[i] =  ((Node) tb_percent_changeList.item(0)).getNodeValue();

	    		NodeList tb_responseList = fstElmnt.getElementsByTagName("tb_response");
	    		Element tb_responseElement = (Element) tb_responseList.item(0);
	    		tb_responseList = tb_responseElement.getChildNodes();
	    		tb_response[i] =  ((Node) tb_responseList.item(0)).getNodeValue();

	    		NodeList ts_total_sizeList = fstElmnt.getElementsByTagName("ts_total_size");
	    		Element ts_total_sizeElement = (Element) ts_total_sizeList.item(0);
	    		ts_total_sizeList = ts_total_sizeElement.getChildNodes();
	    		ts_total_size[i] =  ((Node) ts_total_sizeList.item(0)).getNodeValue();

	    		NodeList ts_percent_changeList = fstElmnt.getElementsByTagName("ts_percent_change");
	    		Element ts_percent_changeElement = (Element) ts_percent_changeList.item(0);
	    		ts_percent_changeList = ts_percent_changeElement.getChildNodes();
	    		ts_percent_change[i] =  ((Node) ts_percent_changeList.item(0)).getNodeValue();

	    		NodeList ts_responseList = fstElmnt.getElementsByTagName("ts_response");
	    		Element ts_responseElement = (Element) ts_responseList.item(0);
	    		ts_responseList = ts_responseElement.getChildNodes();
	    		ts_response[i] =  ((Node) ts_responseList.item(0)).getNodeValue();

	    		NodeList non_target_response_codeList = fstElmnt.getElementsByTagName("non_target_response_code");
	    		Element non_target_response_codeElement = (Element) non_target_response_codeList.item(0);
	    		non_target_response_codeList = non_target_response_codeElement.getChildNodes();
	    		non_target_response_code[i] =  ((Node) non_target_response_codeList.item(0)).getNodeValue();

	    		NodeList ntc_total_sizeList = fstElmnt.getElementsByTagName("ntc_total_size");
	    		Element ntc_total_sizeElement = (Element) ntc_total_sizeList.item(0);
	    		ntc_total_sizeList = ntc_total_sizeElement.getChildNodes();
	    		ntc_total_size[i] =  ((Node) ntc_total_sizeList.item(0)).getNodeValue();

	    		NodeList ntb_total_sizeList = fstElmnt.getElementsByTagName("ntb_total_size");
	    		Element ntb_total_sizeElement = (Element) ntb_total_sizeList.item(0);
	    		ntb_total_sizeList = ntb_total_sizeElement.getChildNodes();
	    		ntb_total_size[i] =  ((Node) ntb_total_sizeList.item(0)).getNodeValue();

	    		NodeList ntb_percent_changeList = fstElmnt.getElementsByTagName("ntb_percent_change");
	    		Element ntb_percent_changeElement = (Element) ntb_percent_changeList.item(0);
	    		ntb_percent_changeList = ntb_percent_changeElement.getChildNodes();
	    		ntb_percent_change[i] =  ((Node) ntb_percent_changeList.item(0)).getNodeValue();

	    		NodeList ntb_responseList = fstElmnt.getElementsByTagName("ntb_response");
	    		Element ntb_responseElement = (Element) ntb_responseList.item(0);
	    		ntb_responseList = ntb_responseElement.getChildNodes();
	    		ntb_response[i] =  ((Node) ntb_responseList.item(0)).getNodeValue();

	    		NodeList nts_total_sizeList = fstElmnt.getElementsByTagName("nts_total_size");
	    		Element nts_total_sizeElement = (Element) nts_total_sizeList.item(0);
	    		nts_total_sizeList = nts_total_sizeElement.getChildNodes();
	    		nts_total_size[i] =  ((Node) nts_total_sizeList.item(0)).getNodeValue();

	    		NodeList nts_percent_changeList = fstElmnt.getElementsByTagName("nts_percent_change");
	    		Element nts_percent_changeElement = (Element) nts_percent_changeList.item(0);
	    		nts_percent_changeList = nts_percent_changeElement.getChildNodes();
	    		nts_percent_change[i] =  ((Node) nts_percent_changeList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->nts" );
	    		NodeList nts_responseList = fstElmnt.getElementsByTagName("nts_response");
	    		Element nts_responseElement = (Element) nts_responseList.item(0);
	    		nts_responseList = nts_responseElement.getChildNodes();
	    		nts_response[i] =  ((Node) nts_responseList.item(0)).getNodeValue();
/*
	    		Log.i(TAG, "Processing->lesion" );
	    		NodeList lesion_numberList = fstElmnt.getElementsByTagName("lesion_number");
	    		Element lesion_numberElement = (Element) lesion_numberList.item(0);
	    		lesion_numberList = lesion_numberElement.getChildNodes();
	    		lesion_number[i] =  ((Node) lesion_numberList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->target" );
	    		NodeList lesion_targetList = fstElmnt.getElementsByTagName("lesion_target");
	    		Element lesion_targetElement = (Element) lesion_targetList.item(0);
	    		lesion_targetList = lesion_targetElement.getChildNodes();
	    		lesion_target[i] =  ((Node) lesion_targetList.item(0)).getNodeValue();
	    		
	    		Log.i(TAG, "Processing->1" );
	    		NodeList current_dateList = fstElmnt.getElementsByTagName("current_date");
	    		Element current_dateElement = (Element) current_dateList.item(0);
	    		current_dateList = current_dateElement.getChildNodes();
	    		current_date[i] =  ((Node) current_dateList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->2" );
	    		NodeList baseline_dateList = fstElmnt.getElementsByTagName("baseline_date");
	    		Element baseline_dateElement = (Element) baseline_dateList.item(0);
	    		baseline_dateList = baseline_dateElement.getChildNodes();
	    		baseline_date[i] =  ((Node) baseline_dateList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->3" );
	    		NodeList current_sizeList = fstElmnt.getElementsByTagName("current_size");
	    		Element current_sizeElement = (Element) current_sizeList.item(0);
	    		current_sizeList = current_sizeElement.getChildNodes();
	    		current_size[i] =  ((Node) current_sizeList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->4" );
	    		NodeList baseline_sizeList = fstElmnt.getElementsByTagName("baseline_size");
	    		Element baseline_sizeElement = (Element) baseline_sizeList.item(0);
	    		baseline_sizeList = baseline_sizeElement.getChildNodes();
	    		baseline_size[i] =  ((Node) baseline_sizeList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->5" );
	    		NodeList baseline_percent_changeList = fstElmnt.getElementsByTagName("baseline_percent_change");
	    		Element baseline_percent_changeElement = (Element) baseline_percent_changeList.item(0);
	    		baseline_percent_changeList = baseline_percent_changeElement.getChildNodes();
	    		baseline_percent_change[i] =  ((Node) baseline_percent_changeList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->6" );
	    		NodeList smallest_dateList = fstElmnt.getElementsByTagName("smallest_date");
	    		Element smallest_dateElement = (Element) smallest_dateList.item(0);
	    		smallest_dateList = smallest_dateElement.getChildNodes();
	    		smallest_date[i] =  ((Node) smallest_dateList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->7" );
	    		NodeList smallest_sizeList = fstElmnt.getElementsByTagName("smallest_size");
	    		Element smallest_sizeElement = (Element) smallest_sizeList.item(0);
	    		smallest_sizeList = smallest_sizeElement.getChildNodes();
	    		smallest_size[i] =  ((Node) smallest_sizeList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->8" );
	    		NodeList smallest_percent_changeList = fstElmnt.getElementsByTagName("smallest_percent_change");
	    		Element smallest_percent_changeElement = (Element) smallest_percent_changeList.item(0);
	    		smallest_percent_changeList = smallest_percent_changeElement.getChildNodes();
	    		smallest_percent_change[i] =  ((Node) smallest_percent_changeList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->9" );
	    		NodeList new_lesionList = fstElmnt.getElementsByTagName("new_lesion");
	    		Element new_lesionElement = (Element) new_lesionList.item(0);
	    		new_lesionList = new_lesionElement.getChildNodes();
	    		new_lesion[i] =  ((Node) new_lesionList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->10" );
	    		NodeList lesion_nodeList = fstElmnt.getElementsByTagName("lesion_node");
	    		Element lesion_nodeElement = (Element) lesion_nodeList.item(0);
	    		lesion_nodeList = lesion_nodeElement.getChildNodes();
	    		lesion_node[i] =  ((Node) lesion_nodeList.item(0)).getNodeValue();
*/	    		
	    		//Log.i(TAG, "Processing->done" );
	    		

	    		
	    	
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
	    	    results1 = new ArrayList<ResponseResults1>();
	    	   
	    	    ResponseResults1 sr = new ResponseResults1();
	    	    // if length is 0 then error from server
	    	    if (overall_response_code.length < 1) {
	    	    	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        		alertbox.setTitle("Invalid response from TEAM Server").setMessage("Contact TEAM support for assistance.").setNeutralButton("OK", null).show();
	        		return;
	    	    }
	    	    for (int i = 0; i < overall_response_code.length; i++) {
	    	    	// if teamid is 0 then no matching patients were found
	    	    	/*
	    	    	String myid = lesion_number[i];
	    	    	if (myid.equalsIgnoreCase("0")) {
	    	    		// no lesions to display
		        		return;
	    	    	}
	    	    	*/
	    	    	//Log.i(TAG,"begin sr");
	    	 
	    	    	sr = new ResponseResults1();
	    	    	
	    	    	sr.setoverall_response_code(overall_response_code[i]);
	    	    	sr.settarget_response_code(target_response_code[i]);
	    	    	sr.settc_total_size(tc_total_size[i]);
	    	    	sr.settb_total_size(tb_total_size[i]);
	    	    	sr.settb_percent_change(tb_percent_change[i]);
	    	    	sr.settb_response(tb_response[i]);
	    	    	sr.setts_total_size(ts_total_size[i]);
	    	    	sr.setts_percent_change(ts_percent_change[i]);
	    	    	sr.setts_response(ts_response[i]);
	    	    	sr.setnon_target_response_code(non_target_response_code[i]);
	    	    	sr.setntc_total_size(ntc_total_size[i]);
	    	    	sr.setntb_total_size(ntb_total_size[i]);
	    	    	sr.setntb_percent_change(ntb_percent_change[i]);
	    	    	sr.setntb_response(ntb_response[i]);
	    	    	sr.setnts_total_size(nts_total_size[i]);
	    	    	sr.setnts_percent_change(nts_percent_change[i]);
	    	    	sr.setnts_response(nts_response[i]);
	    	    	/*
	    	    	sr.setlesion_number(lesion_number[i]);
	    	    	sr.setlesion_target(lesion_target[i]);
	    	    	sr.setcurrent_date(current_date[i]);
	    	    	sr.setbaseline_date(baseline_date[i]);
	    	    	sr.setcurrent_size(current_size[i]);
	    	    	sr.setbaseline_size(baseline_size[i]);
	    	    	sr.setbaseline_percent_change(baseline_percent_change[i]);
	    	    	sr.setsmallest_date(smallest_date[i]);
	    	    	sr.setsmallest_size (smallest_size[i]);
	    	    	sr.setsmallest_percent_change(smallest_percent_change[i]);
	    	    	sr.setnew_lesion(new_lesion[i]);
	    	    	sr.setlesion_node(lesion_node[i]);
	    	    	*/
	    	    
	   	     		//sr.setCityState(patient_doctor[i] + " / " + patient_study_id[i]);
	   	     		results1.add(sr);
	    	    }
	    	    
	    	    
	    	    //final ListView lv = (ListView) findViewById(R.id.lesionListView);
	           // lv.setAdapter(new ResponseAdapter(this, results));
	     
	            
	    	    
	    }
	   public void processXML2(String content) throws ClientProtocolException, IOException {
		 /*
		 String overall_response_code[] = null;
	     String target_response_code[] = null;
	     String tc_total_size[] = null;
	     String tb_total_size[] = null;
	     String tb_percent_change[] = null;
	     String tb_response[] = null;
	     String ts_total_size[] = null;
	     String ts_percent_change[] = null;
	     String ts_response[] = null;
	     String non_target_response_code[] = null;
	     String ntc_total_size[] = null;
	     String ntb_total_size[] = null;
	     String ntb_percent_change[] = null;
	     String ntb_response[] = null;
	     String nts_total_size[] = null;
	     String nts_percent_change[] = null;
	     String nts_response[] = null;
	     */
	     String lesion_number[] = null;
	     String lesion_target[] = null;
	     String current_date[] = null;
	     String baseline_date[] = null;
	     String current_size[] = null;
	     String baseline_size[] = null;
	     String baseline_percent_change[] = null;
	     String smallest_date[] = null;
	     String smallest_size[] = null;
	     String smallest_percent_change[] = null;
	     String new_lesion[] = null;
	     String lesion_node[] = null;
	     
	    	
	    	try {
	    		//Log.i(TAG,"XML Processing");
	    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder db = dbf.newDocumentBuilder();
	    		Document doc = db.parse(new InputSource(new StringReader(content)));
	    		doc.getDocumentElement().normalize();

	    		NodeList nodeList = doc.getElementsByTagName("lesion"); 

	    		/** Assign String array length by arraylist size */
	    		/*
	    		overall_response_code = new String[nodeList.getLength()];
	    		target_response_code = new String[nodeList.getLength()];
	    		tc_total_size = new String[nodeList.getLength()];
	    		tb_total_size = new String[nodeList.getLength()];
	    		tb_percent_change = new String[nodeList.getLength()];
	    		tb_response = new String[nodeList.getLength()];
	    		ts_total_size = new String[nodeList.getLength()];
	    		ts_percent_change = new String[nodeList.getLength()];
	    		ts_response = new String[nodeList.getLength()];
	    		non_target_response_code = new String[nodeList.getLength()];
	    		ntc_total_size = new String[nodeList.getLength()];
	    		ntb_total_size = new String[nodeList.getLength()];
	    		ntb_percent_change = new String[nodeList.getLength()];
	    		ntb_response = new String[nodeList.getLength()];
	    		nts_total_size = new String[nodeList.getLength()];
	    		nts_percent_change = new String[nodeList.getLength()];
	    		nts_response = new String[nodeList.getLength()];
	    		*/
	    		lesion_number = new String[nodeList.getLength()];
	    		lesion_target = new String[nodeList.getLength()];
	    		current_date = new String[nodeList.getLength()];
	    		baseline_date = new String[nodeList.getLength()];
	    		current_size = new String[nodeList.getLength()];
	    		baseline_size = new String[nodeList.getLength()];
	    		baseline_percent_change = new String[nodeList.getLength()];
	    		smallest_date = new String[nodeList.getLength()];
	    		smallest_size = new String[nodeList.getLength()];
	    		smallest_percent_change = new String[nodeList.getLength()];
	    		new_lesion = new String[nodeList.getLength()];
	    		lesion_node = new String[nodeList.getLength()];
	    		
	    		//Log.i(TAG,"XML End Processing");

	    		for (int i = 0; i < nodeList.getLength(); i++) {
	    			//Log.i(TAG, "Processing->" + i);
	    		Node node = nodeList.item(i);

	    		/*
	    		overall_response_code[i] = new String("");
	    		target_response_code[i] = new String("");
	    		tc_total_size[i] = new String("");
	    		tb_total_size[i] = new String("");
	    		tb_percent_change[i] = new String("");
	    		tb_response[i] = new String("");
	    		ts_total_size[i] = new String("");
	    		ts_percent_change[i] = new String("");
	    		ts_response[i] = new String("");
	    		non_target_response_code[i] = new String("");
	    		ntc_total_size[i] = new String("");
	    		ntb_total_size[i] = new String("");
	    		ntb_percent_change[i] = new String("");
	    		ntb_response[i] = new String("");
	    		nts_total_size[i] = new String("");
	    		nts_percent_change[i] = new String("");
	    		nts_response[i] = new String("");
	    		*/
	    		lesion_number[i] = new String("");
	    		lesion_target[i] = new String("");
	    		current_date[i] = new String("");
	    		baseline_date[i] = new String("");
	    		current_size[i] = new String("");
	    		baseline_size[i] = new String("");
	    		baseline_percent_change[i] = new String("");
	    		smallest_date[i] = new String("");
	    		smallest_size[i] = new String("");
	    		smallest_percent_change[i] = new String("");
	    		new_lesion[i] = new String("");
	    		lesion_node[i] = new String("");
	    		
	    		
	    		
	    		Element fstElmnt = (Element) node;
	    		/*
	    		NodeList overall_response_codeList = fstElmnt.getElementsByTagName("overall_response_code");
	    		Element overall_response_codeElement = (Element) overall_response_codeList.item(0);
	    		overall_response_codeList = overall_response_codeElement.getChildNodes();
	    		overall_response_code[i] =  ((Node) overall_response_codeList.item(0)).getNodeValue();

	    		NodeList target_response_codeList = fstElmnt.getElementsByTagName("target_response_code");
	    		Element target_response_codeElement = (Element) target_response_codeList.item(0);
	    		target_response_codeList = target_response_codeElement.getChildNodes();
	    		target_response_code[i] =  ((Node) target_response_codeList.item(0)).getNodeValue();

	    		NodeList tc_total_sizeList = fstElmnt.getElementsByTagName("tc_total_size");
	    		Element tc_total_sizeElement = (Element) tc_total_sizeList.item(0);
	    		tc_total_sizeList = tc_total_sizeElement.getChildNodes();
	    		tc_total_size[i] =  ((Node) tc_total_sizeList.item(0)).getNodeValue();

	    		NodeList tb_total_sizeList = fstElmnt.getElementsByTagName("tb_total_size");
	    		Element tb_total_sizeElement = (Element) tb_total_sizeList.item(0);
	    		tb_total_sizeList = tb_total_sizeElement.getChildNodes();
	    		tb_total_size[i] =  ((Node) tb_total_sizeList.item(0)).getNodeValue();

	    		NodeList tb_percent_changeList = fstElmnt.getElementsByTagName("tb_percent_change");
	    		Element tb_percent_changeElement = (Element) tb_percent_changeList.item(0);
	    		tb_percent_changeList = tb_percent_changeElement.getChildNodes();
	    		tb_percent_change[i] =  ((Node) tb_percent_changeList.item(0)).getNodeValue();

	    		NodeList tb_responseList = fstElmnt.getElementsByTagName("tb_response");
	    		Element tb_responseElement = (Element) tb_responseList.item(0);
	    		tb_responseList = tb_responseElement.getChildNodes();
	    		tb_response[i] =  ((Node) tb_responseList.item(0)).getNodeValue();

	    		NodeList ts_total_sizeList = fstElmnt.getElementsByTagName("ts_total_size");
	    		Element ts_total_sizeElement = (Element) ts_total_sizeList.item(0);
	    		ts_total_sizeList = ts_total_sizeElement.getChildNodes();
	    		ts_total_size[i] =  ((Node) ts_total_sizeList.item(0)).getNodeValue();

	    		NodeList ts_percent_changeList = fstElmnt.getElementsByTagName("ts_percent_change");
	    		Element ts_percent_changeElement = (Element) ts_percent_changeList.item(0);
	    		ts_percent_changeList = ts_percent_changeElement.getChildNodes();
	    		ts_percent_change[i] =  ((Node) ts_percent_changeList.item(0)).getNodeValue();

	    		NodeList ts_responseList = fstElmnt.getElementsByTagName("ts_response");
	    		Element ts_responseElement = (Element) ts_responseList.item(0);
	    		ts_responseList = ts_responseElement.getChildNodes();
	    		ts_response[i] =  ((Node) ts_responseList.item(0)).getNodeValue();

	    		NodeList non_target_response_codeList = fstElmnt.getElementsByTagName("non_target_response_code");
	    		Element non_target_response_codeElement = (Element) non_target_response_codeList.item(0);
	    		non_target_response_codeList = non_target_response_codeElement.getChildNodes();
	    		non_target_response_code[i] =  ((Node) non_target_response_codeList.item(0)).getNodeValue();

	    		NodeList ntc_total_sizeList = fstElmnt.getElementsByTagName("ntc_total_size");
	    		Element ntc_total_sizeElement = (Element) ntc_total_sizeList.item(0);
	    		ntc_total_sizeList = ntc_total_sizeElement.getChildNodes();
	    		ntc_total_size[i] =  ((Node) ntc_total_sizeList.item(0)).getNodeValue();

	    		NodeList ntb_total_sizeList = fstElmnt.getElementsByTagName("ntb_total_size");
	    		Element ntb_total_sizeElement = (Element) ntb_total_sizeList.item(0);
	    		ntb_total_sizeList = ntb_total_sizeElement.getChildNodes();
	    		ntb_total_size[i] =  ((Node) ntb_total_sizeList.item(0)).getNodeValue();

	    		NodeList ntb_percent_changeList = fstElmnt.getElementsByTagName("ntb_percent_change");
	    		Element ntb_percent_changeElement = (Element) ntb_percent_changeList.item(0);
	    		ntb_percent_changeList = ntb_percent_changeElement.getChildNodes();
	    		ntb_percent_change[i] =  ((Node) ntb_percent_changeList.item(0)).getNodeValue();

	    		NodeList ntb_responseList = fstElmnt.getElementsByTagName("ntb_response");
	    		Element ntb_responseElement = (Element) ntb_responseList.item(0);
	    		ntb_responseList = ntb_responseElement.getChildNodes();
	    		ntb_response[i] =  ((Node) ntb_responseList.item(0)).getNodeValue();

	    		NodeList nts_total_sizeList = fstElmnt.getElementsByTagName("nts_total_size");
	    		Element nts_total_sizeElement = (Element) nts_total_sizeList.item(0);
	    		nts_total_sizeList = nts_total_sizeElement.getChildNodes();
	    		nts_total_size[i] =  ((Node) nts_total_sizeList.item(0)).getNodeValue();

	    		NodeList nts_percent_changeList = fstElmnt.getElementsByTagName("nts_percent_change");
	    		Element nts_percent_changeElement = (Element) nts_percent_changeList.item(0);
	    		nts_percent_changeList = nts_percent_changeElement.getChildNodes();
	    		nts_percent_change[i] =  ((Node) nts_percent_changeList.item(0)).getNodeValue();

	    		Log.i(TAG, "Processing->nts" );
	    		NodeList nts_responseList = fstElmnt.getElementsByTagName("nts_response");
	    		Element nts_responseElement = (Element) nts_responseList.item(0);
	    		nts_responseList = nts_responseElement.getChildNodes();
	    		nts_response[i] =  ((Node) nts_responseList.item(0)).getNodeValue();
			*/
	    		//Log.i(TAG, "Processing->lesion" );
	    		NodeList lesion_numberList = fstElmnt.getElementsByTagName("lesion_number");
	    		Element lesion_numberElement = (Element) lesion_numberList.item(0);
	    		lesion_numberList = lesion_numberElement.getChildNodes();
	    		lesion_number[i] =  ((Node) lesion_numberList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->target" );
	    		NodeList lesion_targetList = fstElmnt.getElementsByTagName("lesion_target");
	    		Element lesion_targetElement = (Element) lesion_targetList.item(0);
	    		lesion_targetList = lesion_targetElement.getChildNodes();
	    		lesion_target[i] =  ((Node) lesion_targetList.item(0)).getNodeValue();
	    		
	    		//Log.i(TAG, "Processing->1" );
	    		NodeList current_dateList = fstElmnt.getElementsByTagName("current_date");
	    		Element current_dateElement = (Element) current_dateList.item(0);
	    		current_dateList = current_dateElement.getChildNodes();
	    		current_date[i] =  ((Node) current_dateList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->2" );
	    		NodeList baseline_dateList = fstElmnt.getElementsByTagName("baseline_date");
	    		Element baseline_dateElement = (Element) baseline_dateList.item(0);
	    		baseline_dateList = baseline_dateElement.getChildNodes();
	    		baseline_date[i] =  ((Node) baseline_dateList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->3" );
	    		NodeList current_sizeList = fstElmnt.getElementsByTagName("current_size");
	    		Element current_sizeElement = (Element) current_sizeList.item(0);
	    		current_sizeList = current_sizeElement.getChildNodes();
	    		current_size[i] =  ((Node) current_sizeList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->4" );
	    		NodeList baseline_sizeList = fstElmnt.getElementsByTagName("baseline_size");
	    		Element baseline_sizeElement = (Element) baseline_sizeList.item(0);
	    		baseline_sizeList = baseline_sizeElement.getChildNodes();
	    		baseline_size[i] =  ((Node) baseline_sizeList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->5" );
	    		NodeList baseline_percent_changeList = fstElmnt.getElementsByTagName("baseline_percent_change");
	    		Element baseline_percent_changeElement = (Element) baseline_percent_changeList.item(0);
	    		baseline_percent_changeList = baseline_percent_changeElement.getChildNodes();
	    		baseline_percent_change[i] =  ((Node) baseline_percent_changeList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->6" );
	    		NodeList smallest_dateList = fstElmnt.getElementsByTagName("smallest_date");
	    		Element smallest_dateElement = (Element) smallest_dateList.item(0);
	    		smallest_dateList = smallest_dateElement.getChildNodes();
	    		smallest_date[i] =  ((Node) smallest_dateList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->7" );
	    		NodeList smallest_sizeList = fstElmnt.getElementsByTagName("smallest_size");
	    		Element smallest_sizeElement = (Element) smallest_sizeList.item(0);
	    		smallest_sizeList = smallest_sizeElement.getChildNodes();
	    		smallest_size[i] =  ((Node) smallest_sizeList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->8" );
	    		NodeList smallest_percent_changeList = fstElmnt.getElementsByTagName("smallest_percent_change");
	    		Element smallest_percent_changeElement = (Element) smallest_percent_changeList.item(0);
	    		smallest_percent_changeList = smallest_percent_changeElement.getChildNodes();
	    		smallest_percent_change[i] =  ((Node) smallest_percent_changeList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->9" );
	    		NodeList new_lesionList = fstElmnt.getElementsByTagName("new_lesion");
	    		Element new_lesionElement = (Element) new_lesionList.item(0);
	    		new_lesionList = new_lesionElement.getChildNodes();
	    		new_lesion[i] =  ((Node) new_lesionList.item(0)).getNodeValue();

	    		//Log.i(TAG, "Processing->10" );
	    		NodeList lesion_nodeList = fstElmnt.getElementsByTagName("lesion_node");
	    		Element lesion_nodeElement = (Element) lesion_nodeList.item(0);
	    		lesion_nodeList = lesion_nodeElement.getChildNodes();
	    		lesion_node[i] =  ((Node) lesion_nodeList.item(0)).getNodeValue();
    		
	    		//Log.i(TAG, "Processing->done" );
	    		

	    		
	    	
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
	    	    results2 = new ArrayList<ResponseResults2>();
	    	   
	    	    ResponseResults2 sr = new ResponseResults2();
	    	    // if length is 0 then error from server
	    	    if (lesion_number.length < 1) {
	    	    	// no lesions
	    	    	return;
	    	    }
	    	    for (int i = 0; i < lesion_number.length; i++) {
	    	    	// if teamid is 0 then no matching patients were found
	    	    	/*
	    	    	String myid = lesion_number[i];
	    	    	if (myid.equalsIgnoreCase("0")) {
	    	    		// no lesions to display
		        		return;
	    	    	}
	    	    	*/
	    	    	//Log.i(TAG,"begin sr");
	    	 
	    	    	sr = new ResponseResults2();
	    	    	/*
	    	    	sr.setoverall_response_code(overall_response_code[i]);
	    	    	sr.settarget_response_code(target_response_code[i]);
	    	    	sr.settc_total_size(tc_total_size[i]);
	    	    	sr.settb_total_size(tb_total_size[i]);
	    	    	sr.settb_percent_change(tb_percent_change[i]);
	    	    	sr.settb_response(tb_response[i]);
	    	    	sr.setts_total_size(ts_total_size[i]);
	    	    	sr.setts_percent_change(ts_percent_change[i]);
	    	    	sr.setts_response(ts_response[i]);
	    	    	sr.setnon_target_response_code(non_target_response_code[i]);
	    	    	sr.setntc_total_size(ntc_total_size[i]);
	    	    	sr.setntb_total_size(ntb_total_size[i]);
	    	    	sr.setntb_percent_change(ntb_percent_change[i]);
	    	    	sr.setntb_response(ntb_response[i]);
	    	    	sr.setnts_total_size(nts_total_size[i]);
	    	    	sr.setnts_percent_change(nts_percent_change[i]);
	    	    	sr.setnts_response(nts_response[i]);
	    	    	*/
	    	    	sr.setlesion_number(lesion_number[i]);
	    	    	sr.setlesion_target(lesion_target[i]);
	    	    	sr.setcurrent_date(current_date[i]);
	    	    	sr.setbaseline_date(baseline_date[i]);
	    	    	sr.setcurrent_size(current_size[i]);
	    	    	sr.setbaseline_size(baseline_size[i]);
	    	    	sr.setbaseline_percent_change(baseline_percent_change[i]);
	    	    	sr.setsmallest_date(smallest_date[i]);
	    	    	sr.setsmallest_size (smallest_size[i]);
	    	    	sr.setsmallest_percent_change(smallest_percent_change[i]);
	    	    	sr.setnew_lesion(new_lesion[i]);
	    	    	sr.setlesion_node(lesion_node[i]);
	    	    	
	    	    
	   	     		//sr.setCityState(patient_doctor[i] + " / " + patient_study_id[i]);
	   	     		results2.add(sr);
	    	    }
	    	    //setResponseLabels();
	    	
	    	    //final ListView lv = (ListView) findViewById(R.id.lesionListView);
	    	  
	            //lv.setAdapter(new ResponseAdapter(this, results2));
	        
	     
	            
	    	    
	    }
}



