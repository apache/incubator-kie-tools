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

import { GetKNativeService } from "../api/knative/KNativeService";
import { KNativeServiceDescriptor } from "../api/types";
import { ResourceFetcher } from "../fetch/ResourceFetcher";

export class KNativeService {
  constructor(private readonly args: { fetcher: ResourceFetcher; namespace: string }) {}

  public async getDeploymentRoute(resourceName: string): Promise<string | undefined> {
    try {
      const knService = await this.args.fetcher.execute<KNativeServiceDescriptor>({
        target: new GetKNativeService({ namespace: this.args.namespace, resourceName }),
      });

      return knService.status?.url;
    } catch (e) {
      throw new Error(`Failed to get deployment route for resource ${resourceName}`);
    }
  }
}
