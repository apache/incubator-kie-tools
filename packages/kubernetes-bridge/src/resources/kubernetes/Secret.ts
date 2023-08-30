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
import { Secret, ISecret } from "kubernetes-models/v1";
import { ResourceDataSource, ResourceMetadataEnforcer, commonLabels } from "../common";

export type CreateSecretTemplateArgs = {
  data: Record<string, string>;
  resourceDataSource: ResourceDataSource.TEMPLATE;
};

export type CreateSecretArgs = CreateResourceFetchArgs &
  (CreateSecretTemplateArgs | { descriptor: SecretDescriptor; resourceDataSource: ResourceDataSource.PROVIDED });

export type SecretDescriptor = ISecret & ResourceMetadataEnforcer;

export const SECRET_TEMPLATE = (args: CreateResourceFetchArgs & CreateSecretTemplateArgs): SecretDescriptor => {
  const encodedData = Object.entries(args.data).reduce(
    (acc, [key, value]) => ({
      ...acc,
      [key]: btoa(value),
    }),
    {} as Record<string, string>
  );

  return new Secret({
    metadata: {
      name: args.resourceName,
      namespace: args.namespace,
      labels: commonLabels({ ...args }),
    },
    data: encodedData,
  }).toJSON();
};

export class CreateSecret extends ResourceFetch {
  constructor(protected args: CreateSecretArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : SECRET_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/api/${Secret.apiVersion}/namespaces/${this.args.namespace}/secrets`;
  }
}

export class DeleteSecret extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/api/${Secret.apiVersion}/namespaces/${this.args.namespace}/secrets/${this.args.resourceName}`;
  }
}
