package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cloudDSF.CloudDSF;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Parser {
	private static Gson gson = new GsonBuilder().setPrettyPrinting()
			.serializeNulls().create();

	public static void main(String[] args) throws IOException {
		String filePath = "/Matrix.xlsx";
		ExcelParser parser = new ExcelParser();
		CloudDSF cdsf = parser.readExcel(filePath);
		// cdsf.printCloudDSF();
		writeDecisionTree(cdsf);
		//writeDecisionTreeWithoutOutcomes(cdsf);
	}

	private static void writeDecisionTree(CloudDSF cdsf) throws IOException {
		cdsf.setId(-1);
		cdsf.setType("root");
		cdsf.setLabel("Decision Points");
		JsonObject jo = new JsonObject();
		jo.add("decisionTree", gson.toJsonTree(cdsf));
		String test = gson.toJson(jo);
		File jsonFile = new File("outputTree.json");
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
		bw.write(test);
		bw.flush();
		bw.close();
	}

	private static void writeDecisionTreeWithoutOutcomes(CloudDSF cdsf)
			throws IOException {
		cdsf.setId(-3);
		cdsf.setType("rootTestDec");
		cdsf.setLabel("");
		JsonObject jo = new JsonObject();
		jo.add("decisionTree", gson.toJsonTree(cdsf));
		String test = gson.toJson(jo);
		File jsonFile = new File("outputTreeWithoutOutcomes.json");
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
		bw.write(test);
		bw.flush();
		bw.close();
	}
}
