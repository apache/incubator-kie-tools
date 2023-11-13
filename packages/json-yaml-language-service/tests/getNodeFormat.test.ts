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

import {
  findNodeAtOffset,
  getNodeFormat,
  parseJsonContent,
  parseYamlContent,
} from "@kie-tools/json-yaml-language-service/dist/channel";
import { FileLanguage } from "@kie-tools/json-yaml-language-service/dist/api";
import { ContentWithCursor, treat } from "./testUtils";

describe("getNodeFormat", () => {
  const getNodeFormatTester = (args: {
    content: ContentWithCursor;
    fileLanguage: FileLanguage;
  }): FileLanguage | undefined => {
    const { content, cursorOffset } = treat(args.content);
    const root = args.fileLanguage === FileLanguage.JSON ? parseJsonContent(content) : parseYamlContent(content);
    const node = findNodeAtOffset(root!, cursorOffset);

    return node ? getNodeFormat(content, node) : undefined;
  };

  describe("JSON format", () => {
    const fileLanguage = FileLanguage.JSON;

    test("string value", async () => {
      expect(
        getNodeFormatTester({
          content: `{
          "name": "🎯Greeting workflow"
        }`,
          fileLanguage,
        })
      ).toBe(FileLanguage.JSON);
    });

    test("boolean value", async () => {
      expect(
        getNodeFormatTester({
          content: `{
          "end": tru🎯e
        }`,
          fileLanguage,
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
            fileLanguage,
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
            fileLanguage,
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
            fileLanguage,
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
            fileLanguage,
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
            fileLanguage,
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
            fileLanguage,
          })
        ).toBe(FileLanguage.JSON);
      });
    });
  });

  describe("YAML format", () => {
    const fileLanguage = FileLanguage.YAML;

    test("string value", async () => {
      expect(
        getNodeFormatTester({
          content: `name: 🎯Greeting workflow`,
          fileLanguage,
        })
      ).toBe(FileLanguage.YAML);
    });

    test("string value / JSON format", async () => {
      expect(
        getNodeFormatTester({
          content: `name: "🎯Greeting workflow"`,
          fileLanguage,
        })
      ).toBe(FileLanguage.JSON);
    });

    test("boolean value / JSON format", async () => {
      expect(
        getNodeFormatTester({
          content: `end: 🎯true`,
          fileLanguage,
        })
      ).toBe(FileLanguage.JSON);
    });

    test("boolean value", async () => {
      expect(
        getNodeFormatTester({
          content: `end: 🎯True`,
          fileLanguage,
        })
      ).toBe(FileLanguage.YAML);
    });

    describe("arrays", () => {
      test("single line declaration / JSON format", async () => {
        expect(getNodeFormatTester({ content: `functions: [🎯]`, fileLanguage })).toBe(FileLanguage.JSON);
      });

      test("two lines declaration / JSON format", async () => {
        expect(
          getNodeFormatTester({
            content: `functions: [🎯    {
        "name": "getGreetingFunction",
        "operation": "openapi.yml#getGreeting"
      }]`,
            fileLanguage,
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
            fileLanguage,
          })
        ).toBe(FileLanguage.YAML);
      });
    });

    describe("objects", () => {
      test("single line declaration / JSON format", async () => {
        expect(getNodeFormatTester({ content: `data: {🎯}`, fileLanguage })).toBe(FileLanguage.JSON);
      });

      test("two lines declaration / JSON format", async () => {
        expect(
          getNodeFormatTester({
            content: `data: {🎯
              "language": "Portuguese" }`,
            fileLanguage,
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
            fileLanguage,
          })
        ).toBe(FileLanguage.YAML);
      });
    });
  });
});
