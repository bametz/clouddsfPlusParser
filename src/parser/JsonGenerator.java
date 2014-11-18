package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cloudDSF.CloudDSF;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Main class to trigger read of excel file and json generation
 * 
 * @author Metz
 *
 */
public class JsonGenerator {
	private static Gson gson = new GsonBuilder().setPrettyPrinting()
			.serializeNulls().create();

	public static void main(String[] args) throws IOException {
		String filePath = "Matrix.xlsx";
		ExcelParser parser = new ExcelParser();
		XSSFWorkbook workbook = null;
		// Create Workbook instance holding reference to .xlsx file
		InputStream in = JsonGenerator.class.getClassLoader()
				.getResourceAsStream(filePath);
		try {
			workbook = new XSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		CloudDSF cdsf = parser.readExcel(workbook);
		//cdsf.printCloudDSF();
		writeDecisionTree(cdsf);
		// writeDecisionTreeWithoutOutcomes(cdsf);
	}
/**
 * Writes json with all outcomes and links list.
 * @param cdsf
 * @throws IOException
 */
	private static void writeDecisionTree(CloudDSF cdsf) throws IOException {
		cdsf.setId(-1);
		cdsf.setType("root");
		cdsf.setLabel("Decision Points");
		JsonObject cloudDSFJson = new JsonObject();
		cloudDSFJson.add("decisionTree", gson.toJsonTree(cdsf));
		cloudDSFJson.add("linksArray",
				gson.toJsonTree(cdsf.getInfluencingRelations()));
		cloudDSFJson.add("linksArrayOutcomes", gson.toJsonTree(cdsf.getInfluencingOutcomes()));
		String json = gson.toJson(cloudDSFJson);
		File jsonFile = new File("outputTree.json");
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
		bw.write(json);
		bw.flush();
		bw.close();
	}
}
