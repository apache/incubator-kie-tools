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

import { test, expect } from "./__fixtures__/base";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Change Properties - Diagram", () => {
  test.beforeEach(async ({ diagramPropertiesPanel }) => {
    await diagramPropertiesPanel.open();
  });

  test("should change the Diagram name", async ({ diagramPropertiesPanel }) => {
    await diagramPropertiesPanel.setName({ newName: "New Diagram Name" });

    expect(await diagramPropertiesPanel.getName()).toBe("New Diagram Name");
  });

  test("should change the Diagram description", async ({ diagramPropertiesPanel }) => {
    await diagramPropertiesPanel.setDescription({ newDescription: "New Diagram Description" });

    expect(await diagramPropertiesPanel.getDescription()).toBe("New Diagram Description");
  });

  test("should change the Diagram expression language", async ({ diagramPropertiesPanel }) => {
    await diagramPropertiesPanel.setExpressionLanguage({ expressionlangugae: "FEEL" });

    expect(await diagramPropertiesPanel.getExpressionLanguage()).toBe("FEEL");
  });

  test("should change the Diagram ID", async ({ diagramPropertiesPanel }) => {
    await diagramPropertiesPanel.setId({ id: "_ABCD" });

    expect(await diagramPropertiesPanel.getId()).toBe("_ABCD");
  });

  test("should change the Diagram namespace", async ({ diagramPropertiesPanel }) => {
    await diagramPropertiesPanel.setNamespace({ namespace: "NAMESPACE" });

    expect(await diagramPropertiesPanel.getNamespace()).toBe("NAMESPACE");
  });

  test("should regenerate Diagram ID & Namespace", async ({ diagramPropertiesPanel }) => {
    const originalId = await diagramPropertiesPanel.getId();
    const originalNamespace = await diagramPropertiesPanel.getNamespace();

    await diagramPropertiesPanel.resetIdAndNamespace({ cancel: false });

    const newId = await diagramPropertiesPanel.getId();
    const newNamespace = await diagramPropertiesPanel.getNamespace();

    expect(originalId).not.toEqual(newId);
    expect(originalNamespace).not.toEqual(newNamespace);
  });

  test("should cancel regenerate Diagram ID & Namespace operation", async ({ diagramPropertiesPanel }) => {
    const originalId = await diagramPropertiesPanel.getId();
    const originalNamespace = await diagramPropertiesPanel.getNamespace();

    await diagramPropertiesPanel.resetIdAndNamespace({ cancel: true });

    const newId = await diagramPropertiesPanel.getId();
    const newNamespace = await diagramPropertiesPanel.getNamespace();

    expect(originalId).toEqual(newId);
    expect(originalNamespace).toEqual(newNamespace);
  });
});
