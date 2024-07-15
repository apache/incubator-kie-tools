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
      const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
      await cellAt1_1.fill("test");

      const rowIndexCell = bee.expression.asDecisionTable().cellAt({ row: 1, column: 0 });
      await rowIndexCell.contextMenu.open();
      await rowIndexCell.contextMenu.option("Insert").click();
      await rowIndexCell.contextMenu.button("plus").click();
      await rowIndexCell.contextMenu.button("Insert").click();
      await expect(bee.expression.asDecisionTable().cellAt({ row: 4, column: 1 }).content).toContainText("test");
    });

    test("should open decision rules context menu and insert multiples rows below", async ({ bee }) => {
      const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
      await cellAt1_1.fill("test");

      const rowIndexCell = bee.expression.asDecisionTable().cellAt({ row: 1, column: 0 });
      await rowIndexCell.contextMenu.open();
      await rowIndexCell.contextMenu.option("Insert").click();
      await rowIndexCell.contextMenu.button("minus").click();
      await rowIndexCell.contextMenu.radio("Below").click();
      await rowIndexCell.contextMenu.button("Insert").click();

      await expect(cellAt1_1.content).toContainText("test");
      await expect(bee.expression.asDecisionTable().cellAt({ row: 2, column: 0 }).content).toBeAttached();
    });

    test("should open decision rules context menu and delete row", async ({ bee }) => {
      const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
      await cellAt1_1.fill("test");
      await cellAt1_1.contextMenu.open();
      await cellAt1_1.contextMenu.option("Insert above").click();
      await expect(bee.expression.asDecisionTable().cellAt({ row: 2, column: 1 }).content).toContainText("test");

      await cellAt1_1.contextMenu.open();
      await cellAt1_1.contextMenu.option("Delete").nth(1).click();
      await expect(cellAt1_1.content).toContainText("test");
    });

    test("should open decision rules context menu and duplicate row", async ({ bee }) => {
      const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
      const cellAt2_1 = bee.expression.asDecisionTable().cellAt({ row: 2, column: 1 });
      await cellAt1_1.fill("test");
      await cellAt1_1.contextMenu.open();
      await cellAt1_1.contextMenu.option("Duplicate").click();
      await expect(cellAt1_1.content).toContainText("test");
      await expect(cellAt2_1.content).toContainText("test");
    });
  });

  test.describe("Columns controls", () => {
    test.describe("Input columns", () => {
      test.beforeEach(async ({ stories }) => {
        await stories.openDecisionTable();
      });

      test("shouldn't render decision rules context menu", async ({ bee }) => {
        const inputHeader = bee.expression.asDecisionTable().inputHeaderAt(0);
        await inputHeader.contextMenu.open();

        await expect(inputHeader.contextMenu.heading("DECISION RULES")).not.toBeAttached();
        await expect(inputHeader.contextMenu.heading("SELECTION")).toBeAttached();
        await expect(inputHeader.contextMenu.heading("INPUT CLAUSE")).toBeAttached();
        await expect(inputHeader.contextMenu.heading("OUTPUT CLAUSE")).not.toBeAttached();
        await expect(inputHeader.contextMenu.heading("RULE ANNOTATION")).not.toBeAttached();
      });

      test("should open input column context menu and insert column right", async ({ page, bee }) => {
        const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
        await cellAt1_1.fill("test");
        await bee.expression.asDecisionTable().inputHeaderAt(0).contextMenu.open();
        await bee.expression.asDecisionTable().inputHeaderAt(0).contextMenu.option("Insert right").click();

        await expect(bee.expression.asDecisionTable().inputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(1).content).toBeAttached();
        await expect(cellAt1_1.content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and insert column left", async ({ page, bee }) => {
        const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
        const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
        await cellAt1_1.fill("test");
        await bee.expression.asDecisionTable().inputHeaderAt(0).contextMenu.open();
        await bee.expression.asDecisionTable().inputHeaderAt(0).contextMenu.option("Insert left").click();

        await expect(bee.expression.asDecisionTable().inputHeaderAt(1).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(0).content).toBeAttached();
        await expect(cellAt1_1.content).not.toContainText("test");
        await expect(cellAt1_2.content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and insert multiples columns on right", async ({ page, bee }) => {
        const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
        await cellAt1_1.fill("test");

        const inputHeader = bee.expression.asDecisionTable().inputHeaderAt(0);
        await inputHeader.contextMenu.open();

        await inputHeader.contextMenu.option("Insert").click();
        await inputHeader.contextMenu.button("plus").click();
        await inputHeader.contextMenu.radio("To the right").click();
        await inputHeader.contextMenu.button("Insert").click();

        await expect(bee.expression.asDecisionTable().inputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(1).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(2).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(3).content).toBeAttached();

        await expect(cellAt1_1.content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open input column context menu and insert multiples columns on left", async ({ page, bee }) => {
        const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
        const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
        await cellAt1_1.fill("test");

        const inputHeader = bee.expression.asDecisionTable().inputHeaderAt(0);
        await inputHeader.contextMenu.open();

        await inputHeader.contextMenu.option("Insert").click();
        await inputHeader.contextMenu.button("minus").click();
        await inputHeader.contextMenu.radio("To the left").click();
        await inputHeader.contextMenu.button("Insert").click();

        await expect(bee.expression.asDecisionTable().inputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(1).content).toBeAttached();

        await expect(cellAt1_2.content).toContainText("test");
        await expect(cellAt1_1.content).not.toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open input column context menu and delete column", async ({ page, bee }) => {
        const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
        const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
        await cellAt1_1.fill("test");

        const inputHeader = bee.expression.asDecisionTable().inputHeaderAt(0);
        await inputHeader.contextMenu.open();
        await inputHeader.contextMenu.option("Insert left").click();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(1).content).toBeAttached();

        await expect(cellAt1_2.content).toContainText("test");
        await expect(cellAt1_1.content).not.toContainText("test");

        await inputHeader.contextMenu.open();
        await inputHeader.contextMenu.option("Delete").click();
        await expect(bee.expression.asDecisionTable().inputHeaderAt(0).content).toBeAttached();
        await expect(cellAt1_1.content).toContainText("test");

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
        const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
        await cellAt1_2.fill("test");
        await bee.expression.asDecisionTable().outputHeaderAt(0).contextMenu.open();
        await bee.expression.asDecisionTable().outputHeaderAt(0).contextMenu.option("Insert right").click();

        await expect(bee.expression.asDecisionTable().nameAndDataTypeCell.content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(1).content).toBeAttached();
        await expect(cellAt1_2.content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and insert column left", async ({ page, bee }) => {
        const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
        const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
        await cellAt1_2.fill("test");
        await bee.expression.asDecisionTable().outputHeaderAt(0).contextMenu.open();
        await bee.expression.asDecisionTable().outputHeaderAt(0).contextMenu.option("Insert left").click();

        await expect(bee.expression.asDecisionTable().nameAndDataTypeCell.content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(1).content).toBeAttached();
        await expect(cellAt1_2.content).not.toContainText("test");
        await expect(cellAt1_3.content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and insert multiples columns on right", async ({ page, bee }) => {
        const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
        await cellAt1_2.fill("test");

        const outputHeader = bee.expression.asDecisionTable().outputHeaderAt(0);
        await outputHeader.contextMenu.open();

        await outputHeader.contextMenu.option("Insert").click();
        await outputHeader.contextMenu.button("plus").click();
        await outputHeader.contextMenu.button("Insert").click();

        await expect(bee.expression.asDecisionTable().nameAndDataTypeCell.content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(1).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(2).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(3).content).toBeAttached();

        await expect(cellAt1_2.content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open output column context menu and insert multiples columns on left", async ({ page, bee }) => {
        const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
        const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
        await cellAt1_2.fill("test");

        const outputHeader = bee.expression.asDecisionTable().outputHeaderAt(0);
        await outputHeader.contextMenu.open();

        await outputHeader.contextMenu.option("Insert").click();
        await outputHeader.contextMenu.button("minus").click();
        await outputHeader.contextMenu.radio("To the left").click();
        await outputHeader.contextMenu.button("Insert").click();

        await expect(bee.expression.asDecisionTable().nameAndDataTypeCell.content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(1).content).toBeAttached();

        await expect(cellAt1_2.content).not.toContainText("test");
        await expect(cellAt1_3.content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open output column context menu and delete column", async ({ page, bee }) => {
        const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
        const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
        await cellAt1_2.fill("test");

        const outputHeader = bee.expression.asDecisionTable().outputHeaderAt(0);
        await outputHeader.contextMenu.open();
        await outputHeader.contextMenu.option("Insert left").click();

        await expect(bee.expression.asDecisionTable().nameAndDataTypeCell.content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(1).content).toBeAttached();

        await expect(cellAt1_3.content).toContainText("test");
        await expect(cellAt1_2.content).not.toContainText("test");

        await bee.expression.asDecisionTable().outputHeaderAt(1).contextMenu.open();
        await bee.expression.asDecisionTable().outputHeaderAt(1).contextMenu.option("Delete").click();

        await expect(bee.expression.asDecisionTable().nameAndDataTypeCell.content).toBeAttached();
        await expect(bee.expression.asDecisionTable().outputHeaderAt(1).content).not.toBeAttached();

        await expect(cellAt1_2.content).toContainText("test");

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
        const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
        await cellAt1_3.fill("test");

        const annotationHeader = bee.expression.asDecisionTable().annotationHeaderAt(0);
        await annotationHeader.contextMenu.open();
        await annotationHeader.contextMenu.option("Insert right").click();

        await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).toBeAttached();
        await expect(cellAt1_3.content).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and insert column left", async ({ page, bee }) => {
        const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
        const cellAt1_4 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 4 });
        await cellAt1_3.fill("test");

        const annotationHeader = bee.expression.asDecisionTable().annotationHeaderAt(0);
        await annotationHeader.contextMenu.open();
        await annotationHeader.contextMenu.option("Insert left").click();

        await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).toBeAttached();
        await expect(cellAt1_4.content).toContainText("test");
        await expect(cellAt1_3.content).not.toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and insert multiples columns on right", async ({
        page,
        bee,
      }) => {
        const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
        await cellAt1_3.fill("test");

        const annotationHeader = bee.expression.asDecisionTable().annotationHeaderAt(0);
        await annotationHeader.contextMenu.open();

        await annotationHeader.contextMenu.option("Insert").click();
        await annotationHeader.contextMenu.button("plus").click();
        await annotationHeader.contextMenu.radio("To the right").click();
        await annotationHeader.contextMenu.button("Insert").click();

        await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(2).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(3).content).toBeAttached();

        await expect(cellAt1_3.content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(7);
      });

      test("should open annotation column context menu and insert multiples columns on left", async ({ page, bee }) => {
        const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
        const cellAt1_4 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 4 });
        await cellAt1_3.fill("test");

        const annotationHeader = bee.expression.asDecisionTable().annotationHeaderAt(0);
        await annotationHeader.contextMenu.open();

        await annotationHeader.contextMenu.option("Insert").click();
        await annotationHeader.contextMenu.button("minus").click();
        await annotationHeader.contextMenu.radio("To the left").click();
        await annotationHeader.contextMenu.button("Insert").click();

        await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).toBeAttached();

        await expect(cellAt1_4.content).toContainText("test");
        await expect(cellAt1_3.content).not.toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(5);
      });

      test("should open annotation column context menu and delete column", async ({ page, bee }) => {
        const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
        const cellAt1_4 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 4 });
        await cellAt1_3.fill("test");

        const annotationHeader = bee.expression.asDecisionTable().annotationHeaderAt(0);
        await annotationHeader.contextMenu.open();
        await annotationHeader.contextMenu.option("Insert left").click();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).toBeAttached();

        await expect(cellAt1_4.content).toContainText("test");
        await expect(cellAt1_3.content).not.toContainText("test");

        await annotationHeader.contextMenu.open();
        await annotationHeader.contextMenu.option("Delete").click();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
        await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).not.toBeAttached();
        await expect(cellAt1_3.content).toContainText("test");

        await expect(page.getByRole("cell")).toHaveCount(4);
      });
    });
  });

  test("should reset insert multiples menu when opening another cell context menu", async ({ stories, bee }) => {
    await stories.openDecisionTable();

    const rowHeaderCell = bee.expression.asDecisionTable().cellAt({ row: 1, column: 0 });
    const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
    await cellAt1_1.fill("test");
    await cellAt1_1.contextMenu.open();
    await cellAt1_1.contextMenu.option("Insert").first().click();

    await rowHeaderCell.contextMenu.open();
    await expect(rowHeaderCell.contextMenu.heading("DECISION RULE")).toBeAttached();
    await expect(rowHeaderCell.contextMenu.heading("SELECTION")).toBeAttached();
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openDecisionTable();
    });

    test.describe("Add decision rules", () => {
      test("should add row above by positioning mouse on the index cell upper section", async ({ bee }) => {
        const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
        const cellAt2_1 = bee.expression.asDecisionTable().cellAt({ row: 2, column: 1 });
        await cellAt1_1.fill("test");
        await bee.expression.asDecisionTable().addRowAtTop();
        await expect(cellAt2_1.content).toContainText("test");
        await expect(cellAt1_1.content).not.toContainText("test");
      });

      test("should add row below by positioning mouse on the index cell lower section", async ({ bee }) => {
        const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
        const cellAt2_1 = bee.expression.asDecisionTable().cellAt({ row: 2, column: 1 });
        await cellAt1_1.fill("test");
        await bee.expression.asDecisionTable().addRowAtBottomOfIndex(1);
        await expect(cellAt1_1.content).toContainText("test");
        await expect(cellAt2_1.content).toContainText("-");
      });
    });

    test.describe("Add columns", () => {
      test.describe("Input columns", () => {
        test("should add column left by positioning mouse on the header cell left section", async ({ page, bee }) => {
          const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
          const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
          await cellAt1_1.fill("test");

          await bee.expression.asDecisionTable().addInputAtStart();

          await expect(bee.expression.asDecisionTable().inputHeaderAt(0).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().inputHeaderAt(1).content).toBeAttached();
          await expect(cellAt1_1.content).not.toContainText("test");
          await expect(cellAt1_2.content).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page, bee }) => {
          const cellAt1_1 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 });
          const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
          await cellAt1_1.fill("test");

          await bee.expression.asDecisionTable().addInputAtIndex(1);

          await expect(bee.expression.asDecisionTable().inputHeaderAt(0).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().inputHeaderAt(1).content).toBeAttached();
          await expect(cellAt1_1.content).toContainText("test");
          await expect(cellAt1_2.content).not.toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });

      test.describe("Output columns", () => {
        test("should add column left by positioning mouse on the header cell left section", async ({ page, bee }) => {
          const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
          const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
          await cellAt1_2.fill("test");

          await bee.expression.asDecisionTable().addOutputAtStart();

          await expect(bee.expression.asDecisionTable().outputHeaderAt(0).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().outputHeaderAt(1).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().outputHeaderAt(2).content).toBeAttached();
          await expect(cellAt1_2.content).not.toContainText("test");
          await expect(cellAt1_3.content).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page, bee }) => {
          const cellAt1_2 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 });
          const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
          await cellAt1_2.fill("test");

          await bee.expression.asDecisionTable().addOutputAtRightOfIndex(0);

          await expect(bee.expression.asDecisionTable().outputHeaderAt(0).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().outputHeaderAt(1).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().outputHeaderAt(2).content).toBeAttached();
          await expect(cellAt1_2.content).toContainText("test");
          await expect(cellAt1_3.content).not.toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });

      test.describe("Rule annotation columns", () => {
        test("should add column left by positioning mouse on the header cell left section", async ({ page, bee }) => {
          const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
          const cellAt1_4 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 4 });
          await cellAt1_3.fill("test");

          await bee.expression.asDecisionTable().addAnnotationAtStart();

          await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).toBeAttached();
          await expect(cellAt1_3.content).not.toContainText("test");
          await expect(cellAt1_4.content).toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });

        test("should add column right by positioning mouse on the header cell right section", async ({ page, bee }) => {
          const cellAt1_3 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 });
          const cellAt1_4 = bee.expression.asDecisionTable().cellAt({ row: 1, column: 4 });
          await cellAt1_3.fill("test");

          await bee.expression.asDecisionTable().addAnnotationAtEnd();

          await expect(bee.expression.asDecisionTable().annotationHeaderAt(0).content).toBeAttached();
          await expect(bee.expression.asDecisionTable().annotationHeaderAt(1).content).toBeAttached();
          await expect(cellAt1_3.content).toContainText("test");
          await expect(cellAt1_4.content).not.toContainText("test");
          await expect(page.getByRole("cell")).toHaveCount(5);
        });
      });
    });
  });
});
