package parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cloudDSF.CloudDSF;
import cloudDSF.Decision;
import cloudDSF.DecisionPoint;
import cloudDSF.Outcome;

public class ExcelParser {

	public CloudDSF readExcel(String filePath) {
		// TODO Auto-generated method stub
		CloudDSF cdsf = new CloudDSF();
		try {
			FileInputStream file = new FileInputStream(new File(filePath));
			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);
			String decisionName = "";
			String decisionPointName = "";
			// HashMap<String, Decision> decisions = new HashMap<String,
			// Decision>();

			DecisionPoint decisionPoint;
			Decision decision;
			Outcome outcome;
			// skip headline

			int decisionPointID = 0;
			int decisionID = 0;
			int outcomeID = 0;
			// iterate over all rows

			for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
				// row 2 gets selected
				Row row = sheet.getRow(j);
				// select cell B
				Cell cell = row.getCell(0);

				// check cell content
				if (cell.getStringCellValue().equals("") == false) {
					decisionPointID++;
					decisionID = decisionPointID * 100 + 1;
					outcomeID = decisionID * 100 + 1;
					// Create new DecisionPoint
					decisionPointName = cell.getStringCellValue();
					decisionPoint = new DecisionPoint(decisionPointName,
							decisionPointID, row.getCell(4)
									.getStringCellValue());

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
					// decision to decisionPoint
					decisionPoint.addDecision(decision);
					// decisions.put(decision.getLabel(), decision);
					cdsf.addDecisionPoint(decisionPoint);
				} else {

					Cell decisionCell = row.getCell(1);
					if (decisionCell.getStringCellValue().equals("") == false) {

						decisionID++;
						outcomeID = decisionID * 100 + 1;

						decisionName = decisionCell.getStringCellValue();
						decision = new Decision(decisionName, row.getCell(3)
								.getStringCellValue(), decisionID,
								decisionPointID);
						// get outcome cell C
						Cell outcomeCell = row.getCell(2);
						// create new outcome object
						outcome = new Outcome(outcomeCell.getStringCellValue(),
								outcomeID, decisionID);

						decision.addOutcome(outcome);
						cdsf.getDecisionPoints().get(decisionPointName)
								.addDecision(decision);

					} else {
						outcomeID++;
						Cell outcomeCell = row.getCell(2);
						// create new outcome object
						outcome = new Outcome(outcomeCell.getStringCellValue(),
								outcomeID, decisionID);
						cdsf.getDecisionPoints().get(decisionPointName)
								.getDecision(decisionName).addOutcome(outcome);
					}
				}
			}

			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setWeight(cdsf.getDecisionPoints());
		for (DecisionPoint dp : cdsf.getDecisionPoints().values()) {
			dp.prepareSortedDecisions();
			for (Decision d : dp.getDecisions().values()) {
				d.prepareSortedOutcomes();
			}
			cdsf.prepareSortedDPs();
		}
		return cdsf;

	}

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
}
