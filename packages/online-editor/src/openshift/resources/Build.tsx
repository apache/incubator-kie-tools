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

import { dirname } from "path";
import { DeploymentFile } from "../../editor/DmnDevSandbox/DmnDevSandboxContext";
import { HttpMethod, JAVA_RUNTIME_VERSION, KOGITO_CREATED_BY, Resource, ResourceArgs, ResourceFetch } from "./Resource";

const API_ENDPOINT = "apis/build.openshift.io/v1";

export interface Build extends Resource {
  status: {
    phase: "New" | "Pending" | "Running" | "Complete" | "Failed" | "Error" | "Cancelled";
  };
}

export interface Builds {
  items: Build[];
}

export interface CreateBuildArgs {
  buildConfigUid: string;
  targetFile: DeploymentFile;
  relatedFiles: DeploymentFile[];
  urls: {
    index: string;
    swaggerUI: string;
    onlineEditor: string;
  };
}

export class ListBuilds extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return ListBuilds.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/builds`;
  }
}

export class DeleteBuild extends ResourceFetch {
  protected method(): HttpMethod {
    return "DELETE";
  }

  protected async requestBody(): Promise<string | undefined> {
    return;
  }

  public name(): string {
    return DeleteBuild.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/builds/${this.args.resourceName}`;
  }
}
