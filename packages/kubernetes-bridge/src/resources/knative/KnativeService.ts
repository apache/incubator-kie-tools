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

import { HttpMethod } from "../../fetch/FetchConstants";
import {
  CreateResourceFetchArgs,
  ResourceFetch,
  ResourceFetchArgs,
  UniqueResourceFetchArgs,
} from "../../fetch/ResourceFetch";
import { OpenShiftLabelNames } from "../openshift/api";
import {
  BUILD_IMAGE_TAG_VERSION,
  ResourceDataSource,
  ResourceGroupDescriptor,
  ResourceLabelNames,
  ResourceMetadataEnforcer,
  commonLabels,
  runtimeLabels,
} from "../common";
import {
  Service as KnativeService,
  IService as IKnativeService,
} from "@kubernetes-models/knative/serving.knative.dev/v1/Service";

export type KnativeServiceDescriptor = IKnativeService & ResourceMetadataEnforcer;

export type KnativeServiceGroupDescriptor = ResourceGroupDescriptor<KnativeServiceDescriptor>;

export interface Trigger {
  from: {
    kind: string;
    name: string;
    namespace: string;
  };
  fieldPath: string;
  pause: boolean;
}

export interface CreateKnativeServiceTemplateArgs {
  uri: string;
  workspaceName: string;
  resourceDataSource: ResourceDataSource.TEMPLATE;
}

export type CreateKnativeServiceArgs = CreateResourceFetchArgs &
  (
    | CreateKnativeServiceTemplateArgs
    | { descriptor: KnativeServiceDescriptor; resourceDataSource: ResourceDataSource.PROVIDED }
  );

export const KNATIVE_SERVICE_TEMPLATE = (
  args: CreateResourceFetchArgs & CreateKnativeServiceTemplateArgs
): KnativeServiceDescriptor => {
  const imageStreamTrigger: Trigger = {
    from: {
      name: `${args.resourceName}:${BUILD_IMAGE_TAG_VERSION}`,
      namespace: args.namespace,
      kind: "ImageStreamTag",
    },
    pause: false,
    fieldPath: `spec.template.spec.containers[?(@.name=="${args.resourceName}")].image`,
  };

  const annotations = {
    [OpenShiftLabelNames.TRIGGERS]: JSON.stringify([imageStreamTrigger]),
    [ResourceLabelNames.URI]: args.uri,
    [ResourceLabelNames.WORKSPACE_NAME]: args.workspaceName,
  };

  return new KnativeService({
    metadata: {
      name: args.resourceName,
      namespace: args.namespace,
      labels: {
        ...commonLabels({ ...args }),
        ...runtimeLabels(),
      },
      annotations,
    },
    spec: {
      template: {
        spec: {
          containers: [
            {
              name: args.resourceName,
              image: `image-registry.openshift-image-registry.svc:5000/${args.namespace}/${args.resourceName}:${BUILD_IMAGE_TAG_VERSION}`,
            },
          ],
        },
      },
    },
  }).toJSON();
};

export class CreateKnativeService extends ResourceFetch {
  constructor(protected args: CreateKnativeServiceArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : KNATIVE_SERVICE_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/apis/${KnativeService.apiVersion}/namespaces/${this.args.namespace}/services`;
  }
}

export class ListKnativeServices extends ResourceFetch {
  constructor(protected args: ResourceFetchArgs & { labelSelector?: string }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    const selector = this.args.labelSelector ? `?labelSelector=${this.args.labelSelector}` : "";
    return `/apis/${KnativeService.apiVersion}/namespaces/${this.args.namespace}/services${selector}`;
  }
}

export class DeleteKnativeService extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/apis/${KnativeService.apiVersion}/namespaces/${this.args.namespace}/services/${this.args.resourceName}`;
  }
}

export class GetKnativeService extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/apis/${KnativeService.apiVersion}/namespaces/${this.args.namespace}/services/${this.args.resourceName}`;
  }
}
