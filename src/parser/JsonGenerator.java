package parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cloudDSF.CloudDSF;
import cloudDSF.Decision;
import cloudDSF.DecisionPoint;
import cloudDSF.Relation;
import cloudDSF.TaskTree;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		// factory.objectNode().
		mapper.setVisibilityChecker(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE));

		JsonNode rootNode = mapper.createObjectNode(); // will be of type
														// ObjectNode
		((ObjectNode) rootNode).putPOJO("deicisonTree", cdsf);
		// Array with all relations
		((ObjectNode) rootNode).putPOJO("linksArray", influencingRelations);

		// Tasks
		TaskTree taskTree = new TaskTree();
		// get Tasks
		taskTree.setTasks(cdsf.getTasks());
		//((ObjectNode) rootNode).putPOJO("taskTree", taskTree);
		
		cdsf.setId(-3);
		cdsf.setType("rootTestDec");
		cdsf.setLabel("");
		for (DecisionPoint dp : cdsf.getDecisionPoints()) {
			for (Decision d : dp.getDecisions()) {
				d.setOutcomes(null);
			}
		}
		((ObjectNode) rootNode).putPOJO("decisionWithoutOutcomes", cdsf);
		mapper.writeValue(new File("elaboratedDSF.json"), rootNode);
	}
}
