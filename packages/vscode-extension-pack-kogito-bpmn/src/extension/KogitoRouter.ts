/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { Router } from "@kogito-tooling/core-api";
import * as vscode from "vscode";
import * as __path from "path";
import { KogitoLanguageData } from "../common/KogitoLanguageData";

const bpmnGwtModuleName = "org.kie.workbench.common.stunner.kogito.KogitoBPMNEditor";
const bpmnDistPath = `dist/webview/editors/bpmn/`;

export class KogitoRouter implements Router<KogitoLanguageData> {
  private readonly context: vscode.ExtensionContext;
  private readonly languageDataByFileExtension: Map<string, KogitoLanguageData>;

  constructor(context: vscode.ExtensionContext) {
    this.context = context;
    const bpmnLanguageData: KogitoLanguageData = {
      type: "gwt",
      editorId: "BPMNDiagramEditor",
      gwtModuleName: bpmnGwtModuleName,
      erraiDomain: "",
      resources: [
        {
          type: "css",
          paths: [this.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/css/patternfly.min.css`)]
        },
        {
          type: "js",
          paths: [
            this.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/ace/ace.js`),
            this.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/ace/theme-chrome.js`),
            this.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/${bpmnGwtModuleName}.nocache.js`)
          ]
        }
      ]
    };

    this.languageDataByFileExtension = new Map<string, KogitoLanguageData>([
      ["bpmn", bpmnLanguageData],
      ["bpmn2", bpmnLanguageData]
    ]);
  }

  public getRelativePathTo(uri: string) {
    return vscode.Uri.file(__path.join(this.context.extensionPath, ...uri.split("/")))
      .with({ scheme: "vscode-resource" })
      .toString();
  }

  public getLanguageData(fileExtension: string) {
    return this.languageDataByFileExtension.get(fileExtension);
  }
}
