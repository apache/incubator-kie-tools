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

import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/BoxedExpressionEditor";
import { BoxedExpressionEditorStory, BoxedExpressionEditorStoryArgs } from "../../boxedExpressionStoriesWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import { DmnBuiltInDataType, generateUuid } from "../../../src/api";
import { INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME } from "../../../src/expressions/InvocationExpression/InvocationExpression";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Invocation",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
  excludeStories: ["BoxedExpressionEditorStory"],
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorStoryArgs>;

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Base: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "invocation",
      "@_id": generateUuid(),
      "@_label": "Expression Name",
      binding: [
        {
          parameter: {
            "@_id": generateUuid(),
            "@_name": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
          },
        },
      ],
      expression: {
        "@_id": generateUuid(),
        __$$element: "literalExpression",
        text: { __$$text: "FUNCTION NAME" },
      },
    },
    isResetSupportedOnRootExpression: true,
  },
};

export const MonthlyInstallment: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "invocation",
      "@_id": generateUuid(),
      "@_label": "Monthly Installment",
      "@_typeRef": DmnBuiltInDataType.Number,
      binding: [
        {
          parameter: {
            "@_id": generateUuid(),
            "@_name": "Rate",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            "@_label": "Rate",
            "@_typeRef": DmnBuiltInDataType.Number,
            text: { __$$text: "0.08" },
          },
        },
        {
          parameter: {
            "@_id": generateUuid(),
            "@_name": "Term",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            "@_label": "Term",
            "@_typeRef": DmnBuiltInDataType.Number,
            text: { __$$text: "36" },
          },
        },
        {
          parameter: {
            "@_id": generateUuid(),
            "@_name": "Amount",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            "@_label": "Amount",
            "@_typeRef": DmnBuiltInDataType.Number,
            text: { __$$text: "10000" },
          },
        },
      ],
      expression: {
        "@_id": generateUuid(),
        __$$element: "literalExpression",
        text: { __$$text: "Installment Calculation" },
      },
    },
    isResetSupportedOnRootExpression: false,
  },
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Nested: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "context",
      "@_id": generateUuid(),
      "@_label": "Expression Name",
      contextEntry: [
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-1",
          },
          expression: {
            __$$element: "invocation",
            "@_id": generateUuid(),
            "@_label": "Expression Name",
            binding: [
              {
                parameter: {
                  "@_id": generateUuid(),
                  "@_name": INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
                },
              },
            ],
            expression: {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "FUNCTION" },
            },
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
