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

import { loanOriginationsDataTypes } from "../boxedExpressionEditorBase";
import { ContextExpressionDefinition, DmnBuiltInDataType } from "../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { beeGwtService, BoxedExpressionEditorWrapper, pmmlDocuments } from "../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../src/expressions";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Application Risk Score",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const applicationRiskScoreExpression: ContextExpressionDefinition = {
  __$$element: "context",
  "@_id": "_36398C55-5ED1-41C6-B643-98DBDD52D143",
  "@_label": "Application risk score",
  "@_typeRef": DmnBuiltInDataType.Number,
  contextEntry: [
    {
      variable: {
        "@_id": "_BB4F357E-80D8-4E5A-955F-387B5DBB2EC5",
        "@_name": "Age",
        "@_typeRef": DmnBuiltInDataType.Number,
      },
      expression: {
        "@_id": "_45DE91F3-7731-4A5C-AC8B-0C6AD17508DF",
        "@_label": "Age",
        "@_typeRef": DmnBuiltInDataType.Number,
        __$$element: "literalExpression",
        text: { __$$text: "Applicant data.Age" },
      },
    },
    {
      variable: {
        "@_id": "_A3E35C5D-A643-40AA-A490-772105D00738",
        "@_name": "Marital Status",
        "@_typeRef": "t.MaritalStatus",
      },
      expression: {
        "@_id": "_F3354465-FA19-4664-A0A1-355511122FBE",
        "@_label": "Marital Status",
        "@_typeRef": "t.MaritalStatus",
        __$$element: "literalExpression",

        text: { __$$text: "Applicant data.MaritalStatus" },
      },
    },
    {
      variable: {
        "@_id": "_4509453F-BB4B-46F2-915C-FA90D90A4DFB",
        "@_name": "Employment Status",
        "@_typeRef": "t.EmploymentStatus",
      },
      expression: {
        "@_id": "_8BDD13D2-4F23-4A76-9A5F-DD9164B1B21A",
        "@_label": "Employment Status",
        "@_typeRef": "t.EmploymentStatus",
        __$$element: "literalExpression",
        text: { __$$text: "Applicant data.EmploymentStatus" },
      },
    },

    {
      expression: {
        __$$element: "decisionTable",
        "@_id": "_40CCF542-F80F-4C14-AAE5-AAAFA3304648",
        "@_label": "Result Expression",
        "@_hitPolicy": "COLLECT",
        "@_aggregation": "SUM",
        input: [
          {
            "@_id": "_BD17099C-29FB-4252-B0DC-C6B93BF5B832",
            "@_label": "Age",
            inputExpression: {
              "@_typeRef": DmnBuiltInDataType.Number,
              "@_id": "_BE1DBAB9-BE82-4F8F-8E16-6227731C9E65",
              text: { __$$text: "Age" },
            },
          },
          {
            "@_id": "_D6A329C9-D92E-43AB-9449-827C83697F59",
            "@_label": "Employment Status",
            inputExpression: {
              "@_typeRef": "t.EmploymentStatus",
              "@_id": "_E7F1CC56-2891-48BF-92A1-41414FC14994",
              text: { __$$text: "Employment Status" },
            },
          },
          {
            "@_id": "_34A3B4B2-988F-49CF-9729-50B97B11B3AB",
            "@_label": "Marital Status",

            inputExpression: {
              "@_typeRef": "t.MaritalStatus",
              "@_id": "_4A1DB037-D38D-45D1-A8E8-CB17896811B2",
              text: { __$$text: "Marital Status" },
            },
          },
        ],
        output: [
          {
            "@_id": "_813B3D23-EA80-4D61-B197-150B9C51FAD8",
            "@_name": "Result Expression",
          },
        ],
        annotation: [{ "@_name": "annotation-1" }],
        rule: [
          {
            "@_id": "_EB2B5C94-DCAB-4020-A78E-BCB9E6B6494D",
            inputEntry: [
              { "@_id": "_20D11878-D7F2-43F5-90F5-C149BF243325", text: { __$$text: "[18..22)" } },
              { "@_id": "_FA84643B-4E41-4833-BA8B-039699D98094", text: { __$$text: "-" } },
              { "@_id": "_A6265958-B697-4B0D-BC55-7435AC2255B4", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_E6BA0A4A-376F-4D5B-922D-07ED1B16F561", text: { __$$text: "32" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_D42C5ACB-95A7-4995-8C0D-926F5989A8CA",
            inputEntry: [
              { "@_id": "_3763755B-DE16-420D-BD88-E6FB242D7B98", text: { __$$text: "[22..26)" } },
              { "@_id": "_976F8FDB-525B-45BB-855B-E19E4B3B3736", text: { __$$text: "-" } },
              { "@_id": "_89EE9CE1-FB90-4266-9EE9-E2A956A4FD16", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_67313A9A-7C28-42A9-B513-F269F3357E93", text: { __$$text: "35" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_C90EF514-B92E-47D2-B41C-C0445F9BB708",
            inputEntry: [
              { "@_id": "_A9813478-558D-43CC-B03E-46E8991D60F0", text: { __$$text: "[26..36)" } },
              { "@_id": "_6DF013B0-8B97-4561-9004-AE96EC3261AE", text: { __$$text: "-" } },
              { "@_id": "_F0A0DE3D-0435-493B-A8FA-1763F8D97352", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_D2598910-8427-4DBC-97A9-E81D64ACBDA9", text: { __$$text: "40" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_A6F1BFC3-1C0F-4416-95A0-7F7B12805048",
            inputEntry: [
              { "@_id": "_94226ADB-5B90-4165-8458-F0C78DA35F9A", text: { __$$text: "[36..50)" } },
              { "@_id": "_267986B1-6716-433D-A827-791BCE46591D", text: { __$$text: "-" } },
              { "@_id": "_8357A853-1754-47D7-B10A-18E9A0CA4A27", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_5C1A37FD-B22A-446D-BC47-0E67B4671E2C", text: { __$$text: "43" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_0597BEB7-17F4-472F-B69C-3E6807C74FDD",
            inputEntry: [
              { "@_id": "_F0EF3ED4-D702-49D2-BBD3-069B9D979799", text: { __$$text: ">=50" } },
              { "@_id": "_2CD88F2E-1566-49B6-A097-46E9555FA8EC", text: { __$$text: "-" } },
              { "@_id": "_30FB6674-CDFD-4610-98AF-58B5F06DCB0A", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_569D30AA-AFFF-49F3-B602-FDE601C2F617", text: { __$$text: "48" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_7D674FE0-672E-4603-9546-C7DC9B2B4AF7",
            inputEntry: [
              { "@_id": "_05170CC3-F815-4AE3-B906-5855D324E857", text: { __$$text: "-" } },
              { "@_id": "_E2722AF5-7D48-41E3-AC07-7C7DFA1CB5ED", text: { __$$text: "-" } },
              { "@_id": "_94363FC7-6731-4860-96A5-B4FD35609F81", text: { __$$text: '"S"' } },
            ],
            outputEntry: [{ "@_id": "_0ECF5E3F-0FDA-498C-9B98-C789DD70E0C9", text: { __$$text: "25" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_773EF50F-4C43-4D37-89A2-81E4246B906A",
            inputEntry: [
              { "@_id": "_D6FF8B88-73E2-41CF-A4E0-101F8FD73ABA", text: { __$$text: "-" } },
              { "@_id": "_2754AEEA-D04D-416B-97BA-ABD9B4F303EC", text: { __$$text: "-" } },
              { "@_id": "_604CD0C4-4E51-4DF4-B464-E731DDCD3340", text: { __$$text: '"M"' } },
            ],
            outputEntry: [{ "@_id": "_E3CA53E0-131D-419A-9E61-35B5666D2ACB", text: { __$$text: "45" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_DA9E4278-8339-4FF4-A068-0190FE25E267",
            inputEntry: [
              { "@_id": "_BE15E162-C9A9-4B3D-BA5B-2886970303FB", text: { __$$text: "-" } },
              {
                "@_id": "_1A4773D1-FDBC-4DB6-B930-A56059872200",
                text: { __$$text: '"Unemployed"' },
              },
              { "@_id": "_C0CDCFF4-F73B-4C5E-8942-F6BB3645948A", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_FDFA6746-4D5F-4CA9-B4CD-36E6E8186D80", text: { __$$text: "15" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_D997BF3C-F595-4143-81FC-81B9A4E64BAA",
            inputEntry: [
              { "@_id": "_3781127D-49B5-4D58-941D-35EA4F341F44", text: { __$$text: "-" } },
              {
                "@_id": "_9EE69D59-9485-40F1-91D9-E300E823BFA7",
                text: { __$$text: '"Student"' },
              },
              { "@_id": "_F89E5DE2-0D3C-4D84-AB2A-CE50B916B0F0", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_30012312-3264-4FD3-9989-E562787519A7", text: { __$$text: "18" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_BBB9511D-41C4-4C58-906F-5D492C1187F4",
            inputEntry: [
              { "@_id": "_64DE80E6-C6B6-487B-9BAF-05A2878D85E7", text: { __$$text: "-" } },
              {
                "@_id": "_822CFDD2-2A2C-4B86-A6BA-D2CC3F0C8827",
                text: { __$$text: '"Employed"' },
              },
              { "@_id": "_D1FFF025-F01B-420A-87EB-5C19BB9409B7", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_73E6A4E8-4733-47C9-85CD-00E7E593CC68", text: { __$$text: "45" } }],
            annotationEntry: [],
          },
          {
            "@_id": "_CC5C1811-24AD-4B59-AE95-703013A00A09",
            inputEntry: [
              { "@_id": "_F670242A-D7BD-44B5-A08B-3D56B54A14F8", text: { __$$text: "-" } },
              {
                "@_id": "_73505448-A119-4045-AC02-83733CEB85DE",
                text: { __$$text: '"Self-Employed"' },
              },
              { "@_id": "_42B348D0-E612-4C63-98CE-6F102BF7C045", text: { __$$text: "-" } },
            ],
            outputEntry: [{ "@_id": "_53F400E8-8E83-4FB0-B840-F2B903F601F8", text: { __$$text: "36" } }],
            annotationEntry: [],
          },
        ],
      },
    },
  ],
};

export const applicationRiskScoreWidthsById = new Map([
  ["_36398C55-5ED1-41C6-B643-98DBDD52D143", [176, 739]],
  ["_45DE91F3-7731-4A5C-AC8B-0C6AD17508DF", [739]],
  ["_F3354465-FA19-4664-A0A1-355511122FBE", [739]],
  ["_8BDD13D2-4F23-4A76-9A5F-DD9164B1B21A", [739]],
  ["_40CCF542-F80F-4C14-AAE5-AAAFA3304648", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 132, 132, 144, 161, 100]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: applicationRiskScoreExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: applicationRiskScoreWidthsById,
  },
};
