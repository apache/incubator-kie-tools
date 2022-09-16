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
      cy.get("span.pf-c-select__toggle-text").click({ force: true });
    });

    cy.get("button:contains('boolean')").click({ force: true });

    // close the context menu
    cy.get("body").click();

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
});

describe("Context Expression Tests :: Nested Relations", () => {
  before(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);

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

    // write some text in the innerTable
    cy.get("table table")
      .ouiaId("expression-row-0")
      .find(".data-cell")
      .each((el, index) => {
        cy.wrap(el).type("nested " + (index + 1));
      });

    //click outside to finish editing
    cy.get("body").click();

    cy.get(".boxed-expression").scrollTo("top");
  });

  it("Regression tests: header cell focus", () => {
    cy.contains("th", "Expression Name").focus().wait(0);

    // check the snapshot for regression
    cy.matchImageSnapshot("header_cell_focus");
  });

  it("Regression tests: data cell focus", () => {
    cy.contains("td", "ContextEntry-1").focus().wait(0);

    // check the snapshot for regression
    cy.matchImageSnapshot("data_cell_focus");
  });

  it("Check nested Relation", () => {
    cy.get("th:contains('column-')").should(($siblings) => {
      expect($siblings).to.have.length(3);
      expect($siblings.eq(0)).to.contain("column-3");
      expect($siblings.eq(1)).to.contain("column-1");
      expect($siblings.eq(2)).to.contain("column-2");
    });
  });

  it("Context Expression keyboard navigation", () => {
    // right click on the first data cell
    cy.contains("td", "ContextEntry-1").as("firstCell").rightclick();

    // insert row below
    cy.contains("Insert below").click({ force: true });

    // select first data cell then navigate to counter-cell 2
    cy.get("@firstCell").click({ force: true }).type("{downarrow}{leftarrow}");

    // from counter cell 2 navigate to last cell
    cy.ouiaId("expression-row-1")
      .ouiaId("expression-column-0")
      .should("be.focused")
      .type("{rightarrow}{rightarrow}{rightarrow}{downarrow}");

    // from last cell navigate to the upper cell
    cy.ouiaId("OUIA-Generated-TableRow-2").contains("td", "Select expression").should("be.focused").type("{uparrow}");

    // check if the expression cell of the 2nd row is focused
    cy.ouiaId("expression-row-1").ouiaId("expression-column-2").should("be.focused");
  });

  it("Navigate inside nested tables", () => {
    // from the 3rd cell navigate inside the nested table and left to the edge
    cy.ouiaId("expression-row-0")
      .ouiaId("expression-column-2")
      .not("td td")
      .as("parentCell")
      .click({ force: true })
      .type("{enter}")
      .contains("td", "nested 1")
      .should("be.focused")
      .type("{leftarrow}{leftarrow}{leftarrow}");

    // from the counter-cell of the inner table navigate to "nested 3"
    cy.get("table table")
      .find(".counter-cell")
      .closest("td")
      .should("be.focused")
      .type("{rightarrow}{rightarrow}{rightarrow}{rightarrow}{downarrow}{downarrow}");

    // navigate back to the cell of the parent table
    cy.contains("td", "nested 3").should("be.focused").type("{esc}");

    // navigate left
    cy.get("@parentCell").should("be.focused").type("{leftarrow}");

    // check parent table is focused
    cy.ouiaId("expression-column-1").not("table table").should("be.focused");
  });

  it("Keyboard interaction with contextMenu", () => {
    // open contextMenu and expression menu from the expression cell of the 2nd row and check you are not able to navigate. Then close the contextMenu.
    cy.ouiaId("OUIA-Generated-TableRow-2")
      .contains("td", "Select expression")
      .rightclick()
      .type("{leftarrow}")
      .should("be.focused")
      .click()
      .type("{leftarrow}")
      .should("be.focused")
      .type("{esc}");

    // check the menu is closed
    cy.get(".pf-c-popover__content").should("not.exist");
  });

  it("Keyboard interaction with header's contextMenu in nested decision table", () => {
    //reset the state of the contextMenu. Necessary to pass test.
    cy.get("body").click();

    // focus the 1st header cell inside the nested decision table.
    cy.contains("th", "column-3").as("targetCell");

    // check the menu is closed
    cy.get(".pf-c-popover__content").should("not.exist");

    // open the contextMenu by pressing enter
    cy.wait(100).get("@targetCell").focus().type("{enter}");

    // check the menu is open
    cy.get(".pf-c-popover__content").should("be.visible");
  });
});
