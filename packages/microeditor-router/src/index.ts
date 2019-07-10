/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { LanguageData } from "./LanguageData";

export * from "./LanguageData";

export const services = {
  microeditor_envelope: "http://localhost:9000",
  web: "http://localhost:9001",
  web_backend: "http://localhost:9002",
  microeditor_dmn: "http://localhost:9003",
  microeditor_bpmn: "http://localhost:9005",
  functions: "http://localhost:9004",
  dmn_knative: "http://dmn-quarkus-knative-builder.submarine.apps.porcelli.devcluster.openshift.com"
};

export const router = new Map<string, LanguageData>([
  [
    "dmn",
    {
      editorId: "DMNDiagramEditor",
      gwtModuleName: "org.kie.workbench.common.dmn.showcase.DMNShowcase",
      erraiDomain: services.microeditor_dmn, //where backend of this editor is running
      resources: [
        {
          type: "css",
          paths: [
            `${services.microeditor_dmn}/org.kie.workbench.common.dmn.showcase.DMNShowcase/css/patternfly.min.css`
          ]
        },
        {
          type: "js",
          paths: [
            `${services.microeditor_dmn}/org.kie.workbench.common.dmn.showcase.DMNShowcase/ace/ace.js`,
            `${services.microeditor_dmn}/org.kie.workbench.common.dmn.showcase.DMNShowcase/ace/theme-chrome.js`,
            `${
              services.microeditor_dmn
            }/org.kie.workbench.common.dmn.showcase.DMNShowcase/org.kie.workbench.common.dmn.showcase.DMNShowcase.nocache.js`
          ]
        }
      ]
    }
  ],
  [
    "bpmn",
    {
      editorId: "BPMNStandaloneDiagramEditor",
      gwtModuleName: "org.kie.workbench.common.stunner.standalone.StunnerStandaloneShowcase",
      erraiDomain: services.microeditor_bpmn, //where backend of this editor is running
      resources: [
        {
          type: "css",
          paths: [
            `${
              services.microeditor_bpmn
            }/org.kie.workbench.common.stunner.standalone.StunnerStandaloneShowcase/css/patternfly.min.css`
          ]
        },
        {
          type: "js",
          paths: [
            `${
              services.microeditor_bpmn
            }/org.kie.workbench.common.stunner.standalone.StunnerStandaloneShowcase/ace/ace.js`,
            `${
              services.microeditor_bpmn
            }/org.kie.workbench.common.stunner.standalone.StunnerStandaloneShowcase/ace/theme-chrome.js`,
            `${
              services.microeditor_bpmn
            }/org.kie.workbench.common.stunner.standalone.StunnerStandaloneShowcase/org.kie.workbench.common.stunner.standalone.StunnerStandaloneShowcase.nocache.js`
          ]
        }
      ]
    }
  ]
]);
