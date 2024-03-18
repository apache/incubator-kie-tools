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

import {
  DmnBuiltInDataType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  InvocationExpressionDefinition,
  RelationExpressionDefinition,
} from "../../../src/api";
import type { Meta, StoryObj } from "@storybook/react";
import {
  beeGwtService,
  BoxedExpressionEditorWrapper,
  dataTypes,
  pmmlDocuments,
} from "../../boxedExpressionStoriesWrapper";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../src/resizing/WidthConstants";

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
  __$$element: "relation",
  "@_id": "_03E4FDF0-AF4A-4C82-A589-2F9BED02921B",
  "@_label": "Employees",
  "@_typeRef": "tEmployees",
  column: [
    { "@_id": "_ACD8C687-7D0D-403A-9449-6A965092E5F4", "@_name": "Id", "@_typeRef": DmnBuiltInDataType.Number },
    { "@_id": "_73B3ACFF-CC74-4022-85F2-5B739EAB64CD", "@_name": "Name", "@_typeRef": DmnBuiltInDataType.String },
    { "@_id": "_7AFBA31B-65D3-4949-BFEC-F9D9C32DF5A2", "@_name": "Dept", "@_typeRef": "tDept" },
    {
      "@_id": "_55A5C22A-2143-44C0-B948-49DA4029B7F2",
      "@_name": "Knowledges",
      "@_typeRef": "tKnowledges",
    },
  ],
  row: [
    {
      "@_id": "_39202806-0CD0-4195-BE39-3273222194A1",
      expression: [
        {
          "@_id": "_266725C0-F1BC-467A-89D4-556778DB7F97",
          __$$element: "literalExpression",
          text: { __$$text: "32" },
        },
        {
          "@_id": "_7797EE7A-674F-4C23-9492-8978F7DD1F21",
          __$$element: "literalExpression",
          text: { __$$text: '"Jane Doe"' },
        },
        {
          "@_id": "_CFBB6D19-68E2-4195-AF33-90EBD1617BBE",
          __$$element: "literalExpression",
          text: { __$$text: '"Management"' },
        },
        {
          "@_id": "_2AC6F43D-DE71-4E4C-8A6B-7AEBBF34F66F",
          __$$element: "literalExpression",
          text: { __$$text: '["Scrum", "Git"]' },
        },
      ],
    },
    {
      "@_id": "_358F4D9D-7F2D-4C0D-B1E0-2AD0ACB0DC87",
      expression: [
        {
          "@_id": "_E8D11920-188B-4DE2-95E6-72C55388C210",
          __$$element: "literalExpression",
          text: { __$$text: "555" },
        },
        {
          "@_id": "_4C2A6CE6-51E7-4E96-9CB3-F7036DD72913",
          __$$element: "literalExpression",
          text: { __$$text: '"John Doe"' },
        },
        {
          "@_id": "_5A6A6B53-A84F-4358-BF59-B653F9C9394E",
          __$$element: "literalExpression",
          text: { __$$text: '"Engineering"' },
        },
        {
          "@_id": "_56769F77-9DA7-4FEF-8940-A5416DF8D869",
          __$$element: "literalExpression",
          text: { __$$text: '["TypeScript", "Java", "Git"]' },
        },
      ],
    },
    {
      "@_id": "_AAA8DCAB-C979-4028-AE29-262975C9DEE2",
      expression: [
        {
          "@_id": "_DA8347DA-F62C-4019-8320-C037AC9BC962",
          __$$element: "literalExpression",
          text: { __$$text: "4567" },
        },
        {
          "@_id": "_02142673-C3DB-42C2-BE0C-B4D984015691",
          __$$element: "literalExpression",
          text: { __$$text: '"Richard Roe"' },
        },
        {
          "@_id": "_0544709B-37E7-4741-9D88-4D8D7E1C1342",
          __$$element: "literalExpression",
          text: { __$$text: '"Sales"' },
        },
        {
          "@_id": "_37E83F66-710D-427E-87D4-DC1547E67153",
          __$$element: "literalExpression",
          text: { __$$text: '["Scrum"]' },
        },
      ],
    },
    {
      "@_id": "_DBF7C3FB-251B-4194-AB3A-5E196D433CA4",
      expression: [
        {
          "@_id": "_241F8262-6B06-4698-8AA8-91E6C3C5F154",
          __$$element: "literalExpression",
          text: { __$$text: "612" },
        },
        {
          "@_id": "_008B8BAA-D9B8-48AB-A6A6-DFD259960738",
          __$$element: "literalExpression",
          text: { __$$text: '"Foo Bar"' },
        },
        {
          "@_id": "_1F184F9F-BD11-4A38-A199-10CD59793279",
          __$$element: "literalExpression",
          text: { __$$text: '"Engineering"' },
        },
        {
          "@_id": "_FA1293F9-4059-4928-9528-CA8D89956CFE",
          __$$element: "literalExpression",
          text: { __$$text: '["TypeScript", "Git"]' },
        },
      ],
    },
  ],
};

export const employeesWidthsById = new Map<string, number[]>([
  ["_03E4FDF0-AF4A-4C82-A589-2F9BED02921B", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 100, 156, 150, 252, 100]],
]);

export const findEmployeesByKnowledgeExpression: FunctionExpressionDefinition = {
  __$$element: "functionDefinition",
  "@_id": "_243056C9-28EA-4EF2-8510-6F3BC6A5E5DC",
  "@_label": "Find employee by knowledge",
  "@_typeRef": "tEmployees",
  formalParameter: [
    { "@_id": "_FC4939B4-6414-4F17-8D8C-FE339EA9AC5D", "@_name": "employees", "@_typeRef": "tEmployees" },
    { "@_id": "_2B8160CE-1F54-42D7-B111-706FE3BF2012", "@_name": "knowledge", "@_typeRef": "tKnowledge" },
    { "@_id": "_810CD0CE-7F46-4827-B19B-BFBD543BB3D9", "@_name": "dept", "@_typeRef": "tDept" },
  ],
  "@_kind": FunctionExpressionDefinitionKind.Feel,
  expression: {
    __$$element: "context",
    "@_id": "_1B96445B-523F-49B3-99BE-308A2F3CFBA5",
    "@_label": "Feel Expression",
    contextEntry: [
      {
        variable: {
          "@_id": "_A762CB8E-56C3-45AE-B59F-731E4A0CA73F",
          "@_name": "Employees by Dept",
          "@_typeRef": "tEmployees",
        },
        expression: {
          "@_id": "_BC626016-B599-47A1-ABD6-67A2C7F761CE",
          "@_label": "Employees by Dept",
          "@_typeRef": "tEmployees",
          __$$element: "literalExpression",
          text: { __$$text: "employees[item.Dept = dept]" },
        },
      },
      {
        variable: {
          "@_id": "_C772ADED-2499-4AF1-9C99-63AA17901EBB",
          "@_name": "Employees with Knowledge",
          "@_typeRef": "tEmployees",
        },
        expression: {
          "@_id": "_3543AF00-0F90-47C4-BCBF-6E3B28A08BD7",
          "@_label": "Employees with Knowledge",
          "@_typeRef": "tEmployees",
          __$$element: "literalExpression",
          text: {
            __$$text:
              "for e in Employees by Dept return \n\tif (list contains(e.Knowledges, knowledge))\n\t\tthen e\n\t\telse null",
          },
        },
      },
      {
        expression: {
          __$$element: "literalExpression",
          "@_id": "_049ECA86-1971-48B7-86FF-7E89B399074A",
          "@_label": "Result Expression",
          text: {
            __$$text: "Employees with Knowledge[item != null]",
          },
        },
      },
    ],
  },
};

export const findEmployeesByKnowledgeWidthsById = new Map<string, number[]>([
  ["_243056C9-28EA-4EF2-8510-6F3BC6A5E5DC", [604]],
  ["_1B96445B-523F-49B3-99BE-308A2F3CFBA5", [204, 400]],
  ["_BC626016-B599-47A1-ABD6-67A2C7F761CE", [400]],
  ["_3543AF00-0F90-47C4-BCBF-6E3B28A08BD7", [400]],
  ["_049ECA86-1971-48B7-86FF-7E89B399074A", [400]],
]);

export const findEmployeesExpression: InvocationExpressionDefinition = {
  __$$element: "invocation",
  "@_id": "_8001CBC8-8DE4-4DBF-8B02-7FBFC582B136",
  "@_label": "Find employees",
  "@_typeRef": "tEmployees",
  expression: {
    __$$element: "literalExpression",
    "@_id": "_173AE705-5374-42AA-B4A4-085FA700B76F",
    text: {
      __$$text: "Find employee by knowledge",
    },
  },
  binding: [
    {
      parameter: {
        "@_id": "_D51D238A-A302-435C-BEED-25810C4FD922",
        "@_name": "employees",
        "@_typeRef": "tEmployees",
      },
      expression: {
        "@_id": "_AF546AE9-D0D0-4090-80FC-3E9EB9FECB07",
        "@_label": "employees",
        "@_typeRef": "tEmployees",
        __$$element: "literalExpression",
        text: {
          __$$text: "Employees",
        },
      },
    },
    {
      parameter: {
        "@_id": "_2F962A92-5918-42F9-8237-30F7795B7CA0",
        "@_name": "knowledge",
        "@_typeRef": "tKnowledge",
      },
      expression: {
        "@_id": "_04265B8B-F45E-47F1-A167-03880698029D",
        "@_label": "knowledge",
        "@_typeRef": "tKnowledge",
        __$$element: "literalExpression",
        text: {
          __$$text: "Knowledge",
        },
      },
    },
    {
      parameter: {
        "@_id": "_D97531A0-AF31-4E5C-9400-82B93B8B17D9",
        "@_name": "dept",
        "@_typeRef": "tDept",
      },
      expression: {
        "@_id": "_6A2F59F4-BE08-45F3-B3B2-7619C5931CC5",
        "@_label": "dept",
        "@_typeRef": "tDept",
        __$$element: "literalExpression",
        text: {
          __$$text: "Dept",
        },
      },
    },
  ],
};

export const findEmployeesWidthsById = new Map<string, number[]>([
  ["_8001CBC8-8DE4-4DBF-8B02-7FBFC582B136", [120, 100]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Employees: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: employeesExpression,
    dataTypes: findEmployeesDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: employeesWidthsById,
  },
};

export const FindEmployeesByKnowledge: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: findEmployeesByKnowledgeExpression,
    dataTypes: findEmployeesDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: findEmployeesByKnowledgeWidthsById,
  },
};

export const FindByEmployees: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: findEmployeesExpression,
    dataTypes: findEmployeesDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
    widthsById: findEmployeesWidthsById,
  },
};
