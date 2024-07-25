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

test.describe("Delete node - Decision Service with output", () => {
  test("should delete the Decision Service node and not the Decision node", async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 130, y: 130 } });

    await expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).toBeAttached();
    await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();

    await nodes.delete({ name: DefaultNodeName.DECISION_SERVICE });

    await expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).not.toBeAttached();
    await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
  });
});
