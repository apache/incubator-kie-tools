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

import { GetProject } from "../resources/openshift/Project";
import { ResourceFetcher } from "../fetch/ResourceFetcher";
import { KnativeSupportService } from "./support";
import { KubernetesService, KubernetesServiceArgs } from "./KubernetesService";
import { DeploymentCondition, DeploymentDescriptor } from "../resources/kubernetes/Deployment";
import { BuildDescriptor, BuildPhase } from "../resources/openshift/Build";
import { RouteDescriptor } from "../resources/openshift/Route";
import { KubernetesConnection, KubernetesConnectionStatus } from "./KubernetesConnection";
import { DeploymentState, Resource } from "../resources/common";

export class OpenShiftService {
  private knativeSupportService: KnativeSupportService;
  private kubernetesService: KubernetesService;

  constructor(readonly args: KubernetesServiceArgs) {
    this.kubernetesService = new KubernetesService(args);
    this.knativeSupportService = new KnativeSupportService({
      fetcher: this.kubernetesService.fetcher,
      namespace: args.connection.namespace,
    });
  }

  public get knative(): KnativeSupportService {
    return this.knativeSupportService;
  }

  public get kubernetes(): KubernetesService {
    return this.kubernetesService;
  }

  public composeDeploymentUrlFromRoute(route: RouteDescriptor): string {
    return `https://${route.spec.host}`;
  }

  public extractDeploymentState(args: { deployment?: DeploymentDescriptor; build?: BuildDescriptor }): DeploymentState {
    if (args.build) {
      if (!args.build.status || (["Failed", "Error", "Cancelled"] as BuildPhase[]).includes(args.build.status.phase)) {
        return DeploymentState.DOWN;
      }

      if ((["New", "Pending"] as BuildPhase[]).includes(args.build.status.phase)) {
        return DeploymentState.PREPARING;
      }

      if (args.build.status.phase === "Running") {
        return DeploymentState.IN_PROGRESS;
      }

      // At this point, BuildPhase is `Complete`.
    }

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
    return this.kubernetes.withFetch<T>(callback);
  }

  public async isConnectionEstablished(connection: KubernetesConnection): Promise<KubernetesConnectionStatus> {
    try {
      const testConnectionFetcher = new ResourceFetcher({ connection, proxyUrl: this.args.proxyUrl });
      await testConnectionFetcher.execute({ target: new GetProject({ namespace: connection.namespace }) });

      return KubernetesConnectionStatus.CONNECTED;
    } catch (error) {
      return KubernetesConnectionStatus.ERROR;
    }
  }

  public newResourceName(prefix: string): string {
    return this.kubernetes.newResourceName(prefix);
  }
}
