package com.PIIProblem.code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONObject;

public class CSVParserToJson {

	static String Fname;
	static String Lname;
	static String Name;
	static String Phone;
	static String ZipCode;
	static String Color;
	static LinkedHashMap<String, Object> PiiMap = new LinkedHashMap<String, Object>();
	static JSONArray ja = new JSONArray();
	static JSONObject mainObj = new JSONObject();
	static List<Integer> errorsList = new ArrayList<Integer>();
	static FileWriter fileErr = null;
	static String inputFile = null;
	static String outputFile = null;
	static Boolean checkPiiIsValid=true;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 2) {

			inputFile = args[0];
			outputFile = args[1];
			readCSVFile(inputFile);

		} else {
			System.out.println("Pass the arguements <Solution> <inputFile><outputFile>");
			System.exit(0);

		}
	}
/**
 * Read the CSV File and calls the methods to check the validation,and 
 * methods to store the valid fields in json array.
 * and Parse the fields and store the results in the JSON.
 * @param fileName
 */
public static void readCSVFile(String fileName) {
		String line = "";
		String cvsSplitBy = ",";	
		BufferedReader br;
		int lines = 0;
		try {
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {

				lines = lines + 1;
				// use comma as separator
				String[] PII = line.split(cvsSplitBy);
				
				// Length should be 4 or 5 PII for it to be valid..
				// 5 PII means that either FirstName or LastName can come first.
				// and following depends on the second Field.
			if (PII.length == 4 || PII.length == 5) {

			// parse Do validation Checks and store the PII in Map.
				parseValidateAndStorePII(PII);
              //Add the PII entries to JSON File.
			if (!PiiMap.isEmpty()) {

				createJSONFromMap(PiiMap, true, outputFile);
					}
			else{
				errorsList.add(lines);
			}

				}
				// if the number of PII is not 4 or 5 it is inValid.
				else {					
				errorsList.add(lines);
				}
				// validatePii(PiiMap);

			}

			//addErrorsToJsonArray(errorsList);
			createJSONFromMap(PiiMap, false, outputFile);
			br.close();
			// validatePii(PiiMap);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
 /**
 * Parse the PII fields and does a Validation check.
 * If Validation Passes it will add to the 
 * Map.
 * @param PII
 */

public static void parseValidateAndStorePII(String[] PII) {
          checkPiiIsValid=true;
		// checks..
		if (PII.length == 4) {

			// shoud Handle the lines like
			// Jamie Stevenson, yellow, 84880, 028 164 6574
			// validation checks for Name.
			Boolean isVaildFnameLname = validateFnameLnameColor(PII[0].trim());

			if (isVaildFnameLname) {

				Fname = PII[0].split(" ")[0];
				Lname = PII[0].split(" ")[1];
				//System.out.println(" The Fname " + Fname);
				//System.out.println(" The Lname " + Lname);
				PiiMap.put("Fname", Fname);
				PiiMap.put("Lname", Lname);
			}
			
			// validation check for color.
			Boolean isVaildColor = validateFnameLnameColor(PII[1].trim());

			if (isVaildColor) {

				Color = PII[1].trim();
			
				PiiMap.put("color", Color);
			}
			// Validation check for Zip Code.
			Boolean isVaildZip = validateZipCode(PII[2].trim());
			if (isVaildZip) {

				ZipCode = PII[2].trim();
			
				PiiMap.put("zipcode", ZipCode);

			}
			// Validation check for PhoneFormat1..
			Boolean isVaildPhone = validatePhoneNumberFormat1(PII[3].trim());

			if (isVaildPhone) {
				Phone = PII[3].trim();
				
				PiiMap.put("phone", Phone);
			}
			
         if(isVaildFnameLname==false||isVaildPhone==false||
        		 isVaildZip==false||isVaildColor==false){
        	 
        	      checkPiiIsValid=false;
        	      PiiMap.clear();
        	      
             }
		}
		// If it has 5 PII attributes..
		else if (PII.length == 5) {

			// Sam T., Washington, 85360, 353 791 6380, purple
			// check if 3rd field is Zip code.Depending on that the
			// firstfield will be Lname or Fname.

			if ((PII[2].trim().length() == 5) && (validateZipCode(PII[2].trim()))) {

				Boolean isVaildLname = validateFnameLnameColor(PII[0].trim());
				if (isVaildLname) {
					Lname = PII[0].trim();

					PiiMap.put("lastname", Lname);
				}

				Boolean isVaildFname = validateFnameLnameColor(PII[1].trim());
				if (isVaildFname) {
					Fname = PII[1].trim();
					PiiMap.put("firstname", Fname);
				}

				Boolean isVaildZip = validateZipCode(PII[2].trim());
				if (isVaildZip) {
					ZipCode = PII[2].trim();
					PiiMap.put("zipcode", ZipCode);

				}

				Boolean isVaildPhone = validatePhoneNumberFormat1(PII[3].trim());
				if (isVaildPhone) {

					Phone = PII[3].trim();
					
					PiiMap.put("phone", Phone);
				}

				Boolean isVaildColor = validateFnameLnameColor(PII[4].trim());
				if (isVaildColor) {
					Color = PII[4].trim();					
					PiiMap.put("color", Color);
				}
				if(isVaildLname==false||isVaildPhone==false||
		        		 isVaildZip==false||isVaildColor==false||isVaildFname==false){
		        	 
		        	      checkPiiIsValid=false;
		        	      PiiMap.clear();
		        	      
		             }
			}

			// we are assuming it is phone number the format will be
			// this...<FirstName>,<LastName>,Phone,Color,zipCode
			// Cameron, Kathy, (613)-658-9293, red, 143123121
			else {

				Boolean isVaildFname = validateFnameLnameColor(PII[0].trim());

				if (isVaildFname) {
					Fname = PII[0].trim();
					PiiMap.put("firstname", Fname);
				}

				Boolean isVaildLname = validateFnameLnameColor(PII[1].trim());
				if (isVaildLname) {
					Lname = PII[1].trim();
					PiiMap.put("lastname", Lname);
				}

				Boolean isVaildPhone = validatePhoneNumberFormat2(PII[2].trim());

				if (isVaildPhone) {
					Phone = PII[2].trim();
					PiiMap.put("phone", Phone);
				}

				Boolean isVaildColor = validateFnameLnameColor(PII[3]);
				if (isVaildColor) {
					Color = PII[3].trim();
					PiiMap.put("color", Color);
				}

				Boolean isVaildZip = validateZipCode(PII[4].trim());

				if (isVaildZip) {
					ZipCode = PII[4].trim();
					PiiMap.put("zipcode", ZipCode);
				}
				if(isVaildLname==false||isVaildPhone==false||
		        		 isVaildZip==false||isVaildColor==false||isVaildFname==false){
		        	 
		        	      checkPiiIsValid=false;
		        	      PiiMap.clear();
		        	      
		             }

			}

		}

	}

	/**
	 * This Method will take a hashMap and convert the hashMap to a JSON Object.
	 * Then add the JSON Object to a JSON Array.
	 * 
	 * @param hmap -Map containg all  PII fields needed to add.
	 * @param isValid -Boolean to true or false based on entries on map.
	 * @param FileName --outputJson File Name.
	 */

	public static void createJSONFromMap(LinkedHashMap<String, Object> hmap, Boolean isValid, String FileName) {

	if (isValid) {

			// Create JSON object from Java Map
			JSONObject tomJsonObj = new JSONObject(hmap);
			hmap.clear();
			ja.put(tomJsonObj);
			mainObj.put("entries", ja);

			try (FileWriter file = new FileWriter(FileName)) {

				file.write(mainObj.toString(2));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (isValid == false) {

			Integer[] errors = new Integer[errorsList.size()];

			for (int i = 0; i < errorsList.size(); i++) {
				//System.out.println(" The error line " + errorsList.get(i));
				errors[i] = errorsList.get(i);
			}

			JSONArray ja1 = new JSONArray(errors);
			mainObj.put("errors", ja1);
			try (FileWriter file = new FileWriter(FileName)) {
				file.write(mainObj.toString(2));
			} catch (JSONException | IOException e) {

				e.printStackTrace();
			}

		}

	}

	public static void addErrorsToJsonArray(List<Integer> errorLines) {
		Integer[] errors = new Integer[errorLines.size()];

		for (int i = 0; i < errorLines.size(); i++) {
			System.out.println(" The error line " + errorLines.get(i));
			errors[i] = errorLines.get(i);
		}
		JSONArray ja1 = new JSONArray(errors);
		mainObj.put("errors", ja1);

	}

	public static Boolean validateFnameLnameColor(String Name) {
		Name = Name.trim();
		
		if (Name.matches("[^0-9]+")) {
			return true;
		}
		return false;
	}

	public static Boolean validatePhoneNumberFormat1(String PhoneNumber) {
		PhoneNumber = PhoneNumber.trim();
		// String regExPhone="\\(?\\d{3}\\)?(-|\\s+)\\d{3}(-|\\s+)\\d{4}";

		String regExPhoneFormat = "\\d{3}\\s\\d{3}\\s\\d{4}";
		if (PhoneNumber.matches(regExPhoneFormat)) {
			return true;
			// System.out.println(" The valid Phone number " +PhoneNumber);
		}
		return false;
	}

	public static Boolean validatePhoneNumberFormat2(String PhoneNumber) {
		PhoneNumber = PhoneNumber.trim();
		// String regExPhone="\\(?\\d{3}\\)?(-|\\s+)\\d{3}(-|\\s+)\\d{4}";

		String regExPhoneFormat = "\\(\\d{3}\\)-\\d{3}-\\d{4}";
		if (PhoneNumber.matches(regExPhoneFormat)) {
			return true;
		}
		return false;
	}

	public static Boolean validateZipCode(String zipCode) {
		String zipCodeRegEx = "\\d{5}";

		if (zipCode.matches(zipCodeRegEx)) {
			return true;
		}

		return false;

	}

}
