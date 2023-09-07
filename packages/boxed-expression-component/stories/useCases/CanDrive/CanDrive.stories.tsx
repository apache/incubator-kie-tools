import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import {
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
} from "../../../src/api";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import {
  BoxedExpressionEditorWrapper,
  beeGwtService,
  dataTypes,
  pmmlParams,
} from "../../boxedExpressionStoriesWrapper";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Can drive?",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const findEmployeesDataTypes = [...dataTypes, { typeRef: "tPerson", name: "tPerson", isCustom: true }];

export const canDriveExpressionDefinition: DecisionTableExpressionDefinition = {
  id: "_21608B6A-1D9E-426D-86CF-B0CA7AB20D31",
  name: "Can drive?",
  dataType: DmnBuiltInDataType.Boolean,
  logicType: ExpressionDefinitionLogicType.DecisionTable,
  hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
  aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
  annotations: [{ name: "annotation-1", width: 100 }],
  input: [
    {
      id: "_E2F88F75-7FD2-4CED-A7AC-015619176072",
      name: "Person.Age",
      dataType: DmnBuiltInDataType.Number,
      width: 100,
      idLiteralExpression: "_092AF86E-5D8C-4299-9998-8CB3B2DDF711",
    },
    {
      id: "_6B5D4A99-C73E-4DC2-8EEC-0158196F30D6",
      name: "Person.Country",
      dataType: DmnBuiltInDataType.String,
      width: 100,
      idLiteralExpression: "_14C2BCC6-5059-40A2-9406-E206C5CB6AF5",
    },
  ],
  output: [
    {
      id: "_6E8F61D6-308A-4E37-A088-F0265CD73F1B",
      name: "output-1",
      dataType: DmnBuiltInDataType.Boolean,
      width: 100,
    },
  ],
  rules: [
    {
      id: "_E81327B9-5E47-4C41-AFDC-73EDCA1D43B8",
      inputEntries: [
        { id: "_7EFC4F27-09D0-4206-BD5C-1C72081D9A75", content: ">= 18" },
        { id: "_A4BD080A-6F76-463A-B8E8-8560C9A79319", content: '"Brazil"' },
      ],
      outputEntries: [{ id: "_F40832D4-8E1B-41CD-8A1E-2384EED9863F", content: "true" }],
      annotationEntries: [""],
    },
    {
      id: "_1FAF2352-4C74-4DB2-9844-5446E7A732C4",
      inputEntries: [
        { id: "_34F94774-6D0F-474F-88FB-CBA164A25756", content: "< 18" },
        { id: "_FF4C4EED-7845-42F8-82A6-15F002A65F5B", content: '"Brazil"' },
      ],
      outputEntries: [{ id: "_D06D4AE9-2246-4A36-8B97-FE18BB893ABD", content: "false" }],
      annotationEntries: [""],
    },
    {
      id: "_00B2707A-36FE-4927-B43C-CD36D71DD0AE",
      inputEntries: [
        { id: "_EA0C9FA0-F959-46DA-97A7-98B935C00414", content: ">= 17" },
        { id: "_F8F56AA9-223B-4C66-B666-15127AA8AB86", content: '"England"' },
      ],
      outputEntries: [{ id: "_51179D92-A775-4776-9228-502A1746FB1F", content: "true" }],
      annotationEntries: [""],
    },
    {
      id: "_2DFCD2B1-FC83-4398-B482-3315D4809640",
      inputEntries: [
        { id: "_974148CB-2372-4044-8CB4-AE0B36819829", content: "< 17" },
        { id: "_D7929881-A203-438F-BE79-81632351501D", content: '"England"' },
      ],
      outputEntries: [{ id: "_6ACB8D90-F867-4BCE-AC3A-1100CC2B778E", content: "false" }],
      annotationEntries: [""],
    },
    {
      id: "_5E3E58B6-AEA8-4580-8BAB-1848CAE675E8",
      inputEntries: [
        { id: "_244C0142-2F80-4FC5-A02B-303B36A22D6A", content: ">= 16" },
        { id: "_FF24BDE6-72F0-49EB-9819-579040D5A0D7", content: '"US"' },
      ],
      outputEntries: [{ id: "_45E663A1-3B52-4672-97D6-05CA66390D20", content: "true" }],
      annotationEntries: [""],
    },
    {
      id: "_ABF08DF5-CD29-4453-8F73-612565D75E03",
      inputEntries: [
        { id: "_C6E28F29-CD4A-4B1A-855C-6FAC51FA09D2", content: "< 16" },
        { id: "_A4F61557-2D30-485E-80B1-EE8B815C4FF5", content: '"US"' },
      ],
      outputEntries: [{ id: "_F2042E73-CDE0-4BAC-A219-8D7E52175D94", content: "false" }],
      annotationEntries: [""],
    },
  ],
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: canDriveExpressionDefinition,
    dataTypes: findEmployeesDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
