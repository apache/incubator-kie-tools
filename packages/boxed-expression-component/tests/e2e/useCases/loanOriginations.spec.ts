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

import { test, expect } from "../__fixtures__/base";

test.describe("Check loan originations use case", () => {
  test("should render application risk score expression correctly", async ({ useCases, boxedExpressionEditor }) => {
    await useCases.openLoanOriginations("application-risk-score");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("application-risk-score-expression.png");
  });

  test("should render required monthly installment expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("required-monthly-installment");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("required-monthly-installment-expression.png");
  });

  test("should render function affordability calculation expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("functions", "affordability-calculation");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "function-affordability-calculation-expression.png"
    );
  });

  test("should render function installment calculation expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("functions", "installment-calculation");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "function-installment-calculation-expression.png"
    );
  });

  test("should render bureau strategy decision service bureau call type expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("bureau-strategy-decision-service", "bureau-call-type");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "bureau-strategy-decision-service-bureau-call-type-expression.png"
    );
  });

  test("should render bureau strategy decision service eligibility expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("bureau-strategy-decision-service", "eligibility");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "bureau-strategy-decision-service-eligibility-expression.png"
    );
  });

  test("should render bureau strategy decision service pre bureau affordability expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("bureau-strategy-decision-service", "pre-bureau-affordability");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "bureau-strategy-decision-service-pre-bureau-affordability-expression.png"
    );
  });

  test("should render bureau strategy decision service pre bureau risk category expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("bureau-strategy-decision-service", "pre-bureau-risk-category");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "bureau-strategy-decision-service-pre-bureau-risk-category-expression.png"
    );
  });

  test("should render bureau strategy decision service strategy expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("bureau-strategy-decision-service", "strategy");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "bureau-strategy-decision-service-strategy-expression.png"
    );
  });

  test("should render routing decision service strategy expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("routing-decision-service", "routing");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "routing-decision-service-routing-expression.png"
    );
  });

  test("should render routing decision service post bureau affordability expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("routing-decision-service", "post-bureau-affordability");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "routing-decision-service-post-bureau-affordability-expression.png"
    );
  });

  test("should render routing decision service post bureau risk category expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openLoanOriginations("routing-decision-service", "post-bureau-risk-category");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "routing-decision-service-post-bureau-risk-category-expression.png"
    );
  });
});
