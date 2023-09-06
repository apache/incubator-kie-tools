import * as React from "react";
import { BoxedExpressionEditorBase } from "../../boxedExpressionEditorBase";
import {
  ContextExpressionDefinition,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
} from "../../../../../src/api";

export const preBureauRiskCategoryExpression: ContextExpressionDefinition = {
  id: "_21BA0CB9-F15E-482F-BCBB-F6694EF9B1FC",
  name: "Pre-bureau risk category",
  dataType: "t.BureauRiskCategory" as DmnBuiltInDataType,
  logicType: ExpressionDefinitionLogicType.Context,
  contextEntries: [
    {
      entryInfo: {
        id: "_E0D6DEC1-A390-4AA7-86A0-42CFCE32C41E",
        name: "Existing Customer",
        dataType: DmnBuiltInDataType.Boolean,
      },
      entryExpression: {
        id: "_9D39AD1B-4DC7-4B5F-A605-9F04A4C761DA",
        name: "Existing Customer",
        dataType: DmnBuiltInDataType.Boolean,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Applicant data.ExistingCustomer",
        width: 570,
      },
    },
  ],
  result: {
    id: "_DD3AABF1-9E83-4F71-A769-D9CC01231580",
    name: "Result Expression",
    dataType: DmnBuiltInDataType.Undefined,
    logicType: ExpressionDefinitionLogicType.DecisionTable,
    hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
    aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
    annotations: [{ name: "annotation-1", width: 100 }],
    input: [
      {
        id: "_6887F8AD-5387-4D00-8CED-5234EB2C4EEB",
        name: "Existing Customer",
        dataType: DmnBuiltInDataType.Boolean,
        width: 123,
        idLiteralExpression: "_19E2A18A-57C5-473F-BCA9-79D1D4E4D9CE",
      },
      {
        id: "_D998C69B-7B36-4ECF-907B-D29B352B8D2F",
        name: "Application risk score",
        dataType: DmnBuiltInDataType.Number,
        width: 154,
        idLiteralExpression: "_9A98108B-7397-446B-AD37-CB00C2914548",
      },
    ],
    output: [
      {
        id: "_9C8DC883-11FB-451F-861F-EC795AE0F22E",
        name: "Result Expression",
        dataType: DmnBuiltInDataType.Undefined,
        width: 123,
      },
    ],
    rules: [
      {
        id: "_1F4DBB8E-192C-4D56-B893-8DAD237B9AA1",
        inputEntries: [
          { id: "_1110BF2B-1C84-4636-AAD9-F44D8A0A0894", content: "false" },
          { id: "_A59583B8-E83D-44CE-8B71-5C29C397CF4E", content: "<100" },
        ],
        outputEntries: [{ id: "_6A15E6CD-2A38-4517-A82C-CDF504E68E29", content: '"High"' }],
        annotationEntries: [""],
      },
      {
        id: "_EFBD32EA-8A08-4645-927D-EBB784D67910",
        inputEntries: [
          { id: "_AACE06DF-0F36-4EF9-8126-B8E16A2699F6", content: "false" },
          {
            id: "_BDED674F-0165-4DA4-9881-E12DADB7B19C",
            content: "[100..120)",
          },
        ],
        outputEntries: [{ id: "_9A9B2A8A-0E51-4093-BF26-038B24F3ED38", content: '"Medium"' }],
        annotationEntries: [""],
      },
      {
        id: "_F2F42947-F671-4230-A462-6BB6D4442D11",
        inputEntries: [
          { id: "_2C8BF827-0BAA-4C6C-B916-B938466F11E1", content: "false" },
          {
            id: "_8B65BDC4-9D04-4E1F-BE72-FFD91DD3EC67",
            content: "[120..130]",
          },
        ],
        outputEntries: [{ id: "_18D2FB23-AD7C-41CA-9EB6-D854BE331FE2", content: '"Low"' }],
        annotationEntries: [""],
      },
      {
        id: "_F65C4811-3179-430C-8733-21FDBF418B99",
        inputEntries: [
          { id: "_418478CE-A3D3-4F7C-8C9A-ACFCC335BC76", content: "false" },
          { id: "_5DE0CC48-75F8-4BD2-A402-CB44C503516E", content: ">130" },
        ],
        outputEntries: [
          {
            id: "_A60598EA-1DA9-443A-9552-C42A5068EEF4",
            content: '"Very Low"',
          },
        ],
        annotationEntries: [""],
      },
      {
        id: "_CDACC55A-3AFE-4A03-81C0-053D23DAE724",
        inputEntries: [
          { id: "_39A8EF67-15A4-4817-BE13-A9B9E2E01734", content: "true" },
          { id: "_6BBF7CDC-5ECC-44F2-98BB-C9FD51DB375C", content: "<80" },
        ],
        outputEntries: [
          {
            id: "_19C95F01-7781-4F9A-8A1B-8C441EEB9265",
            content: '"Decline"',
          },
        ],
        annotationEntries: [""],
      },
      {
        id: "_2131DA82-B7B0-454E-9176-4E529E7A2B48",
        inputEntries: [
          { id: "_01465B90-C329-45BB-B3D7-AAC159957788", content: "true" },
          { id: "_A933C989-DF1C-4635-8A07-3A7459F76698", content: "[80..90)" },
        ],
        outputEntries: [{ id: "_8B0A30E9-9627-48B4-82CA-7B549F27D393", content: '"High"' }],
        annotationEntries: [""],
      },
      {
        id: "_7A91A43D-7987-4C93-869C-5EA96E3ED201",
        inputEntries: [
          { id: "_FA35EBAF-DDDB-4B29-8ADC-BA431DCA3164", content: "true" },
          {
            id: "_C4653903-A560-4B79-83DD-98E0656AAB12",
            content: "[90..110]",
          },
        ],
        outputEntries: [{ id: "_4F267262-0CB6-48B5-8A8F-4DFA8808E6C6", content: '"Medium"' }],
        annotationEntries: [""],
      },
      {
        id: "_CDEDBBBE-05B7-4070-B479-1391ADC9C6E6",
        inputEntries: [
          { id: "_4FD8C67F-0AAE-47D5-917B-FB9B4812DC3E", content: "true" },
          { id: "_195C7AC3-9F87-41EA-9291-9E52E2CD84F6", content: ">110" },
        ],
        outputEntries: [{ id: "_67BF6667-022D-4852-A47F-4D40AEED4009", content: '"Low"' }],
        annotationEntries: [""],
      },
    ],
  },
  entryInfoWidth: 154,
};

export function PreBureauRiskCategory() {
  return <BoxedExpressionEditorBase expressionDefinition={preBureauRiskCategoryExpression} />;
}
