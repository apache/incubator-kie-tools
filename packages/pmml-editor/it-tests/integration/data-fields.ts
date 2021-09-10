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

describe("Data Fields Test", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.pmmlEditor.dev.port}/`);
    cy.newButtonPMML().click();
  });

  it("Create a data type (integer) - same pmml as in fixture: empty-characteristics-DD-defined.pmml", () => {
    cy.ouiaId("model-name").click();
    cy.ouiaId("set-model-name").type("{selectall}{del}EmptyModelWithData");

    cy.buttonDataDictionary().click();

    cy.ouiaId("dd-toolbar")
      .should("be.visible")
      .within(() => {
        cy.ouiaId("add-data-type").click();
      });
    cy.ouiaId("dd-types-list")
      .should("be.visible")
      .within(() => {
        cy.ouiaType("field-type").find("button").click();
        cy.ouiaType("select-option").contains("integer").click();
        cy.ouiaType("field-optype").find("button").click();
        cy.ouiaType("select-option").contains("ordinal").click();
        cy.ouiaType("field-name").find("input").type("test");
      });
    cy.ouiaId("dd-toolbar").click();
    cy.get("button[data-title='DataDictionaryModalClose']").click();

    cy.buttonMiningSchema().click();
    cy.ouiaId("mining-toolbar")
      .should("be.visible")
      .within(() => {
        cy.ouiaId("select-mining-field").find("input").click();
        cy.ouiaId("select-mining-field").find("button:contains('test')").click();
        cy.ouiaId("add-mining-field").click();
      });
    cy.get("button[data-title='MiningSchemaModalClose']").click();

    cy.buttonOutputs().click();
    cy.ouiaId("outputs-toolbar")
      .should("be.visible")
      .within(() => {
        cy.ouiaId("add-output").click();
      });

    cy.ouiaId("outputs-overview")
      .should("be.visible")
      .within(() => {
        cy.ouiaType("select-output-field-type").click();
        cy.ouiaType("select-option").contains("integer").click();
        cy.ouiaType("set-output-field-name").type("{selectall}{del}output");
      });
    cy.ouiaId("outputs-toolbar").click();
    cy.get("button[data-title='OutputsModalClose']").click();

    cy.buttonPMML()
      .click()
      .editorShouldContains("empty-characteristics-DD-defined.pmml")
      .ouiaId("pmml-modal-confirm")
      .click();
  });
});
