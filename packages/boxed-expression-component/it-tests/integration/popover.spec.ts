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

import * as buildEnv from "@kie-tools/build-env";

describe("PopoverMenu Tests", () => {
  before(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);

    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Context
    cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });
  });

  it("Cancel edit of expression data type by pressing escape", () => {
    // open the context menu
    cy.contains("th", "Expression Name").as("ExpressionNameCell").click();

    // open the dataType select
    cy.ouiaId("edit-expression-data-type").as("dataTypeInput").find("button").click({ force: true });

    // select "context" type
    cy.get("@dataTypeInput").contains("context").click({ force: true });

    // context menu should be open. This check is to check that the context menu is not closed after data type selection
    cy.ouiaId("expression-popover-menu").should("be.visible");

    // Cancel the editing
    cy.ouiaId("expression-popover-menu").type("{esc}").should("not.exist");

    // Assert data type not to be changed
    cy.get("@ExpressionNameCell").find(".data-type").should("contain.text", "Undefined");
  });

  it("Cancel edit of expression name by pressing escape", () => {
    // open the context menu
    cy.contains("th", "Expression Name").as("ExpressionNameCell").click();

    // change the expression name
    cy.get("#expression-name").type("{selectall}Newname", { force: true });

    // Cancel the editing
    cy.ouiaId("expression-popover-menu").contains("Edit Expression").click();

    // context menu should be open. This check is to check that the context menu is not closed after data type selection
    cy.ouiaId("expression-popover-menu").should("be.visible").type("{esc}").should("not.exist");

    // Assert expression name not to be changed
    cy.get("@ExpressionNameCell").find(".label").should("contain.text", "Expression Name");
  });

  it("The header cell's in the popover should have original values", () => {
    // open the context menu
    cy.contains("th", "Expression Name").as("ExpressionNameCell").click();

    // expression name input should have original value
    cy.get("#expression-name").should("have.value", "Expression Name");

    // dataType select should have original value
    cy.ouiaId("edit-expression-data-type").should("contain.text", "Undefined");
  });
});
