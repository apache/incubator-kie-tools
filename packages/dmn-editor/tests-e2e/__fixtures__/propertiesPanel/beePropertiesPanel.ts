/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Page } from "@playwright/test";
import { DecisionTableInputHeaderPropertiesPanel } from "./bee/decisionTableInputHeaderPropertiesPanel";
import { DecisionTableInputRulePropertiesPanel } from "./bee/decisionTableInputRulePropertiesPanel";
import { DecisionTableOutputHeaderPropertiesPanel } from "./bee/decisionTableOutputHeaderPropertiesPanel";
import { DecisionTableOutputRulePropertiesPanel } from "./bee/decisionTableOutputRulePropertiesPanel";
import { Diagram } from "../diagram";
import { DecisionPropertiesPanel } from "./bee/decisionPropertiesPanel";
import { BkmPropertiesPanel } from "./bee/bkmPropertiesPanel";

export class BeePropertiesPanel {
  public decisionTableInputHeader: DecisionTableInputHeaderPropertiesPanel;
  public decisionTableInputRule: DecisionTableInputRulePropertiesPanel;
  public decisionTableOutputHeader: DecisionTableOutputHeaderPropertiesPanel;
  public decisionTableOutputRule: DecisionTableOutputRulePropertiesPanel;
  public decisionNode: DecisionPropertiesPanel;
  public bkmNode: BkmPropertiesPanel;

  constructor(
    public diagram: Diagram,
    public page: Page,
    public baseURL?: string
  ) {
    this.decisionTableInputHeader = new DecisionTableInputHeaderPropertiesPanel(diagram, page);
    this.decisionTableInputRule = new DecisionTableInputRulePropertiesPanel(diagram, page);
    this.decisionTableOutputHeader = new DecisionTableOutputHeaderPropertiesPanel(diagram, page);
    this.decisionTableOutputRule = new DecisionTableOutputRulePropertiesPanel(diagram, page);
    this.decisionNode = new DecisionPropertiesPanel(diagram, page);
    this.bkmNode = new BkmPropertiesPanel(diagram, page);
  }

  public async open() {
    await this.page.getByTitle("Properties panel").click();
  }
}
