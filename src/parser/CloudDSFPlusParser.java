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
import cloudDSF.Decision;
import cloudDSF.DecisionPoint;
import cloudDSF.Outcome;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Iterator;

/**
 * Reads knowledge base (excel file) and collects all data necessary for the CloudDSFPlus.
 * 
 * @author Metz
 *
 */
public class CloudDSFPlusParser {

  private final CloudDSF cdsf;
  private final XSSFWorkbook workbook;
  // row numbers
  private int dpCol = 0;
  private int decCol = 1;
  private int outCol = 2;

  private int dpDescCol = 3;
  private int decDescCol = 4;
  private int outDescCol = 5;
  private int dpClassCol = 6;
  private int decClassCol = 7;

  // private int dpAddInfoCol = 8;
  // private int decAddInfoCol = 9;
  // private int outAddInfoCol = 10;
  /**
   * Default constructor setting workbook and new cloudDSFPlus object.
   * 
   * @param workbook
   */
  public CloudDSFPlusParser(XSSFWorkbook workbook) {
    // create new CloudDSF object with information for the CloudDSFPlus
    this.cdsf = new CloudDSF(0, "root", "CloudDSF+");
    cdsf.setAbbrev("CDSF+");
    cdsf.setDescription("CDSF+ knowledge base containing decision points, decisions and their outcomes.");
    this.workbook = workbook;
  }

  /**
   * Retrieves the knowledge base for the CloudDSFPlus from the sheet and the relations.
   *
   * @return
   */
  public CloudDSF readExcel() {
    // Get desired sheet from the workbook
    XSSFSheet sheet = workbook.getSheet("Knowledge Base");

    // setup variable
    String decisionName = "";
    String decisionPointName = "";

    DecisionPoint decisionPoint;
    Decision decision;
    Outcome outcome;

    int decisionPointId = 0;
    int decisionId = 0;
    int outcomeId = 0;

    // iterate over all rows skipping headline
    for (int j = 1; j <= sheet.getLastRowNum(); j++) {
      // row 2 gets selected
      Row row = sheet.getRow(j);
      // select cell A
      Cell cell = row.getCell(dpCol);
      // if not empty than new decision Point
      if (cell.getStringCellValue().equals("") == false) {
        decisionPointId++;
        decisionId = decisionPointId * 100 + 1;
        outcomeId = decisionId * 100 + 1;
        decisionPointName = cell.getStringCellValue();
        // create new DecisionPoint
        decisionPoint = generateDecisionPoint(cell, decisionPointId, row);
        // create new Decision
        Cell decisionCell = row.getCell(decCol);
        decisionName = decisionCell.getStringCellValue();
        decision = generateDecision(decisionCell, decisionId, decisionPointId, row);
        // create new outcome
        Cell outcomeCell = row.getCell(outCol);
        outcome = generateOutcome(outcomeCell, decisionId, decisionPointId, outcomeId, row);
        // add outcome to decision
        decision.addOutcome(outcome);
        // add decision to decisionPoint
        decisionPoint.addDecision(decision);
        // add decisionPoint to cloudDSFPlus
        cdsf.addDecisionPoint(decisionPoint);
      } else {
        // Select Cell B
        Cell decisionCell = row.getCell(decCol);
        // if text than new decision
        if (decisionCell.getStringCellValue().equals("") == false) {
          decisionId++;
          outcomeId = decisionId * 100 + 1;
          // create new decision
          decisionName = decisionCell.getStringCellValue();
          decision = generateDecision(decisionCell, decisionId, decisionPointId, row);
          // create new outcome
          Cell outcomeCell = row.getCell(outCol);
          outcome = generateOutcome(outcomeCell, decisionId, decisionPointId, outcomeId, row);
          // add outcome to decision
          decision.addOutcome(outcome);
          // add decision to current decision point
          cdsf.getDecisionPoint(decisionPointName).addDecision(decision);
        } else {
          // if no text in dp or d than new outcome
          outcomeId++;
          // create new outcome
          Cell outcomeCell = row.getCell(outCol);
          outcome = generateOutcome(outcomeCell, decisionId, decisionPointId, outcomeId, row);
          // add outcome to current decision in current decision point
          cdsf.getDecisionPoint(decisionPointName).getDecision(decisionName).addOutcome(outcome);
        }
      }
    }
    // retrive relations
    setInfluencingRelations();
    setRequiringRelations();
    setInfluencingOutcomes();
    // sorting
    cdsf.sortEntities();
    cdsf.sortLists();
    return cdsf;

  }

  /**
   * Generates a new outcome.
   * 
   * @param cell current cell in the excel file
   * @param decisionId id of the decision
   * @param decisionPointId id of the decision point
   * @param outcomeId id of the outcome
   * @param row current row in the excel file
   * @return
   */
  private Outcome generateOutcome(Cell cell, int decisionId, int decisionPointId, int outcomeId,
      Row row) {
    String label = cell.getStringCellValue();
    String description = row.getCell(outDescCol).getStringCellValue();
    String abbrev = cell.getCellComment().getString().getString();
    Outcome out =
        new Outcome(label, outcomeId, decisionPointId, decisionId, description, null, abbrev);
    return out;
  }

  /**
   * Generates a new decision.
   * 
   * @param cell current cell in the excel file
   * @param decisionId id of the decision
   * @param decisionPointId id of the decision point
   * @param row current row in the excel file
   * @return
   */
  private Decision generateDecision(Cell cell, int decisionId, int decisionPointId, Row row) {
    String label = cell.getStringCellValue();
    String classification = row.getCell(decClassCol).getStringCellValue();
    String description = row.getCell(decDescCol).getStringCellValue();
    String abbrev = cell.getCellComment().getString().getString();
    Decision dec =
        new Decision(label, decisionId, decisionPointId, decisionPointId, classification,
            description, null, abbrev);
    return dec;
  }

  /**
   * Generates a new decision point.
   * 
   * @param cell current cell in the excel file
   * @param decisionPointId id of the decision point
   * @param row current row in the excel file
   * @return
   */
  private DecisionPoint generateDecisionPoint(Cell cell, int decisionPointId, Row row) {
    String label = cell.getStringCellValue();
    String classification = row.getCell(dpClassCol).getStringCellValue();
    String description = row.getCell(dpDescCol).getStringCellValue();
    String abbrev = cell.getCellComment().getString().getString();
    DecisionPoint dp =
        new DecisionPoint(label, decisionPointId, decisionPointId, classification, description,
            null, abbrev);
    return dp;
  }

  /**
   * Retrieves influencing relations between decisions.
   * 
   * @return
   */
  private void setInfluencingRelations() {
    XSSFSheet sheet = workbook.getSheet("Decision Level");
    // Column B has name of start Decision
    int startDecisionColumn = 1;
    // Row 1 has names of endDecision
    Row endDecisionRow = sheet.getRow(1);
    // Iterate over all rows starting at 3
    Iterator<Row> rows = sheet.rowIterator();
    while (rows.hasNext()) {
      XSSFRow row = (XSSFRow) rows.next();
      // select cell C
      Iterator<Cell> cells = row.cellIterator();
      // Iterate of all cells in row
      while (cells.hasNext()) {
        XSSFCell cell = (XSSFCell) cells.next();
        String relationType = cell.getStringCellValue();
        if (relationType.equals("Influencing") || relationType.equals("Affecting")
            || relationType.equals("Binding")) {
          // if type of relationship matches predefined values get names of the two participating
          // decisions
          String startDecision = row.getCell(startDecisionColumn).getStringCellValue();
          String endDecision = endDecisionRow.getCell(cell.getColumnIndex()).getStringCellValue();
          // add decision relation to cloudDSFPlus
          cdsf.setDecisionRelation(startDecision, endDecision, relationType, null);
        }
      }
    }
  }

  /**
   * Retrieves requiring relations between decisions.
   * 
   * @return
   */
  private void setRequiringRelations() {
    XSSFSheet sheet = workbook.getSheet("Required Level");
    // Column B has name of start Decision
    int startDecisionColumn = 1;
    // Row 1 has names of endDecision
    Row endDecisionRow = sheet.getRow(1);
    // Iterate over all rows starting at 3
    Iterator<Row> rows = sheet.rowIterator();
    while (rows.hasNext()) {
      XSSFRow row = (XSSFRow) rows.next();
      Iterator<Cell> cells = row.cellIterator();
      while (cells.hasNext()) {
        XSSFCell cell = (XSSFCell) cells.next();
        String relationType = cell.getStringCellValue();
        if (relationType.equals("Requiring")) {
          // if requiring relationship is denoted get names of both decisions
          String startDecision = row.getCell(startDecisionColumn).getStringCellValue();
          String endDecision = endDecisionRow.getCell(cell.getColumnIndex()).getStringCellValue();
          // add requiring relation to cloudDSFPlus
          cdsf.setDecisionRelation(startDecision, endDecision, relationType, null);
        }
      }
    }
  }

  /**
   * Retrieves relations between outcomes.
   * 
   * @return
   */
  private void setInfluencingOutcomes() {
    XSSFSheet sheet = workbook.getSheet("Outcome Level");
    // Column B has name of start Decision
    int startOutcomeColumn = 1;
    // Row 1 has names of endDecision
    Row endOutcomeRow = sheet.getRow(0);
    // Iterate over all rows
    Iterator<Row> rows = sheet.rowIterator();
    while (rows.hasNext()) {
      XSSFRow row = (XSSFRow) rows.next();
      Iterator<Cell> cells = row.cellIterator();
      // Iterate over all cells
      while (cells.hasNext()) {
        XSSFCell cell = (XSSFCell) cells.next();
        String relationType = cell.getStringCellValue();
        if (relationType.equals("in") || relationType.equals("ex") || relationType.equals("a")
            || relationType.equals("eb") || relationType.equals("aff")) {
          // if relationship is denoted get names of both outcomes
          String startOutcome = row.getCell(startOutcomeColumn).getStringCellValue();
          String endOutcome = endOutcomeRow.getCell(cell.getColumnIndex()).getStringCellValue();
          // add new outcome relation to cloudDSFPlus
          cdsf.setOutcomeRelation(startOutcome, endOutcome, relationType, null, null);
        }
      }
    }
  }
}
