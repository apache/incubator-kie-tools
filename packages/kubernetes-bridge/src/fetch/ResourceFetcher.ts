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

import { Resource } from "../resources/common";
import { KubernetesConnection } from "../service/KubernetesConnection";
import { ContentTypes, HeaderKeys } from "./FetchConstants";
import { ResourceFetch } from "./ResourceFetch";

export class ResourceFetcher {
  constructor(private readonly args: { connection: KubernetesConnection; proxyUrl?: string }) {}

  public async execute<T = Resource>(args: {
    target: ResourceFetch;
    rollbacks?: ResourceFetch[];
  }): Promise<Readonly<T>> {
    const targetUrl = `${this.args.connection.host}${args.target.endpoint()}`;
    const urlToFetch = this.args.proxyUrl ?? targetUrl;

    const headers: HeadersInit = {
      [HeaderKeys.AUTHORIZATION]: `Bearer ${this.args.connection.token}`,
      [HeaderKeys.ACCEPT]: ContentTypes.APPLICATION_JSON,
      [HeaderKeys.CONTENT_TYPE]: args.target.contentType(),
    };

    if (this.args.proxyUrl) {
      headers[HeaderKeys.TARGET_URL] = targetUrl;
    }

    let error;

    try {
      const response = await fetch(urlToFetch, {
        method: args.target.method(),
        body: args.target.body(),
        headers,
      });

      if (response.ok) {
        return (await response.json()) as T;
      } else {
        error = {
          status: response.status,
          statusText: response.statusText,
        };
      }
    } catch (e) {
      // No-op
    }

    if (args.rollbacks && args.rollbacks.length > 0) {
      for (const resource of args.rollbacks) {
        await this.execute({ target: resource });
      }
    }

    throw new Error(`Error fetching ${args.target.name()}`);
  }
}
