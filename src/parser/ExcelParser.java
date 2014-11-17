package parser;

import java.util.HashMap;
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
public class ExcelParser {
	/**
	 * Retrieves the knowledge base of the CloudDSF from the sheet and trigger
	 * retrieving of relations.
	 * 
	 * @param workbook
	 * @return
	 */
	public CloudDSF readExcel(XSSFWorkbook workbook) {
		// new CloudDSF representation object
		CloudDSF cdsf = new CloudDSF();
		// Get first/desired sheet from the workbook
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
			Cell cell = row.getCell(0);
			// if not empty than new decision Point
			if (cell.getStringCellValue().equals("") == false) {
				decisionPointID++;
				decisionID = decisionPointID * 100 + 1;
				outcomeID = decisionID * 100 + 1;
				// Create new DecisionPoint
				decisionPointName = cell.getStringCellValue();
				decisionPoint = new DecisionPoint(decisionPointName,
						decisionPointID, row.getCell(4).getStringCellValue());

				// Create new Decision
				Cell decisionCell = row.getCell(1);
				decisionName = decisionCell.getStringCellValue();
				decision = new Decision(decisionName, row.getCell(3)
						.getStringCellValue(), decisionID, decisionPointID);
				// Create new outcome
				Cell outcomeCell = row.getCell(2);
				outcome = new Outcome(outcomeCell.getStringCellValue(),
						outcomeID, decisionID);
				// Add outcome to decision
				decision.addOutcome(outcome);
				// add decision to decisionPoint
				decisionPoint.addDecision(decision);
				// add decisionPoint to cloudDSF
				cdsf.addDecisionPoint(decisionPoint);
			} else {
				// Select Cell B
				Cell decisionCell = row.getCell(1);
				// if text than new decision
				if (decisionCell.getStringCellValue().equals("") == false) {
					decisionID++;
					outcomeID = decisionID * 100 + 1;
					decisionName = decisionCell.getStringCellValue();
					decision = new Decision(decisionName, row.getCell(3)
							.getStringCellValue(), decisionID, decisionPointID);
					// create new outcome object
					Cell outcomeCell = row.getCell(2);
					outcome = new Outcome(outcomeCell.getStringCellValue(),
							outcomeID, decisionID);
					decision.addOutcome(outcome);
					cdsf.getDecisionPoints().get(decisionPointName)
							.addDecision(decision);
				} else {
					// if no text in dp or d than new outcome
					outcomeID++;
					// create new outcome object
					Cell outcomeCell = row.getCell(2);
					outcome = new Outcome(outcomeCell.getStringCellValue(),
							outcomeID, decisionID);
					cdsf.getDecisionPoints().get(decisionPointName)
							.getDecision(decisionName).addOutcome(outcome);
				}
			}
		}
		// calculate weight of outcomes
		setWeight(cdsf.getDecisionPoints());
		// parse the relations
		cdsf = setInfluencingRelations(cdsf, workbook);
		cdsf = setRequiringRelations(cdsf, workbook);
		cdsf = setInfluencingOutcomes(cdsf, workbook);
		for (DecisionPoint dp : cdsf.getDecisionPoints().values()) {
			dp.prepareSortedDecisions();
			for (Decision d : dp.getDecisions().values()) {
				d.prepareSortedOutcomes();
			}
			cdsf.prepareSortedDPs();
		}
		return cdsf;
	}

	/**
	 * Calculates the weight for each outcome within a decision to the same
	 * amount by dividing 1 / the amount of outcomes per decision.
	 * 
	 * @param decisionPoints
	 */
	private void setWeight(HashMap<String, DecisionPoint> decisionPoints) {
		for (DecisionPoint dp : decisionPoints.values()) {
			for (Decision d : dp.getDecisions().values()) {
				double amount = d.getOutcomes().size();
				double weight = 1 / amount;
				for (Outcome o : d.getOutcomes().values()) {
					o.setWeight(weight);
				}
			}
		}
	}

	/**
	 * Retrieves influencing relations between decisions from sheet
	 * 
	 * @param cdsf
	 * @param workbook
	 * @return
	 */
	private CloudDSF setInfluencingRelations(CloudDSF cdsf,
			XSSFWorkbook workbook) {
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
				String relationName = cell.getStringCellValue();
				if (relationName.equals("Influencing")
						|| relationName.equals("Affecting")
						|| relationName.equals("Binding")) {
					String startDecision = row.getCell(startDecisionColumn)
							.getStringCellValue();
					String endDecision = endDecisionRow.getCell(
							cell.getColumnIndex()).getStringCellValue();
					cdsf.setDecisionRelation(startDecision, endDecision,
							relationName);
				}
			}
		}
		return cdsf;
	}

	/**
	 * Retrieves requiring relations between decisions
	 * 
	 * @param cdsf
	 * @param workbook
	 * @return
	 */
	private CloudDSF setRequiringRelations(CloudDSF cdsf, XSSFWorkbook workbook) {
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
				String relationName = cell.getStringCellValue();
				if (relationName.equals("Requiring")) {
					String startDecision = row.getCell(startDecisionColumn)
							.getStringCellValue();
					String endDecision = endDecisionRow.getCell(
							cell.getColumnIndex()).getStringCellValue();
					cdsf.setDecisionRelation(startDecision, endDecision,
							relationName);
				}
			}
		}
		return cdsf;
	}

	/**
	 * Retrieves relations between outcomes from sheet.
	 * 
	 * @param cdsf
	 * @param workbook
	 * @return
	 */
	private CloudDSF setInfluencingOutcomes(CloudDSF cdsf, XSSFWorkbook workbook) {
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
				String relationName = cell.getStringCellValue();
				if (relationName.equals("in") || relationName.equals("ex")
						|| relationName.equals("a")
						|| relationName.equals("eb")) {
					String startOutcome = row.getCell(startOutcomeColumn)
							.getStringCellValue();
					String endOutcome = endOutcomeRow.getCell(
							cell.getColumnIndex()).getStringCellValue();
					cdsf.setOutcomeRelation(startOutcome, endOutcome,
							relationName);
				}
			}
		}
		return cdsf;
	}
}
