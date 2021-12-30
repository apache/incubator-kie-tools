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

describe("Decision Table Expression Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);
  });

  const inColumns = (size: number) => {
    const columns = new Array(size);
    for (let index = 0; index < size; index++) {
      columns[index] = { id: `in-column-${index}`, name: `in-column-${index}`, dataType: "<Undefined>" };
    }
    return columns;
  };

  const outColumns = (size: number) => {
    const columns = new Array(size);
    for (let index = 0; index < size; index++) {
      columns[index] = { id: `out-column-${index}`, name: `out-column-${index}`, dataType: "<Undefined>" };
    }
    return columns;
  };

  const annotationColumns = (size: number) => {
    const columns = new Array(size);
    for (let index = 0; index < size; index++) {
      columns[index] = { id: `annotation-${index}`, name: `annotation-${index}` };
    }
    return columns;
  };

  const rows = (inSize: number, outSize: number, annotationSize: number, rulesSize: number) => {
    const rows = new Array(rulesSize);
    for (let rowIndex = 0; rowIndex < rulesSize; rowIndex++) {
      const inValues = new Array(inSize);
      for (let index = 0; index < inSize; index++) {
        inValues[index] = `in-value-${rowIndex}:${index}`;
      }

      const outValues = new Array(outSize);
      for (let index = 0; index < outSize; index++) {
        outValues[index] = `out-value-${rowIndex}:${index}`;
      }

      const annotationValues = new Array(annotationSize);
      for (let index = 0; index < annotationSize; index++) {
        annotationValues[index] = `annotation-value-${rowIndex}:${index}`;
      }

      rows[rowIndex] = {
        id: `row-${rowIndex}`,
        inputEntries: inValues,
        outputEntries: outValues,
        annotationEntries: annotationValues,
      };
    }
    return rows;
  };

  const decisionTable = (inSize: number, outSize: number, annotationSize: number, rulesSize: number) => {
    return {
      name: "Expression Name",
      dataType: "<Undefined>",
      uid: "id1",
      logicType: "Decision Table",
      aggregation: "",
      input: inColumns(inSize),
      output: outColumns(outSize),
      annotations: annotationColumns(annotationSize),
      rules: rows(inSize, outSize, annotationSize, rulesSize),
    };
  };

  it("Define 50x50 Decision Table expression", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Relation
    cy.ouiaId("expression-popover-menu").contains("Decision Table").click({ force: true });

    // Define 50x50 Decision Table
    cy.ouiaId("edit-expression-json").click();

    cy.ouiaId("typed-expression-json").clear();
    cy.ouiaId("typed-expression-json")
      .invoke("val", JSON.stringify(decisionTable(20, 20, 10, 50)))
      .type(" ");

    cy.ouiaId("confirm-expression-json").click();

    cy.ouiaId("expression-grid-table").should("contain.text", "in-value-49:19");
    cy.ouiaId("expression-grid-table").should("contain.text", "out-value-49:19");
    cy.ouiaId("expression-grid-table").should("contain.text", "annotation-value-49:9");
  });

  it("Duplicate row", () => {
    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Relation
    cy.ouiaId("expression-popover-menu").contains("Decision Table").click({ force: true });

    // Define Decision Table
    cy.ouiaId("edit-expression-json").click();

    cy.ouiaId("typed-expression-json").clear();
    cy.ouiaId("typed-expression-json")
      .invoke("val", JSON.stringify(decisionTable(2, 2, 1, 5)))
      .type(" ");

    cy.ouiaId("confirm-expression-json").click();

    cy.ouiaId("expression-grid-table").contains("in-value-1:1").rightclick();
    cy.ouiaId("expression-table-handler-menu").contains("Duplicate").click({ force: true });

    cy.ouiaId("expression-row-2").within(($row) => {
      cy.ouiaId("expression-column-0").should("have.text", "3");
      cy.ouiaId("expression-column-1").should("contain.text", "in-value-1:0");
      cy.ouiaId("expression-column-2").should("contain.text", "in-value-1:1");
      cy.ouiaId("expression-column-3").should("contain.text", "out-value-1:0");
      cy.ouiaId("expression-column-4").should("contain.text", "out-value-1:1");
      cy.ouiaId("expression-column-5").should("contain.text", "annotation-value-1:0");
    });
  });
});
