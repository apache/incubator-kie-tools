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
import {
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  InvocationExpressionDefinition,
} from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper, beeGwtService, pmmlParams } from "../../../../boxedExpressionStoriesWrapper";
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
  id: "_1E880009-77B2-4309-AE2A-8964E05636B1",
  name: "Post-bureau affordability",
  dataType: DmnBuiltInDataType.Boolean,
  logicType: ExpressionDefinitionLogicType.Invocation,
  invokedFunction: { id: "_0B151359-63F6-4DAB-BC88-53F34446F9C7", name: "f.Affordability calculation" },
  bindingEntries: [
    {
      entryInfo: {
        id: "_2882DEEF-03E9-431B-9E96-0B9E06DBDBFA",
        name: "Monthly Income",
        dataType: DmnBuiltInDataType.Number,
      },
      entryExpression: {
        id: "_FAD12087-C2B6-4060-840B-077972DCCB80",
        name: "Monthly Income",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Applicant data.Monthly.Income",
        width: 300,
      },
    },
    {
      entryInfo: {
        id: "_ECCC661F-9489-494A-81A9-CADD18075235",
        name: "Monthly Repayments",
        dataType: DmnBuiltInDataType.Number,
      },
      entryExpression: {
        id: "_79F0E706-6018-49AB-86EB-78AC55582CE7",
        name: "Monthly Repayments",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Applicant data.Monthly.Repayments",
        width: 300,
      },
    },
    {
      entryInfo: {
        id: "_50CB1671-8A51-40A2-B86C-5A59A76ADA99",
        name: "Monthly Expenses",
        dataType: DmnBuiltInDataType.Number,
      },
      entryExpression: {
        id: "_A541AA7D-2DE8-4E04-B5C9-0320EE7D26BE",
        name: "Monthly Expenses",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Applicant data.Monthly.Expenses",
        width: 300,
      },
    },
    {
      entryInfo: {
        id: "_1B4A9572-B67B-4D52-94B0-A990B3114810",
        name: "Risk Category",
        dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
      },
      entryExpression: {
        id: "_9C3C6ED5-F875-4C5B-A7C1-76D10DC2E5DF",
        name: "Risk Category",
        dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Post-bureau risk category",
        width: 300,
      },
    },
    {
      entryInfo: {
        id: "_6C831C67-F80C-44FC-B4B6-55FD4BAE1A79",
        name: "Required Monthly Installment",
        dataType: DmnBuiltInDataType.Number,
      },
      entryExpression: {
        id: "_656110E9-A234-46A4-B4CF-7FD691F8F565",
        name: "Required Monthly Installment",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Required monthly installment",
        width: 300,
      },
    },
  ],
  entryInfoWidth: 180,
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: postBureauAffordabilityExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
