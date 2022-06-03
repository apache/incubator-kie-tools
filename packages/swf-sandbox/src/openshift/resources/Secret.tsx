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

import { HttpMethod, RESOURCE_CREATED_BY, ResourceArgs, ResourceFetch } from "./Resource";

const API_ENDPOINT = "api/v1";

interface CreateSecretArgs {
  data: { [key: string]: string };
}

export class CreateSecret extends ResourceFetch {
  public constructor(protected args: ResourceArgs & CreateSecretArgs) {
    super(args);
  }

  protected method(): HttpMethod {
    return "POST";
  }

  protected async requestBody(): Promise<string | undefined> {
    const encodedData = Object.entries(this.args.data).reduce(
      (acc, [key, value]) => ({
        ...acc,
        [key]: btoa(value),
      }),
      {} as { [key: string]: string }
    );

    return `
    kind: Secret
    apiVersion: v1
    metadata:
      name: ${this.args.resourceName}
      namespace: ${this.args.namespace}
      labels:
        app: ${this.args.resourceName}
        app.kubernetes.io/component: ${this.args.resourceName}
        app.kubernetes.io/instance: ${this.args.resourceName}
        app.kubernetes.io/part-of: ${this.args.resourceName}
        app.kubernetes.io/name:  ${this.args.resourceName}
        ${RESOURCE_CREATED_BY}: ${this.args.createdBy}
    data: ${JSON.stringify(encodedData)}
  `;
  }

  public name(): string {
    return CreateSecret.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/secrets`;
  }
}

export class DeleteSecret extends ResourceFetch {
  protected method(): HttpMethod {
    return "DELETE";
  }

  public name(): string {
    return DeleteSecret.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/secrets/${this.args.resourceName}`;
  }
}
