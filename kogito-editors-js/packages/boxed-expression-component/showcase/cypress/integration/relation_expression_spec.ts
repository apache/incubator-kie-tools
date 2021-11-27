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

/// <reference types="Cypress" />

describe("Relation Expression Tests", () => {
  beforeEach(() => {
    cy.visit("/");
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
      uid: "id1",
      isHeadless: false,
      logicType: "Relation",
      columns: relationColumns(columnSize),
      rows: relationRows(columnSize, rowSize),
    };
  };

  it("Define 50x50 Relation expression", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Relation
    cy.ouiaId("expression-popover-menu").contains("Relation").click({ force: true });

    // Define 50x50 Relation
    cy.ouiaId("edit-expression-json").click();

    cy.ouiaId("typed-expression-json").clear();
    cy.ouiaId("typed-expression-json")
      .invoke("val", JSON.stringify(relation(50, 50)))
      .type(" ");

    cy.ouiaId("confirm-expression-json").click();

    cy.ouiaId("expression-grid-table").should("contain.text", "row 49 column 49");
  });

  it("Insert bellow", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Relation
    cy.ouiaId("expression-popover-menu").contains("Relation").click({ force: true });

    // Define 50x50 Relation
    cy.ouiaId("edit-expression-json").click();

    cy.ouiaId("typed-expression-json").clear();
    cy.ouiaId("typed-expression-json")
      .invoke("val", JSON.stringify(relation(3, 3)))
      .type(" ");

    cy.ouiaId("confirm-expression-json").click();

    cy.ouiaId("expression-grid-table").contains("row 1 column 1").rightclick();
    cy.ouiaId("expression-table-handler-menu").contains("Insert below").click({ force: true });

    cy.ouiaId("expression-row-2").within(($row) => {
      cy.ouiaId("expression-column-0").should("have.text", "3");
      cy.ouiaId("expression-column-1").should("have.text", "");
      cy.ouiaId("expression-column-2").should("have.text", "");
      cy.ouiaId("expression-column-3").should("have.text", "");
    });
  });
});
