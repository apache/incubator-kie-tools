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
  SwfJsonLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { CodeLens, CompletionItem, CompletionItemKind, InsertTextFormat } from "vscode-languageserver-types";
import {
  defaultConfig,
  defaultServiceCatalogConfig,
  testRelativeFunction1,
  testRelativeService1,
} from "./SwfLanguageServiceConfigs";
import { treat, trim } from "./testUtils";

describe("SWF LS JSON", () => {
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

  describe("functions code lenses", () => {
    test("add function - formatted", async () => {
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

    test("add function - unformatted", async () => {
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

    test("service registries integration disabled", async () => {
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

    test("login to service registries", async () => {
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

    test("setup service registries", async () => {
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

    test("refresh service registries", async () => {
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
  });

  describe("code completion", () => {
    describe("function completion", () => {
      test("empty completion items", async () => {
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
    {🎯}
  ]
}`);

        const completionItems = await ls.getCompletionItems({
          uri: "test.sw.json",
          content,
          cursorPosition,
          cursorWordRange: { start: cursorPosition, end: cursorPosition },
        });

        expect(completionItems).toHaveLength(0);
      });

      test("add into empty functions array", async () => {
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

      test("add at the end", async () => {
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
  "functions": [{...},🎯]
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

      test("add at the beginning", async () => {
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
  "functions": [🎯{...}]
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
},`,
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

      test("add in the middle", async () => {
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
  "functions": [{...},🎯{...}]
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
},`,
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

      test("add in a new line", async () => {
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
    🎯
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
    });

    describe("operation completion", () => {
      test("not in quotes / without same level content after", async () => {
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
      "operation": 🎯
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
          kind: CompletionItemKind.Folder,
          label: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          detail: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          filterText: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          textEdit: {
            newText: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
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
      "operation": 🎯,
      "type": "rest"
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
          kind: CompletionItemKind.Folder,
          label: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          detail: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          filterText: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          textEdit: {
            newText: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
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

      test("inside quotes / without same level content after", async () => {
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
      "operation": "🎯"
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
          kind: CompletionItemKind.Folder,
          label: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          detail: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          filterText: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          textEdit: {
            newText: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
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

      test("inside quotes / with same level content after", async () => {
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
      "operation": "🎯",
      "type": "rest"
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
          kind: CompletionItemKind.Folder,
          label: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          detail: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          filterText: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
          textEdit: {
            newText: `"specs/testRelativeService1.yml#testRelativeFunction1"`,
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
      test("without same level content after", async () => {
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
          "functionRef": 🎯
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
          label: "testRelativeFunction1",
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: "testRelativeFunction1",
          textEdit: {
            newText: `{
  "refName": "testRelativeFunction1",
  "arguments": {\n    "argString": "\${1:}",\n    "argNumber": "\${2:}",\n    "argBoolean": "\${3:}"\n  }
}`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("with same level content after", async () => {
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
          "functionRef": 🎯,
          "name": "testStateAction",
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
          label: "testRelativeFunction1",
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: "testRelativeFunction1",
          textEdit: {
            newText: `{
  "refName": "testRelativeFunction1",
  "arguments": {\n    "argString": "\${1:}",\n    "argNumber": "\${2:}",\n    "argBoolean": "\${3:}"\n  }
}`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });
    });

    describe("functionRef refName completion", () => {
      test("not in quotes / without same level content after", async () => {
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
            "refName": 🎯
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
            "refName": 🎯,
            "arguments": {}
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

      test("inside quotes / without same level content after", async () => {
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
            "refName": "🎯"
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

      test("inside quotes / with same level content after", async () => {
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
            "refName": "🎯",
            "arguments": {}
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
    });

    describe("functionRef arguments completion", () => {
      test("without same level content after", async () => {
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
            "arguments": 🎯
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
            newText: `{
  "argString": "\${1:}",
  "argNumber": "\${2:}",
  "argBoolean": "\${3:}"
}`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });

      test("with same level content after", async () => {
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
                        "arguments": 🎯,
                        "refName":"testRelativeFunction1"
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
            newText: `{
  "argString": "\${1:}",
  "argNumber": "\${2:}",
  "argBoolean": "\${3:}"
}`,
            range: { start: cursorPosition, end: cursorPosition },
          },
          insertTextFormat: InsertTextFormat.Snippet,
        } as CompletionItem);
      });
    });
  });
});
