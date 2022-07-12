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

export interface ResourceArgs {
  host: string;
  namespace: string;
  token: string;
  resourceName?: string;
  createdBy?: string;
}

export interface Resource {
  metadata: {
    uid: string;
    name: string;
    labels: Record<string, string>;
    annotations: Record<string, string>;
    creationTimestamp: string;
    workspaceId: string;
    namespace: string;
  };
}

export type HttpMethod = "GET" | "POST" | "PUT" | "DELETE" | "PATCH";

export const APP_LABEL = "app";
export const KNATIVE_SERVING_SERVICE = "serving.knative.dev/service";
export const RESOURCE_GROUP_ID = "kogito.kie.org";
export const RESOURCE_URI = `${RESOURCE_GROUP_ID}/uri`;
export const RESOURCE_CREATED_BY = `${RESOURCE_GROUP_ID}/created-by`;
export const RESOURCE_WORKSPACE_NAME = `${RESOURCE_GROUP_ID}/workspace-name`;
export const JAVA_RUNTIME_VERSION = "openjdk-11-el7";
export const BUILD_IMAGE_TAG = "1.0";

export abstract class ResourceFetch {
  public constructor(protected readonly args: ResourceArgs) {}

  protected abstract method(): HttpMethod;

  protected async requestBody(): Promise<string | Blob | undefined> {
    return;
  }

  protected contentType(): string {
    return "application/yaml";
  }

  public abstract url(): string;

  public abstract name(): string;

  public async requestInit(): Promise<RequestInit> {
    return {
      method: this.method(),
      headers: {
        Authorization: `Bearer ${this.args.token}`,
        Accept: "application/json",
        "Content-Type": this.contentType(),
        "Target-Url": this.url(),
      },
      body: await this.requestBody(),
    };
  }
}
