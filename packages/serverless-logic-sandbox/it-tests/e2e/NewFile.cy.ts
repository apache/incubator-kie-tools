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
describe("Serverless Logic Web Tools - New empty files test", () => {
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
    cy.ouia({ ouiaId: "new-dash.yaml-button" }).should("have.text", "New Dashboard");
  });

  it("should create a new JSON serverless workflow", () => {
    cy.ouia({ ouiaId: "new-sw.json-button" }).click();
    cy.loadEditor();

    cy.getEditor().within(() => {
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        cy.get("a").contains("Create a Serverless Workflow").click();
        cy.get("span").contains("Create your first Serverless Workflow").click();

        cy.get(".monaco-editor textarea")
          .should("contain.value", '"id": "Workflow unique identifier"')
          .should("contain.value", '"name": "Workflow name"')
          .should("contain.value", '"start": "StartState"');

        cy.get(".contentWidgets a").contains("+ Add function...");
        cy.get(".contentWidgets a").contains("+ Add event...");
        cy.get(".contentWidgets a").contains("+ Add state...");
      });
    });
  });

  it("should create a new YAML serverless workflow", () => {
    cy.ouia({ ouiaId: "new-sw.yaml-button" }).click();
    cy.loadEditor();

    cy.getEditor().within(() => {
      cy.iframe("#kogito-iframe[src='./serverless-workflow-text-editor-envelope.html']").within(() => {
        cy.get("a").contains("Create a Serverless Workflow").click();
        cy.get("span").contains("Create your first Serverless Workflow").click();

        cy.get(".monaco-editor textarea")
          .should("contain.value", "id: 'Workflow unique identifier'")
          .should("contain.value", "name: 'Workflow name'")
          .should("contain.value", "start: 'StartState'");

        cy.get(".contentWidgets a").contains("+ Add function...");
        cy.get(".contentWidgets a").contains("+ Add event...");
        cy.get(".contentWidgets a").contains("+ Add state...");
      });
    });
  });
});
