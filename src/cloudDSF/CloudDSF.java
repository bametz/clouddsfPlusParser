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

package cloudDSF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import util.CloudDSFEntityComparator;
import util.RelationComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the coudDSF(Plus) object with decision points, decisions, outcomes and their
 * relations.
 * 
 * @author Metz
 */

public class CloudDSF extends CloudDSFEntity {
  /**
   * Contains all decision points of the clouddsf.
   */
  private List<DecisionPoint> decisionPoints = new ArrayList<DecisionPoint>();
  /**
   * contains all relations between decisions (i.e. requiring, influencing, binding, affecting)
   */
  private List<DecisionRelation> influencingDecisions = new ArrayList<DecisionRelation>();
  /**
   * contain all relations between decisions (i.e. aff, eb, a, ex, in)
   */
  private List<OutcomeRelation> influencingOutcomes = new ArrayList<OutcomeRelation>();
  /**
   * contains all relations between tasks and decisions, null in case of cloudDSFPlus.
   */
  private List<TaskRelation> influencingTasks = new ArrayList<TaskRelation>();
  /**
   * contains all tasks of the cloudDSF null in case of the cloudDSFPlus.
   */
  private List<Task> tasks = new ArrayList<Task>();
  /**
   * Contains all influecing tasks and influencing decisions to enable link array for the
   * cloudDSFVisualizations, null for cloudDSFPlus
   */
  private List<Relation> influencingRelations = new ArrayList<Relation>();

  /**
   * Default constructor
   * 
   * @param id
   * @param type
   * @param label
   */
  public CloudDSF(int id, String type, String label) {
    super(id, type, label);
    this.setGroup("root");
  }

  /**
   * Sets relation between two decisions by retrieving their id and add new entry into
   * influencingDecisions for the CloudDSFPlus.
   * 
   * @param startDecision name of start decision
   * @param endDecision name of end decision
   * @param type relationship type
   * @param explanation
   * @param additionalInfo
   */
  public void setDecisionRelation(String startDecision, String endDecision, String type,
      String explanation) {
    int source = getDecision(startDecision).getId();
    int target = getDecision(endDecision).getId();
    influencingDecisions.add(new DecisionRelation(source, target, type, explanation));
  }

  /**
   * /** Sets relation between two decisions by retrieving their id and add new entry into
   * influencingDecisions for the CloudDSF.
   * 
   * @param startDecision
   * @param endDecision
   */
  public void setLegacyDecisionRelation(String startDecision, String endDecision) {
    int source = getDecision(startDecision).getId();
    int target = getDecision(endDecision).getId();
    influencingDecisions.add(new DecisionRelation(source, target));
  }

  /**
   * Sets relation between two outcomes by retrieving their id and adding new entry to
   * influencingOutcomes.
   * 
   * @param startOutcome
   * @param endOutcome
   * @param type Type of outcome e.g. ex, in, a
   */
  public void setOutcomeRelation(String startOutcome, String endOutcome, String type,
      String explanation, String additionalInfo) {
    int source = getOutcome(startOutcome).getId();
    int target = getOutcome(endOutcome).getId();
    influencingOutcomes.add(new OutcomeRelation(source, target, type, explanation));
  }

  /**
   * Sets task relation according to the specified type.
   * 
   * @param sourceDesc
   * @param targetDesc
   * @param dir
   * @param label
   */
  public void setTaskRelation(String sourceDesc, String targetDesc, String dir, String label) {
    int source = 0;
    int target = 0;
    // depending on direction different relation has to be set.
    switch (dir) {
      case "oneWay":
        source = getTask(sourceDesc).getId();
        target = getDecision(targetDesc).getId();
        dir = "auto";
        break;
      case "twoWay":
        source = getTask(sourceDesc).getId();
        target = getDecision(targetDesc).getId();
        dir = "both";
        break;
      // switch of source and target
      case "backwards":
        source = getDecision(targetDesc).getId();
        target = getTask(sourceDesc).getId();
        dir = "auto";
        break;
    // no default always has to have a specified direction
    }
    // add new task relation
    TaskRelation tr = new TaskRelation(source, target, dir);
    influencingTasks.add(tr);
  }

  /**
   * Retrieves decision point by name.
   * 
   * @param decisionPointName name of desired decision point
   * @return decision point or null if decision point does not exist
   */
  public DecisionPoint getDecisionPoint(String decisionPointName) {
    for (DecisionPoint decisionPoint : decisionPoints) {
      if (decisionPoint.getLabel().equals(decisionPointName)) {
        return decisionPoint;
      }
    }
    return null;
  }

  /**
   * Retrieves decision by name
   * 
   * @param decisionName name of desired decision
   * @return decision or null if decision does not exist
   */
  private Decision getDecision(String decisionName) {
    for (DecisionPoint dp : decisionPoints) {
      Decision dec = dp.getDecision(decisionName);
      if (dec != null) {
        return dec;
      }
    }
    return null;
  }

  /**
   * Retrieves decision by id
   * 
   * @param decisionName name of deisred decision
   * @return decision or null if decision does not exist
   */
  private Decision getDecision(int decisionId) {
    for (DecisionPoint dp : decisionPoints) {
      Decision dec = dp.getDecision(decisionId);
      if (dec != null) {
        return dec;
      }
    }
    return null;
  }

  /**
   * Retrieves Outcome by name
   * 
   * @param outcomeName name of desired outcome
   * @return outcome or null if outcome does not exist
   */
  private Outcome getOutcome(String outcomeName) {
    for (DecisionPoint dp : decisionPoints) {
      for (Decision d : dp.getDecisions()) {
        Outcome out = d.getOutcome(outcomeName);
        if (out != null) {
          return out;
        }
      }
    }
    return null;
  }

  /**
   * Retrieves Outcome by id
   * 
   * @param outcomeName name of desired outcome
   * @return outcome or null if outcome does not exist
   */
  private Outcome getOutcome(int outcomeId) {
    for (DecisionPoint dp : decisionPoints) {
      for (Decision d : dp.getDecisions()) {
        Outcome out = d.getOutcome(outcomeId);
        if (out != null) {
          return out;
        }
      }
    }
    return null;
  }

  /**
   * Retrieves task by name
   * 
   * @param taskName name of desired task
   * @return task or null if task does not exist
   */
  private Task getTask(String taskName) {
    for (Task t : tasks) {
      if (t.getLabel().equals(taskName)) {
        return t;
      }
    }
    return null;
  }

  /**
   * Sort of all relations to produce sorted output in id ascending order of the source.
   */
  public void sortLists() {
    RelationComparator rc = new RelationComparator();
    Collections.sort(influencingDecisions, rc);
    Collections.sort(influencingOutcomes, rc);
    Collections.sort(influencingTasks, rc);
  }

  /**
   * Sort of all entities of the CloudDSF to produce sorted output depending on id in ascending
   * order.
   */
  public void sortEntities() {
    CloudDSFEntityComparator cec = new CloudDSFEntityComparator();
    Collections.sort(decisionPoints, cec);
    Collections.sort(tasks, cec);
    for (DecisionPoint dp : decisionPoints) {
      for (Decision d : dp.getDecisions()) {
        d.sortOutcomes();
      }
      dp.sortDecisions();
    }
  }

  /**
   * Sorts influencing Relations.
   */
  public void sortInfluencingRelations() {
    Collections.sort(influencingRelations, new RelationComparator());
  }

  public void addDecisionPoint(DecisionPoint dp) {
    decisionPoints.add(dp);
  }

  public void addTask(Task task) {
    tasks.add(task);
  }

  @JsonIgnore
  public List<TaskRelation> getInfluencingTasks() {
    return influencingTasks;
  }

  @JsonIgnore
  public List<OutcomeRelation> getInfluencingOutcomes() {
    return influencingOutcomes;
  }

  @JsonProperty("children")
  public List<DecisionPoint> getDecisionPoints() {
    return decisionPoints;
  }

  @JsonIgnore
  public List<DecisionRelation> getInfluencingDecisions() {
    return influencingDecisions;
  }

  @JsonIgnore
  public List<Task> getTasks() {
    return tasks;
  }

  @JsonIgnore
  public List<Relation> getInfluencingRelations() {
    return influencingRelations;
  }

  /**
   * Adds all relations from the task, decision and outcome list to the relations list for CloudDSF.
   */
  public void setInfluencingRelations() {
    influencingRelations.clear();
    influencingRelations.addAll(influencingDecisions);
    influencingRelations.addAll(influencingTasks);
    influencingRelations.addAll(influencingOutcomes);
    sortInfluencingRelations();
  }

  /**
   * Helper Method to print out content of cloudDSF Object to check content.
   */
  public void printCloudDSF() {

    // counter for entities
    int dpamount = 0;
    int damount = 0;
    int oamount = 0;
    int tamount = 0;

    // counter for relations
    int decRelations = 0;
    int taskRelations = 0;
    int outRelationsAmount = 0;

    int allRelations = 0;
    int inRelations = 0;
    int exRelations = 0;
    int affRelations = 0;
    int binRelations = 0;
    int dpaRelations = 0;
    int dpinRelations = 0;
    int dpexRelations = 0;
    int dpaffRelations = 0;
    int dpbinRelations = 0;

    int dreqRelations = 0;
    int dinfRelations = 0;
    int daffRelations = 0;
    int dbinRelations = 0;
    int dpDecReqRelations = 0;
    int dpDecinfRelations = 0;
    int dpdecaffRelations = 0;
    int dpdecbinRelations = 0;

    for (DecisionPoint dp : getDecisionPoints()) {
      // counter for relations per decision point
      dpaRelations = 0;
      dpinRelations = 0;
      dpexRelations = 0;
      dpaffRelations = 0;
      dpbinRelations = 0;

      dpDecReqRelations = 0;
      dpDecinfRelations = 0;
      dpdecaffRelations = 0;
      dpdecbinRelations = 0;

      dpamount++;
      System.out.println("Decision Point Name = " + dp.getLabel() + " ID " + dp.getId());
      for (Decision d : dp.getDecisions()) {
        // counter for relations per decision
        allRelations = 0;
        inRelations = 0;
        exRelations = 0;
        affRelations = 0;
        binRelations = 0;

        dreqRelations = 0;
        dinfRelations = 0;
        daffRelations = 0;
        dbinRelations = 0;

        damount++;

        for (Outcome o : d.getOutcomes()) {
          oamount++;
          for (OutcomeRelation outRelation : influencingOutcomes) {
            if (outRelation.getSource() == o.getId()) {
              switch (outRelation.getType()) {
                case "in":
                  inRelations++;
                  dpinRelations++;
                  break;
                case "ex":
                  exRelations++;
                  dpexRelations++;
                  break;
                case "eb":
                  binRelations++;
                  dpbinRelations++;
                  break;
                case "aff":
                  affRelations++;
                  dpaffRelations++;
                  break;
                case "a":
                  allRelations++;
                  dpaRelations++;
                  break;
                default:
                  break;
              }
            }
          }
        }

        for (DecisionRelation decRelation : influencingDecisions) {
          if (decRelation.getSource() == d.getId()) {
            switch (decRelation.getType()) {
              case "Influencing":
                dinfRelations++;
                dpDecinfRelations++;
                break;
              case "Binding":
                dbinRelations++;
                dpdecbinRelations++;
                break;
              case "Affecting":
                daffRelations++;
                dpdecaffRelations++;
                break;
              case "Requiring":
                dreqRelations++;
                dpDecReqRelations++;
                break;
              default:
                break;
            }
          }
        }

        System.out.println("Decision " + d.getLabel() + " has:");
        System.out.println("Affecting: " + daffRelations);
        System.out.println("Binding: " + dbinRelations);
        System.out.println("Influencing: " + dinfRelations);
        System.out.println("Requiring: " + dreqRelations);
        System.out.println("#### Corresponding Outcome Relations ####");

        System.out.println("Including: " + inRelations);
        System.out.println("Excluding: " + exRelations);
        System.out.println("Allowing: " + allRelations);
        System.out.println("Affecting: " + affRelations);
        System.out.println("Binding: " + binRelations);

      }
      System.out.println("Decision Point " + dp.getLabel() + " has:");
      System.out.println("Affecting: " + dpdecaffRelations);
      System.out.println("Binding: " + dpdecbinRelations);
      System.out.println("Influencing: " + dpDecinfRelations);
      System.out.println("Requiring: " + dpDecReqRelations);

      System.out.println("#### Corresponding Outcome Relations ####");

      System.out.println("Including: " + dpinRelations);
      System.out.println("Excluding: " + dpexRelations);
      System.out.println("Allowing: " + dpaRelations);
      System.out.println("Affecting: " + dpaffRelations);
      System.out.println("Binding: " + dpbinRelations);
    }
    for (Task t : tasks) {
      System.out.println("Task " + t.getLabel() + " " + t.getId());
      tamount++;
    }

    for (DecisionRelation decRelation : influencingDecisions) {
      System.out.println("Decision Relationship from " + decRelation.getSource() + " to "
          + decRelation.getTarget() + " with 'type' ");
      decRelations++;
    }

    allRelations = 0;
    inRelations = 0;
    exRelations = 0;
    affRelations = 0;
    binRelations = 0;

    for (OutcomeRelation outRelation : influencingOutcomes) {
      switch (outRelation.getType()) {
        case "in":
          inRelations++;
          break;
        case "ex":
          exRelations++;
          break;
        case "eb":
          binRelations++;
          break;
        case "aff":
          affRelations++;
          break;
        case "a":
          allRelations++;
          break;
        default:
          break;
      }
      outRelationsAmount++;
    }
    System.out.println("Total outcome Relations " + allRelations + "a Relations " + exRelations
        + "ex Relations " + inRelations + "in Relations " + binRelations + "binding Relations "
        + affRelations + "aff Relations");

    for (TaskRelation taskRelation : influencingTasks) {
      System.out.println("Task Relationship from " + taskRelation.getSource() + " to "
          + taskRelation.getTarget() + " with type " + taskRelation.getDir());
      taskRelations++;
    }
    System.out.println("##################################");
    System.out.println("#Decision Points = " + dpamount);
    System.out.println("#Decision = " + damount);
    System.out.println("#Number Outcomes = " + oamount);
    System.out.println("#Number Tasks = " + tamount);
    System.out.println("#Relations between Decisions = " + decRelations);
    System.out.println("#Relations between Outcomes = " + outRelationsAmount);
    System.out.println("#Relations between Tasks and Decision = " + taskRelations);
  }

  /**
   * Executes all verification methods and returns the result.
   * 
   * @return true if all checks are successfull otherwise false
   */
  public boolean checkSanity() {
    return (checkAffBinDecRelations("affecting", "binding")
        && checkAffBinDecRelations("binding", "affecting") && checkAffBinOutRelations("aff", "eb")
        && checkAffBinOutRelations("eb", "aff") && checkDecRelComb() && checkDecRelForOutRel()
        && checkInAOutRelations("in", "a", "in") && checkInAOutRelations("a", "a", "in")
        && checkOutRelAmountForDecRel() && checkOutRelTypeForDecRel() && checkRelTypesDecisions()
        && checkRelTypesOutcomes() && checkSingleOutcomeRel() && checkXOROutcomes());
  }

  /**
   * Checks if only valid decision relationship types are present.
   * 
   * @return
   */
  public boolean checkRelTypesDecisions() {
    for (DecisionRelation decRel : influencingDecisions) {
      switch (decRel.getType()) {
        case "influencing":
          break;
        case "requiring":
          break;
        case "affecting":
          break;
        case "binding":
          break;
        default:
          System.out.println("Fail: Wrong decision relation type " + decRel.getType()
              + " found from " + decRel.getSource() + " to " + decRel.getTarget());
          return false;
      }
    }
    System.out.println("Success: All decision relationship types are valid");
    return true;
  }

  /**
   * Checks if only valid outcome relationship types are present.
   * 
   * @return
   */
  public boolean checkRelTypesOutcomes() {
    for (OutcomeRelation outRel : influencingOutcomes) {
      switch (outRel.getType()) {
        case "eb":
          break;
        case "in":
          break;
        case "a":
          break;
        case "ex":
          break;
        case "aff":
          break;
        default:
          System.out.println("Fail: Wrong outcome relation type " + outRel.getType()
              + " found from " + outRel.getSource() + " to " + outRel.getTarget());
          return false;
      }
    }
    System.out.println("Success: All outcome relationship types are valid");
    return true;
  }

  /**
   * Checks if two or more relations between the same decisions exists and if so if they are in the
   * correct combinations.
   * 
   * @return
   */
  public boolean checkDecRelComb() {
    for (DecisionRelation decRel : influencingDecisions) {
      // type of first decisions
      String relType = decRel.getType();
      for (DecisionRelation decRelComp : influencingDecisions) {
        if (decRelComp.getSource() == decRel.getSource()
            && decRelComp.getTarget() == decRel.getTarget()) {
          // another relation exists between the same two decisions
          String relTypeComp = decRelComp.getType();
          // both relations have the same type thus it is the same
          // relations (or a duplicate)
          if (relType.equals(relTypeComp)) {
            continue;
          } else {
            // initial relation is requiring and compared relations
            // is one of the other
            if (relType.equals("requiring")
                && (relTypeComp.equals("influencing") || relTypeComp.equals("affecting") || relTypeComp
                    .equals("binding"))) {
              continue;
            }
            // compared relationship is requiring and the initial
            // rel is one of the other types
            else if (relTypeComp.equals("requiring")
                && (relType.equals("influencing") || relType.equals("affecting") || relType
                    .equals("binding"))) {
              continue;
            }
            // both relations are not requiring and thus an error
            // exists
            else {
              System.out.println("Fail: Wrong decision relation combination between "
                  + decRel.getSource() + " to " + decRel.getTarget() + " with relation type "
                  + relType + " and " + relTypeComp);
              return false;
            }
          }
        }
        // otherweise relation is not between same decisions and is skipped
      }
    }
    System.out
        .println("Success: All decision relations are only existing in the only valid combination (requiring + any other)");
    return true;
  }

  /**
   * Checks if the exact amount of relations are present for a decision relation.
   * 
   * @return
   */
  public boolean checkOutRelAmountForDecRel() {
    for (DecisionRelation decRel : influencingDecisions) {
      if (decRel.getType().equals("requiring") == false) {
        // set source and target outcome to check
        Decision sourceDecision = getDecision(decRel.getSource());
        Decision targetDecision = getDecision(decRel.getTarget());
        // traverse starting outcomes
        int foundRelations =
            sourceDecision.getOutcomes().size() * targetDecision.getOutcomes().size();
        for (Outcome outSource : sourceDecision.getOutcomes()) {
          // traverse target outcomes
          for (Outcome outTarget : targetDecision.getOutcomes()) {
            // traverse outcomeRelations
            for (OutcomeRelation outRel : influencingOutcomes) {
              // if source and target are found than a
              // corresponding
              // relation exists
              if (outSource.getId() == outRel.getSource()
                  && outTarget.getId() == outRel.getTarget()) {
                foundRelations--;
              }
            }
          }
        }
        if (foundRelations != 0) {
          System.out.println("Fail: There are " + foundRelations + " missing outcome relations");
          return false;
        }
      }
    }
    System.out
        .println("Success: There are the exactly necessary amount of relations between outcomes per decision relation");
    return true;
  }

  /**
   * Checks for every outcome relation if the decicions have relationship as well.
   * 
   * @return
   */
  public boolean checkDecRelForOutRel() {
    // iterate over all outcome relations
    for (OutcomeRelation outRel : influencingOutcomes) {
      // get decision for outcome relation
      Decision decSource = getDecision(getOutcome(outRel.getSource()).getParent());
      Decision decTarget = getDecision(getOutcome(outRel.getTarget()).getParent());
      boolean found = false;
      // check decision relations if relationship exists
      for (DecisionRelation decRel : influencingDecisions) {
        if (decSource.getId() == decRel.getSource() && decTarget.getId() == decRel.getTarget()) {
          found = true;
          break;
        }
      }
      if (!found) {
        // no relation between decision for an outcome relation was found
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the correct relationship types between outcomes are present according to the decision
   * relationship type.
   * 
   * @return
   */
  public boolean checkOutRelTypeForDecRel() {
    for (DecisionRelation decRel : influencingDecisions) {
      if (decRel.getType().equals("requiring") == false) {
        // set source and target outcome to check
        Decision sourceDecision = getDecision(decRel.getSource());
        Decision targetDecision = getDecision(decRel.getTarget());
        // traverse starting outcomes
        for (Outcome outSource : sourceDecision.getOutcomes()) {
          // traverse target outcomes
          for (Outcome outTarget : targetDecision.getOutcomes()) {
            // traverse outcomeRelations
            for (OutcomeRelation outRel : influencingOutcomes) {
              // if source and target are found than a
              // corresponding relation exists
              if (outSource.getId() == outRel.getSource()
                  && outTarget.getId() == outRel.getTarget()) {
                if (decRel.getType().equals("affecting")) {
                  if (outRel.getType().equals("aff") == false) {
                    System.out.println("Fail: An outcome relation (" + outRel.getType()
                        + ") does not match with its affecting decision relation");
                    return false;
                  }
                } else if (decRel.getType().equals("binding")) {
                  if (outRel.getType().equals("eb") == false) {
                    System.out.println("Fail: An outcome relation (" + outRel.getType()
                        + ") does not match with its binding decision relation");
                    return false;
                  }
                } else if (decRel.getType().equals("influencing")) {
                  if (outRel.getType().equals("aff") || outRel.getType().equals("eb")) {
                    System.out.println("Fail: An outcome relation (" + outRel.getType()
                        + ") does not match with its influecing decision relation");
                    return false;
                  }
                }
              }
            }
          }
        }
      }
    }
    System.out
        .println("Success: All outcome relations are valid corresponding to their decision relation");
    return true;
  }

  /**
   * Checks in case an affecting relation exists into one direction that a binding exists into the
   * other way around and vice versa for decisions
   * 
   * @param type1 relationship type to check against (e.g. aff)
   * @param type2 second relationship type (e.g. eb)
   * @return
   */
  public boolean checkAffBinDecRelations(String type1, String type2) {
    int aff = 0;
    int bin = 0;
    for (DecisionRelation decRel : influencingDecisions) {
      // filter affecting relations only
      if (decRel.getType().equals(type1)) {
        aff++;
        for (DecisionRelation decRelComp : influencingDecisions) {
          // find relation for reverse case
          if (decRel.getSource() == decRelComp.getTarget()
              && decRel.getTarget() == decRelComp.getSource()) {
            if (decRelComp.getType().equals(type2)) {
              bin++;
              break;
            }
          }
        }
      }
    }
    if (aff != bin) {
      // unequal amount of binding and affecting relations for decisions
      System.out.println("Fail: There are not the same amount of " + type1 + " to " + type2
          + " decision relations");
      return false;
    }
    System.out.println("Success: There are the same amount of " + type1 + " to " + type2
        + " decisions relations");
    return true;
  }

  /**
   * Checks if affecting rel exists into one direction that a binding exists into the other way
   * around and vice versa for outcomes
   * 
   * @param type1 relationship type to check against (e.g. aff)
   * @param type2 second relationship type (e.g. eb)
   * @return
   */
  public boolean checkAffBinOutRelations(String type1, String type2) {
    int aff = 0;
    int bin = 0;
    for (OutcomeRelation outRel : influencingOutcomes) {
      // filter affecting relations only
      if (outRel.getType().equals(type1)) {
        aff++;
        for (OutcomeRelation outRelComp : influencingOutcomes) {
          // find relation for reverse case
          if (outRel.getSource() == outRelComp.getTarget()
              && outRel.getTarget() == outRelComp.getSource()) {
            if (outRelComp.getType().equals(type2)) {
              bin++;
              break;
            }
          }
        }
      }
    }
    if (aff != bin) {
      // unequal amount of binding and affecting relations
      System.out.println("Fail: There are not the same amount of " + type1 + " to " + type2
          + " outcome relations");
      return false;
    }
    System.out.println("Success: There are the same amount of " + type1 + " to " + type2
        + " outcome relations");
    return true;
  }

  /**
   * Checks if in or a outcome relation from a to b has an in or a relation in the reverse case as
   * well.
   * 
   * @param type1 first relationship type that others are compared with
   * @param type2 relationship type that is allowed with type1
   * @param type3 relationship type that is allowed with type1
   * @return
   */
  public boolean checkInAOutRelations(String type1, String type2, String type3) {
    for (OutcomeRelation outRel : influencingOutcomes) {
      if (outRel.getType().equals(type1)) {
        for (OutcomeRelation outRelComp : influencingOutcomes) {
          // find relation for reverse case
          if (outRel.getSource() == outRelComp.getTarget()
              && outRel.getTarget() == outRelComp.getSource()) {
            if (outRelComp.getType().equals(type2) || outRelComp.getType().equals(type3)) {
              break;
            } else {
              System.out
                  .println("Fail: There is a conflict between two (vice versa) outcome relationship types");
              return false;
            }
          }
        }
      }
    }
    System.out
        .println("Success: All outcome relations do not have an ex to in/a relation combination in the reverse case");
    return true;
  }

  /**
   * Checks if outcomes have a relation to themselves or towards outcomes of the same decision.
   * 
   * @return
   */
  public boolean checkXOROutcomes() {
    // iterate all outcomes
    for (DecisionPoint decisionPoint : decisionPoints) {
      for (Decision decision : decisionPoint.getDecisions()) {
        for (Outcome outcome : decision.getOutcomes()) {
          // iterate over outcome relations for each outcome
          for (OutcomeRelation outRel : influencingOutcomes) {
            if (outcome.getId() == outRel.getSource()) {
              if (outcome.getId() == outRel.getTarget()) {
                // if target and source are equal
                System.out.println("Fail: Outcome has a relation to itself");
                return false;
              } else if (outcome.getParent() == getOutcome(outRel.getTarget()).getParent()) {
                // parental decision of both outcomes are similar
                System.out.println("Fail: Outcome has relations towards outcome of same decision");
                return false;
              }
            }
          }
        }
      }
    }
    System.out.println("Success: All outcomes satisfy the XOR rule");
    return true;
  }

  /**
   * Checks if an outcome has multiple relations towards another outcome.
   * 
   * @return
   */
  public boolean checkSingleOutcomeRel() {
    // traverse all outcomes
    for (DecisionPoint decisionPoint : decisionPoints) {
      for (Decision decision : decisionPoint.getDecisions()) {
        for (Outcome outcome : decision.getOutcomes()) {
          // set for target Ids
          Set<Integer> uniqueTargets = new HashSet<Integer>();
          for (OutcomeRelation outRel : influencingOutcomes) {
            if (outcome.getId() == outRel.getSource()) {
              // if adding returns false value is already in set thus
              // a target has been twice in the target list for the respective outcome.
              if (uniqueTargets.add(outRel.getTarget()) == false) {
                System.out
                    .println("Fail: One outcome has several relations towards another outcome");
                return false;
              }
            }
          }
        }
      }
    }
    System.out.println("Success: All outcomes have only one relation towards other outcomes");
    return true;
  }
}
