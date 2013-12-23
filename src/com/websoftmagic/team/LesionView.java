package com.websoftmagic.team;

import java.io.ByteArrayOutputStream;
import android.graphics.Bitmap; 
import android.webkit.HttpAuthHandler;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class LesionView extends Activity{
	WebView mWebView;
	private static final String TAG = "Lesion View";
	String imageDocServer = MySingleton.getInstance().imageDocServer;
	String imageDocUser = MySingleton.getInstance().imageDocUser;
	String imageDocPassword = MySingleton.getInstance().imageDocPassword;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.lesion_view);
        String test = MySingleton.getInstance().teamid;
        String patient_id = getIntent().getExtras().getString("patient_id");
        String patient_name = getIntent().getExtras().getString("patient_name");
        String scan_date = getIntent().getExtras().getString("scan_date");
        String lesion_number = getIntent().getExtras().getString("lesion_number");
        String lesion_media_type = getIntent().getExtras().getString("lesion_media_type");
        String year = scan_date.substring(2,4);
        String month = scan_date.substring(5,7);
        String day = scan_date.substring(8,10);
        String fileName = patient_id + "-"  + month + day  + year + "-" + lesion_number + "." + lesion_media_type;
        TextView namedate = (TextView)findViewById(R.id.nameDateLesion); 
        namedate.setText(patient_name + " " + month + "/" + day + "/" + year + " (Lesion #" + lesion_number + ")"); 
        
        mWebView = (WebView) findViewById(R.id.webViewLesion);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.clearHistory();   
        mWebView.getSettings().setBuiltInZoomControls(true);
        
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true); 

        // Team Images and Doc for Client 1000
        //mWebView.setHttpAuthUsernamePassword("www.websoftmagic.com","", "teamdoc1000", "TeamDemo143");
        mWebView.setHttpAuthUsernamePassword(imageDocServer,"", imageDocUser, imageDocPassword);

        mWebView.setWebViewClient( new WebViewClient() { 
            @Override 
            public void onReceivedHttpAuthRequest  (WebView view, 
                    HttpAuthHandler handler, String host,String realm){ 
            	//Log.i("in web", "auth");
            	//handler.proceed("teamdoc1000", "TeamDemo143"); 
            	handler.proceed(imageDocUser, imageDocPassword);
                String[] up = view.getHttpAuthUsernamePassword(host, realm); 
                if( up != null && up.length == 2 ) { 
                    //handler.proceed("teamdoc1000", "TeamDemo143"); 
                   // handler.proceed(imageDocUser, imageDocPassword);
                } 
            }
            @Override
            public void onPageStarted(WebView webview, String url,Bitmap favicon) {
            	//Log.i("in web", "started");
            	setProgressBarIndeterminateVisibility(true);
             }
            @Override
            public void onPageFinished(WebView view, String url) {
            	//Log.i("in web", "ended");
            	setProgressBarIndeterminateVisibility(false);
             }
        });
        
        
        if (!isNetworkAvailable()) {
        	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
    		alertbox.setTitle("Your Internet Connection is not online").setMessage("Check your internet settings before proceeding.").setNeutralButton("OK", null).show();
    		return;
		}
        DisplayURL(fileName);
        
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
	public void DisplayURL(String fileName) {
		//mWebView.loadUrl("http://websoftmagic.com/teamdoc1000/100-062512-2.jpg");
		String user_name = MySingleton.getInstance().userName;
		String teamid = MySingleton.getInstance().teamid;
		String patient_id = getIntent().getExtras().getString("patient_id");
		mWebView.loadUrl("http://"+ imageDocServer + "/teamredirect.php?user_teamid=" + teamid 
				+ "&user_name=" + user_name 
				+ "&fn=" + fileName
				+ "&patient_id=" + patient_id
				+ "&study_id=n/a");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.activity_lesionview, menu);
	    return true;
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
	public void GoToHelp (MenuItem item){
		Intent myIntent = new Intent();
		myIntent.setClassName("com.websoftmagic.team", "com.websoftmagic.team.Help");
    	startActivity(myIntent);
	}
}


