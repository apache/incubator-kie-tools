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

import { HttpMethod, KOGITO_CREATED_BY, ResourceArgs, ResourceFetch } from "./Resource";

const API_ENDPOINT = "api/v1";

// TODO: allow a map of key/value pairs, not only id and secret
interface CreateSecretArgs {
  id: {
    key: string;
    value: string;
  };
  secret: {
    key: string;
    value: string;
  };
}

export class CreateSecret extends ResourceFetch {
  public constructor(protected args: ResourceArgs & CreateSecretArgs) {
    super(args);
  }

  protected method(): HttpMethod {
    return "POST";
  }

  protected async requestBody(): Promise<string | undefined> {
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
        ${KOGITO_CREATED_BY}: ${this.args.createdBy}
    data:
      ${this.args.id.key}: ${btoa(this.args.id.value)}
      ${this.args.secret.key}: ${btoa(this.args.secret.value)}
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

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return DeleteSecret.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/secrets/${this.args.resourceName}`;
  }
}
