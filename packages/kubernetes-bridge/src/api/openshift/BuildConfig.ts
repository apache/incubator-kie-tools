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

import { CreateResourceFetchArgs, UniqueResourceFetchArgs, ResourceFetch } from "../../fetch/ResourceFetch";
import { ContentTypes, HttpMethod } from "../../fetch/FetchConstants";
import { OpenshiftApiVersions } from "./api";
import { IObjectReference, IResourceRequirements } from "kubernetes-models/v1";
import { ResourceDescriptor } from "../types";
import { CommonTemplateArgs } from "../../template/types";
import { commonLabels, runtimeLabels } from "../../template/TemplateConstants";
import { BUILD_IMAGE_TAG_VERSION } from "../api";

export interface DockerBuildStrategy {
  type: "Docker";
}

export interface BinaryBuildSource {
  type: "Binary";
  binary: {};
}

export interface BuildOutput {
  to: IObjectReference;
}
export type BuildSource = BinaryBuildSource;
export type BuildStrategy = DockerBuildStrategy;

export interface BuildConfigSpec {
  output: BuildOutput;
  resources: IResourceRequirements;
  source: BuildSource;
  strategy: BuildStrategy;
}

export interface BuildConfigDescriptor extends ResourceDescriptor {
  spec: BuildConfigSpec;
}

export const BUILD_CONFIG_TEMPLATE = (args: CommonTemplateArgs): BuildConfigDescriptor => ({
  apiVersion: OpenshiftApiVersions.BUILD_CONFIG,
  kind: "BuildConfig",
  metadata: {
    name: args.resourceName,
    namespace: args.namespace,
    labels: {
      ...commonLabels({ ...args }),
      ...runtimeLabels(),
    },
  },
  spec: {
    output: {
      to: {
        kind: "ImageStreamTag",
        name: `${args.resourceName}:${BUILD_IMAGE_TAG_VERSION}`,
      },
    },
    strategy: {
      type: "Docker",
    },
    source: {
      type: "Binary",
      binary: {},
    },
    resources: {
      limits: {
        memory: "4Gi",
      },
    },
  },
});

export class CreateBuildConfig extends ResourceFetch {
  constructor(protected args: CreateResourceFetchArgs & { descriptor?: BuildConfigDescriptor }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(this.args.descriptor ?? BUILD_CONFIG_TEMPLATE({ ...this.args }));
  }

  public endpoint(): string {
    return `/api/${OpenshiftApiVersions.BUILD_CONFIG}/namespaces/${this.args.namespace}/buildconfigs`;
  }
}

export class DeleteBuildConfig extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/api/${OpenshiftApiVersions.BUILD_CONFIG}/namespaces/${this.args.namespace}/buildconfigs/${this.args.resourceName}`;
  }
}

export class InstantiateBinary extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs & { zipBlob: Blob }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): Blob {
    return this.args.zipBlob;
  }

  public contentType(): string {
    return ContentTypes.APPLICATION_ZIP;
  }

  public endpoint(): string {
    return `/api/${OpenshiftApiVersions.BUILD_CONFIG}/namespaces/${this.args.namespace}/buildconfigs/${this.args.resourceName}/instantiatebinary?name=${this.args.resourceName}&namespace=${this.args.namespace}`;
  }
}
