import * as React from "react";
import {
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  InvocationExpressionDefinition,
  RelationExpressionDefinition,
} from "../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import {
  BoxedExpressionEditorWrapper,
  beeGwtService,
  dataTypes,
  pmmlParams,
} from "../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Use cases/Find Employees",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorProps>;

export const findEmployeesDataTypes = [
  ...dataTypes,
  { typeRef: "tEmployee", name: "tEmployee", isCustom: true },
  { typeRef: "tEmployees", name: "tEmployees", isCustom: true },
  { typeRef: "tDept", name: "tDept", isCustom: true },
  { typeRef: "tKnowledge", name: "tKnowledge", isCustom: true },
  { typeRef: "tKnowledges", name: "tKnowledges", isCustom: true },
];

export const employeesExpression: RelationExpressionDefinition = {
  id: "_03E4FDF0-AF4A-4C82-A589-2F9BED02921B",
  name: "Employees",
  dataType: "tEmployees" as DmnBuiltInDataType,
  logicType: ExpressionDefinitionLogicType.Relation,
  columns: [
    { id: "_ACD8C687-7D0D-403A-9449-6A965092E5F4", name: "Id", dataType: DmnBuiltInDataType.Number, width: 100 },
    { id: "_73B3ACFF-CC74-4022-85F2-5B739EAB64CD", name: "Name", dataType: DmnBuiltInDataType.String, width: 156 },
    { id: "_7AFBA31B-65D3-4949-BFEC-F9D9C32DF5A2", name: "Dept", dataType: "tDept" as DmnBuiltInDataType, width: 150 },
    {
      id: "_55A5C22A-2143-44C0-B948-49DA4029B7F2",
      name: "Knowledges",
      dataType: "tKnowledges" as DmnBuiltInDataType,
      width: 252,
    },
  ],
  rows: [
    {
      id: "_39202806-0CD0-4195-BE39-3273222194A1",
      cells: [
        { id: "_266725C0-F1BC-467A-89D4-556778DB7F97", content: "32" },
        { id: "_7797EE7A-674F-4C23-9492-8978F7DD1F21", content: '"Jane Doe"' },
        {
          id: "_CFBB6D19-68E2-4195-AF33-90EBD1617BBE",
          content: '"Management"',
        },
        {
          id: "_2AC6F43D-DE71-4E4C-8A6B-7AEBBF34F66F",
          content: '["Scrum", "Git"]',
        },
      ],
    },
    {
      id: "_358F4D9D-7F2D-4C0D-B1E0-2AD0ACB0DC87",
      cells: [
        { id: "_E8D11920-188B-4DE2-95E6-72C55388C210", content: "555" },
        { id: "_4C2A6CE6-51E7-4E96-9CB3-F7036DD72913", content: '"John Doe"' },
        {
          id: "_5A6A6B53-A84F-4358-BF59-B653F9C9394E",
          content: '"Engineering"',
        },
        {
          id: "_56769F77-9DA7-4FEF-8940-A5416DF8D869",
          content: '["TypeScript", "Java", "Git"]',
        },
      ],
    },
    {
      id: "_AAA8DCAB-C979-4028-AE29-262975C9DEE2",
      cells: [
        { id: "_DA8347DA-F62C-4019-8320-C037AC9BC962", content: "4567" },
        {
          id: "_02142673-C3DB-42C2-BE0C-B4D984015691",
          content: '"Richard Roe"',
        },
        { id: "_0544709B-37E7-4741-9D88-4D8D7E1C1342", content: '"Sales"' },
        { id: "_37E83F66-710D-427E-87D4-DC1547E67153", content: '["Scrum"]' },
      ],
    },
    {
      id: "_DBF7C3FB-251B-4194-AB3A-5E196D433CA4",
      cells: [
        { id: "_241F8262-6B06-4698-8AA8-91E6C3C5F154", content: "612" },
        { id: "_008B8BAA-D9B8-48AB-A6A6-DFD259960738", content: '"Foo Bar"' },
        {
          id: "_1F184F9F-BD11-4A38-A199-10CD59793279",
          content: '"Engineering"',
        },
        {
          id: "_FA1293F9-4059-4928-9528-CA8D89956CFE",
          content: '["TypeScript", "Git"]',
        },
      ],
    },
  ],
};

export const findEmployeesByKnowledgeExpression: FunctionExpressionDefinition = {
  id: "_243056C9-28EA-4EF2-8510-6F3BC6A5E5DC",
  name: "Find employee by knowledge",
  dataType: "tEmployees" as DmnBuiltInDataType,
  logicType: ExpressionDefinitionLogicType.Function,
  formalParameters: [
    { id: "_FC4939B4-6414-4F17-8D8C-FE339EA9AC5D", name: "employees", dataType: "tEmployees" as DmnBuiltInDataType },
    { id: "_2B8160CE-1F54-42D7-B111-706FE3BF2012", name: "knowledge", dataType: "tKnowledge" as DmnBuiltInDataType },
    { id: "_810CD0CE-7F46-4827-B19B-BFBD543BB3D9", name: "dept", dataType: "tDept" as DmnBuiltInDataType },
  ],
  functionKind: FunctionExpressionDefinitionKind.Feel,
  expression: {
    id: "_1B96445B-523F-49B3-99BE-308A2F3CFBA5",
    name: "Feel Expression",
    dataType: DmnBuiltInDataType.Undefined,
    logicType: ExpressionDefinitionLogicType.Context,
    contextEntries: [
      {
        entryInfo: {
          id: "_A762CB8E-56C3-45AE-B59F-731E4A0CA73F",
          name: "Employees by Dept",
          dataType: "tEmployees" as DmnBuiltInDataType,
        },
        entryExpression: {
          id: "_BC626016-B599-47A1-ABD6-67A2C7F761CE",
          name: "Employees by Dept",
          dataType: "tEmployees" as DmnBuiltInDataType,
          logicType: ExpressionDefinitionLogicType.Literal,
          content: "employees[item.Dept = dept]",
          width: 400,
        },
      },
      {
        entryInfo: {
          id: "_C772ADED-2499-4AF1-9C99-63AA17901EBB",
          name: "Employees with Knowledge",
          dataType: "tEmployees" as DmnBuiltInDataType,
        },
        entryExpression: {
          id: "_3543AF00-0F90-47C4-BCBF-6E3B28A08BD7",
          name: "Employees with Knowledge",
          dataType: "tEmployees" as DmnBuiltInDataType,
          logicType: ExpressionDefinitionLogicType.Literal,
          content:
            "for e in Employees by Dept return \n\tif (list contains(e.Knowledges, knowledge))\n\t\tthen e\n\t\telse null",
          width: 400,
        },
      },
    ],
    result: {
      id: "_049ECA86-1971-48B7-86FF-7E89B399074A",
      name: "Result Expression",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Literal,
      content: "Employees with Knowledge[item != null]",
      width: 400,
    },
    entryInfoWidth: 204,
  },
};

export const findEmployeesExpression: InvocationExpressionDefinition = {
  id: "_8001CBC8-8DE4-4DBF-8B02-7FBFC582B136",
  name: "Find employees",
  dataType: "tEmployees" as DmnBuiltInDataType,
  logicType: ExpressionDefinitionLogicType.Invocation,
  invokedFunction: { id: "_173AE705-5374-42AA-B4A4-085FA700B76F", name: "Find employee by knowledge" },
  bindingEntries: [
    {
      entryInfo: {
        id: "_D51D238A-A302-435C-BEED-25810C4FD922",
        name: "employees",
        dataType: "tEmployees" as DmnBuiltInDataType,
      },
      entryExpression: {
        id: "_AF546AE9-D0D0-4090-80FC-3E9EB9FECB07",
        name: "employees",
        dataType: "tEmployees" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Employees",
        width: 100,
      },
    },
    {
      entryInfo: {
        id: "_2F962A92-5918-42F9-8237-30F7795B7CA0",
        name: "knowledge",
        dataType: "tKnowledge" as DmnBuiltInDataType,
      },
      entryExpression: {
        id: "_04265B8B-F45E-47F1-A167-03880698029D",
        name: "knowledge",
        dataType: "tKnowledge" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Knowledge",
        width: 100,
      },
    },
    {
      entryInfo: {
        id: "_D97531A0-AF31-4E5C-9400-82B93B8B17D9",
        name: "dept",
        dataType: "tDept" as DmnBuiltInDataType,
      },
      entryExpression: {
        id: "_6A2F59F4-BE08-45F3-B3B2-7619C5931CC5",
        name: "dept",
        dataType: "tDept" as DmnBuiltInDataType,
        logicType: ExpressionDefinitionLogicType.Literal,
        content: "Dept",
        width: 100,
      },
    },
  ],
  entryInfoWidth: 120,
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Employees: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: employeesExpression,
    dataTypes: findEmployeesDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};

export const FindEmployeesByKnowledge: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: findEmployeesByKnowledgeExpression,
    dataTypes: findEmployeesDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};

export const FindByEmployees: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    decisionNodeId: "_00000000-0000-0000-0000-000000000000",
    expressionDefinition: findEmployeesExpression,
    dataTypes: findEmployeesDataTypes,
    beeGwtService,
    pmmlParams,
    isResetSupportedOnRootExpression: false,
  },
};
