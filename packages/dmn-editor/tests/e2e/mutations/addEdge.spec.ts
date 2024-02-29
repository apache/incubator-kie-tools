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
import { EDGE_TYPES } from "../../../src/diagram/edges/EdgeTypes";

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
      await page.getByTitle("New Input Data").click();
      await page
        .getByTitle("Add Information Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Decision"));

      const from = await page.getByTitle("New Input Data").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Decision").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.informationRequirement)).toBeAttached();
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
      await page.getByTitle("New Input Data").click();
      await page
        .getByTitle("Add Authority Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Knowledge Source"));

      const from = await page.getByTitle("New Input Data").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Knowledge Source").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.authorityRequirement)).toBeAttached();
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
      await page.getByTitle("New text annotation").click();
      await page.getByTitle("Add Association edge").locator("visible=true").dragTo(page.getByText("New Input Data"));

      const from = await page.getByTitle("New text annotation").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Input Data").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.association)).toBeAttached();
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
      await page.getByTitle("New Decision").nth(1).click();
      await page
        .getByTitle("Add Information Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Decision").first());

      const from = await page.getByTitle("New Decision").nth(1).getAttribute("data-nodeid");
      const to = await page.getByTitle("New Decision").first().getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.informationRequirement)).toBeAttached();
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
      await page.getByTitle("New Decision").click();
      await page
        .getByTitle("Add Authority Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Knowledge Source"));

      const from = await page.getByTitle("New Decision").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Knowledge Source").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.authorityRequirement)).toBeAttached();
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
      await page.getByTitle("New text annotation").click();
      await page.getByTitle("Add Association edge").locator("visible=true").dragTo(page.getByText("New Decision"));

      const from = await page.getByTitle("New text annotation").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Decision").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.association)).toBeAttached();
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
      await page.getByTitle("New BKM").click();
      await page
        .getByTitle("Add Knowledge Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New Decision"));

      const from = await page.getByTitle("New BKM").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Decision").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.knowledgeRequirement)).toBeAttached();
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
      await page.getByTitle("New BKM").nth(1).click();
      await page
        .getByTitle("Add Knowledge Requirement edge")
        .locator("visible=true")
        .dragTo(page.getByText("New BKM").first());

      const from = await page.getByTitle("New BKM").nth(1).getAttribute("data-nodeid");
      const to = await page.getByTitle("New BKM").first().getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.knowledgeRequirement)).toBeAttached();
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
      await page.getByTitle("New text annotation").click();
      await page.getByTitle("Add Association edge").locator("visible=true").dragTo(page.getByText("New BKM"));

      const from = await page.getByTitle("New text annotation").getAttribute("data-nodeid");
      const to = await page.getByTitle("New BKM").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.association)).toBeAttached();
    });
  });
});
