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

  it("Define 50x50 Relation expression", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Relation
    cy.ouiaId("expression-popover-menu").contains("Relation").click({ force: true });

    // Define 50x50 Relation
    cy.ouiaId("edit-expression-json").click();

    const relationColumns = new Array(50);
    for (let index = 0; index < 50; index++) {
      relationColumns[index] = { name: `column-${index}`, dataType: "<Undefined>", width: 150 };
    }

    const relationRows = new Array(50);
    for (let rowIndex = 0; rowIndex < 50; rowIndex++) {
      const row = new Array(50);
      for (let columnIndex = 0; columnIndex < 50; columnIndex++) {
        row[columnIndex] = `row ${rowIndex} column ${columnIndex}`;
      }
      relationRows[rowIndex] = row;
    }

    const bigRelation = {
      name: "Expression Name",
      dataType: "<Undefined>",
      uid: "id1",
      isHeadless: false,
      logicType: "Relation",
      columns: relationColumns,
      rows: relationRows,
    };

    cy.ouiaId("typed-expression-json").clear();
    cy.ouiaId("typed-expression-json").invoke("val", JSON.stringify(bigRelation)).type(" ");

    cy.ouiaId("confirm-expression-json").click();

    cy.ouiaId("expression-grid-table").should("contain.text", "row 49 column 49");
  });
});
