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
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/BoxedExpressionEditor";
import { BoxedExpressionEditorStory, BoxedExpressionEditorStoryArgs } from "../../boxedExpressionStoriesWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import { DmnBuiltInDataType, generateUuid } from "../../../src/api";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "../../../src/expressions/RelationExpression/RelationExpression";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Relation",
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
      __$$element: "relation",
      "@_id": generateUuid(),
      "@_label": "Expression Name",
      column: [
        {
          "@_id": generateUuid(),
          "@_name": "column-1",
        },
      ],
      row: [
        {
          "@_id": generateUuid(),
          expression: [
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: RELATION_EXPRESSION_DEFAULT_VALUE },
            },
          ],
        },
      ],
    },
    isResetSupportedOnRootExpression: true,
  },
};

export const People: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...Base.args,
    expression: {
      __$$element: "relation",
      "@_id": generateUuid(),
      "@_label": "People",
      "@_typeRef": "tPeople",
      column: [
        {
          "@_id": generateUuid(),
          "@_name": "Name",
          "@_typeRef": DmnBuiltInDataType.String,
        },
        {
          "@_id": generateUuid(),
          "@_name": "Age",
          "@_typeRef": DmnBuiltInDataType.Number,
        },
        {
          "@_id": generateUuid(),
          "@_name": "Country",
          "@_typeRef": DmnBuiltInDataType.String,
        },
      ],
      row: [
        {
          "@_id": generateUuid(),
          expression: [
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: '"Luiz"' },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "30" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: '"Brazil"' },
            },
          ],
        },
        {
          "@_id": generateUuid(),
          expression: [
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: '"Tiago"' },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "29" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: '"USA"' },
            },
          ],
        },
      ],
    },
  },
};

export const Bigger: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...Base.args,
    expression: {
      __$$element: "relation",
      "@_id": generateUuid(),
      "@_label": "Expression Name",
      column: [
        {
          "@_id": generateUuid(),
          "@_name": "column-1",
        },
        {
          "@_id": generateUuid(),
          "@_name": "column-2",
        },
        {
          "@_id": generateUuid(),
          "@_name": "column-3",
        },
        {
          "@_id": generateUuid(),
          "@_name": "column-4",
        },
      ],
      row: [
        {
          "@_id": generateUuid(),
          expression: [
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
          ],
        },
        {
          "@_id": generateUuid(),
          expression: [
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
          ],
        },
        {
          "@_id": generateUuid(),
          expression: [
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
            {
              __$$element: "literalExpression",
              "@_id": generateUuid(),
              text: { __$$text: "" },
            },
          ],
        },
      ],
    },
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
            __$$element: "relation",
            "@_id": generateUuid(),
            column: [
              {
                "@_id": generateUuid(),
                "@_name": "column-1",
              },
            ],
            row: [
              {
                "@_id": generateUuid(),
                expression: [
                  {
                    __$$element: "literalExpression",
                    "@_id": generateUuid(),
                    text: { __$$text: RELATION_EXPRESSION_DEFAULT_VALUE },
                  },
                ],
              },
            ],
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
