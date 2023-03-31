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

import { CreateResourceFetchArgs, ResourceFetch, UniqueResourceFetchArgs } from "../../fetch/ResourceFetch";
import { HttpMethod } from "../../fetch/FetchConstants";
import { CommonTemplateArgs } from "../../template/types";
import { Service, IService } from "kubernetes-models/v1";
import { commonLabels, runtimeLabels } from "../../template/TemplateConstants";

export type ServiceDescriptor = IService;

export const SERVICE_TEMPLATE = (args: CommonTemplateArgs): Service => {
  return new Service({
    metadata: {
      name: args.resourceName,
      namespace: args.namespace,
      labels: {
        ...commonLabels({ ...args }),
        ...runtimeLabels(),
      },
    },
    spec: {
      ports: [
        {
          name: "8080-tcp",
          protocol: "TCP",
          port: 8080,
          targetPort: 8080,
        },
      ],
      selector: {
        app: args.resourceName,
        deploymentconfig: args.resourceName,
      },
    },
  });
};

export class CreateService extends ResourceFetch {
  constructor(protected args: CreateResourceFetchArgs & { descriptor?: ServiceDescriptor }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(this.args.descriptor ?? SERVICE_TEMPLATE({ ...this.args }).toJSON());
  }

  public endpoint(): string {
    return `/api/${Service.apiVersion}/namespaces/${this.args.namespace}/services`;
  }
}

export class ListServices extends ResourceFetch {
  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/api/${Service.apiVersion}/namespaces/${this.args.namespace}/services`;
  }
}

export class DeleteService extends ResourceFetch {
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
    return `/api${Service.apiVersion}/namespaces/${this.args.namespace}/services/${this.args.resourceName}`;
  }
}
