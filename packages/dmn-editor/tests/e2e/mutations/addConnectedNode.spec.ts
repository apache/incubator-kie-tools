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

test.describe("Add connected node", () => {
  test.describe("From Input Data", () => {
    test("Add Decision", async ({ page, diagram }) => {
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      await page.getByTitle("New Input Data").click();
      await page.getByTitle("Add Decision node").dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New Input Data").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Decision").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.informationRequirement)).toBeAttached();
    });

    test("Add Knowledge Source", async ({ page, diagram }) => {
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      await page.getByTitle("New Input Data").click();
      await page
        .getByTitle("Add Knowledge Source node")
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New Input Data").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Knowledge Source").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.authorityRequirement)).toBeAttached();
    });

    test("Add Text Annotation", async ({ page, diagram }) => {
      await page
        .getByTitle("Input Data", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 400 } });

      await page.getByTitle("New Input Data").click();
      await page
        .getByTitle("Add Text Annotation node")
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New Input Data").getAttribute("data-nodeid");
      const to = await page.getByTitle("New text annotation").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.association)).toBeAttached();
    });
  });

  test.describe("From Decision", () => {
    test("Add Decision", async ({ page, diagram }) => {
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      await page.getByTitle("New Decision").click();
      await page.getByTitle("Add Decision node").dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New Decision").first().getAttribute("data-nodeid");
      const to = await page.getByTitle("New Decision").nth(1).getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.informationRequirement)).toBeAttached();
    });

    test("Add Knoledge Source", async ({ page, diagram }) => {
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      await page.getByTitle("New Decision").click();
      await page
        .getByTitle("Add Knowledge Source node")
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New Decision").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Knowledge Source").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.authorityRequirement)).toBeAttached();
    });

    test("Add Text Annotation", async ({ page, diagram }) => {
      await page
        .getByTitle("Decision", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 400 } });

      await page.getByTitle("New Decision").click();
      await page
        .getByTitle("Add Text Annotation node")
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New Decision").getAttribute("data-nodeid");
      const to = await page.getByTitle("New text annotation").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.association)).toBeAttached();
    });
  });

  test.describe("From BKM", () => {
    test("Add Decision", async ({ page, diagram }) => {
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      await page.getByTitle("New BKM").click();
      await page.getByTitle("Add Decision node").dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New BKM").getAttribute("data-nodeid");
      const to = await page.getByTitle("New Decision").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.knowledgeRequirement)).toBeAttached();
    });

    test("Add BKM", async ({ page, diagram }) => {
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 300 } });

      await page.getByTitle("New BKM").click();
      await page
        .getByTitle("Add Business Knowledge Model node")
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New BKM").first().getAttribute("data-nodeid");
      const to = await page.getByTitle("New BKM").nth(1).getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.knowledgeRequirement)).toBeAttached();
    });

    test("Add Text Annotation", async ({ page, diagram }) => {
      await page
        .getByTitle("Business Knowledge Model", { exact: true })
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 400 } });

      await page.getByTitle("New BKM").click();
      await page
        .getByTitle("Add Text Annotation node")
        .dragTo(diagram.getContainer(), { targetPosition: { x: 100, y: 100 } });

      const from = await page.getByTitle("New BKM").getAttribute("data-nodeid");
      const to = await page.getByTitle("New text annotation").getAttribute("data-nodeid");

      await expect(diagram.getEdge(from, to)).toBeAttached();
      await expect(diagram.getEdge(from, to).getByTestId(EDGE_TYPES.association)).toBeAttached();
    });
  });
});
