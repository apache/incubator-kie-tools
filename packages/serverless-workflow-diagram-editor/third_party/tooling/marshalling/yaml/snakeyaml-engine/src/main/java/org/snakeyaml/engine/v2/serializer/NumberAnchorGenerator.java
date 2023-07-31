/*
 * Copyright (c) 2018, SnakeYAML
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
package org.snakeyaml.engine.v2.serializer;

import org.snakeyaml.engine.v2.common.Anchor;
import org.snakeyaml.engine.v2.nodes.Node;

/** Simple generate of the format id + number */
public class NumberAnchorGenerator implements AnchorGenerator {

  private int lastAnchorId = 0;

  /**
   * Create
   *
   * @param lastAnchorId - the number to start from
   */
  public NumberAnchorGenerator(int lastAnchorId) {
    this.lastAnchorId = lastAnchorId;
  }

  /**
   * Create value increasing the number
   *
   * @param node - ignored
   * @return value with format 'id001'
   */
  public Anchor nextAnchor(Node node) {
    throw new UnsupportedOperationException("Anchors are not supported in JS");
    /*    this.lastAnchorId++;
    java.text.NumberFormat format = java.text.NumberFormat.getNumberInstance();
    format.setMinimumIntegerDigits(3);
    format.setMaximumFractionDigits(0);// issue 172
    format.setGroupingUsed(false);
    String anchorId = format.format(this.lastAnchorId);
    return new Anchor("id" + anchorId);*/
  }
}
