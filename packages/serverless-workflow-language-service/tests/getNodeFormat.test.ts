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
  findNodeAtOffset,
  getNodeFormat,
  SwfJsonLanguageService,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { defaultConfig, defaultJqCompletionsConfig, defaultServiceCatalogConfig } from "./SwfLanguageServiceConfigs";
import { ContentWithCursor, treat } from "./testUtils";

describe("getNodeFormat", () => {
  const getNodeFormatTester = (args: {
    content: ContentWithCursor;
    ls: SwfJsonLanguageService | SwfYamlLanguageService;
  }): FileLanguage | undefined => {
    const { content, cursorOffset } = treat(args.content);
    const root = args.ls.parseContent(content);
    const node = findNodeAtOffset(root!, cursorOffset);

    return node ? getNodeFormat(content, node) : undefined;
  };

  describe("JSON format", () => {
    const ls = new SwfJsonLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
      jqCompletions: defaultJqCompletionsConfig,
    });

    test("string value", async () => {
      expect(
        getNodeFormatTester({
          content: `{
          "name": "🎯Greeting workflow"
        }`,
          ls,
        })
      ).toBe(FileLanguage.JSON);
    });

    test("boolean value", async () => {
      expect(
        getNodeFormatTester({
          content: `{
          "end": tru🎯e
        }`,
          ls,
        })
      ).toBe(FileLanguage.JSON);
    });

    describe("arrays", () => {
      test("single line declaration", async () => {
        expect(
          getNodeFormatTester({
            content: `{
          "functions": [🎯]
        }`,
            ls,
          })
        ).toBe(FileLanguage.JSON);
      });

      test("two lines declaration", async () => {
        expect(
          getNodeFormatTester({
            content: `{
          "functions": 
          [🎯]
        }`,
            ls,
          })
        ).toBe(FileLanguage.JSON);
      });

      test("single line declaration / with one function / with content before and after", async () => {
        expect(
          getNodeFormatTester({
            content: `{
          "name": "Greeting workflow",
          "functions": [
           🎯 {
                "name": "getGreetingFunction",
                "operation": "openapi.yml#getGreeting"
              }
          ],
          "states": []
        }`,
            ls,
          })
        ).toBe(FileLanguage.JSON);
      });
    });

    describe("objects", () => {
      test("single line declaration", async () => {
        expect(
          getNodeFormatTester({
            content: `{
          "data": {🎯}
        }`,
            ls,
          })
        ).toBe(FileLanguage.JSON);
      });

      test("two lines declaration", async () => {
        expect(
          getNodeFormatTester({
            content: `{
            "data": 
            {🎯
              "language": "Portuguese"
            }
        }`,
            ls,
          })
        ).toBe(FileLanguage.JSON);
      });

      test("single line declaration / with one attribute / with content before and after", async () => {
        expect(
          getNodeFormatTester({
            content: `{
            "name": "GreetInPortuguese",
            "data": {🎯
              "language": "Portuguese",
              "message": "Olá"
            },
            "transition": "GetGreeting"
        }`,
            ls,
          })
        ).toBe(FileLanguage.JSON);
      });
    });
  });

  describe("YAML format", () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
      jqCompletions: defaultJqCompletionsConfig,
    });

    test("string value", async () => {
      expect(
        getNodeFormatTester({
          content: `name: 🎯Greeting workflow`,
          ls,
        })
      ).toBe(FileLanguage.YAML);
    });

    test("string value / JSON format", async () => {
      expect(
        getNodeFormatTester({
          content: `name: "🎯Greeting workflow"`,
          ls,
        })
      ).toBe(FileLanguage.JSON);
    });

    test("boolean value / JSON format", async () => {
      expect(
        getNodeFormatTester({
          content: `end: 🎯true`,
          ls,
        })
      ).toBe(FileLanguage.JSON);
    });

    test("boolean value", async () => {
      expect(
        getNodeFormatTester({
          content: `end: 🎯True`,
          ls,
        })
      ).toBe(FileLanguage.YAML);
    });

    describe("arrays", () => {
      test("single line declaration / JSON format", async () => {
        expect(getNodeFormatTester({ content: `functions: [🎯]`, ls })).toBe(FileLanguage.JSON);
      });

      test("two lines declaration / JSON format", async () => {
        expect(
          getNodeFormatTester({
            content: `functions: [🎯    {
        "name": "getGreetingFunction",
        "operation": "openapi.yml#getGreeting"
      }]`,
            ls,
          })
        ).toBe(FileLanguage.JSON);
      });

      test("YAML format / with two functions / with content before and after", async () => {
        expect(
          getNodeFormatTester({
            content: `---
name: Greeting workflow
functions:
🎯- name: getGreetingFunction
  operation: openapi.yml#getGreeting
- name: greetFunction
  type: custom
  operation: sysout
states: [] `,
            ls,
          })
        ).toBe(FileLanguage.YAML);
      });
    });

    describe("objects", () => {
      test("single line declaration / JSON format", async () => {
        expect(getNodeFormatTester({ content: `data: {🎯}`, ls })).toBe(FileLanguage.JSON);
      });

      test("two lines declaration / JSON format", async () => {
        expect(
          getNodeFormatTester({
            content: `data: {🎯
              "language": "Portuguese" }`,
            ls,
          })
        ).toBe(FileLanguage.JSON);
      });

      test("YAML format / with content before and after", async () => {
        expect(
          getNodeFormatTester({
            content: `---
name: GreetInPortuguese
data:
🎯  language: Portuguese
  message: Hello
transition: GetGreeting `,
            ls,
          })
        ).toBe(FileLanguage.YAML);
      });
    });
  });
});
