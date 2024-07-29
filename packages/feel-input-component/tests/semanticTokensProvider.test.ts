/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { SemanticTokensProvider } from "../src/semanticTokensProvider";
import { FeelVariables, DmnDefinitions } from "@kie-tools/dmn-feel-antlr4-parser";

import * as Monaco from "@kie-tools-core/monaco-editor";

describe("Semantic Tokens Provider", () => {
  const cancellationTokenMock = {
    isCancellationRequested: false,
    onCancellationRequested: jest.fn().mockImplementation(),
  };

  const knownVariable =
    "This is a variable with a very long name to reproduce the issue thousand one hundred and seventy-eight";

  /**
   * The 'parsedTokens' are the tokens that parser should found in the provided 'expression'.
   * The 'expected' are the Monaco Semantic Tokens that we are expecting to pass to Monaco to paint it on the screen.
   *
   * Each Monaco Semantic Tokens is an array of 5 positions
   * 0 = The start line of the token RELATIVE TO THE PREVIOUS LINE
   * 1 = The start index of the token relative to the START of the previous token
   * 2 = The length of the token
   * 3 = The type of the token (GlobalVariable, Unknown, Function Parameter, etc.). It determines the color of the token
   * 4 = Token modifier. It's always zero since we don't have this feature.
   */
  test.each([
    {
      expression:
        'This is a variable with a very long name to reproduce the issue thousand one hundred and seventy-eight + "bar"',
      expected: [[0, 0, 102, 5, 0]],
    },
    {
      expression: `This is a variable with a very long 
name to reproduce the issue thousand 
one hundred and seventy-eight + "bar"`,
      expected: [
        [0, 0, 36, 5, 0],
        [1, 0, 37, 5, 0],
        [1, 0, 29, 5, 0],
      ],
    },
    {
      expression: `"aaaaaa" + 
This is a variable with a very 
long name to
 reproduce the issue thousand 
 one hundred and seventy-eight + "bar" + "NICE" + This is a variable with a very long name to reproduce the issue thousand one hundred and seventy-eight`,
      expected: [
        [1, 0, 31, 5, 0],
        [1, 0, 12, 5, 0],
        [1, 0, 30, 5, 0],
        [1, 0, 30, 5, 0],
        [0, 50, 102, 5, 0],
      ],
    },
    {
      expression: `This is a variable with a very long name to 
reproduce 
the issue 
thousand 
one hundred 
and 
seventy-eight + "bar`,
      expected: [
        [0, 0, 44, 5, 0],
        [1, 0, 10, 5, 0],
        [1, 0, 10, 5, 0],
        [1, 0, 9, 5, 0],
        [1, 0, 12, 5, 0],
        [1, 0, 4, 5, 0],
        [1, 0, 13, 5, 0],
      ],
    },
    {
      expression: `"My " + This is a variable with a                         very long name to             reproduce
 the issue             thousand             one hundred               and                  seventy-eight + "bar"`,
      expected: [
        [0, 8, 89, 5, 0],
        [1, 0, 104, 5, 0],
      ],
    },
    {
      expression: `This is a variable with a very long name to
reproduce the issue thousand one hundred and seventy-eight + "bar"`,
      expected: [
        [0, 0, 43, 5, 0],
        [1, 0, 58, 5, 0],
      ],
    },
    {
      expression: `VeryLongVariableWithoutSpaces
ThatShouldFailWhenBreakLine`,
      expected: [
        [0, 0, 29, 7, 0],
        [1, 0, 27, 7, 0],
      ],
    },
  ])("multiline variables", async ({ expression, expected }) => {
    const modelMock = {
      getValue: jest.fn().mockReturnValue(expression),
      getLinesContent: jest.fn().mockReturnValue(expression.split("\n")),
    };

    const id = "expressionId";
    const dmnDefinitions = getDmnModel({ knownVariable: knownVariable, expressionId: id, expression: expression });

    const feelVariables = new FeelVariables(dmnDefinitions, new Map());
    const semanticTokensProvider = new SemanticTokensProvider(feelVariables, id, () => {});

    const semanticMonacoTokens = await semanticTokensProvider.provideDocumentSemanticTokens(
      modelMock as unknown as Monaco.editor.ITextModel,
      null,
      cancellationTokenMock
    );

    const expectedSemanticMonacoTokens = expected.reduce((accumulator, value) => accumulator.concat(value), []);

    for (let i = 0; i < expectedSemanticMonacoTokens.length; i++) {
      expect(semanticMonacoTokens?.data[i]).toEqual(expectedSemanticMonacoTokens[i]);
    }
  });
});

function getDmnModel({
  knownVariable,
  expressionId,
  expression,
}: {
  knownVariable: string;
  expressionId: string;
  expression: string;
}) {
  const dmnDefinitions: DmnDefinitions = {
    "@_name": "DMN_3DB2E0BB-1A3A-4F52-A1F3-A0A5EBCB2C4E",
    "@_namespace": "dmn",
    drgElement: [
      {
        "@_id": "_5532AD11-7084-4A64-8838-239FBCF9BAF6",
        __$$element: "decision",
        "@_name": "Some Decision",
        expression: {
          "@_id": "_E15A1DB2-4621-45ED-825C-EBB8669095B2",
          __$$element: "context",
          contextEntry: [
            {
              "@_id": "_DD2E6BE8-B2AF-452C-A980-8937527FC3F2",
              variable: {
                "@_id": "_401F4E2D-442A-4A29-B6B9-906A121C6FC0",
                "@_name": knownVariable,
              },
              expression: {
                __$$element: "literalExpression",
                "@_id": "_785F4412-9BD3-4D5A-9A39-E113780390D7",
                text: { __$$text: "foo" },
              },
            },
            {
              "@_id": "_4AD499F5-BB34-4BD8-9B9D-DDA3D031AD97",
              variable: {
                "@_id": "_4C262520-1AD4-495F-A1BB-9BEB7BDD3841",
                "@_name": "Test var",
              },
              expression: {
                __$$element: "literalExpression",
                "@_id": expressionId,
                text: { __$$text: expression },
              },
            },
          ],
        },
      },
    ],
  };

  return dmnDefinitions;
}
