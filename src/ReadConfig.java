import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ReadConfig {

	static String[] returnFileData() throws FileNotFoundException, UnsupportedEncodingException {
		
		String webPage = "";
		String refreshMin = "";
		String errorMinimum = "";
		String recipient1 = "";
		String recipient2 = "";
		String locationName = "";
		String emailSender = "";
		String emailSenderPass = "";
		
		File f = new File("config.txt");
		if(f.exists() && !f.isDirectory()) { 
			// The name of the file to open.
	        String fileName = "config.txt";

	        // This will reference one line at a time
	        String line = null;

	        try {
	            // FileReader reads text files in the default encoding.
	            FileReader fileReader = 
	                new FileReader(fileName);

	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader = 
	                new BufferedReader(fileReader);

	            while((line = bufferedReader.readLine()) != null) {
	                //System.out.println(line);
	            	if (line.contains("#")) {
	            		//ignore these lines
	            	} else {
	            		if (line.contains("webPage="))
		                	webPage = line.substring(line.lastIndexOf("=")+1);
		                if (line.contains("refreshMin="))
		                	refreshMin = line.substring(line.lastIndexOf("=")+1);
		                if (line.contains("recipient1="))
		                	recipient1 = line.substring(line.lastIndexOf("=")+1);
		                if (line.contains("recipient2="))
		                	recipient2 = line.substring(line.lastIndexOf("=")+1);
		                if (line.contains("errorMinimum="))
		                	errorMinimum = line.substring(line.lastIndexOf("=")+1);
		                if (line.contains("locationName="))
		                	locationName = line.substring(line.lastIndexOf("=")+1);
		                if (line.contains("emailSender="))
		                	emailSender = line.substring(line.lastIndexOf("=")+1);
		                if (line.contains("emailSenderPass="))
		                	emailSenderPass = line.substring(line.lastIndexOf("=")+1);
	            	}
	            }

	            // Always close files.
	            bufferedReader.close();         
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                fileName + "'");                
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file '" 
	                + fileName + "'");                  
	            // Or we could just do this: 
	            // ex.printStackTrace();
	        }
	        String[] data = {webPage,
	        				 refreshMin,
	        				 recipient1,
	        				 recipient2,
	        				 emailSender,
	        				 emailSenderPass,
	        				 errorMinimum,
	        				 locationName};
	        return data;
		}else{
			try {
				String content = "# Use '#' for commented lines"+System.lineSeparator()+
								System.lineSeparator()+
								"# Miner location name (ex. 'Home'), (this will display in the subject of the alert email)."+System.lineSeparator()+
								"locationName=Home"+System.lineSeparator()+
								System.lineSeparator()+
								"# URL to your ethdistro webpage (ex. http://1b9f67.ethosdistro.com/)."+System.lineSeparator()+
								"webPage="+System.lineSeparator()+
								System.lineSeparator()+
								"# Set value to how many x minutes to refresh megahash data."+System.lineSeparator()+
								"refreshMin=30"+System.lineSeparator()+
								System.lineSeparator()+
								"# First recipient email address (ex. bobjoe@somedomain.com)."+System.lineSeparator()+
								"# If no second email, leave blank."+System.lineSeparator()+
								"recipient1="+System.lineSeparator()+
								"recipient2="+System.lineSeparator()+
								System.lineSeparator()+
								"# Gmail address where emails are sent from. (Use a current or create one specifically for this application)"+System.lineSeparator()+
								"# (ex. bobjoe@gmail.com)"+System.lineSeparator()+
								"emailSender="+System.lineSeparator()+
								"emailSenderPass="+System.lineSeparator()+
								System.lineSeparator()+
								"# If MH/s goes below x value, throw error and send email."+System.lineSeparator()+
								"# Do not enter decimals, and no negative values."+System.lineSeparator()+
								"errorMinimum=0";
								

				File file = new File("config.txt");

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();

				System.out.println("Config not found. If this is the first time running, this is normal."
								+ "\nCreating config.txt in root..."
								+ "\nClose this application and spcify properties in config, then run again.");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
