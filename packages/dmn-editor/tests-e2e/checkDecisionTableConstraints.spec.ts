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
import { DataType } from "./__fixtures__/dataTypes";
import { TabName } from "./__fixtures__/editor";
import { DefaultNodeName, NodeType } from "./__fixtures__/nodes";

test.describe("Decision Table - Type Constraints", () => {
  test.beforeEach(async ({ editor, page, dataTypes }) => {
    await editor.openEmpty();

    // create string data type with enum constraint;
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
    await dataTypes.changeDataTypeName({ newName: "enumType" });
    await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.String });
    await dataTypes.addEnumConstraint({ values: ["foo", "bar", "baz"] });

    // create number data type with range constraint;
    await dataTypes.createNewDataType();
    await dataTypes.changeDataTypeName({ newName: "rangeType" });
    await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
    await dataTypes.addRangeConstraint({ values: ["10", "200"] });

    await page.getByRole("tab", { name: "Editor" }).click();
  });

  test("should check decision table input header constraint", async ({
    page,
    palette,
    nodes,
    decisionPropertiesPanel,
    propertiesPanel,
  }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await nodes.edit({ name: DefaultNodeName.DECISION });
    await decisionPropertiesPanel.open();

    // TODO: use new API
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Decision table" }).click();
    await page.getByRole("columnheader", { name: "Input-1 (<Undefined>)" }).click();

    expect(propertiesPanel.panel().getByText("Constraint")).not.toBeAttached();
  });
});

/**
 * add decision node
 * add decision table
 * change input column type to string
 * check constraint on input (shouldnt have any)
 */

/**
 * add decision node
 * add decision table
 * change output column type to string
 * check constraint on output (shouldnt have any)
 */

/**
 * open data types tab,
 * add type
 * add constraint
 * change to editor tab
 * add decision node
 * add decision table
 * change input column type to previous created
 * check constraint on input
 */

/**
 * open data types tab,
 * add type
 * add constraint
 * change to editor tab
 * add decision node
 * add decision table
 * change output column type to previous created
 * check constraint on output
 */

/**
 * open data types tab,
 * add type
 * add constraint
 * change to editor tab
 * add decision node
 * add decision table
 * change input column type to previous created
 * check constraint on input
 * open data types tab
 * change constraint
 * open editor
 * check input column again
 */

/**
 * open data types tab,
 * add type
 * add constraint
 * change to editor tab
 * add decision node
 * add decision table
 * change output column type to previous created
 * check constraint on output
 * open data types tab
 * change constraint
 * open editor
 * check output column again
 */

/**
 * create story with this case.
 */
