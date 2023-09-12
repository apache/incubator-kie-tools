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
import { OpenshiftApiVersions } from "./api";
import { ResourceDataSource, ResourceDescriptor, commonLabels, runtimeLabels } from "../common";

export interface ImageStreamSpec {
  lookupPolicy: {
    local: boolean;
  };
}

export interface ImageStreamDescriptor extends ResourceDescriptor {
  spec: ImageStreamSpec;
}

export type CreateImageStreamTemplateArgs = {
  resourceDataSource: ResourceDataSource.TEMPLATE;
};

export type CreateImageStreamArgs = CreateResourceFetchArgs &
  (
    | CreateImageStreamTemplateArgs
    | { descriptor: ImageStreamDescriptor; resourceDataSource: ResourceDataSource.PROVIDED }
  );

export const IMAGE_STREAM_TEMPLATE = (
  args: CreateResourceFetchArgs & CreateImageStreamTemplateArgs
): ImageStreamDescriptor => ({
  apiVersion: OpenshiftApiVersions.IMAGE_STREAM,
  kind: "ImageStream",
  metadata: {
    name: args.resourceName,
    namespace: args.namespace,
    labels: {
      ...commonLabels({ ...args }),
      ...runtimeLabels(),
    },
  },
  spec: {
    lookupPolicy: {
      local: true,
    },
  },
});

export class CreateImageStream extends ResourceFetch {
  constructor(protected args: CreateImageStreamArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : IMAGE_STREAM_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/apis/${OpenshiftApiVersions.IMAGE_STREAM}/namespaces/${this.args.namespace}/imagestreams`;
  }
}

export class DeleteImageStream extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/apis/${OpenshiftApiVersions.IMAGE_STREAM}/namespaces/${this.args.namespace}/imagestreams/${this.args.resourceName}`;
  }
}
