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

const ACTIVE_CLASS_REGEXP = /(^|\s)active(\s|$)/;

test.describe("Keyboard", () => {
  test.describe("Navigation", () => {
    test("should correctly navigate", async ({ bee, page, useCases }) => {
      await useCases.openLoanOriginations("bureau-strategy-decision-service", "bureau-call-type");
      const decisionTable = bee.expression.asDecisionTable();
      await decisionTable.cellAt({ row: 1, column: 1 }).select();

      await page.keyboard.press("ArrowRight");
      await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("ArrowDown");
      await expect(decisionTable.cellAt({ row: 2, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("ArrowUp");
      await expect(decisionTable.cellAt({ row: 1, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("ArrowLeft");
      await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.type(`"test"`);
      await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Enter");
      await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).toContainText(`"test"`);
      await expect(decisionTable.cellAt({ row: 2, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.type(`"test2"`);
      await expect(decisionTable.cellAt({ row: 2, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Escape");
      await expect(decisionTable.cellAt({ row: 1, column: 1 }).content).not.toContainText(`"test2"`);
      await expect(decisionTable.cellAt({ row: 2, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Tab");
      await expect(decisionTable.cellAt({ row: 2, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Tab");
      await expect(decisionTable.cellAt({ row: 2, column: 3 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Shift+Tab");
      await expect(decisionTable.cellAt({ row: 2, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Escape");
      await expect(decisionTable.cellAt({ row: 2, column: 2 }).content).not.toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Enter");
      await expect(decisionTable.cellAt({ row: 2, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);
    });
  });

  test.describe("Nested stories", () => {
    test("should correctly navigate", async ({ bee, page, useCases }) => {
      await useCases.openLoanOriginations("bureau-strategy-decision-service", "pre-bureau-risk-category");
      const contextExpression = bee.expression.asContext();
      const literalExpression = contextExpression.entry(0).expression.asLiteral();
      const resultDecisionTable = contextExpression.result.expression.asDecisionTable();
      await resultDecisionTable.cellAt({ row: 1, column: 1 }).select();

      // Check nested decision table
      await page.keyboard.press("ArrowRight");
      await expect(resultDecisionTable.cellAt({ row: 1, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("ArrowDown");
      await expect(resultDecisionTable.cellAt({ row: 2, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("ArrowUp");
      await expect(resultDecisionTable.cellAt({ row: 1, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("ArrowLeft");
      await expect(resultDecisionTable.cellAt({ row: 1, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.type(`"test"`);
      await expect(resultDecisionTable.cellAt({ row: 1, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Enter");
      await expect(resultDecisionTable.cellAt({ row: 1, column: 1 }).content).toContainText(`"test"`);
      await expect(resultDecisionTable.cellAt({ row: 2, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.type(`"test2"`);
      await expect(resultDecisionTable.cellAt({ row: 2, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Escape");
      await expect(resultDecisionTable.cellAt({ row: 1, column: 1 }).content).not.toContainText(`"test2"`);
      await expect(resultDecisionTable.cellAt({ row: 2, column: 1 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Tab");
      await expect(resultDecisionTable.cellAt({ row: 2, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Tab");
      await expect(resultDecisionTable.cellAt({ row: 2, column: 3 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Shift+Tab");
      await expect(resultDecisionTable.cellAt({ row: 2, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Escape");
      await expect(resultDecisionTable.cellAt({ row: 2, column: 2 }).content).not.toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Enter");
      await expect(resultDecisionTable.cellAt({ row: 2, column: 2 }).content).toHaveClass(ACTIVE_CLASS_REGEXP);

      // Selecting context expression result cell (decision table container)
      await page.keyboard.press("Escape");
      await expect(contextExpression.resultExpressionContainer).toHaveClass(ACTIVE_CLASS_REGEXP);

      // Selecting context expression entry 0 cell (literal expression container)
      await page.keyboard.press("ArrowUp");
      await expect(contextExpression.entry(0).contextExpressionContainer).toHaveClass(ACTIVE_CLASS_REGEXP);

      await page.keyboard.press("Enter");
      await expect(literalExpression.content).toHaveClass(ACTIVE_CLASS_REGEXP);
    });
  });
});
