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

import { DmnBuiltInDataType, InvocationExpressionDefinition } from "../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { beeGwtService, BoxedExpressionEditorWrapper, pmmlDocuments } from "../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../src/expressions";
import { loanOriginationsDataTypes } from "../boxedExpressionEditorBase";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Required monthly installment",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const requiredMonthlyInstallmentExpression: InvocationExpressionDefinition = {
  __$$element: "invocation",
  "@_id": "_EF51A747-D5E1-414E-9DD5-964362FB2AEC",
  "@_label": "Required monthly installment",
  "@_typeRef": DmnBuiltInDataType.Number,
  expression: {
    __$$element: "literalExpression",
    "@_id": "_99D8F2CD-01CF-499B-9D51-410C59865F15",
    text: { __$$text: "f.Installment calculation" },
  },
  binding: [
    {
      parameter: {
        "@_id": "_189F1672-7BED-47F9-884F-6A1FB31DFE34",
        "@_name": "Product Type",
        "@_typeRef": "t.ProductType",
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_EE98C97E-4CFE-463D-A500-B8D6D7FC69BD",
        "@_label": "Product Type",
        "@_typeRef": "t.ProductType",
        text: { __$$text: "Requested product.ProductType" },
      },
    },
    {
      parameter: {
        "@_id": "_38FC5DFF-390B-42FE-A29D-35EF44EC6BB8",
        "@_name": "Rate",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_25149F92-AA1B-45F0-92F6-8B478803E61C",
        "@_label": "Rate",
        "@_typeRef": DmnBuiltInDataType.Number,
        text: { __$$text: "Requested product.Rate" },
      },
    },
    {
      parameter: {
        "@_id": "_3C364710-888A-455C-B70C-6DE20914A6F4",
        "@_name": "Term",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_A245D6C9-1A2D-48E1-B001-B7CA9318A82F",
        "@_label": "Term",
        "@_typeRef": DmnBuiltInDataType.Number,
        text: { __$$text: "Requested product.Term" },
      },
    },
    {
      parameter: {
        "@_id": "_DCCB50E0-CB3D-483D-9D13-3BD221A236E3",
        "@_name": "Amount",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_CCBA629B-04C7-4B45-9FBF-AF9AED2FE10D",
        "@_label": "Amount",
        "@_typeRef": DmnBuiltInDataType.Number,

        text: { __$$text: "Requested product.Amount" },
      },
    },
  ],
};

export const requiredMonthlyInstallmentWidthsById = new Map<string, number[]>([
  ["_EF51A747-D5E1-414E-9DD5-964362FB2AEC", [120]],
  ["_EE98C97E-4CFE-463D-A500-B8D6D7FC69BD", [250]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: requiredMonthlyInstallmentExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: requiredMonthlyInstallmentWidthsById,
  },
};
