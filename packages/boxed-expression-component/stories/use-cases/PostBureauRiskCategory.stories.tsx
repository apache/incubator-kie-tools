import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../boxedExpressionWrapper";
import { Base as EmptyExpression } from "./EmptyExpression.stories";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
} from "../../src/api";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Post Bureau Risk Category",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
  excludeStories: ["BoxedExpressionEditorWrapper"],
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

const dataTypes = EmptyExpression.args?.dataTypes;
dataTypes?.push({ typeRef: "tRisk", name: "tRisk", isCustom: true });
dataTypes?.push({ typeRef: "tApplicant", name: "tApplicant", isCustom: true });
dataTypes?.push({ typeRef: "tReport", name: "tReport", isCustom: true });

export const Base: Story = {
  render: (args) => BoxedExpressionEditorWrapper({ dataTypes }),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: "_F6913551-7226-4F7C-A006-05AA0443C1AD",
      name: "Post-Bureau Risk Category",
      dataType: "tRisk" as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Context,
      contextEntries: [
        {
          entryInfo: {
            id: "_D65ECF44-9D87-4ED8-8407-57FC63C589E8",
            name: "Product",
            dataType: DmnBuiltInDataType.String,
          },
          entryExpression: {
            id: "_FF02E828-2931-4607-A1BD-02D26CA05F27",
            name: "Product",
            dataType: DmnBuiltInDataType.String,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: '"Special Loan"',
            width: 665,
          },
        },
        {
          entryInfo: {
            id: "_ED2FF8D1-3640-4D81-9549-15B7BE383408",
            name: "Is existing customer?",
            dataType: DmnBuiltInDataType.Boolean,
          },
          entryExpression: {
            id: "_DCC28FDF-8A0A-4E40-8E0B-5FCC9F8756A3",
            name: "Is existing customer?",
            dataType: DmnBuiltInDataType.Boolean,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "true",
            width: 665,
          },
        },
        {
          entryInfo: {
            id: "_73DDBA41-C75A-403C-8932-5D28489A1C4B",
            name: "Credit Score",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: "_50A7F560-A6E8-4C67-8A2D-D88672167493",
            name: "Credit Score",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "610",
            width: 665,
          },
        },
        {
          entryInfo: {
            id: "_ADBC9A2A-885C-4477-8CE8-0F9B6338E060",
            name: "Affordability Model",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: "_3871BC41-C750-49CD-9D6C-8A062132F17E",
            name: "Affordability Model",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Function,
            formalParameters: [
              { id: "_DE366C1F-CCCF-4E1B-9154-31A247EF2559", name: "Product", dataType: DmnBuiltInDataType.String },
            ],
            functionKind: FunctionExpressionDefinitionKind.Feel,
            expression: {
              id: "_4565F1AE-9522-4D1F-A090-F4092DACBEA3",
              name: "Feel Expression",
              dataType: DmnBuiltInDataType.Undefined,
              logicType: ExpressionDefinitionLogicType.Context,
              contextEntries: [
                {
                  entryInfo: {
                    id: "_D5EA69E0-B81A-46A2-B326-8B0E4CF1FB60",
                    name: "Product Multiplier",
                    dataType: DmnBuiltInDataType.Number,
                  },
                  entryExpression: {
                    id: "_845E9BB4-662B-41FB-BB2E-C6049D6735CE",
                    name: "Product Multiplier",
                    dataType: DmnBuiltInDataType.Number,
                    logicType: ExpressionDefinitionLogicType.Literal,
                    content: 'if Product = "Special Loan" then 1.2 else 1',
                    width: 438,
                  },
                },
              ],
              result: {
                id: "_3BE079EB-4555-498C-AAF9-4124BFFCBA7E",
                name: "Result Expression",
                dataType: DmnBuiltInDataType.Undefined,
                logicType: ExpressionDefinitionLogicType.Literal,
                content: "100 * Product Multiplier",
                width: 438,
              },
              entryInfoWidth: 163,
            },
          },
        },
        {
          entryInfo: {
            id: "_DACF7738-1B70-410C-BD00-2E0B651E55E3",
            name: "Application Risk Score",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            id: "_9524EE95-E8F8-4AED-A543-3C82CCEE0EAB",
            name: "Application Risk Score",
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "Affordability Model(Product)",
            width: 665,
          },
        },
      ],
      result: {
        id: "_F06A52FE-A5EB-42F1-B35B-C5403813AD19",
        name: "Result Expression",
        dataType: DmnBuiltInDataType.Undefined,
        logicType: ExpressionDefinitionLogicType.DecisionTable,
        hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
        aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
        annotations: [{ name: "Annotations", width: 100 }],
        input: [
          {
            id: "_6B891640-E70B-43AA-8140-771E194C424C",
            idLiteralExpression: "_F007BA54-9BBB-4975-8A6D-E8D7E4B58359",
            name: "Is existing customer?",
            dataType: DmnBuiltInDataType.Boolean,
            width: 148,
          },
          {
            id: "_096D9801-63C1-425C-B1C0-519C664749E1",
            idLiteralExpression: "_F4DF902D-2E6B-4F33-9A80-26AE8AF8AB26",
            name: "Application Risk Score",
            dataType: DmnBuiltInDataType.Number,
            width: 154,
          },
          {
            id: "_E1DB636C-390D-450F-A365-20B99FFF7182",
            idLiteralExpression: "_1FDBAE87-3164-464F-A0D7-E26F6D566A62",
            name: "Credit Score",
            dataType: DmnBuiltInDataType.Number,
            width: 100,
          },
        ],
        output: [
          {
            id: "_4DDC5414-53C9-4709-ACEC-C2FFA0EBE715",
            name: "Result Expression",
            dataType: "tRisk" as DmnBuiltInDataType,
            width: 123,
          },
        ],
        rules: [
          {
            id: "_753AB9A4-A48C-4E5E-852D-F405936CD9C8",
            inputEntries: [
              { id: "_A006C541-FCEB-4662-9C1F-E375E01C73D7", content: "true" },
              { id: "_2AED60E8-0940-44BA-9CF9-2E425B63D01B", content: "<=120" },
              { id: "_590BAC11-F696-449B-A00E-E03F76E03E4A", content: "<590" },
            ],
            outputEntries: [{ id: "_18462712-012C-4808-9581-2CD9B6080330", content: '"HIGH"' }],
            annotationEntries: [""],
          },
          {
            id: "_ED418F80-B496-47E9-B5D3-09E093E2378E",
            inputEntries: [
              { id: "_399EDF13-95AC-4FF5-8153-9E5925D08B2A", content: "true" },
              { id: "_352E13BF-675E-4ECB-9F2D-0058FAE13AC0", content: "<=120" },
              { id: "_2BED1DCA-9E99-4DA7-A074-9570681F616A", content: ">=590" },
            ],
            outputEntries: [{ id: "_5C066C81-A3EC-48AC-A26E-0AB610F582C0", content: '"LOW"' }],
            annotationEntries: [""],
          },
          {
            id: "_D2CBB191-0C08-448B-B019-4557952EB98D",
            inputEntries: [
              { id: "_8A91E8F8-79D2-4A8F-A25E-0768779092BC", content: "true" },
              { id: "_E973BAE2-D165-4CAB-8F0A-E6E649D98EF6", content: ">120" },
              { id: "_43611647-BD27-4842-8BCC-885280BBA139", content: "<610" },
            ],
            outputEntries: [{ id: "_0A9DC20A-DFD7-4827-92E4-302BD0DB86F9", content: '"HIGH"' }],
            annotationEntries: [""],
          },
          {
            id: "_6241662D-9DBD-47EE-A463-EBACB1AB8611",
            inputEntries: [
              { id: "_18576417-6218-4E88-B1F4-33DE1B1C3208", content: "true" },
              { id: "_A995F892-8E0B-4C50-9060-30AA6282F9F5", content: ">120" },
              { id: "_8AD1F3F4-33FE-4D41-8444-CA9024E07B84", content: ">=610" },
            ],
            outputEntries: [{ id: "_EB28BE28-B6EE-4322-A2E1-A896AFE2D09F", content: '"LOW"' }],
            annotationEntries: [""],
          },
          {
            id: "_A8385DBA-FE69-4CC5-8492-2CB2ECCC64C2",
            inputEntries: [
              { id: "_1E74598B-6E2C-4BC4-B6C6-60B78239C4B4", content: "false" },
              { id: "_05397DE6-85B7-4323-8C5C-CA30843F0D21", content: "<=120" },
              { id: "_2D5675B2-1398-4E0F-B04E-3C481B774A6F", content: "<600" },
            ],
            outputEntries: [{ id: "_C4F7DB89-1826-46CB-A7CE-8C605F4EA725", content: '"HIGH"' }],
            annotationEntries: [""],
          },
          {
            id: "_E30809AC-C526-49E7-B36A-616899CB0D8E",
            inputEntries: [
              { id: "_F827EDA0-A2A0-42EC-9B06-5218319873C9", content: "false" },
              { id: "_1F7E7076-E691-4348-BAC5-31FDF2D978AF", content: "<=120" },
              { id: "_9CD83BD7-CE45-4437-9FD7-A676E38B6115", content: ">=600" },
            ],
            outputEntries: [{ id: "_9F760DAC-6F84-48D1-B20D-C158287ECB7A", content: '"LOW"' }],
            annotationEntries: [""],
          },
          {
            id: "_EEAE58D2-1505-4970-A916-D0FC00C9FA6C",
            inputEntries: [
              { id: "_30254C45-5E3D-4396-B787-0A588F552B02", content: "false" },
              { id: "_B8E616E0-658A-4FF2-9F3A-E330852307CA", content: ">120" },
              { id: "_60C79895-66E6-4FDF-9D58-A338DD8086F0", content: "<620" },
            ],
            outputEntries: [{ id: "_46586589-377F-442E-A95C-0E46653919B3", content: '"HIGH"' }],
            annotationEntries: [""],
          },
          {
            id: "_05E65344-58C4-4703-8B45-8C75E67F5915",
            inputEntries: [
              { id: "_26D9F0F9-C823-4DDE-AD85-3859BBD52100", content: "false" },
              { id: "_6B30E225-C43A-4095-916A-C3911256C9E1", content: ">120" },
              { id: "_3AEA2719-2523-4D9C-A02E-7A968B39BD36", content: ">=620" },
            ],
            outputEntries: [{ id: "_9EEAEC41-96AC-4017-9331-94919AB1D94B", content: '"LOW"' }],
            annotationEntries: [""],
          },
        ],
      },
      entryInfoWidth: 182,
    },
    isResetSupportedOnRootExpression: false,
  },
};
