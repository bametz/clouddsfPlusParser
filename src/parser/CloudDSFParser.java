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
import cloudDSF.Task;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Iterator;

/**
 * Reads knowledge base (excel file) and parses all data necessary for the CloudDSF.
 * 
 * @author Metz
 *
 */
public class CloudDSFParser {
  private final CloudDSF cdsf;
  private final XSSFWorkbook workbook;
  // row numbers of excel sheet
  private int dpCol = 0;
  private int decCol = 1;
  private int outCol = 2;
  private int dpClassCol = 6;
  private int decClassCol = 7;

  /**
   * Default constructor setting workbook and new cloudDSF object.
   * 
   * @param workbook
   */
  public CloudDSFParser(XSSFWorkbook workbook) {
    // new cloudDSF object with basic info
    this.cdsf = new CloudDSF(-1, "root", "CloudDSF");
    this.workbook = workbook;
  }

  /**
   * Retrieves the knowledge base of the CloudDSF from the knowledge base sheet and the relations.
   * 
   * @return cdsf object
   */
  public CloudDSF readExcel() {
    // Get desired sheet from the workbook
    XSSFSheet sheet = workbook.getSheet("Knowledge Base");

    // setup variables
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
      Row row = sheet.getRow(j);
      // select cell A
      Cell cell = row.getCell(dpCol);
      // if not empty than new decision Point
      if (cell.getStringCellValue().equals("") == false) {
        // calculate Ids
        decisionPointId++;
        decisionId = decisionPointId * 100 + 1;
        outcomeId = decisionId * 100 + 1;

        // create new DecisionPoint
        decisionPointName = cell.getStringCellValue();
        decisionPoint =
            new DecisionPoint(decisionPointName, decisionPointId, row.getCell(dpClassCol)
                .getStringCellValue());

        // create new Decision
        Cell decisionCell = row.getCell(decCol);
        decisionName = decisionCell.getStringCellValue();
        decision =
            new Decision(decisionName, row.getCell(decClassCol).getStringCellValue(), decisionId,
                decisionPointId);

        // create new outcome
        Cell outcomeCell = row.getCell(outCol);
        outcome = new Outcome(outcomeCell.getStringCellValue(), outcomeId, decisionId);

        // add outcome to decision
        decision.addOutcome(outcome);
        // add decision to decision point
        decisionPoint.addDecision(decision);
        // add decision point to cloudDSF
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
          decision =
              new Decision(decisionName, row.getCell(decClassCol).getStringCellValue(), decisionId,
                  decisionPointId);
          // create new outcome
          Cell outcomeCell = row.getCell(outCol);
          outcome = new Outcome(outcomeCell.getStringCellValue(), outcomeId, decisionId);
          // add outcome to decision
          decision.addOutcome(outcome);
          // add decision to current decision point
          cdsf.getDecisionPoint(decisionPointName).addDecision(decision);
        } else {
          // if no text in dp or d than new outcome
          outcomeId++;
          // create new outcome
          Cell outcomeCell = row.getCell(outCol);
          outcome = new Outcome(outcomeCell.getStringCellValue(), outcomeId, decisionId);
          // add outcome to current decision in current decision point
          cdsf.getDecisionPoint(decisionPointName).getDecision(decisionName).addOutcome(outcome);
        }
      }
    }
    // parse the relations
    setInfluencingRelations();
    setTasks();
    setInfluencingTasks();
    // sort knowledge base and relations
    cdsf.sortEntities();
    cdsf.sortLists();
    // return cdsf object
    return cdsf;
  }

  /**
   * Retrieves influencing relations between decisions. All decisions (influencing, affecting,
   * binding) are parsed and stored as basic influencing ones for the cloudDSF.
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
      // Iterate over cells
      Iterator<Cell> cells = row.cellIterator();
      while (cells.hasNext()) {
        XSSFCell cell = (XSSFCell) cells.next();
        String relationName = cell.getStringCellValue();
        if (relationName.equals("Influencing") || relationName.equals("Affecting")
            || relationName.equals("Binding")) {
          // if type of relationship matches predefined values get names of the two participating
          // decisions
          String startDecision = row.getCell(startDecisionColumn).getStringCellValue();
          String endDecision = endDecisionRow.getCell(cell.getColumnIndex()).getStringCellValue();
          // add new decision relation
          cdsf.setLegacyDecisionRelation(startDecision, endDecision);
        }
      }
    }
  }

  /**
   * Retrieves influencing relations between tasks and decisions.
   */
  private void setInfluencingTasks() {
    XSSFSheet sheet = workbook.getSheet("Task Level");
    // Column A has name of start Task
    int startTaskColumn = 0;
    // Row 1 has names of endDecision
    Row endDecisionRow = sheet.getRow(1);
    // Iterate over all rows
    Iterator<Row> rows = sheet.rowIterator();
    while (rows.hasNext()) {
      XSSFRow row = (XSSFRow) rows.next();
      Iterator<Cell> cells = row.cellIterator();
      while (cells.hasNext()) {
        XSSFCell cell = (XSSFCell) cells.next();
        if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
          // Depending on the relation type source and target are set
          // accordingly
          String relationName = cell.getStringCellValue();
          String sourceDesc = row.getCell(startTaskColumn).getStringCellValue();
          String targetDesc = endDecisionRow.getCell(cell.getColumnIndex()).getStringCellValue();
          switch (relationName) {
            case "Affecting":
              cdsf.setTaskRelation(sourceDesc, targetDesc, "oneWay", relationName);
              break;
            case "Both":
              cdsf.setTaskRelation(sourceDesc, targetDesc, "twoWay", relationName);
              break;
            case "Affected":
              cdsf.setTaskRelation(sourceDesc, targetDesc, "backwards", relationName);
              break;
          // no default
          }
        }
      }
    }
  }

  /**
   * Retrieve defined tasks.
   */
  private void setTasks() {
    XSSFSheet sheet = workbook.getSheet("Task Level");
    // start with fixed task id
    int taskId = 901;
    // iterate over all rows skipping headline
    for (int j = 2; j <= sheet.getLastRowNum(); j++) {
      // row 2 gets selected
      Row row = sheet.getRow(j);
      // select cell A
      Cell cell = row.getCell(0);
      // create new task
      Task task = new Task(taskId, cell.getStringCellValue());
      taskId++;
      cdsf.addTask(task);
    }
  }
}
