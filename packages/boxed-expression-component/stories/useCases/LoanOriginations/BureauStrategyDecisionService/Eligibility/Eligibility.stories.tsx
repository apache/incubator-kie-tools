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

import { DecisionTableExpressionDefinition, DmnBuiltInDataType } from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { beeGwtService, BoxedExpressionEditorWrapper, pmmlDocuments } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Bureau Strategy Decision Service/Eligibility",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const eligibilityExpression: DecisionTableExpressionDefinition = {
  __$$element: "decisionTable",
  "@_id": "_830C9FEF-FA57-4245-8FFF-1E7F305F4536",
  "@_label": "Eligibility",
  "@_typeRef": "t.Eligibility",

  "@_hitPolicy": "FIRST",

  annotation: [{ "@_name": "annotation-1" }],
  input: [
    {
      "@_id": "_60976225-DA1D-4C7E-A910-E341D763C218",
      "@_label": "Applicant data.Age",

      inputExpression: {
        "@_typeRef": DmnBuiltInDataType.Number,
        "@_id": "_7F37D542-44A4-41CF-8FB1-A28846FB9733",
      },
    },
    {
      "@_id": "_82E34039-BBE7-46C8-91BF-B2BB0E8EE905",
      "@_label": "Pre-bureau affordabillity",

      inputExpression: {
        "@_typeRef": DmnBuiltInDataType.Boolean,
        "@_id": "_8037BDB5-E08B-4E4B-8BFB-86ECBB0C8E19",
      },
    },
    {
      "@_id": "_1D329E96-6CF6-46DD-A9EA-3478C5F39704",
      "@_label": "Pre-bureau risk category",

      inputExpression: {
        "@_typeRef": "t.BureauRiskCategory",
        "@_id": "_335DFCA2-2C76-4D72-9534-DF3F1453F405",
      },
    },
  ],
  output: [
    {
      "@_id": "_AAEB91CE-F337-4071-A9F9-8D225C0013C8",
      "@_label": "Eligibility",
      "@_typeRef": "t.Eligibility",
    },
  ],
  rule: [
    {
      "@_id": "_A248202B-E33B-40E9-99E7-1259AD34AC98",
      inputEntry: [
        { "@_id": "_D2A538BE-A6AE-469A-8A63-A8826A90FCE2", text: { __$$text: "<18" } },
        { "@_id": "_146D0D81-1AFD-4BA7-93E4-A0F62D39C1B0", text: { __$$text: "-" } },
        { "@_id": "_F374FE00-72B8-43AC-818E-0791FA7B0B1F", text: { __$$text: "-" } },
      ],
      outputEntry: [
        {
          "@_id": "_2A1B4461-1291-4ECE-903D-01127D1A1C87",
          text: { __$$text: '"Ineligible"' },
        },
      ],
    },
    {
      "@_id": "_EA508F7C-4858-4AAD-B32B-DB34D920D1F9",
      inputEntry: [
        { "@_id": "_1C08CC38-579D-4C48-981C-8F0E8EEFD066", text: { __$$text: "-" } },
        { "@_id": "_2DCB24FA-D34B-49F4-B93E-E88495D20DB0", text: { __$$text: "false" } },
        { "@_id": "_FB259E0D-3568-41DC-9C60-0843623B83AE", text: { __$$text: "-" } },
      ],
      outputEntry: [
        {
          "@_id": "_CDC56FA2-FF08-44BB-8A5F-83FE7CECE62E",
          text: { __$$text: '"Ineligible"' },
        },
      ],
    },
    {
      "@_id": "_F1586A1F-9263-400B-BD5A-2054DE7237B4",
      inputEntry: [
        { "@_id": "_992575D4-9337-4500-B95E-0B52FACABECE", text: { __$$text: "-" } },
        { "@_id": "_1E5D4C7B-2FBB-4382-B776-6F4FCE24F04C", text: { __$$text: "-" } },
        { "@_id": "_94C4B06B-D58D-492D-80FC-BC7177D83951", text: { __$$text: '"Decline"' } },
      ],
      outputEntry: [
        {
          "@_id": "_68120756-8AEA-4946-A8B6-45D4CF27B793",
          text: { __$$text: '"Ineligible"' },
        },
      ],
    },
    {
      "@_id": "_FF7FFE97-DF00-442F-B3E3-E3C66C95160F",
      inputEntry: [
        { "@_id": "_7AE9C2AB-2148-42F7-AC02-B465B844541A", text: { __$$text: "-" } },
        { "@_id": "_6BCDF325-5A65-4C50-97B6-D907938B31D7", text: { __$$text: "-" } },
        { "@_id": "_1B17B6E8-BCC7-46E6-8EA7-45CB3EDE3DBF", text: { __$$text: "-" } },
      ],
      outputEntry: [{ "@_id": "_17CCED2E-3B24-4CC3-B4E9-1DBE6EC74871", text: { __$$text: '"Eligible"' } }],
    },
  ],
};

export const eligibilityWidthsById = new Map([
  ["_830C9FEF-FA57-4245-8FFF-1E7F305F4536", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 150, 180, 180, 150, 100]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: eligibilityExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: eligibilityWidthsById,
  },
};
