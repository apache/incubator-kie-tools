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

import { CreateResourceFetchArgs, ResourceFetch, UniqueResourceFetchArgs } from "../../fetch/ResourceFetch";
import { HttpMethod } from "../../fetch/FetchConstants";
import { OpenshiftApiVersions } from "./api";
import { IObjectReference } from "kubernetes-models/v1";
import {
  ResourceDataSource,
  ResourceDescriptor,
  ResourceGroupDescriptor,
  commonLabels,
  runtimeLabels,
} from "../common";

export interface TLSConfig {
  insecureEdgeTerminationPolicy?: "None" | "Allow" | "Redirect";
  termination: "edge" | "passthrough" | "reencrypt";
}

export interface RouteSpec {
  host?: string;
  port: {
    targetPort: number | string;
  };
  tls: TLSConfig;
  to: IObjectReference;
}

export interface RouteDescriptor extends ResourceDescriptor {
  spec: RouteSpec;
}

export type RouteGroupDescriptor = ResourceGroupDescriptor<RouteDescriptor>;

export type CreateRouteTemplateArgs = {
  resourceDataSource: ResourceDataSource.TEMPLATE;
};

export type CreateRouteArgs = CreateResourceFetchArgs &
  (CreateRouteTemplateArgs | { descriptor: RouteDescriptor; resourceDataSource: ResourceDataSource.PROVIDED });

export const ROUTE_TEMPLATE = (args: CreateResourceFetchArgs & CreateRouteTemplateArgs): RouteDescriptor => ({
  apiVersion: OpenshiftApiVersions.ROUTE,
  kind: "Route",
  metadata: {
    name: args.resourceName,
    namespace: args.namespace,
    labels: {
      ...commonLabels({ ...args }),
      ...runtimeLabels(),
    },
  },
  spec: {
    to: {
      name: args.resourceName,
      kind: "Service",
    },
    port: {
      targetPort: "8080-tcp",
    },
    tls: {
      termination: "edge",
      insecureEdgeTerminationPolicy: "None",
    },
  },
});

export class CreateRoute extends ResourceFetch {
  constructor(protected args: CreateRouteArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : ROUTE_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/apis/${OpenshiftApiVersions.ROUTE}/namespaces/${this.args.namespace}/routes`;
  }
}

export class ListRoutes extends ResourceFetch {
  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/apis/${OpenshiftApiVersions.ROUTE}/namespaces/${this.args.namespace}/routes`;
  }
}

export class DeleteRoute extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public body(): string {
    return JSON.stringify({
      propagationPolicy: "Background",
    });
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/apis/${OpenshiftApiVersions.ROUTE}/namespaces/${this.args.namespace}/routes/${this.args.resourceName}`;
  }
}

export class GetRoute extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/apis/${OpenshiftApiVersions.ROUTE}/namespaces/${this.args.namespace}/routes/${this.args.resourceName}`;
  }
}
