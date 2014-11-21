package parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Views;
import cloudDSF.CloudDSF;
import cloudDSF.Relation;
import cloudDSF.TaskTree;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Main class to trigger read of excel file and json generation
 * 
 * @author Metz
 *
 */
public class JsonWriter {

	public static void main(String[] args) throws IOException {
		String filePath = "Matrix.xlsx";
		XSSFWorkbook workbook = null;
		// Create Workbook instance holding reference to .xlsx file
		InputStream in = JsonWriter.class.getClassLoader().getResourceAsStream(
				filePath);
		try {
			workbook = new XSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ExcelParser parser = new ExcelParser(workbook);
		CloudDSF cdsf = parser.readExcel();
		cdsf.printCloudDSF();
		// writeLegacyJson(cdsf);
		writeJsonJackson(cdsf);
		// writeDecisionTreeWithoutOutcomes(cdsf);
	}

	private static void writeJsonJackson(CloudDSF cdsf)
			throws JsonGenerationException, JsonMappingException, IOException {
		cdsf.setId(-1);
		cdsf.setType("root");
		cdsf.setLabel("Decision Points");

		// Relations = relatios of tasks AND Decisions
		List<Relation> influencingRelations = cdsf.getInfluencingRelations();
		influencingRelations.clear();
		influencingRelations.addAll(cdsf.getInfluencingDecisions());
		influencingRelations.addAll(cdsf.getInfluencingTasks());
		// sort by id
		cdsf.sortInfluencingRelations();
		// new CloudDSF Object without Outcomes to avoid annotations or
		// appending on file
		//
		// cloudDSFwithoutOutcomes.setId(-3);
		// cloudDSFwithoutOutcomes.setType("rootTestDec");
		// cloudDSFwithoutOutcomes.setLabel("");

		TaskTree taskTree = new TaskTree();
		taskTree.setTasks(cdsf.getTasks());

		// Jackson objectmapper and settings
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setVisibilityChecker(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE));

		JsonNode rootNode = mapper.createObjectNode();
		ObjectWriter w = mapper.writerWithView(Views.NoOutcomes.class);

//		((ObjectNode) rootNode).putPOJO("decisionTreeWithoutOutcomes",
//				w.writ(cdsf));

		((ObjectNode) rootNode).putPOJO("decisionTree", cdsf);
		((ObjectNode) rootNode).putPOJO("taskTree", taskTree);
		((ObjectNode) rootNode).putPOJO("linksArray", influencingRelations);
		File f = new File("elaboratedDSF.json");
		mapper.writeValue(f, rootNode);
	}
}
