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

describe("Upload file test", () => {
  beforeEach(() => {
    cy.visit(`https://localhost:${buildEnv.onlineEditor.dev.port}/`);
  });

  it("should upload BPMN file", () => {
    // upload bpmn file from fixtures directory by drag and drop
    cy.get("#upload-field").attachFile("testProcess.bpmn", { subjectType: "drag-n-drop" });

    // wait until loading dialog disappears
    cy.loadEditor();

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

      // mark start node
      cy.get("[data-ouia-component-id='tree-item-Start test node']").click();

      // add nodes by shortcuts (t - task, g - gateway, s - subprocess, e - end)
      cy.get("iframe").type("tgse", { force: true });

      // rename end node
      cy.get("[data-title='Properties']").click();
      cy.get("div[data-i18n-prefix='FormDisplayerViewImpl.']:not([hidden]) [name$='general.name']")
        .focus()
        .clear()
        .type("End test node");
      cy.get("[data-title='Explore Diagram']").click();

      // check nodes are added
      cy.get("a.gwt-Anchor").should(($nodes) => {
        expect($nodes).length(6);
        expect($nodes.eq(0)).text("Test process");
        expect($nodes.eq(1)).text("Start test node");
        expect($nodes.eq(2)).text("Task");
        expect($nodes.eq(3)).text("Parallel");
        expect($nodes.eq(4)).text("Sub-process");
        expect($nodes.eq(5)).text("End test node");
      });
    });

    // wait for autosave
    cy.wait(1000);

    // download process
    cy.get("[data-ouia-component-id='kebab-sm']").click();
    cy.get("[data-ouia-component-id='download-file-dropdown-button']").click();

    // check process content
    cy.readFile("downloads/testProcess.bpmn").should(($text) => {
      expect($text).match(/<bpmn2:endEvent id="[A-Z0-9_-]*" name="End test node">/);
      expect($text).match(/<bpmn2:startEvent id="[A-Z0-9_-]*" name="Start test node">/);
    });
  });

  it("should upload DMN file", () => {
    // upload dmn file from fixtures directory by drag and drop
    cy.get("#upload-field").attachFile("testModel.dmn", { subjectType: "drag-n-drop" });

    // wait until loading dialog disappears
    cy.loadEditor();

    // check editor title name
    cy.get("[aria-label='Edit file name']").should("have.value", "testModel");

    // close DMN guided tour dialog
    cy.get("[data-ouia-component-id='dmn-guided-tour'] button[aria-label='Close']").click();

    cy.getEditor().within(() => {
      // open properties panel and check values
      cy.get("[data-title='Properties']").click();
      cy.get("[name$='definitions.nameHolder']").should("have.value", "Test model");
      cy.get("[name$='definitions.description']").should("have.value", "This is test model.");

      // open decision navigator and check nodes
      cy.get("[data-title='Decision Navigator']").click();
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").should(($nodes) => {
        expect($nodes).length(2);
        expect($nodes.eq(0)).attr("title", "Test model");
        expect($nodes.eq(1)).attr("title", "Test input data");
      });

      // mark input data node and add node by shortcuts (d - decision)
      cy.get("[title='Test input data'] > div").click();
      cy.get("iframe").type("d", { force: true });

      // close decision navigatior, open properties and rename decision node
      cy.get("[data-title='Decision Navigator']").click();
      cy.get("[data-title='Properties']").click();
      cy.get("[name$='nameHolder']").focus().clear().type("Test decision node").type("{enter}");

      // check nodes are added
      cy.get("[data-title='Decision Navigator']").click();
      cy.get("li[data-i18n-prefix='DecisionNavigatorTreeView.']").should(($nodes) => {
        expect($nodes).length(3);
        expect($nodes.eq(0)).attr("title", "Test model");
        expect($nodes.eq(1)).attr("title", "Test decision node");
        expect($nodes.eq(2)).attr("title", "Test input data");
      });
    });

    // wait for autosave
    cy.wait(1000);

    // download model
    cy.get("[data-ouia-component-id='kebab-sm']").click();
    cy.get("[data-ouia-component-id='download-file-dropdown-button']").click();

    // check model content
    cy.readFile("downloads/testModel.dmn").should(($text) => {
      expect($text).match(/<dmn:inputData id="[A-Z0-9_-]*" name="Test input data">/);
      expect($text).match(/<dmn:decision id="[A-Z0-9_-]*" name="Test decision node">/);
    });
  });

  it("DMN Guided Tour popup shouldn't appear when opening broken file", () => {
    cy.on("uncaught:exception", (err, runnable) => {
      // The DMN Editor will throw an exception because it failed to marshall the contents of the uploaded file.
      // Returning false here prevents Cypress from failing the test.
      return false;
    });

    // upload dmn file from fixtures directory by drag and drop
    cy.get("#upload-field").attachFile("testModelBroken.dmn", { subjectType: "drag-n-drop" });

    // wait until loading dialog disappears
    cy.loadEditor();

    // check editor title name
    cy.get("[aria-label='Edit file name']").should("have.value", "testModelBroken");

    // DMN guided tour dialog can't be shown for invalid models
    cy.get("[data-ouia-component-id='dmn-guided-tour']").should("not.be.visible");

    cy.get("[data-ouia-component-id='set-content-error-alert']").should("be.visible");
  });

  it("should upload PMML file", () => {
    // upload pmml file from fixtures directory by drag and drop
    cy.get("#upload-field").attachFile("testScoreCard.pmml", { subjectType: "drag-n-drop" });

    // load pmml editor
    cy.getEditor().within(() => {
      cy.get("[data-testid='editor-page']", { timeout: 60000 }).should("be.visible");
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

      // add characteristic
      cy.get("#add-characteristic-button").click();
      cy.get("#characteristic-name").focus().clear().type("Second Test Characteristic");
      cy.get("#characteristic-reason-code").focus().clear().type("4");
      cy.get("#characteristic-baseline-score").focus().clear().type("47");
      cy.get("#characteristics-toolbar").click();

      // check characteristic is added
      cy.get(".characteristics-container div > strong").should(($characteristics) => {
        expect($characteristics).length(2);
        expect($characteristics.eq(0)).text("Test Characteristic");
        expect($characteristics.eq(1)).text("Second Test Characteristic");
      });
    });

    // wait for autosave
    cy.wait(1000);

    // download score card
    cy.get("[data-ouia-component-id='kebab-sm']").click();
    cy.get("[data-ouia-component-id='download-file-dropdown-button']").click();

    // check score card content
    cy.readFile("downloads/testScoreCard.pmml").should(($text) => {
      expect($text).contains('<Characteristic name="Test Characteristic" reasonCode="3" baselineScore="22"/>');
      expect($text).contains('<Characteristic name="Second Test Characteristic" reasonCode="4" baselineScore="47"/>');
    });
  });
});
