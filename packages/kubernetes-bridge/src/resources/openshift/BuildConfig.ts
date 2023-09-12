/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { CreateResourceFetchArgs, UniqueResourceFetchArgs, ResourceFetch } from "../../fetch/ResourceFetch";
import { ContentTypes, HttpMethod } from "../../fetch/FetchConstants";
import { OpenshiftApiVersions } from "./api";
import { IObjectReference, IResourceRequirements } from "kubernetes-models/v1";
import {
  ResourceDescriptor,
  BUILD_IMAGE_TAG_VERSION,
  commonLabels,
  runtimeLabels,
  ResourceDataSource,
} from "../common";

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

export type CreateBuildConfigTemplateArgs = {
  resourceDataSource: ResourceDataSource.TEMPLATE;
};

export type CreateBuildConfigArgs = CreateResourceFetchArgs &
  (
    | CreateBuildConfigTemplateArgs
    | { descriptor: BuildConfigDescriptor; resourceDataSource: ResourceDataSource.PROVIDED }
  );

export const BUILD_CONFIG_TEMPLATE = (
  args: CreateResourceFetchArgs & CreateBuildConfigTemplateArgs
): BuildConfigDescriptor => ({
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
  constructor(protected args: CreateBuildConfigArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : BUILD_CONFIG_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/apis/${OpenshiftApiVersions.BUILD_CONFIG}/namespaces/${this.args.namespace}/buildconfigs`;
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
    return `/apis/${OpenshiftApiVersions.BUILD_CONFIG}/namespaces/${this.args.namespace}/buildconfigs/${this.args.resourceName}`;
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
    return `/apis/${OpenshiftApiVersions.BUILD_CONFIG}/namespaces/${this.args.namespace}/buildconfigs/${this.args.resourceName}/instantiatebinary?name=${this.args.resourceName}&namespace=${this.args.namespace}`;
  }
}
