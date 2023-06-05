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
describe("Serverless Logic Web Tools - Create and edit test", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("should check all new buttons are present", () => {
    cy.ouia({ ouiaId: "Workflow-card" }).find("button").should("have.length", 2);
    cy.ouia({ ouiaId: "new-sw.json-button" }).should("have.text", "JSON");
    cy.ouia({ ouiaId: "new-sw.yaml-button" }).should("have.text", "YAML");

    cy.ouia({ ouiaId: "Decision-card" }).find("button").should("have.length", 2);
    cy.ouia({ ouiaId: "new-yard.json-button" }).should("have.text", "JSON");
    cy.ouia({ ouiaId: "new-yard.yaml-button" }).should("have.text", "YAML");

    cy.ouia({ ouiaId: "Dashboard-card" }).find("button").should("have.length", 1);
    cy.ouia({ ouiaId: "new-dash.yaml-button" }).should("have.text", "YAML");
  });

  it("should create a new JSON serverless workflow", () => {
    cy.ouia({ ouiaId: "new-sw.json-button" }).click();
    cy.loadEditor();

    // check header labels
    cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "Untitled");
    cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Serverless Workflow");

    cy.getEditor().within(() => {
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        // create basic JSON structure
        cy.get(".codelens-decoration a:contains('Create a Serverless Workflow')").click();
        cy.get("[aria-label='Serverless Workflow Example'] a").click();

        // check basic workflow parameters
        cy.get(".monaco-editor textarea")
          .should("contain.value", '"id": "Workflow unique identifier",')
          .should("contain.value", '"name": "Workflow name",')
          .should("contain.value", '"start": "StartState",');

        // check visible codelenses
        cy.get(".contentWidgets .codelens-decoration a").should(($items) => {
          expect($items.length).eq(3);
          expect($items.eq(0)).text("+ Add function...");
          expect($items.eq(1)).text("+ Add event...");
          expect($items.eq(2)).text("+ Add state...");
        });
      });

      // check JSON diagram
      cy.iframe("#kogito-iframe[src='./serverless-workflow-diagram-editor-envelope.html']").within(() => {
        cy.get("#canvasPanel").should("contain.html", "<canvas id");
      });
    });

    // check there are no problems in JSON file
    cy.get("#total-notifications").should("have.text", 0);
  });

  it("should create a new YAML serverless workflow", () => {
    cy.ouia({ ouiaId: "new-sw.yaml-button" }).click();
    cy.loadEditor();

    // check header labels
    cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "Untitled");
    cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Serverless Workflow");

    cy.getEditor().within(() => {
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        // create basic YAML structure
        cy.get(".codelens-decoration a:contains('Create a Serverless Workflow')").click();
        cy.get("[aria-label='Serverless Workflow Example'] a").click();

        // check basic workflow parameters
        cy.get(".monaco-editor textarea")
          .should("contain.value", "id: 'Workflow unique identifier'")
          .should("contain.value", "name: 'Workflow name'")
          .should("contain.value", "start: 'StartState'");

        // check visible codelenses
        cy.get(".contentWidgets .codelens-decoration a").should(($items) => {
          expect($items.length).eq(3);
          expect($items.eq(0)).text("+ Add function...");
          expect($items.eq(1)).text("+ Add event...");
          expect($items.eq(2)).text("+ Add state...");
        });
      });

      // check YAML diagram state
      cy.iframe("#kogito-iframe[src='./serverless-workflow-diagram-editor-envelope.html']").within(() => {
        cy.get("#canvasPanel").should("contain.html", "<canvas id");
      });
    });

    // check there are no problems in YAML file
    cy.get("#total-notifications").should("have.text", 0);
  });

  it("should create a new Dashbuilder file", () => {
    cy.ouia({ ouiaId: "new-dash.yaml-button" }).click();
    cy.loadEditor();

    // check header labels
    cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "Untitled");
    cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Dashboard");

    // insert text and check dashbuilder editor and diagram
    cy.getEditor().within(() => {
      cy.get(".monaco-editor textarea")
        .type("pages:\n")
        .type("- components:\n")
        .type('    - html: <b data-ouia-component-id="hello-text">Hello</b> Dashbuilder!')
        .should("contain.value", "pages:")
        .should("contain.value", "- components")
        .should("contain.value", '- html: <b data-ouia-component-id="hello-text">Hello</b> Dashbuilder!');

      cy.iframe("iframe[src='dashbuilder-client/index.html']").within(() => {
        cy.get("#mainContainer").should("have.text", "Hello Dashbuilder!");
        cy.ouia({ ouiaId: "hello-text" }).should("have.text", "Hello");
      });
    });

    // check there are no problems in dashbuilder file
    cy.get("#total-notifications").should("have.text", 0);
  });
});
