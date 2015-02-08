package parser;

import static org.junit.Assert.assertTrue;

import cloudDSF.CloudDSF;
import cloudDSF.Outcome;
import cloudDSF.OutcomeRelation;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.IOException;


/**
 * Check CloudDSFPlusParser.
 * 
 * @author Metz
 *
 */
public class CloudDSFPlusParserTest {
  private CloudDSFPlusParser cloudDSFPlusParser;
  private CloudDSF cdsf;
/**
 * Fetches new instance of the cloudDSF object prior to each test.
 * @throws Exception
 */
  @Before
  public void setUp() throws Exception {
    String filePath = "MockKnowledgeBase.xlsx";
    XSSFWorkbook workbook = null;
    // Create Workbook instance holding reference to .xlsx file
    InputStream in = JsonWriter.class.getClassLoader().getResourceAsStream(filePath);
    try {
      workbook = new XSSFWorkbook(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
    cloudDSFPlusParser = new CloudDSFPlusParser(workbook);
  }

  /**
   * Checks if created object from parsed file corresponds to the expected results.
   */
  @Test
  public void testReadExcel() {
    cdsf = cloudDSFPlusParser.readExcel();
    assertTrue(cdsf != null);
    assertTrue(cdsf.getDecisionPoints().size() == 2);
    assertTrue(cdsf.getInfluencingDecisions().size() == 11);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecisions().size() == 2);
    assertTrue(cdsf.getDecisionPoint("Select Service Provider / Offering").getDecisions().size() == 3);
    assertTrue(cdsf.getInfluencingOutcomes().size() == 246);

    int ex = 0;
    int aff = 0;
    int in = 0;
    int all = 0;
    int eb = 0;
    int error = 0;
    for (OutcomeRelation or : cdsf.getInfluencingOutcomes()) {
      switch (or.getType()) {
        case "in":
          in++;
          break;
        case "ex":
          ex++;
          break;
        case "a":
          all++;
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
    assertTrue(all == 118);
    assertTrue(error == 0);

    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(101) != null);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(102) != null);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(101)
        .getOutcome(10107) != null);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(102)
        .getOutcome(10207) != null);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(102)
        .getOutcome(10209) == null);

    Outcome sourceOut =
        cdsf.getDecisionPoint("Define Application Distribution").getDecision(102).getOutcome(10207);
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
