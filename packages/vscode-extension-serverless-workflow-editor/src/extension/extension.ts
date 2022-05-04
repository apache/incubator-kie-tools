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
import * as KieToolsVsCodeExtensions from "@kie-tools-core/vscode-extension";
import * as vscode from "vscode";
import { ServerlessWorkflowEditorChannelApiProducer } from "./ServerlessWorkflowEditorChannelApiProducer";
import { SwfVsCodeExtensionConfiguration, WEBVIEW_EDITOR_VIEW_TYPE } from "./configuration";
import { RhhccAuthenticationStore } from "./rhhcc/RhhccAuthenticationStore";
import { setupServiceRegistryIntegrationCommands } from "./serviceCatalog/serviceRegistryCommands";
import { VsCodeSwfLanguageService } from "./languageService/VsCodeSwfLanguageService";
import { SwfServiceCatalogStore } from "./serviceCatalog/SwfServiceCatalogStore";
import { RhhccServiceRegistryServiceCatalogStore } from "./serviceCatalog/rhhccServiceRegistry/RhhccServiceRegistryServiceCatalogStore";
import { setupBuiltInVsCodeEditorSwfContributions } from "./builtInVsCodeEditorSwfContributions";
import { SwfServiceCatalogSupportActions } from "./serviceCatalog/SwfServiceCatalogSupportActions";
import { setupDiagramEditorControls } from "./setupDiagramEditorControls";
import { COMMAND_IDS } from "./commandIds";
import { setupRhhccAuthenticationStore } from "./rhhcc/setupRhhccAuthenticationStore";

export async function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const backendI18n = new I18n(backendI18nDefaults, backendI18nDictionaries, vscode.env.language);
  const backendProxy = new VsCodeBackendProxy(context, backendI18n);
  context.subscriptions.push(
    new vscode.Disposable(() => {
      return backendProxy.stopServices();
    })
  );

  const configuration = new SwfVsCodeExtensionConfiguration();

  const rhhccAuthenticationStore = new RhhccAuthenticationStore();

  const rhhccServiceRegistryServiceCatalogStore = new RhhccServiceRegistryServiceCatalogStore({
    rhhccAuthenticationStore,
    configuration,
  });

  // FIXME: editor does not open with it
  // await setupRhhccAuthenticationStore({
  //   context,
  //   configuration,
  //   rhhccAuthenticationStore,
  //   rhhccServiceRegistryServiceCatalogStore,
  // });

  const swfServiceCatalogGlobalStore = new SwfServiceCatalogStore({ rhhccServiceRegistryServiceCatalogStore });
  await swfServiceCatalogGlobalStore.init();
  console.info(
    `SWF Service Catalog global store successfully initialized with ${swfServiceCatalogGlobalStore.storedServices.length} services.`
  );

  const vsCodeSwfLanguageService = new VsCodeSwfLanguageService({
    configuration,
    rhhccAuthenticationStore,
    swfServiceCatalogGlobalStore,
  });
  context.subscriptions.push(vsCodeSwfLanguageService);

  const swfServiceCatalogSupportActions = new SwfServiceCatalogSupportActions({
    configuration,
    swfServiceCatalogGlobalStore,
  });

  const kieToolsEditorStore = await KieToolsVsCodeExtensions.startExtension({
    editorDocumentType: "text",
    extensionName: "kie-group.vscode-extension-serverless-workflow-editor",
    context: context,
    viewType: WEBVIEW_EDITOR_VIEW_TYPE,
    generateSvgCommandId: COMMAND_IDS.getPreviewSvg,
    silentlyGenerateSvgCommandId: COMMAND_IDS.silentlyGetPreviewSvg,
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping(
        "sw",
        "**/*.sw.+(json|yml|yaml)",
        "dist/webview/ServerlessWorkflowDiagramEditorEnvelopeApp.js",
        "dist/webview/editors/serverless-workflow-diagram"
      ),
    ]),
    channelApiProducer: new ServerlessWorkflowEditorChannelApiProducer({
      configuration,
      rhhccAuthenticationStore,
      swfLanguageService: vsCodeSwfLanguageService.ls,
      swfServiceCatalogSupportActions,
    }),
    backendProxy,
  });

  setupBuiltInVsCodeEditorSwfContributions({
    context,
    swfLanguageService: vsCodeSwfLanguageService.ls,
    configuration,
    swfServiceCatalogGlobalStore,
    swfServiceCatalogSupportActions,
  });

  setupServiceRegistryIntegrationCommands({
    context,
    configuration,
  });

  await setupDiagramEditorControls({
    context,
    configuration,
    kieToolsEditorStore,
  });

  console.info("Extension is successfully setup.");
}
