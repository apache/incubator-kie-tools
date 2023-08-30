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
  createNewFileCodeLens,
  createOpenCompletionItemsCodeLenses,
  EditorJsonCodeCompletionStrategy,
  EditorLanguageServiceCodeLenses,
  EditorLanguageServiceCodeLensesFunctionsArgs,
  hasNodeComma,
  isOffsetAtLastChild,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { CodeLens, Position } from "vscode-languageserver-types";
import { TestJsonLanguageService } from "./testLanguageService";
import { codeCompletionTester, ContentWithCursor, getStartNodeValuePositionTester, treat, trim } from "./testUtils";

const documentUri = "test.sw.json";
const editorJsonLanguageServiceArgs = {
  fs: {},
  codeCompletionStrategy: new EditorJsonCodeCompletionStrategy(),
};

export const codeLenses: EditorLanguageServiceCodeLenses = {
  createNewFile: (): CodeLens[] => createNewFileCodeLens("Create a Serverless Workflow"),

  addFunction: (args: EditorLanguageServiceCodeLensesFunctionsArgs): CodeLens[] =>
    createOpenCompletionItemsCodeLenses({
      ...args,
      jsonPath: ["functions"],
      title: "+ Add function...",
      nodeType: "array",
    }),
};

describe("JsonCodeCompletionStrategy", () => {
  describe("getStartNodeValuePosition", () => {
    const codeCompletionStrategy = new EditorJsonCodeCompletionStrategy();
    const ls = new TestJsonLanguageService(editorJsonLanguageServiceArgs);

    test("string value", async () => {
      expect(
        getStartNodeValuePositionTester({
          content: `{
          "name": "Greeting workflow"
        }`,
          path: ["name"],
          codeCompletionStrategy,
          documentUri,
          ls,
        })
      ).toStrictEqual({ line: 1, character: 19 });
    });

    test("boolean value", async () => {
      expect(
        getStartNodeValuePositionTester({
          content: `{
          "end": true
        }`,
          path: ["end"],
          codeCompletionStrategy,
          documentUri,
          ls,
        })
      ).toStrictEqual({ line: 1, character: 18 });
    });

    describe("arrays", () => {
      test("single line declaration", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `{
          "functions": []
        }`,
            path: ["functions"],
            codeCompletionStrategy,
            documentUri,
            ls,
          })
        ).toStrictEqual({ line: 1, character: 24 });
      });

      test("two lines declaration", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `{
          "functions": 
          []
        }`,
            path: ["functions"],
            codeCompletionStrategy,
            documentUri,
            ls,
          })
        ).toStrictEqual({ line: 2, character: 11 });
      });

      test("single line declaration / with one function / with content before and after", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `{
          "name": "Greeting workflow",
          "functions": [
            {
                "name": "getGreetingFunction",
                "operation": "openapi.yml#getGreeting"
              }
          ],
          "states": []
        }`,
            path: ["functions"],
            codeCompletionStrategy,
            documentUri,
            ls,
          })
        ).toStrictEqual({ line: 2, character: 24 });
      });
    });

    describe("objects", () => {
      test("single line declaration", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `{
          "data": {}
        }`,
            path: ["data"],
            codeCompletionStrategy,
            documentUri,
            ls,
          })
        ).toStrictEqual({ line: 1, character: 19 });
      });

      test("two lines declaration", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `{
            "data": 
            {
              "language": "Portuguese"
            }
        }`,
            path: ["data"],
            codeCompletionStrategy,
            documentUri,
            ls,
          })
        ).toStrictEqual({ line: 2, character: 13 });
      });

      test("single line declaration / with one attribute / with content before and after", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `{
            "name": "GreetInPortuguese",
            "data": {
              "language": "Portuguese"
            },
            "transition": "GetGreeting"
        }`,
            path: ["data"],
            codeCompletionStrategy,
            documentUri,
            ls,
          })
        ).toStrictEqual({ line: 2, character: 21 });
      });
    });
  });
});

describe("TEST LS JSON", () => {
  describe("isOffsetAtLastChild", () => {
    test.each([
      ["empty content", `🎯`, true],
      ["empty content with spaces", ` 🎯 `, true],
      [
        "no value / with comma",
        `{
        "start": 🎯 ,
        "states" []
      }`,
        false,
      ],
      [
        "no value / without comma",
        `{
        "start": 🎯 
        "states" []
      }`,
        false,
      ],
      [
        "emtpy quotes / without comma",
        `{
        "start": "🎯" 
        "states" []
      }`,
        false,
      ],
      [
        "emtpy quotes",
        `{
        "start": "🎯" , 
        "states" []
      }`,
        false,
      ],
      [
        "empty quotes / with newline",
        `{
        "start": "🎯"
        , "states" []
      }`,
        false,
      ],
      [
        "at the end of the object",
        `{
        "states" [],
        "start": 🎯
      }`,
        true,
      ],
      [
        "at the beginning of the array",
        `{
        "states" [🎯 {}]
      }`,
        false,
      ],
      [
        "at the beginning of the array / with comma",
        `{
        "states" [🎯 , {}]
      }`,
        false,
      ],
      [
        "in the middle of the array",
        `{
        "states" [{},
        🎯 
        {}]
      }`,
        false,
      ],
      [
        "at the end of the array",
        `{
        "states" [{}, 🎯]
      }`,
        true,
      ],
    ])("%s", async (_description: string, contentWithCursor: ContentWithCursor, expectedValue: boolean) => {
      const { cursorOffset, content } = treat(contentWithCursor);
      expect(isOffsetAtLastChild(content, cursorOffset)).toBe(expectedValue);
    });
  });

  describe("hasNodeComma", () => {
    test.each([
      ["empty content", `🎯`, false],
      ["empty content with spaces", ` 🎯 `, false],
      [
        "no value / with comma",
        `{
        "start": 🎯 ,
        "states" []
      }`,
        true,
      ],
      [
        "no value / without comma",
        `{
        "start": 🎯 
        "states" []
      }`,
        false,
      ],
      [
        "emtpy quotes / without comma",
        `{
        "start": "🎯" 
        "states" []
      }`,
        false,
      ],
      [
        "emtpy quotes",
        `{
        "start": "🎯" , 
        "states" []
      }`,
        true,
      ],
      [
        "empty quotes / with newline",
        `{
        "start": "🎯"
        , "states" []
      }`,
        true,
      ],
      [
        "at the end of the object",
        `{
        "states" [],
        "start": 🎯
      }`,
        false,
      ],
      [
        "at the beginning of the array",
        `{
        "states" [🎯 {}]
      }`,
        false,
      ],
      [
        "at the beginning of the array / with comma",
        `{
        "states" [🎯 , {}]
      }`,
        true,
      ],
      [
        "in the middle of the array",
        `{
        "states" [{},
        🎯 
        {}]
      }`,
        false,
      ],
      [
        "at the end of the array",
        `{
        "states" [{}, 🎯]
      }`,
        false,
      ],
    ])("%s", async (_description: string, contentWithCursor: ContentWithCursor, expectedValue: boolean) => {
      const { cursorOffset, content } = treat(contentWithCursor);
      expect(hasNodeComma(content, cursorOffset)).toBe(expectedValue);
    });
  });

  test("basic", async () => {
    const ls = new TestJsonLanguageService(editorJsonLanguageServiceArgs);

    const completionItems = await ls.getCompletionItems({
      uri: documentUri,
      content: "{}",
      cursorPosition: { line: 0, character: 0 },
      cursorWordRange: { start: { line: 0, character: 0 }, end: { line: 0, character: 0 } },
    });

    const codeLenses = await ls.getCodeLenses({
      uri: documentUri,
      content: "{}",
    });

    expect(completionItems).toStrictEqual([]);
    expect(codeLenses).toStrictEqual([]);
  });

  describe("code lenses", () => {
    describe("emtpy file code lenses", () => {
      test.each([
        ["empty object", `{}`],
        ["empty object / with cursor before the object", `{}`],
        ["empty object / with cursor after the object", `{}`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const ls = new TestJsonLanguageService(editorJsonLanguageServiceArgs);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(0);
      });

      test.each([
        ["total empty file", ``],
        ["empty file with a newline before the cursor", `\n`],
        ["empty file with a newline after the cursor", `\n`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const ls = new TestJsonLanguageService(editorJsonLanguageServiceArgs);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });
        const position = Position.create(0, 0);

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: position, end: position },
          command: {
            title: "Create a Serverless Workflow",
            command: "editor.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: position }],
          },
        } as CodeLens);
      });
    });

    describe("functions code lenses", () => {
      test("add function - formatted", async () => {
        const ls = new TestJsonLanguageService(editorJsonLanguageServiceArgs);

        const { content } = trim(`
{
  "functions": []
}`);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
          command: {
            title: "+ Add function...",
            command: "editor.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
          },
        } as CodeLens);
      });

      test("add function - unformatted", async () => {
        const ls = new TestJsonLanguageService(editorJsonLanguageServiceArgs);

        const { content } = trim(`{"functions":[]}`);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toMatchSnapshot();
      });
    });
  });

  describe("code completion", () => {
    const ls = new TestJsonLanguageService(editorJsonLanguageServiceArgs);

    describe("empty file completion", () => {
      test.each([
        ["empty object", `{🎯}`],
        ["empty object / with cursor before the object", `🎯{}`],
        ["empty object / with cursor after the object", `{}🎯`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems).toHaveLength(0);
      });

      test.each([
        ["total empty file", `🎯`],
        ["empty file with a newline before the cursor", `\n🎯`],
        ["empty file with a newline after the cursor", `🎯\n`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe.each([["functions"]])(`%s completion`, (nodeName: string) => {
      test.each([
        ["pointing inside an object of the array", `{ "${nodeName}": [ {🎯} ] }`],
        ["pointing before the array", `{ "${nodeName}":🎯 [] }`],
        ["pointing before the array", `{ "${nodeName}": 🎯 [] }`],
        ["pointing after the array", `{ "${nodeName}": []🎯 }`],
        ["add into empty array", `{ "${nodeName}": [🎯] }`],
        ["add at the beginning of the array", `{ "${nodeName}": [🎯 {}] }`],
        ["add in the middle of the array", `{ "${nodeName}": [{}, 🎯 {} ] }`],
        ["add in the middle of the array / with comma at the end", `{ "${nodeName}": [{}, 🎯, {} ] }`],
        ["add at the end of the array", `{ "${nodeName}": [{}, 🎯 ] }`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("start completion", () => {
      test.each([
        ["not in quotes / without same level content after", ` 🎯`],
        ["not in quotes / with same level content after", ` 🎯\n        "id": "jsongreet"`],
        ["inside double quotes / without same level content after", ` "🎯"`],
        ["inside double quotes / with same level content after", ` "🎯"\n        "id": "jsongreet"`],
        [
          "inside double quotes / with same level content after / with comma at the end",
          ` "🎯",\n        "id": "jsongreet"`,
        ],
        [
          "inside double quotes / with same level content after / with spaces and comma at the end",
          ` "🎯"    ,\n        "id": "jsongreet"`,
        ],
      ])("%s", async (_description, nodeValue) => {
        const content = `{
  "states": [
    {
      "name": "GreetInEnglish"
    },
    {
      "name": "GreetInSpanish"
    }
  ],
  "start":${nodeValue}
}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });
  });

  describe("diagnostic", () => {
    const ls = new TestJsonLanguageService(editorJsonLanguageServiceArgs);

    test.each([
      ["empty file", ``],
      [
        "valid",
        `{
        "id": "hello_world", 
        "specVersion": "0.1",
        "start": "Inject Hello World", 
        "states": [ 
          {
            "name": "Inject Hello World",
            "type": "inject", 
            "data": {},
            "end":true
          }
        ]
      }`,
      ],
      [
        "unclosed brackets",
        `{
        "id": "hello_world", 
        "specVersion": "0.1",
        "start": "Inject Hello World", 
        "states": [ 
          {
            "name": "Inject Hello World",
            "type": "inject", 
            "data": {},
            "end": true
        ]
      }`,
      ],
      [
        "missing property value",
        `{
        "id": "hello_world", 
        "specVersion": "0.1",
        "start": "Inject Hello World", 
        "states": [ 
          {
            "name": "Inject Hello World",
            "duration": "PT15M",
            "end": true,
            "type": 
          }]
      }`,
      ],
      [
        "missing state type",
        `{
        "id": "hello_world", 
        "specVersion": "0.1",
        "start": "Inject Hello World", 
        "states": [ 
          {
            "name": "Inject Hello World",
            "duration": "PT15M",
            "end": true
          }
        ]
      }`,
      ],
      [
        "wrong states type",
        `{
        "id": "hello_world", 
        "specVersion": "0.1",
        "states": "Wrong states type"
      }`,
      ],
      [
        "wrong start state",
        `{
        "id": "hello_world", 
        "specVersion": "0.1",
        "start": "Wrong state name", 
        "states": [ 
          {
            "name": "Inject Hello World",
            "type": "inject", 
            "data": {},
            "end":true
          }
        ]}`,
      ],
    ])("%s", async (_description, content) => {
      const diagnostic = await ls.getDiagnostics({ uriPath: documentUri, content });

      expect(diagnostic.length).toMatchSnapshot();
      expect(diagnostic).toMatchSnapshot();
    });
  });
});
