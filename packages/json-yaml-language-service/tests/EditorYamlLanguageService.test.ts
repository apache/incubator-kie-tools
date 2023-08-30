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

import { FileLanguage } from "@kie-tools/json-yaml-language-service/dist/api";
import {
  createNewFileCodeLens,
  createOpenCompletionItemsCodeLenses,
  EditorLanguageServiceCodeLenses,
  EditorLanguageServiceCodeLensesFunctionsArgs,
  EditorYamlCodeCompletionStrategy,
  isNodeUncompleted,
  parseYamlContent,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, Position } from "vscode-languageserver-types";
import { TestYamlLanguageService } from "./testLanguageService";
import { codeCompletionTester, ContentWithCursor, getStartNodeValuePositionTester, treat, trim } from "./testUtils";

const documentUri = "test.sw.yaml";
const editorYamlLanguageServiceArgs = {
  fs: {},
  codeCompletionStrategy: new EditorYamlCodeCompletionStrategy(),
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

describe("YamlCodeCompletionStrategy", () => {
  describe("getStartNodeValuePosition", () => {
    const codeCompletionStrategy = new EditorYamlCodeCompletionStrategy();
    const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

    describe("string value", () => {
      test("no quotes", () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          name: Greeting workflow`,
            path: ["name"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 1, character: 16 });
      });

      test("single quotes", () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          name: 'Greeting workflow'`,
            path: ["name"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 1, character: 17 });
      });

      test("double quotes", () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          name: "Greeting workflow"`,
            path: ["name"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 1, character: 17 });
      });
    });

    test("boolean value", async () => {
      expect(
        getStartNodeValuePositionTester({
          content: `---
          end: true`,
          path: ["end"],
          documentUri,
          ls,
          codeCompletionStrategy,
        })
      ).toStrictEqual({ line: 1, character: 15 });
    });

    describe("arrays", () => {
      test("single line declaration using JSON format", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          functions: []`,
            path: ["functions"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 1, character: 22 });
      });

      test("single line declaration using JSON format / with one element", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          functions: [
            {
                "name": "getGreetingFunction",
                "operation": "openapi.yml#getGreeting"
              }

          ]`,
            path: ["functions"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 1, character: 22 });
      });

      test("YAML format declaration", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          functions:
          - name: getGreetingFunction`,
            path: ["functions"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 2, character: 10 });
      });

      test("YAML format declaration / with one function / with content before and after", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          name: Greeting workflow
          functions:
          - name: getGreetingFunction
            operation: openapi.yml#getGreeting
          states: []`,
            path: ["functions"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 3, character: 10 });
      });
    });

    describe("objects", () => {
      test("single line declaration using JSON format", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          data: {}`,
            path: ["data"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 1, character: 17 });
      });

      test("two lines declaration using JSON format", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
            data: {
              "language": "Portuguese"
            }`,
            path: ["data"],
            documentUri,
            ls,
            codeCompletionStrategy,
          })
        ).toStrictEqual({ line: 1, character: 19 });
      });

      test("YAML format declaration", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          data:
            language: Portuguese `,
            path: ["data"],
            codeCompletionStrategy,
            documentUri,
            ls,
          })
        ).toStrictEqual({ line: 2, character: 12 });
      });

      test("YAML format declaration / with one attribute / with content before and after", async () => {
        expect(
          getStartNodeValuePositionTester({
            content: `---
          name: GreetInPortuguese
          data:
            language: Portuguese
          transition: GetGreeting`,
            path: ["data"],
            codeCompletionStrategy,
            documentUri,
            ls,
          })
        ).toStrictEqual({ line: 3, character: 12 });
      });
    });
  });
});

describe("TEST LS YAML", () => {
  describe("parsing content", () => {
    test("parsing content with empty function array", async () => {
      const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

      const { content } = trim(`
---
functions: []
`);

      const rootNode = parseYamlContent(content);

      expect(rootNode).not.toBeUndefined();
      expect(rootNode!.type).toBe("object");
      expect(rootNode!.children).toHaveLength(1);
      expect(rootNode!.children![0].type).toBe("property");
      expect(rootNode!.children![0].children).toHaveLength(2);
      expect(rootNode!.children![0].children![0]).not.toBeUndefined();
      expect(rootNode!.children![0].children![0].value).toBe("functions");
      expect(rootNode!.children![0].children![1]).not.toBeUndefined();
      expect(rootNode!.children![0].children![1].type).toBe("array");
      expect(rootNode!.children![0].children![1].children).toEqual([]);

      expect(rootNode).toMatchSnapshot();
    });

    test("parsing content with one state and one function", () => {
      const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

      const { content } = trim(`
---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
"states":
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: 'myFunc'
`);

      const rootNode = parseYamlContent(content);

      expect(rootNode).not.toBeUndefined();
      expect(rootNode!.type).toBe("object");
      expect(rootNode!.children).toHaveLength(2);
      expect(rootNode!.children![0].type).toBe("property");
      expect(rootNode!.children![0].colonOffset).toBe(13);
      expect(rootNode!.children![0].children).toHaveLength(2);
      expect(rootNode!.children![0].children![0]).not.toBeUndefined();
      expect(rootNode!.children![0].children![0].value).toBe("functions");
      expect(rootNode!.children![0].children![0].colonOffset).toBeUndefined();
      expect(rootNode!.children![0].children![1]).not.toBeUndefined();
      expect(rootNode!.children![0].children![1].type).toBe("array");
      expect(rootNode!.children![1].colonOffset).toBe(91);
      expect(rootNode!.children![1].children![1].children![0].children![0].children![1].value).toBe("testState");

      expect(rootNode).toMatchSnapshot();
    });

    test("parsing content with an incomplete functionRef object", async () => {
      const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

      const { content } = trim(`
---
states:
- name: testState1
  transition: end
  actions:
  - name: testStateAction1
    functionRef: 
      a
  - name: testStateAction2
    functionRef:
      refName: myFunc
`);

      const rootNode = parseYamlContent(content);

      expect(rootNode).not.toBeUndefined();
      expect(
        rootNode?.children?.[0].children?.[1].children?.[0].children?.[2].children?.[1].children?.[0].children?.[1]
          .children?.[1].value
      ).toBe("a");
    });

    test("parsing content with an incomplete operation property", async () => {
      const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

      const { content } = trim(`---
functions:
- name: testRelativeFunction1
  operation: `);

      const rootNode = parseYamlContent(content);

      expect(rootNode).not.toBeUndefined();
      expect(rootNode?.children?.[0].children?.[1].children?.[0].children?.[1].children?.[0].value).toBe("operation");
    });
  });

  describe("isNodeUncompleted", () => {
    const parseContentTester = (contentToParse: ContentWithCursor) => {
      const { content, cursorPosition } = treat(contentToParse);

      const rootNode = parseYamlContent(content);
      const doc = TextDocument.create(documentUri, FileLanguage.YAML, 0, content);
      const cursorOffset = doc.offsetAt(cursorPosition);

      return isNodeUncompleted({
        content,
        uri: documentUri,
        rootNode: rootNode!,
        cursorOffset,
      });
    };

    test("with space after property name / without same level content after", async () => {
      expect(
        parseContentTester(`---
functions: 🎯`)
      ).toBeTruthy();
    });

    test("with space after property name / with same level content after", async () => {
      expect(
        parseContentTester(`---
functions: 🎯
states:
- name: testState`)
      ).toBeTruthy();
    });

    test("inside double quotes", async () => {
      expect(
        parseContentTester(`---
functions: "🎯"`)
      ).toBeFalsy();
    });

    test("inside single quotes", async () => {
      expect(
        parseContentTester(`---
functions: '🎯'`)
      ).toBeFalsy();
    });

    test("defining an array / without same level content after", async () => {
      expect(
        parseContentTester(`---
functions: 
  - 🎯`)
      ).toBeFalsy();
    });

    test("defining an array / with same level content after", async () => {
      expect(
        parseContentTester(`---
functions: 
  - 🎯
states:
- name: testState`)
      ).toBeFalsy();
    });

    test("defining an object / without same level content after", async () => {
      expect(
        parseContentTester(`---
functions: 
  🎯`)
      ).toBeFalsy();
    });

    test("defining an object / with same level content after", async () => {
      expect(
        parseContentTester(`---
functions: 
  🎯
states:
- name: testState`)
      ).toBeFalsy();
    });
  });

  test("basic", async () => {
    const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

    const completionItems = await ls.getCompletionItems({
      uri: documentUri,
      content: "---",
      cursorPosition: { line: 0, character: 0 },
      cursorWordRange: { start: { line: 0, character: 0 }, end: { line: 0, character: 0 } },
    });

    const codeLenses = await ls.getCodeLenses({
      uri: documentUri,
      content: "---",
    });

    expect(completionItems).toStrictEqual([]);
    expect(codeLenses).toStrictEqual([]);
  });

  describe("code lenses", () => {
    describe("emtpy file code lenses", () => {
      test.each([
        ["empty object / using JSON format", `{}`],
        ["empty object / with cursor before the object / using JSON format", `{}`],
        ["empty object / with cursor after the object / using JSON format", `{}`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(0);
      });

      test.each([
        ["total empty file", ``],
        ["empty file with a newline before the cursor", `\n`],
        ["empty file with a newline after the cursor", `\n`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

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
      test("add function", async () => {
        const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

        const { content } = trim(`functions:\n- `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 1, character: 0 }, end: { line: 1, character: 0 } },
          command: {
            title: "+ Add function...",
            command: "editor.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 0, line: 1 } }],
          },
        } as CodeLens);
      });

      test("add function - using JSON format", async () => {
        const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

        const { content } = trim(`functions: [] `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(0);
      });
    });
  });

  describe("code completion", () => {
    const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

    describe("empty file completion", () => {
      test.each([
        ["empty object / using JSON format", `{🎯}`],
        ["empty object / with cursor before the object / using JSON format", `🎯{}`],
        ["empty object / with cursor after the object / using JSON format", `{}🎯`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems).toHaveLength(0);
      });

      test.each([
        ["total empty file", `🎯`],
        ["empty file with a newline before the cursor", `\n🎯`],
        ["empty file with a newline after the cursor", `🎯\n`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe.each([["functions"]])(`%s completion`, (nodeName: string) => {
      describe("using JSON format", () => {
        test.each([
          [`using JSON format`, `${nodeName}: [🎯]`],
          [`using JSON format / before a ${nodeName}`, `${nodeName}: [🎯{ }]`],
          [`using JSON format / after a ${nodeName}`, `${nodeName}: [{  },🎯]`],
          [`pointing before the array of ${nodeName} / using JSON format`, `${nodeName}: 🎯[] `],
          [
            `pointing before the array of ${nodeName} / with extra space after ':' / using JSON format`,
            `${nodeName}: 🎯 [] `,
          ],
          [`pointing after the array of ${nodeName} / using JSON format`, `${nodeName}: []🎯 `],
          [`pointing inside an object of the array of ${nodeName} / using JSON format`, `${nodeName}: [ {🎯 }]`],
        ])(`%s`, async (_description, content: ContentWithCursor) => {
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems).toHaveLength(0);
        });
      });

      test.each([
        [`empty completion items`, `${nodeName}:\n-🎯`],
        [`empty completion items / with extra space`, `${nodeName}:\n- 🎯`],
        [`add at the end`, `${nodeName}:\n- name: itemName\n- 🎯`],
        [`add at the beginning`, `${nodeName}:\n- 🎯\n- name: itemName`],
        [`add in the middle`, `${nodeName}:\n- name: itemName1\n- 🎯\n- name: itemName2`],
        [`add in the middle / without dash character`, `${nodeName}:\n- name: itemName1\n🎯\n- name: itemName2`],
        [`add at the beginning, using the code lenses`, `${nodeName}:\n🎯- name: itemName`],
        [`add at the beginning / with extra indentation / using the code lenses`, `${nodeName}:\n  🎯- name: itemName`],
        [
          `add at the beginning / with double extra indentation / using the code lenses`,
          `${nodeName}:\n    🎯- name: itemName`,
        ],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("start completion", () => {
      test.each([
        ["not in quotes / without space after property name", `🎯`],
        ["not in quotes", ` 🎯 `],
        ["inside single quotes", ` '🎯'`],
        ["inside double quotes", ` "🎯"`],
      ])("%s", async (_description, nodeValue) => {
        const content = `start:${nodeValue}
states:
- name: GreetInEnglish
- name: GreetInSpanish
` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });
  });

  describe("diagnostic", () => {
    const ls = new TestYamlLanguageService(editorYamlLanguageServiceArgs);

    describe("using JSON format", () => {
      test.each([
        [
          "unclosed brackets",
          `id: jsongreet
version: '1.0'
specVersion: '0.8'
name: Greeting workflow
expressionLang: jsonpath
description: JSON based greeting workflow
start: HandleNewGreet
functions: [{
      "name": "printMessage",
      "type": "custom",
      "operation": "sysout"
  }
states:
- name: 'HandleNewGreet'
  type: inject
  data: {}
  end: true`,
        ],
      ])("%s", async (_description, content) => {
        const diagnostic = await ls.getDiagnostics({ uriPath: documentUri, content });

        expect(diagnostic.length).toMatchSnapshot();
        expect(diagnostic).toMatchSnapshot();
      });
    });

    test.each([
      ["empty file", ``],
      [
        "missing state type",
        `id: hello_world
specVersion: "0.1"
start: Inject Hello World
states:
  - name: Inject Hello World
    duration: "PT15M"
    end: true`,
      ],
      [
        "wrong states type",
        `id: hello_world
specVersion: "0.1"
states: Wrong states type`,
      ],
      [
        "wrong start state",
        `id: hello_world
specVersion: "0.1"
start: Wrong state name
states:
  - name: Inject Hello World
    type: inject
    data: {}
    end: true`,
      ],
      [
        "valid",
        `id: hello_world
specVersion: "0.1"
start: Inject Hello World
states:
  - name: Inject Hello World
    type: inject
    data: {}
    end: true`,
      ],
    ])("%s", async (_description, content) => {
      const diagnostic = await ls.getDiagnostics({ uriPath: documentUri, content });

      expect(diagnostic.length).toMatchSnapshot();
      expect(diagnostic).toMatchSnapshot();
    });
  });
});
