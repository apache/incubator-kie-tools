/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// eslint-disable-next-line @typescript-eslint/triple-slash-reference
/// <reference path="./global.d.ts" /> // Required for bundling types

import dmnEnvelopeJs from "../dist/envelope.js";
import { StateControl } from "@kie-tools-core/editor/dist/channel";
import { DmnEditorStandaloneChannelApiImpl, DmnEditorStandaloneResource } from "./DmnEditorStandaloneChannelApiImpl";
import { DmnEditorStandaloneApi } from "./DmnEditorStandaloneApi";
import { createEnvelopeServer } from "./DmnEditorStandaloneEnvelopeServer";
import { createEditor } from "./DmnEditorStandaloneApiImpl";
import { basename } from "path";

export const DEFAULT_DMN_MODEL_POSIX_FILE_PATH_RELATIVE_TO_WORKSPACE_ROOT = "model.dmn";

export function open(args: {
  container: Element;
  initialContent: Promise<string>;
  initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot?: string;
  readOnly?: boolean;
  origin?: string;
  onError?: () => any;
  resources?: Map<string, DmnEditorStandaloneResource>;
}): DmnEditorStandaloneApi {
  const iframe = document.createElement("iframe");
  iframe.srcdoc = `
<!doctype html>
<html lang="en">
  <head>
    <style>
      html,
      body,
      div#envelope-app {
        margin: 0;
        border: 0;
        padding: 0;
        overflow: hidden;
        height: 100%;
        width: 100%;
      }
      body {
        background-color: #fff !important;
      }
    </style>

    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <script type="text/javascript">
      ${dmnEnvelopeJs}
    </script>
  </head>
  <body>
    <div id="envelope-app" />
  </body>
</html>
  `;
  iframe.id = "dmn-editor-standalone";
  iframe.style.width = "100%";
  iframe.style.height = "100%";
  iframe.style.border = "none";

  const envelopeServer = createEnvelopeServer(iframe, args.readOnly, args.origin);

  const stateControl = new StateControl();

  let receivedSetContentError = false;

  const initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot =
    args.initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot ??
    DEFAULT_DMN_MODEL_POSIX_FILE_PATH_RELATIVE_TO_WORKSPACE_ROOT;

  const channelApiImpl = new DmnEditorStandaloneChannelApiImpl(
    stateControl,
    {
      normalizedPosixPathRelativeToTheWorkspaceRoot: initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot,
      fileName: basename(initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot),
      fileExtension: initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot.split(".").at(-1) as string,
      getFileContents: () => Promise.resolve(args.initialContent),
      isReadOnly: args.readOnly ?? false,
    },
    "en-US",
    {
      kogitoEditor_setContentError() {
        if (!receivedSetContentError) {
          args.onError?.();
          receivedSetContentError = true;
        }
      },
    },
    args.resources
  );

  const listener = (message: MessageEvent) => {
    envelopeServer.receive(message.data, channelApiImpl);
  };

  window.addEventListener("message", listener);

  args.container.appendChild(iframe);
  envelopeServer.startInitPolling(channelApiImpl);

  const editor = createEditor(envelopeServer.envelopeApi, stateControl, listener, iframe);

  return editor;
}

declare global {
  interface Window {
    DmnEditor: { open: typeof open };
  }
}

window.DmnEditor = { open };
