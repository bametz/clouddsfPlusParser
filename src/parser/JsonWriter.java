package parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cloudDSF.CloudDSF;
import cloudDSF.TaskTree;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
		writeLegacyJson(workbook);
		// writeForceLayout(workbook);
		// writeInfluencingLayout(workbook);
	}

	private static void writeLegacyJson(XSSFWorkbook workbook)
			throws JsonGenerationException, JsonMappingException, IOException {
		CloudDSFParser parser = new CloudDSFParser(workbook);
		CloudDSF cdsf = parser.readExcel();

		TaskTree taskTree = new TaskTree();
		taskTree.setTasks(cdsf.getTasks());

		// Jackson objectmapper and settings
		ObjectMapper mapper = new ObjectMapper();
		// Pretty Print
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setVisibilityChecker(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE));
		mapper.setSerializationInclusion(Include.NON_NULL);

		cdsf.setInfluencingRelations();
		// List<Relation> influencingRelations = cdsf.getInfluencingRelations();

		JsonNode rootNode = mapper.createObjectNode();

		((ObjectNode) rootNode).putPOJO("decisionTree", cdsf);
		((ObjectNode) rootNode).putPOJO("taskTree", taskTree);
		((ObjectNode) rootNode).putPOJO("linksArray",
				cdsf.getInfluencingRelations());
		File f = new File("elaboratedDSF.json");
		mapper.writeValue(f, rootNode);
	}

	// private static void writeForceLayout(XSSFWorkbook workbook)
	// throws JsonGenerationException, JsonMappingException, IOException {
	// CloudDSFPlusParser ForceLayoutParser = new CloudDSFPlusParser(workbook);
	// CloudDSFPlus cdsf = ForceLayoutParser.readExcel();
	//
	// // for (DecisionPoint dp : cdsf.getDecisionPoints()) {
	// // dp.setSize(60);
	// // for (Decision d : dp.getDecisions()) {
	// // d.setSize(30);
	// // d.setOutcomes(null);
	// // }
	// // }
	// cdsf.setId(-1);
	// cdsf.setType("root");
	// // cdsf.setSize(0);
	// cdsf.setLabel("Decision Points");
	//
	// // Relations = relatios of tasks AND Decisions
	// List<Relation> influencingRelations = cdsf.getInfluencingRelations();
	// influencingRelations.clear();
	// influencingRelations.addAll(cdsf.getInfluencingDecisions());
	// // sort by id
	// cdsf.sortInfluencingRelations();
	// // new CloudDSF Object without Outcomes to avoid annotations or
	// // appending on file
	// //
	// // cloudDSFwithoutOutcomes.setId(-3);
	// // cloudDSFwithoutOutcomes.setType("rootTestDec");
	// // cloudDSFwithoutOutcomes.setLabel("");
	//
	// // Jackson objectmapper and settings
	// ObjectMapper mapper = new ObjectMapper();
	// mapper.enable(SerializationFeature.INDENT_OUTPUT);
	// mapper.setVisibilityChecker(mapper.getSerializationConfig()
	// .getDefaultVisibilityChecker()
	// .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
	// .withGetterVisibility(JsonAutoDetect.Visibility.NONE));
	//
	// JsonNode rootNode = mapper.createObjectNode();
	// // weight null setzen dass es nicht exportiert wird
	// ((ObjectNode) rootNode).putPOJO("decisionTree", cdsf);
	// ((ObjectNode) rootNode).putPOJO("links", influencingRelations);
	// File f = new File("forceLayout.json");
	// mapper.writeValue(f, rootNode);
	// }
	//
	// private static void writeInfluencingLayout(XSSFWorkbook workbook)
	// throws JsonGenerationException, JsonMappingException, IOException {
	// CloudDSFPlusParser parser = new CloudDSFPlusParser(workbook);
	// CloudDSFPlus cdsf = parser.readExcel();
	//
	// List<Decision> decisions = new ArrayList<Decision>();
	// for (DecisionPoint dp : cdsf.getDecisionPoints()) {
	// for (Decision d : dp.getDecisions()) {
	// //d.setSize(30);
	// d.setOutcomes(null);
	// decisions.add(d);
	// }
	//
	// }
	//
	// List<DecisionRelation> requiringRelations = new
	// ArrayList<DecisionRelation>();
	// // Relations = relatios of tasks AND Decisions
	//
	// for (Relation relation : cdsf.getInfluencingDecisions()) {
	// if (relation.getLabel().equals("Requiring")) {
	// // System.out.println("found one");
	// DecisionRelation dr = (DecisionRelation) relation;
	// requiringRelations.add(dr);
	// }
	// }
	// // for (DecisionRelation relation : requiringRelations) {
	// // for (Decision d : decisions) {
	// //
	// // if (relation.getSource()== d.getId()){
	// // relation.setSource(decisions.indexOf(d));
	// // }
	// // if (relation.getTarget()==d.getId()){
	// // relation.setTarget(decisions.indexOf(d));
	// //
	// // }
	// // }
	// // }
	// // Jackson objectmapper and settings
	// ObjectMapper mapper = new ObjectMapper();
	// mapper.enable(SerializationFeature.INDENT_OUTPUT);
	// // mapper.setVisibilityChecker(mapper.getSerializationConfig()
	// // .getDefaultVisibilityChecker()
	// // .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
	// // .withGetterVisibility(JsonAutoDetect.Visibility.NONE));
	//
	// JsonNode rootNode = mapper.createObjectNode();
	// // weight null setzen dass es nicht exportiert wird
	// ((ObjectNode) rootNode).putPOJO("nodes", decisions);
	// ((ObjectNode) rootNode)
	// .putPOJO("links", cdsf.getInfluencingDecisions());
	// File f = new File("influencingLayout.json");
	// mapper.writeValue(f, rootNode);
	// }

}
