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

import * as buildEnv from "@kie-tools/build-env";

describe("Context Expression Tests", () => {
  before(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);

    /* TODO: popover.spec: uncomment me */
    // // Entry point for each new expression
    // cy.ouiaId("expression-container").click();
    //
    // // Define new expression as Context
    // cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });
  });

  it("should user can cancel edit of select in context menu of header cell by pressing escape", () => {
    // open the context menu
    cy.contains("th", "Expression Name").as("ExpressionNameCell").click();

    // open the dataType select
    cy.ouiaId("edit-expression-data-type").as("dataTypeInput").find("> button").click({ force: true });

    // select "context" type
    cy.get("@dataTypeInput").contains("context").click({ force: true });

    // context menu should be open
    cy.ouiaId("expression-popover-menu").should("be.visible");

    // Cancel the editing
    cy.ouiaId("expression-popover-menu").type("{esc}").wait(500);

    // Assert some content
    cy.get("@ExpressionNameCell").find(".data-type").should("contain.text", "Undefined");
  });

  it("the header cell's context menu should have original values", () => {
    // open the context menu
    cy.contains("th", "Expression Name").as("ExpressionNameCell").click();

    // open the dataType select
    cy.ouiaId("edit-expression-data-type").should("contain.text", "Undefined");

    cy.get("body").click();
  });
});
