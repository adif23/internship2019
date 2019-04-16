package int03;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class INT03 {
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		computeHolidayRights();
	}

	@SuppressWarnings({ "rawtypes" })
	public static int computeHolidayRights() throws FileNotFoundException, IOException, ParseException {
		JSONParser jsonParser = new JSONParser();

		FileReader reader = new FileReader("input.json");
		Object obj = jsonParser.parse(reader);

		JSONObject jo = (JSONObject) obj;

		String employmentStartDateString = (String) ((HashMap) jo.get("employeeData")).get("employmentStartDate");
		String employmentEndDateString = (String) ((HashMap) jo.get("employeeData")).get("employmentEndDate");

		JSONArray suspensionPeriodList = (JSONArray) ((HashMap) jo.get("employeeData")).get("suspensionPeriodList");

		String[] startDateString = new String[suspensionPeriodList.size()];
		String[] endDateString = new String[suspensionPeriodList.size()];
		for (int i = 0; i < suspensionPeriodList.size(); i++) {
			startDateString[i] = (String) ((HashMap) suspensionPeriodList.get(i)).get("startDate");
			endDateString[i] = (String) ((HashMap) suspensionPeriodList.get(i)).get("endDate");
		}

		int[] employmentStartDate = stringToInt(employmentStartDateString); // converts the date from employmentStartDate to a int array(year,month,day)
		int[] employmentEndDate = stringToInt(employmentEndDateString); // converts the date from employmentEndDate to a int array(year,month,day)

		int[][] startDate = new int[suspensionPeriodList.size()][]; 
		int[][] endDate = new int[suspensionPeriodList.size()][];
		for (int i = 0; i < suspensionPeriodList.size(); i++) {
			startDate[i] = stringToInt(startDateString[i]); // converts the date from startDate to a int array(year,month,day)
			endDate[i] = stringToInt(endDateString[i]); // converts the date from endDate to a int array(year,month,day)
		}
		int startYear = employmentStartDate[0];
		int endYear = employmentEndDate[0];
		int[] daysThisYear = new int[(endYear - startYear) + 1]; // the size will be the duration of the contract
		int[] year = new int[(endYear - startYear) + 1]; // the size will be the duration of the contract
		int holidayDays = 20; //initial days is set at 20
		int k = 0; 
		do {
			daysThisYear[k] = holidayDays;
			year[k] = startYear;

			int suspendedMonths = 0;

			for (int i = 0; i < suspensionPeriodList.size(); i++) {
				if (startYear == startDate[i][0]) {
					suspendedMonths = endDate[i][1] - startDate[i][1];
					daysThisYear[k] = daysThisYear[k] - suspendedMonths * 2;
				}

			}
			if (daysThisYear[k] < 0)
				daysThisYear[k] = 0;
			holidayDays++;
			if (startYear == endYear) {
				break;
			} else {
				startYear++;
				k++;
			}
		} while (true);
		writeJSON("no error", daysThisYear, year); //this will write the json file

		return 0;
	}

	public static void writeJSON(String error, int[] daysThisYear, int[] year) { // this method writes the data into the json file
		File file = new File("output.json");
		FileWriter fr = null;
		try {
			fr = new FileWriter(file);
			fr.write("{\n");
			fr.write(" \"output\": {\n");
			fr.write("    \"errorMessage\" : \"" + error + "\",\n");
			fr.write("    \"holidayRightsPerYearList\":[\n");
			if (daysThisYear != null) {
				for (int i = 0; i < daysThisYear.length - 1; i++) {
					fr.write("      {\"year\": \"" + year[i] + "\", \"holidayDays\": \"" + daysThisYear[i] + "\"},\n");
					if (i == daysThisYear.length - 2)
						fr.write("      {\"year\": \"" + year[i + 1] + "\", \"holidayDays\": \"" + daysThisYear[i + 1]
								+ "\"}\n");
				}
			}
			fr.write("    ]\n");
			fr.write(" }\n");
			fr.write("}\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isValidDate(int[] numbers) { // verifies if the year, month and day are correct.
		if (numbers[0] < 0 || numbers[0] > 10000)
			return false;
		if (numbers[1] <= 0 || numbers[1] > 12)
			return false;
		if (numbers[2] <= 0 || numbers[2] > 31)
			return false;
		return true;
	}

	public static int[] stringToInt(String str) { // converts the date string to an int array
		String[] numbersString = (str.split("-"));
		int[] date = extractNumbersFromString(numbersString);
		return date;
	}

	public static int[] extractNumbersFromString(String[] dateString) { // returns the year month and day in an int array
		int[] numbers = new int[dateString.length];
		for (int i = 0; i < dateString.length; i++) {
			try {
				numbers[i] = Integer.valueOf(dateString[i]);
			} catch (NumberFormatException nfe) {
				writeJSON("invalid date entered", null, null);
				System.out.println("Invalid date entered!");
				System.exit(0);
			}
		}
		return numbers;
	}

}
