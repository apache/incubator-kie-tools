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

import { CodeLens, Position } from "vscode-languageserver-types";
import { DashbuilderLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";
import { codeCompletionTester, ContentWithCursor } from "./testUtils";

const documentUri = "test.sw.yaml";

describe("Code lenses", () => {
  describe("empty file code lenses", () => {
    test.each([
      ["empty object / using JSON format", `{}`],
      ["empty object / with cursor before the object / using JSON format", `{}`],
      ["empty object / with cursor after the object / using JSON format", `{}`],
    ])("%s", async (_description, content: ContentWithCursor) => {
      const ls = new DashbuilderLanguageService();

      const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });

      expect(codeLenses).toHaveLength(0);
    });
  });

  test.each([
    ["total empty file", ``],
    ["empty file with a newline before the cursor", `\n`],
    ["empty file with a newline after the cursor", `\n`],
  ])("%s", async (_description, content: ContentWithCursor) => {
    const ls = new DashbuilderLanguageService();

    const codeLenses = await ls.getCodeLenses({ uri: documentUri, content });
    const position = Position.create(0, 0);

    expect(codeLenses).toHaveLength(1);
    expect(codeLenses[0]).toStrictEqual({
      range: { start: position, end: position },
      command: {
        title: "Create a dashboard",
        command: "dashbuilder.ls.commands.OpenCompletionItems",
        arguments: [{ newCursorPosition: position }],
      },
    } as CodeLens);
  });
});

describe("code completion", () => {
  const ls = new DashbuilderLanguageService();
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
