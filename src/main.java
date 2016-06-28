import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
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
    	
    	int rigCountLast = 0;
    	int rigCountCurrent = 0;
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
		JSONObject initialJSON = null;
		Iterator<?> initialIterator = null;
		try {
			initialJSON = importJSON.readJsonFromUrl(webPage+"/?json=yes");
			//initialJSON = importJSON.readJsonFromUrl(webPage);
			initialIterator = initialJSON.getJSONObject("rigs").keys();
		} catch (Exception e) {
			System.out.println("No rigs found on the webpage provided. Check config. Exiting...");
			System.exit(0);
		}
		//Assign initial rignames and get megahash from ethdistro JSON data
		ArrayList<String> initialAL = new ArrayList<String>();
	    while (initialIterator.hasNext()) {
	    	   String key = (String)initialIterator.next();
	    	   initialAL.add(key);
	    	}
	    for (int i=initialAL.size(); i>0; i--) {
	    	if (initialJSON.getJSONObject("rigs").getJSONObject(initialAL.get(i-1)).getString("condition").equals("unreachable")) {
	    		initialJSON.getJSONObject("rigs").remove(initialAL.get(i-1));
	    	}else{
	    		rigName.add(initialAL.get(i-1));
	 		   	try {
					rigMegaHash.add(Double.parseDouble(initialJSON.getJSONObject("rigs").getJSONObject(initialAL.get(i-1)).getString("hash")));
				} catch (JSONException e) {
					rigMegaHash.add((double) 0);
				}
	 		   	rigIp.add(initialJSON.getJSONObject("rigs").getJSONObject(initialAL.get(i-1)).getString("ip"));
	    	}
	    }
	    if (rigName.size() == 0) {
	    	System.out.println("No rigs found on provided EthDistro webpage. Exiting...");
	    	System.exit(0);
	    }	    
    	
    	//Display properties to console
    	System.out.println("--Welcome to the HashAlert Application--");
    	System.out.println("Pointing to: "+webPage);
    	System.out.println("Monitored Rigs:");
		System.out.println("Note: Ignoring all 'unreachable' rigs.");
    	rigNumber = 0;
    	for (int i=rigName.size(); i>0 ; i--) {
    		rigNumber++;
    		System.out.println("  Rig"+rigNumber+": "+rigName.get(i-1)+" ("+rigIp.get(i-1)+")");
    	}
    		System.out.println("  Using global minimum of: "+errorMinimum);
    		System.out.println("  Email alerts to: "+recipient1+" "+recipient2);
    	
    	//While there's no error in the MH/s, keep running
		while (rigError < 2) {
			//Get rig names from ethdistro JSON data
			JSONObject json = importJSON.readJsonFromUrl(webPage+"/?json=yes");
			//JSONObject json = importJSON.readJsonFromUrl(webPage);
		    Iterator<?> iterator = json.getJSONObject("rigs").keys();   
		    //Assign rignames and get megahash from ethdistro JSON data
		    rigCountLast = rigName.size();
		    rigName.clear();
		    rigMegaHash.clear();
		    rigIp.clear();
			ArrayList<String> AL = new ArrayList<String>();
		    while (iterator.hasNext()) {
		    	   String key = (String)iterator.next();
		    	   AL.add(key);
		    	} 
		    for (int i=initialAL.size(); i>0; i--) {
		    	if (json.getJSONObject("rigs").getJSONObject(AL.get(i-1)).getString("condition").equals("unreachable")) {
		    		json.getJSONObject("rigs").remove(AL.get(i-1));
		    	}else{
		    		rigName.add(AL.get(i-1));
		 		   	try {
						rigMegaHash.add(Double.parseDouble(json.getJSONObject("rigs").getJSONObject(AL.get(i-1)).getString("hash")));
					} catch (JSONException e) {
						rigMegaHash.add((double) 0);
					}
		 		   	rigIp.add(json.getJSONObject("rigs").getJSONObject(AL.get(i-1)).getString("ip"));
		    	}
		    }
		    rigCountCurrent = rigName.size();

	        //Get Current Date/Time
	        Date now = new Date();
	        System.out.println("\nMiner Status - "+locationName);
	        System.out.println("As of: "+now);
	        //Display data in console
	        rigNumber = 0;
	        for (int i=rigName.size(); i>0 ; i--) {
	        	rigNumber++;
	    		System.out.println("Rig"+rigNumber+": "+rigName.get(i-1)+" ("+rigIp.get(i-1)+")....."+rigMegaHash.get(i-1)+" MH/s");
	    	}
	        System.out.println("Using global minimum of: "+errorMinimum);
	        //Error handling
	        rigNumber = 0;
	        for (int i=rigName.size(); i>0 ; i--) {
		        if (rigMegaHash.get(i-1) <= errorMinimum) {
		        	rigNumber++;
		        	rigError++;
		        	System.out.println("ALERT! Rig"+rigNumber+"("+rigName.get(i-1)+") has dropped below "+errorMinimum+" MH/s. It is currently: "+rigMegaHash.get(i-1)+" MH/s");
		        }
	        }
	        /*if (rigError == 1) {
	        	System.out.println("Detected error in one or more miners. Refreshing in 5 seconds to confirm...");
	        	TimeUnit.SECONDS.sleep(5);
	        }*/
	        //If a monitored rig becomes unreachable, increment rigError.
		    if (rigCountCurrent < rigCountLast) {
		    	rigError += rigCountLast - rigCountCurrent;
		    	System.out.println("ALERT! "+rigError+" Miner(s) is unreachable!");
		    }
		    
	        //When an error has been detected, send an alert email
	        if (rigError > 0){
	        	//Get rig names from ethdistro JSON data
				JSONObject finalJSON = importJSON.readJsonFromUrl(webPage+"/?json=yes");
				//JSONObject finalJSON = importJSON.readJsonFromUrl(webPage);
			    Iterator<?> finalIterator = finalJSON.getJSONObject("rigs").keys();   
			    //Assign rignames and get megahash from ethdistro JSON data
			    rigName.clear();
			    rigMegaHash.clear();
			    rigIp.clear();
				ArrayList<String> finalAL = new ArrayList<String>();
			    while (finalIterator.hasNext()) {
			    	   String key = (String)finalIterator.next();
			    	   finalAL.add(key);
			    	} 
			    for (int i=finalAL.size(); i>0; i--) {
			    		rigName.add(finalAL.get(i-1));
			 		   	try {
							rigMegaHash.add(Double.parseDouble(finalJSON.getJSONObject("rigs").getJSONObject(finalAL.get(i-1)).getString("hash")));
						} catch (JSONException e) {
							rigMegaHash.add((double) 0);
						}
			 		   	rigIp.add(finalJSON.getJSONObject("rigs").getJSONObject(finalAL.get(i-1)).getString("ip"));
			    }
	        	
	            String[] to = {recipient1, recipient2};
	            if (to[1].equals("")){
	            	to = new String[] {recipient1};
	            }
	            String subject = "ALERT! "+rigError+" Miner(s) at '"+locationName+"' Need Your Attention!";
	            String body = "";
	            body = "Miner Status:\n"+
	            	   "As of: "+now+"\n\n";
	            rigNumber = 0;
	            for (int i=rigName.size(); i>0 ; i--) {
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