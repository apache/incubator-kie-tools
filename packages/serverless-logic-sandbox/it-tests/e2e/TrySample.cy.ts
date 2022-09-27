/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

describe("Serverless Logic Web Tools - Try samples test", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("should check all samples are present", () => {
    cy.ouia({ ouiaType: "sample-title" }).should(($titles) => {
      expect($titles).length(6);
      expect($titles.eq(0)).text("Greetings");
      expect($titles.eq(1)).text("Greetings with Kafka events");
      expect($titles.eq(2)).text("Compensation");
      expect($titles.eq(3)).text("Dashbuilder Kitchensink");
      expect($titles.eq(4)).text("Products Dashboard");
      expect($titles.eq(5)).text("Serverless Workflow Report");
    });
  });

  it("should check greetings sample", () => {
    // open greeting example
    cy.ouia({ ouiaId: "greetings-try-swf-sample-button" }).click();
    cy.loadEditor();

    // check header type and name
    cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Serverless Workflow");
    cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "greetings");

    cy.getEditor().within(() => {
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        // check text editor contains text
        cy.get(".monaco-editor textarea")
          .should("contain.value", '"id": "jsongreet"')
          .should("contain.value", '"name": "Greeting workflow",');

        // check shortcuts modal dialog
        cy.ouia({ ouiaId: "keyboard-shortcuts-icon" }).click();
        cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box")
          .should("contain.text", "Keyboard shortcuts")
          .should("contain.text", "Ctrl + Z")
          .should("contain.text", "Undo last edit");
        cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box .pf-c-button").click();
      });

      cy.iframe("#kogito-iframe[src='./serverless-workflow-diagram-editor-envelope.html']")
        .trigger("mouseover")
        .within(() => {
          // check canvas is present
          cy.get("#canvasPanel").should("have.descendants", "canvas").should("be.visible");

          // check zoom controls are present
          cy.ouia({ ouiaId: "zoom-controls-panel" })
            .should("be.visible")
            .should("have.descendants", "button[data-ouia-component-id='reset-zoom-button']")
            .should("have.descendants", "button[data-ouia-component-id='minus-zoom-button']")
            .should("have.descendants", "button[data-ouia-component-id='plus-zoom-button']")
            .should("have.descendants", "div[data-ouia-component-id='select-zoom-button']");

          // check shortcuts modal dialog
          cy.ouia({ ouiaId: "keyboard-shortcuts-icon" }).click();
          cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box")
            .should("contain.text", "Keyboard shortcuts")
            .should("contain.text", "Ctrl + Alt")
            .should("contain.text", "Hold to Preview");
          cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box .pf-c-button").click();
        });
    });
  });

  it("should check swf-report example of dashboard", () => {
    // open swf-report example
    cy.ouia({ ouiaId: "swf-report-try-swf-sample-button" }).click();
    cy.loadEditor();

    // check header type and name
    cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Dashboard");
    cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "swf-report");

    cy.getEditor().within(() => {
      // check text editor contains text
      cy.get(".monaco-editor textarea")
        .should("contain.value", "properties:")
        .should("contain.value", "dataIndexUrl: https://your.data.index.url.com")
        .should("contain.value", "cardTemplate: >-");

      // check preview buttow hides text editor
      cy.ouia({ ouiaId: "preview-button" }).click();
      cy.get(".monaco-editor textarea").should("not.be.visible");
      cy.ouia({ ouiaId: "preview-button" }).click();
      cy.get(".monaco-editor textarea").should("be.visible");

      // check shortcuts modal dialog
      cy.ouia({ ouiaId: "keyboard-shortcuts-icon" }).click();
      cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box")
        .should("contain.text", "Keyboard shortcuts")
        .should("contain.text", "Shift + Ctrl + Z")
        .should("contain.text", "Redo last edit");
      cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box .pf-c-button").click();

      cy.iframe("[src='dashbuilder-client/index.html']").within(() => {
        // check default values
        cy.get("#mainContainer").should("contain.text", "Serverless Workflow Summary ");
        cy.get(".card-pf-aggregate-status:contains('Total Workflows') > h2").should("have.text", 464);
        cy.get(".card-pf-aggregate-status:contains('Completed') > h2").should("have.text", 458);
        cy.get(".card-pf-aggregate-status:contains('Error') > h2").should("have.text", 6);
        cy.iframe("#externalComponentIFrame").within(() => {
          cy.get(".pf-c-options-menu__toggle-text b").last().should("have.text", 464);
        });

        // switch filter and check values
        cy.get("select.form-control").select("yamlgreet");
        cy.get(".card-pf-aggregate-status:contains('Total Workflows') > h2").should("have.text", 177);
        cy.get(".card-pf-aggregate-status:contains('Completed') > h2").should("have.text", 173);
        cy.get(".card-pf-aggregate-status:contains('Error') > h2").should("have.text", 4);
        cy.iframe("#externalComponentIFrame").within(() => {
          cy.get(".pf-c-options-menu__toggle-text b").last().should("have.text", 177);
        });

        // check average duration graph
        cy.get(".uf-perspective-rendered-col:contains('Average Duration') .c3-event-rect").click();
        cy.get(".c3-tooltip-name--Total .value").should("have.text", 2.06);
      });
    });
  });
});
