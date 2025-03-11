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
import { test, expect } from "./__fixtures__/base";
import { DataType, RangeConstraintPosition } from "./__fixtures__/dataTypes";
import { TabName } from "./__fixtures__/editor";
import { DefaultNodeName, NodeType } from "./__fixtures__/nodes";

test.describe("Decision Table - Cells Data Type", () => {
  test.describe("Decision Table - Cells Data Type - Merged expression header and output column", () => {
    test.beforeEach(async ({ editor, palette, nodes }) => {
      await editor.open();

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await nodes.edit({ name: DefaultNodeName.DECISION });
    });

    test("Decision type should match expression header type and output column type should be hidden with a single output column - built-in type", async ({
      bee,
      beePropertiesPanel,
    }) => {
      await beePropertiesPanel.open();
      await bee.selectExpressionMenu.selectDecisionTable();
      await bee.expression.asDecisionTable().outputHeaderAt(0).select();
      await beePropertiesPanel.decisionTableOutputHeader.setExpressionDataType({
        newDataType: DataType.DateTimeDuration,
      });

      await expect(beePropertiesPanel.decisionTableOutputHeader.getColumnDataType()).not.toBeAttached();
      await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(DataType.DateTimeDuration);
    });

    test("Decision type should match expression header type and output column type should be hidden with a single output column - custom type", async ({
      editor,
      dataTypes,
      bee,
      beePropertiesPanel,
    }) => {
      await editor.changeTab({ tab: TabName.DATA_TYPES });
      await dataTypes.createFirstCustonDataType();
      await dataTypes.changeDataTypeName({ newName: "testType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.String });
      await editor.changeTab({ tab: TabName.EDITOR });

      await beePropertiesPanel.open();
      await bee.selectExpressionMenu.selectDecisionTable();
      await bee.expression.asDecisionTable().outputHeaderAt(0).select();
      await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "testType" });

      await expect(beePropertiesPanel.decisionTableOutputHeader.getColumnDataType()).not.toBeAttached();
      await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(/^testType\s$/i);
    });

    test("Decision table output column type shouldn't be there after deleting one output column", async ({
      bee,
      beePropertiesPanel,
    }) => {
      await beePropertiesPanel.open();

      // Setup a decision table with expression header and output column with two different types.
      // First create a decision table with two output columns, change the type of one and delete the other.
      await bee.selectExpressionMenu.selectDecisionTable();
      await bee.expression.asDecisionTable().addOutputAtStart();
      await bee.expression.asDecisionTable().outputHeaderAt(0).select();
      await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: DataType.Number });
      await bee.expression.asDecisionTable().outputHeaderAt(2).select();
      await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: DataType.Boolean });
      await bee.expression.asDecisionTable().outputHeaderAt(1).contextMenu.open();
      await bee.expression.asDecisionTable().outputHeaderAt(1).contextMenu.option("Delete").click();
      await bee.expression.asDecisionTable().outputHeaderAt(0).select();

      await expect(beePropertiesPanel.decisionTableOutputHeader.getColumnDataType()).not.toBeAttached();
      await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(DataType.Number);
    });

    test("Decision table fix output column with different type than expression header", async ({
      bee,
      beePropertiesPanel,
    }) => {
      await beePropertiesPanel.open();

      // Setup a decision table with expression header and output column with two different types.
      // First create a decision table with two output columns, change the type of one and delete the other.
      await bee.selectExpressionMenu.selectDecisionTable();
      await bee.expression.asDecisionTable().addOutputAtStart();
      await bee.expression.asDecisionTable().outputHeaderAt(0).select();
      await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: DataType.Number });
      await bee.expression.asDecisionTable().outputHeaderAt(2).select();
      await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: DataType.Boolean });
      await bee.expression.asDecisionTable().outputHeaderAt(1).contextMenu.open();
      await bee.expression.asDecisionTable().outputHeaderAt(1).contextMenu.option("Delete").click();
      await bee.expression.asDecisionTable().outputHeaderAt(0).select();

      await expect(beePropertiesPanel.decisionTableOutputHeader.getColumnDataType()).not.toBeAttached();
      await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(DataType.Number);
    });
  });
});
test.describe("Decision Table - Cells Data Type - Constraint", () => {
  test.beforeEach(async ({ editor, palette, nodes }) => {
    await editor.open();

    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await nodes.edit({ name: DefaultNodeName.DECISION });
  });

  test.describe("Decision Table - Cells Data Type - Constraint - built-in data type", () => {
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
        beePropertiesPanel,
        bee,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setDataType({ newDataType: dataType });

        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).not.toBeAttached();
      });

      test(`Decision Table input rule properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        beePropertiesPanel,
        bee,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setDataType({ newDataType: dataType });
        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();

        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).not.toBeAttached();
      });

      test(`Decision Table output header properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        beePropertiesPanel,
        bee,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionDataType({ newDataType: dataType });

        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getColumnDataType()).not.toBeAttached();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();
      });

      test(`Decision Table output header with nested columns properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        beePropertiesPanel,
        bee,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().addOutputAtStart();

        await bee.expression.asDecisionTable().expressionHeaderCell.content.click();
        await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: dataType });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();

        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: dataType });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();

        await bee.expression.asDecisionTable().outputHeaderAt(1).select();
        await beePropertiesPanel.decisionTableOutputHeader.setDataType({ newDataType: dataType });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();
      });

      test(`Decision Table output rule properties panel shouldn't contain constraint - '${dataType}' data type`, async ({
        beePropertiesPanel,
        bee,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionDataType({ newDataType: dataType });
        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();

        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(`${dataType}`);
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).not.toBeAttached();
      });
    }
  });

  test.describe("Decision Table - Cells Data Type - Constraint - With custom data types", () => {
    test.beforeEach("create custom data types", async ({ editor, dataTypes }) => {
      // create string data type with enum constraint;
      await editor.changeTab({ tab: TabName.DATA_TYPES });
      await dataTypes.createFirstCustonDataType();
      await dataTypes.changeDataTypeName({ newName: "enumType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.String });
      await dataTypes.getEnumerationConstraintButton().click();
      await dataTypes.addEnumerationConstraint({ values: ["foo", "bar", "baz"] });

      // create number data type with range constraint;
      await dataTypes.createNewDataType();
      await dataTypes.changeDataTypeName({ newName: "rangeType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
      await dataTypes.getRangeConstraintButton().click();
      await dataTypes.addRangeConstraint({ values: ["10", "200"] });

      // create number data type with expression constraint;
      await dataTypes.createNewDataType();
      await dataTypes.changeDataTypeName({ newName: "expressionType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
      await dataTypes.getExpressionConstraintButton().click();
      await dataTypes.addExpressionConstraint({ value: "> 20" });

      // create date data type without constraint;
      await dataTypes.createNewDataType();
      await dataTypes.changeDataTypeName({ newName: "noneType" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Date });

      await editor.changeTab({ tab: TabName.EDITOR });
    });

    test.describe("Decision Table input column", () => {
      test(`Decision Table input header and rule properties panel should contain constraint - enum`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "enumType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(2)).toHaveValue("baz");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(2)).toHaveValue("baz");
      });

      test(`Decision Table input header and rule properties panel should contain constraint - enum edited`, async ({
        bee,
        beePropertiesPanel,
        editor,
        dataTypes,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "enumType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(2)).toHaveValue("baz");

        await editor.changeTab({ tab: TabName.DATA_TYPES });
        await dataTypes.selectDataType({ name: "enumType" });
        await dataTypes.addEnumerationConstraint({ values: ["qux"] });

        await editor.changeTab({ tab: TabName.EDITOR });
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(2)).toHaveValue("baz");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(3)).toHaveValue("qux");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(2)).toHaveValue("baz");
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(3)).toHaveValue("qux");
      });

      test(`Decision Table input header and rule properties panel should contain constraint - range`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "rangeType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();

        await expect(
          beePropertiesPanel.decisionTableInputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableInputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableInputRule.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableInputRule.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");
      });

      test(`Decision Table input header and rule properties panel should contain constraint - range edited`, async ({
        bee,
        beePropertiesPanel,
        editor,
        dataTypes,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "rangeType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableInputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableInputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await editor.changeTab({ tab: TabName.DATA_TYPES });
        await dataTypes.selectDataType({ name: "rangeType" });
        await dataTypes.changeRangeStartConstraint("20");

        await editor.changeTab({ tab: TabName.EDITOR });
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableInputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("20");
        await expect(
          beePropertiesPanel.decisionTableInputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableInputRule.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("20");
        await expect(
          beePropertiesPanel.decisionTableInputRule.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");
      });

      test(`Decision Table input header and rule properties panel should contain constraint - expression`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "expressionType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getExpressionConstraintValue()).toHaveText("> 20");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getExpressionConstraintValue()).toHaveText("> 20");
      });

      test(`Decision Table input header and rule properties panel should contain constraint - expression edited`, async ({
        bee,
        beePropertiesPanel,
        editor,
        dataTypes,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "expressionType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getExpressionConstraintValue()).toHaveText("> 20");

        await editor.changeTab({ tab: TabName.DATA_TYPES });
        await dataTypes.selectDataType({ name: "expressionType" });
        await dataTypes.addExpressionConstraint({ value: "< 30" });

        await editor.changeTab({ tab: TabName.EDITOR });
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getExpressionConstraintValue()).toHaveText("< 30");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getExpressionConstraintValue()).toHaveText("< 30");
      });

      test(`Decision Table input header and rule properties panel shouldn't contain constraint`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "noneType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getNoneConstraint()).toBeAttached();

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getNoneConstraint()).toBeAttached();
      });

      test(`Decision Table input header and rule properties panel should contain constraint - change between types`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "enumType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableInputHeader.getEnumerationValueAt(2)).toHaveValue("baz");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableInputRule.getEnumerationValueAt(2)).toHaveValue("baz");

        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "rangeType" });
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableInputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableInputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableInputRule.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableInputRule.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "expressionType" });
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getExpressionConstraintValue()).toHaveText("> 20");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getExpressionConstraintValue()).toHaveText("> 20");

        await bee.expression.asDecisionTable().inputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableInputHeader.setCustomDataType({ newDataType: "noneType" });
        await expect(beePropertiesPanel.decisionTableInputHeader.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputHeader.getNoneConstraint()).toBeAttached();

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).select();
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableInputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableInputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableInputRule.getNoneConstraint()).toBeAttached();
      });
    });

    test.describe("Decision Table output column", () => {
      test(`Decision Table output header and rule properties panel should contain constraint - enum`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "enumType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*enumType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(2)).toHaveValue("baz");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(2)).toHaveValue("baz");
      });

      test(`Decision Table output header and rule properties panel should contain constraint - enum edited`, async ({
        bee,
        beePropertiesPanel,
        editor,
        dataTypes,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "enumType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*enumType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(2)).toHaveValue("baz");

        await editor.changeTab({ tab: TabName.DATA_TYPES });
        await dataTypes.selectDataType({ name: "enumType" });
        await dataTypes.addEnumerationConstraint({ values: ["qux"] });

        await editor.changeTab({ tab: TabName.EDITOR });
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*enumType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(2)).toHaveValue("baz");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(3)).toHaveValue("qux");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(2)).toHaveValue("baz");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(3)).toHaveValue("qux");
      });

      test(`Decision Table output header and rule properties panel should contain constraint - range`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "rangeType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*rangeType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();

        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableOutputRule.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableOutputRule.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");
      });

      test(`Decision Table output header and rule properties panel should contain constraint - range edited`, async ({
        bee,
        beePropertiesPanel,
        editor,
        dataTypes,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "rangeType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*rangeType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await editor.changeTab({ tab: TabName.DATA_TYPES });
        await dataTypes.selectDataType({ name: "rangeType" });
        await dataTypes.changeRangeStartConstraint("20");

        await editor.changeTab({ tab: TabName.EDITOR });
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*rangeType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("20");
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableOutputRule.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("20");
        await expect(
          beePropertiesPanel.decisionTableOutputRule.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");
      });

      test(`Decision Table output header and rule properties panel should contain constraint - expression`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({
          newDataType: "expressionType",
        });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*expressionType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionConstraintValue()).toHaveText("> 20");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getExpressionConstraintValue()).toHaveText("> 20");
      });

      test(`Decision Table output header and rule properties panel should contain constraint - expression edited`, async ({
        bee,
        beePropertiesPanel,
        editor,
        dataTypes,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({
          newDataType: "expressionType",
        });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*expressionType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionConstraintValue()).toHaveText("> 20");

        await editor.changeTab({ tab: TabName.DATA_TYPES });
        await dataTypes.selectDataType({ name: "expressionType" });
        await dataTypes.addExpressionConstraint({ value: "< 30" });

        await editor.changeTab({ tab: TabName.EDITOR });
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*expressionType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionConstraintValue()).toHaveText("< 30");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getExpressionConstraintValue()).toHaveText("< 30");
      });

      test(`Decision Table output header and rule properties panel shouldn't contain constraint`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "noneType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*noneType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getNoneConstraint()).toBeAttached();

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getNoneConstraint()).toBeAttached();
      });

      test(`Decision Table output header and rule properties panel should contain constraint - change between types`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "enumType" });

        // Using RegExp matcher as it will check for &nbsp; characters as well.
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*enumType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(2)).toHaveValue("baz");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(2)).toHaveValue("baz");

        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "rangeType" });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*rangeType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableOutputRule.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableOutputRule.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({
          newDataType: "expressionType",
        });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*expressionType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionConstraintValue()).toHaveText("> 20");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getExpressionConstraintValue()).toHaveText("> 20");

        await bee.expression.asDecisionTable().outputHeaderAt(0).select();
        await beePropertiesPanel.decisionTableOutputHeader.setExpressionCustomDataType({ newDataType: "noneType" });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionDataType()).toHaveValue(
          /^\s*noneType\s$/i
        );
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getNoneConstraint()).toBeAttached();

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getNoneConstraint()).toBeAttached();
      });
    });

    test.describe("Decision Table output header with nested columns", () => {
      test(`Decision Table output header and rule with nested columns properties panel should contain constraint - each column with a type`, async ({
        bee,
        beePropertiesPanel,
      }) => {
        await beePropertiesPanel.open();
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().addOutputAtStart();
        await bee.expression.asDecisionTable().addOutputAtStart();
        await bee.expression.asDecisionTable().addOutputAtStart();

        await bee.expression.asDecisionTable().expressionHeaderCell.select();
        await beePropertiesPanel.decisionTableOutputHeader.setCustomDataType({ newDataType: "enumType" });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).not.toBeAttached();

        await bee.expression.asDecisionTable().outputHeaderAt(4).select();
        await beePropertiesPanel.decisionTableOutputHeader.setCustomDataType({ newDataType: "enumType" });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputHeader.getEnumerationValueAt(2)).toHaveValue("baz");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 5 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*enumType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(0)).toHaveValue("foo");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(1)).toHaveValue("bar");
        await expect(beePropertiesPanel.decisionTableOutputRule.getEnumerationValueAt(2)).toHaveValue("baz");

        await bee.expression.asDecisionTable().outputHeaderAt(3).select();
        await beePropertiesPanel.decisionTableOutputHeader.setCustomDataType({ newDataType: "rangeType" });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableOutputHeader.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 4 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*rangeType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(
          beePropertiesPanel.decisionTableOutputRule.getRangeConstraintValueAt(RangeConstraintPosition.START)
        ).toHaveValue("10");
        await expect(
          beePropertiesPanel.decisionTableOutputRule.getRangeConstraintValueAt(RangeConstraintPosition.END)
        ).toHaveValue("200");

        await bee.expression.asDecisionTable().outputHeaderAt(2).select();
        await beePropertiesPanel.decisionTableOutputHeader.setCustomDataType({ newDataType: "expressionType" });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getExpressionConstraintValue()).toHaveText("> 20");

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 3 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*expressionType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getExpressionConstraintValue()).toHaveText("> 20");

        await bee.expression.asDecisionTable().outputHeaderAt(1).select();
        await beePropertiesPanel.decisionTableOutputHeader.setCustomDataType({ newDataType: "noneType" });
        await expect(beePropertiesPanel.decisionTableOutputHeader.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputHeader.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputHeader.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputHeader.getNoneConstraint()).toBeAttached();

        await bee.expression.asDecisionTable().cellAt({ row: 1, column: 2 }).select();
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toHaveValue(/^\s*noneType\s$/i);
        await expect(beePropertiesPanel.decisionTableOutputRule.getDataType()).toBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getConstraintSection()).toBeAttached();
        await beePropertiesPanel.decisionTableOutputRule.expectConstraintButtonsToBeDisabled();
        await expect(beePropertiesPanel.decisionTableOutputRule.getNoneConstraint()).toBeAttached();
      });
    });

    test.describe("Decision Table output header under context expression", () => {
      test("Decision Table output header data type change", async ({ bee, editor, palette, nodes }) => {
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/1851",
        });

        await editor.open();

        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
        await nodes.edit({ name: DefaultNodeName.DECISION });

        await bee.selectExpressionMenu.selectContext();
        await bee.expression.asContext().entry(0).selectExpressionMenu.selectDecisionTable();

        await bee.expression.asContext().entry(0).expression.asDecisionTable().expressionHeaderCell.open();

        await bee.expression
          .asContext()
          .entry(0)
          .expression.asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: DataType.DateTimeDuration, close: true });

        expect(await bee.expression.asContext().entry(0).variable.content.textContent()).toEqual(
          "ContextEntry-1(days and time duration)"
        );
        expect(
          await bee.expression
            .asContext()
            .entry(0)
            .expression.asDecisionTable()
            .expressionHeaderCell.content.textContent()
        ).toEqual("ContextEntry-1(days and time duration)");
      });
    });
  });
});
