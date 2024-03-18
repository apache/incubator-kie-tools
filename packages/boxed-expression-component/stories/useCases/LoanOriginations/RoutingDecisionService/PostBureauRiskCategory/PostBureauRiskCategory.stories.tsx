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
  title: "Use cases/Loan Originations/Routing Decision Service/Post Bureau Risk Category",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const postBureauRiskCategoryExpression: ContextExpressionDefinition = {
  __$$element: "context",
  "@_id": "_55A050DB-250D-473E-A7DF-43C49CE87694",
  "@_label": "Post-bureau risk category",
  "@_typeRef": "t.BureauRiskCategory",
  contextEntry: [
    {
      variable: {
        "@_id": "_79173F55-FFE4-4FE7-9620-A4CAC4CCD084",
        "@_name": "Existing Customer",
        "@_typeRef": DmnBuiltInDataType.Boolean,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_BD22ABA2-C1AE-413A-A74D-4175F7184066",
        "@_label": "Existing Customer",
        "@_typeRef": DmnBuiltInDataType.Boolean,
        text: { __$$text: "Applicant data.ExistingCustomer" },
      },
    },
    {
      variable: {
        "@_id": "_538E6427-680B-4307-A234-D1C6C60D5D75",
        "@_name": "Credit Score",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        __$$element: "literalExpression",
        "@_id": "_34BA1DC9-9043-41AB-AA05-323916F38A4C",
        "@_label": "Credit Score",
        "@_typeRef": DmnBuiltInDataType.Number,
        text: { __$$text: "Bureau data.CreditScore" },
      },
    },
    {
      expression: {
        __$$element: "decisionTable",
        "@_id": "_E26346D5-7A7C-44A2-8EF2-D79CECB257FF",
        "@_label": "Result Expression",
        "@_typeRef": "UNIQUE",
        input: [
          {
            "@_id": "_1906CFD2-703A-4E1E-A352-51F2445FD057",
            "@_label": "Existing Customer",
            inputExpression: {
              "@_typeRef": DmnBuiltInDataType.Boolean,
              "@_id": "_19E2A18A-57C5-473F-BCA9-79D1D4E4D9CE",
            },
          },
          {
            "@_id": "_EDC6FAAA-A509-41CF-9CE6-9B263C9297A9",
            "@_label": "Application risk score",
            inputExpression: {
              "@_typeRef": DmnBuiltInDataType.Number,
              "@_id": "_9A98108B-7397-446B-AD37-CB00C2914548",
            },
          },
          {
            "@_id": "_F2749D32-7BCD-4AC1-89CF-3FBF63BB3542",
            "@_label": "Credit Score",
            inputExpression: {
              "@_typeRef": DmnBuiltInDataType.Number,
              "@_id": "_CF09CF88-A176-471F-ACD8-346E8444B836",
            },
          },
        ],
        output: [
          {
            "@_id": "_FF94F953-C3C2-42E3-A0C9-A2669A882745",
            "@_name": "Result Expression",
          },
        ],
        rule: [
          {
            "@_id": "_B3B7B02B-AFDC-4B5E-882C-1126140F6261",
            inputEntry: [
              { "@_id": "_6EE6E2B4-25C0-4576-BE14-C5FFD5FF35F1", text: { __$$text: "false" } },
              { "@_id": "_71C5F6CF-C80E-44E1-891D-27C71F7FA31A", text: { __$$text: "<120" } },
              { "@_id": "_6176635B-7642-4EF0-9489-BAFC932D8A2F", text: { __$$text: "<590" } },
            ],
            outputEntry: [{ "@_id": "_41276E26-9C54-4582-92D1-DB28B48B6D42", text: { __$$text: '"High"' } }],
          },
          {
            "@_id": "_67AEC34B-3ED8-44B9-8601-ACC7CB4BA653",
            inputEntry: [
              { "@_id": "_1BE6DE9A-C5A4-45C6-BBC6-DC435B2E7C2F", text: { __$$text: "false" } },
              { "@_id": "_5653730A-89BA-4936-B4C4-15E524C2818C", text: { __$$text: "<120" } },
              {
                "@_id": "_E601DCF5-3717-4EA9-B12F-C56452384BF8",
                text: { __$$text: "[590..610]" },
              },
            ],
            outputEntry: [{ "@_id": "_FF5C09F7-2BC9-4A1A-8463-3802FFAECAFA", text: { __$$text: '"Medium"' } }],
          },
          {
            "@_id": "_47659E3A-2BE4-4D8F-94B2-C92ABBA4062F",
            inputEntry: [
              { "@_id": "_FB886F49-1AE9-4548-8B83-73F3DCFB205A", text: { __$$text: "false" } },
              { "@_id": "_D4F4AE8D-FE43-4CD9-BAC2-994E19C9E70E", text: { __$$text: "<120" } },
              { "@_id": "_15D10670-5BA5-4F95-8D83-A47B5A656960", text: { __$$text: ">610" } },
            ],
            outputEntry: [{ "@_id": "_7540490A-B202-42EE-BABF-BA9A25C26D48", text: { __$$text: '"Low"' } }],
          },
          {
            "@_id": "_2EEC946B-D748-4D00-AF60-0CFAC61321AB",
            inputEntry: [
              { "@_id": "_1F41E4F4-B5B7-46F0-9C0A-7BFC623DCD3E", text: { __$$text: "false" } },
              {
                "@_id": "_187EA6CD-D0AB-4231-A3D3-84C01A9AF001",
                text: { __$$text: "[120..130]" },
              },
              { "@_id": "_48C1888F-52ED-4D41-9CF5-1F66763BA3AF", text: { __$$text: "<600" } },
            ],
            outputEntry: [{ "@_id": "_04F885D9-C264-4064-994F-A133CDCE394B", text: { __$$text: '"High"' } }],
          },
          {
            "@_id": "_56E54983-AC45-4767-B642-2799CCE6D50F",
            inputEntry: [
              { "@_id": "_858B276A-2BDD-4763-80C3-F206A20905B6", text: { __$$text: "false" } },
              {
                "@_id": "_B663B385-41E6-4657-A7FD-862D8933285E",
                text: { __$$text: "[120..130]" },
              },
              {
                "@_id": "_C5300D02-A83A-4C99-8138-A42F018493AD",
                text: { __$$text: "[600..625]" },
              },
            ],
            outputEntry: [{ "@_id": "_78CDAC29-3A04-4E2A-BEC9-773F0DF8FD0F", text: { __$$text: '"Medium"' } }],
          },
          {
            "@_id": "_FE0CB2A9-7ED4-4A70-B49D-550BE920CA12",
            inputEntry: [
              { "@_id": "_A027F2CF-5383-429E-A055-12F542ADF49D", text: { __$$text: "false" } },
              {
                "@_id": "_D451BA0E-67B7-4027-91B0-98059CD0A0A1",
                text: { __$$text: "[120..130]" },
              },
              { "@_id": "_32C86336-8704-457F-BF70-40AAB6812F8A", text: { __$$text: ">625" } },
            ],
            outputEntry: [{ "@_id": "_687BF7E8-EE6B-4D16-BF17-241320165DEC", text: { __$$text: '"Low"' } }],
          },
          {
            "@_id": "_82A7FE01-348D-44EC-936F-83C43221FB02",
            inputEntry: [
              { "@_id": "_A4C9DEF7-B188-4844-A7E5-B5E9C4FC1794", text: { __$$text: "false" } },
              { "@_id": "_02787450-EA48-4D98-9FEB-A756905704B6", text: { __$$text: ">130" } },
              { "@_id": "_2B73F7F0-9AE8-4F17-A4BF-F6D43E0B0FB8", text: { __$$text: "-" } },
            ],
            outputEntry: [
              {
                "@_id": "_A7CBAD9A-9B75-4867-A45E-226C70BFD66A",
                text: { __$$text: '"Very Low"' },
              },
            ],
          },
          {
            "@_id": "_28AF629C-807F-4CBE-A025-9A009CC274E3",
            inputEntry: [
              { "@_id": "_6F690146-833C-4C0A-90EF-168DC32BBFE3", text: { __$$text: "true" } },
              { "@_id": "_D0EB6833-B4C7-446A-B22A-7C87AFBCF473", text: { __$$text: "<=100" } },
              { "@_id": "_08F4C0B8-B319-4C4D-9BC7-657ED95DBC41", text: { __$$text: "<580" } },
            ],
            outputEntry: [{ "@_id": "_23511F7E-7B18-43A4-9900-A8ACDB1AE9BD", text: { __$$text: '"High"' } }],
          },
          {
            "@_id": "_0E545FB9-ADF9-410D-BD97-4CC1AB840D1C",
            inputEntry: [
              { "@_id": "_BAAC92F9-4FC7-42AF-88DD-143E44637DB6", text: { __$$text: "true" } },
              { "@_id": "_525E7BE8-2BBE-4B9C-A934-DC7066634AF8", text: { __$$text: "<=100" } },
              {
                "@_id": "_7A912E27-B0E2-404E-BDCD-4B4BB0AB5CA0",
                text: { __$$text: "[580..600]" },
              },
            ],
            outputEntry: [{ "@_id": "_F826875B-E18C-4763-A0D1-3C37448B438E", text: { __$$text: '"Medium"' } }],
          },
          {
            "@_id": "_A7C61E08-3F8B-4D49-B747-C5D30566F429",
            inputEntry: [
              { "@_id": "_B0D56E8A-D671-4AA9-8BF6-659205EDD56D", text: { __$$text: "true" } },
              { "@_id": "_068ABD8A-37D4-4A16-AD90-2E675B95A2B9", text: { __$$text: "<=100" } },
              { "@_id": "_B028DFA7-FF93-402E-9588-4D1BA4F4A5F9", text: { __$$text: ">600" } },
            ],
            outputEntry: [{ "@_id": "_1501225E-003D-4E01-88AA-34CB6DDACEFB", text: { __$$text: '"Low"' } }],
          },
          {
            "@_id": "_0301CF79-EF2E-4255-8A97-636823083143",
            inputEntry: [
              { "@_id": "_6EECCA9B-949D-4FB2-A08C-4F63E0BD3A6D", text: { __$$text: "true" } },
              { "@_id": "_F262D841-EEB0-4AAE-9481-0CB13FBD6E2D", text: { __$$text: ">100" } },
              { "@_id": "_FFFE87D4-60E8-48FA-9A48-4E5EBA034E2E", text: { __$$text: "<590" } },
            ],
            outputEntry: [{ "@_id": "_595B441E-1B28-484A-95D6-E2EC82D9032D", text: { __$$text: '"High"' } }],
          },
          {
            "@_id": "_50016EFD-1885-47A1-BEE6-62B021DAB2A5",
            inputEntry: [
              { "@_id": "_90E71E73-8FB2-41BA-907A-FC1024E831DB", text: { __$$text: "true" } },
              { "@_id": "_76CC83EC-B78D-4ED0-AD19-CD429C40B00A", text: { __$$text: ">100" } },
              {
                "@_id": "_EC44C8A1-12D5-4577-8183-81430CD0C847",
                text: { __$$text: "[590..615]" },
              },
            ],
            outputEntry: [{ "@_id": "_FA9AB6DE-2A6C-48B6-AD77-607DB78B69E2", text: { __$$text: '"Medium"' } }],
          },
          {
            "@_id": "_93A0E9B6-E42B-443F-937E-9EF9D2A070F5",
            inputEntry: [
              { "@_id": "_9C795343-756D-4086-AA3C-C684A9D6D5C5", text: { __$$text: "true" } },
              { "@_id": "_42153785-6979-4096-9DE1-1D0C9EA307B3", text: { __$$text: ">100" } },
              { "@_id": "_F56F3328-8E74-424B-ABB3-F0CA072AB0BB", text: { __$$text: ">615" } },
            ],
            outputEntry: [{ "@_id": "_E455C193-E689-46AD-A263-CAE9DFA9B731", text: { __$$text: '"Low"' } }],
          },
        ],
      },
    },
  ],
};

export const postBureauRiskCategoryWidthsById = new Map<string, number[]>([
  ["_55A050DB-250D-473E-A7DF-43C49CE87694", [154, 695]],
  ["_BD22ABA2-C1AE-413A-A74D-4175F7184066", [695]],
  ["_34BA1DC9-9043-41AB-AA05-323916F38A4C", [695]],
  ["_DD3AABF1-9E83-4F71-A769-D9CC01231580", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 123, 154, 100, 148, 100]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: postBureauRiskCategoryExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: postBureauRiskCategoryWidthsById,
  },
};
