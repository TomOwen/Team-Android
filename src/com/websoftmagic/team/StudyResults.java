package com.websoftmagic.team;

import android.util.Log;

public class StudyResults {
	    private static final String TAG = "StudyResults";
		private String study_teamid = "";
		private String study_id = "";
	    private String study_owner = "";
	    private String study_name = "";
	    private String study_url = "";
	    private String study_percentpr = "";
	    private String study_percentpd = "";
	    private String study_seats = "";
	   
	    
	    public void setstudy_teamid(String tid) {
		     this.study_teamid = tid;
		 }

		public String getstudy_teamid() {
		     return study_teamid;
		 }

	    
	    public void setstudy_id(String sid) {
	     this.study_id = sid;
	    }

	    public String getstudy_id() {
	     return study_id;
	    }

	    public void setstudy_owner(String owner) {
	     this.study_owner = owner;
	    }

	    public String getstudy_owner() {
	     return study_owner;
	    }
	    
	    public void setstudy_name(String name) {
	    	//Log.i(TAG,"setting comment=" + comment);
		     this.study_name = name;
		}

		public String getstudy_name() {
			//Log.i(TAG,"getting comment=" + lesion_comment);
		     return study_name;
	 	}
	    
	    
	    public void setstudy_url(String url) {
	        this.study_url = url;
	       }

	    public String getstudy_url() {
	        return study_url;
	       }
	    public void setstudy_percentpr(String pct) {
	        this.study_percentpr = pct;
	       }

	    public String getstudy_percentpr() {
	        return study_percentpr;
	       }
	    public void setstudy_percentpd(String pctpd) {
	        this.study_percentpd = pctpd;
	       }

	    public String getstudy_percentpd() {
	        return study_percentpd;
	       }
	    public void setstudy_seats(String seats) {
	        this.study_seats = seats;
	       }

	    public String getstudy_seats() {
	        return study_seats;
	       }
	}
