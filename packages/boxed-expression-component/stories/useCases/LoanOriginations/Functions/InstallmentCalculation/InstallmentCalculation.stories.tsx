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
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
} from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper, beeGwtService, pmmlParams } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Functions/Installment Calculation",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const installmentCalculationExpression: FunctionExpressionDefinition = {
  id: "_1E31E836-0609-4B4C-8FAF-389F774B1FE3",
  name: "Installment calculation",
  dataType: DmnBuiltInDataType.Number,
  logicType: ExpressionDefinitionLogicType.Function,
  formalParameters: [
    {
      id: "_18DFB02E-9A87-44A2-AE57-997D6FE092B6",
      name: "Product Type",
      dataType: "t.ProductType" as DmnBuiltInDataType,
    },
    { id: "_907026CD-42C1-4731-B0B3-CC65A00180B1", name: "Rate", dataType: DmnBuiltInDataType.Number },
    { id: "_BF350457-ED49-42CD-A087-7E6CBCB0B0E0", name: "Term", dataType: DmnBuiltInDataType.Number },
    { id: "_F5D2470B-A1F2-4E40-98D7-DBCD286149FB", name: "Amount", dataType: DmnBuiltInDataType.Number },
  ],
  functionKind: FunctionExpressionDefinitionKind.Feel,
  expression: {
    id: "_7A96C527-9D9E-4199-987B-A6A0C8308296",
    name: "Feel Expression",
    dataType: DmnBuiltInDataType.Undefined,
    logicType: ExpressionDefinitionLogicType.Context,
    contextEntries: [
      {
        entryInfo: {
          id: "_4AAFD507-11D2-4C6A-82E0-CBEEFA26FCE9",
          name: "Monthly Fee",
          dataType: DmnBuiltInDataType.Number,
        },
        entryExpression: {
          id: "_6B635731-73ED-41B8-9D3C-72F174D68036",
          name: "Monthly Fee",
          dataType: DmnBuiltInDataType.Number,
          logicType: ExpressionDefinitionLogicType.Literal,
          content:
            'if Product Type = "Standard Loan"\nthen 20.00\nelse if Product Type = "Special Loan"\nthen 25.00\nelse null',
          width: 320,
        },
      },
      {
        entryInfo: {
          id: "_1852E1E0-A49C-49D8-BB83-18D395672ECB",
          name: "Monthly Repayments",
          dataType: DmnBuiltInDataType.Number,
        },
        entryExpression: {
          id: "_68539BE5-8AA0-4C5B-8AE6-D4A8F1118B7B",
          name: "Monthly Repayments",
          dataType: DmnBuiltInDataType.Number,
          logicType: ExpressionDefinitionLogicType.Literal,
          content: "(Amount*Rate/12)/(1-(1+Rate/12)**-Term)",
          width: 320,
        },
      },
    ],
    result: {
      id: "_94444797-708D-418A-A22A-5CE6CAB35F6F",
      name: "Result Expression",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Literal,
      content: "Monthly Fee + Monthly Repayments",
      width: 320,
    },
    entryInfoWidth: 120,
  },
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: installmentCalculationExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
