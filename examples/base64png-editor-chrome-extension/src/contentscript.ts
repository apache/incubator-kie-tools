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

import { startExtension } from "@kie-tools-core/chrome-extension";
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";

const resourcesPathPrefix = process.env["WEBPACK_REPLACE__targetOrigin"];

/**
 * Starts the extension and set initial properties:
 *
 * @params args.name The extension name
 * @params args.extensionIconUrl The extension icon that will be displayed on the chrome://extension page.
 * @params args.githubAuthTokenCookieName The name of the cookie that will be set when using the github oauth. This is required to open files on private repos.
 * @params args.editorEnvelopeLocator.targetOrigin The initial path of the envelope.
 * @params args.editorEnvelopeLocator.mapping A map associating a file extension with the respective envelope path and resources path.
 */
startExtension({
  name: "Kogito Base64 PNG React Editor",
  extensionIconUrl: chrome.extension.getURL("/resources/kie_icon_rgb_fullcolor_default.svg"),
  githubAuthTokenCookieName: "github-oauth-token-base64-editors",
  editorEnvelopeLocator: new EditorEnvelopeLocator(window.location.origin, [
    new EnvelopeMapping({
      type: "base64png",
      filePathGlob: "**/*.base64png",
      resourcesPathPrefix: `${resourcesPathPrefix}/dist/`,
      envelopeContent: { type: EnvelopeContentType.PATH, path: `${resourcesPathPrefix}/dist/envelope/index.html` },
    }),
  ]),
});
