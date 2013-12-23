package com.websoftmagic.team;

import android.util.Log;

public class DoctorResults {
	    private static final String TAG = "DoctorResults";
		private String doctor_teamid = "";
		private String doctor_name = "";
	    private String doctor_info = "";
	    
	    
	    public void setdoctor_teamid(String tid) {
		     this.doctor_teamid = tid;
		 }

		public String getdoctor_teamid() {
		     return doctor_teamid;
		 }

	    
	    public void setdoctor_name(String name) {
	     this.doctor_name = name;
	    }

	    public String getdoctor_name() {
	     return doctor_name;
	    }

	    public void setdoctor_info(String info) {
	     this.doctor_info = info;
	    }

	    public String getsdoctor_info() {
	     return doctor_info;
	    }
	}
