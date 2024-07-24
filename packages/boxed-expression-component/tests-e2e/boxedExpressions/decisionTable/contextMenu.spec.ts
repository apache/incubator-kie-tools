/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { expect, test } from "../../__fixtures__/base";

test.describe("Decision table context menu", () => {
  test.describe("Decision rules control", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openDecisionTable();
    });

    test("shouldn't render columns context menu", async ({ bee }) => {
      const rowHeading = bee.expression.asDecisionTable().cellAt({ row: 1, column: 0 });
      await rowHeading.contextMenu.open();
      await expect(rowHeading.contextMenu.heading("DECISION RULE")).toBeAttached();
      await expect(rowHeading.contextMenu.heading("SELECTION")).toBeAttached();
      await expect(rowHeading.contextMenu.heading("INPUT CLAUSE")).not.toBeAttached();
      await expect(rowHeading.contextMenu.heading("OUTPUT CLAUSE")).not.toBeAttached();
      await expect(rowHeading.contextMenu.heading("RULE ANNOTATION")).not.toBeAttached();
    });

    test("should open decision rules context menu and insert row above", async ({ bee }) => {
      const cell = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
      await cell.fill("test");
      await cell.contextMenu.open();
      await cell.contextMenu.option("Insert above").click();
      await expect(bee.expression.asDecisionTable().cellAt({ row: 2, column: 1 }).content).toContainText("test");
    });

    test("should open decision rules context menu and insert row below", async ({ bee }) => {
      const cell = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
      await cell.fill("test");
      await cell.contextMenu.open();
      await cell.contextMenu.option("Insert below").click();
      await expect(bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).content).toContainText("test");
      await expect(bee.expression.asDecisionTable().cellAt({ row: 2, column: 1 }).content).toContainText("");
    });

    test("should open decision rules context menu and insert multiples rows above", async ({ bee }) => {
      const decisionTable = bee.expression.asDecisionTable();

      await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.open();
      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.option("Insert").click();
      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.button("plus").click();
      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.button("Insert").click();

      await expect(bee.expression.asDecisionTable().cellAt({ row: 4, column: 1 }).content).toContainText("test");
    });

    test("should open decision rules context menu and insert multiples rows below", async ({ bee }) => {
      const decisionTable = bee.expression.asDecisionTable();

      await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");

      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.open();
      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.option("Insert").click();
      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.button("minus").click();
      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.radio("Below").click();
      await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.button("Insert").click();

      await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText("test");
      await expect(decisionTable.cellAt({ row: 2, column: 0 }).content).toBeAttached();
    });

    test("should open decision rules context menu and delete row", async ({ bee }) => {
      const decisionTable = bee.expression.asDecisionTable();

      await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
      await decisionTable.cellAt({ row: 1, column: 1 }).contextMenu.open();
      await decisionTable.cellAt({ row: 1, column: 1 }).contextMenu.option("Insert above").click();
      await expect(decisionTable.cellAt({ row: 2, column: 1 }).content).toContainText("test");
      await decisionTable.cellAt({ row: 1, column: 1 }).contextMenu.open();
      await decisionTable.cellAt({ row: 1, column: 1 }).contextMenu.option("Delete").nth(1).click();
      await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText("test");
    });

    test("should open decision rules context menu and duplicate row", async ({ bee }) => {
      const decisionTable = bee.expression.asDecisionTable();

      await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
      await decisionTable.cellAt({ row: 1, column: 1 }).contextMenu.open();
      await decisionTable.cellAt({ row: 1, column: 1 }).contextMenu.option("Duplicate").click();
      await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText("test");
      await expect(decisionTable.cellAt({ row: 2, column: 1 }).content).toContainText("test");
    });
  });

  test.describe("Columns controls", () => {
    test.describe("Input columns", () => {
      test.beforeEach(async ({ stories }) => {
        await stories.openDecisionTable();
      });

      test("shouldn't render decision rules context menu", async ({ bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.inputHeaderAt(0).contextMenu.open();

        await expect(decisionTable.inputHeaderAt(0).contextMenu.heading("DECISION RULES")).not.toBeAttached();
        await expect(decisionTable.inputHeaderAt(0).contextMenu.heading("SELECTION")).toBeAttached();
        await expect(decisionTable.inputHeaderAt(0).contextMenu.heading("INPUT CLAUSE")).toBeAttached();
        await expect(decisionTable.inputHeaderAt(0).contextMenu.heading("OUTPUT CLAUSE")).not.toBeAttached();
        await expect(decisionTable.inputHeaderAt(0).contextMenu.heading("RULE ANNOTATION")).not.toBeAttached();
      });

      test("should open input column context menu and insert column right", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
        await decisionTable.inputHeaderAt(0).contextMenu.open();
        await decisionTable.inputHeaderAt(0).contextMenu.option("Insert right").click();

        await expect(decisionTable.inputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.inputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and insert column left", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
        await decisionTable.inputHeaderAt(0).contextMenu.open();
        await decisionTable.inputHeaderAt(0).contextMenu.option("Insert left").click();

        await expect(decisionTable.inputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.inputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).not.toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and insert multiples columns on right", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");

        await decisionTable.inputHeaderAt(0).contextMenu.open();

        await decisionTable.inputHeaderAt(0).contextMenu.option("Insert").click();
        await decisionTable.inputHeaderAt(0).contextMenu.button("plus").click();
        await decisionTable.inputHeaderAt(0).contextMenu.radio("To the right").click();
        await decisionTable.inputHeaderAt(0).contextMenu.button("Insert").click();

        await expect(decisionTable.inputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.inputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.inputHeaderAt(2).content).toBeAttached();
        await expect(decisionTable.inputHeaderAt(3).content).toBeAttached();

        await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open input column context menu and insert multiples columns on left", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");

        await decisionTable.inputHeaderAt(0).contextMenu.open();
        await decisionTable.inputHeaderAt(0).contextMenu.option("Insert").click();
        await decisionTable.inputHeaderAt(0).contextMenu.button("minus").click();
        await decisionTable.inputHeaderAt(0).contextMenu.radio("To the left").click();
        await decisionTable.inputHeaderAt(0).contextMenu.button("Insert").click();

        await expect(decisionTable.inputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.inputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).not.toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and delete column", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");

        await decisionTable.inputHeaderAt(0).contextMenu.open();
        await decisionTable.inputHeaderAt(0).contextMenu.option("Insert left").click();

        await expect(decisionTable.inputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.inputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).not.toContainText("test");

        await decisionTable.inputHeaderAt(0).contextMenu.open();
        await decisionTable.inputHeaderAt(0).contextMenu.option("Delete").click();

        await expect(decisionTable.inputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(4);
      });
    });

    test.describe("Output columns", () => {
      test.beforeEach(async ({ stories }) => {
        await stories.openDecisionTable();
      });

      test("shouldn't render decision rules context menu", async ({ bee }) => {
        const outputHeader = bee.expression.asDecisionTable().outputHeaderAt(0);
        await outputHeader.contextMenu.open();

        await expect(outputHeader.contextMenu.heading("DECISION RULES")).not.toBeAttached();
        await expect(outputHeader.contextMenu.heading("SELECTION")).toBeAttached();
        await expect(outputHeader.contextMenu.heading("INPUT CLAUSE")).not.toBeAttached();
        await expect(outputHeader.contextMenu.heading("OUTPUT CLAUSE")).toBeAttached();
        await expect(outputHeader.contextMenu.heading("RULE ANNOTATION")).not.toBeAttached();
      });

      test("should open output column context menu and insert column right", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();
        await decisionTable.cellAt({ row: 1, column: 2 }).fill("test");
        await decisionTable.outputHeaderAt(0).contextMenu.open();
        await decisionTable.outputHeaderAt(0).contextMenu.option("Insert right").click();

        await expect(decisionTable.expressionHeaderCell.content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and insert column left", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 2 }).fill("test");
        await decisionTable.outputHeaderAt(0).contextMenu.open();
        await decisionTable.outputHeaderAt(0).contextMenu.option("Insert left").click();

        await expect(decisionTable.expressionHeaderCell.content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).not.toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and insert multiples columns on right", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 2 }).fill("test");
        await decisionTable.outputHeaderAt(0).contextMenu.open();
        await decisionTable.outputHeaderAt(0).contextMenu.option("Insert").click();
        await decisionTable.outputHeaderAt(0).contextMenu.button("plus").click();
        await decisionTable.outputHeaderAt(0).contextMenu.button("Insert").click();

        await expect(decisionTable.expressionHeaderCell.content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(2).content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(3).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open output column context menu and insert multiples columns on left", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 2 }).fill("test");

        await decisionTable.outputHeaderAt(0).contextMenu.open();
        await decisionTable.outputHeaderAt(0).contextMenu.option("Insert").click();
        await decisionTable.outputHeaderAt(0).contextMenu.button("minus").click();
        await decisionTable.outputHeaderAt(0).contextMenu.radio("To the left").click();
        await decisionTable.outputHeaderAt(0).contextMenu.button("Insert").click();

        await expect(decisionTable.expressionHeaderCell.content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).not.toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and delete column", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 2 }).fill("test");
        await decisionTable.outputHeaderAt(0).contextMenu.open();
        await decisionTable.outputHeaderAt(0).contextMenu.option("Insert left").click();

        await expect(decisionTable.expressionHeaderCell.content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).not.toContainText("test");

        await decisionTable.outputHeaderAt(1).contextMenu.open();
        await decisionTable.outputHeaderAt(1).contextMenu.option("Delete").click();

        await expect(decisionTable.expressionHeaderCell.content).toBeAttached();
        await expect(decisionTable.outputHeaderAt(1).content).not.toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(4);
      });
    });

    test.describe("Rule annotation columns", () => {
      test.beforeEach(async ({ stories }) => {
        await stories.openDecisionTable();
      });

      test("shouldn't render decision rules context menu", async ({ bee }) => {
        const annotationHeader = bee.expression.asDecisionTable().annotationHeaderAt(0);
        await annotationHeader.contextMenu.open();
        await expect(annotationHeader.contextMenu.heading("DECISION RULES")).not.toBeAttached();
        await expect(annotationHeader.contextMenu.heading("SELECTION")).toBeAttached();
        await expect(annotationHeader.contextMenu.heading("INPUT CLAUSE")).not.toBeAttached();
        await expect(annotationHeader.contextMenu.heading("OUTPUT CLAUSE")).not.toBeAttached();
        await expect(annotationHeader.contextMenu.heading("RULE ANNOTATION")).toBeAttached();
      });

      test("should open annotation column context menu and insert column right", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();
        await decisionTable.cellAt({ row: 1, column: 3 }).fill("test");

        await decisionTable.annotationHeaderAt(0).contextMenu.open();
        await decisionTable.annotationHeaderAt(0).contextMenu.option("Insert right").click();

        await expect(decisionTable.annotationHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.annotationHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and insert column left", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 3 }).fill("test");
        await decisionTable.annotationHeaderAt(0).contextMenu.open();
        await decisionTable.annotationHeaderAt(0).contextMenu.option("Insert left").click();

        await expect(decisionTable.annotationHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.annotationHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 4 }).content).toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).not.toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and insert multiples columns on right", async ({
        page,
        bee,
      }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 3 }).fill("test");

        await decisionTable.annotationHeaderAt(0).contextMenu.open();
        await decisionTable.annotationHeaderAt(0).contextMenu.option("Insert").click();
        await decisionTable.annotationHeaderAt(0).contextMenu.button("plus").click();
        await decisionTable.annotationHeaderAt(0).contextMenu.radio("To the right").click();
        await decisionTable.annotationHeaderAt(0).contextMenu.button("Insert").click();

        await expect(decisionTable.annotationHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.annotationHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.annotationHeaderAt(2).content).toBeAttached();
        await expect(decisionTable.annotationHeaderAt(3).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open annotation column context menu and insert multiples columns on left", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 3 }).fill("test");
        await decisionTable.annotationHeaderAt(0).contextMenu.open();
        await decisionTable.annotationHeaderAt(0).contextMenu.option("Insert").click();
        await decisionTable.annotationHeaderAt(0).contextMenu.button("minus").click();
        await decisionTable.annotationHeaderAt(0).contextMenu.radio("To the left").click();
        await decisionTable.annotationHeaderAt(0).contextMenu.button("Insert").click();

        await expect(decisionTable.annotationHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.annotationHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 4 }).content).toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).not.toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and delete column", async ({ page, bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 3 }).fill("test");

        const annotationHeader = decisionTable.annotationHeaderAt(0);
        await annotationHeader.contextMenu.open();
        await annotationHeader.contextMenu.option("Insert left").click();

        await expect(decisionTable.annotationHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.annotationHeaderAt(1).content).toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 4 }).content).toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).not.toContainText("test");

        await annotationHeader.contextMenu.open();
        await annotationHeader.contextMenu.option("Delete").click();

        await expect(decisionTable.annotationHeaderAt(0).content).toBeAttached();
        await expect(decisionTable.annotationHeaderAt(1).content).not.toBeAttached();
        await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(4);
      });
    });
  });

  test("should reset insert multiples menu when opening another cell context menu", async ({ stories, bee }) => {
    await stories.openDecisionTable();

    const decisionTable = bee.expression.asDecisionTable();

    await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
    await decisionTable.cellAt({ row: 1, column: 1 }).contextMenu.open();
    await decisionTable.cellAt({ row: 1, column: 1 }).contextMenu.option("Insert").first().click();

    await decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.open();

    await expect(decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.heading("DECISION RULE")).toBeAttached();
    await expect(decisionTable.cellAt({ row: 1, column: 0 }).contextMenu.heading("SELECTION")).toBeAttached();
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openDecisionTable();
    });

    test.describe("Add decision rules", () => {
      test("should add row above by positioning mouse on the index cell upper section", async ({ bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
        await decisionTable.addRowAtTop();
        await expect(decisionTable.cellAt({ row: 2, column: 1 }).content).toContainText("test");
        await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).not.toContainText("test");
      });

      test("should add row below by positioning mouse on the index cell lower section", async ({ bee }) => {
        const decisionTable = bee.expression.asDecisionTable();

        await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
        await decisionTable.addRowAtBottomOfIndex(1);
        await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText("test");
        await expect(decisionTable.cellAt({ row: 2, column: 1 }).content).toContainText("-");
      });
    });

    test.describe("Add columns", () => {
      test.describe("Input columns", () => {
        test("should add column left by positioning mouse on the header cell left section", async ({ page, bee }) => {
          const decisionTable = bee.expression.asDecisionTable();

          await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
          await decisionTable.addInputAtStart();

          await expect(decisionTable.inputHeaderAt(0).content).toBeAttached();
          await expect(decisionTable.inputHeaderAt(1).content).toBeAttached();
          await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).not.toContainText("test");
          await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page, bee }) => {
          const decisionTable = bee.expression.asDecisionTable();

          await decisionTable.cellAt({ row: 1, column: 1 }).fill("test");
          await decisionTable.addInputAtIndex(1);

          await expect(decisionTable.inputHeaderAt(0).content).toBeAttached();
          await expect(decisionTable.inputHeaderAt(1).content).toBeAttached();
          await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText("test");
          await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).not.toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });

      test.describe("Output columns", () => {
        test("should add column left by positioning mouse on the header cell left section", async ({ page, bee }) => {
          const decisionTable = bee.expression.asDecisionTable();

          await decisionTable.cellAt({ row: 1, column: 2 }).fill("test");
          await decisionTable.addOutputAtStart();

          await expect(decisionTable.outputHeaderAt(0).content).toBeAttached();
          await expect(decisionTable.outputHeaderAt(1).content).toBeAttached();
          await expect(decisionTable.outputHeaderAt(2).content).toBeAttached();
          await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).not.toContainText("test");
          await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page, bee }) => {
          const decisionTable = bee.expression.asDecisionTable();

          await decisionTable.cellAt({ row: 1, column: 2 }).fill("test");
          await bee.expression.asDecisionTable().addOutputAtRightOfIndex(0);

          await expect(decisionTable.outputHeaderAt(0).content).toBeAttached();
          await expect(decisionTable.outputHeaderAt(1).content).toBeAttached();
          await expect(decisionTable.outputHeaderAt(2).content).toBeAttached();
          await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toContainText("test");
          await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).not.toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });

      test.describe("Rule annotation columns", () => {
        test("should add column left by positioning mouse on the header cell left section", async ({ page, bee }) => {
          const decisionTable = bee.expression.asDecisionTable();

          await decisionTable.cellAt({ row: 1, column: 3 }).fill("test");
          await decisionTable.addAnnotationAtStart();

          await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).toBeAttached();
          await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).not.toContainText("test");
          await expect(decisionTable.cellAt({ row: 1, column: 4 }).content).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page, bee }) => {
          const decisionTable = bee.expression.asDecisionTable();

          await decisionTable.cellAt({ row: 1, column: 3 }).fill("test");
          await decisionTable.addAnnotationAtEnd();

          await expect(decisionTable.annotationHeaderAt(0).content).toBeAttached();
          await expect(decisionTable.annotationHeaderAt(1).content).toBeAttached();
          await expect(decisionTable.cellAt({ row: 1, column: 3 }).content).toContainText("test");
          await expect(decisionTable.cellAt({ row: 1, column: 4 }).content).not.toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });
    });
  });
});
