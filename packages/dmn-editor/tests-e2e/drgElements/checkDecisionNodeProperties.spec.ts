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

test.beforeEach(async ({ editor, palette, nodes }) => {
  await editor.open();

  await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
  await nodes.edit({ name: DefaultNodeName.DECISION });
});

test("Decision properties should be able to type the description", async ({ bee, decisionPropertiesPanel }) => {
  await bee.selectExpressionMenu.selectContext();

  await decisionPropertiesPanel.open();
  await bee.expression.asContext().expressionHeaderCell.select();

  await decisionPropertiesPanel.setDecisionDescription({ newDescription: "New Description" });
  expect(await decisionPropertiesPanel.getDecisionDescription()).toBe("New Description");

  await decisionPropertiesPanel.setDecisionAllowedAnswers({ newAllowedAnswers: "New Allowed Answers" });
  expect(await decisionPropertiesPanel.getDecisionAllowedAnswers()).toBe("New Allowed Answers");

  await decisionPropertiesPanel.setDecisionQuestion({ newQuestion: "New Question" });
  expect(await decisionPropertiesPanel.getDecisionQuestion()).toBe("New Question");
});
