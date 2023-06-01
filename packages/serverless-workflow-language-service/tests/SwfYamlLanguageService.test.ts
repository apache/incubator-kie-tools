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

import { SwfYamlLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { CodeLens, Position } from "vscode-languageserver-types";
import {
  getJqBuiltInFunctionTests,
  getJqReusableFunctionTests,
  getJqVariableTests,
  getSingleQuoteTestForYaml,
  getYamlLsForJqExpressionTests,
} from "./SwfJqExpressionTestUtils";
import {
  defaultConfig,
  defaultJqCompletionsConfig,
  defaultServiceCatalogConfig,
  testRelativeService1,
} from "./SwfLanguageServiceConfigs";
import { codeCompletionTester, ContentWithCursor, trim } from "./testUtils";

const documentUri = "test.sw.yaml";

describe("SWF LS YAML", () => {
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
          jqCompletions: defaultJqCompletionsConfig,
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
          jqCompletions: defaultJqCompletionsConfig,
        });

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
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
          jqCompletions: defaultJqCompletionsConfig,
        });

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
        const ls = new SwfYamlLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
          jqCompletions: defaultJqCompletionsConfig,
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
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`events:\n- `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(1);
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 1, character: 0 }, end: { line: 1, character: 0 } },
        command: {
          title: "+ Add event...",
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 0, line: 1 } }],
        },
      } as CodeLens);
    });

    test("add state", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`states:\n- `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(1);
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 1, character: 0 }, end: { line: 1, character: 0 } },
        command: {
          title: "+ Add state...",
          command: "editor.ls.commands.OpenCompletionItems",
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
        jqCompletions: defaultJqCompletionsConfig,
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
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 0, line: 2 } }],
        },
      } as CodeLens);
    });

    test("login to service registries", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: { ...defaultConfig, shouldServiceRegistriesLogIn: () => true },
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
---
functions: 
- name: getGreetingFunction `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
        command: {
          command: "swf.ls.commands.LogInServiceRegistries",
          title: "↪ Log in Service Registries...",
          arguments: [{ position: { character: 0, line: 2 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
        command: {
          title: "+ Add function...",
          command: "editor.ls.commands.OpenCompletionItems",
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
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
---
functions: 
- name: getGreetingFunction `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
        command: {
          command: "swf.ls.commands.OpenServiceRegistriesConfig",
          title: "↪ Setup Service Registries...",
          arguments: [{ position: { character: 0, line: 2 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
        command: {
          title: "+ Add function...",
          command: "editor.ls.commands.OpenCompletionItems",
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
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
---
functions: 
- name: getGreetingFunction `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
        command: {
          command: "swf.ls.commands.RefreshServiceRegistries",
          title: "↺ Refresh Service Registries...",
          arguments: [{ position: { character: 0, line: 2 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 2, character: 0 }, end: { line: 2, character: 0 } },
        command: {
          title: "+ Add function...",
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 0, line: 2 } }],
        },
      } as CodeLens);
    });

    test("login to service registries with events", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: { ...defaultConfig, shouldServiceRegistriesLogIn: () => true },
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
---
events:
  - name: 'wait'
    source: ''
    type: ''
    kind: consumed
    metadata:
      reference: 'specs/test (1).yaml#wait' `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 2, character: 2 }, end: { line: 2, character: 2 } },
        command: {
          command: "swf.ls.commands.LogInServiceRegistries",
          title: "↪ Log in Service Registries...",
          arguments: [{ position: { character: 2, line: 2 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 2, character: 2 }, end: { line: 2, character: 2 } },
        command: {
          title: "+ Add event...",
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 2, line: 2 } }],
        },
      } as CodeLens);
    });

    test("setup service registries with events", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: {
          ...defaultConfig,
          shouldConfigureServiceRegistries: () => true,
        },
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
---
events:
  - name: 'wait'
    source: ''
    type: ''
    kind: consumed
    metadata:
      reference: 'specs/test (1).yaml#wait' `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 2, character: 2 }, end: { line: 2, character: 2 } },
        command: {
          command: "swf.ls.commands.OpenServiceRegistriesConfig",
          title: "↪ Setup Service Registries...",
          arguments: [{ position: { character: 2, line: 2 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 2, character: 2 }, end: { line: 2, character: 2 } },
        command: {
          title: "+ Add event...",
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 2, line: 2 } }],
        },
      } as CodeLens);
    });

    test("refresh service registries with events", async () => {
      const ls = new SwfYamlLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: {
          ...defaultConfig,
          canRefreshServices: () => true,
        },
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
---
events:
  - name: 'wait'
    source: ''
    type: ''
    kind: consumed
    metadata:
      reference: 'specs/test (1).yaml#wait' `);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 2, character: 2 }, end: { line: 2, character: 2 } },
        command: {
          command: "swf.ls.commands.RefreshServiceRegistries",
          title: "↺ Refresh Service Registries...",
          arguments: [{ position: { character: 2, line: 2 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 2, character: 2 }, end: { line: 2, character: 2 } },
        command: {
          title: "+ Add event...",
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 2, line: 2 } }],
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
      jqCompletions: defaultJqCompletionsConfig,
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
    describe("Jq completions", () => {
      describe("operation completions", () => {
        describe.each([["built-in functions"], ["variables"]])(`%s completion`, () => {
          test.each([
            ...getJqBuiltInFunctionTests(),
            ...getJqReusableFunctionTests(),
            ...getJqVariableTests(),
            ...getSingleQuoteTestForYaml(),
          ])("%s", async (_description, nodeValue) => {
            const content = `---
              dataInputSchema: "path/to/schema"
              functions:
                - name: "testFunc1"
                  type: "asyncapi"
                  "operation": "http://path_to_remote_asyncApiFile/"
                - name: "testFunc2"
                  type: "rest"
                  operation: "http://path_to_remote_openApiFile/"
                - name: "expressionFunc1"
                  type: "expression"
                  operation: "."
                - name: "expressionFunc2"
                  type: "expression"
                  operation: "."
                - name: "expressionFunc3"
                  type: "expression"
                  operation: ${nodeValue}` as ContentWithCursor;
            const { completionItems } = await codeCompletionTester(
              getYamlLsForJqExpressionTests(),
              documentUri,
              content,
              false
            );
            expect(completionItems.length).toMatchSnapshot();
            expect(completionItems.slice(0, 5)).toMatchSnapshot();
          });
        });
      });
      describe("data condition completion", () => {
        describe.each([["built-in functions"], ["variables"]])(`%s completion`, () => {
          test.each([
            ...getJqBuiltInFunctionTests(),
            ...getJqReusableFunctionTests(),
            ...getJqVariableTests(),
            ...getSingleQuoteTestForYaml(),
          ])("%s", async (_description, nodeValue) => {
            const content = `---
              dataInputSchema: "path/to/schema"
              functions:
               - name: "testFunc"
                 type: "asyncapi"
                 "operation": "http://path_to_remote_asyncApiFile/"
               - name: "expressionFunc1"
                 type: "expression"
                 operation: "."
               - name: "expressionFunc2"
                 type: "expression"
                 operation: "."
              states:
              - name: testState
                type: switch
                dataConditions:
                  - condition: ${nodeValue}
                    transition: nextTransition` as ContentWithCursor;
            const { completionItems } = await codeCompletionTester(
              getYamlLsForJqExpressionTests(),
              documentUri,
              content,
              false
            );
            expect(completionItems.length).toMatchSnapshot();
            expect(completionItems.slice(0, 5)).toMatchSnapshot();
          });
        });
      });
      describe("state data filter completions", () => {
        describe.each([["built-in functions"], ["variables"]])(`%s completion`, () => {
          test.each([
            ...getJqBuiltInFunctionTests(),
            ...getJqReusableFunctionTests(),
            ...getJqVariableTests(),
            ...getSingleQuoteTestForYaml(),
          ])("%s", async (_description, nodeValue) => {
            const content = `---
              dataInputSchema: "path/to/schema"
              functions:
               - name: "testFunc"
                 type: "asyncapi"
                 "operation": "http://path_to_remote_asyncApiFile/"
               - name: "expressionFunc1"
                 type: "expression"
                 operation: "."
               - name: "expressionFunc2"
                 type: "expression"
                 operation: "."
              states:
              - name: testState
                type: inject
                stateDataFilter:
                  output: ${nodeValue}` as ContentWithCursor;
            const { completionItems } = await codeCompletionTester(
              getYamlLsForJqExpressionTests(),
              documentUri,
              content,
              false
            );
            expect(completionItems.length).toMatchSnapshot();
            expect(completionItems.slice(0, 5)).toMatchSnapshot();
          });
        });
      });
      describe("event data filter completions", () => {
        describe.each([["built-in functions"], ["variables"]])(`%s completion`, () => {
          test.each([
            ...getJqBuiltInFunctionTests(),
            ...getJqReusableFunctionTests(),
            ...getJqVariableTests(),
            ...getSingleQuoteTestForYaml(),
          ])("%s", async (_description, nodeValue) => {
            const content = `---
              dataInputSchema: "path/to/schema"
              functions:
               - name: "testFunc"
                 type: "asyncapi"
                 "operation": "http://path_to_remote_asyncApiFile/"
               - name: "expressionFunc1"
                 type: "expression"
                 operation: "."
               - name: "expressionFunc2"
                 type: "expression"
                 operation: "."
              states:
              - name: testEvent
                type: callBack
                eventDataFilter:
                  data: ${nodeValue}
                  toStateData: ${nodeValue}` as ContentWithCursor;
            const { completionItems } = await codeCompletionTester(
              getYamlLsForJqExpressionTests(),
              documentUri,
              content,
              false
            );
            expect(completionItems.length).toMatchSnapshot();
            expect(completionItems.slice(0, 5)).toMatchSnapshot();
          });
        });
      });
      describe("event data filter inside onEvents completion", () => {
        describe.each([["built-in functions"], ["variables"]])(`%s completion`, () => {
          test.each([
            ...getJqBuiltInFunctionTests(),
            ...getJqReusableFunctionTests(),
            ...getJqVariableTests(),
            ...getSingleQuoteTestForYaml(),
          ])("%s", async (_description, nodeValue) => {
            const content = `---
              dataInputSchema: "path/to/schema"
              functions:
               - name: "testFunc"
                 type: "asyncapi"
                 "operation": "http://path_to_remote_asyncApiFile/"
               - name: "expressionFunc1"
                 type: "expression"
                 operation: "."
               - name: "expressionFunc2"
                 type: "expression"
                 operation: "."
              states:
              - name: testEvent
                type: event
                onEvents:
                  - eventDataFilter:
                      data: ${nodeValue}
                      toStateData: ${nodeValue}` as ContentWithCursor;
            const { completionItems } = await codeCompletionTester(
              getYamlLsForJqExpressionTests(),
              documentUri,
              content,
              false
            );
            expect(completionItems.length).toMatchSnapshot();
            expect(completionItems.slice(0, 5)).toMatchSnapshot();
          });
        });
      });
      describe("action data filter completions", () => {
        describe.each([["built-in functions"], ["variables"]])(`%s completion`, () => {
          test.each([
            ...getJqBuiltInFunctionTests(),
            ...getJqReusableFunctionTests(),
            ...getJqVariableTests(),
            ...getSingleQuoteTestForYaml(),
          ])("%s", async (_description, nodeValue) => {
            const content = `---
              dataInputSchema: "path/to/schema"
              functions:
               - name: "testFunc"
                 type: "asyncapi"
                 "operation": "http://path_to_remote_asyncApiFile/"
               - name: "expressionFunc1"
                 type: "expression"
                 operation: "."
               - name: "expressionFunc2"
                 type: "expression"
                 operation: "."
              states:
              - name: testState
                type: operation
                actions:
                  - name: testName
                    actionDataFilter:
                      results: ${nodeValue}
                      toStateData: ${nodeValue}` as ContentWithCursor;

            const { completionItems } = await codeCompletionTester(
              getYamlLsForJqExpressionTests(),
              documentUri,
              content,
              false
            );
            expect(completionItems.length).toMatchSnapshot();
            expect(completionItems.slice(0, 5)).toMatchSnapshot();
          });
        });
      });
      describe("functionRef arguments completions", () => {
        describe.each([["built-in functions"], ["variables"]])(`%s completion`, () => {
          test.each([
            ...getJqBuiltInFunctionTests(),
            ...getJqReusableFunctionTests(),
            ...getJqVariableTests(),
            ...getSingleQuoteTestForYaml(),
          ])("%s", async (_description, nodeValue) => {
            const content = `---
    dataInputSchema: path/to/schema
    functions:
      - name: "testFunc"
        type: "asyncapi"
        "operation": "http://path_to_remote_asyncApiFile/"
      - name: "expressionFunc1"
        type: "expression"
        operation: "."
      - name: "expressionFunc2"
        type: "expression"
        operation: "."
    states:
      - name: testState
        type: operation
        actions:
          - functionRef:
              refName: testRefName
              arguments:
                numberOfRunningPods: ${nodeValue}` as ContentWithCursor;
            const { completionItems } = await codeCompletionTester(
              getYamlLsForJqExpressionTests(),
              documentUri,
              content,
              false
            );
            expect(completionItems.length).toMatchSnapshot();
            expect(completionItems.slice(0, 5)).toMatchSnapshot();
          });
        });
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
          jqCompletions: defaultJqCompletionsConfig,
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
});
