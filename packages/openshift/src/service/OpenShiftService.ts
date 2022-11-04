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
import { BuildDescriptor, BuildPhase, DeploymentCondition, DeploymentDescriptor, Resource } from "../api/types";
import { ResourceFetcher } from "../fetch/ResourceFetcher";
import { isOpenShiftConnectionValid, OpenShiftConnection } from "./OpenShiftConnection";
import { OpenShiftDeploymentState } from "./types";

export interface OpenShiftServiceArgs {
  connection: OpenShiftConnection;
  proxyUrl: string;
}

export class OpenShiftService {
  private readonly fetcher: ResourceFetcher;

  constructor(private readonly args: OpenShiftServiceArgs) {
    this.fetcher = new ResourceFetcher({ proxyUrl: args.proxyUrl, connection: this.args.connection });
  }

  public async withFetch<T = Resource>(callback: (fetcher: ResourceFetcher) => Promise<T>): Promise<T> {
    if (!isOpenShiftConnectionValid(this.args.connection)) {
      throw new Error("The OpenShift connection is not valid");
    }

    return callback(this.fetcher);
  }

  public async isConnectionEstablished(connection: OpenShiftConnection): Promise<boolean> {
    try {
      await this.withFetch((fetcher: ResourceFetcher) =>
        fetcher.fetchIt({ target: new GetProject({ namespace: connection.namespace }) })
      );

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

  public extractDeploymentState(args: {
    deployment?: DeploymentDescriptor;
    build?: BuildDescriptor;
  }): OpenShiftDeploymentState {
    if (args.build) {
      if (!args.build.status || (["Failed", "Error", "Cancelled"] as BuildPhase[]).includes(args.build.status.phase)) {
        return OpenShiftDeploymentState.DOWN;
      }

      if ((["New", "Pending"] as BuildPhase[]).includes(args.build.status.phase)) {
        return OpenShiftDeploymentState.PREPARING;
      }

      if (args.build.status.phase === "Running") {
        return OpenShiftDeploymentState.IN_PROGRESS;
      }

      // At this point, BuildPhase is `Complete`.
    }

    if (!args.deployment || !args.deployment.status || !args.deployment.status.replicas) {
      return OpenShiftDeploymentState.DOWN;
    }

    const progressingCondition = args.deployment.status.conditions?.find(
      (condition: DeploymentCondition) => condition.type === "Progressing"
    );

    if (!progressingCondition || progressingCondition.status !== "True") {
      return OpenShiftDeploymentState.DOWN;
    }

    if (!args.deployment.status.readyReplicas) {
      return OpenShiftDeploymentState.IN_PROGRESS;
    }

    return OpenShiftDeploymentState.UP;
  }
}
