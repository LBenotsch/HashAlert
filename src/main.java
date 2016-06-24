import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

public class main {
	
	public static void main (String[] args) throws Exception{ 

    	//Import data from config.txt
    	String[] data = ReadConfig.returnFileData();
    	
    	String webPage = "";
    	String strrefreshMin = "";
    	String emailSender = "";
    	String emailSenderPass = "";
    	String recipient1 = "";
    	String recipient2 = "";
    	String strErrorMinimum = "";
    	String locationName = "";
    	
    	int rigCount = 0;
    	int rigError = 0;
        int rigNumber = 0;
    	//int resumeAlert = 0;
    	
	    List<String> rigName = new ArrayList<String>();
	    List<Double> rigMegaHash = new ArrayList<Double>();
	    List<String> rigIp = new ArrayList<String>();

		//Data validation
    	if (data[0].equals("")){
    		System.out.println("webPage was not provided, check config. Exiting...");
    		System.exit(0);
    	}else{
    		webPage = data[0];
    	}
    	if (data[1].equals("")){
    		System.out.println("refreshMin was not provided, check config. Exiting...");
    		System.exit(0);
    	}else{
    		strrefreshMin = data[1];
    	}
    	if (data[2].equals("")) {
    		System.out.println("Enter at least one reciepent email address, check config. Exiting...");
    		System.exit(0);
    	}else{
    		recipient1 = data[2];
    	}
		recipient2 = data[3];
		if (data[4].equals("")){
    		System.out.println("emailSender was not provided, check config. Exiting...");
    		System.exit(0);
    	}else{
    		emailSender = data[4];
    	}
		if (data[5].equals("")){
    		System.out.println("emailSenderPass was not provided, check config. Exiting...");
    		System.exit(0);
    	}else{
    		emailSenderPass = data[5];
    	}
		if (data[6].equals("") || Integer.parseInt(data[6]) < 0){
    		System.out.println("errorMinimum was not provided or value was a negitive number, check config. Exiting...");
    		System.exit(0);
    	}else{
    		strErrorMinimum = data[6];
    	}
		if (data[7].equals("")){
    		System.out.println("locationName was not provided, check config. Exiting...");
    		System.exit(0);
    	}else{
    		locationName = data[7];
    	}
    	
    	int refreshMin = Integer.parseInt(strrefreshMin);
    	int errorMinimum = Integer.parseInt(strErrorMinimum);
    	
    	//Get initial rig names from ethdistro JSON data
		JSONObject json1 = null;
		Iterator<?> iterator1 = null;
		try {
			json1 = importJSON.readJsonFromUrl(webPage+"/?json=yes");
			iterator1 = json1.getJSONObject("rigs").keys();
		} catch (Exception e) {
			System.out.println("No rigs found on the webpage provided. Check config. Exiting...");
			System.exit(0);
		}
	    ArrayList<String> ar1 = new ArrayList<String>();
	    while (iterator1.hasNext()) {
	    	   String key = (String)iterator1.next();
	    	   ar1.add(key);
	    	}
	    rigCount = ar1.size(); //Get number of Rigs being used
	    if (rigCount == 0) {
	    	System.out.println("Error, no rigs found on provided EthDistro webpage.");
	    	System.exit(0);
	    }
	    //Assign initial rignames and get megahash from ethdistro JSON data
	    for (int i=rigCount; i>0 ; i--) {
	    	rigName.add(ar1.get(i-1));
	    	rigMegaHash.add(Double.parseDouble(json1.getJSONObject("rigs").getJSONObject(ar1.get(i-1)).getString("hash")));
	    	rigIp.add(json1.getJSONObject("rigs").getJSONObject(ar1.get(i-1)).getString("ip"));
	    }
    	
    	//Display properties to console
    	System.out.println("--Welcome to the HashAlert Application--");
    	System.out.println("Pointing to: "+webPage);
    	System.out.println("Monitored Rigs:");
    	rigNumber = 0;
    	for (int i=rigCount; i>0 ; i--) {
    		rigNumber++;
    		System.out.println("  Rig"+rigNumber+": "+rigName.get(i-1)+" ("+rigIp.get(i-1)+")");
    	}
    		System.out.println("  Using global minimum of: "+errorMinimum);
    		System.out.println("  Email alerts to: "+recipient1+" "+recipient2);
    	
    	//While there's no error in the MH/s, keep running
		while (rigError < 2) {
			//Get rig names from ethdistro JSON data
			JSONObject json = importJSON.readJsonFromUrl(webPage+"/?json=yes");
		    Iterator<?> iterator = json.getJSONObject("rigs").keys();
		    ArrayList<String> ar = new ArrayList<String>();
		    while (iterator.hasNext()) {
		    	   String key = (String)iterator.next();
		    	   ar.add(key);
		    	}
		    rigCount = ar.size(); //Get number of Rigs being used
		    
		    //Assign rignames and get megahash from ethdistro JSON data
		    rigName.clear();
		    rigMegaHash.clear();
		    rigIp.clear();
		    for (int i=rigCount; i>0 ; i--) {
		    	rigName.add(ar.get(i-1));
		    	rigMegaHash.add(Double.parseDouble(json.getJSONObject("rigs").getJSONObject(ar.get(i-1)).getString("hash")));
		    	rigIp.add(json.getJSONObject("rigs").getJSONObject(ar.get(i-1)).getString("ip"));
		    }
	        //Get Current Date/Time
	        Date now = new Date();
	        System.out.println("\nMiner Status - "+locationName);
	        System.out.println("As of: "+now);
	        //Display data in console
	        rigNumber = 0;
	        for (int i=rigCount; i>0 ; i--) {
	        	rigNumber++;
	    		System.out.println("Rig"+rigNumber+": "+rigName.get(i-1)+" ("+rigIp.get(i-1)+")....."+rigMegaHash.get(i-1)+" MH/s");
	    	}
	        System.out.println("Using global minimum of: "+errorMinimum);
	        //Error handling
	        rigNumber = 0;
	        for (int i=rigMegaHash.size(); i>0 ; i--) {
		        if (rigMegaHash.get(i-1) < errorMinimum) {
		        	rigNumber++;
		        	rigError++;
		        	System.out.println("ALERT! Rig"+rigNumber+"("+rigName.get(i-1)+") has dropped below "+errorMinimum+" MH/s. It is currently: "+rigMegaHash.get(i-1)+" MH/s");
		        }
	        }
	        /*if (rigError == 1) {
	        	System.out.println("Detected error in one or more miners. Refreshing in 5 seconds to confirm...");
	        	TimeUnit.SECONDS.sleep(5);
	        }*/
	        //When an error has been detected, send an alert email
	        if (rigError > 0){
	            String[] to = {recipient1, recipient2};
	            if (to[1].equals("")){
	            	to = new String[] {recipient1};
	            }
	            String subject = "ALERT! "+rigError+" Miner(s) at '"+locationName+"' Need Your Attention!";
	            String body = "";
	            body = "Miner Status:\n"+
	            	   "As of: "+now+"\n\n";
	            rigNumber = 0;
	            for (int i=rigCount; i>0 ; i--) {
	            	rigNumber++;
	            	body +="Rig"+rigNumber+": "+rigName.get(i-1)+" ("+rigIp.get(i-1)+")......."+rigMegaHash.get(i-1)+" MH/s\n";
	            }
	            body += "For more information, vist "+webPage+"\n"+
	            			"Sent from HashAlert.jar";
	            //Send email alert
	            System.out.println("Sending Email Alert to: "+recipient1+" "+recipient2+" ...");
	            SendEmail.sendFromGMail(emailSender, emailSenderPass, to, subject, body);
	            System.out.println("\nRerun this application once miners are back on to resume alerts.");
	            System.exit(0); //Stop application after error has been found, and email alert has been sent
	            }
	        if (rigError == 0) {
	        	System.out.println("Refreshing every "+refreshMin+" Mins...");
	        	TimeUnit.MINUTES.sleep(refreshMin); //To prevent spam requests to the website, pull hash info every x minutes
	        }
	        }
		}
    }