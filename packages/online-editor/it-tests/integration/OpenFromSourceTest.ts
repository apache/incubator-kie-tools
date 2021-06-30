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

describe("Open from source test", () => {
  const SAMPLES_URL: string =
    "https://raw.githubusercontent.com/kiegroup/kogito-tooling/main/packages/online-editor/it-tests/fixtures/";

  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.onlineEditor.dev.port}/`);
  });

  it("should open BPMN file from GitHub url", () => {
    // open BPMN file from github url
    cy.get("[data-ouia-component-id='open-from-source-button']").should("be.disabled");
    cy.get("[data-ouia-component-id='url-input']").type(SAMPLES_URL + "testProcess.bpmn");
    cy.get("[data-ouia-component-id='url-input']").should("have.value", SAMPLES_URL + "testProcess.bpmn");
    cy.get("[data-ouia-component-id='open-from-source-button']", { timeout: 15000 }).should("be.enabled");
    cy.get("[data-ouia-component-id='open-from-source-button']").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // check editor logo
    cy.get("[class='pf-c-brand']").within(($logo) => {
      expect($logo.attr("src")).contain("bpmn");
      expect($logo.attr("alt")).contain("bpmn");
    });

    // check editor title name
    cy.get("[aria-label='Edit file name']").should("have.value", "testProcess");

    cy.getEditor().within(() => {
      // open properties panel and check values
      cy.get("[data-title='Properties']").click();
      cy.get("[name$='diagramSet.name']").should("have.value", "Test process");
      cy.get("[name$='diagramSet.packageProperty']").should("have.value", "org.kie");
      cy.get("[name$='diagramSet.id']").should("have.value", "test-process");

      // open diagram panel and check nodes
      cy.get("[data-title='Explore Diagram']").click();
      cy.get("a.gwt-Anchor").should(($nodes) => {
        expect($nodes).length(2);
        expect($nodes.eq(0)).text("Test process");
        expect($nodes.eq(1)).text("Start test node");
      });
    });
  });

  it("should open DMN file from GitHub url", () => {
    // open DMN file from github url
    cy.get("[data-ouia-component-id='open-from-source-button']").should("be.disabled");
    cy.get("[data-ouia-component-id='url-input']").type(SAMPLES_URL + "testModel.dmn");
    cy.get("[data-ouia-component-id='url-input']").should("have.value", SAMPLES_URL + "testModel.dmn");
    cy.get("[data-ouia-component-id='open-from-source-button']", { timeout: 15000 }).should("be.enabled");
    cy.get("[data-ouia-component-id='open-from-source-button']").click();

    // wait until loading dialog disappears
    cy.loadEditor();

    // check editor logo
    cy.get("[class='pf-c-brand']").within(($logo) => {
      expect($logo.attr("src")).contain("dmn");
      expect($logo.attr("alt")).contain("dmn");
    });

    // check editor title name
    cy.get("[aria-label='Edit file name']").should("have.value", "testModel");

    // close DMN guided tour dialog
    cy.get("[data-ouia-component-id='dmn-guided-tour'] button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open properties panel and check values
      cy.get("[data-title='Properties']").click();
      cy.get("[name$='definitions.nameHolder']").should("have.value", "Test model");
      cy.get("[name$='definitions.description']").should("have.value", "This is test model.");
      cy.get("[data-title='Properties']").click();

      // open decision navigator and check nodes
      cy.get("[data-ouia-component-id='collapsed-docks-bar-W'] > button").click();
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").should(($nodes) => {
        expect($nodes).length(2);
        expect($nodes.eq(0)).attr("title", "Test model");
        expect($nodes.eq(1)).attr("title", "Test input data");
      });
    });
  });

  it("should open PMML file from GitHub url", () => {
    // open PMML file from github url
    cy.get("[data-ouia-component-id='open-from-source-button']").should("be.disabled");
    cy.get("[data-ouia-component-id='url-input']").type(SAMPLES_URL + "testScoreCard.pmml");
    cy.get("[data-ouia-component-id='url-input']").should("have.value", SAMPLES_URL + "testScoreCard.pmml");
    cy.get("[data-ouia-component-id='open-from-source-button']", { timeout: 15000 }).should("be.enabled");
    cy.get("[data-ouia-component-id='open-from-source-button']").click();

    // load pmml editor
    cy.getEditor().within(() => {
      cy.get("[data-testid='editor-page']", { timeout: 60000 }).should("be.visible");
    });

    // check editor logo
    cy.get("[class='pf-c-brand']").within(($logo) => {
      expect($logo.attr("src")).contain("pmml");
      expect($logo.attr("alt")).contain("pmml");
    });

    // check editor title name
    cy.get("[aria-label='Edit file name']").should("have.value", "testScoreCard");

    cy.getEditor().within(() => {
      // check model name
      cy.get(".modelTitle__truncate").should("have.text", "Test model");

      // check characteristics
      cy.get(".characteristics-container div > strong").should(($characteristics) => {
        expect($characteristics).length(1);
        expect($characteristics.eq(0)).text("Test Characteristic");
      });

      // open, check and close PMML DataDictionary modal
      cy.get("[data-title='DataDictionary']").click();
      cy.get(".data-type-item__name").should(($dataTypes) => {
        expect($dataTypes).length(1);
        expect($dataTypes.eq(0)).text("Test Data Type");
      });
      cy.get("[data-title='DataDictionaryModalClose']").click();

      // open and close PMML MiningSchema modal
      cy.get("[data-title='MiningSchema']").click();
      cy.get(".mining-schema-list__item__name").should(($miningSchema) => {
        expect($miningSchema).length(1);
        expect($miningSchema.eq(0)).text("Test Data Type");
      });
      cy.get("[data-title='MiningSchemaModalClose']").click();

      // open and close PMML Outputs modal
      cy.get("[data-title='Outputs']").click();
      cy.get(".outputs-container div > strong").should(($outputs) => {
        expect($outputs).length(1);
        expect($outputs.eq(0)).text("Test Output");
      });
      cy.get("[data-title='OutputsModalClose']").click();
    });
  });
});
