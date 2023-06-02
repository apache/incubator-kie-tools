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

import { createWorkspaceServices } from "@kie-tools-core/workspaces-git-fs/dist/worker/createWorkspaceServices";
import { setupWorkerConnection } from "@kie-tools-core/workspaces-git-fs/dist/worker/setupWorkerConnection";
import { WorkspacesWorkerApiImpl } from "@kie-tools-core/workspaces-git-fs/dist/worker/WorkspacesWorkerApiImpl";
import { ENV_FILE_PATH } from "../../env/EnvConstants";
import { EnvJson } from "../../env/EnvJson";
import { EditorEnvelopeLocatorFactory } from "../../envelopeLocator/EditorEnvelopeLocatorFactory";
import { EditorConfig } from "../../envelopeLocator/EditorEnvelopeLocatorApi";

declare const importScripts: any;
importScripts("fsMain.js");

async function gitCorsProxyUrl(): Promise<string> {
  const envFilePath = `../../${ENV_FILE_PATH}`; // Needs to go back two dirs, since this file is at `workspaces/worker`.
  const env = (await (await fetch(envFilePath)).json()) as EnvJson;
  return env.KIE_SANDBOX_GIT_CORS_PROXY_URL;
}

async function fetchEditorsConfig(): Promise<EditorConfig[]> {
  const envFilePath = `../../${ENV_FILE_PATH}`; // Needs to go back two dirs, since this file is at `workspaces/worker`.
  const env = (await (await fetch(envFilePath)).json()) as EnvJson;
  return env.KIE_SANDBOX_EDITORS;
}

async function fetchAppName(): Promise<string> {
  const envFilePath = `../../${ENV_FILE_PATH}`; // Needs to go back two dirs, since this file is at `workspaces/worker`.
  const env = (await (await fetch(envFilePath)).json()) as EnvJson;
  return env.KIE_SANDBOX_APP_NAME;
}

const workspaceServices = createWorkspaceServices({ gitCorsProxyUrl: gitCorsProxyUrl() });

declare let onconnect: any;

// eslint-disable-next-line prefer-const
onconnect = async (e: MessageEvent) => {
  const appName = await fetchAppName();
  const editorsConfig = await fetchEditorsConfig();
  const editorEnvelopeLocator = new EditorEnvelopeLocatorFactory().create({
    targetOrigin: "",
    editorsConfig,
  });

  console.log("Connected to Workspaces Shared Worker");
  setupWorkerConnection({
    fsFlushManager: workspaceServices.fsFlushManager,
    apiImpl: new WorkspacesWorkerApiImpl({
      appName,
      services: workspaceServices,
      fileFilter: {
        isModel: (path) => editorEnvelopeLocator.hasMappingFor(path),
        isEditable: (path) => editorEnvelopeLocator.hasMappingFor(path),
        isSupported: (path) => editorEnvelopeLocator.hasMappingFor(path),
      },
    }),
    port: e.ports[0],
  });
};
