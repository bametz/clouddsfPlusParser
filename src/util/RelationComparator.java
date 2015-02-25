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

package util;

import cloudDSF.Relation;

import java.util.Comparator;

/**
 * Comparator to compare Relations by their ascending source id.
 * 
 * @author Metz
 *
 */
public class RelationComparator implements Comparator<Relation> {

  @Override
  public int compare(Relation r1, Relation r2) {
    int diff = r1.getSource() - r2.getSource();
    if (diff < 0) {
      return -1;
    }
    if (diff > 0) {
      return 1;
    } else {
      return 0;
    }
  }
}
