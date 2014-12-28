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
import cloudDSF.Task;

/**
 * Reads excel file and collects all the data necessary for the cloudDSF
 * 
 * @author Metz
 *
 */
public class CloudDSFParser {

	private final CloudDSF cdsf;
	private final XSSFWorkbook workbook;

	public CloudDSFParser(XSSFWorkbook workbook) {
		this.cdsf = new CloudDSF(-1, "root", "Legacy CloudDSF");
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
					cdsf.getDecisionPoint(decisionPointName).addDecision(
							decision);
				} else {
					// if no text in dp or d than new outcome
					outcomeID++;
					// create new outcome object
					Cell outcomeCell = row.getCell(2);
					outcome = new Outcome(outcomeCell.getStringCellValue(),
							outcomeID, decisionID);
					cdsf.getDecisionPoint(decisionPointName)
							.getDecision(decisionName).addOutcome(outcome);
				}
			}
		}
		// parse the relations
		setInfluencingRelations();
		setTasks();
		setInfluencingTasks();
		cdsf.sortEntities();
		cdsf.sortLists();
		return cdsf;
	}

	/**
	 * Retrieves influencing relations between decisions from sheet. All
	 * decisions (influencing, affecting, binding) are parsed and stored as
	 * basic influencing ones.
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
				String relationName = cell.getStringCellValue();
				if (relationName.equals("Influencing")
						|| relationName.equals("Affecting")
						|| relationName.equals("Binding")) {
					String startDecision = row.getCell(startDecisionColumn)
							.getStringCellValue();
					String endDecision = endDecisionRow.getCell(
							cell.getColumnIndex()).getStringCellValue();
					cdsf.setLegacyDecisionRelation(startDecision, endDecision);
				}
			}
		}
	}

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
				if ((cell == null)
						|| ((cell != null) && (cell.getCellType() == Cell.CELL_TYPE_BLANK))) {

				} else {
					String relationName = cell.getStringCellValue();
					String sourceDesc = row.getCell(startTaskColumn)
							.getStringCellValue();
					String targetDesc = endDecisionRow.getCell(
							cell.getColumnIndex()).getStringCellValue();
					switch (relationName) {
					case "Affecting":
						cdsf.setTaskRelation(sourceDesc, targetDesc, "oneWay",
								relationName);
						break;
					case "Both":
						cdsf.setTaskRelation(sourceDesc, targetDesc, "twoWay",
								relationName);
						break;
					case "Affected":
						cdsf.setTaskRelation(sourceDesc, targetDesc,
								"backwards", relationName);
						break;
					}
				}
			}
		}
	}

	private void setTasks() {
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheet("Task Level");
		int taskID = 901;
		// iterate over all rows
		// skip headline
		for (int j = 2; j < 12; j++) {
			// row 2 gets selected
			Row row = sheet.getRow(j);
			// select cell A
			Cell cell = row.getCell(0);
			Task task = new Task(taskID, cell.getStringCellValue());
			taskID++;
			cdsf.addTask(task);
		}
	}
}
