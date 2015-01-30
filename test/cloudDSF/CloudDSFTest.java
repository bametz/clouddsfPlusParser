package cloudDSF;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import parser.CloudDSFPlusParser;
import parser.JsonWriter;

public class CloudDSFTest {
	private CloudDSF cdsf;

	@Before
	public void setUp() throws Exception {
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

	@Test
	public void testCheckRelTypesDecisions() {
		assertTrue(cdsf.checkRelTypesDecisions());
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Application Tier", "test", "");
		assertFalse(cdsf.checkRelTypesDecisions());
	}

	@Test
	public void testCheckRelTypesOutcomes() {
		assertTrue(cdsf.checkRelTypesOutcomes());
		cdsf.setOutcomeRelation("Application Component", "Migration Type I",
				"test", "", "");
		assertFalse(cdsf.checkRelTypesOutcomes());
	}

	@Test
	public void testCheckDecRelComb() {
		assertTrue(cdsf.checkDecRelComb());
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Application Tier", "influencing", "");
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Application Tier", "affecting", "");
		assertFalse(cdsf.checkDecRelComb());
	}

	@Test
	public void testcheckOutRelAmountForDecRel() {
		assertTrue(cdsf.checkOutRelAmountForDecRel());
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Application Tier", "influencing", "");
		// new relation between layer and tier thus several relations between
		// outcomes must exist
		assertFalse(cdsf.checkOutRelAmountForDecRel());
	}

	@Test
	public void testcheckOutRelAmountForDecRel2() {
		assertTrue(cdsf.checkOutRelAmountForDecRel());
		// additional outcoem relation thus one relation is too much
		cdsf.setOutcomeRelation("Application Component", "Presentation Layer",
				"ex", "", "");
		assertFalse(cdsf.checkOutRelAmountForDecRel());
	}

	// check that only aff are under affecting
	@Test
	public void testcheckOutRelTypeForDecRelAffecting() {
		assertTrue(cdsf.checkOutRelTypeForDecRel());
		cdsf.setDecisionRelation("Select Application Components",
				"Select Application Tier", "affecting", "");
		cdsf.setOutcomeRelation("Application Component", "Client Tier", "in",
				"", "");
		assertFalse(cdsf.checkOutRelTypeForDecRel());
	}

	// check that only eb are under binding
	@Test
	public void testcheckOutRelTypeForDecRelBinding() {
		assertTrue(cdsf.checkOutRelTypeForDecRel());
		cdsf.setDecisionRelation("Select Application Components",
				"Select Application Tier", "binding", "");
		cdsf.setOutcomeRelation("Application Component", "Client Tier", "in",
				"", "");
		assertFalse(cdsf.checkOutRelTypeForDecRel());
	}

	@Test
	public void testCheckAffBinDecRelations() {
		assertTrue(cdsf.checkAffBinDecRelations("affecting", "binding"));
		assertTrue(cdsf.checkAffBinDecRelations("binding", "affecting"));
		cdsf.setDecisionRelation("Select Application Components",
				"Select Application Tier", "binding", "");
		assertFalse(cdsf.checkAffBinDecRelations("binding", "affecting"));
		cdsf.setDecisionRelation("Select Application Components",
				"Select Application Layer", "affecting", "");
		assertFalse(cdsf.checkAffBinDecRelations("affecting", "binding"));
	}

	@Test
	public void testCheckAffBinOutRelations() {
		assertTrue(cdsf.checkAffBinOutRelations("aff", "eb"));
		assertTrue(cdsf.checkAffBinOutRelations("eb", "aff"));
		cdsf.setOutcomeRelation("Application Component", "Client Tier", "eb",
				"", "");
		assertFalse(cdsf.checkAffBinOutRelations("eb", "aff"));
		cdsf.setOutcomeRelation("Application Component", "Data Tier", "aff",
				"", "");
		assertFalse(cdsf.checkAffBinOutRelations("aff", "eb"));
	}

	@Test
	public void testCheckInAOutRelations() {
		assertTrue(cdsf.checkInAOutRelations("in", "a", "in"));
		assertTrue(cdsf.checkInAOutRelations("a", "in", "a"));
		
		cdsf.setOutcomeRelation("Application Component", "Data Tier", "a", "",
				"");
		cdsf.setOutcomeRelation("Data Tier", "Application Component", "ex", "",
				"");
		assertTrue(cdsf.checkInAOutRelations("in", "a", "in"));
		assertFalse(cdsf.checkInAOutRelations("a", "a", "in"));
		
		cdsf.setOutcomeRelation("Application Component", "Client Tier", "in", "",
				"");
		cdsf.setOutcomeRelation("Client Tier", "Application Component", "ex", "",
				"");
		assertFalse(cdsf.checkInAOutRelations("in", "a", "in"));
		assertFalse(cdsf.checkInAOutRelations("a", "a", "in"));
		}

	@Test
	public void testCheckXOROutcomesSelf() {
		assertTrue(cdsf.checkXOROutcomes());
		cdsf.setOutcomeRelation("Application Component",
				"Application Component", "a", "", "");
		assertFalse(cdsf.checkXOROutcomes());
	}

	@Test
	public void testCheckXOROutcomes() {
		assertTrue(cdsf.checkXOROutcomes());
		cdsf.setOutcomeRelation("Application Component",
				"Middleware Component", "a", "", "");
		assertFalse(cdsf.checkXOROutcomes());
	}

	@Test
	public void testCheckSingleOutcomeRel() {
		assertTrue(cdsf.checkSingleOutcomeRel());
		cdsf.setOutcomeRelation("Application Component", "Data Tier", "a", "",
				"");
		cdsf.setOutcomeRelation("Application Component", "Data Tier", "a", "",
				"");
		assertFalse(cdsf.checkSingleOutcomeRel());
	}

}
