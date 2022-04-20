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

const API_ENDPOINT = "apis/serving.knative.dev/v1";

export interface KNativeService extends Resource {
  status: {
    url: string;
  };
}

export interface KNativeServices {
  items: KNativeService[];
}

export interface CreateKNativeServiceArgs {
  uri: string;
  workspaceName: string;
}

export class CreateKNativeService extends ResourceFetch {
  public constructor(protected args: ResourceArgs & CreateKNativeServiceArgs) {
    super(args);
  }

  protected method(): HttpMethod {
    return "POST";
  }

  protected async requestBody(): Promise<string | undefined> {
    return `
    kind: Service
    apiVersion: serving.knative.dev/v1
    metadata:
      annotations:
        image.openshift.io/triggers: >-
          [{"from":{"kind":"ImageStreamTag","name":"${this.args.resourceName}:latest","namespace":"${this.args.namespace}"},"fieldPath":"spec.template.spec.containers[?(@.name==\\"${this.args.resourceName}\\")].image","pause":"false"}]
        ${KOGITO_URI}: ${this.args.uri}
        ${KOGITO_WORKSPACE_NAME}: ${this.args.workspaceName}
      name: ${this.args.resourceName}
      namespace: ${this.args.namespace}
      labels:
        app: ${this.args.resourceName}
        app.kubernetes.io/component: ${this.args.resourceName}
        app.kubernetes.io/instance: ${this.args.resourceName}
        app.kubernetes.io/part-of: ${this.args.resourceName}
        app.kubernetes.io/name: ${this.args.resourceName}
        app.openshift.io/runtime: quarkus
        app.openshift.io/runtime-version: ${JAVA_RUNTIME_VERSION}
        ${KOGITO_CREATED_BY}: ${this.args.createdBy}
    spec:
      template:
        spec:
          containers:
            - name: ${this.args.resourceName}
              image: >-
                image-registry.openshift-image-registry.svc:5000/${this.args.namespace}/${this.args.resourceName}:latest
  `;
  }

  public name(): string {
    return CreateKNativeService.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/services`;
  }
}

export class ListKNativeServices extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return ListKNativeServices.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/services?labelSelector=${KOGITO_CREATED_BY}`;
  }
}

export class DeleteKNativeService extends ResourceFetch {
  protected method(): HttpMethod {
    return "DELETE";
  }

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return DeleteKNativeService.name;
  }
  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/services/${this.args.resourceName}`;
  }
}

export class GetKNativeService extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return GetKNativeService.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/services/${this.args.resourceName}`;
  }
}
