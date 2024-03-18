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

import { DmnBuiltInDataType, InvocationExpressionDefinition } from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { beeGwtService, BoxedExpressionEditorWrapper, pmmlDocuments } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Routing Decision Service/Post Bureau Affordability",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const postBureauAffordabilityExpression: InvocationExpressionDefinition = {
  __$$element: "invocation",
  "@_id": "_1E880009-77B2-4309-AE2A-8964E05636B1",
  "@_label": "Post-bureau affordability",
  "@_typeRef": DmnBuiltInDataType.Boolean,
  expression: {
    __$$element: "literalExpression",
    "@_id": "_0B151359-63F6-4DAB-BC88-53F34446F9C7",
    text: { __$$text: "f.Affordability calculation" },
  },
  binding: [
    {
      parameter: {
        "@_id": "_2882DEEF-03E9-431B-9E96-0B9E06DBDBFA",
        "@_name": "Monthly Income",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_FAD12087-C2B6-4060-840B-077972DCCB80",
        "@_label": "Monthly Income",
        "@_typeRef": DmnBuiltInDataType.Number,
        text: { __$$text: "Applicant data.Monthly.Income" },
      },
    },
    {
      parameter: {
        "@_id": "_ECCC661F-9489-494A-81A9-CADD18075235",
        "@_name": "Monthly Repayments",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_79F0E706-6018-49AB-86EB-78AC55582CE7",
        "@_label": "Monthly Repayments",
        "@_typeRef": DmnBuiltInDataType.Number,
        text: { __$$text: "Applicant data.Monthly.Repayments" },
      },
    },
    {
      parameter: {
        "@_id": "_50CB1671-8A51-40A2-B86C-5A59A76ADA99",
        "@_name": "Monthly Expenses",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_A541AA7D-2DE8-4E04-B5C9-0320EE7D26BE",
        "@_label": "Monthly Expenses",
        "@_typeRef": DmnBuiltInDataType.Number,
        text: { __$$text: "Applicant data.Monthly.Expenses" },
      },
    },
    {
      parameter: {
        "@_id": "_1B4A9572-B67B-4D52-94B0-A990B3114810",
        "@_name": "Risk Category",
        "@_typeRef": "t.BureauRiskCategory" as DmnBuiltInDataType,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_9C3C6ED5-F875-4C5B-A7C1-76D10DC2E5DF",
        "@_label": "Risk Category",
        "@_typeRef": "t.BureauRiskCategory" as DmnBuiltInDataType,
        text: {
          __$$text: "Post-bureau risk category",
        },
      },
    },
    {
      parameter: {
        "@_id": "_6C831C67-F80C-44FC-B4B6-55FD4BAE1A79",
        "@_name": "Required Monthly Installment",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_656110E9-A234-46A4-B4CF-7FD691F8F565",
        "@_label": "Required Monthly Installment",
        "@_typeRef": DmnBuiltInDataType.Number,

        text: { __$$text: "Required monthly installment" },
      },
    },
  ],
};

export const postBureauAffordabilityWidthsById = new Map<string, number[]>([
  ["_1E880009-77B2-4309-AE2A-8964E05636B1", [180, 300]],
  ["_FAD12087-C2B6-4060-840B-077972DCCB80", [300]],
  ["_79F0E706-6018-49AB-86EB-78AC55582CE7", [300]],
  ["_A541AA7D-2DE8-4E04-B5C9-0320EE7D26BE", [300]],
  ["_9C3C6ED5-F875-4C5B-A7C1-76D10DC2E5DF", [300]],
  ["_656110E9-A234-46A4-B4CF-7FD691F8F565", [300]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: postBureauAffordabilityExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: postBureauAffordabilityWidthsById,
  },
};
