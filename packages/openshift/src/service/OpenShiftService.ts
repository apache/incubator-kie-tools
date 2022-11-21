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

import { GetProject } from "../api/kubernetes/Project";
import { Resource } from "../api/types";
import { ResourceFetcher } from "../fetch/ResourceFetcher";
import { KNativeService } from "./KNativeService";
import { KubernetesService } from "./KubernetesService";
import { isOpenShiftConnectionValid, OpenShiftConnection } from "./OpenShiftConnection";

export interface OpenShiftServiceArgs {
  connection: OpenShiftConnection;
  proxyUrl: string;
}

export class OpenShiftService {
  private readonly kubernetesService: KubernetesService;
  private readonly knativeService: KNativeService;
  private readonly fetcher: ResourceFetcher;

  constructor(private readonly args: OpenShiftServiceArgs) {
    this.fetcher = new ResourceFetcher({ proxyUrl: args.proxyUrl, connection: this.args.connection });
    this.kubernetesService = new KubernetesService({ fetcher: this.fetcher, namespace: args.connection.namespace });
    this.knativeService = new KNativeService({ fetcher: this.fetcher, namespace: args.connection.namespace });
  }

  public get kubernetes(): KubernetesService {
    return this.kubernetesService;
  }

  public get knative(): KNativeService {
    return this.knativeService;
  }

  public async withFetch<T = Resource>(callback: (fetcher: ResourceFetcher) => Promise<T>): Promise<T> {
    if (!isOpenShiftConnectionValid(this.args.connection)) {
      throw new Error("The OpenShift connection is not valid");
    }

    return callback(this.fetcher);
  }

  public async isConnectionEstablished(connection: OpenShiftConnection): Promise<boolean> {
    try {
      const testConnectionFetcher = new ResourceFetcher({ connection, proxyUrl: this.args.proxyUrl });
      await testConnectionFetcher.execute({ target: new GetProject({ namespace: connection.namespace }) });

      return true;
    } catch (error) {
      return false;
    }
  }

  public newResourceName(prefix: string): string {
    const randomPart = Math.random().toString(36).substring(2, 9);
    const milliseconds = new Date().getMilliseconds();
    const suffix = `${randomPart}${milliseconds}`;
    return `${prefix}-${suffix}`;
  }
}
