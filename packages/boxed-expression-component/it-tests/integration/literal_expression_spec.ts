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

import { env } from "../../env";
const buildEnv = env;

describe("Literal Expression Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);

    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Literal Expression
    cy.ouiaId("expression-popover-menu").contains("Literal expression").click({ force: true });
  });

  it("Regression without focus", () => {
    // check the snapshot for regression
    cy.matchImageSnapshot("no_focus");
  });

  it("Regression tests with cell focus", () => {
    cy.get(".literal-expression-body").click();

    // check the snapshot for regression
    cy.matchImageSnapshot("data_cell_focus");
  });

  it("Change data type", () => {
    // Change return type to boolean
    cy.get(".literal-expression-header").click();

    cy.ouiaId("edit-expression-data-type").within(($container) => {
      cy.get("span.pf-c-select__toggle-text").click({ force: true });
    });

    cy.get("button:contains('boolean')").click({ force: true });

    // check boolean is now also in grid
    cy.get(".expression-data-type").contains("boolean").should("be.visible");
  });

  it("Check monaco-editor autocompletion", () => {
    cy.get(".literal-expression-body").dblclick().type("abs");
    cy.get("div[class='monaco-list-rows']:contains('abs(n)')").should("be.visible").click();
    cy.get("div[class='monaco-list-rows']").should("not.be.visible");
    cy.get(".editable-cell-value").should("have.text", "abs()");
  });
});
