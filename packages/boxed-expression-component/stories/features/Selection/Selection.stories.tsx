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
import * as Empty from "../../misc/Empty/EmptyExpression.stories";
import * as Literal from "../../boxedExpressions/Literal/Literal.stories";
import * as Relation from "../../boxedExpressions/Relation/Relation.stories";
import * as DecisionTable from "../../boxedExpressions/DecisionTable/DecisionTable.stories";
import { BoxedDecisionTable, generateUuid } from "../../../src/api";
import { BoxedExpressionEditorStoryArgs } from "../../boxedExpressionStoriesWrapper";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Features/Selection",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorStoryArgs>;

export const CanDrive: Story = {
  ...Literal.CanDrive,
  args: {
    ...Literal.CanDrive.args,
    isResetSupportedOnRootExpression: true,
  },
};

export const People: Story = {
  ...Relation.People,
  args: {
    ...Relation.People.args,
    isResetSupportedOnRootExpression: true,
  },
};

export const EmptyExpression: Story = {
  ...Empty.Base,
  args: {
    ...Empty.Base.args,
    isResetSupportedOnRootExpression: true,
  },
};

export const Discount: Story = {
  ...DecisionTable.Discount,
  args: {
    ...DecisionTable.Discount.args!,
    expression: {
      ...(DecisionTable.Discount.args!.expression! as BoxedDecisionTable),
      rule: [
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: "-" } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: "" } }],
          annotationEntry: [{ text: { __$$text: "" } }],
        },
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: "-" } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: "" } }],
          annotationEntry: [{ text: { __$$text: "" } }],
        },
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: "-" } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: "" } }],
          annotationEntry: [{ text: { __$$text: "" } }],
        },
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: "-" } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: "" } }],
          annotationEntry: [{ text: { __$$text: "" } }],
        },
      ],
    },
  },
};
