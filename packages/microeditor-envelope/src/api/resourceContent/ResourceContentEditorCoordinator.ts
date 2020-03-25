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

import { EnvelopeBusInnerMessageHandler } from "../../EnvelopeBusInnerMessageHandler";
import { ResourcesList, ResourceContent, ResourceContentOptions } from "@kogito-tooling/core-api";
import { ResourceContentApi } from "./ResourceContentApi";

export class ResourceContentEditorCoordinator {
  private pendingResourceRequests = new Map<string, (c: string) => void>();
  private pendingResourceListRequests = new Map<string, (c: string[]) => void>();

  public resolvePendingList(resourcesList: ResourcesList) {
    const resourceListCallback = this.pendingResourceListRequests.get(resourcesList.pattern);
    if (resourceListCallback) {
      resourceListCallback(resourcesList.paths);
      this.pendingResourceRequests.delete(resourcesList.pattern);
    } else {
      console.error(`[ResourceContentEditorCoordinator]: Callback for pattern "${resourcesList.pattern}" not found.`);
    }
  }

  public resolvePending(resourceContent: ResourceContent) {
    const resourceContentCallback = this.pendingResourceRequests.get(resourceContent.path);
    if (resourceContentCallback) {
      resourceContentCallback(resourceContent.content!);
      this.pendingResourceRequests.delete(resourceContent.path);
    } else {
      console.error(`[ResourceContentEditorCoordinator]: Callback for resource "${resourceContent.path}" not found.`);
    }
  }

  public exposeApi(messageBus: EnvelopeBusInnerMessageHandler): ResourceContentApi {
    const pendingResourceRequests = this.pendingResourceRequests;
    const pendingResourceListRequests = this.pendingResourceListRequests;
    return {
      get(path: string, opts?: ResourceContentOptions) {
        messageBus.request_resourceContent(path, opts);

        return new Promise(resolve => {
          const previousCallback = pendingResourceRequests.get(path);
          pendingResourceRequests.set(path, (value: string) => {
            if (previousCallback) {
              previousCallback(value);
            }
            resolve(value);
          });
        });
      },
      list(pattern: string) {
        messageBus.request_resourceList(pattern);
        return new Promise(resolve => {
          const previousCallback = pendingResourceListRequests.get(pattern);
          pendingResourceListRequests.set(pattern, (value: string[]) => {
            value.sort();
            if (previousCallback) {
              previousCallback(value);
            }
            resolve(value);
          });
        });
      }
    };
  }
}
