package parser;

import cloudDSF.CloudDSF;
import cloudDSF.Decision;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Parser {

	public static void main(String[] args) {
		// iterating over values only

		String filePath = "Matrix.xlsx";
		ExcelParser parser = new ExcelParser();
		CloudDSF cdsf = parser.readExcel(filePath);
		//cdsf.printCloudDSF();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//Outcome oA = cdsf.getDecisionPoints().get("Distribute Application").getDecision("Select Application Layer").getOutcome("Presentation Layer");
		Decision d = cdsf.getDecisionPoints().get("Distribute Application").getDecision("Select Application Layer");
		String test = gson.toJson(d);
		System.out.println(test);
	}
}
