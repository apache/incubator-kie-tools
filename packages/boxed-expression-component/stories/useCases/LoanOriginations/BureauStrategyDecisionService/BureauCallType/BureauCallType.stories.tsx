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
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
} from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper, beeGwtService, pmmlParams } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Bureau Strategy Decision Service/Bureau call type",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const bureauCallTypeExpression: DecisionTableExpressionDefinition = {
  id: "_7FB4A019-EC04-4153-86C1-C90A8BA8E6C3",
  name: "Bureau call type",
  dataType: "t.BureauCallType" as DmnBuiltInDataType,
  logicType: ExpressionDefinitionLogicType.DecisionTable,
  hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
  aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
  annotations: [{ name: "annotation-1", width: 145 }],
  input: [
    {
      id: "_EDEB6C03-3FD1-4845-A18F-1463314BB5FB",
      name: "Pre-bureau risk category",
      dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
      width: 210,
      idLiteralExpression: "_72DD65BD-56CA-4429-B5FD-2EBDEEE01B9B",
    },
  ],
  output: [
    {
      id: "_BC216B66-6B93-47B7-8F37-8B03D392D653",
      name: "Bureau call type",
      dataType: "t.BureauCallType" as DmnBuiltInDataType,
      width: 175,
    },
  ],
  rules: [
    {
      id: "_BCF175DE-81A0-428C-BC28-4D12605ACBE8",
      inputEntries: [
        {
          id: "_C3306F98-0B9A-482C-8951-72F58C101FE3",
          content: '"High", "Medium"',
        },
      ],
      outputEntries: [{ id: "_5D1A4638-1023-44B2-9FD3-47862126935F", content: '"Full"' }],
      annotationEntries: [""],
    },
    {
      id: "_B1C67777-2868-471F-98DE-C634BC3FBD90",
      inputEntries: [{ id: "_53EEAFDA-1F59-49A6-A347-9F37F8FCD9E4", content: '"Low"' }],
      outputEntries: [{ id: "_F22833EE-A811-428B-979B-EE1F2E7FAF96", content: '"Mini"' }],
      annotationEntries: [""],
    },
    {
      id: "_E7F703F9-08E3-4B35-AFF5-F6F5F2AE5640",
      inputEntries: [
        {
          id: "_9E274DD9-73C1-4D06-B97A-C0112D33AB8A",
          content: '"Very Low", "Decline"',
        },
      ],
      outputEntries: [{ id: "_AB3E865D-1AB4-4E49-9B5B-BD328190BBA8", content: '"None"' }],
      annotationEntries: [""],
    },
  ],
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: bureauCallTypeExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
