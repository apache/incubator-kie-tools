/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { WorkspacesWorkerApiImpl } from "@kie-tools-core/workspaces-git-fs/dist/worker/WorkspacesWorkerApiImpl";
import { setupWorkerConnection } from "@kie-tools-core/workspaces-git-fs/dist/worker/setupWorkerConnection";
import { ENV_FILE_PATH } from "../../env/EnvConstants";
import { EditorEnvelopeLocatorFactory } from "../../envelopeLocator/EditorEnvelopeLocatorFactory";
import { EnvVars } from "../../env/hooks/EnvContext";

export const WORKSPACES_SHARED_WORKER_SCRIPT_URL = "workspace/worker/sharedWorker.js";

declare const importScripts: any;
importScripts("fsMain.js");

async function corsProxyUrl() {
  const envFilePath = `../../${ENV_FILE_PATH}`; // Needs to go back two dirs, since this file is at `workspaces/worker`.
  const env = (await (await fetch(envFilePath)).json()) as EnvVars;
  return env.CORS_PROXY_URL ?? process.env.WEBPACK_REPLACE__corsProxyUrl ?? "";
}

const editorEnvelopeLocator = new EditorEnvelopeLocatorFactory().create({ targetOrigin: "" });

declare let onconnect: any;
// eslint-disable-next-line prefer-const
onconnect = async (e: MessageEvent) => {
  console.log(`Connected to Workspaces Shared Worker`);

  setupWorkerConnection({
    apiImpl: new WorkspacesWorkerApiImpl({
      corsProxyUrl: await corsProxyUrl(),
      gitDefaultUser: {
        name: "KIE Sandbox",
        email: "",
      },
      isModelFn: (path) => editorEnvelopeLocator.hasMappingFor(path),
      isEditableFn: (path) => editorEnvelopeLocator.hasMappingFor(path),
    }),
    port: e.ports[0],
  });
};
