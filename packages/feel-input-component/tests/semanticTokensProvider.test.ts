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

import { SemanticTokensProvider } from "@kie-tools/feel-input-component/dist/semanticTokensProvider";
import { BuiltInTypes, DmnDefinitions, FeelIdentifiers } from "@kie-tools/dmn-feel-antlr4-parser";

import * as Monaco from "@kie-tools-core/monaco-editor";
import { Element } from "@kie-tools/feel-input-component/dist/themes/Element";
import * as fs from "fs";
import * as path from "path";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

describe("Semantic Tokens Provider", () => {
  const cancellationTokenMock = {
    isCancellationRequested: false,
    onCancellationRequested: jest.fn().mockImplementation(),
  };
  describe("long variables with and without line breaks", () => {
    const knownVariable =
      "This is a variable with a very long name to reproduce the issue thousand one hundred and seventy-eight";

    /**
     * The 'parsedTokens' are the tokens that parser should found in the provided 'expression'.
     * The 'expected' are the Monaco Semantic Tokens that we are expecting to pass to Monaco to paint it on the screen.
     */
    test.each([
      {
        expression:
          'This is a variable with a very long name to reproduce the issue thousand one hundred and seventy-eight + "bar"',
        expected: [
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 102,
          }),
        ],
      },
      {
        expression: `This is a variable with a very long 
name to reproduce the issue thousand 
one hundred and seventy-eight + "bar"`,
        expected: [
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 36,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 37,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 29,
          }),
        ],
      },
      {
        expression: `"aaaaaa" + 
This is a variable with a very 
long name to
 reproduce the issue thousand 
 one hundred and seventy-eight + "bar" + "NICE" + This is a variable with a very long name to reproduce the issue thousand one hundred and seventy-eight`,
        expected: [
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 31,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 12,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 30,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 30,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: 50,
            tokenLength: 102,
          }),
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
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 44,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 10,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 10,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 9,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 12,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 4,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 13,
          }),
        ],
      },
      {
        expression: `"My " + This is a variable with a                         very long name to             reproduce
 the issue             thousand             one hundred               and                  seventy-eight + "bar"`,
        expected: [
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: 8,
            tokenLength: 89,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 104,
          }),
        ],
      },
      {
        expression: `This is a variable with a very long name to
reproduce the issue thousand one hundred and seventy-eight + "bar"`,
        expected: [
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 43,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 58,
          }),
        ],
      },
      {
        expression: `VeryLongVariableWithoutSpaces
ThatShouldFailWhenBreakLine`,
        expected: [
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 29,
            tokenType: Element.UnknownVariable,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 1,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: 27,
            tokenType: Element.UnknownVariable,
          }),
        ],
      },
    ])("multiline variables", async ({ expression, expected }) => {
      const modelMock = createModelMockForExpression(expression);

      const id = "expressionId";
      const dmnDefinitions = getDmnModelWithContextEntry({
        entry: {
          variable: knownVariable,
          expression: {
            id: id,
            value: expression,
          },
        },
      });

      const feelVariables = new FeelIdentifiers({
        _readonly_dmnDefinitions: dmnDefinitions,
        _readonly_externalDefinitions: new Map(),
      });
      const semanticTokensProvider = new SemanticTokensProvider(feelVariables, id, () => {});

      const semanticMonacoTokens = await semanticTokensProvider.provideDocumentSemanticTokens(
        modelMock as unknown as Monaco.editor.ITextModel,
        null,
        cancellationTokenMock
      );

      for (let i = 0; i < expected.length; i++) {
        expect(semanticMonacoTokens?.data[i]).toEqual(expected[i]);
      }
    });
  });

  describe("built-in types", () => {
    test.each([
      { type: BuiltInTypes.Number },
      { type: BuiltInTypes.Boolean },
      { type: BuiltInTypes.String },
      { type: BuiltInTypes.DaysAndTimeDuration },
      { type: BuiltInTypes.DateAndTime },
      { type: BuiltInTypes.YearsAndMonthsDuration },
      { type: BuiltInTypes.Time },
      { type: BuiltInTypes.Date },
    ])("should recognize built-in type '$type.name' properties as valid", async ({ type }) => {
      const myVariable = "myVar";
      const id = "someId";

      for (const dataType of type.properties.keys()) {
        const expression = `${myVariable}.${dataType}`;
        const modelMock = createModelMockForExpression(expression);

        const expected = [
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: 0,
            tokenLength: myVariable.length,
          }),
          ...getMonacoSemanticToken({
            startLineRelativeToPreviousLine: 0,
            startIndexRelativeToPreviousStartIndex: myVariable.length + 1, // +1 because of the dot after "myVar"
            tokenLength: dataType.length,
          }),
        ];

        const model = getDmnModelWithContextEntry({
          entry: {
            variable: myVariable,
            type: type.name,
            expression: {
              value: expression,
              id: id,
            },
          },
        });

        const feelVariables = new FeelIdentifiers({
          _readonly_dmnDefinitions: model,
          _readonly_externalDefinitions: new Map(),
        });
        const semanticTokensProvider = new SemanticTokensProvider(feelVariables, id, () => {});

        const semanticMonacoTokens = await semanticTokensProvider.provideDocumentSemanticTokens(
          modelMock as unknown as Monaco.editor.ITextModel,
          null,
          cancellationTokenMock
        );

        for (let i = 0; i < expected.length; i++) {
          expect(semanticMonacoTokens?.data[i]).toEqual(expected[i]);
        }
      }
    });
  });

  describe("variables inside Decision Tables", () => {
    const dmnModelWithIncludesPosixPathRelativeToTheTestFile =
      "../tests-data/variables-inside-decision-tables/modelWithInclude.dmn";
    const includedDmnModelPosixPathRelativeToTheTestFile =
      "../tests-data/variables-inside-decision-tables/included.dmn";
    const localModel = getDmnModelFromFilePath(dmnModelWithIncludesPosixPathRelativeToTheTestFile);
    const includedModel = getDmnModelFromFilePath(includedDmnModelPosixPathRelativeToTheTestFile);

    test("should recognize local nodes", async () => {
      const expression = "LocalInput + LocalDecision";
      const id = "_AEC3EEB0-8436-4767-A214-20FF5E5CB7BE";
      const modelMock = createModelMockForExpression(expression);

      const feelVariables = new FeelIdentifiers({
        _readonly_dmnDefinitions: localModel.definitions,
        _readonly_externalDefinitions: new Map([[includedModel.definitions["@_namespace"] ?? "", includedModel]]),
      });

      const semanticTokensProvider = new SemanticTokensProvider(feelVariables, id, () => {});

      const semanticMonacoTokens = await semanticTokensProvider.provideDocumentSemanticTokens(
        modelMock as unknown as Monaco.editor.ITextModel,
        null,
        cancellationTokenMock
      );

      const expected = [
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: 0,
          tokenLength: "LocalInput".length,
        }),
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: "LocalInput".length + 3, // +3 because of the " + "
          tokenLength: "LocalDecision".length,
        }),
      ];

      for (let i = 0; i < expected.length; i++) {
        expect(semanticMonacoTokens?.data[i]).toEqual(expected[i]);
      }
    });

    test("should recognize included nodes", async () => {
      const expression = "MyIncludedModel.MyDS(LocalInput) + MyIncludedModel.RemoteInput";
      const id = "_206131ED-0B81-4013-980A-4BB2539A53D0";
      const modelMock = createModelMockForExpression(expression);

      const feelVariables = new FeelIdentifiers({
        _readonly_dmnDefinitions: localModel.definitions,
        _readonly_externalDefinitions: new Map([[includedModel.definitions["@_namespace"] ?? "", includedModel]]),
      });

      const semanticTokensProvider = new SemanticTokensProvider(feelVariables, id, () => {});

      const semanticMonacoTokens = await semanticTokensProvider.provideDocumentSemanticTokens(
        modelMock as unknown as Monaco.editor.ITextModel,
        null,
        cancellationTokenMock
      );

      const expected = [
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: 0,
          tokenLength: "MyIncludedModel.MyDS".length,
          tokenType: Element.FunctionCall,
        }),
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: "MyIncludedModel.MyDS".length + 1, // +1 because of the "("
          tokenLength: "LocalInput".length,
        }),
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: "LocalInput".length + ") + ".length,
          tokenLength: "MyIncludedModel.RemoteInput".length,
        }),
      ];

      for (let i = 0; i < expected.length; i++) {
        expect(semanticMonacoTokens?.data[i]).toEqual(expected[i]);
      }
    });

    test("should recognize included nodes mixed with included nodes", async () => {
      const expression = "MyIncludedModel.MyDS(LocalInput) + MyIncludedModel.RemoteInput + LocalInput + LocalDecision";
      const id = "_18832484-9481-49BC-BD40-927CB9872C6B";
      const modelMock = createModelMockForExpression(expression);

      const feelVariables = new FeelIdentifiers({
        _readonly_dmnDefinitions: localModel.definitions,
        _readonly_externalDefinitions: new Map([[includedModel.definitions["@_namespace"] ?? "", includedModel]]),
      });

      const semanticTokensProvider = new SemanticTokensProvider(feelVariables, id, () => {});

      const semanticMonacoTokens = await semanticTokensProvider.provideDocumentSemanticTokens(
        modelMock as unknown as Monaco.editor.ITextModel,
        null,
        cancellationTokenMock
      );

      const expected = [
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: 0,
          tokenLength: "MyIncludedModel.MyDS".length,
          tokenType: Element.FunctionCall,
        }),
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: "MyIncludedModel.MyDS".length + 1, // +1 because of the "("
          tokenLength: "LocalInput".length,
        }),
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: "LocalInput".length + ") + ".length,
          tokenLength: "MyIncludedModel.RemoteInput".length,
        }),
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: "MyIncludedModel.RemoteInput".length + " + ".length,
          tokenLength: "LocalInput".length,
        }),
        ...getMonacoSemanticToken({
          startLineRelativeToPreviousLine: 0,
          startIndexRelativeToPreviousStartIndex: "LocalInput".length + " + ".length,
          tokenLength: "LocalDecision".length,
        }),
      ];

      for (let i = 0; i < expected.length; i++) {
        expect(semanticMonacoTokens?.data[i]).toEqual(expected[i]);
      }
    });
  });
});

function getDmnModelWithContextEntry({
  entry,
}: {
  entry: {
    variable: string;
    type?: string;
    expression: {
      value: string;
      id: string;
    };
  };
}) {
  // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
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
                "@_name": entry.variable,
                "@_typeRef": entry.type,
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
                "@_id": entry.expression.id,
                text: { __$$text: entry.expression.value },
              },
            },
          ],
        },
      },
    ],
  };

  return dmnDefinitions;
}

/**
 * Create a Monaco Semantic Token, which is an array with 5 positions.
 * 0 = The start line of the token RELATIVE TO THE PREVIOUS LINE
 * 1 = The start index of the token relative to the START of the previous token
 * 2 = The length of the token
 * 3 = The type of the token (GlobalVariable, Unknown, Function Parameter, etc.). It determines the color of the token
 * 4 = Token modifier. It's always zero since we don't have this feature.
 * @param args The token values.
 */
function getMonacoSemanticToken(args: {
  startLineRelativeToPreviousLine: number;
  startIndexRelativeToPreviousStartIndex: number;
  tokenLength: number;
  tokenType?: Element;
}) {
  return [
    args.startLineRelativeToPreviousLine,
    args.startIndexRelativeToPreviousStartIndex,
    args.tokenLength,
    args.tokenType ?? Element.Variable,
    0,
  ];
}

function createModelMockForExpression(expression: string) {
  return {
    getValue: jest.fn().mockReturnValue(expression),
    getLinesContent: jest.fn().mockReturnValue(expression.split("\n")),
  };
}

function getDmnModelFromFilePath(modelFilePosixPathRelativeToTheTestFile: string) {
  const { parser } = getMarshaller(
    fs.readFileSync(path.join(__dirname, modelFilePosixPathRelativeToTheTestFile), "utf-8"),
    { upgradeTo: "latest" }
  );
  return parser.parse();
}
