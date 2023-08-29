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

import { DashbuilderYamlLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";
import { CodeLens, Position } from "vscode-languageserver-types";
import { codeCompletionTester, ContentWithCursor } from "./testUtils";

const documentUri = "test.sw.yaml";

describe("Code lenses", () => {
  describe("empty file code lenses", () => {
    test.each([
      ["empty object / using JSON format", `{}`],
      ["empty object / with cursor before the object / using JSON format", `{}`],
      ["empty object / with cursor after the object / using JSON format", `{}`],
    ])("%s", async (_description, content: ContentWithCursor) => {
      const ls = new DashbuilderYamlLanguageService();

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(0);
    });
  });

  test.each([
    ["total empty file", ``],
    ["empty file with a newline before the cursor", `\n`],
    ["empty file with a newline after the cursor", `\n`],
  ])("%s", async (_description, content: ContentWithCursor) => {
    const ls = new DashbuilderYamlLanguageService();

    const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });
    const position = Position.create(0, 0);

    expect(codeLenses).toHaveLength(1);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: position, end: position },
      command: {
        title: "Create a dashboard",
        command: "editor.ls.commands.OpenCompletionItems",
        arguments: [{ newCursorPosition: position }],
      },
    } as CodeLens);
  });
});

describe("code completion", () => {
  const ls = new DashbuilderYamlLanguageService();

  describe("empty file completion", () => {
    test.each([
      ["empty object / using JSON format", `{🎯}`],
      ["empty object / with cursor before the object / using JSON format", `🎯{}`],
      ["empty object / with cursor after the object / using JSON format", `{}🎯`],
    ])("%s", async (_description, content: ContentWithCursor) => {
      const { completionItems } = await codeCompletionTester(ls, documentUri, content, false);

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
});

describe("diagnostic", () => {
  const ls = new DashbuilderYamlLanguageService();
  test.each([
    ["empty file", ``],
    [
      "valid",
      `pages:
      - rows:
          - columns:
              - span: '6'
                components:
                  - html: Row 1 Column 1
              - span: '6'
                components:
                  - html: Row 1 Column 2
          - columns:
              - span: '6'
                components:
                  - html: Row 2 Column 1
              - span: '6'
                components:
                  - html: Row 2 Column 2`,
    ],
  ])("%s", async (_description, content) => {
    const diagnostic = await ls.getDiagnostics({ uriPath: documentUri, content });

    expect(diagnostic.length).toMatchSnapshot();
    expect(diagnostic).toMatchSnapshot();
  });
});
