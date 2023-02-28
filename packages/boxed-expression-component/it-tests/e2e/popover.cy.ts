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

import { env } from "../../env";
const buildEnv = env;

describe("PopoverMenu Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);

    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Context
    cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });

    // Define ExpressionNameCell
    cy.contains("th", "Expression Name").as("ExpressionNameCell");

    // check the popover to be closed
    cy.ouiaId("expression-popover-menu").should("not.to.exist");

    // focus the 1st header cell inside the nested decision table.
    cy.get("@ExpressionNameCell").focus();
  });

  it("Cancel edit of expression data type by pressing escape", () => {
    // open the popover
    cy.get("@ExpressionNameCell").click();

    // open the data type select
    cy.ouiaId("edit-expression-data-type").as("dataTypeInput").find("button").click({ force: true });

    // select "context" type
    cy.get("@dataTypeInput").contains("context").click({ force: true });

    cy.ouiaId("expression-popover-menu")
      // check the popover be open. This check is to check that the popover is not closed after data type selection
      .should("be.visible");

    cy.realPress("Escape");

    // Assert data type not to be changed
    cy.get("@ExpressionNameCell").find(".data-type").should("contain.text", "Undefined");
  });

  it("Cancel edit of expression name by pressing escape", () => {
    // open the popover
    cy.get("@ExpressionNameCell").click();

    // change the expression name
    cy.get("#expression-name").type("{selectall}Newname", { force: true });

    // Cancel the editing
    cy.ouiaId("expression-popover-menu").contains("Name").click();

    // check the popover be open. This check is to check that the popover is not closed after data type selection
    cy.ouiaId("expression-popover-menu").should("be.visible");

    cy.realPress("Escape");

    // Assert expression name not to be changed
    cy.get("@ExpressionNameCell").find(".label").should("contain.text", "Expression Name");
  });

  it("The header cell's in the popover should have original values", () => {
    // open the popover
    cy.get("@ExpressionNameCell").click();

    // expression name input should have original value
    cy.get("#expression-name").should("have.value", "Expression Name");

    // data type select should have original value
    cy.ouiaId("edit-expression-data-type").should("contain.text", "Undefined");

    // close the popover to reset the state of it
    cy.ouiaId("expression-popover-menu").should("exist");

    cy.realPress("Escape");

    cy.ouiaId("expression-popover-menu").should("not.exist");
  });

  describe("Keyboard interaction with header's contextMenu in nested decision table", () => {
    beforeEach(() => {
      // open the popover by pressing enter. Esc key is needed to reset the poopover state and wait to pass the tests with Cypress in headless mode
      cy.get("@ExpressionNameCell").focus().type("{esc}");

      // check the popover is closed
      cy.ouiaId("expression-popover-menu").should("not.to.exist");

      // open the popover menu
      cy.get("@ExpressionNameCell").click();

      // check the popover is open
      cy.ouiaId("expression-popover-menu").should("be.visible");

      // check expression name is focused
      cy.ouiaId("edit-expression-name").should("be.focused");
    });

    it("Edit expression name field and type esc to cancel", () => {
      // type some text in expression name field
      cy.ouiaId("edit-expression-name").type(" cancelled{esc}");

      // check the expression name field doesn't have the new text
      cy.get("@ExpressionNameCell").should("not.contain.text", "cancelled").should("contain.text", "Expression Name");
    });

    it("Edit expression name field and type enter to save", () => {
      // type some text in expression name field
      cy.ouiaId("edit-expression-name").type(" edited{enter}");

      // check the expression name field has the new text
      cy.get("@ExpressionNameCell").should("contain.text", "Expression Name edited");
    });

    it("Edit expression data type", () => {
      // move from expression name field to data type field with the keyboard
      cy.ouiaId("edit-expression-name").realPress("Tab").realPress("Tab");

      // open the data type menu
      cy.ouiaId("edit-expression-data-type").as("expressionDataType").find("button").should("be.focused").click();

      // check the data type menu is open
      cy.get("@expressionDataType").find(".pf-c-select__menu").should("be.visible");

      // set data type to "date". This only works with realPress
      cy.focused()
        .realPress("ArrowDown")
        .realPress("ArrowDown")
        .realPress("ArrowDown")
        .realPress("ArrowDown")
        .realPress("ArrowDown")
        .realPress("Enter");

      // check the data type menu is closed
      cy.get("@expressionDataType").find(".pf-c-select__menu").should("not.exist");

      // check data type is now "date"
      cy.get("@expressionDataType").find(".pf-c-select__toggle-text").should("contain.text", "date");

      cy.realPress("Enter");

      // check the expression name field has the new text
      cy.get("@ExpressionNameCell").should("contain.text", "(date)");
    });
  });
});
