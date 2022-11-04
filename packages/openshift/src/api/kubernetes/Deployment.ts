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

import { HttpMethod } from "../../fetch/FetchConstants";
import {
  CreateResourceFetchArgs,
  ResourceFetch,
  ResourceFetchArgs,
  UniqueResourceFetchArgs,
} from "../../fetch/ResourceFetch";
import { DEPLOYMENT_TEMPLATE } from "../../template/ResourceTemplates";
import { CreateDeploymentArgs } from "../../template/types";
import { baseEndpoint, KubernetesApiVersions } from "../ApiConstants";
import { DeploymentDescriptor } from "../types";

export class CreateDeployment extends ResourceFetch {
  constructor(protected args: CreateResourceFetchArgs & CreateDeploymentArgs & { descriptor?: DeploymentDescriptor }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(this.args.descriptor ?? DEPLOYMENT_TEMPLATE({ ...this.args }));
  }

  public endpoint(): string {
    return `/${baseEndpoint(KubernetesApiVersions.DEPLOYMENT)}/namespaces/${this.args.namespace}/deployments`;
  }
}

export class ListDeployments extends ResourceFetch {
  constructor(protected args: ResourceFetchArgs & { labelSelector?: string }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    const selector = this.args.labelSelector ? `?labelSelector=${this.args.labelSelector}` : "";
    return `/${baseEndpoint(KubernetesApiVersions.DEPLOYMENT)}/namespaces/${
      this.args.namespace
    }/deployments${selector}`;
  }
}

export class DeleteDeployment extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/${baseEndpoint(KubernetesApiVersions.DEPLOYMENT)}/namespaces/${this.args.namespace}/deployments/${
      this.args.resourceName
    }`;
  }
}

export class GetDeployment extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/${baseEndpoint(KubernetesApiVersions.DEPLOYMENT)}/namespaces/${this.args.namespace}/deployments/${
      this.args.resourceName
    }`;
  }
}
