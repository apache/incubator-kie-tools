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
import { Ingress, IIngress } from "kubernetes-models/networking.k8s.io/v1";
import { CommonTemplateArgs, ResourceGroupDescriptor, commonLabels, runtimeLabels } from "../common";

export type IngressDescriptor = IIngress;

export type IngressGroupDescriptor = ResourceGroupDescriptor<IngressDescriptor>;

export const INGRESS_TEMPLATE = (args: CommonTemplateArgs): Ingress => {
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
  });
};

export class CreateIngress extends ResourceFetch {
  constructor(protected args: CreateResourceFetchArgs & { descriptor?: IngressDescriptor }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(this.args.descriptor ?? INGRESS_TEMPLATE({ ...this.args }).toJSON());
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
