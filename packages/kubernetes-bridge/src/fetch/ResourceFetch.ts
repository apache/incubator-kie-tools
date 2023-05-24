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

import { ContentTypes, HttpMethod } from "./FetchConstants";

export interface ResourceFetchArgs {
  namespace: string;
}

export type UniqueResourceFetchArgs = ResourceFetchArgs & { resourceName: string };
export type CreateResourceFetchArgs = ResourceFetchArgs & { resourceName: string; createdBy: string };

export abstract class ResourceFetch {
  constructor(protected readonly args: ResourceFetchArgs) {}

  public abstract method(): HttpMethod;

  public abstract endpoint(): string;

  public body(): BodyInit | undefined {
    return;
  }

  public contentType(): string {
    return ContentTypes.APPLICATION_JSON;
  }

  public name(): string {
    return this.constructor.name;
  }
}
