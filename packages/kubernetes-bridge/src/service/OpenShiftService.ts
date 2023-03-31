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

import { GetProject } from "../api/openshift/Project";
import { ResourceFetcher } from "../fetch/ResourceFetcher";
import { KnativeService } from "./support/KnativeService";
import { KubernetesService, KubernetesServiceArgs } from "./KubernetesService";
import { DeploymentCondition, DeploymentDescriptor } from "../api/kubernetes/Deployment";
import { BuildDescriptor, BuildPhase } from "../api/openshift/Build";
import { DeploymentState } from "./types";
import { RouteDescriptor } from "../api/openshift/Route";
import { KubernetesConnection } from "./KubernetesConnection";

export class OpenShiftService extends KubernetesService {
  private readonly knativeService: KnativeService;

  constructor(readonly args: KubernetesServiceArgs) {
    super(args);
    this.knativeService = new KnativeService({ fetcher: this.fetcher, namespace: args.connection.namespace });
  }

  public get knative(): KnativeService {
    return this.knativeService;
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

  public async isConnectionEstablished(connection: KubernetesConnection): Promise<boolean> {
    try {
      const testConnectionFetcher = new ResourceFetcher({ connection, proxyUrl: this.args.proxyUrl });
      await testConnectionFetcher.execute({ target: new GetProject({ namespace: connection.namespace }) });

      return true;
    } catch (error) {
      return false;
    }
  }
}
