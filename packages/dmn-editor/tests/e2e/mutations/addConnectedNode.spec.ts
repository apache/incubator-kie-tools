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

import { test, expect } from "@playwright/test";
import { env } from "../../../env";
import {
  ASSOCIATION,
  AUTHORITY_REQUIREMENET,
  DIAGRAM_CONTAINER,
  INFORMATION_REQUIREMENT,
  KNOWLEDGE_REQUIREMENT,
} from "../../../src/constants";

test.beforeEach(async ({ page }, testInfo) => {
  await page.goto(
    `http://localhost:${env.dmnEditor.storybook.port}/iframe.html?args=&id=dev-web-app--empty-model&viewMode=story`
  );
});

test.describe("Add connected node", () => {
  test.describe("From Input Data", () => {
    test("Add Decision", async ({ page }) => {
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      await page.getByTestId("New Input Data node").click();

      await page
        .getByTitle("Add Decision node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(INFORMATION_REQUIREMENT)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("Add Knowledge Source", async ({ page }) => {
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      await page.getByTestId("New Input Data node").click();
      await page
        .getByTitle("Add Knowledge Source node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(AUTHORITY_REQUIREMENET)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("Add Text Annotation", async ({ page }) => {
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 400 } });

      await page.getByTestId("New Input Data node").click();
      await page
        .getByTitle("Add Text Annotation node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(ASSOCIATION)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });
  });

  test.describe("From Decision", () => {
    test("Add Decision", async ({ page }) => {
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      await page.getByTestId("New Decision node").click();
      await page
        .getByTitle("Add Decision node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(INFORMATION_REQUIREMENT)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("Add Knoledge Source", async ({ page }) => {
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      await page.getByTestId("New Decision node").click();
      await page
        .getByTitle("Add Knowledge Source node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(AUTHORITY_REQUIREMENET)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("Add Text Annotation", async ({ page }) => {
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 400 } });

      await page.getByTestId("New Decision node").click();
      await page
        .getByTitle("Add Text Annotation node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(ASSOCIATION)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });
  });

  test.describe("From BKM", () => {
    test("Add Decision", async ({ page }) => {
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      await page.getByTestId("New BKM node").click();
      await page
        .getByTitle("Add Decision node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(KNOWLEDGE_REQUIREMENT)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("Add BKM", async ({ page }) => {
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      await page.getByTestId("New BKM node").click();
      await page
        .getByTitle("Add Business Knowledge Model node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(KNOWLEDGE_REQUIREMENT)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("Add Text Annotation", async ({ page }) => {
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 400 } });

      await page.getByTestId("New BKM node").click();
      await page
        .getByTitle("Add Text Annotation node")
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });

      expect(await page.getByTestId(ASSOCIATION)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });
  });
});
