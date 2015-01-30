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

public class CloudDSFTestMockKB {
	private CloudDSF cdsf;

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
		CloudDSFPlusParser cloudDSFPlusParser = new CloudDSFPlusParser(workbook);
		cdsf = cloudDSFPlusParser.readExcel();
	}

	@Test
	public void testCheckRelTypesDecisions() {
		assertTrue(cdsf.checkRelTypesDecisions());
		// insert decision relation with wrong relationship type
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Cloud Deployment Model", "test", "");
		assertFalse(cdsf.checkRelTypesDecisions());
	}

	@Test
	public void testCheckRelTypesOutcomes() {
		assertTrue(cdsf.checkRelTypesOutcomes());
		// insert outcome relation with wrong relationship type "test"
		cdsf.setOutcomeRelation("Presentation Layer", "Public Cloud", "test",
				"", "");
		assertFalse(cdsf.checkRelTypesOutcomes());
	}

	@Test
	public void testCheckDecRelComb() {
		assertTrue(cdsf.checkDecRelComb());
		// insert two decision relations between same decisions with non
		// combinational relationship types
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Cloud Deployment Model", "influencing", "");
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Cloud Deployment Model", "affecting", "");
		assertFalse(cdsf.checkDecRelComb());
	}

	@Test
	public void testcheckOutRelAmountForDecRel() {
		assertTrue(cdsf.checkOutRelAmountForDecRel());
		// add additional decision relation withouth outcome relations
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Cloud Deployment Model", "influencing", "");
		assertFalse(cdsf.checkOutRelAmountForDecRel());
	}

	@Test
	public void testcheckOutRelAmountForDecRel2() {
		assertTrue(cdsf.checkOutRelAmountForDecRel());
		// additional outcome relation thus one relation is too much
		cdsf.setOutcomeRelation("Application Component", "Presentation Layer",
				"ex", "", "");
		assertFalse(cdsf.checkOutRelAmountForDecRel());
	}

	// check that only aff are under affecting
	@Test
	public void testcheckOutRelTypeForDecRelAffecting() {
		assertTrue(cdsf.checkOutRelTypeForDecRel());
		// set affecting relation between
		cdsf.setDecisionRelation("Select Cloud Vendor",
				"Select Application Components", "affecting", "");
		cdsf.setOutcomeRelation("Evaluated Cloud Vendor",
				"Application Component", "in", "", "");
		assertFalse(cdsf.checkOutRelTypeForDecRel());

	}

	// check that only eb are under binding
	@Test
	public void testcheckOutRelTypeForDecRelBinding() {
		assertTrue(cdsf.checkOutRelTypeForDecRel());
		cdsf.setDecisionRelation("Select Application Components",
				"Select Cloud Vendor", "binding", "");
		cdsf.setOutcomeRelation("Application Component",
				"Evaluated Cloud Vendor", "in", "", "");
		assertFalse(cdsf.checkOutRelTypeForDecRel());
	}

	// check that no eb are under influencing
	@Test
	public void testcheckOutRelTypeForDecRelInfluencingBin() {
		assertTrue(cdsf.checkOutRelTypeForDecRel());
		cdsf.setDecisionRelation("Select Application Components",
				"Select Cloud Vendor", "influencing", "");
		cdsf.setOutcomeRelation("Application Component",
				"Evaluated Cloud Vendor", "eb", "", "");
		assertFalse(cdsf.checkOutRelTypeForDecRel());
	}

	// check that no aff is under influencing
	@Test
	public void testcheckOutRelTypeForDecRelInfluencingAff() {
		assertTrue(cdsf.checkOutRelTypeForDecRel());
		cdsf.setDecisionRelation("Select Application Components",
				"Select Cloud Vendor", "influencing", "");
		cdsf.setOutcomeRelation("Application Component",
				"Evaluated Cloud Vendor", "aff", "", "");
		assertFalse(cdsf.checkOutRelTypeForDecRel());
	}

	@Test
	public void testCheckAffBinDecRelations() {
		assertTrue(cdsf.checkAffBinDecRelations("affecting", "binding"));
		assertTrue(cdsf.checkAffBinDecRelations("binding", "affecting"));
		// add new binding relation without corresponding affecting relation
		cdsf.setDecisionRelation("Select Cloud Vendor",
				"Select Application Components", "binding", "");
		assertFalse(cdsf.checkAffBinDecRelations("binding", "affecting"));
		// add new affecting relation without corresponding binding relation
		cdsf.setDecisionRelation("Select Application Layer",
				"Select Cloud Vendor", "affecting", "");
		assertFalse(cdsf.checkAffBinDecRelations("affecting", "binding"));
	}

	@Test
	public void testCheckAffBinOutRelations() {
		assertTrue(cdsf.checkAffBinOutRelations("aff", "eb"));
		assertTrue(cdsf.checkAffBinOutRelations("eb", "aff"));
		// add new binding relation without corresponding affecting relation
		cdsf.setOutcomeRelation("Evaluated Cloud Vendor",
				"Application Component", "eb", "", "");
		assertFalse(cdsf.checkAffBinOutRelations("eb", "aff"));
		// add new affecting relation without corresponding binding relation
		cdsf.setOutcomeRelation("Application Components",
				"Evaluated Cloud Vendor", "aff", "", "");
		assertFalse(cdsf.checkAffBinOutRelations("aff", "eb"));
	}

	@Test
	public void testCheckInAOutRelations() {
		assertTrue(cdsf.checkInAOutRelations("in", "a", "in"));
		assertTrue(cdsf.checkInAOutRelations("a", "in", "a"));
		// add two new contradicting outcome relations a to ex
		cdsf.setOutcomeRelation("Application Component", "Public Cloud", "a",
				"", "");
		cdsf.setOutcomeRelation("Public Cloud", "Application Component", "ex",
				"", "");
		assertTrue(cdsf.checkInAOutRelations("in", "a", "in"));
		assertFalse(cdsf.checkInAOutRelations("a", "a", "in"));
		// add two new contradicting outcome relations in to ex
		cdsf.setOutcomeRelation("Application Component", "Private Cloud", "in",
				"", "");
		cdsf.setOutcomeRelation("Private Cloud", "Application Component", "ex",
				"", "");
		assertFalse(cdsf.checkInAOutRelations("in", "a", "in"));
		assertFalse(cdsf.checkInAOutRelations("a", "a", "in"));
	}

	@Test
	public void testCheckXOROutcomesSelf() {
		assertTrue(cdsf.checkXOROutcomes());
		// add self referencing outcome
		cdsf.setOutcomeRelation("Application Component",
				"Application Component", "a", "", "");
		assertFalse(cdsf.checkXOROutcomes());
	}

	@Test
	public void testCheckXOROutcomes() {
		assertTrue(cdsf.checkXOROutcomes());
		// add relation between two outcomes of same decision
		cdsf.setOutcomeRelation("Application Component",
				"Middleware Component", "a", "", "");
		assertFalse(cdsf.checkXOROutcomes());
	}

	@Test
	public void testCheckSingleOutcomeRel() {
		assertTrue(cdsf.checkSingleOutcomeRel());
		// add two relations between the same outcomes
		cdsf.setOutcomeRelation("Application Component", "Public Cloud", "a",
				"", "");
		cdsf.setOutcomeRelation("Application Component", "Public Cloud", "ex",
				"", "");
		assertFalse(cdsf.checkSingleOutcomeRel());
	}
}
