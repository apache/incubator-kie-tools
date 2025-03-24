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
import { BeePropertiesPanelBase } from "./beePropertiesPanelBase";
import { DescriptionProperties } from "../parts/descriptionProperties";
import { QuestionProperties } from "../parts/questionProperties";
import { AllowedAnswersProperties } from "../parts/allowedAnswersProperties";
import { Diagram } from "../../diagram";

export class DecisionPropertiesPanel extends BeePropertiesPanelBase {
  private descriptionProperties: DescriptionProperties;
  private questionProperties: QuestionProperties;
  private allowedAnswersProperties: AllowedAnswersProperties;

  constructor(
    public diagram: Diagram,
    public page: Page
  ) {
    super(diagram, page);
    this.descriptionProperties = new DescriptionProperties(this.panel());
    this.questionProperties = new QuestionProperties(this.panel());
    this.allowedAnswersProperties = new AllowedAnswersProperties(this.panel());
  }

  public async setDescription(args: { newDescription: string }) {
    await this.descriptionProperties.setDescription({ ...args });
  }

  public async getDescription() {
    return await this.descriptionProperties.getDescription();
  }

  public async setQuestion(args: { newQuestion: string }) {
    await this.questionProperties.setQuestion({ ...args });
  }

  public async getQuestion() {
    return await this.questionProperties.getQuestion();
  }

  public async setAllowedAnswers(args: { newAllowedAnswers: string }) {
    await this.allowedAnswersProperties.setAllowedAnswers({ ...args });
  }

  public async getAllowedAnswers() {
    return await this.allowedAnswersProperties.getAllowedAnswers();
  }
}
