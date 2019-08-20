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
const bpmnGwtModuleName = "org.kie.workbench.common.stunner.kogito.StunnerStandaloneShowcase";
export class GwtEditorRoutes implements Routes {
  private readonly dmnLocation: string;
  private readonly bpmnLocation: string;

  constructor(args: { dmnLocation: string; bpmnLocation: string }) {
    this.dmnLocation = args.dmnLocation;
    this.bpmnLocation = args.bpmnLocation;
  }

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
              paths: [router.getRelativePathTo(`${this.dmnLocation}${dmnGwtModuleName}/css/patternfly.min.css`)]
            },
            {
              type: "js",
              paths: [
                router.getRelativePathTo(`${this.dmnLocation}/${dmnGwtModuleName}/ace/ace.js`),
                router.getRelativePathTo(`${this.dmnLocation}/${dmnGwtModuleName}/ace/theme-chrome.js`),
                router.getRelativePathTo(`${this.dmnLocation}/${dmnGwtModuleName}/${dmnGwtModuleName}.nocache.js`)
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
          editorId: "BPMNDiagramEditor",
          gwtModuleName: bpmnGwtModuleName,
          erraiDomain: "",
          resources: [
            {
              type: "css",
              paths: [router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/css/patternfly.min.css`)]
            },
            {
              type: "js",
              paths: [
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/js/jquery-1.12.0.min.cache.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/js/bootstrap-3.3.6.min.cache.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/ace/ace.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/ace/theme-chrome.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/ace/ext-language_tools.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/bootstrap-select/js/bootstrap-select.min.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/js/bootstrap-datetimepicker-2.3.8.min.cache.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/js/patternfly.min.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/polyfill/sha_512.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/polyfill/enc_b64.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/js/typeahead.jquery-0.10.5.min.cache.js`),
                router.getRelativePathTo(`${this.bpmnLocation}/${bpmnGwtModuleName}/${bpmnGwtModuleName}.nocache.js`)
              ]
            }
          ]
        }
      ]
    ]);
  }
}
