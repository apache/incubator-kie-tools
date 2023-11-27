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

const ctrlOrCmd = Cypress.platform === "darwin" ? "Cmd" : "Ctrl";
const controlOrMeta = Cypress.platform === "darwin" ? "Meta" : "Control";

describe("Serverless Logic Web Tools - Try sample test", () => {
  // CYPRESS_E2E_TEST_GITHUB_TOKEN env. variable
  const E2E_TEST_GITHUB_TOKEN = Cypress.env("E2E_TEST_GITHUB_TOKEN");

  if (E2E_TEST_GITHUB_TOKEN) {
    beforeEach(() => {
      cy.visit("/");

      // allow clipboard permissions in browser
      Cypress.automation("remote:debugger:protocol", {
        command: "Browser.grantPermissions",
        params: {
          permissions: ["clipboardReadWrite", "clipboardSanitizedWrite"],
          origin: window.location.origin,
        },
      });

      // set GitHub token
      cy.ouia({ ouiaId: "settings-button" }).click();
      cy.ouia({ ouiaId: "add-access-token-button" }).click();
      cy.window().then((win) => {
        win.navigator.clipboard.writeText(E2E_TEST_GITHUB_TOKEN);

        // Meta key does not work on Mac https://github.com/dmtrKovalenko/cypress-real-events/issues/292
        cy.ouia({ ouiaId: "token-input" }).realPress([controlOrMeta, "v"]);
      });
      cy.ouia({ ouiaId: "signed-in-text" }).should("have.text", "You're signed in with GitHub.");
    });

    it("should check greeting sample", () => {
      // open samples catalog
      cy.ouia({ ouiaId: "app-title" }).click();
      cy.goToSidebarLink({ ouiaId: "samples-catalog-nav" });

      // open greeting example
      cy.ouia({ ouiaId: "sample-category-dropdown" }).click();
      cy.ouia({ ouiaId: "serverless-workflow-dropdown-item" }).click();
      cy.ouia({ ouiaId: "samples-count-text" })
        .should("contain.text", "Showing")
        .should("contain.text", "sample")
        .should("not.contain.text", "Showing 0");
      cy.ouia({ ouiaId: "greeting-json-try-sample-button" }).click();

      cy.loadEditor();

      // check header type and name
      cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Serverless Workflow");
      cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "greeting");

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
            .should("contain.text", `${ctrlOrCmd} + Z`)
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
              .should("contain.text", `${ctrlOrCmd} + Alt`)
              .should("contain.text", "Hold to Preview");
            cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box .pf-c-button").click();
          });
      });

      // check there are no problems in JSON file
      cy.get("#total-notifications").should("have.text", 0);
    });

    it("should check serverless-workflow-report example of dashboard", () => {
      // open samples catalog
      cy.ouia({ ouiaId: "app-title" }).click();
      cy.goToSidebarLink({ ouiaId: "samples-catalog-nav" });

      // open serverless-workflow-report example
      cy.ouia({ ouiaId: "sample-category-dropdown" }).click();
      cy.ouia({ ouiaId: "dashbuilder-dropdown-item" }).click();
      cy.ouia({ ouiaId: "samples-count-text" })
        .should("contain.text", "Showing")
        .should("contain.text", "sample")
        .should("not.contain.text", "Showing 0");
      cy.ouia({ ouiaId: "search-sample-input" }).click().type("Report");
      cy.ouia({ ouiaId: "serverless-workflow-report-try-sample-button" }).click();

      cy.loadEditor();

      // check header type and name
      cy.ouia({ ouiaId: "file-type-label" }).should("have.text", "Dashboard");
      cy.ouia({ ouiaId: "file-name-input" }).should("have.value", "serverless-workflow-report");

      cy.getEditor().within(() => {
        // check text editor contains text
        cy.get(".monaco-editor textarea")
          .should("contain.value", "global:")
          .should("contain.value", "mode: dark")
          .should("contain.value", 'class="card-pf card-pf-aggregate-status"');

        // check preview buttow hides text editor
        cy.ouia({ ouiaId: "preview-button" }).click();
        cy.get(".monaco-editor textarea").should("not.be.visible");
        cy.ouia({ ouiaId: "preview-button" }).click();
        cy.get(".monaco-editor textarea").should("be.visible");

        // check shortcuts modal dialog
        cy.ouia({ ouiaId: "keyboard-shortcuts-icon" }).click();
        cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box")
          .should("contain.text", "Keyboard shortcuts")
          .should("contain.text", `Shift + ${ctrlOrCmd} + Z`)
          .should("contain.text", "Redo last edit");
        cy.get(".kie-tools--keyboard-shortcuts.pf-c-modal-box .pf-c-button").click();

        cy.iframe("[src='dashbuilder-client/index.html']").within(() => {
          // check default values
          cy.get("#mainContainer").should("contain.text", "Serverless Workflow Summary ");
          cy.get(".card-pf-aggregate-status:contains('Total Workflows') > h2").should("contain.text", 464);
          cy.get(".card-pf-aggregate-status:contains('Completed') > h2").should("contain.text", 458);
          cy.get(".card-pf-aggregate-status:contains('Error') > h2").should("contain.text", 6);
          cy.iframe("#externalComponentIFrame").within(() => {
            cy.get(".pf-c-options-menu__toggle-text b").last().should("contain.text", 464);
          });

          // switch filter and check values
          cy.get("select.form-control").select("yamlgreet");
          cy.get(".card-pf-aggregate-status:contains('Total Workflows') > h2").should("contain.text", 177);
          cy.get(".card-pf-aggregate-status:contains('Completed') > h2").should("contain.text", 173);
          cy.get(".card-pf-aggregate-status:contains('Error') > h2").should("contain.text", 4);
          cy.iframe("#externalComponentIFrame").within(() => {
            cy.get(".pf-c-options-menu__toggle-text b").last().should("contain.text", 177);
          });
        });
      });

      // check there are no problems in dashbuilder file
      cy.get("#total-notifications").should("have.text", 0);
    });
  }
});
