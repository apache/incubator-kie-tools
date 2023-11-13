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

import * as React from "react";
import type { Meta, StoryObj } from "@storybook/react";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/expressions";
import { BoxedExpressionEditorWrapper } from "../../boxedExpressionStoriesWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import { DmnBuiltInDataType, ExpressionDefinitionLogicType, generateUuid } from "../../../src/api";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "../../../src/expressions/RelationExpression";
import { CONTEXT_ENTRY_INFO_MIN_WIDTH } from "../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Relation",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
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
      logicType: ExpressionDefinitionLogicType.Relation,
      columns: [
        {
          id: generateUuid(),
          name: "column-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: RELATION_EXPRESSION_DEFAULT_VALUE,
            },
          ],
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const People: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...Base.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "People",
      dataType: "tPeople" as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Relation,
      columns: [
        {
          id: generateUuid(),
          name: "Name",
          dataType: DmnBuiltInDataType.String,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "Age",
          dataType: DmnBuiltInDataType.Number,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "Country",
          dataType: DmnBuiltInDataType.String,
          width: 100,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: '"Luiz"',
            },
            {
              id: generateUuid(),
              content: "30",
            },
            {
              id: generateUuid(),
              content: '"Brazil"',
            },
          ],
        },
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: '"Tiago"',
            },
            {
              id: generateUuid(),
              content: "29",
            },
            {
              id: generateUuid(),
              content: '"USA"',
            },
          ],
        },
      ],
    },
  },
};

export const Bigger: Story = {
  render: (args) => BoxedExpressionEditorWrapper(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlParams"] },
  args: {
    ...Base.args,
    expressionDefinition: {
      id: generateUuid(),
      name: "Expression Name",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Relation,
      columns: [
        {
          id: generateUuid(),
          name: "column-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "column-2",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "column-3",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
        {
          id: generateUuid(),
          name: "column-4",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
          ],
        },
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
          ],
        },
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
            {
              id: generateUuid(),
              content: "",
            },
          ],
        },
      ],
    },
  },
};

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Nested: Story = {
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
            logicType: ExpressionDefinitionLogicType.Relation,
            dataType: DmnBuiltInDataType.Undefined,
            columns: [
              {
                id: generateUuid(),
                name: "column-1",
                dataType: DmnBuiltInDataType.Undefined,
                width: 100,
              },
            ],
            rows: [
              {
                id: generateUuid(),
                cells: [
                  {
                    id: generateUuid(),
                    content: RELATION_EXPRESSION_DEFAULT_VALUE,
                  },
                ],
              },
            ],
          },
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};
