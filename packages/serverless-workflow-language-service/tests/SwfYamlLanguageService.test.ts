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
import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { TextDocument } from "vscode-languageserver-textdocument";
import { CodeLens, CompletionItem, CompletionItemKind, InsertTextFormat } from "vscode-languageserver-types";
import { SwfLanguageServiceConfig } from "../src/channel";

const testRelativeFunction1: SwfServiceCatalogFunction = {
  name: "testRelativeFunction1",
  type: SwfServiceCatalogFunctionType.rest,
  source: {
    type: SwfServiceCatalogFunctionSourceType.LOCAL_FS,
    serviceFileAbsolutePath: "/Users/tiago/Desktop/testRelativeService1.yml",
  },
  arguments: {},
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
};

describe("SWF LS YAML", () => {
  test("basic", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.yaml",
      content: "---",
      cursorPosition: { line: 0, character: 0 },
      cursorWordRange: { start: { line: 0, character: 0 }, end: { line: 0, character: 0 } },
    });

    const codeLenses = await ls.getCodeLenses({
      uri: "test.sw.yaml",
      content: "---",
    });

    expect(completionItems).toStrictEqual([]);
    expect(codeLenses).toStrictEqual([]);
  });

  test.only("functions code lenses (add function - formatted)", async () => {
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const { content } = trim(`
---
functions: []`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

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
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: defaultConfig,
    });

    const { content } = trim(`
---
functions: [] `);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

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
functions: []
`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

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
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: defaultServiceCatalogConfig,
      config: { ...defaultConfig, shouldServiceRegistriesLogIn: () => true },
    });

    const { content } = trim(`
---
functions: []
`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

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
functions: []
`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

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
functions: []
`);

    const codeLenses = await ls.getCodeLenses({ uri: "test.sw.yaml", content });

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
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const { content, cursorPosition } = treat(`
---
functions: [🎯]
`);

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.yaml",
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
            documentUri: "test.sw.yaml",
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
    const ls = new SwfYamlLanguageService({
      fs: {},
      serviceCatalog: {
        ...defaultServiceCatalogConfig,
        relative: { getServices: async () => [testRelativeService1] },
      },
      config: defaultConfig,
    });

    const { content, cursorPosition } = treat(`
---
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
      refName: 🎯
`);

    const completionItems = await ls.getCompletionItems({
      uri: "test.sw.yaml",
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
        range: { start: cursorPosition, end: cursorPosition },
      },
      insertTextFormat: InsertTextFormat.Snippet,
    } as CompletionItem);
  });
});

type ContentWithCursor = `${string}🎯${string}`;

function treat(content: ContentWithCursor) {
  const trimmedContent = content.trim();
  const treatedContent = trimmedContent.replace("🎯", "");
  const doc = TextDocument.create("", "yaml", 0, trimmedContent);
  return { content: treatedContent, cursorPosition: doc.positionAt(trimmedContent.indexOf("🎯")) };
}

function trim(content: string) {
  return { content: content.trim() };
}
