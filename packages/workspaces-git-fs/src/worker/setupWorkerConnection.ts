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

import { EnvelopeBusMessageManager } from "@kie-tools-core/envelope-bus/dist/common";
import { FsFlushManager } from "../services/FsFlushManager";
import { WorkspacesWorkerApi } from "./api/WorkspacesWorkerApi";
import { WorkspacesWorkerChannelApi } from "./api/WorkspacesWorkerChannelApi";
import { WorkspacesWorkerApiImpl } from "./WorkspacesWorkerApiImpl";

export function setupWorkerConnection(args: {
  apiImpl: WorkspacesWorkerApiImpl;
  port: MessagePort;
  fsFlushManager: FsFlushManager;
}) {
  const bus = new EnvelopeBusMessageManager<WorkspacesWorkerApi, WorkspacesWorkerChannelApi>((m) =>
    args.port.postMessage(m)
  );
  args.port.addEventListener("message", (message) => bus.server.receive(message.data, args.apiImpl));
  args.port.start(); // Required when using addEventListener. Otherwise, called implicitly by onmessage setter.
  bus.clientApi.notifications.kieToolsWorkspacesWorker_ready.send();

  args.fsFlushManager.subscribable.subscribe((flushes) => {
    bus.shared.kieSandboxWorkspacesStorage_flushes.set(flushes);
  });
}
