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

import { Router, Routes } from "@kogito-tooling/core-api";
import { GwtLanguageData } from "./GwtLanguageData";

export const editors = {
  dmn: {
    id: "DMNDiagramEditor",
    name: "org.kie.workbench.common.dmn.showcase.DMNKogitoRuntimeWebapp"
  },
  bpmn: {
    id: "BPMNDiagramEditor",
    name: "org.kie.workbench.common.stunner.kogito.KogitoBPMNEditor"
  }
};

export class GwtEditorRoutes implements Routes {
  private readonly bpmnPath: string;
  private readonly dmnPath: string;

  constructor(args: { bpmnPath: string; dmnPath: string }) {
    this.bpmnPath = args.bpmnPath;
    this.dmnPath = args.dmnPath;
  }

  public getRoutes(router: Router) {
    const bpmnLanguageData: GwtLanguageData = {
      type: "gwt",
      editorId: editors.bpmn.id,
      gwtModuleName: editors.bpmn.name,
      resources: [
        {
          type: "css",
          paths: [router.getRelativePathTo(`${this.bpmnPath}/${editors.bpmn.name}/css/patternfly.min.css`)]
        },
        {
          type: "js",
          paths: [
            router.getRelativePathTo(`${this.bpmnPath}/${editors.bpmn.name}/ace/ace.js`),
            router.getRelativePathTo(`${this.bpmnPath}/${editors.bpmn.name}/ace/mode-xml.js`),
            router.getRelativePathTo(`${this.bpmnPath}/${editors.bpmn.name}/ace/theme-chrome.js`),
            router.getRelativePathTo(`${this.bpmnPath}/${editors.bpmn.name}/${editors.bpmn.name}.nocache.js`)
          ]
        }
      ]
    };
    const dmnLanguageData: GwtLanguageData = {
      type: "gwt",
      editorId: editors.dmn.id,
      gwtModuleName: editors.dmn.name,
      resources: [
        {
          type: "css",
          paths: [router.getRelativePathTo(`${this.dmnPath}/${editors.dmn.name}/css/patternfly.min.css`)]
        },
        {
          type: "js",
          paths: [
            router.getRelativePathTo(`${this.dmnPath}/model/Jsonix-all.js`),
            router.getRelativePathTo(`${this.dmnPath}/model/DC.js`),
            router.getRelativePathTo(`${this.dmnPath}/model/DI.js`),
            router.getRelativePathTo(`${this.dmnPath}/model/DMNDI12.js`),
            router.getRelativePathTo(`${this.dmnPath}/model/DMN12.js`),
            router.getRelativePathTo(`${this.dmnPath}/model/KIE.js`),
            router.getRelativePathTo(`${this.dmnPath}/model/MainJs.js`),
            router.getRelativePathTo(`${this.dmnPath}/${editors.dmn.name}/ace/ace.js`),
            router.getRelativePathTo(`${this.dmnPath}/${editors.dmn.name}/ace/mode-xml.js`),
            router.getRelativePathTo(`${this.dmnPath}/${editors.dmn.name}/ace/theme-chrome.js`),
            router.getRelativePathTo(`${this.dmnPath}/${editors.dmn.name}/${editors.dmn.name}.nocache.js`)
          ]
        }
      ]
    };

    return new Map<string, GwtLanguageData>([
      ["dmn", dmnLanguageData],
      ["bpmn", bpmnLanguageData],
      ["bpmn2", bpmnLanguageData]
    ]);
  }
}
