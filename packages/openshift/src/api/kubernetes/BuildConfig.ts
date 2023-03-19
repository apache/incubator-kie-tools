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

import { baseEndpoint, KubernetesApiVersions } from "../ApiConstants";
import { BUILD_CONFIG_TEMPLATE } from "../../template/ResourceTemplates";
import { CreateResourceFetchArgs, UniqueResourceFetchArgs, ResourceFetch } from "../../fetch/ResourceFetch";
import { BuildConfigDescriptor } from "../types";
import { ContentTypes, HttpMethod } from "../../fetch/FetchConstants";

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
    return `/${baseEndpoint(KubernetesApiVersions.BUILD_CONFIG)}/namespaces/${this.args.namespace}/buildconfigs`;
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
    return `/${baseEndpoint(KubernetesApiVersions.BUILD_CONFIG)}/namespaces/${this.args.namespace}/buildconfigs/${
      this.args.resourceName
    }`;
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
    return `/${baseEndpoint(KubernetesApiVersions.BUILD_CONFIG)}/namespaces/${this.args.namespace}/buildconfigs/${
      this.args.resourceName
    }/instantiatebinary?name=${this.args.resourceName}&namespace=${this.args.namespace}`;
  }
}
