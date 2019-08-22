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
import { gwtEditors, GwtLanguageData } from "appformer-js-gwt-editors-common";

const dmnLocation = "dmn";
const bpmnLocation = "bpmn";

export class GwtEditorChromeExtensionRoutes implements Routes {
  public getRoutes(router: Router) {
    return new Map<string, GwtLanguageData>([
      [
        "dmn",
        {
          type: "gwt",
          editorId: gwtEditors.dmn.id,
          gwtModuleName: gwtEditors.dmn.name,
          resources: [
            {
              type: "css",
              paths: [router.getRelativePathTo(`${dmnLocation}${gwtEditors.dmn.name}/css/patternfly.min.css`)]
            },
            {
              type: "js",
              paths: [
                router.getRelativePathTo(`${dmnLocation}/${gwtEditors.dmn.name}/ace/ace.js`),
                router.getRelativePathTo(`${dmnLocation}/${gwtEditors.dmn.name}/ace/theme-chrome.js`),
                router.getRelativePathTo(`${dmnLocation}/${gwtEditors.dmn.name}/${gwtEditors.dmn.name}.nocache.js`)
              ]
            }
          ]
        }
      ],
      [
        "bpmn",
        {
          type: "gwt",
          editorId: gwtEditors.bpmn.id,
          gwtModuleName: gwtEditors.bpmn.name,
          resources: [
            {
              type: "css",
              paths: [router.getRelativePathTo(`${bpmnLocation}/${gwtEditors.bpmn.name}/css/patternfly.min.css`)]
            },
            {
              type: "js",
              paths: [
                router.getRelativePathTo(`${bpmnLocation}/${gwtEditors.bpmn.name}/ace/ace.js`),
                router.getRelativePathTo(`${bpmnLocation}/${gwtEditors.bpmn.name}/ace/theme-chrome.js`),
                router.getRelativePathTo(`${bpmnLocation}/${gwtEditors.bpmn.name}/${gwtEditors.bpmn.name}.nocache.js`)
              ]
            }
          ]
        }
      ]
    ]);
  }
}
