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

describe("Bpmn Read Only.", () => {
  before("Visit page", () => {
    cy.visit(`localhost:${buildEnv.standaloneEditors.dev.port}/bpmn-read-only`);
    cy.loadEditors(["bpmn-read-only"]);
  });

  describe("Loads empty editor in read only mode", () => {
    it("Editor palette is not visible", () => {
      cy.editor("bpmn-read-only").find("[data-field='palettePanel']").should("not.be.visible");
    });

    it("Editor properties bar is visible", () => {
      cy.editor("bpmn-read-only")
        .ouiaId("collapsed-docks-bar", "collapsed-docks-bar-E", { timeout: 10000 })
        .should("be.visible");
    });
  });

  describe("Loads non-empty editor in read only mode", () => {
    it("Loads non-empty editor without palette", () => {
      cy.uploadFile("process-string.bpmn", "bpmn-read-only");
      cy.viewFile("process-string.bpmn", "bpmn-read-only");

      cy.editor("bpmn-read-only").find("[data-field='palettePanel']").should("not.be.visible");
    });

    it("Loads non-empty editor with properties bar visible", () => {
      cy.editor("bpmn-read-only")
        .ouiaId("collapsed-docks-bar", "collapsed-docks-bar-E", { timeout: 10000 })
        .should("be.visible");
    });

    it("Editor properties bar can be opened", () => {
      cy.editor("bpmn-read-only")
        .ouiaId("docks-item", "docks-item-DiagramEditorPropertiesScreen")
        .find("button")
        .first()
        .should("be.visible")
        .click(); // open Properties
      cy.editor("bpmn-read-only").ouiaId("expanded-docks-bar", "expanded-docks-bar-E").should("be.visible");
    });

    describe("Editor properties are displayed as read-only", () => {
      let propertyItems: JQuery<HTMLElement>;

      it("Properties have expected length", () => {
        cy.editor("bpmn-read-only")
          .ouiaId("expanded-docks-bar", "expanded-docks-bar-E")
          .within(($properties) => {
            cy.wrap($properties)
              .find("[id='mainContainer']")
              .should("be.visible")
              .children(".row")
              .should("have.length", 17)
              .then(($items) => {
                propertyItems = $items;
              });
          });
      });

      it("Process name property is read only", () => {
        cy.wrap(propertyItems)
          .find("input[name*='diagramSet.name']")
          .should("have.value", "Process string")
          .should("not.have.class", "editable");
      });

      it("Process documentation property is read only", () => {
        cy.wrap(propertyItems)
          .find("textarea[name*='diagramSet.documentation']")
          .should("have.value", "Documentation")
          .should("not.have.class", "editable");
      });

      it("Process ID property is read only", () => {
        cy.wrap(propertyItems)
          .find("input[name*='diagramSet.id']")
          .should("not.have.class", "editable")
          .should("have.value", "defaultProcessId");
      });

      it("Process package property is read only", () => {
        cy.wrap(propertyItems)
          .find("input[name*='diagramSet.packageProperty']")
          .should("not.have.class", "editable")
          .should("have.value", "com.example");
      });

      it("Process type property is read only", () => {
        cy.wrap(propertyItems)
          .find("select[name*='diagramSet.processType']")
          .should("not.have.class", "editable")
          .should("have.value", "Public");
      });

      it("Process version property is read only", () => {
        cy.wrap(propertyItems)
          .find("input[name*='diagramSet.version']")
          .should("not.have.class", "editable")
          .should("have.value", "1.0");
      });

      it("Process ad-hoc property is read only", () => {
        cy.wrap(propertyItems).find("input[name*='diagramSet.adHoc']").should("be.disabled").should("not.be.checked");
      });

      it("Process instance description property is read only", () => {
        cy.wrap(propertyItems)
          .find("input[name*='diagramSet.processInstanceDescription']")
          .should("not.have.class", "editable")
          .should("have.value", "");
      });

      it("Process imports property is read only", () => {
        cy.wrap(propertyItems)
          .find("input[id='importsTextBox']")
          .should("have.value", "No imports")
          .should("not.have.class", "editable");
      });

      it.skip("Process imports buttons is read only", () => {
        cy.wrap(propertyItems).find("button[id='importsButton']").should("be.disabled");
      });

      it("Process executable property is read only", () => {
        cy.wrap(propertyItems).find("input[name*='diagramSet.executable']").should("be.checked").should("be.disabled");
      });

      it("Process SLA due date property is read only", () => {
        cy.wrap(propertyItems)
          .find("input[name*='diagramSet.slaDueDate']")
          .should("have.value", "")
          .should("have.attr", "disabled");
      });

      it("Process variables (Process Data section) section is not disabled", () => {
        // Wait for animation to stop before scrolling;
        cy.wrap(propertyItems)
          .find("a")
          .contains("Process Data")
          .should("not.be.disabled")
          .click()
          .wait(1000)
          .scrollIntoView();
      });

      it("Process variables add button is read only", () => {
        cy.wrap(propertyItems).find("button[data-field='addVarButton']").should("be.disabled");
      });

      describe("Process variables are displayed as read only", () => {
        let processVariables: JQuery<HTMLElement>;

        it("Process Variables have expected length", () => {
          cy.wrap(propertyItems)
            .contains("Process Variables")
            .siblings()
            .first()
            .should("not.be.disabled")
            .should("be.visible")
            .then(($processVariablesContainer) => {
              processVariables = $processVariablesContainer;
              cy.wrap($processVariablesContainer).find("#variableRow").siblings().should("have.length", "0");
            });
        });

        it("Process variable name is read only", () => {
          cy.wrap(processVariables)
            .find("input[data-field='name']")
            .should("not.have.class", "editable")
            .should("have.value", "string_input");
        });

        it("Process variable data type is read only", () => {
          cy.wrap(processVariables)
            .find("select[data-field='dataType']")
            .should("not.have.class", "editable")
            .should("have.value", "String");
        });

        it.skip("Process variable TAG handle is read only", () => {
          cy.wrap(processVariables).find("a[data-field='variable-tags-settings']").should("be.disabled");
        });

        it("Process variable delete button is read only", () => {
          cy.wrap(processVariables).find("button[data-field='deleteButton']").should("not.have.class", "editable");
        });
      });

      it("Process advanced (Advanced section) section is not disabled", () => {
        // Wait for animation to stop before scrolling;
        cy.wrap(propertyItems)
          .find("a")
          .contains("Advanced")
          .should("not.be.disabled")
          .click()
          .wait(1000)
          .scrollIntoView();
      });

      describe("Process metadata attributes rows are displayed as read only", () => {
        let metadataAtttributes: JQuery<HTMLElement>;

        it("Metadata attributes list have expected size", () => {
          cy.wrap(propertyItems)
            .contains("Metadata Attributes")
            .siblings()
            .first()
            .should("not.be.disabled")
            .should("be.visible")
            .then(($metadataAtributesContainer) => {
              metadataAtttributes = $metadataAtributesContainer;
              cy.wrap($metadataAtributesContainer).find("#metaDataRow").siblings().should("have.length", "0");
            });
        });

        it("Process metadata attribute name is read only", () => {
          cy.wrap(metadataAtttributes)
            .find("input[data-field='attribute']")
            .should("not.have.class", "editable")
            .should("have.value", "perf_indicator");
        });

        it("Process metadata attribute value is read only", () => {
          cy.wrap(metadataAtttributes)
            .find("input[data-field='value']")
            .should("not.have.class", "editable")
            .should("have.value", "good_performance");
        });

        it("Process metadata attribute delete button is read only", () => {
          cy.wrap(metadataAtttributes).find("button[data-field='deleteButton']").should("be.disabled");
        });
      });

      describe("Process global variables rows are displayed as read only", () => {
        let globalVariables: JQuery<HTMLElement>;

        it("Global variables list have expected length", () => {
          cy.wrap(propertyItems)
            .contains("Global Variables")
            .siblings()
            .first()
            .wait(1000) // Wait for animation to stop before scrolling;
            .scrollIntoView()
            .should("not.be.disabled")
            .should("be.visible")
            .then(($globalVariablesContainer) => {
              globalVariables = $globalVariablesContainer;
              cy.wrap($globalVariablesContainer).find("#variableRow").siblings().should("have.length", "0");
            });
        });

        it("Process global variable name is read only", () => {
          cy.wrap(globalVariables)
            .find("input[data-field='name']")
            .should("not.have.class", "editable")
            .should("have.value", "is_processing");
        });

        it("Process global variable typeis read only", () => {
          cy.wrap(globalVariables)
            .find("select[data-field='dataType']")
            .should("not.have.class", "editable")
            .should("have.value", "Boolean");
        });

        it("Process metadata attribute delete button is read only", () => {
          cy.wrap(globalVariables).find("button[data-field='deleteButton']").should("be.disabled");
        });
      });
    });
  });
});
