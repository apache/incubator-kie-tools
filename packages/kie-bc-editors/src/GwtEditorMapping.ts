/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { EditorInitArgs } from "@kogito-tooling/editor/dist/api";
import { GwtLanguageData } from "./GwtLanguageData";

export const editors = {
  dmn: {
    id: "DMNDiagramEditor",
    name: "org.kie.workbench.common.dmn.showcase.DMNKogitoRuntimeWebapp"
  },
  bpmn: {
    id: "BPMNDiagramEditor",
    name: "org.kie.workbench.common.stunner.kogito.KogitoBPMNEditor"
  },
  scesim: {
    id: "ScenarioSimulationEditor",
    name: "org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime"
  }
};

export class GwtEditorMapping {
  public getLanguageData(editorInitArgs: EditorInitArgs) {
    const bpmnLanguageData: GwtLanguageData = {
      type: "gwt",
      editorId: editors.bpmn.id,
      gwtModuleName: editors.bpmn.name,
      resources: [
        {
          type: "css",
          paths: [`${editorInitArgs.resourcesPathPrefix}/${editors.bpmn.name}/css/patternfly.min.css`]
        },
        {
          type: "js",
          paths: [
            `${editorInitArgs.resourcesPathPrefix}/${editors.bpmn.name}/ace/ace.js`,
            `${editorInitArgs.resourcesPathPrefix}/${editors.bpmn.name}/ace/mode-xml.js`,
            `${editorInitArgs.resourcesPathPrefix}/${editors.bpmn.name}/ace/theme-chrome.js`,
            `${editorInitArgs.resourcesPathPrefix}/${editors.bpmn.name}/${editors.bpmn.name}.nocache.js`
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
          paths: [`${editorInitArgs.resourcesPathPrefix}/${editors.dmn.name}/css/patternfly.min.css`]
        },
        {
          type: "js",
          paths: [
            `${editorInitArgs.resourcesPathPrefix}/model/Jsonix-all.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/DC.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/DI.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/DMNDI12.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/DMN12.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/KIE.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/MainJs.js`,
            `${editorInitArgs.resourcesPathPrefix}/${editors.dmn.name}/ace/ace.js`,
            `${editorInitArgs.resourcesPathPrefix}/${editors.dmn.name}/ace/mode-xml.js`,
            `${editorInitArgs.resourcesPathPrefix}/${editors.dmn.name}/ace/theme-chrome.js`,
            `${editorInitArgs.resourcesPathPrefix}/${editors.dmn.name}/${editors.dmn.name}.nocache.js`
          ]
        }
      ]
    };
    const scesimLanguageData: GwtLanguageData = {
      type: "gwt",
      editorId: editors.scesim.id,
      gwtModuleName: editors.scesim.name,
      resources: [
        {
          type: "css",
          paths: [`${editorInitArgs.resourcesPathPrefix}/${editors.scesim.name}/css/patternfly.min.css`]
        },
        {
          type: "js",
          paths: [
            `${editorInitArgs.resourcesPathPrefix}/model/Jsonix-all.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/DC.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/DI.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/DMNDI12.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/DMN12.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/KIE.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/MainJs.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/SCESIM.js`,
            `${editorInitArgs.resourcesPathPrefix}/model/SCESIMMainJs.js`,
            `${editorInitArgs.resourcesPathPrefix}/${editors.scesim.name}/${editors.scesim.name}.nocache.js`
          ]
        }
      ]
    };

    return new Map<string, GwtLanguageData>([
      ["dmn", dmnLanguageData],
      ["bpmn", bpmnLanguageData],
      ["bpmn2", bpmnLanguageData],
      ["scesim", scesimLanguageData]
    ]).get(editorInitArgs.fileExtension);
  }
}
