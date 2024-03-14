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
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Rename - BKM", () => {
  test("should rename BKM node on the diagram", async ({ diagram, palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });
    await nodes.rename({ current: DefaultNodeName.BKM, new: "Renamed BKM" });

    await expect(nodes.get({ name: "Renamed BKM" })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot("rename-bkm-node-from-diagram.png");
  });
});
