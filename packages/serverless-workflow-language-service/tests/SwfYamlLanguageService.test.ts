/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { FileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/api";
import {
  SwfYamlLanguageService,
  YamlCodeCompletionStrategy,
  isNodeUncompleted,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, CompletionItemKind, InsertTextFormat, Position } from "vscode-languageserver-types";
import { dump } from "yaml-language-server-parser";
import * as path from "path";
import * as fs from "fs";
import {
  defaultConfig,
  defaultServiceCatalogConfig,
  testRelativeFunction1,
  testRelativeService1,
} from "./SwfLanguageServiceConfigs";
import { codeCompletionTester, ContentWithCursor, getStartNodeValuePositionTester, treat, trim } from "./testUtils";

const EXPECTED_RESULTS_PROJECT_FOLDER: string = path.resolve("tests", "expectedResults");
const documentUri = "test.sw.yaml";

describe("YamlCodeCompletionStrategy", () => {
  const ls = new SwfYamlLanguageService({
    fs: {},
    serviceCatalog: defaultServiceCatalogConfig,
    config: defaultConfig,
  });

  describe("getStartNodeValuePosition", () => {
    const codeCompletionStrategy = new YamlCodeCompletionStrategy();
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

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

describe("SWF LS YAML", () => {
  describe("parsing content", () => {
    test("parsing content with empty function array", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });

      const { content } = trim(`
---
functions: []
`);

      const rootNode = ls.parseContent(content);

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

      expect(rootNode).toMatchObject({
        type: "object",
        children: [
          {
            type: "property",
            children: [
              {
                type: "string",
                value: "functions",
              },
              {
                type: "array",
                children: [],
              },
            ],
          },
        ],
      });
    });

    test("parsing content with one state and one function", () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });

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

      const rootNode = ls.parseContent(content);

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

      expect(rootNode).toMatchObject({
        type: "object",
        children: [
          {
            type: "property",
            children: [
              {
                type: "string",
                value: "functions",
              },
              {
                type: "array",
                children: [
                  {
                    type: "object",
                    children: [
                      {
                        type: "property",
                        children: [
                          {
                            type: "string",
                            value: "name",
                          },
                          {
                            type: "string",
                            value: "myFunc",
                          },
                        ],
                      },
                      {
                        type: "property",
                        children: [
                          {
                            type: "string",
                            value: "operation",
                          },
                          {
                            type: "string",
                            value: "./specs/myService#myFunc",
                          },
                        ],
                      },
                      {
                        type: "property",
                        children: [
                          {
                            type: "string",
                            value: "type",
                          },
                          {
                            type: "string",
                            value: "rest",
                          },
                        ],
                      },
                    ],
                  },
                ],
              },
            ],
          },
          {
            type: "property",
            children: [
              {
                type: "string",
                value: "states",
              },
              {
                type: "array",
                children: [
                  {
                    type: "object",
                    children: [
                      {
                        type: "property",
                        children: [
                          {
                            type: "string",
                            value: "name",
                          },
                          {
                            type: "string",
                            value: "testState",
                          },
                        ],
                      },
                      {
                        type: "property",
                        children: [
                          {
                            type: "string",
                            value: "type",
                          },
                          {
                            type: "string",
                            value: "operation",
                          },
                        ],
                      },
                      {
                        type: "property",
                        children: [
                          {
                            type: "string",
                            value: "transition",
                          },
                          {
                            type: "string",
                            value: "end",
                          },
                        ],
                      },
                      {
                        type: "property",
                        children: [
                          {
                            type: "string",
                            value: "actions",
                          },
                          {
                            type: "array",
                            children: [
                              {
                                type: "object",
                                children: [
                                  {
                                    type: "property",
                                    children: [
                                      {
                                        type: "string",
                                        value: "name",
                                      },
                                      {
                                        type: "string",
                                        value: "testStateAction",
                                      },
                                    ],
                                  },
                                  {
                                    type: "property",
                                    children: [
                                      {
                                        type: "string",
                                        value: "functionRef",
                                      },
                                      {
                                        type: "object",
                                        children: [
                                          {
                                            type: "property",
                                            children: [
                                              {
                                                type: "string",
                                                value: "refName",
                                              },
                                              {
                                                type: "string",
                                                value: "myFunc",
                                              },
                                            ],
                                          },
                                        ],
                                      },
                                    ],
                                  },
                                ],
                              },
                            ],
                          },
                        ],
                      },
                    ],
                  },
                ],
              },
            ],
          },
        ],
      });
    });

    test("parsing content with an incomplete functionRef object", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });

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

      const rootNode = ls.parseContent(content);

      expect(rootNode).not.toBeUndefined();
      expect(
        rootNode?.children?.[0].children?.[1].children?.[0].children?.[2].children?.[1].children?.[0].children?.[1]
          .children?.[1].value
      ).toBe("a");
    });

    test("parsing content with an incomplete operation property", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });

      const { content } = trim(`---
functions:
- name: testRelativeFunction1
  operation: `);

      const rootNode = ls.parseContent(content);

      expect(rootNode).not.toBeUndefined();
      expect(rootNode?.children?.[0].children?.[1].children?.[0].children?.[1].children?.[0].value).toBe("operation");
    });
  });

  describe("isNodeUncompleted", () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const parseContentTester = (contentToParse: ContentWithCursor) => {
      const { content, cursorPosition } = treat(contentToParse);

      const rootNode = ls.parseContent(content);
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
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

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
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(0);
      });

      test.each([
        ["total empty file", ``],
        ["empty file with a newline before the cursor", `\n`],
        ["empty file with a newline after the cursor", `\n`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });
        const position = Position.create(0, 0);

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: position, end: position },
          command: {
            title: "Create a Serverless Workflow",
            command: "swf.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: position }],
          },
        } as CodeLens);
      });
    });

    describe("functions code lenses", () => {
      test("add function", async () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });

        const { content } = trim(`
---
functions: 
- `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 0, line: 2 } }],
          },
        } as CodeLens);
      });

      test("add function - using JSON format", async () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });

        const { content } = trim(`
---
functions: [] `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(0);
      });

      test("service registries integration disabled", async () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: {
            ...defaultConfig,
            shouldDisplayServiceRegistriesIntegration: async () => Promise.resolve(false),
            shouldConfigureServiceRegistries: () => true,
            shouldServiceRegistriesLogIn: () => true,
            canRefreshServices: () => true,
          },
        });

        const { content } = trim(`
---
functions: 
- name: getGreetingFunction `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 0, line: 2 } }],
          },
        } as CodeLens);
      });

      test("login to service registries", async () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: { ...defaultConfig, shouldServiceRegistriesLogIn: () => true },
        });

        const { content } = trim(`
---
functions: 
- name: getGreetingFunction `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(2);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
          command: {
            command: "swf.ls.commands.LogInServiceRegistries",
            title: "↪ Log in Service Registries...",
            arguments: [{ position: { character: 0, line: 2 } }],
          },
        });
        expect(codeLenses[1]).toStrictEqual({
          range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 0, line: 2 } }],
          },
        } as CodeLens);
      });

      test("setup service registries", async () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: {
            ...defaultConfig,
            shouldConfigureServiceRegistries: () => true,
          },
        });

        const { content } = trim(`
---
functions: 
- name: getGreetingFunction `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(2);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
          command: {
            command: "swf.ls.commands.OpenServiceRegistriesConfig",
            title: "↪ Setup Service Registries...",
            arguments: [{ position: { character: 0, line: 2 } }],
          },
        });
        expect(codeLenses[1]).toStrictEqual({
          range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 0, line: 2 } }],
          },
        } as CodeLens);
      });

      test("refresh service registries", async () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: {
            ...defaultConfig,
            canRefreshServices: () => true,
          },
        });

        const { content } = trim(`
---
functions: 
- name: getGreetingFunction `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(2);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
          command: {
            command: "swf.ls.commands.RefreshServiceRegistries",
            title: "↺ Refresh Service Registries...",
            arguments: [{ position: { character: 0, line: 2 } }],
          },
        });
        expect(codeLenses[1]).toStrictEqual({
          range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 0, line: 2 } }],
          },
        } as CodeLens);
      });
    });
  });

  describe("code completion", () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

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
        let { completionItems, cursorPosition } = await codeCompletionTester(ls, documentUri, content, false);
        const expectedResult = fs.readFileSync(
          path.resolve(EXPECTED_RESULTS_PROJECT_FOLDER, "emptyfile_autocompletion.sw.yaml.result"),
          "utf-8"
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Text,
          label: "Create your first Serverless Workflow",
          sortText: "100_Create your first Serverless Workflow",
          detail: "Start with a simple Serverless Workflow",
          textEdit: {
            range: { start: cursorPosition, end: cursorPosition },
            newText: expectedResult,
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });
    });

    describe("function completion", () => {
      test.each([
        ["empty completion items", "functions:\n-🎯"],
        ["pointing before the array of functions / using JSON format", `functions: 🎯[] `],
        [
          "pointing before the array of functions / with extra space after ':' / using JSON format",
          `functions: 🎯 [] `,
        ],
        ["pointing after the array of functions / using JSON format", `functions: []🎯 `],
        [
          "pointing inside an object of the array of functions / using JSON format",
          `functions: [ {🎯 "name":"getGreetingFunction", "operation":"openapi.yml#getGreeting"}]`,
        ],
        ["using JSON format", `functions: [🎯]`],
        [
          "using JSON format / before a function",
          `functions: [🎯{ "name": "getGreetingFunction", "operation": "openapi.yml#getGreeting" }]`,
        ],
        [
          "using JSON format / after a function",
          `functions: [{ "name": "getGreetingFunction", "operation": "openapi.yml#getGreeting" },🎯]`,
        ],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems).toHaveLength(0);
      });

      test.each([
        ["add at the end", `functions:\n- name: getGreetingFunction\n- 🎯`],
        ["add at the beginning", `functions:\n- 🎯\n- name: getGreetingFunction`],
        ["add in the middle", `functions:\n- name: getGreetingFunction\n- 🎯\n- name: helloWorldFunction`],
        ["add in a new line", `functions: \n- 🎯`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems, cursorPosition } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Reference,
          label: "specs»testRelativeService1.yml#testRelativeFunction1",
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          textEdit: {
            range: { start: cursorPosition, end: cursorPosition },
            newText: `name: '\${1:testRelativeFunction1}'
  operation: 'specs/testRelativeService1.yml#testRelativeFunction1'
  type: rest`,
          },
          snippet: true,
          insertTextFormat: InsertTextFormat.Snippet,
          command: {
            command: "swf.ls.commands.ImportFunctionFromCompletionItem",
            title: "Import function from completion item",
            arguments: [
              {
                documentUri,
                containingService: {
                  ...testRelativeService1,
                  functions: [
                    {
                      ...testRelativeFunction1,
                      operation: "specs/testRelativeService1.yml#testRelativeFunction1",
                    },
                  ],
                },
              },
            ],
          },
        } as CompletionItem);
      });

      test.each([
        ["add at the beginning, using the code lenses", `functions:\n🎯- name: helloWorldFunction`],
        [
          "add at the beginning / with extra indentation / using the code lenses",
          `functions:\n  🎯- name: helloWorldFunction`,
        ],
        [
          "add at the beginning / with double extra indentation / using the code lenses",
          `functions:\n    🎯- name: helloWorldFunction`,
        ],
      ])("%s", async (_description, content: ContentWithCursor) => {
        const { completionItems, cursorPosition } = await codeCompletionTester(ls, documentUri, content);

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Reference,
          label: "specs»testRelativeService1.yml#testRelativeFunction1",
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          textEdit: {
            range: { start: cursorPosition, end: cursorPosition },
            newText: `- name: '\${1:testRelativeFunction1}'
  operation: 'specs/testRelativeService1.yml#testRelativeFunction1'
  type: rest\n`,
          },
          snippet: true,
          insertTextFormat: InsertTextFormat.Snippet,
          command: {
            command: "swf.ls.commands.ImportFunctionFromCompletionItem",
            title: "Import function from completion item",
            arguments: [
              {
                documentUri,
                containingService: {
                  ...testRelativeService1,
                  functions: [
                    {
                      ...testRelativeFunction1,
                      operation: "specs/testRelativeService1.yml#testRelativeFunction1",
                    },
                  ],
                },
              },
            ],
          },
        } as CompletionItem);
      });
    });

    describe("operation completion", () => {
      test.each([
        ["using JSON format", `functions: [{"name": "getGreetingFunction", "operation": 🎯 }]`],
        ["using JSON format", `functions: [{\n      "name": "getGreetingFunction",\n      "operation": "🎯"\n      }]`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems, cursorPosition } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems).toHaveLength(0);
      });

      test.each([
        [
          "not in quotes / without space after property name",
          `functions:\n- name: testRelativeFunction1\n  operation: 🎯`,
        ],
        [
          "not in quotes / with same level content after",
          `functions:\n- name: testRelativeFunction1\n  operation: 🎯\n  type: rest`,
        ],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems, cursorPosition } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Folder,
          label: `'specs/testRelativeService1.yml#testRelativeFunction1'`,
          detail: `'specs/testRelativeService1.yml#testRelativeFunction1'`,
          filterText: `'specs/testRelativeService1.yml#testRelativeFunction1'`,
          textEdit: {
            newText: `'specs/testRelativeService1.yml#testRelativeFunction1'`,
            range: {
              start: {
                ...cursorPosition,
                character: cursorPosition.character,
              },
              end: {
                ...cursorPosition,
                character: cursorPosition.character,
              },
            },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        });
      });

      test.each([
        [
          "inside single quotes / without same level content after",
          `functions:\n- name: testRelativeFunction1\n  operation: '🎯'`,
        ],
        [
          "inside single quotes / with same level content after",
          `functions:\n- name: testRelativeFunction1\n  operation: '🎯'\n  type: 'rest'`,
        ],
        [
          "inside double quotes / without same level content after",
          `functions:\n- name: testRelativeFunction1\n  operation: "🎯"`,
        ],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems, cursorPosition } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Folder,
          label: `'specs/testRelativeService1.yml#testRelativeFunction1'`,
          detail: `'specs/testRelativeService1.yml#testRelativeFunction1'`,
          filterText: `'specs/testRelativeService1.yml#testRelativeFunction1'`,
          textEdit: {
            newText: `'specs/testRelativeService1.yml#testRelativeFunction1'`,
            range: {
              start: {
                ...cursorPosition,
                character: cursorPosition.character - 1,
              },
              end: {
                ...cursorPosition,
                character: cursorPosition.character + 1,
              },
            },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });
    });

    describe("functionRef completion", () => {
      test("without same level content after / without space after property name", async () => {
        const { completionItems } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:🎯`
        );

        expect(completionItems).toHaveLength(0);
      });

      test("without same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef: 🎯`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Module,
          label: "testRelativeFunction1",
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: "testRelativeFunction1",
          textEdit: {
            newText: `
  refName: 'testRelativeFunction1'
  arguments:
    argString: '\${1:}'
    argNumber: '\${2:}'
    argBoolean: '\${3:}'`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("with same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - functionRef: 🎯
    name: testStateAction
          `
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Module,
          label: "testRelativeFunction1",
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: "testRelativeFunction1",
          textEdit: {
            newText: `
  refName: 'testRelativeFunction1'
  arguments:
    argString: '\${1:}'
    argNumber: '\${2:}'
    argBoolean: '\${3:}'`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("using JSON format", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - functionRef: {🎯}`
        );

        expect(completionItems).toHaveLength(0);
      });
    });

    describe("functionRef refName completion", () => {
      test("not in quotes / without space after property name", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName:🎯`
        );

        expect(completionItems).toHaveLength(0);
      });

      test("not in quotes / without same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: 🎯`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Value,
          label: `myFunc`,
          detail: `"myFunc"`,
          filterText: `myFunc`,
          sortText: `myFunc`,
          textEdit: {
            newText: `myFunc`,
            range: {
              start: {
                ...cursorPosition,
                character: cursorPosition.character,
              },
              end: {
                ...cursorPosition,
                character: cursorPosition.character,
              },
            },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("not in quotes / with same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: 🎯
      arguments: {}`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Value,
          label: `myFunc`,
          detail: `"myFunc"`,
          filterText: `myFunc`,
          sortText: `myFunc`,
          textEdit: {
            newText: `myFunc`,
            range: {
              start: {
                ...cursorPosition,
                character: cursorPosition.character,
              },
              end: {
                ...cursorPosition,
                character: cursorPosition.character,
              },
            },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("inside single quotes / without same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: '🎯'`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Value,
          label: `myFunc`,
          detail: `"myFunc"`,
          filterText: `myFunc`,
          sortText: `myFunc`,
          textEdit: {
            newText: `myFunc`,
            range: {
              start: {
                ...cursorPosition,
                character: cursorPosition.character - 1,
              },
              end: {
                ...cursorPosition,
                character: cursorPosition.character + 1,
              },
            },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("inside double quotes / without same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: "🎯"`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Value,
          label: `myFunc`,
          detail: `"myFunc"`,
          filterText: `myFunc`,
          sortText: `myFunc`,
          textEdit: {
            newText: `myFunc`,
            range: {
              start: {
                ...cursorPosition,
                character: cursorPosition.character - 1,
              },
              end: {
                ...cursorPosition,
                character: cursorPosition.character + 1,
              },
            },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("inside double quotes / with same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: myFunc
  operation: "./specs/myService#myFunc"
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: "🎯"
      arguments: {}`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Value,
          label: `myFunc`,
          detail: `"myFunc"`,
          filterText: `myFunc`,
          sortText: `myFunc`,
          textEdit: {
            newText: `myFunc`,
            range: {
              start: {
                ...cursorPosition,
                character: cursorPosition.character - 1,
              },
              end: {
                ...cursorPosition,
                character: cursorPosition.character + 1,
              },
            },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });
    });

    describe("functionRef arguments completion", () => {
      test("without any function arguments to complete", async () => {
        const testRelativeService1WithEmptyFunctionArgs = {
          ...testRelativeService1,
          functions: [{ ...testRelativeFunction1, arguments: {} }],
        };

        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: {
            ...defaultServiceCatalogConfig,
            relative: { getServices: async () => [testRelativeService1WithEmptyFunctionArgs] },
          },
          config: defaultConfig,
        });

        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: testRelativeFunction1
      arguments: 🎯`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Module,
          label: `'testRelativeFunction1' arguments`,
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: `'testRelativeFunction1' arguments`,
          textEdit: {
            newText: `{}`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("without same level content after / without space after property name", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: testRelativeFunction1
      arguments:🎯`
        );

        expect(completionItems).toHaveLength(0);
      });

      test("without same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: testRelativeFunction1
      arguments: 🎯`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Module,
          label: `'testRelativeFunction1' arguments`,
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: `'testRelativeFunction1' arguments`,
          textEdit: {
            newText: `
  argString: '\${1:}'
  argNumber: '\${2:}'
  argBoolean: '\${3:}'`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("with same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      arguments: 🎯
      refName: testRelativeFunction1`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Module,
          label: `'testRelativeFunction1' arguments`,
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: `'testRelativeFunction1' arguments`,
          textEdit: {
            newText: `
  argString: '\${1:}'
  argNumber: '\${2:}'
  argBoolean: '\${3:}'`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("using JSON format", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `---
functions:
- name: testRelativeFunction1
  operation: specs/testRelativeService1.yml#testRelativeFunction1
  type: rest
states:
- name: testState
  type: operation
  transition: end
  actions:
  - name: testStateAction
    functionRef:
      refName: testRelativeFunction1
      arguments: {🎯}`
        );

        expect(completionItems).toHaveLength(0);
      });
    });
  });
});
