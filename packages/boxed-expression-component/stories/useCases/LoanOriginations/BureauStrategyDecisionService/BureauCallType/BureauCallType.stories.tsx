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

import { DecisionTableExpressionDefinition } from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { beeGwtService, BoxedExpressionEditorWrapper, pmmlDocuments } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Bureau Strategy Decision Service/Bureau call type",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const bureauCallTypeExpression: DecisionTableExpressionDefinition = {
  __$$element: "decisionTable",
  "@_id": "_7FB4A019-EC04-4153-86C1-C90A8BA8E6C3",
  "@_label": "Bureau call type",
  "@_typeRef": "t.BureauCallType",
  "@_hitPolicy": "UNIQUE",
  annotation: [{ "@_name": "annotation-1" }],
  input: [
    {
      "@_id": "_EDEB6C03-3FD1-4845-A18F-1463314BB5FB",
      "@_label": "Pre-bureau risk category",
      inputExpression: {
        "@_typeRef": "t.BureauRiskCategory",
        "@_id": "_72DD65BD-56CA-4429-B5FD-2EBDEEE01B9B",
      },
    },
  ],
  output: [
    {
      "@_id": "_BC216B66-6B93-47B7-8F37-8B03D392D653",
      "@_name": "Bureau call type",
      "@_typeRef": "t.BureauCallType",
    },
  ],
  rule: [
    {
      "@_id": "_BCF175DE-81A0-428C-BC28-4D12605ACBE8",
      inputEntry: [
        {
          "@_id": "_C3306F98-0B9A-482C-8951-72F58C101FE3",
          text: { __$$text: '"High", "Medium"' },
        },
      ],
      outputEntry: [{ "@_id": "_5D1A4638-1023-44B2-9FD3-47862126935F", text: { __$$text: '"Full"' } }],
    },
    {
      "@_id": "_B1C67777-2868-471F-98DE-C634BC3FBD90",
      inputEntry: [{ "@_id": "_53EEAFDA-1F59-49A6-A347-9F37F8FCD9E4", text: { __$$text: '"Low"' } }],
      outputEntry: [{ "@_id": "_F22833EE-A811-428B-979B-EE1F2E7FAF96", text: { __$$text: '"Mini"' } }],
    },
    {
      "@_id": "_E7F703F9-08E3-4B35-AFF5-F6F5F2AE5640",
      inputEntry: [
        {
          "@_id": "_9E274DD9-73C1-4D06-B97A-C0112D33AB8A",
          text: { __$$text: '"Very Low", "Decline"' },
        },
      ],
      outputEntry: [{ "@_id": "_AB3E865D-1AB4-4E49-9B5B-BD328190BBA8", text: { __$$text: '"None"' } }],
    },
  ],
};

export const abureauCallTypeWidthsById = new Map([
  ["_7FB4A019-EC04-4153-86C1-C90A8BA8E6C3", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 210, 175, 145]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: bureauCallTypeExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: abureauCallTypeWidthsById,
  },
};
