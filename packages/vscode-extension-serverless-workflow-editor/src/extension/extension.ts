/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { backendI18nDefaults, backendI18nDictionaries } from "@kie-tools-core/backend/dist/i18n";
import { VsCodeBackendProxy } from "@kie-tools-core/backend/dist/vscode/VsCodeBackendProxy";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import * as KogitoVsCode from "@kie-tools-core/vscode-extension";
import * as vscode from "vscode";
import { ServerlessWorkflowEditorChannelApiProducer } from "./ServerlessWorkflowEditorChannelApiProducer";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "./configuration";
import { RhhccAuthenticationStore } from "./rhhcc/RhhccAuthenticationStore";
import { askForServiceRegistryUrl } from "./serviceCatalog/rhhccServiceRegistry";
import { COMMAND_IDS } from "./commands";
import { SwfLanguageServiceChannelApiImpl } from "./languageService/SwfLanguageServiceChannelApiImpl";
import * as ls from "vscode-languageserver-types";

let backendProxy: VsCodeBackendProxy;

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const backendI18n = new I18n(backendI18nDefaults, backendI18nDictionaries, vscode.env.language);
  backendProxy = new VsCodeBackendProxy(context, backendI18n);
  const configuration = new SwfVsCodeExtensionConfiguration();
  const rhhccAuthenticationStore = new RhhccAuthenticationStore();
  const swfLanguageService = new SwfLanguageServiceChannelApiImpl();

  KogitoVsCode.startExtension({
    extensionName: "kie-group.vscode-extension-serverless-workflow-editor",
    context: context,
    viewType: "kieKogitoWebviewEditorsServerlessWorkflow",
    generateSvgCommandId: "extension.kogito.swf.getPreviewSvg",
    silentlyGenerateSvgCommandId: "extension.kogito.swf.silentlyGenerateSvg",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping(
        "sw",
        "**/*.sw.+(json|yml|yaml)",
        "dist/webview/ServerlessWorkflowEditorEnvelopeApp.js",
        "dist/webview/editors/serverless-workflow"
      ),
    ]),
    channelApiProducer: new ServerlessWorkflowEditorChannelApiProducer({
      configuration,
      rhhccAuthenticationStore,
      swfLanguageService,
    }),
    backendProxy,
  });

  context.subscriptions.push(
    vscode.languages.registerCodeLensProvider(
      { scheme: "file", language: "serverless-workflow" },
      {
        provideCodeLenses: async (document: vscode.TextDocument, token: vscode.CancellationToken) => {
          const lsCodeLenses = await swfLanguageService.kogitoSwfLanguageService__getCodeLenses({
            uri: document.uri.toString(),
            content: document.getText(),
          });

          const vscodeCodeLenses: vscode.CodeLens[] = lsCodeLenses.map((c) => {
            return new vscode.CodeLens(
              new vscode.Range(
                new vscode.Position(c.range.start.line, c.range.start.character),
                new vscode.Position(c.range.end.line, c.range.end.character)
              ),
              c.command
            );
          });

          return vscodeCodeLenses;
        },
      }
    )
  );

  context.subscriptions.push(
    vscode.languages.registerCompletionItemProvider(
      { scheme: "file", language: "serverless-workflow" },
      {
        provideCompletionItems: async (
          document: vscode.TextDocument,
          position: vscode.Position,
          token: vscode.CancellationToken,
          context: vscode.CompletionContext
        ) => {
          const cursorWordRange = document.getWordRangeAtPosition(position);

          const lsCompletionItems = await swfLanguageService.kogitoSwfLanguageService__getCompletionItems({
            uri: document.uri.toString(),
            content: document.getText(),
            cursorPosition: position,
            cursorWordRange: {
              start: cursorWordRange?.start ?? position,
              end: cursorWordRange?.end ?? position,
            },
          });

          const vscodeCompletionItems: vscode.CompletionItem[] = lsCompletionItems.map((c) => {
            const rangeStart = (c.textEdit as ls.TextEdit).range.start;
            const rangeEnd = (c.textEdit as ls.TextEdit).range.end;
            return {
              kind: c.kind,
              label: c.label,
              sortText: c.sortText,
              detail: c.detail,
              filterText: c.filterText,
              insertText: c.insertText ?? c.textEdit?.newText ?? "",
              command: c.command,
              range: new vscode.Range(
                new vscode.Position(rangeStart.line, rangeStart.character),
                new vscode.Position(rangeEnd.line, rangeEnd.character)
              ),
            };
          });

          return { items: vscodeCompletionItems };
        },
      },
      ` `,
      `:`,
      `"`
    )
  );

  context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.loginToRhhcc, () => {
      vscode.authentication.getSession("redhat-mas-account-auth", ["openid"], { createIfNone: true });
    })
  );

  context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.setupServiceRegistryUrl, async () => {
      const serviceRegistryUrl = await askForServiceRegistryUrl({
        currentValue: configuration.getConfiguredServiceRegistryUrl(),
      });

      if (!serviceRegistryUrl) {
        return;
      }

      vscode.workspace.getConfiguration().update(CONFIGURATION_SECTIONS.serviceRegistryUrl, serviceRegistryUrl);
      vscode.window.setStatusBarMessage("Serverless Workflow: Service Registry URL saved.", 3000);
    })
  );

  context.subscriptions.push(
    vscode.commands.registerCommand(COMMAND_IDS.removeServiceRegistryUrl, () => {
      vscode.workspace.getConfiguration().update(CONFIGURATION_SECTIONS.serviceRegistryUrl, "");
      vscode.window.setStatusBarMessage("Serverless Workflow: Service Registry URL removed.", 3000);
    })
  );

  context.subscriptions.push(
    vscode.authentication.onDidChangeSessions(async (e) => {
      if (e.provider.id === "redhat-mas-account-auth") {
        await updateRhhccAuthenticationSession(rhhccAuthenticationStore);
      }
    })
  );

  updateRhhccAuthenticationSession(rhhccAuthenticationStore);

  console.info("Extension is successfully setup.");
}

async function updateRhhccAuthenticationSession(rhhccAuthenticationStore: RhhccAuthenticationStore) {
  rhhccAuthenticationStore.setSession(await vscode.authentication.getSession("redhat-mas-account-auth", ["openid"]));
}

export function deactivate() {
  backendProxy?.stopServices();
}
