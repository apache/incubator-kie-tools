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
  test.beforeEach(async ({ editor, palette, nodes }) => {
    await editor.openEmpty();

    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await nodes.edit({ name: DefaultNodeName.DECISION });
  });

  test.describe("Decision Table - Type Constraints - built-in data type", () => {
    const dataTypes: DataType[] = [
      DataType.Any,
      DataType.Boolean,
      DataType.Context,
      DataType.Date,
      DataType.DateTime,
      DataType.DateTimeDuration,
      DataType.Number,
      DataType.String,
      DataType.Time,
      DataType.Undefined,
      DataType.YearsMonthsDuration,
    ];

    for (const dataType of dataTypes) {
      test(`Decision Table input header properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "Input-1 (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableInputHeader.setDataType({ newDataType: dataType });

        expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(`${dataType}`);
        expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).not.toBeAttached();
      });

      test(`Decision Table input rule properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "Input-1 (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableInputHeader.setDataType({ newDataType: dataType });
        await page.getByTestId("monaco-container").nth(0).click();

        expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(`${dataType}`);
        expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).not.toBeAttached();
      });

      test(`Decision Table output header properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "New Decision (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableOutputHeader.setDecisionDataType({ newDataType: dataType });

        expect(beePropertiesPanel.decisionTableOutputHeader.getDecisionDataType()).toHaveValue(`${dataType}`);
        expect(beePropertiesPanel.decisionTableOutputHeader.getColumnDataType()).toHaveValue(`${dataType}`);
        expect(beePropertiesPanel.decisionTableOutputHeader.getColumnDataType()).toBeDisabled();
        expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();
      });

      test(`Decision Table nested output header properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "New Decision (<Undefined>)" }).hover();
        await page.getByRole("row", { name: "U Input-1 (<Undefined>) New" }).locator("path").click();

        await page.getByRole("columnheader", { name: "New Decision (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: dataType });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();

        await page.getByRole("columnheader", { name: "Output-1 (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: dataType });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();

        await page.getByRole("columnheader", { name: "Output-2 (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: dataType });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();
      });

      test(`Decision Table output rule properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "New Decision (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableOutputHeader.setDecisionDataType({ newDataType: dataType });
        await page.getByTestId("monaco-container").nth(1).click();

        expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(`${dataType}`);
        expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).not.toBeAttached();
      });
    }
  });

  test.describe("Decision Table - Type Constraints - With custom data types", () => {
    test.beforeEach("create custom data types", async ({ editor, page, dataTypes }) => {
      // create string data type with enum constraint;
      await editor.changeTab({ tab: TabName.DATA_TYPES });
      await dataTypes.createFirstCustonDataType();
      await dataTypes.changeDataTypeName({ newName: "enumType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.String });
      await dataTypes.addEnumerationConstraint({ values: ["foo", "bar", "baz"] });

      // create number data type with range constraint;
      await dataTypes.createNewDataType();
      await dataTypes.changeDataTypeName({ newName: "rangeType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
      await dataTypes.addRangeConstraint({ values: ["10", "200"] });

      // create number data type with expression constraint;
      await dataTypes.createNewDataType();
      await dataTypes.changeDataTypeName({ newName: "expressionType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
      await dataTypes.addExpressionConstraint({ value: "> 20" });

      // create date data type without constraint;
      await dataTypes.createNewDataType();
      await dataTypes.changeDataTypeName({ newName: "noneType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Date });

      await page.getByRole("tab", { name: "Editor" }).click();
    });

    test.describe("Decision Table input header", () => {
      test(`Decision Table input header properties panel should contain constraint - enum`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "Input-1 (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "enumType" });

        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await expect(beePropertiesPanel.decisionTableInputHeader.getNoneConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getExpressionConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getRangeConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationElementAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationElementAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationElementAt(2)).toHaveValue("baz");
      });

      test(`Decision Table input header properties panel should contain constraint - range`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "Input-1 (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "rangeType" });

        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await expect(beePropertiesPanel.decisionTableInputHeader.getNoneConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getExpressionConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getRangeConstraintButton()).toBeDisabled();
        // TODO: add range check;
      });

      test(`Decision Table input header properties panel should contain constraint - expression`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "Input-1 (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "expressionType" });

        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await expect(beePropertiesPanel.decisionTableInputHeader.getNoneConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getExpressionConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getRangeConstraintButton()).toBeDisabled();
        // TODO: add expression check;
      });

      test(`Decision Table input header properties panel shouldn't contain constraint`, async ({
        page,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        // TODO: use new API
        await page.getByText("Select expression").click();
        await page.getByRole("menuitem", { name: "Decision table" }).click();
        await page.getByRole("columnheader", { name: "Input-1 (<Undefined>)" }).click();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "noneType" });

        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await expect(beePropertiesPanel.decisionTableInputHeader.getNoneConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getExpressionConstraintButton()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getRangeConstraintButton()).toBeDisabled();
      });

      // Add change constraint;
    });
  });
});

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
