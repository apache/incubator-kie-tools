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

import {
  findNodeAtOffset,
  matchNodeWithLocation,
  nodeUpUntilType,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { CodeLens, CompletionItem, CompletionItemKind, InsertTextFormat } from "vscode-languageserver-types";
import {
  defaultConfig,
  defaultServiceCatalogConfig,
  testRelativeFunction1,
  testRelativeService1,
} from "./SwfLanguageServiceConfigs";
import { treat, trim } from "./testUtils";

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
states:
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
      expect(rootNode!.children![0].children).toHaveLength(2);
      expect(rootNode!.children![0].children![0]).not.toBeUndefined();
      expect(rootNode!.children![0].children![0].value).toBe("functions");
      expect(rootNode!.children![0].children![1]).not.toBeUndefined();
      expect(rootNode!.children![0].children![1].type).toBe("array");
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

    test("parsing content with an incomplete functionRef", async () => {
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
      debugger;

      expect(rootNode).not.toBeUndefined();
      expect(
        rootNode?.children?.[0].children?.[1].children?.[0].children?.[2].children?.[1].children?.[0].children?.[1]
          .children?.[1].value
      ).toBe("a");
    });
  });

  test("basic", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.yaml",
      content: "---",
      cursorPosition: { line: 0, character: 0 },
      cursorWordRange: { start: { line: 0, character: 0 }, end: { line: 0, character: 0 } },
    });

    const codeLenses = await ls.getCodeLenses({
      uri: "test.sw.yaml",
      content: "---",
    });

    expect(completionItems).toStrictEqual([]);
    expect(codeLenses).toStrictEqual([]);
  });

  test("functions code lenses (add function - formatted)", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const { content } = trim(`
---
functions: []`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

    expect(codeLenses).toHaveLength(1);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 12, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (add function - unformatted)", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const { content } = trim(`
---
functions: [] `);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

    expect(codeLenses).toHaveLength(1);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 12, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (service registries integration disabled)", async () => {
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
functions: []
`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

    expect(codeLenses).toHaveLength(1);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 12, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (login to service registries)", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: { ...defaultConfig, shouldServiceRegistriesLogIn: () => true },
    });

    const { content } = trim(`
---
functions: []
`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

    expect(codeLenses).toHaveLength(2);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        command: "swf.ls.commands.LogInServiceRegistries",
        title: "↪ Log in Service Registries...",
        arguments: [{ position: { character: 11, line: 1 } }],
      },
    });
    expect(codeLenses[1]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 12, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (setup service registries)", async () => {
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
functions: []
`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

    expect(codeLenses).toHaveLength(2);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        command: "swf.ls.commands.OpenServiceRegistriesConfig",
        title: "↪ Setup Service Registries...",
        arguments: [{ position: { character: 11, line: 1 } }],
      },
    });
    expect(codeLenses[1]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 12, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (refresh service registries)", async () => {
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
functions: []
`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

    expect(codeLenses).toHaveLength(2);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        command: "swf.ls.commands.RefreshServiceRegistries",
        title: "↺ Refresh Service Registries...",
        arguments: [{ position: { character: 11, line: 1 } }],
      },
    });
    expect(codeLenses[1]).toStrictEqual({
      range: { start: { line: 1, character: 11 }, end: { line: 1, character: 11 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 12, line: 1 } }],
      },
    } as CodeLens);
  });

  test("function completion", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const { content, cursorPosition } = treat(`
---
functions: [🎯]
`);

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.yaml",
      content,
      cursorPosition,
      cursorWordRange: { start: cursorPosition, end: cursorPosition },
    });

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
            documentUri: "test.sw.yaml",
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

  test("functionRef completion", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const { content, cursorPosition } = treat(`
---
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
`);

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.yaml",
      content,
      cursorPosition,
      cursorWordRange: { start: cursorPosition, end: cursorPosition },
    });

    expect(completionItems).toHaveLength(1);
    expect(completionItems[0]).toStrictEqual({
      kind: CompletionItemKind.Value,
      label: `"myFunc"`,
      detail: `"myFunc"`,
      filterText: `"myFunc"`,
      sortText: `"myFunc"`,
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

  test("functionRef arguments completion", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const { content, cursorPosition } = treat(`
---
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
      arguments:
        🎯arg
  end: true
`);

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.json",
      content,
      cursorPosition,
      cursorWordRange: { start: cursorPosition, end: cursorPosition },
    });

    expect(completionItems).toHaveLength(1);
    expect(completionItems[0]).toStrictEqual({
      kind: CompletionItemKind.Module,
      label: `'testRelativeFunction1' arguments`,
      detail: "specs/testRelativeService1.yml#testRelativeFunction1",
      sortText: "testRelativeFunction1 arguments",
      textEdit: {
        newText: `argString: '\${1:}'
argNumber: '$\{2:}'
argBoolean: '\${3:}'`,
        range: {
          start: {
            ...cursorPosition,
            character: cursorPosition.character,
          },
          end: {
            ...cursorPosition,
            character: cursorPosition.character + 3,
          },
        },
      },
      insertTextFormat: InsertTextFormat.Snippet,
    } as CompletionItem);
  });
});
