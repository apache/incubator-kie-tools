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

import { HttpMethod } from "../../fetch/FetchConstants";
import {
  CreateResourceFetchArgs,
  ResourceFetch,
  ResourceFetchArgs,
  UniqueResourceFetchArgs,
} from "../../fetch/ResourceFetch";
import { Deployment, IDeployment, IDeploymentCondition } from "kubernetes-models/apps/v1";
import {
  EnvVar,
  ResourceDataSource,
  ResourceGroupDescriptor,
  ResourceLabelNames,
  ResourceMetadataEnforcer,
  commonLabels,
  runtimeLabels,
} from "../common";
import { IContainer } from "kubernetes-models/v1";

export type CreateDeploymentTemplateArgs = {
  uri: string;
  baseUrl: string;
  workspaceName: string;
  containerImageUrl: string;
  envVars: EnvVar[];
  resourceDataSource: ResourceDataSource.TEMPLATE;
  imagePullPolicy?: IContainer["imagePullPolicy"];
};

export type CreateDeploymentArgs = CreateResourceFetchArgs &
  (
    | CreateDeploymentTemplateArgs
    | { descriptor: DeploymentDescriptor; resourceDataSource: ResourceDataSource.PROVIDED }
  );

export type DeploymentDescriptor = IDeployment & ResourceMetadataEnforcer;

export type DeploymentGroupDescriptor = ResourceGroupDescriptor<DeploymentDescriptor>;

export type DeploymentCondition = IDeploymentCondition;

export const DEPLOYMENT_TEMPLATE = (
  args: CreateResourceFetchArgs & CreateDeploymentTemplateArgs
): DeploymentDescriptor => {
  const annotations = {
    [ResourceLabelNames.URI]: args.uri,
    [ResourceLabelNames.WORKSPACE_NAME]: args.workspaceName,
  };

  return new Deployment({
    metadata: {
      annotations,
      name: args.resourceName,
      namespace: args.namespace,
      labels: {
        ...commonLabels({ ...args }),
        ...runtimeLabels(),
      },
    },
    spec: {
      replicas: 1,
      selector: {
        matchLabels: {
          app: args.resourceName,
        },
      },
      template: {
        metadata: {
          labels: {
            app: args.resourceName,
            deploymentconfig: args.resourceName,
          },
        },
        spec: {
          containers: [
            {
              name: args.resourceName,
              image: args.containerImageUrl,
              ports: [
                {
                  containerPort: 8080,
                  protocol: "TCP",
                },
              ],
              env: args.envVars,
              imagePullPolicy: args.imagePullPolicy ?? "Always",
            },
          ],
        },
      },
    },
  }).toJSON();
};

export class CreateDeployment extends ResourceFetch {
  constructor(protected args: CreateDeploymentArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : DEPLOYMENT_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/apis/${Deployment.apiVersion}/namespaces/${this.args.namespace}/deployments`;
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
    return `/apis/${Deployment.apiVersion}/namespaces/${this.args.namespace}/deployments${selector}`;
  }
}

export class DeleteDeployment extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public body(): string {
    return JSON.stringify({
      propagationPolicy: "Background",
    });
  }

  public endpoint(): string {
    return `/apis/${Deployment.apiVersion}/namespaces/${this.args.namespace}/deployments/${this.args.resourceName}`;
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
    return `/apis/${Deployment.apiVersion}/namespaces/${this.args.namespace}/deployments/${this.args.resourceName}`;
  }
}

export class UpdateDeployment extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs & { descriptor: DeploymentDescriptor }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.PUT;
  }

  public body(): string {
    return JSON.stringify(this.args.descriptor);
  }

  public endpoint(): string {
    return `/apis/${Deployment.apiVersion}/namespaces/${this.args.namespace}/deployments/${this.args.resourceName}`;
  }
}
