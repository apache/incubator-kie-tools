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
  JsonCodeCompletionStrategy,
  SwfJsonLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import * as path from "path";
import * as fs from "fs";
import { CodeLens, CompletionItem, CompletionItemKind, InsertTextFormat, Position } from "vscode-languageserver-types";
import {
  defaultConfig,
  defaultServiceCatalogConfig,
  testRelativeFunction1,
  testRelativeService1,
} from "./SwfLanguageServiceConfigs";
import { codeCompletionTester, ContentWithCursor, getStartNodeValuePositionTester, trim } from "./testUtils";

const EXPECTED_RESULTS_PROJECT_FOLDER: string = path.resolve("tests", "expectedResults");
const documentUri = "test.sw.json";

describe("JsonCodeCompletionStrategy", () => {
  const ls = new SwfJsonLanguageService({
    fs: {},
    serviceCatalog: defaultServiceCatalogConfig,
    config: defaultConfig,
  });

  describe("getStartNodeValuePosition", () => {
    const codeCompletionStrategy = new JsonCodeCompletionStrategy();
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

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
      ).toStrictEqual({ line: 1, character: 17 });
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

describe("SWF LS JSON", () => {
  test("basic", async () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

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
        const ls = new SwfJsonLanguageService({
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
        const ls = new SwfJsonLanguageService({
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

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
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

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 0, character: 13 }, end: { line: 0, character: 13 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
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

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
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

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

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
            command: "swf.ls.commands.OpenCompletionItems",
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

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

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
            command: "swf.ls.commands.OpenCompletionItems",
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

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

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
            command: "swf.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
          },
        } as CodeLens);
      });
    });
  });

  describe("code completion", () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

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
        const { completionItems, cursorPosition } = await codeCompletionTester(ls, documentUri, content, false);
        const expectedResult = fs.readFileSync(
          path.resolve(EXPECTED_RESULTS_PROJECT_FOLDER, "emptyfile_autocompletion.sw.json.result"),
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
        ["empty completion items", `{ "functions": [ {🎯} ] }`],
        ["pointing before the array of functions", `{ "functions":🎯 [] }`],
        ["pointing before the array of functions / with extra space after ':'", `{ "functions": 🎯 [] }`],
        ["pointing after the array of functions", `{ "functions": []🎯 }`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems).toHaveLength(0);
      });

      test("add into empty functions array", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
  "functions": [🎯]
}`
        );

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
                documentUri: documentUri,
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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `
{
  "functions": [{...},🎯]
}`
        );

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
                documentUri: documentUri,
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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
  "functions": [🎯{...}]
}`
        );

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
                documentUri: documentUri,
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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
  "functions": [{...},🎯{...}]
}`
        );

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
                documentUri: documentUri,
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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
  "functions": [
    🎯
  ]
}`
        );

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
                documentUri: documentUri,
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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
  "functions": [
    {
      "name": "testRelativeFunction1",
      "operation": 🎯
    }
  ]
}`
        );

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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
  "functions": [
    {
      "name": "testRelativeFunction1",
      "operation": 🎯,
      "type": "rest"
    }
  ]
}`
        );

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

      test("inside double quotes / without same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
  "functions": [
    {
      "name": "testRelativeFunction1",
      "operation": "🎯"
    }
  ]
}`
        );

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

      test("inside double quotes / with same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
  "functions": [
    {
      "name": "testRelativeFunction1",
      "operation": "🎯",
      "type": "rest"
    }
  ]
}`
        );

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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
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
}`
        );

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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
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
}`
        );

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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
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
}`
        );

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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
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
}`
        );

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

      test("inside double quotes / without same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
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
}`
        );

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

      test("inside double quotes / with same level content after", async () => {
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
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
}`
        );

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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
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
}`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Module,
          label: `'testRelativeFunction1' arguments`,
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: `'testRelativeFunction1' arguments`,
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
        const { completionItems, cursorPosition } = await codeCompletionTester(
          ls,
          documentUri,
          `{
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
          }`
        );

        expect(completionItems).toHaveLength(1);
        expect(completionItems[0]).toStrictEqual({
          kind: CompletionItemKind.Module,
          label: `'testRelativeFunction1' arguments`,
          detail: "specs/testRelativeService1.yml#testRelativeFunction1",
          sortText: `'testRelativeFunction1' arguments`,
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
