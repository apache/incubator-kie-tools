import * as React from "react";
import { loanOriginationsDataTypes } from "../boxedExpressionEditorBase";
import {
  ContextExpressionDefinition,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
} from "../../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditorWrapper, beeGwtService, pmmlParams } from "../../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../../src/expressions";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Loan Originations/Application Risk Score",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const applicationRiskScoreExpression: ContextExpressionDefinition = {
  id: "_36398C55-5ED1-41C6-B643-98DBDD52D143",
  name: "Application risk score",
  dataType: DmnBuiltInDataType.Number,
  logicType: ExpressionDefinitionLogicType.Context,
  contextEntries: [
    {
      entryInfo: { id: "_BB4F357E-80D8-4E5A-955F-387B5DBB2EC5", name: "Age", dataType: DmnBuiltInDataType.Number },
      entryExpression: {
        id: "_45DE91F3-7731-4A5C-AC8B-0C6AD17508DF",
        name: "Age",
        dataType: DmnBuiltInDataType.Number,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Applicant data.Age",
        width: 739,
      },
    },
    {
      entryInfo: {
        id: "_A3E35C5D-A643-40AA-A490-772105D00738",
        name: "Marital Status",
        dataType: "t.MaritalStatus" as DmnBuiltInDataType,
      },
      entryExpression: {
        id: "_F3354465-FA19-4664-A0A1-355511122FBE",
        name: "Marital Status",
        dataType: "t.MaritalStatus" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Applicant data.MaritalStatus",
        width: 739,
      },
    },
    {
      entryInfo: {
        id: "_4509453F-BB4B-46F2-915C-FA90D90A4DFB",
        name: "Employment Status",
        dataType: "t.EmploymentStatus" as DmnBuiltInDataType,
      },
      entryExpression: {
        id: "_8BDD13D2-4F23-4A76-9A5F-DD9164B1B21A",
        name: "Employment Status",
        dataType: "t.EmploymentStatus" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Applicant data.EmploymentStatus",
        width: 739,
      },
    },
  ],
  result: {
    id: "_40CCF542-F80F-4C14-AAE5-AAAFA3304648",
    name: "Result Expression",
    dataType: DmnBuiltInDataType.Undefined,
    logicType: ExpressionDefinitionLogicType.DecisionTable,
    hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Collect,
    aggregation: DecisionTableExpressionDefinitionBuiltInAggregation.SUM,
    annotations: [{ name: "annotation-1", width: 100 }],
    input: [
      {
        id: "_BD17099C-29FB-4252-B0DC-C6B93BF5B832",
        name: "Age",
        dataType: DmnBuiltInDataType.Number,
        width: 132,
        idLiteralExpression: "_BE1DBAB9-BE82-4F8F-8E16-6227731C9E65",
      },
      {
        id: "_D6A329C9-D92E-43AB-9449-827C83697F59",
        name: "Employment Status",
        dataType: "t.EmploymentStatus" as DmnBuiltInDataType,
        width: 132,
        idLiteralExpression: "_E7F1CC56-2891-48BF-92A1-41414FC14994",
      },
      {
        id: "_34A3B4B2-988F-49CF-9729-50B97B11B3AB",
        name: "Marital Status",
        dataType: "t.MaritalStatus" as DmnBuiltInDataType,
        width: 144,
        idLiteralExpression: "_4A1DB037-D38D-45D1-A8E8-CB17896811B2",
      },
    ],
    output: [
      {
        id: "_813B3D23-EA80-4D61-B197-150B9C51FAD8",
        name: "Result Expression",
        dataType: DmnBuiltInDataType.Undefined,
        width: 161,
      },
    ],
    rules: [
      {
        id: "_EB2B5C94-DCAB-4020-A78E-BCB9E6B6494D",
        inputEntries: [
          { id: "_20D11878-D7F2-43F5-90F5-C149BF243325", content: "[18..22)" },
          { id: "_FA84643B-4E41-4833-BA8B-039699D98094", content: "-" },
          { id: "_A6265958-B697-4B0D-BC55-7435AC2255B4", content: "-" },
        ],
        outputEntries: [{ id: "_E6BA0A4A-376F-4D5B-922D-07ED1B16F561", content: "32" }],
        annotationEntries: [""],
      },
      {
        id: "_D42C5ACB-95A7-4995-8C0D-926F5989A8CA",
        inputEntries: [
          { id: "_3763755B-DE16-420D-BD88-E6FB242D7B98", content: "[22..26)" },
          { id: "_976F8FDB-525B-45BB-855B-E19E4B3B3736", content: "-" },
          { id: "_89EE9CE1-FB90-4266-9EE9-E2A956A4FD16", content: "-" },
        ],
        outputEntries: [{ id: "_67313A9A-7C28-42A9-B513-F269F3357E93", content: "35" }],
        annotationEntries: [""],
      },
      {
        id: "_C90EF514-B92E-47D2-B41C-C0445F9BB708",
        inputEntries: [
          { id: "_A9813478-558D-43CC-B03E-46E8991D60F0", content: "[26..36)" },
          { id: "_6DF013B0-8B97-4561-9004-AE96EC3261AE", content: "-" },
          { id: "_F0A0DE3D-0435-493B-A8FA-1763F8D97352", content: "-" },
        ],
        outputEntries: [{ id: "_D2598910-8427-4DBC-97A9-E81D64ACBDA9", content: "40" }],
        annotationEntries: [""],
      },
      {
        id: "_A6F1BFC3-1C0F-4416-95A0-7F7B12805048",
        inputEntries: [
          { id: "_94226ADB-5B90-4165-8458-F0C78DA35F9A", content: "[36..50)" },
          { id: "_267986B1-6716-433D-A827-791BCE46591D", content: "-" },
          { id: "_8357A853-1754-47D7-B10A-18E9A0CA4A27", content: "-" },
        ],
        outputEntries: [{ id: "_5C1A37FD-B22A-446D-BC47-0E67B4671E2C", content: "43" }],
        annotationEntries: [""],
      },
      {
        id: "_0597BEB7-17F4-472F-B69C-3E6807C74FDD",
        inputEntries: [
          { id: "_F0EF3ED4-D702-49D2-BBD3-069B9D979799", content: ">=50" },
          { id: "_2CD88F2E-1566-49B6-A097-46E9555FA8EC", content: "-" },
          { id: "_30FB6674-CDFD-4610-98AF-58B5F06DCB0A", content: "-" },
        ],
        outputEntries: [{ id: "_569D30AA-AFFF-49F3-B602-FDE601C2F617", content: "48" }],
        annotationEntries: [""],
      },
      {
        id: "_7D674FE0-672E-4603-9546-C7DC9B2B4AF7",
        inputEntries: [
          { id: "_05170CC3-F815-4AE3-B906-5855D324E857", content: "-" },
          { id: "_E2722AF5-7D48-41E3-AC07-7C7DFA1CB5ED", content: "-" },
          { id: "_94363FC7-6731-4860-96A5-B4FD35609F81", content: '"S"' },
        ],
        outputEntries: [{ id: "_0ECF5E3F-0FDA-498C-9B98-C789DD70E0C9", content: "25" }],
        annotationEntries: [""],
      },
      {
        id: "_773EF50F-4C43-4D37-89A2-81E4246B906A",
        inputEntries: [
          { id: "_D6FF8B88-73E2-41CF-A4E0-101F8FD73ABA", content: "-" },
          { id: "_2754AEEA-D04D-416B-97BA-ABD9B4F303EC", content: "-" },
          { id: "_604CD0C4-4E51-4DF4-B464-E731DDCD3340", content: '"M"' },
        ],
        outputEntries: [{ id: "_E3CA53E0-131D-419A-9E61-35B5666D2ACB", content: "45" }],
        annotationEntries: [""],
      },
      {
        id: "_DA9E4278-8339-4FF4-A068-0190FE25E267",
        inputEntries: [
          { id: "_BE15E162-C9A9-4B3D-BA5B-2886970303FB", content: "-" },
          {
            id: "_1A4773D1-FDBC-4DB6-B930-A56059872200",
            content: '"Unemployed"',
          },
          { id: "_C0CDCFF4-F73B-4C5E-8942-F6BB3645948A", content: "-" },
        ],
        outputEntries: [{ id: "_FDFA6746-4D5F-4CA9-B4CD-36E6E8186D80", content: "15" }],
        annotationEntries: [""],
      },
      {
        id: "_D997BF3C-F595-4143-81FC-81B9A4E64BAA",
        inputEntries: [
          { id: "_3781127D-49B5-4D58-941D-35EA4F341F44", content: "-" },
          {
            id: "_9EE69D59-9485-40F1-91D9-E300E823BFA7",
            content: '"Student"',
          },
          { id: "_F89E5DE2-0D3C-4D84-AB2A-CE50B916B0F0", content: "-" },
        ],
        outputEntries: [{ id: "_30012312-3264-4FD3-9989-E562787519A7", content: "18" }],
        annotationEntries: [""],
      },
      {
        id: "_BBB9511D-41C4-4C58-906F-5D492C1187F4",
        inputEntries: [
          { id: "_64DE80E6-C6B6-487B-9BAF-05A2878D85E7", content: "-" },
          {
            id: "_822CFDD2-2A2C-4B86-A6BA-D2CC3F0C8827",
            content: '"Employed"',
          },
          { id: "_D1FFF025-F01B-420A-87EB-5C19BB9409B7", content: "-" },
        ],
        outputEntries: [{ id: "_73E6A4E8-4733-47C9-85CD-00E7E593CC68", content: "45" }],
        annotationEntries: [""],
      },
      {
        id: "_CC5C1811-24AD-4B59-AE95-703013A00A09",
        inputEntries: [
          { id: "_F670242A-D7BD-44B5-A08B-3D56B54A14F8", content: "-" },
          {
            id: "_73505448-A119-4045-AC02-83733CEB85DE",
            content: '"Self-Employed"',
          },
          { id: "_42B348D0-E612-4C63-98CE-6F102BF7C045", content: "-" },
        ],
        outputEntries: [{ id: "_53F400E8-8E83-4FB0-B840-F2B903F601F8", content: "36" }],
        annotationEntries: [""],
      },
    ],
  },
  entryInfoWidth: 176,
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: applicationRiskScoreExpression,
    dataTypes: loanOriginationsDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
