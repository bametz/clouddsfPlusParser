package cloudDSF;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import parser.CloudDSFPlusParser;
import parser.JsonWriter;

public class cloudDSFTest {
	private static CloudDSF cdsf;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String filePath = "KnowledgeBase.xlsx";
		XSSFWorkbook workbook = null;
		// Create Workbook instance holding reference to .xlsx file
		InputStream in = JsonWriter.class.getClassLoader().getResourceAsStream(
				filePath);
		try {
			workbook = new XSSFWorkbook(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		CloudDSFPlusParser cloudDSFPlusParser = new CloudDSFPlusParser(workbook);
		cdsf = cloudDSFPlusParser.readExcel();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testCheckRelTypesDecisions() {

		cdsf.checkRelTypesDecisions();
		fail("Not yet implemented");
	}

	@Test
	public void testCheckRelTypesOutcomes() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckDecRelComb() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckOutRelTypeForDecRel() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckAffBinDecRelations() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckAffBinOutRelations() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckInAOutRelations() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckXOROutcomes() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckSingleOutcomeRel() {
		fail("Not yet implemented");
	}

}
