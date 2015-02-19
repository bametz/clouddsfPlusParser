/*
 * Copyright 2015 Balduin Metz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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
 * Triggers parsing and serialization of clouddsf knowledge base (excel file) into two json files.
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
    // Create Workbook instance holding reference to .xlsx file located at resources folder
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
   * Generates json file for the CloudDSF avoiding any unnecessary attribute serialization.
   * 
   * @param workbook
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  private static void writeCloudDSFJson(XSSFWorkbook workbook) throws JsonGenerationException,
      JsonMappingException, IOException {
    // Instantiate parser to parse file for CloudDSF
    CloudDSFParser parser = new CloudDSFParser(workbook);
    // CloudDSF object representing all necessary information
    CloudDSF cdsf = parser.readExcel();
    // Helper Method to check content
    // cdsf.printCloudDSF();
    // Create task tree for legacy visualizations
    TaskTree taskTree = new TaskTree();
    taskTree.setTasks(cdsf.getTasks());

    // Jackson objectmapper and settings
    ObjectMapper mapper = new ObjectMapper();
    // Pretty Print
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    // If getter is found values will be serialized avoiding unnecessary attributes
    mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.DEFAULT)
        .withGetterVisibility(JsonAutoDetect.Visibility.DEFAULT));
    // Ignore fields with null values to avoid serialization of empty lists
    mapper.setSerializationInclusion(Include.NON_NULL);
    // Write all relations into one list to conform to legacy implementation
    cdsf.setInfluencingRelations();
    // create json root node and add json objects
    JsonNode rootNode = mapper.createObjectNode();
    ((ObjectNode) rootNode).putPOJO("decisionTree", cdsf);
    ((ObjectNode) rootNode).putPOJO("taskTree", taskTree);
    ((ObjectNode) rootNode).putPOJO("linksArray", cdsf.getInfluencingRelations());
    // serialize CloudDSF into file
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
    // instantiate parser for CloudDSFPlus and read excel
    CloudDSFPlusParser cloudDSFPlusParser = new CloudDSFPlusParser(workbook);
    CloudDSF cdsf = cloudDSFPlusParser.readExcel();
    // check the internal consistency and if successfull serialize data
    if (cdsf.checkSanity()) {
      // Helper Method
      // cdsf.printCloudDSF();
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
      // Serialize CloudDSFPlus into json file
      File file = new File("cloudDSFPlus.json");
      mapper.writeValue(file, rootNode);
      System.out.println("Knowledge Base has been successfully verified and exported");
    } else {
      // knowledge base is not valid abort serialization
      System.out.println("The knowledge base is not valid");
    }
  }
}
