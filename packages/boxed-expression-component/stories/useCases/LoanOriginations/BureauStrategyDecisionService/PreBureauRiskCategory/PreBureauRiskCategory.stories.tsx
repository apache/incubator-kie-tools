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

import { ContextExpressionDefinition, DmnBuiltInDataType } from "../../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { beeGwtService, BoxedExpressionEditorWrapper, pmmlDocuments } from "../../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../../src/expressions";
import { loanOriginationsDataTypes } from "../../boxedExpressionEditorBase";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Bureau Strategy Decision Service/Pre Bureau Risk Category",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const preBureauRiskCategoryExpression: ContextExpressionDefinition = {
  __$$element: "context",
  "@_id": "_21BA0CB9-F15E-482F-BCBB-F6694EF9B1FC",
  "@_label": "Pre-bureau risk category",
  "@_typeRef": "t.BureauRiskCategory",
  contextEntry: [
    {
      variable: {
        "@_id": "_E0D6DEC1-A390-4AA7-86A0-42CFCE32C41E",
        "@_name": "Existing Customer",
        "@_typeRef": DmnBuiltInDataType.Boolean,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_9D39AD1B-4DC7-4B5F-A605-9F04A4C761DA",
        "@_label": "Existing Customer",
        "@_typeRef": DmnBuiltInDataType.Boolean,
        text: { __$$text: "Applicant data.ExistingCustomer" },
      },
    },
    {
      expression: {
        __$$element: "decisionTable",
        "@_id": "_DD3AABF1-9E83-4F71-A769-D9CC01231580",
        "@_label": "Result Expression",
        "@_hitPolicy": "UNIQUE",
        annotation: [{ "@_name": "annotation-1" }],
        input: [
          {
            "@_id": "_6887F8AD-5387-4D00-8CED-5234EB2C4EEB",
            inputExpression: {
              "@_typeRef": DmnBuiltInDataType.Boolean,
              "@_id": "_19E2A18A-57C5-473F-BCA9-79D1D4E4D9CE",
              text: { __$$text: "Existing Customer" },
            },
          },
          {
            "@_id": "_D998C69B-7B36-4ECF-907B-D29B352B8D2F",
            inputExpression: {
              "@_typeRef": DmnBuiltInDataType.Number,
              "@_id": "_9A98108B-7397-446B-AD37-CB00C2914548",
              text: { __$$text: "Application risk score" },
            },
          },
        ],
        output: [
          {
            "@_id": "_9C8DC883-11FB-451F-861F-EC795AE0F22E",
            "@_label": "Result Expression",
          },
        ],
        rule: [
          {
            "@_id": "_1F4DBB8E-192C-4D56-B893-8DAD237B9AA1",
            inputEntry: [
              { "@_id": "_1110BF2B-1C84-4636-AAD9-F44D8A0A0894", text: { __$$text: "false" } },
              { "@_id": "_A59583B8-E83D-44CE-8B71-5C29C397CF4E", text: { __$$text: "<100" } },
            ],
            outputEntry: [{ "@_id": "_6A15E6CD-2A38-4517-A82C-CDF504E68E29", text: { __$$text: '"High"' } }],
            annotationEntry: [{ text: { __$$text: "" } }],
          },
          {
            "@_id": "_EFBD32EA-8A08-4645-927D-EBB784D67910",
            inputEntry: [
              { "@_id": "_AACE06DF-0F36-4EF9-8126-B8E16A2699F6", text: { __$$text: "false" } },
              {
                "@_id": "_BDED674F-0165-4DA4-9881-E12DADB7B19C",
                text: { __$$text: "[100..120)" },
              },
            ],
            outputEntry: [
              {
                "@_id": "_9A9B2A8A-0E51-4093-BF26-038B24F3ED38",
                text: { __$$text: '"Medium"' },
              },
            ],
            annotationEntry: [{ text: { __$$text: "" } }],
          },
          {
            "@_id": "_F2F42947-F671-4230-A462-6BB6D4442D11",
            inputEntry: [
              { "@_id": "_2C8BF827-0BAA-4C6C-B916-B938466F11E1", text: { __$$text: "false" } },
              {
                "@_id": "_8B65BDC4-9D04-4E1F-BE72-FFD91DD3EC67",
                text: { __$$text: "[120..130]" },
              },
            ],
            outputEntry: [{ "@_id": "_18D2FB23-AD7C-41CA-9EB6-D854BE331FE2", text: { __$$text: '"Low"' } }],
            annotationEntry: [{ text: { __$$text: "" } }],
          },
          {
            "@_id": "_F65C4811-3179-430C-8733-21FDBF418B99",
            inputEntry: [
              { "@_id": "_418478CE-A3D3-4F7C-8C9A-ACFCC335BC76", text: { __$$text: "false" } },
              { "@_id": "_5DE0CC48-75F8-4BD2-A402-CB44C503516E", text: { __$$text: ">130" } },
            ],
            outputEntry: [
              {
                "@_id": "_A60598EA-1DA9-443A-9552-C42A5068EEF4",
                text: { __$$text: '"Very Low"' },
              },
            ],
            annotationEntry: [{ text: { __$$text: "" } }],
          },
          {
            "@_id": "_CDACC55A-3AFE-4A03-81C0-053D23DAE724",
            inputEntry: [
              { "@_id": "_39A8EF67-15A4-4817-BE13-A9B9E2E01734", text: { __$$text: "true" } },
              { "@_id": "_6BBF7CDC-5ECC-44F2-98BB-C9FD51DB375C", text: { __$$text: "<80" } },
            ],
            outputEntry: [
              {
                "@_id": "_19C95F01-7781-4F9A-8A1B-8C441EEB9265",
                text: { __$$text: '"Decline"' },
              },
            ],
            annotationEntry: [{ text: { __$$text: "" } }],
          },
          {
            "@_id": "_2131DA82-B7B0-454E-9176-4E529E7A2B48",
            inputEntry: [
              { "@_id": "_01465B90-C329-45BB-B3D7-AAC159957788", text: { __$$text: "true" } },
              { "@_id": "_A933C989-DF1C-4635-8A07-3A7459F76698", text: { __$$text: "[80..90)" } },
            ],
            outputEntry: [{ "@_id": "_8B0A30E9-9627-48B4-82CA-7B549F27D393", text: { __$$text: '"High"' } }],
            annotationEntry: [{ text: { __$$text: "" } }],
          },
          {
            "@_id": "_7A91A43D-7987-4C93-869C-5EA96E3ED201",
            inputEntry: [
              { "@_id": "_FA35EBAF-DDDB-4B29-8ADC-BA431DCA3164", text: { __$$text: "true" } },
              {
                "@_id": "_C4653903-A560-4B79-83DD-98E0656AAB12",
                text: { __$$text: "[90..110]" },
              },
            ],
            outputEntry: [
              {
                "@_id": "_4F267262-0CB6-48B5-8A8F-4DFA8808E6C6",
                text: { __$$text: '"Medium"' },
              },
            ],
            annotationEntry: [{ text: { __$$text: "" } }],
          },
          {
            "@_id": "_CDEDBBBE-05B7-4070-B479-1391ADC9C6E6",
            inputEntry: [
              { "@_id": "_4FD8C67F-0AAE-47D5-917B-FB9B4812DC3E", text: { __$$text: "true" } },
              { "@_id": "_195C7AC3-9F87-41EA-9291-9E52E2CD84F6", text: { __$$text: ">110" } },
            ],
            outputEntry: [{ "@_id": "_67BF6667-022D-4852-A47F-4D40AEED4009", text: { __$$text: '"Low"' } }],
            annotationEntry: [{ text: { __$$text: "" } }],
          },
        ],
      },
    },
  ],
};

export const preBureauRiskCategoryWidthsById = new Map<string, number[]>([
  ["_21BA0CB9-F15E-482F-BCBB-F6694EF9B1FC", [154, 570]],
  ["_9D39AD1B-4DC7-4B5F-A605-9F04A4C761DA", [570]],
  ["_DD3AABF1-9E83-4F71-A769-D9CC01231580", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 123, 154, 123, 100]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: preBureauRiskCategoryExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: preBureauRiskCategoryWidthsById,
  },
};
