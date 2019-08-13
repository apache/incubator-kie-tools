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

const dmnGwtModuleName = "org.kie.workbench.common.dmn.showcase.DMNShowcase";
const dmnDistPath = `dist/webview/editors/dmn/`;
const bpmnGwtModuleName = "org.kie.workbench.common.stunner.standalone.StunnerStandaloneShowcase";
const bpmnDistPath = `dist/webview/editors/bpmn/`;

export class GwtEditorRoutes implements Routes {
  public getRoutes(router: Router) {
    return new Map<string, GwtLanguageData>([
      [
        "dmn",
        {
          type: "gwt",
          editorId: "DMNDiagramEditor",
          gwtModuleName: dmnGwtModuleName,
          erraiDomain: "",
          resources: [
            {
              type: "css",
              paths: [router.getRelativePathTo(`${dmnDistPath}${dmnGwtModuleName}/css/patternfly.min.css`)]
            },
            {
              type: "js",
              paths: [
                router.getRelativePathTo(`${dmnDistPath}/${dmnGwtModuleName}/ace/ace.js`),
                router.getRelativePathTo(`${dmnDistPath}/${dmnGwtModuleName}/ace/theme-chrome.js`),
                router.getRelativePathTo(`${dmnDistPath}/${dmnGwtModuleName}/${dmnGwtModuleName}.nocache.js`)
              ]
            }
          ]
        }
      ],
      [
        //FIXME: BPMN doesn't have a client-side only editor yet.
        "bpmn",
        {
          type: "gwt",
          editorId: "BPMNStandaloneDiagramEditor",
          gwtModuleName: bpmnGwtModuleName,
          erraiDomain: "",
          resources: [
            {
              type: "css",
              paths: [router.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/css/patternfly.min.css`)]
            },
            {
              type: "js",
              paths: [
                router.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/ace/ace.js`),
                router.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/ace/theme-chrome.js`),
                router.getRelativePathTo(`${bpmnDistPath}/${bpmnGwtModuleName}/${bpmnGwtModuleName}.nocache.js`)
              ]
            }
          ]
        }
      ]
    ]);
  }
}
