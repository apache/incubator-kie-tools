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

import { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/BoxedExpressionEditor";
import { BoxedExpressionEditorStory, BoxedExpressionEditorStoryArgs } from "../../boxedExpressionStoriesWrapper";
import { generateUuid } from "../../../src/api";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Some",
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
      __$$element: "some",
      "@_id": generateUuid(),
      "@_label": "Expression Name",
      in: {
        "@_id": generateUuid(),
        expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
      },
      satisfies: {
        "@_id": generateUuid(),
        expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
      },
    },
  },
};

export const Readonly: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "some",
      "@_id": generateUuid(),
      "@_label": "Expression Name",
      in: {
        "@_id": generateUuid(),
        expression: {
          "@_id": generateUuid(),
          __$$element: "literalExpression",
          text: { __$$text: "['read', 'only']" },
        },
      },
      satisfies: {
        "@_id": generateUuid(),
        expression: {
          "@_id": generateUuid(),
          __$$element: "literalExpression",
          text: { __$$text: "readOnlyTest(itemReadOnly)" },
        },
      },
      "@_iteratorVariable": "itemReadOnly",
    },
    isReadOnly: true,
  },
};
