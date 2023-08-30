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

describe("Bpmn Read Only.", () => {
  beforeEach("Visit page", () => {
    cy.visit("/bpmn-read-only");
    cy.loadEditors(["bpmn-read-only"]);
  });

  it("Loads empty editor in read only mode", () => {
    // Editor palette is not visible
    cy.editor("bpmn-read-only").find("[data-field='palettePanel']").should("not.be.visible");

    // Editor properties bar is visible"
    cy.editor("bpmn-read-only")
      .ouiaId("collapsed-docks-bar", "collapsed-docks-bar-E", { timeout: 10000 })
      .should("be.visible");
  });

  it("Loads non-empty editor in read only mode", () => {
    // Loads non-empty editor without palette
    cy.uploadFile("process-string.bpmn", "bpmn-read-only");
    cy.viewFile("process-string.bpmn", "bpmn-read-only");

    cy.editor("bpmn-read-only").find("[data-field='palettePanel']").should("not.be.visible");

    // Loads non-empty editor with properties bar visible
    cy.editor("bpmn-read-only")
      .ouiaId("collapsed-docks-bar", "collapsed-docks-bar-E", { timeout: 10000 })
      .should("be.visible");

    // Editor properties bar can be opened
    cy.editor("bpmn-read-only")
      .ouiaId("docks-item", "docks-item-DiagramEditorPropertiesScreen")
      .find("button")
      .first()
      .should("be.visible")
      .click(); // open Properties
    cy.editor("bpmn-read-only").ouiaId("expanded-docks-bar", "expanded-docks-bar-E").should("be.visible");

    // Editor properties are displayed as read-only
    cy.editor("bpmn-read-only")
      .ouiaId("expanded-docks-bar", "expanded-docks-bar-E")
      .within(($properties) => {
        // Properties have expected length
        cy.wrap($properties)
          .find("[id='mainContainer']")
          .should("be.visible")
          .children(".row")
          .should("have.length", 19);

        // Process name property is read only
        cy.wrap($properties)
          .find("input[name*='diagramSet.name']")
          .should("have.value", "Process string")
          .should("not.have.class", "editable");

        // Process documentation property is read only
        cy.wrap($properties)
          .find("textarea[name*='diagramSet.documentation']")
          .should("have.value", "Documentation")
          .should("not.have.class", "editable");

        // Process ID property is read only
        cy.wrap($properties)
          .find("input[name*='diagramSet.id']")
          .should("not.have.class", "editable")
          .should("have.value", "defaultProcessId");

        // Process package property is read only
        cy.wrap($properties)
          .find("input[name*='diagramSet.packageProperty']")
          .should("not.have.class", "editable")
          .should("have.value", "com.example");

        // Process type property is read only
        cy.wrap($properties)
          .find("select[name*='diagramSet.processType']")
          .should("not.have.class", "editable")
          .should("have.value", "Public");

        // Process version property is read only"
        cy.wrap($properties)
          .find("input[name*='diagramSet.version']")
          .should("not.have.class", "editable")
          .should("have.value", "1.0");

        // Process ad-hoc property is read only"
        cy.wrap($properties).find("input[name*='diagramSet.adHoc']").should("be.disabled").should("not.be.checked");

        // Process instance description property is read only
        cy.wrap($properties)
          .find("input[name*='diagramSet.processInstanceDescription']")
          .should("not.have.class", "editable")
          .should("have.value", "");

        // Process imports property is read only
        cy.wrap($properties)
          .find("input[id='importsTextBox']")
          .should("have.value", "No imports")
          .should("not.have.class", "editable");

        // Process executable property is read only
        cy.wrap($properties).find("input[name*='diagramSet.executable']").should("be.checked").should("be.disabled");

        // Process SLA due date property is read only
        cy.wrap($properties)
          .find("input[name*='diagramSet.slaDueDate']")
          .should("have.value", "")
          .should("have.attr", "disabled");

        // Process variables (Process Data section) section is not disabled
        // Wait for animation to stop before scrolling;
        cy.wrap($properties)
          .find("a")
          .contains("Process Data")
          .should("not.be.disabled")
          .click()
          .wait(1000)
          .scrollIntoView();

        // Process variables add button is read only
        cy.wrap($properties).find("button[data-field='addVarButton']").should("be.disabled");

        // Process variables are displayed as read only

        // Process Variables have expected length
        cy.wrap($properties)
          .contains("Process Variables")
          .siblings()
          .first()
          .should("not.be.disabled")
          .should("be.visible")
          .then(($processVariablesContainer) => {
            cy.wrap($processVariablesContainer).find("#variableRow").siblings().should("have.length", "0");

            // Process variable name is read only
            cy.wrap($processVariablesContainer)
              .find("input[data-field='name']")
              .should("not.have.class", "editable")
              .should("have.value", "string_input");

            // Process variable data type is read only
            cy.wrap($processVariablesContainer)
              .find("select[data-field='dataType']")
              .should("not.have.class", "editable")
              .should("have.value", "String");

            // Process variable delete button is read only
            cy.wrap($processVariablesContainer)
              .find("button[data-field='deleteButton']")
              .should("not.have.class", "editable");
          });

        // Process advanced (Advanced section) section is not disabled
        // Wait for animation to stop before scrolling;
        cy.wrap($properties)
          .find("a")
          .contains("Advanced")
          .should("not.be.disabled")
          .click()
          .wait(1000)
          .scrollIntoView();

        // Process metadata attributes rows are displayed as read only

        // Metadata attributes list have expected size
        cy.wrap($properties)
          .contains("Metadata Attributes")
          .siblings()
          .first()
          .should("not.be.disabled")
          .should("be.visible")
          .then(($metadataAtributesContainer) => {
            cy.wrap($metadataAtributesContainer).find("#metaDataRow").siblings().should("have.length", "0");

            // Process metadata attribute name is read only"
            cy.wrap($metadataAtributesContainer)
              .find("input[data-field='attribute']")
              .should("not.have.class", "editable")
              .should("have.value", "perf_indicator");

            // Process metadata attribute value is read only"
            cy.wrap($metadataAtributesContainer)
              .find("input[data-field='value']")
              .should("not.have.class", "editable")
              .should("have.value", "good_performance");

            // Process metadata attribute delete button is read only"
            cy.wrap($metadataAtributesContainer).find("button[data-field='deleteButton']").should("be.disabled");
          });

        // Process global variables rows are displayed as read only

        // Global variables list have expected length"
        cy.wrap($properties)
          .contains("Global Variables")
          .siblings()
          .first()
          .wait(1000) // Wait for animation to stop before scrolling;
          .scrollIntoView()
          .should("not.be.disabled")
          .should("be.visible")
          .then(($globalVariablesContainer) => {
            cy.wrap($globalVariablesContainer).find("#variableRow").siblings().should("have.length", "0");

            // Process global variable name is read only
            cy.wrap($globalVariablesContainer)
              .find("input[data-field='name']")
              .should("not.have.class", "editable")
              .should("have.value", "is_processing");

            // Process global variable type is read only"
            cy.wrap($globalVariablesContainer)
              .find("select[data-field='dataType']")
              .should("not.have.class", "editable")
              .should("have.value", "Boolean");

            // Process metadata attribute delete button is read only
            cy.wrap($globalVariablesContainer).find("button[data-field='deleteButton']").should("be.disabled");
          });
      });
  });
});
