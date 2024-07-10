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

import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { expect, test } from "../__fixtures__/base";
import { NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Model - DRD", () => {
  test.describe("Create DRD", () => {
    test("Create DRD in empty model", async ({ drds, page }) => {
      await drds.open();
      await drds.create({ name: "second drd" });

      await expect(page.getByTestId("kie-tools--dmn-editor--palette-nodes-popover")).toBeVisible();
      await expect(page.getByTestId("kie-tools--dmn-editor--palette-nodes-popover")).toContainText("No DRG nodes yet");
      expect(await drds.getCurrent()).toEqual("second drd");
    });

    test("Create DRD in non empty model", async ({ drds, page, palette }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await drds.open();
      await drds.create({ name: "second drd" });

      await expect(page.getByTestId("kie-tools--dmn-editor--palette-nodes-popover")).toBeVisible();
      await expect(page.getByTestId("kie-tools--dmn-editor--palette-nodes-popover")).not.toContainText(
        "No DRG nodes yet"
      );
      expect(await drds.getCurrent()).toEqual("second drd");
    });
  });

  test.describe("Rename DRD", () => {
    test.beforeEach("Create DRDs", async ({ drds }) => {
      await drds.open();
      await drds.create({ name: "First DRD" });

      await drds.open();
      await drds.create({ name: "Second DRD" });
    });

    test("Rename DRD and navigate away", async ({ drds }) => {
      await drds.open();
      await drds.navigateTo({ name: "Second DRD" });
      await drds.rename({ newName: "SECOND DRD" });

      expect(await drds.getAll()).toEqual(["1. First DRD", "2. SECOND DRD"]);
    });

    test("Rename DRD using special character", async ({ drds }) => {
      await drds.open();
      await drds.navigateTo({ name: "Second DRD" });
      await drds.rename({ newName: "SECOND%20DRD" });

      expect(await drds.getAll()).toEqual(["1. First DRD", "2. SECOND%20DRD"]);
    });
  });

  test.describe("Delete DRD", async () => {
    test.beforeEach("Create DRDs", async ({ drds }) => {
      await drds.open();
      await drds.create({ name: "First DRD" });

      await drds.open();
      await drds.create({ name: "Second DRD" });

      await drds.open();
      await drds.create({ name: "Third DRD" });
    });

    test("Remove DRD and check the indexes", async ({ drds }) => {
      test.info().annotations.push({
        type: TestAnnotations.AFFECTED_BY,
        description: "https://github.com/apache/incubator-kie-issues/issues/1174",
      });
      await drds.open();
      await drds.remove({ name: "Second DRD" });

      expect(await drds.getAll()).toEqual(["1. First DRD", "2. Third DRD"]);
    });
  });

  test.describe("Navigate DRD", () => {
    test.beforeEach("Create DRDs", async ({ drds }) => {
      await drds.open();
      await drds.create({ name: "First DRD" });

      await drds.open();
      await drds.create({ name: "Second DRD" });
    });

    test("Navigate to first", async ({ drds }) => {
      await drds.open();
      await drds.navigateTo({ name: "First DRD" });

      expect(await drds.getCurrent()).toEqual("First DRD");
    });

    test("Navigate to multiple DRDs", async ({ drds }) => {
      await drds.open();
      await drds.navigateTo({ name: "First DRD" });
      await drds.navigateTo({ name: "Second DRD" });

      expect(await drds.getCurrent()).toEqual("Second DRD");
    });
  });

  test.describe("Use DRD", async () => {
    test("remove DRG element - its removed from all DRDs", async ({ drds }) => {
      // TODO
    });
  });
});
