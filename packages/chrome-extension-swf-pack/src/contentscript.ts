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

import { startExtension } from "@kie-tools-core/chrome-extension-swf";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { ChromeRouter } from "./ChromeRouter";

const resourcesPathPrefix = new ChromeRouter().getResourcesPathPrefix();

startExtension({
  name: "Kogito :: Serverless Workflow Extension",
  imageUris: {
    kie: chrome.runtime.getURL("/resources/kie_icon_rgb_fullcolor_default.svg"),
    serverlessWorkflow: chrome.runtime.getURL("/resources/sw-logo-transparent.png"),
  },
  editorEnvelopeLocator: new EditorEnvelopeLocator(window.location.origin, [
    new EnvelopeMapping(
      "sw",
      "**/*.sw.+(json|yml|yaml)",
      `${resourcesPathPrefix}/`,
      `${resourcesPathPrefix}/envelope/index.html`
    ),
  ]),
});
