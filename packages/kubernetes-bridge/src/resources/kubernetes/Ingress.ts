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

import { CreateResourceFetchArgs, ResourceFetch, UniqueResourceFetchArgs } from "../../fetch/ResourceFetch";
import { HttpMethod } from "../../fetch/FetchConstants";
import { Ingress, IIngress } from "kubernetes-models/networking.k8s.io/v1";
import {
  ResourceDataSource,
  ResourceGroupDescriptor,
  ResourceMetadataEnforcer,
  commonLabels,
  runtimeLabels,
} from "../common";

export type IngressDescriptor = IIngress & ResourceMetadataEnforcer;

export type IngressGroupDescriptor = ResourceGroupDescriptor<IngressDescriptor>;

export type CreateIngressArgs = CreateResourceFetchArgs &
  (
    | { resourceDataSource: ResourceDataSource.TEMPLATE }
    | { descriptor: IngressDescriptor; resourceDataSource: ResourceDataSource.PROVIDED }
  );

export const INGRESS_TEMPLATE = (args: CreateResourceFetchArgs): IngressDescriptor => {
  return new Ingress({
    metadata: {
      name: args.resourceName,
      namespace: args.namespace,
      labels: {
        ...commonLabels({ ...args }),
        ...runtimeLabels(),
      },
      annotations: {
        "nginx.ingress.kubernetes.io/ssl-redirect": "false",
        "nginx.ingress.kubernetes.io/backend-protocol": "HTTP",
      },
    },
    spec: {
      rules: [
        {
          http: {
            paths: [
              {
                path: `/${args.resourceName}`,
                pathType: "Prefix",
                backend: {
                  service: {
                    name: args.resourceName,
                    port: {
                      number: 8080,
                    },
                  },
                },
              },
            ],
          },
        },
      ],
    },
  }).toJSON();
};

export class CreateIngress extends ResourceFetch {
  constructor(protected args: CreateIngressArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : INGRESS_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/apis/${Ingress.apiVersion}/namespaces/${this.args.namespace}/ingresses`;
  }
}

export class ListIngresses extends ResourceFetch {
  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/apis/${Ingress.apiVersion}/namespaces/${this.args.namespace}/ingresses`;
  }
}

export class DeleteIngress extends ResourceFetch {
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
    return `/apis/${Ingress.apiVersion}/namespaces/${this.args.namespace}/ingresses/${this.args.resourceName}`;
  }
}
