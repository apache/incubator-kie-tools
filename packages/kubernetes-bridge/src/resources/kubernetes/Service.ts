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
import { Service, IService } from "kubernetes-models/v1";
import { ResourceDataSource, ResourceMetadataEnforcer, commonLabels, runtimeLabels } from "../common";

export type ServiceDescriptor = IService & ResourceMetadataEnforcer;

export type CreateServiceTemplateArgs = {
  resourceDataSource: ResourceDataSource.TEMPLATE;
};

export type CreateServiceArgs = CreateResourceFetchArgs &
  (CreateServiceTemplateArgs | { descriptor: ServiceDescriptor; resourceDataSource: ResourceDataSource.PROVIDED });

export const SERVICE_TEMPLATE = (args: CreateResourceFetchArgs & CreateServiceTemplateArgs): ServiceDescriptor => {
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
  }).toJSON();
};

export class CreateService extends ResourceFetch {
  constructor(protected args: CreateServiceArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : SERVICE_TEMPLATE({ ...this.args })
    );
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
    return `/api/${Service.apiVersion}/namespaces/${this.args.namespace}/services/${this.args.resourceName}`;
  }
}
