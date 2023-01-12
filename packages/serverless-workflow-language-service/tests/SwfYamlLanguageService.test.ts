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
  isNodeUncompleted,
  SwfYamlLanguageService,
  YamlCodeCompletionStrategy,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, Position } from "vscode-languageserver-types";
import { defaultConfig, defaultServiceCatalogConfig, testRelativeService1 } from "./SwfLanguageServiceConfigs";
import { codeCompletionTester, ContentWithCursor, getStartNodeValuePositionTester, treat, trim } from "./testUtils";

const documentUri = "test.sw.yaml";

describe("YamlCodeCompletionStrategy", () => {
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

      expect(rootNode).toMatchSnapshot();
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

      expect(rootNode).toMatchSnapshot();
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

        const { content } = trim(`functions:\n- `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 1, character: 0 }, end: { line: 1, character: 0 } },
          command: {
            title: "+ Add function...",
            command: "swf.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 0, line: 1 } }],
          },
        } as CodeLens);
      });

      test("add function - using JSON format", async () => {
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
        });

        const { content } = trim(`functions: [] `);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(0);
      });
    });

    test("add event", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });

      const { content } = trim(`events:\n- `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(1);
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 1, character: 0 }, end: { line: 1, character: 0 } },
        command: {
          title: "+ Add event...",
          command: "swf.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 0, line: 1 } }],
        },
      } as CodeLens);
    });

    test("add state", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
      });

      const { content } = trim(`states:\n- `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(1);
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 1, character: 0 }, end: { line: 1, character: 0 } },
        command: {
          title: "+ Add state...",
          command: "swf.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 0, line: 1 } }],
        },
      } as CodeLens);
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
        let { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("operation completion", () => {
      test.each([
        ["using JSON format", `functions: [{"name": "getGreetingFunction", "operation": 🎯 }]`],
        ["using JSON format", `functions: [{\n      "name": "getGreetingFunction",\n      "operation": "🎯"\n      }]`],
      ])("%s", async (_description, content: ContentWithCursor) => {
        let { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

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
        let { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
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
    functionRef: 🎯`
        );

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });

      test("with same level content after", async () => {
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
  - functionRef: 🎯
    name: testStateAction
          `
        );

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });

      test("using JSON format", async () => {
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
  - functionRef: {🎯}`
        );

        expect(completionItems).toHaveLength(0);
      });
    });

    describe("functionRef refName completion", () => {
      test.each([
        ["not in quotes / without space after property name", `🎯`],
        ["not in quotes / without same level content after", ` 🎯 `],
        ["not in quotes / with same level content after", ` 🎯\n      arguments: {}`],
        ["inside single quotes / without same level content after", ` '🎯'`],
        ["inside double quotes / without same level content after", ` "🎯"`],
        ["inside double quotes / with same level content after", ` "🎯"\n      arguments: {}`],
      ])("%s", async (_description, nodeValue) => {
        const content = `---
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
      refName:${nodeValue}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("functionRef arguments completion", () => {
      test("without any function arguments to complete", async () => {
        const testRelativeService1WithEmptyFunctionArgs = {
          ...testRelativeService1,
          functions: [],
        };

        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: {
            ...defaultServiceCatalogConfig,
            relative: { getServices: async () => [testRelativeService1WithEmptyFunctionArgs] },
          },
          config: defaultConfig,
        });

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
    functionRef:
      refName: testRelativeFunction1
      arguments: 🎯`
        );

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });

      test.each([
        ["without same level content after / without space after property name", `🎯`],
        ["without same level content after", ` 🎯`],
        ["with same level content after", ` 🎯\n      invoke: sync`],
        ["using JSON format", ` {🎯}`],
      ])("%s", async (_description, nodeValue) => {
        const content = `---
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
      arguments:${nodeValue}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe.each([["functions"], ["events"], ["states"]])(`%s completion`, (nodeName: string) => {
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

    describe("eventRefs completion", () => {
      describe("using JSON format", () => {
        test.each([
          ["pointing inside an object of the array", ` [ "🎯" ]`],
          ["pointing before the array", `🎯 []`],
          ["pointing before the array", ` 🎯 []`],
          ["pointing after the array", ` []🎯`],
          ["add into empty array", ` [🎯]`],
          ["add at the beginning of the array", ` [🎯, ""]`],
          ["add at the end of the array", ` ["", 🎯 ]`],
        ])("%s", async (_description, nodeValue) => {
          const content = `events:
- name: GreetEvent
states:
- onEvents:
  - eventRefs:${nodeValue}` as ContentWithCursor;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems).toHaveLength(0);
        });
      });

      test.each([
        [`empty completion items`, `-🎯`],
        [`empty completion items / with extra space`, `- 🎯`],
        [`add at the end`, `- name: itemName\n    - 🎯`],
        [`add at the beginning`, `- 🎯\n- itemName`],
        [`add in the middle`, `- itemName1\n    - 🎯\n    - itemName2`],
        [`add in the middle / without dash character`, `- itemName1\n    🎯\n    - itemName2`],
        [`add at the beginning, using the code lenses`, `🎯- itemName`],
        [`add at the beginning / with extra indentation / using the code lenses`, `  🎯- itemName`],
      ])("%s", async (_description, nodeValue) => {
        const content = `events:
- name: GreetEvent
states:
- onEvents:
  - eventRefs:
    ${nodeValue}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("transition completion", () => {
      describe("state transition completion", () => {
        test.each([
          ["not in quotes / without space after property name", `🎯`],
          ["not in quotes / without same level content after", ` 🎯 `],
          ["not in quotes / with same level content after", ` 🎯\n  type: inject`],
          ["inside single quotes / without same level content after", ` '🎯'`],
          ["inside double quotes / without same level content after", ` "🎯"`],
          ["inside double quotes / with same level content after", ` "🎯"\n  type: inject`],
        ])("%s", async (_description, nodeValue) => {
          const content = `states:
- name: Inject Hello World
  transition:${nodeValue}
- name: Inject Mantra` as ContentWithCursor;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems.length).toMatchSnapshot();
          expect(completionItems).toMatchSnapshot();
        });
      });

      describe("dataConditions transition completion", () => {
        test("simple case", async () => {
          const content = `states:
- name: Inject Hello World
  dataConditions:
  - transition: 🎯
- name: Inject Mantra` as ContentWithCursor;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems.length).toMatchSnapshot();
          expect(completionItems).toMatchSnapshot();
        });
      });

      describe("defaultCondition transition completion", () => {
        test("simple case", async () => {
          const content = `states:
- name: Inject Hello World
  defaultCondition:
    transition: 🎯
- name: Inject Mantra` as ContentWithCursor;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems.length).toMatchSnapshot();
          expect(completionItems).toMatchSnapshot();
        });
      });

      describe("eventConditions transition completion", () => {
        test("simple case", async () => {
          const content = `states:
- name: Inject Hello World
  eventConditions:
  - transition: 🎯
- name: Inject Mantra` as ContentWithCursor;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems.length).toMatchSnapshot();
          expect(completionItems).toMatchSnapshot();
        });
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
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [] },
      },
      config: defaultConfig,
    });

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
