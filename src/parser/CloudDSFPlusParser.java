package parser;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cloudDSF.CloudDSF;
import cloudDSF.Decision;
import cloudDSF.DecisionPoint;
import cloudDSF.Outcome;

/**
 * Reads excel file and collects all the data.
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

	public CloudDSFPlusParser(XSSFWorkbook workbook) {
		this.cdsf = new CloudDSF(0, "root", "CloudDSF+");
		cdsf.setAbbrev("CDSF+");
		cdsf.setDescription("CDSF+ knowledge base containing decision point, decisions and their outcomes.");
		this.workbook = workbook;
	}

	/**
	 * Retrieves the knowledge base of the CloudDSF from the sheet and trigger
	 * retrieving of relations.
	 * 
	 * @param workbook
	 * @return
	 */
	public CloudDSF readExcel() {
		// Get first/desired sheet from the workbook
		// workbook.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL);
		XSSFSheet sheet = workbook.getSheet("Knowledge Base");
		// XSSFSheet sheet = workbook.getSheetAt(0);

		String decisionName = "";
		String decisionPointName = "";

		DecisionPoint decisionPoint;
		Decision decision;
		Outcome outcome;

		int decisionPointID = 0;
		int decisionID = 0;
		int outcomeID = 0;

		// iterate over all rows
		// skip headline
		for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
			// row 2 gets selected
			Row row = sheet.getRow(j);
			// select cell A
			Cell cell = row.getCell(dpCol);
			// if not empty than new decision Point
			if (cell.getStringCellValue().equals("") == false) {
				decisionPointID++;
				decisionID = decisionPointID * 100 + 1;
				outcomeID = decisionID * 100 + 1;
				decisionPointName = cell.getStringCellValue();
				// Create new DecisionPoint
				decisionPoint = generateDecisionPoint(cell, decisionPointID,
						row);
				// Create new Decision
				Cell decisionCell = row.getCell(decCol);
				decisionName = decisionCell.getStringCellValue();
				// todo description
				decision = generateDecision(decisionCell, decisionID,
						decisionPointID, row);
				// Create new outcome
				Cell outcomeCell = row.getCell(outCol);
				// ToDo Description
				outcome = generateOutcome(outcomeCell, decisionID,
						decisionPointID, outcomeID, row);
				// Add outcome to decision
				decision.addOutcome(outcome);
				// add decision to decisionPoint
				decisionPoint.addDecision(decision);
				// add decisionPoint to cloudDSF
				cdsf.addDecisionPoint(decisionPoint);
			} else {
				// Select Cell B
				Cell decisionCell = row.getCell(decCol);
				// if text than new decision
				if (decisionCell.getStringCellValue().equals("") == false) {
					decisionID++;
					outcomeID = decisionID * 100 + 1;
					decisionName = decisionCell.getStringCellValue();
					decision = generateDecision(decisionCell, decisionID,
							decisionPointID, row);
					// create new outcome object
					Cell outcomeCell = row.getCell(outCol);
					outcome = generateOutcome(outcomeCell, decisionID,
							decisionPointID, outcomeID, row);
					decision.addOutcome(outcome);
					cdsf.getDecisionPoint(decisionPointName).addDecision(
							decision);
				} else {
					// if no text in dp or d than new outcome
					outcomeID++;
					// create new outcome object
					Cell outcomeCell = row.getCell(outCol);
					outcome = generateOutcome(outcomeCell, decisionID,
							decisionPointID, outcomeID, row);
					cdsf.getDecisionPoint(decisionPointName)
							.getDecision(decisionName).addOutcome(outcome);
				}
			}
		}
		setInfluencingRelations();
		setRequiringRelations();
		setInfluencingOutcomes();
		cdsf.sortEntities();
		cdsf.sortLists();
		return cdsf;

	}

	private Outcome generateOutcome(Cell cell, int decisionID,
			int decisionPointID, int outcomeID, Row row) {
		String label = cell.getStringCellValue();
		String description = row.getCell(outDescCol).getStringCellValue();
		String abbrev = cell.getCellComment().getString().getString();
		Outcome o = new Outcome(label, outcomeID, decisionPointID, decisionID,
				description, null, abbrev);
		return o;
	}

	private Decision generateDecision(Cell cell, int decisionID,
			int decisionPointID, Row row) {
		String label = cell.getStringCellValue();
		String classification = row.getCell(decClassCol).getStringCellValue();
		String description = row.getCell(decDescCol).getStringCellValue();
		String abbrev = cell.getCellComment().getString().getString();
		Decision d = new Decision(label, decisionID, decisionPointID,
				decisionPointID, classification, description, null, abbrev);
		return d;
	}

	private DecisionPoint generateDecisionPoint(Cell cell, int decisionPointID,
			Row row) {
		String label = cell.getStringCellValue();
		String classification = row.getCell(dpClassCol).getStringCellValue();
		String description = row.getCell(dpDescCol).getStringCellValue();
		String abbrev = cell.getCellComment().getString().getString();
		DecisionPoint dp = new DecisionPoint(label, decisionPointID,
				decisionPointID, classification, description, null, abbrev);
		return dp;
	}

	/**
	 * Retrieves influencing relations between decisions from sheet
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
			// for (int j = 2; j < sheet.getLastRowNum(); j++) {
			// Row row = sheet.getRow(j);
			// select cell C
			Iterator<Cell> cells = row.cellIterator();
			while (cells.hasNext()) {
				XSSFCell cell = (XSSFCell) cells.next();
				String relationType = cell.getStringCellValue();
				if (relationType.equals("Influencing")
						|| relationType.equals("Affecting")
						|| relationType.equals("Binding")) {
					String startDecision = row.getCell(startDecisionColumn)
							.getStringCellValue();
					String endDecision = endDecisionRow.getCell(
							cell.getColumnIndex()).getStringCellValue();
					// todo additional info and explanation
					cdsf.setDecisionRelation(startDecision, endDecision,
							relationType, null, null);
				}
			}
		}

	}

	/**
	 * Retrieves requiring relations between decisions
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
					String startDecision = row.getCell(startDecisionColumn)
							.getStringCellValue();
					String endDecision = endDecisionRow.getCell(
							cell.getColumnIndex()).getStringCellValue();
					// todo explanation additonal info
					cdsf.setDecisionRelation(startDecision, endDecision,
							relationType, null, null);
				}
			}
		}
	}

	/**
	 * Retrieves relations between outcomes from sheet.
	 * 
	 * @return
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
			while (cells.hasNext()) {
				XSSFCell cell = (XSSFCell) cells.next();
				String relationType = cell.getStringCellValue();
				if (relationType.equals("in") || relationType.equals("ex")
						|| relationType.equals("a")
						|| relationType.equals("eb")) {
					String startOutcome = row.getCell(startOutcomeColumn)
							.getStringCellValue();
					String endOutcome = endOutcomeRow.getCell(
							cell.getColumnIndex()).getStringCellValue();
					// todo explanation additional info
					cdsf.setOutcomeRelation(startOutcome, endOutcome,
							relationType, null, null);
				}
			}
		}
	}

}
