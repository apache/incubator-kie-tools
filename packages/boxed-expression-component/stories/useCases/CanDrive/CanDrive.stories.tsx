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

import type { Meta, StoryObj } from "@storybook/react";
import { DecisionTableExpressionDefinition, DmnBuiltInDataType } from "../../../src/api";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import {
  beeGwtService,
  BoxedExpressionEditorWrapper,
  dataTypes,
  pmmlDocuments,
} from "../../boxedExpressionStoriesWrapper";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../../src/resizing/WidthConstants";

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
  __$$element: "decisionTable",
  "@_id": "_21608B6A-1D9E-426D-86CF-B0CA7AB20D31",
  "@_label": "Can drive?",
  "@_typeRef": DmnBuiltInDataType.Boolean,
  "@_hitPolicy": "UNIQUE",
  annotation: [{ "@_name": "annotation-1" }],
  input: [
    {
      "@_id": "_E2F88F75-7FD2-4CED-A7AC-015619176072",
      inputExpression: {
        "@_id": "_092AF86E-5D8C-4299-9998-8CB3B2DDF711",
        "@_typeRef": DmnBuiltInDataType.Number,
        text: { __$$text: "Person.Age" },
      },
    },
    {
      "@_id": "_6B5D4A99-C73E-4DC2-8EEC-0158196F30D6",
      inputExpression: {
        "@_id": "_14C2BCC6-5059-40A2-9406-E206C5CB6AF5",
        "@_typeRef": DmnBuiltInDataType.String,
        text: { __$$text: "Person.Country" },
      },
    },
  ],
  output: [
    {
      "@_id": "_6E8F61D6-308A-4E37-A088-F0265CD73F1B",
      "@_name": "output-1",
      "@_typeRef": DmnBuiltInDataType.Boolean,
    },
  ],
  rule: [
    {
      "@_id": "_E81327B9-5E47-4C41-AFDC-73EDCA1D43B8",
      inputEntry: [
        { "@_id": "_7EFC4F27-09D0-4206-BD5C-1C72081D9A75", text: { __$$text: ">= 18" } },
        { "@_id": "_A4BD080A-6F76-463A-B8E8-8560C9A79319", text: { __$$text: '"Brazil"' } },
      ],
      outputEntry: [{ "@_id": "_F40832D4-8E1B-41CD-8A1E-2384EED9863F", text: { __$$text: "true" } }],
    },
    {
      "@_id": "_1FAF2352-4C74-4DB2-9844-5446E7A732C4",
      inputEntry: [
        { "@_id": "_34F94774-6D0F-474F-88FB-CBA164A25756", text: { __$$text: "< 18" } },
        { "@_id": "_FF4C4EED-7845-42F8-82A6-15F002A65F5B", text: { __$$text: '"Brazil"' } },
      ],
      outputEntry: [{ "@_id": "_D06D4AE9-2246-4A36-8B97-FE18BB893ABD", text: { __$$text: "false" } }],
    },
    {
      "@_id": "_00B2707A-36FE-4927-B43C-CD36D71DD0AE",
      inputEntry: [
        { "@_id": "_EA0C9FA0-F959-46DA-97A7-98B935C00414", text: { __$$text: ">= 17" } },
        { "@_id": "_F8F56AA9-223B-4C66-B666-15127AA8AB86", text: { __$$text: '"England"' } },
      ],
      outputEntry: [{ "@_id": "_51179D92-A775-4776-9228-502A1746FB1F", text: { __$$text: "true" } }],
    },
    {
      "@_id": "_2DFCD2B1-FC83-4398-B482-3315D4809640",
      inputEntry: [
        { "@_id": "_974148CB-2372-4044-8CB4-AE0B36819829", text: { __$$text: "< 17" } },
        { "@_id": "_D7929881-A203-438F-BE79-81632351501D", text: { __$$text: '"England"' } },
      ],
      outputEntry: [{ "@_id": "_6ACB8D90-F867-4BCE-AC3A-1100CC2B778E", text: { __$$text: "false" } }],
    },
    {
      "@_id": "_5E3E58B6-AEA8-4580-8BAB-1848CAE675E8",
      inputEntry: [
        { "@_id": "_244C0142-2F80-4FC5-A02B-303B36A22D6A", text: { __$$text: ">= 16" } },
        { "@_id": "_FF24BDE6-72F0-49EB-9819-579040D5A0D7", text: { __$$text: '"US"' } },
      ],
      outputEntry: [{ "@_id": "_45E663A1-3B52-4672-97D6-05CA66390D20", text: { __$$text: "true" } }],
    },
    {
      "@_id": "_ABF08DF5-CD29-4453-8F73-612565D75E03",
      inputEntry: [
        { "@_id": "_C6E28F29-CD4A-4B1A-855C-6FAC51FA09D2", text: { __$$text: "< 16" } },
        { "@_id": "_A4F61557-2D30-485E-80B1-EE8B815C4FF5", text: { __$$text: '"US"' } },
      ],
      outputEntry: [{ "@_id": "_F2042E73-CDE0-4BAC-A219-8D7E52175D94", text: { __$$text: "false" } }],
    },
  ],
};

export const canDriveWidthsById = new Map<string, number[]>([
  ["_21608B6A-1D9E-426D-86CF-B0CA7AB20D31", [BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, 100, 100, 100]],
]);

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Expression: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  args: {
    expressionHolderId: "_00000000-0000-0000-0000-000000000000",
    expression: canDriveExpressionDefinition,
    dataTypes: findEmployeesDataTypes,
    beeGwtService,
    pmmlDocuments,
    isResetSupportedOnRootExpression: false,
  },
};
