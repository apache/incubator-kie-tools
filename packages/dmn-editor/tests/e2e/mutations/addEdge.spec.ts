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

import { expect } from "@playwright/test";
import { test } from "../__fixtures__/base";
import { env } from "../../../env";

test.beforeEach(async ({ diagram }, testInfo) => {
  await diagram.openEmpty();
});

test.describe("Add edge", () => {
  test.describe("Between Input Data", () => {
    test("And Decision", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      await page.getByTestId("New Input Data node").click();
      await page
        .getByTitle("Add Information Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Decision"));

      expect(await page.getByTestId("edge_informationRequirement")).toBeAttached();
    });

    test("And Knowledge Source", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Knowledge Source", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      await page.getByTestId("New Input Data node").click();
      await page
        .getByTitle("Add Authority Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Knowledge Source"));

      expect(await page.getByTestId("edge_authorityRequirement")).toBeAttached();
    });

    test("And Text Annotation", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Text Annotation", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 400 } });

      // Connect these nodes
      await page.getByTestId("New Input Data node").click();
      await page
        .getByTitle("Add Association edge")
        .locator("visible=true")
        .dragTo(page.getByText("New text annotation"), { targetPosition: { x: 100, y: 100 } });

      // expect(await page.getByTestId(ASSOCIATION)).toBeAttached();
    });
  });

  test.describe("Between Decision", () => {
    test("And Decision", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      await page.getByTestId("New Decision node").nth(1).click();
      await page
        .getByTitle("Add Information Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Decision").first());

      expect(await page.getByTestId("edge_informationRequirement")).toBeAttached();
    });

    test("And Knoledge Source", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Knowledge Source", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      await page.getByTestId("New Decision node").click();
      await page
        .getByTitle("Add Authority Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Knowledge Source"));

      expect(await page.getByTestId("edge_authorityRequirement")).toBeAttached();
    });

    test("And Text Annotation", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Text Annotation", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 400 } });

      // Connect these nodes
      await page.getByTestId("New Decision node").click();
      await page
        .getByTitle("Add Association edge")
        .locator("visible=true")
        .dragTo(page.getByText("New text annotation"), { targetPosition: { x: 100, y: 100 } });

      // expect(await page.getByTestId(ASSOCIATION)).toBeAttached();
    });
  });

  test.describe("Between BKM", () => {
    test("And Decision", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      await page.getByTestId("New BKM node").click();
      await page
        .getByTitle("Add Knowledge Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Decision"));

      expect(await page.getByTestId("edge_knowledgeRequirement")).toBeAttached();
    });

    test("And BKM", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      await page.getByTestId("New BKM node").nth(1).click();
      await page
        .getByTitle("Add Knowledge Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New BKM").first());

      expect(await page.getByTestId("edge_knowledgeRequirement")).toBeAttached();
    });

    test("And Text Annotation", async ({ page, diagram }) => {
      // Add two nodes
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });
      await page
        .getByTitle("Text Annotation", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      // Connect these nodes
      await page.getByTestId("New text annotation node").click();
      await page.getByTitle("Add Association edge").locator("visible=true").dragTo(page.getByText("New BKM"));

      expect(await page.getByTestId("edge_association")).toBeAttached();
    });
  });
});
