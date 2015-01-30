package parser;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import cloudDSF.CloudDSF;
import cloudDSF.Outcome;
import cloudDSF.OutcomeRelation;

public class CloudDSFPlusParserTest {
	private  CloudDSFPlusParser cloudDSFPlusParser;
	private  CloudDSF cdsf;

	@Before
	public void setUp() throws Exception {
		String filePath = "TestKB.xlsx";
		XSSFWorkbook workbook = null;
		// Create Workbook instance holding reference to .xlsx file
		InputStream in = JsonWriter.class.getClassLoader().getResourceAsStream(
				filePath);
		try {
			workbook = new XSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cloudDSFPlusParser = new CloudDSFPlusParser(workbook);
	}

	@Test
	public void testReadExcel() {
		cdsf = cloudDSFPlusParser.readExcel();
		assertTrue(cdsf != null);
		assertTrue(cdsf.getDecisionPoints().size() == 2);
		assertTrue(cdsf.getInfluencingDecisions().size() == 11);
		assertTrue(cdsf.getDecisionPoint("Define Application Distribution")
				.getDecisions().size() == 2);
		assertTrue(cdsf.getDecisionPoint("Select Service Provider / Offering")
				.getDecisions().size() == 3);
		assertTrue(cdsf.getInfluencingOutcomes().size() == 246);

		int ex = 0, aff = 0, in = 0, a = 0, eb = 0, error = 0;
		for (OutcomeRelation or : cdsf.getInfluencingOutcomes()) {
			switch (or.getType()) {
			case "in":
				in++;
				break;
			case "ex":
				ex++;
				break;
			case "a":
				a++;
				break;
			case "eb":
				eb++;
				break;
			case "aff":
				aff++;
				break;
			default:
				error++;
				break;
			}
		}
		assertTrue(in == 2);
		assertTrue(ex == 104);
		assertTrue(aff == 11);
		assertTrue(eb == 11);
		assertTrue(a == 118);
		assertTrue(error == 0);

		assertTrue(cdsf.getDecisionPoint("Define Application Distribution")
				.getDecision(101) != null);
		assertTrue(cdsf.getDecisionPoint("Define Application Distribution")
				.getDecision(102) != null);
		assertTrue(cdsf.getDecisionPoint("Define Application Distribution")
				.getDecision(101).getOutcome(10107) != null);
		assertTrue(cdsf.getDecisionPoint("Define Application Distribution")
				.getDecision(102).getOutcome(10207) != null);
		assertTrue(cdsf.getDecisionPoint("Define Application Distribution")
				.getDecision(102).getOutcome(10209) == null);

		Outcome sourceOut = cdsf
				.getDecisionPoint("Define Application Distribution")
				.getDecision(102).getOutcome(10207);
		for (OutcomeRelation outRel : cdsf.getInfluencingOutcomes()) {
			if (outRel.getSource() == sourceOut.getId()) {
				if (outRel.getTarget() == 10103) {
					assertTrue(outRel.getType().equals("ex"));
				} else if (outRel.getTarget() < 10108) {
					assertTrue(outRel.getType().equals("a"));
				}
			}
		}
	}
}
