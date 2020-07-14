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

import { KogitoEnvelopeBus } from "../../KogitoEnvelopeBus";
import { ResourceContentOptions, ResourceListOptions } from "@kogito-tooling/microeditor-envelope-protocol";
import { ResourceContentApi } from "./ResourceContentApi";

export class ResourceContentServiceCoordinator {
  public exposeApi(kogitoEnvelopeBus: KogitoEnvelopeBus): ResourceContentApi {
    return {
      get(path: string, opts?: ResourceContentOptions) {
        return kogitoEnvelopeBus.request_resourceContent(path, opts).then(r => r?.content);
      },
      list(pattern: string, opts?: ResourceListOptions) {
        return kogitoEnvelopeBus.request_resourceList(pattern, opts).then(r => r.paths.sort());
      }
    };
  }
}
