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

import { ChromeRouter } from "./ChromeRouter";
import { startExtension } from "@kie-tools-core/chrome-extension";

const resourcesPathPrefix = new ChromeRouter().getResourcesPathPrefix();

const bpmnEnvelope = {
  resourcesPathPrefix: `${resourcesPathPrefix}/bpmn`,
  envelopePath: `${resourcesPathPrefix}/bpmn-envelope.html`,
};

const dmnEnvelope = {
  resourcesPathPrefix: `${resourcesPathPrefix}/dmn`,
  envelopePath: `${resourcesPathPrefix}/dmn-envelope.html`,
};

const scesimEnvelope = {
  resourcesPathPrefix: `${resourcesPathPrefix}/scesim`,
  envelopePath: `${resourcesPathPrefix}/scesim-envelope.html`,
};

startExtension({
  name: "Kogito :: BPMN and DMN editors",
  extensionIconUrl: chrome.extension.getURL("/resources/kie_icon_rgb_fullcolor_default.svg"),
  githubAuthTokenCookieName: "github-oauth-token-kie-editors",
  externalEditorManager: {
    name: "KIE Sandbox",
    getImportRepoUrl: (repoUrl) => {
      //FIXME: The paths are duplicated from `online-editor`.
      return `${process.env.WEBPACK_REPLACE__onlineEditor_url}/#/import?url=${repoUrl}`;
    },
  },
  editorEnvelopeLocator: {
    targetOrigin: window.location.origin,
    mapping: new Map([
      ["bpmn", bpmnEnvelope],
      ["bpmn2", bpmnEnvelope],
      ["BPMN", bpmnEnvelope],
      ["BPMN2", bpmnEnvelope],
      ["dmn", dmnEnvelope],
      ["DMN", dmnEnvelope],
      ["scesim", scesimEnvelope],
      ["SCESIM", scesimEnvelope],
    ]),
  },
});
