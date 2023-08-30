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

describe("Bpmn Workitem E2E Test.", () => {
  const NAME_INPUT_LOCATOR: string = "input[data-field='name']";
  const DATATYPE_SELECT_LOCATOR: string = "select[data-field='dataType']";

  beforeEach("Visit page", () => {
    cy.visit("/bpmn-workitem");
    cy.loadEditors(["bpmn-workitem"]);
  });

  it("Loads custom workitem definition to Palette on open", () => {
    cy.editor("bpmn-workitem")
      .find("[data-field='kie-palette']")
      .should("be.visible")
      .find("[data-field='listGroupItem'] [data-field='categoryIcon']")
      .should("have.length", 9)
      .then(($items) => {
        expect($items.eq(0)).to.have.attr("title", "Start Events");
        expect($items.eq(1)).to.have.attr("title", "Intermediate Events");
        expect($items.eq(2)).to.have.attr("title", "End Events");
        expect($items.eq(3)).to.have.attr("title", "Activities");
        expect($items.eq(4)).to.have.attr("title", "SubProcesses");
        expect($items.eq(5)).to.have.attr("title", "Gateways");
        expect($items.eq(6)).to.have.attr("title", "Containers");
        expect($items.eq(7)).to.have.attr("title", "Artifacts");
        expect($items.eq(8)).to.have.attr("title", "Custom Tasks");
        cy.wrap($items.eq(8)).click();
      });
    cy.editor("bpmn-workitem")
      .find("[data-field='listGroupItem']")
      .eq(8)
      .find("[data-field='floatingPanel']")
      .find("[data-field='name']")
      .should("exist")
      .should("contain.text", "CreateCustomer");
  });

  it("Loads the Custom Workitem in diagram explorer", () => {
    cy.editor("bpmn-workitem").find("[data-title='Explore Diagram']").should("be.visible").click();

    cy.editor("bpmn-workitem")
      .find("[data-field='explorerPanelBody']")
      .wait(1000)
      .scrollIntoView()
      .should("be.visible")
      .click();

    cy.editor("bpmn-workitem")
      .find("[data-field='explorerPanelBody']")
      .wait(1000)
      .scrollIntoView()
      .should("be.visible")
      .find("a.gwt-Anchor")
      .should("have.length", 4)
      .then(($links) => {
        expect($links.eq(0)).to.contain.text("process-wid");
        expect($links.eq(1)).to.contain.text("Start");
        expect($links.eq(2)).to.contain.text("End");
        expect($links.eq(3)).to.contain.text("Create Customer Internal Service");

        cy.wrap($links.eq(3)).click();

        cy.editor("bpmn-workitem")
          .ouiaId("docks-item", "docks-item-DiagramEditorPropertiesScreen")
          .find("button")
          .first()
          .should("be.visible")
          .click(); // open Properties

        cy.editor("bpmn-workitem").ouiaId("expanded-docks-bar", "expanded-docks-bar-E").should("be.visible");
      });

    // Custom workitem task has expected properties
    // Properties have expected length
    cy.editor("bpmn-workitem")
      .ouiaId("expanded-docks-bar", "expanded-docks-bar-E")
      .within(($properties) => {
        cy.wrap($properties).find("[id='mainContainer']").should("be.visible").children(".row");

        // Custom workitem has expected name", () => {
        cy.wrap($properties)
          .find("textarea[name*='general.name']")
          .should("have.value", "Create Customer Internal Service")
          .should("not.be", "disabled");

        // Custom workitem has expected documentation", () => {
        cy.wrap($properties)
          .find("textarea[name*='general.documentation']")
          .should("have.value", "Calls internal service that creates the customer in database server.")
          .should("not.be", "disabled");

        // Custom workitem has expected isAsync value", () => {
        cy.wrap($properties)
          .find("input[name*='executionSet.isAsync']")
          .should("not.be.checked")
          .should("not.be.disabled");

        // Custom workitem has expected AdHoc Autostart value", () => {
        cy.wrap($properties)
          .find("input[name*='executionSet.adHocAutostart']")
          .should("not.be.checked")
          .should("not.be.disabled");

        // Custom workitem has expected SLA Due Date", () => {
        cy.wrap($properties)
          .find("input[name*='executionSet.slaDueDate']")
          .should("have.value", "")
          .should("not.be", "disabled");

        // Custom workitem has expected data assignment size", () => {
        cy.wrap($properties)
          .find("input[id='assignmentsTextBox']")
          .should("have.value", "7 data inputs, 1 data output")
          .should("not.be", "disabled");

        // Data assignments of custom work item tasks are not disabled", () => {
        cy.wrap($properties)
          .find("a")
          .contains("Data Assignments")
          .should("not.be.disabled")
          .click()
          .wait(1000)
          .scrollIntoView();

        // Custom workitem has expected size of data assignments when opened for edit
        cy.wrap($properties).find("button[id='assignmentsButton']").should("not.be", "disabled").click();
      });

    // Custom workitem task has expected data inputs and data output assignments
    cy.editor("bpmn-workitem")
      .find(".modal-content")
      .should("be.visible")
      .within(() => {
        cy.get(".modal-title").should("have.text", "Create Customer Internal Service Data I/O");

        cy.get("tr[id='assignment']")
          .should("have.length", 8)
          .then(($assignments) => {
            // Custom workitem has correct in_customer_id data input assignments", () => {
            cy.wrap($assignments.eq(0)).within(() => {
              cy.get(NAME_INPUT_LOCATOR)
                .should("have.value", "in_customer_id")
                .get(DATATYPE_SELECT_LOCATOR)
                .should("have.value", "String");
            });

            // Custom workitem has correct in_customer_initial_balance data input assignment", () => {
            cy.wrap($assignments.eq(1)).within(() => {
              cy.get(NAME_INPUT_LOCATOR)
                .should("have.value", "in_customer_initial_balance")
                .get(DATATYPE_SELECT_LOCATOR)
                .should("have.value", "Float");
            });

            // Custom workitem has correct in_customer_level_id data input assignments", () => {
            cy.wrap($assignments.eq(2)).within(() => {
              cy.get(NAME_INPUT_LOCATOR)
                .should("have.value", "in_customer_level_id")
                .get(DATATYPE_SELECT_LOCATOR)
                .should("have.value", "Integer");
            });

            // Custom workitem has correct in_customer_level_label data input assignment", () => {
            cy.wrap($assignments.eq(3)).within(() => {
              cy.get(NAME_INPUT_LOCATOR)
                .should("have.value", "in_customer_level_label")
                .get(DATATYPE_SELECT_LOCATOR)
                .should("have.value", "java.lang.Object");
            });

            // Custom workitem has correct in_customer_roles data input assignment", () => {
            cy.wrap($assignments.eq(4)).within(() => {
              cy.get(NAME_INPUT_LOCATOR)
                .should("have.value", "in_customer_roles")
                .get(DATATYPE_SELECT_LOCATOR)
                .should("have.value", "java.util.List");
            });

            // Custom workitem has correct in_message data input assignment", () => {
            cy.wrap($assignments.eq(5)).within(() => {
              cy.get(NAME_INPUT_LOCATOR)
                .should("have.value", "in_message")
                .get(DATATYPE_SELECT_LOCATOR)
                .should("have.value", "java.lang.Object");
            });

            // Custom workitem has correct in_security_token data input assignment", () => {
            cy.wrap($assignments.eq(6)).within(() => {
              cy.get(NAME_INPUT_LOCATOR)
                .should("have.value", "in_security_token")
                .get(DATATYPE_SELECT_LOCATOR)
                .should("have.value", "java.lang.Object");
            });

            // Custom workitem has correct out_operation_success data output assignment", () => {
            cy.wrap($assignments.eq(7)).within(() => {
              cy.get(NAME_INPUT_LOCATOR)
                .should("have.value", "out_operation_success")
                .get(DATATYPE_SELECT_LOCATOR)
                .should("have.value", "Boolean");
            });
          });
      });

    cy.editor("bpmn-workitem").find(".modal-body").contains("button", "OK").click();
  });

  describe.skip("Unknown custom workitem task has expected properties.", () => {
    let unknownCustomWorkitemProperties: JQuery<HTMLElement>;

    it("Loads the unknown custom workitem in diagram explorer", () => {
      cy.editor("bpmn-workitem").find("[data-title='Explore Diagram']").should("be.visible").click();

      cy.editor("bpmn-workitem")
        .find("[data-field='explorerPanelBody']")
        .wait(1000)
        .scrollIntoView()
        .should("be.visible")
        .find("a.gwt-Anchor")
        .should("have.length", 5)
        .then(($links) => {
          expect($links.eq(2)).to.contain.text("Email");
          cy.wrap($links.eq(2)).click();

          cy.editor("bpmn-workitem")
            .ouiaId("docks-item", "docks-item-DiagramEditorPropertiesScreen")
            .find("button")
            .first()
            .should("be.visible")
            .click(); // open Properties

          cy.editor("bpmn-workitem").ouiaId("expanded-docks-bar", "expanded-docks-bar-E").should("be.visible");
        });
    });

    // Skipped due to missing fix for https://issues.redhat.com/browse/RHPAM-3588 in 7.52.Final
    it.skip("Unkown custom workitem properties have expected length.", () => {
      cy.editor("bpmn-workitem")
        .ouiaId("expanded-docks-bar", "expanded-docks-bar-E")
        .within(($properties) => {
          cy.wrap($properties)
            .find("[id='mainContainer']")
            .should("be.visible")
            .children(".row")
            .should("have.length", 11)
            .then(($items) => {
              unknownCustomWorkitemProperties = $items;
            });
        });
    });
  });
});
