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
import { ROUTE_TEMPLATE } from "../../template/ResourceTemplates";
import { CreateResourceFetchArgs, ResourceFetch, UniqueResourceFetchArgs } from "../../fetch/ResourceFetch";
import { RouteDescriptor } from "../types";
import { HttpMethod } from "../../fetch/FetchConstants";

export class CreateRoute extends ResourceFetch {
  constructor(protected args: CreateResourceFetchArgs & { descriptor?: RouteDescriptor }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(this.args.descriptor ?? ROUTE_TEMPLATE({ ...this.args }));
  }

  public endpoint(): string {
    return `/${baseEndpoint(KubernetesApiVersions.ROUTE)}/namespaces/${this.args.namespace}/routes`;
  }
}

export class ListRoutes extends ResourceFetch {
  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/${baseEndpoint(KubernetesApiVersions.ROUTE)}/namespaces/${this.args.namespace}/routes`;
  }
}

export class DeleteRoute extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/${baseEndpoint(KubernetesApiVersions.ROUTE)}/namespaces/${this.args.namespace}/routes/${
      this.args.resourceName
    }`;
  }
}
