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
import {
  FeelSyntacticSymbolNature,
  FeelVariable,
  FeelVariables,
  FeelVariablesParser,
} from "@kie-tools/dmn-feel-antlr4-parser";

import * as Monaco from "@kie-tools-core/monaco-editor";

describe("Semantic Tokens Provider", () => {
  const tokensFoundByParser: FeelVariable[] = [];
  const mockedParser = FeelVariablesParser as jest.Mocked<typeof FeelVariablesParser>;
  jest
    .spyOn(mockedParser.prototype, "parse")
    .mockReturnValue({ availableSymbols: [], feelVariables: tokensFoundByParser });

  const mockedFeelVariables: jest.Mocked<typeof FeelVariables> = FeelVariables as jest.Mocked<typeof FeelVariables>;
  jest.spyOn(mockedFeelVariables.prototype, "parser", "get").mockReturnValue(mockedParser.prototype);

  const cancellationTokenMock = {
    isCancellationRequested: false,
    onCancellationRequested: jest.fn().mockImplementation(),
  };

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
      expression: 'this is a very long expression + "bar"',
      parsedTokens: [new FeelVariable(0, 30, 0, 0, FeelSyntacticSymbolNature.GlobalVariable, "")],
      expected: [[0, 0, 30, 5, 0]],
    },
    {
      expression: `This is a variable with a very long 
name to reproduce the issue thousand 
one hundred and seventy-eight + "bar"`,
      parsedTokens: [new FeelVariable(0, 104, 0, 2, FeelSyntacticSymbolNature.GlobalVariable, "")],
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
      parsedTokens: [
        new FeelVariable(12, 106, 1, 4, FeelSyntacticSymbolNature.GlobalVariable, ""),
        new FeelVariable(138, 102, 4, 4, FeelSyntacticSymbolNature.GlobalVariable, ""),
      ],
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
      parsedTokens: [new FeelVariable(0, 108, 0, 6, FeelSyntacticSymbolNature.GlobalVariable, "")],
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
      parsedTokens: [new FeelVariable(8, 194, 0, 1, FeelSyntacticSymbolNature.GlobalVariable, "")],
      expected: [
        [0, 8, 89, 5, 0],
        [1, 0, 104, 5, 0],
      ],
    },
    {
      expression: `This is a variable with a very long name to
reproduce the issue thousand one hundred and seventy-eight + "bar"`,
      parsedTokens: [new FeelVariable(0, 102, 0, 1, FeelSyntacticSymbolNature.GlobalVariable, "")],
      expected: [
        [0, 0, 43, 5, 0],
        [1, 0, 58, 5, 0],
      ],
    },
    {
      expression: `VeryLongVariableWithoutSpaces
ThatShouldFailWhenBreakLine`,
      parsedTokens: [
        new FeelVariable(0, 57, 0, 1, FeelSyntacticSymbolNature.Unknown, "this is a very long expression"),
      ],
      expected: [
        [0, 0, 29, 7, 0],
        [1, 0, 27, 7, 0],
      ],
    },
  ])("multiline variables", async ({ expression, parsedTokens, expected }) => {
    tokensFoundByParser.splice(0, tokensFoundByParser.length);
    tokensFoundByParser.push(...parsedTokens);

    const modelMock = {
      getValue: jest.fn().mockReturnValue(expression),
      getLinesContent: jest.fn().mockReturnValue(expression.split("\n")),
    };

    const semanticTokensProvider = new SemanticTokensProvider(mockedFeelVariables.prototype, "someId", () => {});

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
