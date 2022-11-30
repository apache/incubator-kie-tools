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

import { backendI18nDefaults, backendI18nDictionaries } from "@kie-tools-core/backend/dist/i18n";
import { VsCodeBackendProxy } from "@kie-tools-core/backend/dist/vscode/VsCodeBackendProxy";
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import * as KieToolsVsCodeExtensions from "@kie-tools-core/vscode-extension";
import * as vscode from "vscode";
import { ServerlessWorkflowDiagramEditorChannelApiProducer } from "./ServerlessWorkflowDiagramEditorChannelApiProducer";
import { SwfVsCodeExtensionConfiguration, WEBVIEW_EDITOR_VIEW_TYPE } from "./configuration";
import { setupServiceRegistryIntegrationCommands } from "./serviceCatalog/serviceRegistryCommands";
import { VsCodeSwfLanguageService } from "./languageService/VsCodeSwfLanguageService";
import { SwfServiceCatalogStore } from "./serviceCatalog/SwfServiceCatalogStore";
import { setupBuiltInVsCodeEditorSwfContributions } from "./builtInVsCodeEditorSwfContributions";
import { SwfServiceCatalogSupportActions } from "./serviceCatalog/SwfServiceCatalogSupportActions";
import { setupDiagramEditorCompanionTab } from "./setupDiagramEditorCompanionTab";
import { COMMAND_IDS } from "./commandIds";
import { ServiceRegistriesStore } from "./serviceCatalog/serviceRegistry";
import { RedHatAuthExtensionStateStore } from "./RedHatAuthExtensionStateStore";

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

  const redhatAuthExtensionStateStore = new RedHatAuthExtensionStateStore();
  context.subscriptions.push(redhatAuthExtensionStateStore);

  const serviceRegistriesStore = new ServiceRegistriesStore({
    redhatAuthExtensionStateStore,
    configuration,
    context,
  });

  const swfServiceCatalogGlobalStore = new SwfServiceCatalogStore({ serviceRegistriesStore: serviceRegistriesStore });
  await swfServiceCatalogGlobalStore.init();
  console.info(
    `SWF Service Catalog global store successfully initialized with ${swfServiceCatalogGlobalStore.storedServices.length} services.`
  );

  const vsCodeSwfLanguageService = new VsCodeSwfLanguageService({
    configuration,
    swfServiceCatalogGlobalStore,
  });

  context.subscriptions.push(vsCodeSwfLanguageService);

  const swfServiceCatalogSupportActions = new SwfServiceCatalogSupportActions({
    configuration,
    swfServiceCatalogGlobalStore,
  });

  const swEnvelopeType = "swf";
  const baseEnvelopePath = "dist/webview/editors/serverless-workflow";

  const diagramEnvelopeMappingConfig = configuration.isKogitoServerlessWorkflowVisualizationPreviewEnabled()
    ? {
        resourcesPathPrefix: baseEnvelopePath + "/diagram",
        envelopePath: baseEnvelopePath + "/serverless-workflow-diagram-editor-envelope.js",
      }
    : {
        resourcesPathPrefix: baseEnvelopePath,
        envelopePath: baseEnvelopePath + "/serverless-workflow-mermaid-viewer-envelope.js",
      };

  const kieEditorsStore = await KieToolsVsCodeExtensions.startExtension({
    editorDocumentType: "text",
    extensionName: "kie-group.vscode-extension-serverless-workflow-editor",
    context: context,
    viewType: WEBVIEW_EDITOR_VIEW_TYPE,
    generateSvgCommandId: COMMAND_IDS.getPreviewSvg,
    silentlyGenerateSvgCommandId: COMMAND_IDS.silentlyGetPreviewSvg,
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: swEnvelopeType,
        filePathGlob: "**/*.sw.json",
        resourcesPathPrefix: diagramEnvelopeMappingConfig.resourcesPathPrefix,
        envelopeContent: { type: EnvelopeContentType.PATH, path: diagramEnvelopeMappingConfig.envelopePath },
      }),
      new EnvelopeMapping({
        type: swEnvelopeType,
        filePathGlob: "**/*.sw.+(yml|yaml)",
        resourcesPathPrefix: baseEnvelopePath,
        envelopeContent: {
          type: EnvelopeContentType.PATH,
          path: baseEnvelopePath + "/serverless-workflow-mermaid-viewer-envelope.js",
        },
      }),
    ]),
    channelApiProducer: new ServerlessWorkflowDiagramEditorChannelApiProducer({
      configuration,
      vsCodeSwfLanguageService,
      swfServiceCatalogSupportActions,
    }),
    backendProxy,
  });

  setupBuiltInVsCodeEditorSwfContributions({
    context,
    vsCodeSwfLanguageService,
    configuration,
    swfServiceCatalogSupportActions,
    kieEditorsStore,
  });

  setupServiceRegistryIntegrationCommands({
    context,
    configuration,
    serviceRegistryStore: serviceRegistriesStore,
  });

  await setupDiagramEditorCompanionTab({
    context,
    configuration,
    kieEditorsStore,
  });

  console.info("Extension is successfully setup.");
}
