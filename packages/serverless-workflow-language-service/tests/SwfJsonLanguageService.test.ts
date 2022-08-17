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
  SwfJsonLanguageService,
  findNodeAtOffset,
  matchNodeWithLocation,
  SwfLanguageServiceConfig,
  nodeUpUntilType,
  SwfLsNode,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionArgumentType,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { CodeLens, CompletionItem, CompletionItemKind, InsertTextFormat } from "vscode-languageserver-types";
import { trim, treat } from "./testUtils";

const testRelativeFunction1: SwfServiceCatalogFunction = {
  name: "testRelativeFunction1",
  type: SwfServiceCatalogFunctionType.rest,
  source: {
    type: SwfServiceCatalogFunctionSourceType.LOCAL_FS,
    serviceFileAbsolutePath: "/Users/tiago/Desktop/testRelativeService1.yml",
  },
  arguments: {
    argString: SwfServiceCatalogFunctionArgumentType.string,
    argNumber: SwfServiceCatalogFunctionArgumentType.number,
    argBoolean: SwfServiceCatalogFunctionArgumentType.boolean,
  },
};

const testRelativeService1: SwfServiceCatalogService = {
  name: "testRelativeService1",
  source: {
    type: SwfServiceCatalogServiceSourceType.LOCAL_FS,
    absoluteFilePath: "/Users/tiago/Desktop/testRelativeService1.yml",
  },
  type: SwfServiceCatalogServiceType.rest,
  rawContent: "",
  functions: [testRelativeFunction1],
};

const defaultServiceCatalogConfig = {
  relative: { getServices: async () => [] },
  global: { getServices: async () => [] },
  getServiceFileNameFromSwfServiceCatalogServiceId: async (registryName: string, serviceId: string) =>
    `${serviceId}.yaml`,
};

const defaultConfig: SwfLanguageServiceConfig = {
  shouldConfigureServiceRegistries: () => false,
  shouldServiceRegistriesLogIn: () => false,
  canRefreshServices: () => false,
  getSpecsDirPosixPaths: async () => ({ specsDirRelativePosixPath: "specs", specsDirAbsolutePosixPath: "" }),
  shouldDisplayServiceRegistriesIntegration: async () => true,
  shouldReferenceServiceRegistryFunctionsWithUrls: async () => false,
  shouldIncludeJsonSchemaDiagnostics: async () => true,
};

describe("SWF LS JSON", () => {
  describe("matchNodeWithLocation", () => {
    test("matching root node with empty content", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`🎯{}`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    test("matching empty functions array", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`
{
  "functions": [🎯]
}`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeTruthy();
      expect(matchNodeWithLocation(root!, node!, ["functions", "none"])).toBeFalsy();
    });

    describe("matching functions array", () => {
      test("with cursorOffset at the functions array", () => {
        const ls = new SwfJsonLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });
        let { content, cursorOffset } = treat(`
{
  "functions": [🎯
  {
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  }]
}`);
        const root = ls.parseContent(content);
        const node = findNodeAtOffset(root!, cursorOffset);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeTruthy();
      });

      test("with cursorOffset at the first function", () => {
        const ls = new SwfJsonLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });
        let { content, cursorOffset } = treat(`
{
  "functions": [{🎯
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  }]
}`);
        const root = ls.parseContent(content);
        const node = findNodeAtOffset(root!, cursorOffset);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      });

      test("with cursorOffset at the second function", () => {
        const ls = new SwfJsonLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });
        let { content, cursorOffset } = treat(`
{
  "functions": [
  {
        "name": "function1",
        "operation": "openapi.yml#getGreeting"
  },
  {🎯
        "name": "function2",
        "operation": "openapi.yml#getGreeting"
  }]
}`);
        const root = ls.parseContent(content);
        const node = findNodeAtOffset(root!, cursorOffset);

        expect(matchNodeWithLocation(root!, node!, ["functions", "*"])).toBeTruthy();
        expect(matchNodeWithLocation(root!, node!, ["functions"])).toBeFalsy();
      });
    });

    test("matching refName", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`
{
  "functions": [
    {
      "name": "myFunc",
      "operation": "./specs/myService#myFunc",
      "type": "rest"
    }
  ],
  "states": [
    {
      "name": "testState1",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction",
          "functionRef": {
            "refName":"myFunc"
          }
        }
      ]
    },
    {
      "name": "testState2",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction1",
          "functionRef": {
            "refName":"myFunc"
          }
        },
        {
          "name": "testStateAction2",
          "functionRef": {
            "refName":"🎯"
          }
        }
      ]
    }
  ]
}`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "refName"])
      ).toBeTruthy();
    });

    test("matching arguments", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`
{
  "functions": [
    {
      "name": "myFunc",
      "operation": "./specs/myService#myFunc",
      "type": "rest"
    }
  ],
  "states": [
    {
      "name": "testState1",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction",
          "functionRef": {
            "refName":"myFunc"
          }
        }
      ]
    },
    {
      "name": "testState2",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction1",
          "functionRef": {
            "refName":"myFunc"
          }
        },
        {
          "name": "testStateAction2",
          "functionRef": {
            "refName":"myfunc",
            "arguments": {
              🎯
            }
          }
        }
      ]
    }
  ]
}`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      expect(
        matchNodeWithLocation(root!, node!, ["states", "*", "actions", "*", "functionRef", "arguments"])
      ).toBeTruthy();
    });
  });

  describe("nodeUpUntilType", () => {
    test("up to functionRef value", () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });
      const { content, cursorOffset } = treat(`{
          "name": "testStateAction2",
          "functionRef": {
            "refName":"🎯",
          }
        }`);
      const root = ls.parseContent(content);
      const node = findNodeAtOffset(root!, cursorOffset);

      const receivedNode = nodeUpUntilType(node!, "object");

      expect(receivedNode).not.toBeUndefined();
      expect(receivedNode!.type).toBe("object");
      expect(receivedNode!.offset).toBe(65);
    });
  });

  test("basic", async () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.json",
      content: "{}",
      cursorPosition: { line: 0, character: 0 },
      cursorWordRange: { start: { line: 0, character: 0 }, end: { line: 0, character: 0 } },
    });

    const codeLenses = await ls.getCodeLenses({
      uri: "test.sw.json",
      content: "{}",
    });

    expect(completionItems).toStrictEqual([]);
    expect(codeLenses).toStrictEqual([]);
  });

  test("functions code lenses (add function - formatted)", async () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const { content } = trim(`
{
  "functions": []
}`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.json", content });

    expect(codeLenses).toHaveLength(1);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (add function - unformatted)", async () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const { content } = trim(`{"functions":[]}`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.json", content });

    expect(codeLenses).toHaveLength(1);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 0, character: 13 }, end: { line: 0, character: 13 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 14, line: 0 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (service registries integration disabled)", async () => {
    const ls = new SwfJsonLanguageService({
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
{
  "functions": []
}`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.json", content });

    expect(codeLenses).toHaveLength(1);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (login to service registries)", async () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: { ...defaultConfig, shouldServiceRegistriesLogIn: () => true },
    });

    const { content } = trim(`
{
  "functions": []
}`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.json", content });

    expect(codeLenses).toHaveLength(2);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
      command: {
        command: "swf.ls.commands.LogInServiceRegistries",
        title: "↪ Log in Service Registries...",
        arguments: [{ position: { character: 15, line: 1 } }],
      },
    });
    expect(codeLenses[1]).toStrictEqual({
      range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (setup service registries)", async () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: {
        ...defaultConfig,
        shouldConfigureServiceRegistries: () => true,
      },
    });

    const { content } = trim(`
{
  "functions": []
}`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.json", content });

    expect(codeLenses).toHaveLength(2);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
      command: {
        command: "swf.ls.commands.OpenServiceRegistriesConfig",
        title: "↪ Setup Service Registries...",
        arguments: [{ position: { character: 15, line: 1 } }],
      },
    });
    expect(codeLenses[1]).toStrictEqual({
      range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
      },
    } as CodeLens);
  });

  test("functions code lenses (refresh service registries)", async () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: {
        ...defaultConfig,
        canRefreshServices: () => true,
      },
    });

    const { content } = trim(`
{
  "functions": []
}`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.json", content });

    expect(codeLenses).toHaveLength(2);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
      command: {
        command: "swf.ls.commands.RefreshServiceRegistries",
        title: "↺ Refresh Service Registries...",
        arguments: [{ position: { character: 15, line: 1 } }],
      },
    });
    expect(codeLenses[1]).toStrictEqual({
      range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
      command: {
        title: "+ Add function...",
        command: "swf.ls.commands.OpenFunctionsCompletionItems",
        arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
      },
    } as CodeLens);
  });

  test("function completion", async () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const { content, cursorPosition } = treat(`
{
  "functions": [🎯]
}`);

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.json",
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
        newText: `{
  "name": "\${1:testRelativeFunction1}",
  "operation": "specs/testRelativeService1.yml#testRelativeFunction1",
  "type": "rest"
}`,
      },
      snippet: true,
      insertTextFormat: InsertTextFormat.Snippet,
      command: {
        command: "swf.ls.commands.ImportFunctionFromCompletionItem",
        title: "Import function from completion item",
        arguments: [
          {
            documentUri: "test.sw.json",
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
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const { content, cursorPosition } = treat(`
{
  "functions": [
    {
      "name": "myFunc",
      "operation": "./specs/myService#myFunc",
      "type": "rest"
    }
  ],
  "states": [
    {
      "name": "testState",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction",
          "functionRef": {
            "refName":"🎯"
          }
        }
      ]
    },
  ]
}`);

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.json",
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
        newText: `"myFunc"`,
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
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const { content, cursorPosition } = treat(`
{
  "functions": [
    {
      "name": "testRelativeFunction1",
      "operation": "specs/testRelativeService1.yml#testRelativeFunction1",
      "type": "rest"
    }
  ],
  "states": [
    {
      "name": "testState",
      "type": "operation",
      "transition": "end",
      "actions": [
        {
          "name": "testStateAction",
          "functionRef": {
            "refName":"testRelativeFunction1",
            "arguments": {
                🎯
            } 
          }
        }
      ]
    }
  ]
}`);

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
        newText: `
  "argString": "\${1:}",
  "argNumber": "\${2:}",
  "argBoolean": "\${3:}"
`,
        range: { start: cursorPosition, end: cursorPosition },
      },
      insertTextFormat: InsertTextFormat.Snippet,
    } as CompletionItem);
  });
});
