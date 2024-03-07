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
import { DefaultNodeName, NodeType } from "./__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Delete node - Group and DRG Elements", () => {
  test.describe("Group with DRG elements", () => {
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GROUP, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 130, y: 130 } });

      await expect(nodes.get({ name: DefaultNodeName.GROUP })).toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    });

    test("should delete the Group node", async ({ nodes }) => {
      await nodes.delete({ name: DefaultNodeName.GROUP });

      await expect(nodes.get({ name: DefaultNodeName.GROUP })).not.toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    });

    test("should delete the DRG node", async ({ nodes }) => {
      await nodes.delete({ name: DefaultNodeName.DECISION });

      await expect(nodes.get({ name: DefaultNodeName.GROUP })).toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
    });

    test("should delete the Group and DRG node", async ({ diagram, nodes }) => {
      await diagram.select({ startPosition: { x: 50, y: 50 }, endPosition: { x: 500, y: 500 } });
      await diagram.get().press("Delete");

      await expect(nodes.get({ name: DefaultNodeName.GROUP })).not.toBeAttached();
      await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
    });
  });
});
