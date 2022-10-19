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

import { EnvelopeBusMessageManager } from "@kie-tools-core/envelope-bus/dist/common";
import { WorkspacesWorkerApi } from "./api/WorkspacesWorkerApi";
import { WorkspacesWorkerChannelApi } from "./api/WorkspacesWorkerChannelApi";

export class WorkspacesSharedWorker {
  private readonly IS_API_AVAILABLE = window["SharedWorker"] != null;
  private readonly SHARED_WORKER_URL = "workspace/worker/sharedWorker.js";
  private readonly SHARED_WORKER_NAME = "workspaces-shared-worker";

  private static instance: WorkspacesSharedWorker | undefined;

  private workspacesWorker: SharedWorker | undefined;
  private ready: Promise<void> | undefined;

  public workspacesWorkerBus: EnvelopeBusMessageManager<WorkspacesWorkerChannelApi, WorkspacesWorkerApi>;

  private constructor() {
    if (this.IS_API_AVAILABLE) {
      this.createWorker();
    } else {
      console.debug("SharedWorker API not available");
    }

    this.workspacesWorkerBus = new EnvelopeBusMessageManager<WorkspacesWorkerChannelApi, WorkspacesWorkerApi>((m) => {
      this.workspacesWorker?.port.postMessage(m);
    });
  }

  public static getInstance(): WorkspacesSharedWorker {
    if (!WorkspacesSharedWorker.instance) {
      WorkspacesSharedWorker.instance = new WorkspacesSharedWorker();
    }
    return WorkspacesSharedWorker.instance;
  }

  public async withBus<T>(
    callback: (
      workspacesWorkerBus: EnvelopeBusMessageManager<WorkspacesWorkerChannelApi, WorkspacesWorkerApi>
    ) => Promise<T>
  ): Promise<T> {
    await (this.ready ?? Promise.reject());
    return callback(this.workspacesWorkerBus);
  }

  private createWorker() {
    this.workspacesWorker = new SharedWorker(this.SHARED_WORKER_URL, this.SHARED_WORKER_NAME);
    this.workspacesWorker.port.start();

    this.ready = new Promise<void>((res) => {
      console.debug(`${this.SHARED_WORKER_NAME} is ready.`);

      this.workspacesWorker!.port.onmessage = (m) => {
        this.workspacesWorkerBus.server.receive(m.data, {
          kieToolsWorkspacesWorker_ready() {
            res();
          },
          async kieToolsWorkspacesWorker_ping() {
            return "pong";
          },
        });
      };
    });
  }
}
