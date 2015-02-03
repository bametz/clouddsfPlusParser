package parser;

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

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

/**
 * Reads clouddsf knowledge base (excel file) and generates two json files.
 * 
 * @author Metz
 *
 */
public class JsonWriter {
  /**
   * Retrieves knowledge base file and starts parsing as well as serialization.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    String filePath = "KnowledgeBase.xlsx";
    XSSFWorkbook workbook = null;
    // Create Workbook instance holding reference to .xlsx file
    InputStream in = JsonWriter.class.getClassLoader().getResourceAsStream(filePath);
    try {
      workbook = new XSSFWorkbook(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
    writeCloudDSFJson(workbook);
    writeCloudDSFPlusJson(workbook);
    System.out.println("Finished");
  }

  /**
   * Generates json file for the CloudDSF. Avoids any unnecessary attribute serialization
   * 
   * @param workbook
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  private static void writeCloudDSFJson(XSSFWorkbook workbook) throws JsonGenerationException,
      JsonMappingException, IOException {
    CloudDSFParser parser = new CloudDSFParser(workbook);
    CloudDSF cdsf = parser.readExcel();
    // cdsf.printCloudDSF();
    TaskTree taskTree = new TaskTree();
    taskTree.setTasks(cdsf.getTasks());

    // Jackson objectmapper and settings
    ObjectMapper mapper = new ObjectMapper();
    // Pretty Print
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    // If getter is found value will be serialized
    // Avoids unnecessary attributes
    mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.DEFAULT)
        .withGetterVisibility(JsonAutoDetect.Visibility.DEFAULT));
    // Ignore fields with null values
    mapper.setSerializationInclusion(Include.NON_NULL);

    cdsf.setInfluencingRelations();

    JsonNode rootNode = mapper.createObjectNode();
    ((ObjectNode) rootNode).putPOJO("decisionTree", cdsf);
    ((ObjectNode) rootNode).putPOJO("taskTree", taskTree);
    ((ObjectNode) rootNode).putPOJO("linksArray", cdsf.getInfluencingRelations());

    File file = new File("cloudDSF.json");
    mapper.writeValue(file, rootNode);
  }

  /**
   * Creates json file for the cloudDSFPlus with all new attributes.
   * 
   * @param workbook
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  private static void writeCloudDSFPlusJson(XSSFWorkbook workbook) throws JsonGenerationException,
      JsonMappingException, IOException {
    CloudDSFPlusParser cloudDSFPlusParser = new CloudDSFPlusParser(workbook);
    CloudDSF cdsf = cloudDSFPlusParser.readExcel();
    if (cdsf.checkSanity()) {
      cdsf.printCloudDSF();
      // Jackson objectmapper and settings
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      // Ignore missing getters to serialize all values
      mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
          .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
          .withGetterVisibility(JsonAutoDetect.Visibility.NONE));
      mapper.setSerializationInclusion(Include.NON_NULL);
      // create json structure
      JsonNode rootNode = mapper.createObjectNode();
      ((ObjectNode) rootNode).putPOJO("cdsfPlus", cdsf);
      ((ObjectNode) rootNode).putPOJO("links", cdsf.getInfluencingDecisions());
      ((ObjectNode) rootNode).putPOJO("outcomeLinks", cdsf.getInfluencingOutcomes());

      File file = new File("cloudDSFPlus.json");
      mapper.writeValue(file, rootNode);
      System.out.println("Knowledge Base has been successfully verified and exported");
    } else {
      System.out.println("The knowledge base is not valid");
    }
  }
}
