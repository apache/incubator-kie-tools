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

// This is a fix for using isomorphic-git (it needs buffer, but chrome screws with it)
// https://github.com/agoncal/swagger-ui-angular6/issues/2
(window as any).global = window;
// eslint-disable-next-line @typescript-eslint/no-var-requires
global.Buffer = global.Buffer || require("buffer").Buffer;

const resourcesPathPrefix = new ChromeRouter().getResourcesPathPrefix();

startExtension({
  name: "Kogito :: Serverless Workflow Extension",
  imagesUriPath: chrome.runtime.getURL("/images"),
  resourcesUriPath: chrome.runtime.getURL("/resources"),
  editorEnvelopeLocator: new EditorEnvelopeLocator(window.location.origin, [
    new EnvelopeMapping(
      "sw",
      "**/*.sw.+(json|yml|yaml)",
      `${resourcesPathPrefix}/`,
      `${resourcesPathPrefix}/envelope/index.html`
    ),
  ]),
});
