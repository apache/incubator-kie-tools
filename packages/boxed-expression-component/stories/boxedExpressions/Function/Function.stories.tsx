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
import { DmnBuiltInDataType, BoxedFunctionKind, generateUuid } from "../../../src/api";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Function",
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
      __$$element: "functionDefinition",
      "@_id": generateUuid(),
      "@_label": "Expression Name",
      "@_kind": BoxedFunctionKind.Feel,
    },
    isResetSupportedOnRootExpression: true,
  },
};

export const InstallmentCalculation: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "functionDefinition",
      "@_id": generateUuid(),
      "@_label": "Installment calculation",
      "@_typeRef": DmnBuiltInDataType.Number,
      "@_kind": BoxedFunctionKind.Feel,
      formalParameter: [
        {
          "@_id": generateUuid(),
          "@_name": "Amount",
          "@_typeRef": DmnBuiltInDataType.Number,
        },
        {
          "@_id": generateUuid(),
          "@_name": "Rate",
          "@_typeRef": DmnBuiltInDataType.Number,
        },
        {
          "@_id": generateUuid(),
          "@_name": "Term",
          "@_typeRef": DmnBuiltInDataType.Number,
        },
      ],
      expression: {
        __$$element: "literalExpression",
        "@_id": "_ACDB9FB9-E16C-42CA-BBCC-DA1DADCFD7E2",
        text: { __$$text: `(Amount*Rate/12) / (1-(1+Rate/12)**-Term)` },
      },
    },
    isResetSupportedOnRootExpression: false,
    widthsById: { "_ACDB9FB9-E16C-42CA-BBCC-DA1DADCFD7E2": [347] },
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
            __$$element: "functionDefinition",
            "@_id": generateUuid(),
            "@_label": "Expression Name",
            "@_kind": BoxedFunctionKind.Feel,
            formalParameter: [],
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
