/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";

const resourcesPathPrefix = new ChromeRouter().getResourcesPathPrefix();
startExtension({
  name: "Kogito :: Serverless workflow editor",
  extensionIconUrl: chrome.runtime.getURL("/resources/kie_icon_rgb_fullcolor_default.svg"),
  githubAuthTokenCookieName: "github-oauth-token-kie-editors",
  editorEnvelopeLocator: new EditorEnvelopeLocator(window.location.origin, [
    new EnvelopeMapping(
      "sw",
      "**/*.sw.+(json|yml|yaml)",
      `${resourcesPathPrefix}/sw`,
      `${resourcesPathPrefix}/serverless-workflow-editor-envelope.html`
    ),
  ]),
});
