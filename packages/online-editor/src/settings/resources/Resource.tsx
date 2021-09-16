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
}

export interface Resource {
  metadata: {
    uid: string;
    name: string;
    labels: Record<string, string>;
    annotations: Record<string, string>;
    creationTimestamp: string;
  };
}

export type HttpMethod = "GET" | "POST" | "PUT" | "DELETE" | "PATCH";

export const KOGITO_CREATED_BY = "kogito.kie.org/created-by";
export const KOGITO_FILENAME = "kogito.kie.org/filename";
export const JAVA_RUNTIME_VERSION = "openjdk-11-el7";

export abstract class ResourceFetch {
  public constructor(protected readonly args: ResourceArgs) {}

  protected abstract method(): HttpMethod;

  protected abstract requestBody(): string | undefined;

  public abstract name(): string;

  public abstract url(): string;

  public requestInit(): RequestInit {
    return {
      method: this.method(),
      headers: {
        Authorization: `Bearer ${this.args.token}`,
        Accept: "application/json",
        "Content-Type": "application/yaml",
        "Target-Url": this.url(),
      },
      body: this.requestBody(),
    };
  }
}
