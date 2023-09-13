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
import {
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  generateUuid,
} from "../../../src/api";
import { CONTEXT_ENTRY_INFO_MIN_WIDTH } from "../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Function",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
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
      logicType: ExpressionDefinitionLogicType.Function,
      functionKind: FunctionExpressionDefinitionKind.Feel,
      formalParameters: [],
      expression: {
        id: generateUuid(),
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
      },
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const InstallmentCalculation: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Installment calculation",
      dataType: DmnBuiltInDataType.Number,
      logicType: ExpressionDefinitionLogicType.Function,
      functionKind: FunctionExpressionDefinitionKind.Feel,
      formalParameters: [
        {
          id: generateUuid(),
          name: "Amount",
          dataType: DmnBuiltInDataType.Number,
        },
        {
          id: generateUuid(),
          name: "Rate",
          dataType: DmnBuiltInDataType.Number,
        },
        {
          id: generateUuid(),
          name: "Term",
          dataType: DmnBuiltInDataType.Number,
        },
      ],
      expression: {
        id: generateUuid(),
        logicType: ExpressionDefinitionLogicType.Literal,
        dataType: DmnBuiltInDataType.Undefined,
        content: `(Amount*Rate/12) /\n(1-(1+Rate/12)**-Term)`,
        width: 300,
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
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Feel,
            formalParameters: [],
            expression: {
              id: generateUuid(),
              logicType: ExpressionDefinitionLogicType.Undefined,
              dataType: DmnBuiltInDataType.Undefined,
            },
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
