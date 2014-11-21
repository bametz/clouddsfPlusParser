package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cloudDSF.CloudDSFMaster;
import cloudDSF.Decision;
import cloudDSF.DecisionPoint;
import cloudDSF.Relation;
import cloudDSF.TaskTree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Main class to trigger read of excel file and json generation
 * 
 * @author Metz
 *
 */
public class JsonGenerator {
	
	public static void main(String[] args) throws IOException {
		String filePath = "Matrix.xlsx";
		XSSFWorkbook workbook = null;
		// Create Workbook instance holding reference to .xlsx file
		InputStream in = JsonGenerator.class.getClassLoader()
				.getResourceAsStream(filePath);
		try {
			workbook = new XSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ExcelParser parser = new ExcelParser(workbook);
		CloudDSFMaster cdsf = parser.readExcel();
		cdsf.printCloudDSF();
		writeLegacyJson(cdsf);
		// writeDecisionTreeWithoutOutcomes(cdsf);
	}

	/**
	 * Writes json with all outcomes and links list.
	 * 
	 * @param cdsf
	 * @throws IOException
	 */
	private static void writeLegacyJson(CloudDSFMaster cdsf) throws IOException {
		//gson
		Gson gson = new GsonBuilder().setPrettyPrinting()
				.serializeNulls().create();
		JsonObject cloudDSFLegacyJson = new JsonObject();
		JsonElement cloudDSFJson = gson.toJsonTree(cdsf);
		//Set attribute of CloudDSF Object to match legacyJson
		cdsf.setId(-1);
		cdsf.setType("root");
		cdsf.setLabel("Decision Points");
		
		//Array with all relations
		JsonElement linksArray = gson
				.toJsonTree(cdsf.getInfluencingRelations());
		//Relations = relatios of tasks AND Decisions
		List<Relation> influencingRelations = new ArrayList<Relation>();
		influencingRelations.addAll(cdsf.getInfluencingDecisions());
		influencingRelations.addAll(cdsf.getInfluencingTasks());
		//sort by id
		cdsf.sortInfluencingRelations();

		//Tasks
		TaskTree taskTree = new TaskTree();
		// get Tasks 
		taskTree.setTasks(cdsf.getTasks());
		JsonElement taskTreeJson = gson.toJsonTree(taskTree);

		for (DecisionPoint dp : cdsf.getDecisionPoints()) {
			for (Decision d : dp.getDecisions()) {
				d.setOutcomes(null);
			}
		}
		Gson gsonONull = new GsonBuilder().setPrettyPrinting().create();
		cdsf.setId(-3);
		cdsf.setType("rootTestDec");
		cdsf.setLabel("");
		JsonElement decisionTreeWithoutOutcomes = gsonONull.toJsonTree(cdsf);

		cloudDSFLegacyJson.add("decisionTreeWithoutOutcomes",
				decisionTreeWithoutOutcomes);
		cloudDSFLegacyJson.add("decisionTree", cloudDSFJson);
		cloudDSFLegacyJson.add("taskTree", taskTreeJson);
		cloudDSFLegacyJson.add("linksArray", linksArray);

		String json = gson.toJson(cloudDSFLegacyJson);
		File jsonFile = new File("elaboratedDSF.json");
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
		bw.write(json);
		bw.flush();
		bw.close();
	}

}
