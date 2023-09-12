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

import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { CodeLens, Position } from "vscode-languageserver-types";
import {
  getJqBuiltInFunctionTests,
  getJqReusableFunctionTests,
  getJqVariableTests,
  getJsonLsForJqExpressionTests,
} from "./SwfJqExpressionTestUtils";
import {
  defaultConfig,
  defaultJqCompletionsConfig,
  defaultServiceCatalogConfig,
  testRelativeService1,
} from "./SwfLanguageServiceConfigs";
import { codeCompletionTester, ContentWithCursor, trim } from "./testUtils";

const documentUri = "test.sw.json";

describe("SWF LS JSON", () => {
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
        const ls = new SwfJsonLanguageService({
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
      test("add function - formatted", async () => {
        const ls = new SwfJsonLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
          jqCompletions: defaultJqCompletionsConfig,
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
            command: "editor.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
          },
        } as CodeLens);
      });

      test("add function - unformatted", async () => {
        const ls = new SwfJsonLanguageService({
          fs: {},
          serviceCatalog: defaultServiceCatalogConfig,
          config: defaultConfig,
          jqCompletions: defaultJqCompletionsConfig,
        });

        const { content } = trim(`{"functions":[]}`);

        const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

        expect(codeLenses).toHaveLength(1);
        expect(codeLenses[0]).toStrictEqual({
          range: { start: { line: 0, character: 13 }, end: { line: 0, character: 13 } },
          command: {
            title: "+ Add function...",
            command: "editor.ls.commands.OpenCompletionItems",
            arguments: [{ newCursorPosition: { character: 14, line: 0 } }],
          },
        } as CodeLens);
      });
    });

    test("add event - unformatted", async () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`{"events":[]}`);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(1);
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 0, character: 10 }, end: { line: 0, character: 10 } },
        command: {
          title: "+ Add event...",
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 11, line: 0 } }],
        },
      } as CodeLens);
    });

    test("add state - unformatted", async () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: defaultConfig,
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`{"states":[]}`);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(1);
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 0, character: 10 }, end: { line: 0, character: 10 } },
        command: {
          title: "+ Add state...",
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 11, line: 0 } }],
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
        jqCompletions: defaultJqCompletionsConfig,
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
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
        },
      } as CodeLens);
    });

    test("login to service registries", async () => {
      const ls = new SwfJsonLanguageService({
        fs: {},
        serviceCatalog: defaultServiceCatalogConfig,
        config: { ...defaultConfig, shouldServiceRegistriesLogIn: () => true },
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
{
  "functions": []
}`);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
        command: {
          command: "swf.ls.commands.LogInServiceRegistries",
          title: "↪ Log in Service Registries...",
          arguments: [{ position: { character: 15, line: 1 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
        command: {
          title: "+ Add function...",
          command: "editor.ls.commands.OpenCompletionItems",
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
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
{
  "functions": []
}`);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
        command: {
          command: "swf.ls.commands.OpenServiceRegistriesConfig",
          title: "↪ Setup Service Registries...",
          arguments: [{ position: { character: 15, line: 1 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
        command: {
          title: "+ Add function...",
          command: "editor.ls.commands.OpenCompletionItems",
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
        jqCompletions: defaultJqCompletionsConfig,
      });

      const { content } = trim(`
{
  "functions": []
}`);

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(2);
      expect(codeLenses[1]).toStrictEqual({
        range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
        command: {
          command: "swf.ls.commands.RefreshServiceRegistries",
          title: "↺ Refresh Service Registries...",
          arguments: [{ position: { character: 15, line: 1 } }],
        },
      });
      expect(codeLenses[0]).toStrictEqual({
        range: { start: { line: 1, character: 15 }, end: { line: 1, character: 15 } },
        command: {
          title: "+ Add function...",
          command: "editor.ls.commands.OpenCompletionItems",
          arguments: [{ newCursorPosition: { character: 16, line: 1 } }],
        },
      } as CodeLens);
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
      jqCompletions: defaultJqCompletionsConfig,
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
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("operation completion", () => {
      test.each([
        ["not in quotes / without same level content after", ` 🎯`],
        ["not in quotes / with same level content after", ` 🎯\n      "type": "rest"`],
        ["inside double quotes / without same level content after", ` "🎯" `],
        ["inside double quotes / with same level content after", ` "🎯"\n      "type": "rest"`],
      ])("%s", async (_description, nodeValue) => {
        const content = `{
  "functions": [
    {
      "name": "testRelativeFunction1",
      "operation":${nodeValue}
    }
  ]
}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });
    describe("Jq completions", () => {
      describe("operations completion", () => {
        describe.each([["built-in functions"], ["reusable functions"], ["variables"]])(`%s completion`, () => {
          test.each([...getJqBuiltInFunctionTests(), ...getJqBuiltInFunctionTests(), ...getJqVariableTests()])(
            "%s",
            async (_description, nodeValue) => {
              const content = `{
                "dataInputSchema": "path/to/schema",
                "functions": [
                    {
                        "name": "testFunc1",
                        "type": "asyncapi",
                        "operation": "http://path_to_remote_asyncApiFile/"
                    },
                    {
                        "name": "testFunc2",
                        "type": "rest",
                        "operation": "http://path_to_remote_openApiFile/"
                    },
                    {
                        "name": "expressionFunc1",
                        "type": "expression",
                        "operation": "."
                    },
                    {
                        "name": "expressionFunc2",
                        "type": "expression",
                        "operation": "."
                    },
                    {
                      "name": "expressionFunc3",
                      "type": "expression",
                      "operation": ${nodeValue}
                  }
                ]
            }` as ContentWithCursor;
              const { completionItems } = await codeCompletionTester(
                getJsonLsForJqExpressionTests(),
                documentUri,
                content,
                false
              );
              expect(completionItems.length).toMatchSnapshot();
              expect(completionItems.slice(0, 5)).toMatchSnapshot();
            }
          );
        });
      });
      describe("state data filter completions", () => {
        describe.each([["built-in functions"], ["reusable functions"], ["variables"]])(`%s completion`, () => {
          test.each([...getJqBuiltInFunctionTests(), ...getJqReusableFunctionTests(), ...getJqVariableTests()])(
            "%s",
            async (_description, nodeValue) => {
              const content = `{
            "dataInputSchema": "/path/to/json_schema.json",
            "functions":[
              {
                "name": "testFunc",
                "type": "rest",
                "operation":"http://path_to_remote_asyncApiFile/"
              },
              {
                "name": "expressionFunc1",
                "type": "expression",
                "operation": "."
              },
              {
                  "name": "expressionFunc2",
                  "type": "expression",
                  "operation": "."
              }
            ],
            "states": [
              {
                "name": "testName",
                "type": "inject",
                "stateDataFilter": {
                  "output": ${nodeValue}
                }
              }
            ]
        }` as ContentWithCursor;
              const { completionItems } = await codeCompletionTester(
                getJsonLsForJqExpressionTests(),
                documentUri,
                content,
                false
              );
              expect(completionItems.length).toMatchSnapshot();
              expect(completionItems.slice(0, 5)).toMatchSnapshot();
            }
          );
        });
      });
      describe("event data filter completions", () => {
        describe.each([["built-in functions"], ["reusable functions"], ["variables"]])(`%s completion`, () => {
          test.each([...getJqBuiltInFunctionTests(), ...getJqReusableFunctionTests(), ...getJqVariableTests()])(
            "%s",
            async (_description, nodeValue) => {
              const content = `{
            "dataInputSchema": "/path/to/json_schema.json",
            "functions":[
              {
                "name": "testFunc",
                "type": "rest",
                "operation":"http://path_to_remote_asyncApiFile/"
              },
              {
                "name": "expressionFunc1",
                "type": "expression",
                "operation": "."
              },
              {
                  "name": "expressionFunc2",
                  "type": "expression",
                  "operation": "."
              }
            ],
            "states": [
              {
                "name": "testState",
                "type": "callback",
                "eventDataFilter": {
                  "data": ${nodeValue},
                  "toStateData": ${nodeValue}
                }
              },
            ]
        }` as ContentWithCursor;
              const { completionItems } = await codeCompletionTester(
                getJsonLsForJqExpressionTests(),
                documentUri,
                content,
                false
              );
              expect(completionItems.length).toMatchSnapshot();
              expect(completionItems.slice(0, 5)).toMatchSnapshot();
            }
          );
        });
      });
      describe("data filter inside on onEvents completions", () => {
        describe.each([["built-in functions"], ["reusable functions"], ["variables"]])(`%s completion`, () => {
          test.each([...getJqBuiltInFunctionTests(), ...getJqReusableFunctionTests(), ...getJqVariableTests()])(
            "%s",
            async (_description, nodeValue) => {
              const content = `{
            "dataInputSchema": "/path/to/json_schema.json",
            "functions":[
              {
                "name": "testFunc",
                "type": "rest",
                "operation":"http://path_to_remote_asyncApiFile/"
              },
              {
                "name": "expressionFunc1",
                "type": "expression",
                "operation": "."
              },
              {
                  "name": "expressionFunc2",
                  "type": "expression",
                  "operation": "."
              }
            ],
            "states":[
              {
                "name": "testEvent",
                "type": "event",
                "onEvents": [
                  {
                    "eventDataFilter": {
                      "data": ${nodeValue},
                      "toStateData": ${nodeValue}
                    },
                  }
                ],
              }
            ]
        }` as ContentWithCursor;
              const { completionItems } = await codeCompletionTester(
                getJsonLsForJqExpressionTests(),
                documentUri,
                content,
                false
              );
              expect(completionItems.length).toMatchSnapshot();
              expect(completionItems.slice(0, 5)).toMatchSnapshot();
            }
          );
        });
      });
      describe("data filter completions", () => {
        describe.each([["built-in functions"], ["reusable functions"], ["variables"]])(`%s completion`, () => {
          test.each([...getJqBuiltInFunctionTests(), ...getJqReusableFunctionTests(), ...getJqVariableTests()])(
            "%s",
            async (_description, nodeValue) => {
              const content = `{
            "dataInputSchema": "/path/to/json_schema.json",
            "functions":[
              {
                "name": "testFunc",
                "type": "rest",
                "operation":"http://path_to_remote_asyncApiFile/"
              },
              {
                "name": "expressionFunc1",
                "type": "expression",
                "operation": "."
              },
              {
                  "name": "expressionFunc2",
                  "type": "expression",
                  "operation": "."
              }
            ],
            "states": [
              {
                "name": "testState",
                "type": "operation",
                "actions": [
                  {
                    "name": "testAction",
                    "functionRef": {
                      "refName": "testRefName"
                    },
                    "actionDataFilter": {
                       "results" : ${nodeValue}, 
                       "toStateData" : ${nodeValue}
                    }
                  }
                ],
              },
            ]
        }` as ContentWithCursor;
              const { completionItems } = await codeCompletionTester(
                getJsonLsForJqExpressionTests(),
                documentUri,
                content,
                false
              );
              expect(completionItems.length).toMatchSnapshot();
              expect(completionItems.slice(0, 5)).toMatchSnapshot();
            }
          );
        });
      });
      describe("functionRef arguments completions", () => {
        describe.each([["built-in functions"], ["reusable functions"], ["variables"]])(`%s completion`, () => {
          test.each([...getJqBuiltInFunctionTests(), ...getJqReusableFunctionTests(), ...getJqVariableTests()])(
            "%s",
            async (_description, nodeValue) => {
              const content = `{
            "dataInputSchema": "/path/to/json_schema.json",
            "functions":[
              {
                "name": "testFunc",
                "type": "rest",
                "operation":"http://path_to_remote_asyncApiFile/"
              },
              {
                "name": "expressionFunc1",
                "type": "expression",
                "operation": "."
              },
              {
                  "name": "expressionFunc2",
                  "type": "expression",
                  "operation": "."
              }
            ],
            "states": [
              {
                "name": "testState",
                "type": "operation",
                "actions": [
                  {
                    "functionRef": {
                      "refName": "testRefName",
                      "arguments": {
                        "numberOfRunningPods": ${nodeValue},
                      }
                    }
                  }
                ],
              },
            ]
        }` as ContentWithCursor;
              const { completionItems } = await codeCompletionTester(
                getJsonLsForJqExpressionTests(),
                documentUri,
                content,
                false
              );
              expect(completionItems.length).toMatchSnapshot();
              expect(completionItems.slice(0, 5)).toMatchSnapshot();
            }
          );
        });
      });
      describe("data condition completions", () => {
        describe.each([["built-in functions"], ["reusable functions"], ["variables"]])(`%s completion`, () => {
          test.each([...getJqBuiltInFunctionTests(), ...getJqReusableFunctionTests(), ...getJqVariableTests()])(
            "%s",
            async (_description, nodeValue) => {
              const content = `{
            "dataInputSchema": "/path/to/json_schema.json",
            "functions":[
              {
                "name": "testFunc",
                "type": "rest",
                "operation":"http://path_to_remote_asyncApiFile/"
              },
              {
                "name": "expressionFunc1",
                "type": "expression",
                "operation": "."
              },
              {
                  "name": "expressionFunc2",
                  "type": "expression",
                  "operation": "."
              }
            ],
            "states": [
            {
              "name": "testState",
              "type": "switch",
              "dataConditions": [
                {
                  "condition": ${nodeValue},
                  "transition": "Scale up on Ansible"
                }
              ],
            }
          ]
        }` as ContentWithCursor;
              const { completionItems } = await codeCompletionTester(
                getJsonLsForJqExpressionTests(),
                documentUri,
                content,
                false
              );
              expect(completionItems.length).toMatchSnapshot();
              expect(completionItems.slice(0, 5)).toMatchSnapshot();
            }
          );
        });
      });
    });
    describe("functionRef completion", () => {
      test.each([
        ["without same level content after", ` 🎯`],
        ["with same level content after", ` 🎯\n                    "name": "testStateAction",`],
      ])("%s", async (_description, nodeValue) => {
        const content = `{
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
          "functionRef":${nodeValue}
        }
      ]
    }
  ]
}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("functionRef refName completion", () => {
      test.each([
        ["not in quotes / without same level content after", ` 🎯`],
        ["not in quotes / with same level content after", ` 🎯\n            "arguments": {}`],
        ["inside double quotes / without same level content after", ` 🎯`],
        ["inside double quotes / with same level content after", ` 🎯\n            "arguments": {}`],
      ])("%s", async (_description, nodeValue) => {
        const content = `{
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
            "refName":${nodeValue}
          }
        }
      ]
    }
  ]
}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("functionRef arguments completion", () => {
      test.each([
        ["without same level content after", ` 🎯`],
        ["with same level content after", ` 🎯\n            "refName":"testRelativeFunction1"`],
      ])("%s", async (_description, nodeValue) => {
        const content = `{
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
            "arguments":${nodeValue}
          }
        }
      ]
    }
  ]
}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe.each([["functions"], ["events"], ["states"]])(`%s completion`, (nodeName: string) => {
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

    describe("eventRefs completion", () => {
      test.each([
        ["pointing inside an object of the array", ` [ "🎯" ]`],
        ["pointing before the array", `🎯 []`],
        ["pointing before the array", ` 🎯 []`],
        ["pointing after the array", ` []🎯`],
        ["add into empty array", ` [🎯]`],
        ["add at the beginning of the array", ` [🎯 ""]`],
        ["add at the end of the array", ` ["", 🎯 ]`],
      ])("%s", async (_description, nodeValue) => {
        const content = `{
  "events": [
    {
      "name": "GreetEvent"
    }
  ],
  "states": [{
    "onEvents": [
      {
        "eventRefs":${nodeValue}
      }
    ]
  }]
}` as ContentWithCursor;
        const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

        expect(completionItems.length).toMatchSnapshot();
        expect(completionItems).toMatchSnapshot();
      });
    });

    describe("transition completion", () => {
      describe("state transition completion", () => {
        test.each([
          ["not in quotes / without same level content after", ` 🎯`],
          ["not in quotes / with same level content after", ` 🎯\n        "type": "inject"`],
          ["inside double quotes / without same level content after", ` 🎯`],
          ["inside double quotes / with same level content after", ` 🎯\n        "type": "inject"`],
        ])("%s", async (_description, nodeValue) => {
          const content = `{
    "states": [ 
      {
        "name": "Inject Hello World",
        "transition":${nodeValue}
      },
      {
        "name": "Inject Mantra"
      }
    ]
  }` as ContentWithCursor;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems.length).toMatchSnapshot();
          expect(completionItems).toMatchSnapshot();
        });
      });

      describe("dataConditions transition completion", () => {
        test("simple case", async () => {
          const content = `{
    "states": [ 
      {
        "name": "Inject Hello World",
        "dataConditions": [
          {
            "transition": 🎯
          }
        ]
      },
      {
        "name": "Inject Mantra"
      }
    ]
  }`;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems.length).toMatchSnapshot();
          expect(completionItems).toMatchSnapshot();
        });
      });

      describe("defaultCondition transition completion", () => {
        test("simple case", async () => {
          const content = `{
    "states": [ 
      {
        "name": "Inject Hello World",
        "defaultCondition": {
          "transition": 🎯
        }
      },
      {
        "name": "Inject Mantra"
      }
    ]
  }` as ContentWithCursor;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems.length).toMatchSnapshot();
          expect(completionItems).toMatchSnapshot();
        });
      });

      describe("eventConditions transition completion", () => {
        test("simple case", async () => {
          const content = `{
    "states": [ 
      {
        "name": "Inject Hello World",
        "eventConditions": [{
          "transition": 🎯
        }]
      },
      {
        "name": "Inject Mantra"
      }
    ]
  }` as ContentWithCursor;
          const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

          expect(completionItems.length).toMatchSnapshot();
          expect(completionItems).toMatchSnapshot();
        });
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
});
