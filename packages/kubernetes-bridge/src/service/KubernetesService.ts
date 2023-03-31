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

import { ResourceFetcher } from "../fetch/ResourceFetcher";
import { IngressDescriptor } from "../resources/kubernetes/Ingress";
import { KubernetesConnection, isKubernetesConnectionValid } from "./KubernetesConnection";
import { DeploymentCondition, DeploymentDescriptor, ListDeployments } from "../resources/kubernetes/Deployment";
import { DeploymentState, Resource } from "../resources/common";

export interface KubernetesServiceArgs {
  connection: KubernetesConnection;
  proxyUrl?: string;
}

export class KubernetesService {
  readonly fetcher: ResourceFetcher;

  constructor(readonly args: KubernetesServiceArgs) {
    this.args = args;
    this.fetcher = new ResourceFetcher({ proxyUrl: args.proxyUrl, connection: this.args.connection });
  }

  public composeDeploymentUrlFromIngress(ingress: IngressDescriptor): string {
    return `http://localhost/${ingress.metadata?.name}`;
  }

  public extractDeploymentState(args: { deployment?: DeploymentDescriptor }): DeploymentState {
    if (!args.deployment || !args.deployment.status) {
      // Deployment still being created
      return DeploymentState.IN_PROGRESS;
    }

    if (!args.deployment.status.replicas) {
      // Deployment with no replicas is down
      return DeploymentState.DOWN;
    }

    const progressingCondition = args.deployment.status.conditions?.find(
      (condition: DeploymentCondition) => condition.type === "Progressing"
    );

    if (!progressingCondition || progressingCondition.status !== "True") {
      // Without `Progressing` condition, the deployment will never be up
      return DeploymentState.DOWN;
    }

    if (!args.deployment.status.readyReplicas) {
      // Deployment is progressing but no replicas are ready yet
      return DeploymentState.IN_PROGRESS;
    }

    return DeploymentState.UP;
  }

  public async withFetch<T = Resource>(callback: (fetcher: ResourceFetcher) => Promise<T>): Promise<T> {
    if (!isKubernetesConnectionValid(this.args.connection)) {
      throw new Error("The OpenShift connection is not valid");
    }

    return callback(this.fetcher);
  }

  public async isConnectionEstablished(connection: KubernetesConnection): Promise<boolean> {
    try {
      const testConnectionFetcher = new ResourceFetcher({ connection, proxyUrl: this.args.proxyUrl });
      await testConnectionFetcher.execute({ target: new ListDeployments({ namespace: connection.namespace }) });

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
