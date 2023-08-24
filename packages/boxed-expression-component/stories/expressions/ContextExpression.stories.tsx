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
  generateUuid,
} from "../../src/api";
import { CONTEXT_ENTRY_INFO_MIN_WIDTH } from "../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Expressions/Context Expression",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
  excludeStories: ["BoxedExpressionEditorWrapper"],
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Base: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Expression Name",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Context,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      result: {
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
        id: generateUuid(),
      },
      contextEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
          },
          entryExpression: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Undefined,
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const PostBureauRiskCategory: Story = {
  render: () => {
    const dataTypes = Base.args?.dataTypes;
    dataTypes?.push({ typeRef: "tRisk", name: "tRisk", isCustom: true });
    return BoxedExpressionEditorWrapper({ dataTypes });
  },
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...EmptyExpression.args,
    expressionDefinition: {
      id: "_558F8D91-80DE-41DD-B45D-FD8C97974138",
      name: "Post-Bureau Risk Category",
      dataType: "tRisk" as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Context,
      entryInfoWidth: 120,
      result: {
        dataType: "tRisk" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.DecisionTable,
        hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
        aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
        input: [
          {
            id: "_A32DDD63-A665-44D9-AAB1-BDA3FC237DC4",
            idLiteralExpression: "_DC904D7F-C388-4095-814F-1A763A080D9B",
            name: "Existing Customer",
            dataType: DmnBuiltInDataType.Boolean,
            width: 150,
          },
          {
            id: "_620D5267-2D0A-4C4E-A1C7-8A8ACE804CBD",
            idLiteralExpression: "_6D72FAD6-DD23-42C0-9F6A-C781448A2CC9",
            name: "Application Risk Score",
            dataType: DmnBuiltInDataType.Number,
            width: 170,
          },
          {
            id: "_398342CD-15AD-439D-B582-D191F6443F3C",
            idLiteralExpression: "_682FDD07-632B-43EC-9E98-EC831252CDB6",
            name: "Credit Score",
            dataType: DmnBuiltInDataType.Number,
            width: 100,
          },
        ],
        output: [
          {
            id: "_86D8EA57-40AF-45E6-91DF-49B6E8FB6802",
            name: "Post-Bureau Risk Category",
            dataType: "tRisk" as DmnBuiltInDataType,
            width: 195,
          },
        ],
        annotations: [
          {
            name: "Annotations",
            width: 100,
          },
        ],
        rules: [
          {
            id: "_3BB21202-73B3-4903-A59D-3392E97F17B5",
            inputEntries: [
              {
                id: "_C4B220C1-2A9A-44D2-8EB3-668268A028B8",
                content: "true",
              },
              {
                id: "_AF8CA3DE-A30C-41CE-B022-1F78896F273A",
                content: "<=120",
              },
              {
                id: "_4092C947-FA5E-442F-A031-93A2935AB166",
                content: "<590",
              },
            ],
            outputEntries: [
              {
                id: "_E96AF270-605F-455D-900E-92375CE031B8",
                content: '"HIGH"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_1A985B72-8D6C-4FD7-AC2B-C320E935212D",
            inputEntries: [
              {
                id: "_31FC61AC-E232-4E08-843D-3BA41D54689C",
                content: "true",
              },
              {
                id: "_4400FCE5-3792-4583-96B4-35F98016BEB3",
                content: "<=120",
              },
              {
                id: "_18CA90A4-998B-4130-B64A-F66F07374715",
                content: "[590..610]",
              },
            ],
            outputEntries: [
              {
                id: "_1DAA52AF-3FAD-4D75-8599-2906F8AA57B9",
                content: '"MEDIUM"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_29414A6D-7D02-4CFF-99C4-FE9528882553",
            inputEntries: [
              {
                id: "_693A8E9E-5D7C-4BA2-983C-3320B3CB1866",
                content: "true",
              },
              {
                id: "_F643692C-300E-42CE-BB6C-66E243F58D2A",
                content: "<=120",
              },
              {
                id: "_06BDCEB9-A784-4BEC-9AD1-1FBE678427A3",
                content: ">610",
              },
            ],
            outputEntries: [
              {
                id: "_6B999377-D69D-41EB-9558-C071592BB198",
                content: '"LOW"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_54EE45CF-CF97-4F0F-B8FA-4C202374D547",
            inputEntries: [
              {
                id: "_94D89903-ED25-41B7-8055-01E7635DC688",
                content: "true",
              },
              {
                id: "_336B3D04-4FCC-4DEC-98B7-541BAEACD31D",
                content: ">120",
              },
              {
                id: "_8D288F94-ADF0-4773-917C-0C9B772E56D4",
                content: "<600",
              },
            ],
            outputEntries: [
              {
                id: "_3C456009-E330-448D-B8CE-DB32AE9B4518",
                content: '"HIGH"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_47D493BE-B6E6-4F02-9DFE-645E29C1389F",
            inputEntries: [
              {
                id: "_75521E86-6FA8-47E6-A592-6B44C8247F40",
                content: "true",
              },
              {
                id: "_9BB52ACC-E333-4A24-8A00-27156E83EEE9",
                content: ">120",
              },
              {
                id: "_7746F0BD-6544-4368-81A9-29C3A32BF329",
                content: "[600..625]",
              },
            ],
            outputEntries: [
              {
                id: "_11D8FE09-300D-4496-BBB6-BD46DE3A5A28",
                content: '"MEDIUM"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_89A281A2-CBA6-4700-9A95-EF5BFD3F6620",
            inputEntries: [
              {
                id: "_228E741F-6BE4-4A63-AF5A-D196649A4BE8",
                content: "true",
              },
              {
                id: "_901699D1-AB01-4027-9511-44195D3E32B6",
                content: ">120",
              },
              {
                id: "_E8126296-7282-4A37-B46D-54EA6C5E79F0",
                content: ">625",
              },
            ],
            outputEntries: [
              {
                id: "_742D72BB-E0B3-4BA1-A81D-62C89154BA6D",
                content: '"LOW"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_9A4A4F12-9FDA-459C-B113-E1F36AE90668",
            inputEntries: [
              {
                id: "_5CA4FFB1-999E-47E4-A540-EE579A27575D",
                content: "false",
              },
              {
                id: "_D6FF22EF-7C91-4A61-BA13-2225DC4C41EB",
                content: "<=100",
              },
              {
                id: "_422CC163-D94D-4E8F-89B1-07ECC2DA3627",
                content: "<580",
              },
            ],
            outputEntries: [
              {
                id: "_3439B665-E6EE-4D47-B22D-583207A7DCF9",
                content: '"HIGH"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_CF3F4B9F-4D25-479A-8163-0682F0FAD3BB",
            inputEntries: [
              {
                id: "_ECEC7B07-7220-4FFB-BED3-63E1430D2638",
                content: "false",
              },
              {
                id: "_7213B7D7-36B0-4B2C-9289-AC4B831768E7",
                content: "<=100",
              },
              {
                id: "_3D1C9B36-4BC0-4D0B-9E32-8F698167C713",
                content: "[580..600]",
              },
            ],
            outputEntries: [
              {
                id: "_3E5D2444-1F65-4AB2-A620-45E34E0AC2BC",
                content: '"MEDIUM"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_A10434D8-6FA0-4B98-A9BC-8AADF37E4D4E",
            inputEntries: [
              {
                id: "_B719673B-BF84-4E1D-B6DD-3B27B63106E7",
                content: "false",
              },
              {
                id: "_86885A60-9950-41D6-BD4D-B2F7EB0A4F1F",
                content: "<=100",
              },
              {
                id: "_F17D2031-CB32-4B64-968F-BA5FF7DA8C24",
                content: ">600",
              },
            ],
            outputEntries: [
              {
                id: "_E68B8176-7D89-4BF4-8143-832C6516F96F",
                content: '"LOW"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_7BEA0C4C-B1BD-4FD9-8D46-C8ACD0798AA8",
            inputEntries: [
              {
                id: "_6C647CC4-8C02-4D4B-A12C-7C851D674847",
                content: "false",
              },
              {
                id: "_A680B024-D6CC-4790-BFD6-202E8BE59A86",
                content: ">100",
              },
              {
                id: "_177B136C-87C5-4AD2-9F42-E5BE6DA0E764",
                content: "<590",
              },
            ],
            outputEntries: [
              {
                id: "_BDFAB34A-102A-4C97-9900-A64CF7C9A682",
                content: '"HIGH"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_B41387A3-D664-4233-8BE7-803CEE378FAB",
            inputEntries: [
              {
                id: "_D2F328A9-C72E-48C7-A5E3-C0E289D9E2B8",
                content: "false",
              },
              {
                id: "_B081C5BC-2FF8-4219-B3F1-9522DFE9C1EE",
                content: ">100",
              },
              {
                id: "_280D9B8C-789E-4D25-8179-AF3635F8E4EB",
                content: "[590..615]",
              },
            ],
            outputEntries: [
              {
                id: "_CDAEAD71-A033-40C4-8086-DC4D559B4C64",
                content: '"MEDIUM"',
              },
            ],
            annotationEntries: [""],
          },
          {
            id: "_2B36F39E-3BA0-4991-8E48-FCADDFDC48CF",
            inputEntries: [
              {
                id: "_A179C9F0-E065-4F25-A103-172D445A6AE0",
                content: "false",
              },
              {
                id: "_B48A6A8A-CD73-4D82-B651-AD1AE0666D9A",
                content: ">100",
              },
              {
                id: "_F0EFFDA9-0A08-4D56-938B-21BB496FDF27",
                content: ">615",
              },
            ],
            outputEntries: [
              {
                id: "_00EE5B75-CADC-40B4-A437-FD0AD8341A30",
                content: '"LOW"',
              },
            ],
            annotationEntries: [""],
          },
        ],
        id: "_90B25A9D-89C1-43BB-A3B8-590C6B16BF1C",
        name: "Post-Bureau Risk Category",
      },
      contextEntries: [
        {
          entryInfo: {
            id: "_9E3859FC-E71E-4921-8718-5B0F0AA25DF9",
            name: "Existing Customer",
            dataType: DmnBuiltInDataType.Boolean,
          },
          entryExpression: {
            id: "_2FDCA1B9-4308-4C7A-A570-9D0A5D7C4D78",
            name: "Existing Customer",
            dataType: DmnBuiltInDataType.Boolean,
            logicType: ExpressionDefinitionLogicType.Literal,
            content: "Applicant.Existing Customer",
            width: 750,
          },
        },
        {
          entryInfo: {
            id: "_068C1B61-B5E5-44FA-9B81-5132995E6770",
            name: "Credit Score",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            width: 750,
            id: "_681ED43C-3598-45B1-B283-B57D7EEEF66B",
            name: "Credit Score",
            content: "Report.Credit Score",
          },
        },
        {
          entryInfo: {
            id: "_92EC5BEC-FF73-4D6E-9622-108DFA42D81C",
            name: "Application Risk Score",
            dataType: DmnBuiltInDataType.Number,
          },
          entryExpression: {
            dataType: DmnBuiltInDataType.Number,
            logicType: ExpressionDefinitionLogicType.Literal,
            width: 750,
            id: "_E7AD166F-A9A4-4D5C-B47D-54EE76F23991",
            name: "Application Risk Score",
            content: "Affordability Model(Applicant, Product).\nApplication Risk Score",
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
