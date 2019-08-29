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

import { Router, Routes } from "appformer-js-core";
import { GwtLanguageData } from "./GwtLanguageData";

export const editors = {
  dmn: {
    id: "DMNDiagramEditor",
    name: "org.kie.workbench.common.dmn.showcase.DMNShowcase"
  },
  bpmn: {
    id: "BPMNDiagramEditor",
    name: "org.kie.workbench.common.stunner.kogito.KogitoBPMNEditor"
  }
};

const bpmnDistPath = `dist/webview/editors/bpmn/`;
const dmnDistPath = `dist/webview/editors/dmn/`;

export class GwtEditorRoutes implements Routes {
  public getRoutes(router: Router) {
    const bpmnLanguageData: GwtLanguageData = {
      type: "gwt",
      editorId: editors.bpmn.id,
      gwtModuleName: editors.bpmn.name,
      resources: [
        {
          type: "css",
          paths: [router.getRelativePathTo(`${bpmnDistPath}/${editors.bpmn.name}/css/patternfly.min.css`)]
        },
        {
          type: "js",
          paths: [
            router.getRelativePathTo(`${bpmnDistPath}/${editors.bpmn.name}/ace/ace.js`),
            router.getRelativePathTo(`${bpmnDistPath}/${editors.bpmn.name}/ace/theme-chrome.js`),
            router.getRelativePathTo(`${bpmnDistPath}/${editors.bpmn.name}/${editors.bpmn.name}.nocache.js`)
          ]
        }
      ]
    };

    return new Map<string, GwtLanguageData>([["bpmn", bpmnLanguageData], ["bpmn2", bpmnLanguageData]]);
  }
}
