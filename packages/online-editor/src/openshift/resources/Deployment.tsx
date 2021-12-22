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

import {
  HttpMethod,
  JAVA_RUNTIME_VERSION,
  KOGITO_CREATED_BY,
  KOGITO_URI,
  KOGITO_WORKSPACE_NAME,
  Resource,
  ResourceArgs,
  ResourceFetch,
} from "./Resource";

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

export interface CreateDeploymentArgs {
  uri: string;
  baseUrl: string;
  workspaceName: string;
}

export class CreateDeployment extends ResourceFetch {
  public constructor(protected args: ResourceArgs & CreateDeploymentArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return "POST";
  }

  public async requestBody(): Promise<string | undefined> {
    return `
      kind: Deployment
      apiVersion: apps/v1
      metadata:
        annotations:
          ${KOGITO_URI}: ${this.args.uri}
          ${KOGITO_WORKSPACE_NAME}: ${this.args.workspaceName}
        name: ${this.args.resourceName}
        namespace: ${this.args.namespace}
        labels:
          app: ${this.args.resourceName}
          app.kubernetes.io/component: ${this.args.resourceName}
          app.kubernetes.io/instance: ${this.args.resourceName}
          app.kubernetes.io/part-of: ${this.args.resourceName}
          app.kubernetes.io/name: java
          app.openshift.io/runtime: quarkus
          app.openshift.io/runtime-version: ${JAVA_RUNTIME_VERSION}
          ${KOGITO_CREATED_BY}: ${this.args.createdBy}
      spec:
        replicas: 1
        selector:
          matchLabels:
            app: ${this.args.resourceName}
        template:
          metadata:
            labels:
              app: ${this.args.resourceName}
              deploymentconfig: ${this.args.resourceName}
          spec:
            containers:
              - name: ${this.args.resourceName}
                image: ${process.env.WEBPACK_REPLACE__dmnDevSandbox_baseImageFullUrl}
                ports:
                  - containerPort: 8080
                    protocol: TCP
                env:
                - name: BASE_URL
                  value: ${this.args.baseUrl}
                - name: QUARKUS_PLATFORM_VERSION
                  value: ${process.env.WEBPACK_REPLACE__quarkusPlatformVersion}
                - name: KOGITO_RUNTIME_VERSION
                  value: ${process.env.WEBPACK_REPLACE__kogitoRuntimeVersion}
    `;
  }

  public name(): string {
    return CreateDeployment.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/deployments`;
  }
}

export class ListDeployments extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return ListDeployments.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/deployments?labelSelector=${KOGITO_CREATED_BY}`;
  }
}

export class DeleteDeployment extends ResourceFetch {
  protected method(): HttpMethod {
    return "DELETE";
  }

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return DeleteDeployment.name;
  }
  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/deployments/${this.args.resourceName}`;
  }
}

export class GetDeployment extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return GetDeployment.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/deployments/${this.args.resourceName}`;
  }
}
