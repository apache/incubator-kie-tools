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
import { BeePropertiesPanelBase } from "./bee/beePropertiesPanelBase";
import { NameAndDataTypeCell } from "@kie-tools/boxed-expression-component/tests-e2e/api/nameAndDataTypeCell";

export class BeePropertiesPanel extends BeePropertiesPanelBase {
  public decisionTableInputHeader: DecisionTableInputHeaderPropertiesPanel;
  public decisionTableInputRule: DecisionTableInputRulePropertiesPanel;
  public decisionTableOutputHeader: DecisionTableOutputHeaderPropertiesPanel;
  public decisionTableOutputRule: DecisionTableOutputRulePropertiesPanel;

  constructor(
    public diagram: Diagram,
    public page: Page,
    public baseURL?: string
  ) {
    super(diagram, page);
    this.decisionTableInputHeader = new DecisionTableInputHeaderPropertiesPanel(diagram, page);
    this.decisionTableInputRule = new DecisionTableInputRulePropertiesPanel(diagram, page);
    this.decisionTableOutputHeader = new DecisionTableOutputHeaderPropertiesPanel(diagram, page);
    this.decisionTableOutputRule = new DecisionTableOutputRulePropertiesPanel(diagram, page);
  }

  get expressionHeaderCell() {
    return new NameAndDataTypeCell(this.page.getByRole("columnheader", { name: "New BKM (<Undefined>)" }));
  }

  public async setDescription(args: { newDescription: string }) {
    const descriptionTextArea = this.panel().getByPlaceholder("Enter a description...");
    await descriptionTextArea.focus();
    await this.page.keyboard.type(args.newDescription);
    await descriptionTextArea.press("Tab");
  }

  public async getDescription() {
    return await this.panel().getByPlaceholder("Enter a description...").inputValue();
  }

  public async setQuestion(args: { newQuestion: string }) {
    const questionTextArea = this.panel().getByPlaceholder("Enter a question...");
    await questionTextArea.focus();
    await this.page.keyboard.type(args.newQuestion);
    await questionTextArea.press("Tab");
  }

  public async getQuestion() {
    return await this.panel().getByPlaceholder("Enter a question...").inputValue();
  }

  public async setAllowedAnswers(args: { newAllowedAnswers: string }) {
    const allowedAnswersTextArea = this.panel().getByPlaceholder("Enter allowed answers...");
    await allowedAnswersTextArea.focus();
    await this.page.keyboard.type(args.newAllowedAnswers);
    await allowedAnswersTextArea.press("Tab");
  }

  public async getAllowedAnswers() {
    return await this.panel().getByPlaceholder("Enter allowed answers...").inputValue();
  }

  public async open() {
    await this.page.getByTitle("Properties panel").click();
  }
}
