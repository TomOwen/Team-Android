package com.websoftmagic.team;

import android.util.Log;

public class LesionResults {
	    private static final String TAG = "LesionResults";
		private String lesion_teamid = "";
		private String lesion_number = "";
	    private String lesion_size = "";
	    private String lesion_comment = "";
	    private String lesion_target = "";
	    private String lesion_media_type = "";
	    private String lesion_media_online = "";
	    private String lesion_node = "";
	    
	    public void setlesion_teamid(String tid) {
		     this.lesion_teamid = tid;
		 }

		public String getlesion_teamid() {
		     return lesion_teamid;
		 }

	    
	    public void setlesion_number(String lesion) {
	     this.lesion_number = lesion;
	    }

	    public String getlesion_number() {
	     return lesion_number;
	    }

	    public void setlesion_size(String lesionsize) {
	     this.lesion_size = lesionsize;
	    }

	    public String getlesion_size() {
	     return lesion_size;
	    }
	    
	    public void setlesion_comment(String comment) {
	    	//Log.i(TAG,"setting comment=" + comment);
		     this.lesion_comment = comment;
		}

		public String getlesion_comment() {
			//Log.i(TAG,"getting comment=" + lesion_comment);
		     return lesion_comment;
		}
	    
	    
	    public void setlesion_target(String target) {
	        this.lesion_target = target;
	       }

	    public String getlesion_target() {
	        return lesion_target;
	       }
	    public void setlesion_media_type(String media) {
	        this.lesion_media_type = media;
	       }

	    public String getlesion_media_type() {
	        return lesion_media_type;
	       }
	    public void setlesion_media_online(String online) {
	        this.lesion_media_online = online;
	       }

	    public String getlesion_media_online() {
	        return lesion_media_online;
	       }
	    public void setlesion_node(String node) {
	        this.lesion_node = node;
	       }

	    public String getlesion_node() {
	        return lesion_node;
	       }
	}
