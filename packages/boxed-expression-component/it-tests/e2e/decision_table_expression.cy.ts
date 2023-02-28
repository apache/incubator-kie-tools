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
      logicType: "Decision table",
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
    cy.ouiaId("expression-popover-menu").contains("Decision table").click({ force: true });

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
    cy.ouiaId("expression-popover-menu").contains("Decision table").click({ force: true });

    // Define Decision Table
    cy.ouiaId("edit-expression-json").click();

    cy.ouiaId("typed-expression-json").clear();
    cy.ouiaId("typed-expression-json")
      .invoke("val", JSON.stringify(decisionTable(2, 2, 1, 5)))
      .type(" ");

    cy.ouiaId("confirm-expression-json").click();

    cy.ouiaId("expression-grid-table").contains("in-value-1:1").rightclick({ force: true });
    cy.ouiaId("expression-table-context-menu").contains("Duplicate").click({ force: true });

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

describe("Decision Table Keyboard Navigation Tests", () => {
  beforeEach(() => {
    cy.visit(`http://localhost:${buildEnv.boxedExpressionComponent.dev.port}/`);

    // Entry point for each new expression
    cy.ouiaId("expression-container").click();

    // Define new expression as Relation
    cy.ouiaId("expression-popover-menu").contains("Decision table").click({ force: true });

    // open contextMenu from first input cell
    cy.ouiaId("expression-column-1").first().as("firstInputCell").rightclick();

    // create a row below
    cy.ouiaId("expression-table-context-menu").contains("Insert below").click({ force: true });

    // open contextMenu from first input cell
    cy.get("@firstInputCell").rightclick();

    // create a row below
    cy.ouiaId("expression-table-context-menu").contains("Insert below").click({ force: true });

    // open contextMenu from first input cell
    cy.get("@firstInputCell").rightclick();

    // create a column right
    cy.contains("Insert right").click({ force: true });

    // write some text in the table
    cy.get(".data-cell").each((cell, cellIndex) => {
      cy.wrap(cell).type(`{enter}{selectall}cell ${cellIndex + 1}`);
      cy.realPress("Tab");
      cy.realPress("Escape");
    });
  });

  it("Regression tests: input header cell focus", () => {
    cy.contains("th", "input-1").focus().wait(0);

    // check the snapshot for regression
    cy.matchImageSnapshot("input_header_cell_focus");
  });

  it("Regression tests: output header cell focus", () => {
    cy.contains("th", "output-1").focus().wait(0);

    // check the snapshot for regression
    cy.matchImageSnapshot("input_header_cell_focus");
  });

  it("Navigate around data cells", () => {
    // from the cell 1, go to cell 10
    cy.contains("td", /cell 1/)
      .type("{rightarrow}{rightarrow}{rightarrow}{leftarrow}{downarrow}{downarrow}{leftarrow}")
      // check the cell 10 is focused
      .focused()
      .should("contain.text", "cell 10");

    // check the cell 9 is not focused
    cy.contains("td", /cell 9/).should("not.be.focused");
  });

  // FIXME: Tiago -> Rewrite those tests
  //
  // it("Navigate around header and data cells", () => {
  //   // from the cell 1, go up
  //   cy.contains("td", /cell 1/)
  //     .type("{uparrow}")
  //     // check the cell "input-1" is focused
  //     .focused()
  //     .should("contain.text", "input-1")
  //     // go right 2 times
  //     .type("{rightarrow}{rightarrow}")
  //     // check the cell "Expression Name" is focused
  //     .focused()
  //     .should("contain.text", "Expression Name")
  //     // go down
  //     .type("{downarrow}")
  //     // check the cell "output-1" is focused
  //     .focused()
  //     .should("contain.text", "output-1")
  //     // go right
  //     .type("{rightarrow}")
  //     // check the cell "annotation-1" is focused
  //     .focused()
  //     .should("contain.text", "annotation-1")
  //     // go down
  //     .type("{downarrow}")
  //     // check the cell "cell-4" is focused
  //     .focused()
  //     .should("contain.text", "cell 4")
  //     // go left and up
  //     .type("{leftarrow}{uparrow}")
  //     // check the cell "output-1" is focused
  //     .focused()
  //     .should("contain.text", "output-1");
  // });

  // it("Navigate around using tab", () => {
  //   // from the cell 2, go to cell 5
  //   cy.contains("td", /cell 2/).focus();
  //   cy.realPress("Tab");
  //   cy.realPress("Tab");
  //   cy.realPress("Tab");
  //   cy.realPress("Tab");
  //   cy.focused().should("contains.text", "cell 6");

  //   // from the cell 11, go to cell 12
  //   cy.contains("td", /cell 11/).focus();
  //   cy.realPress("Tab");
  //   cy.realPress("Tab");
  //   cy.realPress("Tab");
  //   cy.realPress("Tab");

  //   cy.focused().should("contains.text", "cell 12");
  // });

  // it("The last cell should keep the focus after tab press", () => {
  //   // from the cell 12
  //   cy.get("[data-xposition='4'][data-yposition='4']")
  //     .as("cell-12")
  //     .should("contain.text", "cell 12")
  //     .type("{enter}")
  //     .focused();

  //   cy.realPress("Tab");

  //   cy.get("@cell-12").should("be.focused");
  // });

  // it("Go against edges", () => {
  //   // from the cell 1, go to cell 4
  //   cy.contains("th", /input-1/)
  //     .click({ force: true })
  //     .type("{uparrow}{uparrow}{uparrow}")
  //     .should("be.focused")
  //     .type("{rightarrow}{rightarrow}{rightarrow}{rightarrow}")
  //     .focused()
  //     .should("contains", /annotation-1/);

  //   // from the cell 4, go to cell 12
  //   cy.contains("td", /cell 4/)
  //     .click({ force: true })
  //     .type("{downarrow}{downarrow}{downarrow}{downarrow}");

  //   // check the cell 12 is focused
  //   cy.contains("td", /cell 12/).should("be.focused");
  // });

  // it("Edit cells appending text selecting the Td", () => {
  //   // from the cell 1, enter edit mode, write TestInput and press enter to save
  //   cy.get("[data-xposition='1'][data-yposition='2']")
  //     .as("cell-1")
  //     .should("contain.text", "cell 1")
  //     .type("{enter}TestAppend{enter}");

  //   // cell 5 should be focused
  //   cy.contains("td", /cell 5/).should("be.focused");

  //   // check the cell 1 now has "TestInput" text
  //   cy.get("@cell-1").find(".editable-cell-textarea").should("have.text", "cell 1TestAppend");
  // });

  // it("Edit a cell in the last row appending text selecting the Td", () => {
  //   // from the cell 9, enter edit mode, write TestInput and press enter to save
  //   cy.contains("td", /cell 9/)
  //     .type("{enter}TestLastRowAppend{enter}")
  //     // cell 9 should be focused
  //     .should("be.focused")
  //     // check the cell 9 now has "TestInput" text
  //     .find(".editable-cell-textarea")
  //     .should("contain.text", "cell 9TestLastRowAppend");
  // });

  // it("Edit cells appending text selecting the TextArea", () => {
  //   // from the cell 5, enter edit mode and write TestInput
  //   cy.get("[data-xposition='1'][data-yposition='3']")
  //     .as("textarea-5")
  //     .should("contain.text", "cell 5")
  //     .type("{enter}")
  //     .type("TestAppend");

  //   // click on cell 6
  //   cy.contains("td", /cell 6/).click({ force: true });

  //   // check the cell 5 now has "TestInput" text
  //   cy.get("@textarea-5").find(".editable-cell-textarea").should("have.text", "-cell 5TestAppend");
  // });

  // it("Edit cells overwriting text selecting the Td", () => {
  //   // from the cell 2, enter edit mode and write TestInput
  //   cy.get("[data-xposition='2'][data-yposition='2']")
  //     .as("cell-2")
  //     .should("contain.text", "cell 2")
  //     .type("TestOverwrite");

  //   // click on cell 3
  //   cy.contains("td", /cell 3/).click({ force: true });

  //   // check the cell 1 now has "TestInput" text
  //   cy.get("@cell-2").find(".editable-cell-textarea").should("have.text", "TestOverwrite");
  // });

  // it("Edit cells overwriting text selecting the TextArea", () => {
  //   // from the cell 6, enter edit mode and write TestInput
  //   cy.get("[data-xposition='2'][data-yposition='3']")
  //     .as("textarea-6")
  //     .should("contain.text", "cell 6")
  //     .type("TestOverwrite");

  //   // click on cell 7
  //   cy.contains("td", /cell 7/).click({ force: true });

  //   // check the cell 1 now has "TestInput" text
  //   cy.get("@textarea-6").find(".editable-cell-textarea").should("have.text", "TestOverwrite");
  // });

  // it("Insert a newline in a cell pressing Ctrl+Enter", () => {
  //   // from the cell 3, enter edit mode and write TestInput
  //   cy.get("[data-xposition='3'][data-yposition='2']")
  //     .as("textarea-3")
  //     .should("contain.text", "cell 3")
  //     .type("{enter}{ctrl+enter}newline");

  //   // click on cell 7
  //   cy.contains("td", /cell 7/).click({ force: true });

  //   // check the cell 1 now has "TestInput" text
  //   cy.get("@textarea-3").find(".editable-cell-textarea").should("have.text", "cell 3\nnewline");
  // });

  // it("Edit a cell using the suggestions", () => {
  //   // from the cell 4, write sum, press enter to accept the suggestion
  //   cy.get("[data-xposition='4'][data-yposition='2']")
  //     .as("cell-4")
  //     .should("contain.text", "cell 4")
  //     .type("sum{enter}")
  //     .prev()
  //     .click();

  //   cy.get("@cell-4")
  //     // cell 4 should be focused
  //     .should("not.be.focused")
  //     // check the cell 4 now has "TestInput" text
  //     .find(".editable-cell-textarea")
  //     .should("contain.text", "sum()");
  // });

  // it("Keyboard interaction with contextMenu", () => {
  //   // select cell 3 and open the contextMenu with rightclick
  //   cy.get("[data-xposition='3'][data-yposition='2']")
  //     .as("cell-3")
  //     .should("contain.text", "cell 3")
  //     .click()
  //     .rightclick();

  //   // check the contextMenu is open
  //   cy.get(".table-handler").should("be.visible");

  //   // try to navigate left with no success
  //   cy.get("@cell-3").type("{leftarrow}").should("be.focused");

  //   // close the menu
  //   cy.realPress("Escape");
  // });

  // describe("Keyboard interaction with annotation cell", () => {
  //   beforeEach(() => {
  //     cy.get("[data-xposition='4'][data-yposition='1']").as("annotationCell").focus().type("{enter}");

  //     cy.get("@annotationCell").find("input:text").as("annotationInput").clear();
  //   });

  //   it("Edit and save with enter key", () => {
  //     // from the annotation cell, edit the text then press enter to finish editing
  //     cy.get("@annotationInput").type("annotation-1 edited{enter}");

  //     cy.get("@annotationCell").should("have.text", "annotation-1 edited");

  //     cy.get("@annotationCell").should("be.focused").get("input").should("not.exist");
  //   });

  //   it("Cancel editing", () => {
  //     // from the annotation cell, edit the text then press esc to cancel editing
  //     cy.get("@annotationInput").type("not to save text{esc}");

  //     cy.get("@annotationCell").should("have.text", "annotation-1").should("not.contain.text", "not to save text");

  //     cy.get("@annotationCell").should("be.focused").get("input").should("not.exist");
  //   });

  //   it("Edit and save clicking outside", () => {
  //     // from the annotation cell, edit the text
  //     cy.get("@annotationInput").type("annotation-1 edited");

  //     // click outside the annotation cell to finish editing
  //     cy.get(".expression-title").click();

  //     cy.get("@annotationCell").should("have.text", "annotation-1 edited");

  //     cy.get("@annotationCell").should("not.be.focused").get("input").should("not.exist");
  //   });
  // });
});
