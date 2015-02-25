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

import static org.junit.Assert.assertTrue;

import cloudDSF.CloudDSF;
import cloudDSF.DecisionPoint;
import cloudDSF.DecisionRelation;
import cloudDSF.Outcome;
import cloudDSF.OutcomeRelation;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.IOException;


/**
 * Check correct parsing of the CloudDSFPlusParser.
 * 
 * @author Metz
 *
 */
public class CloudDSFPlusParserTest {
  private CloudDSFPlusParser cloudDSFPlusParser;
  private CloudDSF cdsf;

  /**
   * Fetches new instance of the cloudDSF object prior to each test.
   * 
   * @throws Exception Reading of Excel file fails
   */
  @Before
  public void setUp() throws Exception {
    String filePath = "MockupKnowledgeBase.xlsx";
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
    // check exact amount of entities and relations
    assertTrue(cdsf.getDecisionPoints().size() == 2);
    assertTrue(cdsf.getInfluencingDecisions().size() == 11);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecisions().size() == 2);
    assertTrue(cdsf.getDecisionPoint("Select Service Provider / Offering").getDecisions().size() == 3);
    assertTrue(cdsf.getInfluencingOutcomes().size() == 246);

    // Check amount of decision relations
    int req = 0;
    int inf = 0;
    int affecting = 0;
    int binding = 0;
    for (DecisionRelation dr : cdsf.getInfluencingDecisions()) {
      switch (dr.getType()) {
        case "requiring":
          req++;
          break;
        case "influencing":
          inf++;
          break;
        case "affecting":
          affecting++;
          break;
        case "binding":
          binding++;
          break;
      // no default
      }
    }
    assertTrue(req == 3);
    assertTrue(inf == 4);
    assertTrue(affecting == 2);
    assertTrue(binding == 2);

    // Check amount of outcome relations
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

    // Check knowledge base entities
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(101) != null);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(102) != null);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(101)
        .getOutcome(10107) != null);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(102)
        .getOutcome(10207) != null);
    assertTrue(cdsf.getDecisionPoint("Define Application Distribution").getDecision(102)
        .getOutcome(10209) == null);

    DecisionPoint dp = cdsf.getDecisionPoint("Select Service Provider / Offering");
    assertTrue(dp.getId() == 2);
    assertTrue(dp.getParent() == 0);

    Outcome sourceOut =
        cdsf.getDecisionPoint("Define Application Distribution").getDecision(102).getOutcome(10207);
    assertTrue(sourceOut.getLabel().equals("Middleware Component + Application Components"));
    assertTrue(sourceOut.getParent() == 102);
    assertTrue(sourceOut.getType() == "out");
    // Check correct parsing of outcome relations
    for (OutcomeRelation outRel : cdsf.getInfluencingOutcomes()) {
      if (outRel.getSource() == sourceOut.getId()) {
        if (outRel.getTarget() == 10103) {
          assertTrue(outRel.getType().equals("ex"));
        } else if (outRel.getTarget() < 10108) {
          assertTrue(outRel.getType().equals("a"));
        }
      }
    }

    // Check types of outcome relations
    for (OutcomeRelation outRel : cdsf.getInfluencingOutcomes()) {
      if (outRel.getSource() == 20204) {
        if (outRel.getTarget() == 10202) {
          assertTrue(outRel.getType().equals("in"));
        }
        if (outRel.getTarget() == 10203) {
          assertTrue(outRel.getType().equals("ex"));
        }
        if (outRel.getTarget() == 10204) {
          assertTrue(outRel.getType().equals("a"));
        }
        if (outRel.getTarget() == 20301) {
          assertTrue(outRel.getType().equals("aff"));
        }
      }

      if (outRel.getSource() == 20301) {
        if (outRel.getTarget() >= 20101 && outRel.getTarget() <= 20104) {
          assertTrue(outRel.getType().equals("eb"));
        }
        if (outRel.getTarget() >= 20201 && outRel.getTarget() <= 20207) {
          assertTrue(outRel.getType().equals("eb"));
        }
      }
    }
  }
}
