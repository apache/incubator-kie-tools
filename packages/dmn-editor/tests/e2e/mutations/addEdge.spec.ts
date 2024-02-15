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
  ADD_ASSOCIATION,
  ADD_AUTHORITY_REQUIREMENET,
  ADD_INFORMATION_REQUIREMENT,
  ADD_KNOWLEDGE_REQUIREMENT,
  ASSOCIATION,
  AUTHORITY_REQUIREMENET,
  DIAGRAM_CONTAINER,
  INFORMATION_REQUIREMENT,
  KNOWLEDGE_REQUIREMENT,
} from "../../../src/constants";

test.beforeEach(async ({ page, browserName }, testInfo) => {
  test.skip(browserName === "webkit", "hover() and dragTo() cause troubles on webkit");
  await page.goto(
    `http://localhost:${env.dmnEditor.storybook.port}/iframe.html?args=&id=dev-web-app--empty-model&viewMode=story`
  );
});

test.describe("Add edge", () => {
  test.describe("Between Input Data", () => {
    test("And Decision", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      // await page.getByText("New Input Data").hover();
      await page.getByTitle(ADD_INFORMATION_REQUIREMENT).locator("visible=true").dragTo(page.getByText("New Decision"));

      expect(await page.getByTestId(INFORMATION_REQUIREMENT)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("And Knowledge Source", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Knowledge Source", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      // await page.getByText("New Input Data").hover();
      await page
        .getByTitle(ADD_AUTHORITY_REQUIREMENET)
        .locator("visible=true")
        .dragTo(page.getByText("New Knowledge Source"));

      expect(await page.getByTestId(AUTHORITY_REQUIREMENET)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("And Text Annotation", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Text Annotation", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 400 } });

      // Connect these nodes
      // await page.getByText("New Input Data").hover();
      await page
        .getByTitle(ADD_ASSOCIATION)
        .locator("visible=true")
        .dragTo(page.getByText("New text annotation"), { targetPosition: { x: 100, y: 100 } });

      // expect(await page.getByTestId(ASSOCIATION)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });
  });

  test.describe("Between Decision", () => {
    test("And Decision", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      // await page.getByText("New Decision").first().hover();
      await page
        .getByTitle(ADD_INFORMATION_REQUIREMENT)
        .locator("visible=true")
        .dragTo(page.getByText("New Decision").first());

      expect(await page.getByTestId(INFORMATION_REQUIREMENT)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("And Knoledge Source", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Knowledge Source", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      // await page.getByText("New Decision").hover();
      await page
        .getByTitle(ADD_AUTHORITY_REQUIREMENET)
        .locator("visible=true")
        .dragTo(page.getByText("New Knowledge Source"));

      expect(await page.getByTestId(AUTHORITY_REQUIREMENET)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("And Text Annotation", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Text Annotation", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 400 } });

      // Connect these nodes
      // await page.getByText("New Decision").hover();
      await page
        .getByTitle(ADD_ASSOCIATION)
        .locator("visible=true")
        .dragTo(page.getByText("New text annotation"), { targetPosition: { x: 100, y: 100 } });

      // expect(await page.getByTestId(ASSOCIATION)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });
  });

  test.describe("Between BKM", () => {
    test("And Decision", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      // await page.getByText("New BKM").hover();
      await page.getByTitle(ADD_KNOWLEDGE_REQUIREMENT).locator("visible=true").dragTo(page.getByText("New Decision"));

      expect(await page.getByTestId(KNOWLEDGE_REQUIREMENT)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("And BKM", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      // await page.getByText("New BKM").first().hover();
      await page
        .getByTitle(ADD_KNOWLEDGE_REQUIREMENT)
        .locator("visible=true")
        .dragTo(page.getByText("New BKM").first());

      expect(await page.getByTestId(KNOWLEDGE_REQUIREMENT)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });

    test("And Text Annotation", async ({ page }) => {
      // Add two nodes
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Text Annotation", { exact: true })
        .dragTo(page.getByTestId(DIAGRAM_CONTAINER), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      // await page.getByText("New BKM").hover();
      await page.getByTitle(ADD_ASSOCIATION).locator("visible=true").dragTo(page.getByText("New BKM"));

      expect(await page.getByTestId(ASSOCIATION)).toBeAttached();
      expect(await page.getByTestId(DIAGRAM_CONTAINER).screenshot()).toMatchSnapshot();
    });
  });
});
