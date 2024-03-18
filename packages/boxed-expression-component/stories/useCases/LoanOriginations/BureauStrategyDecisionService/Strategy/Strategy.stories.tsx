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
  title: "Use cases/Loan Originations/Bureau Strategy Decision Service/Strategy",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const strategyExpression: DecisionTableExpressionDefinition = {
  __$$element: "decisionTable",
  "@_id": "_1AAE9CB0-2B4B-4159-A994-A93D5F91EE23",
  "@_label": "Strategy",
  "@_typeRef": "t.Strategy",
  "@_hitPolicy": "UNIQUE",
  annotation: [{ "@_name": "annotation-1" }],
  input: [
    {
      "@_id": "_000159BA-3887-4445-8DC7-3A7A82EE3ED9",
      inputExpression: {
        "@_typeRef": "t.BureauCallType",
        "@_id": "_2B5896FA-9555-4CC9-8893-3AE9CB9C0FA9",
        text: { __$$text: "Bureau call type" },
      },
    },
    {
      "@_id": "_3D46C973-60DA-433A-B3DE-F2B6DB2B892C",
      inputExpression: {
        "@_typeRef": "t.Eligibility",
        "@_id": "_0E4FC630-E178-4DE4-BA63-D8A02E14C8A2",
        text: { __$$text: "Eligibility" },
      },
    },
  ],
  output: [
    {
      "@_id": "_03C0D3AD-AD53-45A0-BB96-D51579F00EA7",
      "@_name": "Strategy",
      "@_typeRef": "t.Strategy",
    },
  ],
  rule: [
    {
      "@_id": "_9F3719DD-B70B-4AA9-AF1B-75870F163136",
      inputEntry: [
        { "@_id": "_15F77394-36F1-4EEF-BA70-8FB63EA4F33E", text: { __$$text: "-" } },
        {
          "@_id": "_CD01C61C-0433-4FBB-A250-14B0A3F412E9",
          text: { __$$text: '"Ineligible"' },
        },
      ],
      outputEntry: [{ "@_id": "_AB9B7F72-DF75-41F8-9DE4-344029170BBC", text: { __$$text: '"Decline"' } }],
    },
    {
      "@_id": "_6DEFC188-313E-4A30-A02C-FFC6730A7F63",
      inputEntry: [
        {
          "@_id": "_781B33A2-70C5-4A5A-B891-0A06016CFA1D",
          text: { __$$text: '"Full", "Mini"' },
        },
        { "@_id": "_29A613E9-3FF8-4CC6-B4B9-CEC2E9CC0F07", text: { __$$text: '"Eligible"' } },
      ],
      outputEntry: [{ "@_id": "_8E245858-B826-4818-8AF4-0A7C3C0130E8", text: { __$$text: '"Bureau"' } }],
    },
    {
      "@_id": "_C2A5C598-38B0-4E62-BEF7-AD24D35918D5",
      inputEntry: [
        { "@_id": "_8052B519-FE3D-4272-9F4E-DA3761638C8D", text: { __$$text: '"None"' } },
        { "@_id": "_5BE642C3-2FD1-4608-BFCA-35F28A872AE2", text: { __$$text: '"Eligible"' } },
      ],
      outputEntry: [{ "@_id": "_9C1FC548-5F28-415C-B7C7-6C9BB813134A", text: { __$$text: '"Through"' } }],
    },
  ],
};

export const strategyWidthsById = new Map([
  ["_1AAE9CB0-2B4B-4159-A994-A93D5F91EE23", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 138, 129, 113, 100]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: strategyExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: strategyWidthsById,
  },
};
