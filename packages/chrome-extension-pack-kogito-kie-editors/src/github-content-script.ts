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
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";

const resourcesPathPrefix = new ChromeRouter().getResourcesPathPrefix();

startExtension({
  name: "Kogito :: BPMN and DMN editors",
  extensionIconUrl: chrome.runtime.getURL("/resources/kie_icon_rgb_fullcolor_default.svg"),
  githubAuthTokenCookieName: "github-oauth-token-kie-editors",
  externalEditorManager: {
    name: "KIE Sandbox",
    getImportRepoUrl: (repoUrl) => {
      //FIXME: The paths are duplicated from `online-editor`.
      return `${process.env.WEBPACK_REPLACE__onlineEditor_url}/#/import?url=${repoUrl}`;
    },
  },
  editorEnvelopeLocator: new EditorEnvelopeLocator(window.location.origin, [
    new EnvelopeMapping({
      type: "bpmn",
      filePathGlob: "**/*.bpmn?(2)",
      resourcesPathPrefix: `${resourcesPathPrefix}/bpmn`,
      envelopeContent: { type: EnvelopeContentType.PATH, path: `${resourcesPathPrefix}/bpmn-envelope.html` },
    }),
    new EnvelopeMapping({
      type: "dmn",
      filePathGlob: "**/*.dmn",
      resourcesPathPrefix: `${resourcesPathPrefix}/dmn`,
      envelopeContent: { type: EnvelopeContentType.PATH, path: `${resourcesPathPrefix}/dmn-envelope.html` },
    }),
    new EnvelopeMapping({
      type: "scesim",
      filePathGlob: "**/*.scesim",
      resourcesPathPrefix: `${resourcesPathPrefix}/scesim`,
      envelopeContent: { type: EnvelopeContentType.PATH, path: `${resourcesPathPrefix}/scesim-envelope.html` },
    }),
  ]),
});
