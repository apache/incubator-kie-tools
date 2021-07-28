/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as buildEnv from "@kogito-tooling/build-env";

describe("New File basic user's interaction", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.pmmlEditor.dev.port}/`);
    cy.newButtonPMML().click();
  });

  it("Menu is visible", () => {
    cy.buttonUndo().contains("Undo").should("be.visible");

    cy.buttonRedo().contains("Redo").should("be.visible");

    cy.buttonPMML().contains("PMML").should("be.visible");

    cy.buttonValidation().contains("Validate").should("be.visible");
  });

  it("Definition of DataSet is visible", () => {
    cy.buttonDataDictionary().contains("Set Data Dictionary").should("be.visible");

    cy.buttonMiningSchema().contains("Set Mining Schema").should("be.visible");

    cy.buttonOutputs().contains("Set Outputs").should("be.visible");
  });

  it("Validation is visible", () => {
    cy.buttonOutputs().get("span.pf-c-button__icon").should("be.visible");

    cy.get("span.pf-m-orange>span.pf-c-label__content").get("span.pf-c-label__icon").should("be.visible");
  });
});
