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

import { HttpMethod, ResourceFetch } from "./Resource";

const API_ENDPOINT = "apis/project.openshift.io/v1";

export class GetProject extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  protected requestBody(): string | undefined {
    return;
  }

  public name(): string {
    return GetProject.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/projects/${this.args.namespace}`;
  }
}
