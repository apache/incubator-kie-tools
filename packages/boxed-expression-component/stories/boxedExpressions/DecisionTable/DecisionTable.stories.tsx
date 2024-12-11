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
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../../../src/BoxedExpressionEditor";
import { BoxedExpressionEditorStory, BoxedExpressionEditorStoryArgs } from "../../boxedExpressionStoriesWrapper";
import { Base as EmptyExpression } from "../../misc/Empty/EmptyExpression.stories";
import { DmnBuiltInDataType, generateUuid } from "../../../src/api";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../../../src/expressions/DecisionTableExpression/DecisionTableExpression";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
} from "../../../src/resizing/WidthConstants";

// More on how to set up stories at: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
const meta: Meta<BoxedExpressionEditorProps> = {
  title: "Boxed Expressions/Decision Table",
  component: BoxedExpressionEditor,
  includeStories: /^[A-Z]/,
};
export default meta;
type Story = StoryObj<BoxedExpressionEditorStoryArgs>;

// More on writing stories with args: https://storybook.js.org/docs/react/writing-stories/args
export const Base: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "decisionTable",
      "@_id": "_92929AE6-3BB5-4217-B66E-07614680971D",
      "@_label": "Expression Name",
      "@_hitPolicy": "UNIQUE",
      input: [
        {
          "@_id": generateUuid(),
          inputExpression: {
            "@_id": generateUuid(),
            text: { __$$text: "input-1" },
            "@_typeRef": undefined,
          },
        },
      ],
      output: [
        {
          "@_id": generateUuid(),
          "@_label": "output-1",
          "@_typeRef": undefined,
        },
      ],
      annotation: [
        {
          "@_name": "Annotations",
        },
      ],
      rule: [
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE } }],
          outputEntry: [
            {
              "@_id": generateUuid(),
              text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE },
            },
          ],
          annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
        },
      ],
    },
    widthsById: {
      "_92929AE6-3BB5-4217-B66E-07614680971D": [
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        DECISION_TABLE_INPUT_DEFAULT_WIDTH,
        DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
      ],
    },

    isResetSupportedOnRootExpression: true,
  },
};

export const EvaluationHits: Story = {
  render: (args) =>
    BoxedExpressionEditorStory({
      evaluationHitsCountById: new Map([["_1FA12B9F-288C-42E8-B77F-BE2D3702B7B8", 30]]),
    }),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "decisionTable",
      "@_id": "_92929AE6-3BB5-4217-B66E-07614680971D",
      "@_label": "Expression Name",
      "@_hitPolicy": "UNIQUE",
      input: [
        {
          "@_id": generateUuid(),
          inputExpression: {
            "@_id": generateUuid(),
            text: { __$$text: "input-1" },
            "@_typeRef": undefined,
          },
        },
      ],
      output: [
        {
          "@_id": generateUuid(),
          "@_label": "output-1",
          "@_typeRef": undefined,
        },
      ],
      annotation: [
        {
          "@_name": "Annotations",
        },
      ],
      rule: [
        {
          "@_id": "_1FA12B9F-288C-42E8-B77F-BE2D3702B7B8",
          inputEntry: [
            { "@_id": generateUuid(), text: { __$$text: "E" } },
            { "@_id": generateUuid(), text: { __$$text: "E" } },
          ],
          outputEntry: [
            { "@_id": generateUuid(), text: { __$$text: "aaa" } },
            { "@_id": generateUuid(), text: { __$$text: "aaa" } },
            { "@_id": generateUuid(), text: { __$$text: "aaa" } },
          ],
          annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
        },
        {
          "@_id": "_1FA12B9F-288C-42E8-B77F-BE2D3702B7B4",
          inputEntry: [
            { "@_id": generateUuid(), text: { __$$text: "E" } },
            { "@_id": generateUuid(), text: { __$$text: "E" } },
          ],
          outputEntry: [
            { "@_id": generateUuid(), text: { __$$text: "aaa" } },
            { "@_id": generateUuid(), text: { __$$text: "aaa" } },
            { "@_id": generateUuid(), text: { __$$text: "aaa" } },
          ],
          annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
        },
      ],
    },
    widthsById: {
      "_92929AE6-3BB5-4217-B66E-07614680971D": [
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        DECISION_TABLE_INPUT_DEFAULT_WIDTH,
        DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
      ],
    },

    isResetSupportedOnRootExpression: true,
  },
};

export const Readonly: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "decisionTable",
      "@_id": "_92929AE6-3BB5-4217-B66E-07614680971D",
      "@_label": "Expression Name",
      "@_hitPolicy": "UNIQUE",
      input: [
        {
          "@_id": generateUuid(),
          inputExpression: {
            "@_id": generateUuid(),
            text: { __$$text: "input-1" },
            "@_typeRef": undefined,
          },
        },
      ],
      output: [
        {
          "@_id": generateUuid(),
          "@_label": "output-1",
          "@_typeRef": undefined,
        },
      ],
      annotation: [
        {
          "@_name": "Annotations",
        },
      ],
      rule: [
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE } }],
          outputEntry: [
            {
              "@_id": generateUuid(),
              text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE },
            },
          ],
          annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
        },
      ],
    },
    widthsById: {
      "_92929AE6-3BB5-4217-B66E-07614680971D": [
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        DECISION_TABLE_INPUT_DEFAULT_WIDTH,
        DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
      ],
    },

    isResetSupportedOnRootExpression: true,
    isReadOnly: true,
  },
};

export const Discount: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "decisionTable",
      "@_id": "_92929AE6-3BB5-4217-B66E-07614680971D",
      "@_label": "Discount",
      "@_typeRef": DmnBuiltInDataType.Number,
      "@_hitPolicy": "PRIORITY",
      input: [
        {
          "@_id": generateUuid(),
          inputExpression: {
            text: { __$$text: "Customer.Category" },
            "@_id": generateUuid(),
            "@_typeRef": "tCategory",
          },
        },
      ],
      output: [
        {
          "@_id": generateUuid(),
          "@_label": "Discount",
          "@_typeRef": DmnBuiltInDataType.Number,
        },
      ],
      annotation: [
        {
          "@_name": "Annotations",
        },
      ],
      rule: [
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: `"New Client"` } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: "0.15" } }],
          annotationEntry: [],
        },
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: `"Promo"` } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: "0.25" } }],
          annotationEntry: [],
        },
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: `"Student"` } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: "0.30" } }],
          annotationEntry: [],
        },
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: `"Default"` } }],
          outputEntry: [{ "@_id": generateUuid(), text: { __$$text: "0" } }],
          annotationEntry: [],
        },
      ],
    },
    isResetSupportedOnRootExpression: false,
    widthsById: {
      "_92929AE6-3BB5-4217-B66E-07614680971D": [
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        120,
        DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
      ],
    },
  },
};

export const Nested: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "context",
      "@_id": "_92929AE6-3BB5-4217-B66E-07614680971D",
      "@_label": "Expression Name",
      contextEntry: [
        {
          "@_id": generateUuid(),
          variable: {
            "@_id": generateUuid(),
            "@_name": "ContextEntry-1",
            description: { __$$text: "ContextEntry-1" },
          },
          expression: {
            __$$element: "decisionTable",
            "@_id": "_5B31AAB6-BD52-4654-88C2-541F687FE5B4",
            "@_label": "Expression Name",
            "@_hitPolicy": "UNIQUE",
            input: [
              {
                "@_id": generateUuid(),
                inputExpression: {
                  "@_id": generateUuid(),
                  text: { __$$text: "input-1" },
                },
              },
            ],
            output: [
              {
                "@_id": generateUuid(),
                "@_label": "output-1",
              },
            ],
            annotation: [
              {
                "@_name": "Annotations",
              },
            ],
            rule: [
              {
                "@_id": generateUuid(),
                inputEntry: [
                  {
                    "@_id": generateUuid(),
                    text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE },
                  },
                ],
                outputEntry: [
                  {
                    "@_id": generateUuid(),
                    text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE },
                  },
                ],
                annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
              },
            ],
          },
        },
      ],
    },
    widthsById: {
      "_92929AE6-3BB5-4217-B66E-07614680971D": [CONTEXT_ENTRY_VARIABLE_MIN_WIDTH],

      "_5B31AAB6-BD52-4654-88C2-541F687FE5B4": [
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        DECISION_TABLE_INPUT_DEFAULT_WIDTH,
        DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
      ],
    },
    isResetSupportedOnRootExpression: false,
  },
};

export const UndefinedWidths: Story = {
  render: (args) => BoxedExpressionEditorStory(),
  parameters: { exclude: ["dataTypes", "beeGwtService", "pmmlDocuments"] },
  args: {
    ...EmptyExpression.args,
    expression: {
      __$$element: "decisionTable",
      "@_id": "_92929AE6-3BB5-4217-B66E-07614680971D",
      "@_label": "Expression Name",
      "@_hitPolicy": "UNIQUE",
      input: [
        {
          "@_id": generateUuid(),
          inputExpression: {
            "@_id": generateUuid(),
            text: { __$$text: "input-1" },
            "@_typeRef": undefined,
          },
        },
      ],
      output: [
        {
          "@_id": generateUuid(),
          "@_label": "output-1",
          "@_typeRef": undefined,
        },
      ],
      annotation: [
        {
          "@_name": "Annotations",
        },
      ],
      rule: [
        {
          "@_id": generateUuid(),
          inputEntry: [{ "@_id": generateUuid(), text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE } }],
          outputEntry: [
            {
              "@_id": generateUuid(),
              text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE },
            },
          ],
          annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
        },
      ],
    },
    widthsById: {
      "_92929AE6-3BB5-4217-B66E-07614680971D": [],
    },

    isResetSupportedOnRootExpression: true,
  },
};
