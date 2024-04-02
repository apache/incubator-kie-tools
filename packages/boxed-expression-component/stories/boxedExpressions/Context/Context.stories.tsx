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
import { CONTEXT_ENTRY_VARIABLE_MIN_WIDTH } from "../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Context",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
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
      __$$element: "context",
      "@_id": "_35255561-88FA-4A78-9C3F-61855213EE0F",
      "@_label": "Expression Name",
      "@_typeRef": DmnBuiltInDataType.Undefined,
      contextEntry: [
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-1",
            "@_typeRef": DmnBuiltInDataType.Undefined,
          },
          expression: undefined!,
        },
      ],
    },
    isResetSupportedOnRootExpression: true,
    widthsById: { "_35255561-88FA-4A78-9C3F-61855213EE0F": [CONTEXT_ENTRY_VARIABLE_MIN_WIDTH] },
  },
};

export const InstallmentCalculation: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "context",
      "@_id": generateUuid(),
      "@_label": "Installment calculation",
      "@_typeRef": DmnBuiltInDataType.Number,
      contextEntry: [
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "Fee",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            "@_label": "Fee",
            "@_typeRef": DmnBuiltInDataType.Number,
            text: { __$$text: "25" },
          },
        },
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "Repayments",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            "@_label": "Repayments",
            "@_typeRef": DmnBuiltInDataType.Number,
            text: { __$$text: `(Amount*Rate/12) /\n(1-(1+Rate/12)**-Term)` },
          },
        },
        {
          // The result expression is a ContextEntry without variable
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            "@_label": "Result Expression",
            "@_typeRef": DmnBuiltInDataType.Number,
            text: { __$$text: "Fee + Repayments" },
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const Customer: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "context",
      "@_id": generateUuid(),
      "@_label": "Customer",
      "@_typeRef": "tCustomer",
      contextEntry: [
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "Name",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            "@_label": "Name",
            "@_typeRef": DmnBuiltInDataType.Number,
            text: { __$$text: "Luiz" },
          },
        },
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "Age",
            "@_typeRef": DmnBuiltInDataType.Number,
          },
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            "@_label": "Age",
            "@_typeRef": DmnBuiltInDataType.Number,
            text: { __$$text: "30" },
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const Nested: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "context",
      "@_id": "_35255561-88FA-4A78-9C3F-61855213EE0F",
      "@_label": "Expression Name",
      contextEntry: [
        {
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-1",
          },
          expression: {
            __$$element: "context",
            "@_id": "_5D97B484-8003-4323-AADB-AA4C6F3ECA73",
            "@_label": "Expression Name",
            contextEntry: [
              {
                variable: {
                  "@_id": generateUuid(),
                  "@_name": "ContextEntry-1",
                },
                expression: {
                  __$$element: "literalExpression",
                  "@_id": generateUuid(),
                  "@_label": "ContextEntry-1",
                  text: { __$$text: "" },
                },
              },
            ],
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
    widthsById: {
      "_35255561-88FA-4A78-9C3F-61855213EE0F": [CONTEXT_ENTRY_VARIABLE_MIN_WIDTH],
      "_5D97B484-8003-4323-AADB-AA4C6F3ECA73": [CONTEXT_ENTRY_VARIABLE_MIN_WIDTH],
    },
  },
};
