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

import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../../boxedExpressionStoriesWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../../src/api";
import { CONTEXT_ENTRY_INFO_MIN_WIDTH } from "../../../src/resizing/WidthConstants";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
} from "../../../src/expressions/InvocationExpression";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Invocation",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
  excludeStories: ["BoxedExpressionEditorWrapper"],
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Base: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Expression Name",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Invocation,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      bindingEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
          },
          entryExpression: {
            id: generateUuid(),
            name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
            logicType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
          },
        },
      ],
      invokedFunction: {
        id: generateUuid(),
        name: "FUNCTION",
      },
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const MonthlyInstallment: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Monthly Installment",
      dataType: DmnBuiltInDataType.Number,
      logicType: ExpressionDefinitionLogicType.Invocation,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      bindingEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "Rate",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: generateUuid(),
            name: "Rate",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "0.08",
          },
        },
        {
          entryInfo: {
            id: generateUuid(),
            name: "Term",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: generateUuid(),
            name: "Term",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "36",
          },
        },
        {
          entryInfo: {
            id: generateUuid(),
            name: "Amount",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: generateUuid(),
            name: "Amount",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "10000",
          },
        },
      ],
      invokedFunction: {
        id: generateUuid(),
        name: "Installment Calculation",
      },
    },
    isResetSupportedOnRootExpression: false,
  },
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Nested: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Expression Name",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Context,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      result: {
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
        id: generateUuid(),
      },
      contextEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
          },
          entryExpression: {
            id: generateUuid(),
            name: "Expression Name",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Invocation,
            entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
            bindingEntries: [
              {
                entryInfo: {
                  id: generateUuid(),
                  name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
                  dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
                },
                entryExpression: {
                  id: generateUuid(),
                  name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
                  dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
                  logicType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
                },
              },
            ],
            invokedFunction: {
              id: generateUuid(),
              name: "FUNCTION",
            },
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
