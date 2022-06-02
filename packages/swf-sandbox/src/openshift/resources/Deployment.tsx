/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { HttpMethod, Resource, ResourceFetch } from "./Resource";

const API_ENDPOINT = "apis/apps/v1";

export interface DeploymentCondition {
  type: string;
  status: "True" | "False" | "Unknown";
}

export interface Deployment extends Resource {
  status: {
    replicas?: number;
    readyReplicas?: number;
    conditions?: DeploymentCondition[];
  };
}

export interface Deployments {
  items: Deployment[];
}

export class ListDeployments extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  public name(): string {
    return ListDeployments.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/deployments`;
  }
}
