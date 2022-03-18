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

describe("Context Expression Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);
  });

  it("Define context expression", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Context
    cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });

    // Assert some content
    cy.ouiaId("expression-grid-table").should("contain.text", "ContextEntry-1");
  });

  it("Define nested Decision Table and sync output type", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Context
    cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });

    // Invoke Logic type selector for first context entry
    cy.ouiaId("expression-row-0").within(($row) => {
      cy.ouiaId("expression-column-2").click();
    });

    // Set first context entry as Decision Table
    cy.ouiaId("expression-popover-menu").contains("Decision Table").click({ force: true });

    // Change First context entry to return boolean
    cy.ouiaId("expression-row-0").within(($row) => {
      cy.ouiaId("expression-column-1").contains("ContextEntry-1").click();
    });
    cy.ouiaId("edit-expression-data-type").within(($container) => {
      cy.get("input").click({ force: true });
    });

    cy.get("button:contains('boolean')").click({ force: true });

    // check boolean is now also in decision table header
    cy.ouiaType("expression-column-header-cell-info").contains("boolean").should("be.visible");
  });

  it("Define nested Decision Table", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Context
    cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });

    // Invoke Logic type selector for first context entry
    cy.ouiaId("expression-row-0").within(($row) => {
      cy.ouiaId("expression-column-2").click();
    });

    // Set first context entry as Decision Table
    cy.ouiaId("expression-popover-menu").contains("Decision Table").click({ force: true });

    // insert one output column right
    cy.ouiaType("expression-column-header-cell-info").contains("output-1").rightclick();
    cy.contains("Insert right").click({ force: true });

    // insert one output column left
    cy.ouiaType("expression-column-header-cell-info").contains("output-1").rightclick();
    cy.contains("Insert left").click({ force: true });

    cy.get("th:contains('output-')").should(($outputs) => {
      expect($outputs).to.have.length(3);
      expect($outputs.eq(0)).to.contain("output-3");
      expect($outputs.eq(1)).to.contain("output-1");
      expect($outputs.eq(2)).to.contain("output-2");
    });
  });

  it("Define nested Decision Table as result", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Context
    cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });

    // Invoke Logic type selector for first context entry
    cy.ouiaId("OUIA-Generated-TableRow-2").within(($row) => {
      cy.contains("Select expression").click();
    });

    // Set first context entry as Decision Table
    cy.ouiaId("expression-popover-menu").contains("Decision Table").click({ force: true });

    // insert one output column right
    cy.ouiaType("expression-column-header-cell-info").contains("output-1").rightclick();
    cy.contains("Insert right").click({ force: true });

    // insert one output column left
    cy.ouiaType("expression-column-header-cell-info").contains("output-1").rightclick();
    cy.contains("Insert left").click({ force: true });

    cy.get("th:contains('output-')").should(($outputs) => {
      expect($outputs).to.have.length(4);
      expect($outputs.eq(0)).to.contain("output-1");
      expect($outputs.eq(1)).to.contain("output-3");
      expect($outputs.eq(2)).to.contain("output-1");
      expect($outputs.eq(3)).to.contain("output-2");
    });
  });

  it("Define nested Relation", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Context
    cy.ouiaId("expression-popover-menu").contains("Context").click({ force: true });

    // Invoke Logic type selector for first context entry
    cy.ouiaId("expression-row-0").within(($row) => {
      cy.ouiaId("expression-column-2").click();
    });

    // Set first context entry as Relation
    cy.ouiaId("expression-popover-menu").contains("Relation").click({ force: true });

    // insert one column right
    cy.ouiaType("expression-column-header-cell-info").contains("column-1").rightclick();
    cy.contains("Insert right").click({ force: true });

    // insert one column left
    cy.ouiaType("expression-column-header-cell-info").contains("column-1").rightclick();
    cy.contains("Insert left").click({ force: true });

    cy.get("th:contains('column-')").should(($siblings) => {
      expect($siblings).to.have.length(3);
      expect($siblings.eq(0)).to.contain("column-3");
      expect($siblings.eq(1)).to.contain("column-1");
      expect($siblings.eq(2)).to.contain("column-2");
    });
  });
});
