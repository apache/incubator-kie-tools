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

describe("Keyboard Navigation Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);
  });

  describe("Decision Table Keyboard Navigation Tests", () => {
    beforeEach("Creates a decision table", () => {
      // Entry point for each new expression
      cy.ouiaId("expression-container").click();

      // Define new expression as Relation
      cy.ouiaId("expression-popover-menu").contains("Decision Table").click({ force: true });

      cy.get(".editable-cell:eq(0)").rightclick();

      cy.ouiaId("expression-table-handler-menu").contains("Insert below").click({ force: true });

      cy.get(".editable-cell:eq(0)").rightclick();

      cy.ouiaId("expression-table-handler-menu").contains("Insert below").click({ force: true });

      cy.get(".editable-cell:eq(0)").rightclick();

      cy.contains("Insert right").click({ force: true });
    });

    it("Navigate around", () => {
      cy.get(".editable-cell:eq(0)").type(
        "{rightarrow}{rightarrow}{rightarrow}{leftarrow}{downarrow}{downarrow}{leftarrow}"
      );

      cy.get("tbody tr:eq(2) td:eq(2)").should("be.focused");
      cy.get("tbody tr:eq(2) td:eq(1)").should("not.be.focused");
    });

    it("Go against edges", () => {
      cy.get("tbody tr:eq(0) td:eq(1)")
        .click({ force: true })
        .type("{uparrow}{uparrow}{uparrow}")
        .should("be.focused")
        .type("{rightarrow}{rightarrow}{rightarrow}{rightarrow}");

      cy.get("tbody tr:eq(0) td:eq(4)")
        .should("be.focused")
        .click({ force: true })
        .type("{downarrow}{downarrow}{downarrow}{downarrow}");

      cy.get("tbody tr:eq(2) td:eq(4)").should("be.focused");
    });

    it("Edit cells with enter/esc", function () {
      cy.get("tbody tr:eq(0) td:eq(1)").click({ force: true }).type("{enter}TestInput");

      cy.get("tbody tr:eq(0) td:eq(2)").click({ force: true });

      cy.get("tbody tr:eq(0) td:eq(1) textarea").should("have.text", "TestInput");
    });
  });
});
