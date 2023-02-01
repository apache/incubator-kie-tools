/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

export type CommitMessageValidationResource = {
  result: boolean;
  reason?: string;
};

export type CommitMessageValidationServiceClientArgs = {
  commitMessageValidationServiceUrl: string;
  commitMessage: string;
};

export enum ContentTypes {
  APPLICATION_JSON = "application/json",
  APPLICATION_YAML = "application/yaml",
}

export enum HttpMethod {
  GET = "GET",
  POST = "POST",
  PUT = "PUT",
  PATCH = "PATCH",
  DELETE = "DELETE",
}

export class CommitMessageValidationServiceClient {
  constructor(protected readonly args: CommitMessageValidationServiceClientArgs) {}

  public method() {
    return HttpMethod.POST;
  }

  public endpoint() {
    return this.args.commitMessageValidationServiceUrl;
  }

  public headers() {
    return {};
  }

  public body(): BodyInit {
    return this.args.commitMessage;
  }

  public contentType(): string {
    return ContentTypes.APPLICATION_JSON;
  }

  public name(): string {
    return this.constructor.name;
  }
}
