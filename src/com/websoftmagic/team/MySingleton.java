package com.websoftmagic.team;
// TEAM singleton for sharing 
// MySingleton.getInstance().setTeamid("1000");
// String test = MySingleton.getInstance().teamid;
public class MySingleton
	{
		public static MySingleton instance;

		public String customVar;
		public String teamid;
		public String dbServer;
		public String dbUser;
		public String dbPassword;
		public String imageDocServer;
		public String imageDocUser;
		public String imageDocPassword;
		public String patientID;
		public String patientName;
		public String scanDate;
		public String scanDate2;
		public String lesionNumber;
		public String lesionNumber2;
		public String userAdmin;
		public String mediaType;
		public String docType;
		public String companyName;
		public String imageExt1;
		public String imageExt2;
		public String scanOnline;
		public String userName;

		public static void initInstance()
		{
			if (instance == null)
			{
				// Create the instance
				instance = new MySingleton();
			}
		}

		public static MySingleton getInstance()
		{
			// Return the instance
			return instance;
		}

		private MySingleton()
		{
			// Constructor hidden because this is a singleton
		}

		public void customSingletonMethod()
		{
			// Custom method
			customVar = "Tom Owen";
		}
		public void setTeamid (String myString) {
			teamid = myString;
		}
		public void setDbserver (String myString) {
			dbServer = myString;
		}
		public void setDbPassword (String myString) {
			dbPassword = myString;
		}
		public void setImageDocServer (String myString) {
			imageDocServer = myString;
		}
		public void setImageDocUser (String myString) {
			imageDocUser = myString;
		}
		public void setImageDocPassword (String myString) {
			imageDocPassword = myString;
		}
		public void setPatientID(String myString) {
			patientID = myString;
		}
		public void setPatientName (String myString) {
			patientName = myString;
		}
		public void setScanDate (String myString) {
			scanDate = myString;
		}
		public void setScanDate2 (String myString) {
			scanDate2 = myString;
		}
		public void setLesionNumber (String myString) {
			lesionNumber = myString;
		}
		public void setLesionNumber2 (String myString) {
			lesionNumber2 = myString;
		}
		public void setUserAdmin (String myString) {
			userAdmin = myString;
		}
		public void setMediaType (String myString) {
			mediaType = myString;
		}
		public void setDocType (String myString) {
			docType = myString;
		}
		public void setDbUser (String myString) {
			dbUser = myString;
		}
		public void setCompanyName (String myString) {
			companyName = myString;
		}
		public void setImageExt1 (String myString) {
			imageExt1 = myString;
		}
		public void setImageExt2 (String myString) {
			imageExt2 = myString;
		}
		public void setScanOnline (String myString) {
			scanOnline = myString;
		}
		public void setUserName (String myString) {
			userName = myString;
		}
		
	}


