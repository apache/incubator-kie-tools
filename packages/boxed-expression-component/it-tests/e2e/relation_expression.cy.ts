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

import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";
import { env } from "../../env";
const buildEnv = env;

const cmdOrControlKey = getOperatingSystem() === OperatingSystem.MACOS ? "Meta" : "Control";

describe("Relation Expression Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);
  });

  const relationColumns = (size: number) => {
    const columns = new Array(size);
    for (let index = 0; index < size; index++) {
      columns[index] = { id: `column-${index}`, name: `column-${index}`, dataType: "<Undefined>", width: 150 };
    }

    return columns;
  };

  const relationRows = (columnSize: number, rowSize: number) => {
    const rows = new Array(rowSize);
    for (let rowIndex = 0; rowIndex < rowSize; rowIndex++) {
      const row = { id: `row-${rowIndex}`, cells: new Array(columnSize) };
      for (let columnIndex = 0; columnIndex < columnSize; columnIndex++) {
        row.cells[columnIndex] = `row ${rowIndex} column ${columnIndex}`;
      }
      rows[rowIndex] = row;
    }
    return rows;
  };

  const relation = (columnSize: number, rowSize: number) => {
    return {
      name: "Expression Name",
      dataType: "<Undefined>",
      id: "id1",
      isHeadless: false,
      logicType: "Relation",
      columns: relationColumns(columnSize),
      rows: relationRows(columnSize, rowSize),
    };
  };

  function defineRelationExpression(columnSize: number, rowsSize: number) {
    // Define new expression as Relation
    cy.ouiaId("expression-popover-menu").contains("Relation").click({ force: true });

    cy.ouiaId("edit-expression-json").click();

    cy.ouiaId("typed-expression-json").clear();
    cy.ouiaId("typed-expression-json")
      .invoke("val", JSON.stringify(relation(columnSize, rowsSize)))
      .type(" ");

    cy.ouiaId("confirm-expression-json").click();
  }

  it("Define 50x50 Relation expression", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    defineRelationExpression(50, 50);

    cy.ouiaId("expression-grid-table").should("contain.text", "row 49 column 49");
  });

  it("Insert below", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    defineRelationExpression(3, 3);

    cy.ouiaId("expression-grid-table").contains("td", "row 1 column 1").rightclick();
    cy.ouiaId("expression-table-context-menu").contains("Insert below").click({ force: true });

    cy.ouiaId("expression-row-2").within(($row) => {
      cy.ouiaId("expression-column-0").should("have.text", "3");
      cy.ouiaId("expression-column-1").should("have.text", "");
      cy.ouiaId("expression-column-2").should("have.text", "");
      cy.ouiaId("expression-column-3").should("have.text", "");
    });
  });

  it("copy and paste", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    defineRelationExpression(3, 1);

    cy.ouiaId("expression-grid-table").contains("td", "row 0 column 0").rightclick();
    cy.ouiaId("expression-table-context-menu").contains("Insert below").click({ force: true });

    cy.ouiaId("expression-grid-table").contains("td", "row 0 column 0").click();

    // select first two cells
    cy.realPress(["Shift", "ArrowRight"]);

    // copy
    cy.realPress([cmdOrControlKey, "C"]);

    // paste
    cy.ouiaId("expression-row-1").ouiaId("expression-column-1").click();
    cy.realPress([cmdOrControlKey, "V"]);

    cy.ouiaId("expression-row-1").within(($row) => {
      cy.ouiaId("expression-column-0").should("have.text", "2");
      cy.ouiaId("expression-column-1").should("contain.text", "row 0 column 0");
      cy.ouiaId("expression-column-2").should("contain.text", "row 0 column 1");
      cy.ouiaId("expression-column-3").should("have.text", "");
    });
  });

  it("Keyboard navigation", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    defineRelationExpression(3, 2);

    cy.contains("td", "row 1 column 2").as("targetCell");

    // go to 2nd row 2nd cell then navigate to around and stop at 2nd row, 4th cell. Then write text
    cy.contains("td", "row 1 column 1").type(
      "{rightarrow}{rightarrow}{rightarrow}{rightarrow}{uparrow}{downarrow}{enter}"
    );

    cy.realPress([cmdOrControlKey, "A"]);
    cy.realPress("Backspace");
    cy.realPress("N");
    cy.realPress("e");
    cy.realPress("w");
    cy.realPress("t");
    cy.realPress("e");
    cy.realPress("x");
    cy.realPress("t");
    cy.realPress("Enter");

    // exit edit mode and check 2nd row, 4th cell has the new text
    cy.contains("td", "Newtext").ouiaId("editable-cell-raw-value").should("have.text", "Newtext");
  });

  it("Regression tests: focus on the first data cell", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    defineRelationExpression(3, 2);

    // go to first cell and open contextMenu
    cy.contains("td", "row 0 column 0").click().wait(0);

    // check the snapshot for regression
    cy.matchImageSnapshot("data_cell_focus");
  });
});
