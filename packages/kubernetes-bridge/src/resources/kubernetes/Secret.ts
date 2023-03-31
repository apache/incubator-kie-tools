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
import { Secret, ISecret } from "kubernetes-models/v1";
import { CommonTemplateArgs, commonLabels } from "../common";

export interface CreateSecretArgs {
  data: Record<string, string>;
}

export type SecretDescriptor = ISecret;

export const SECRET_TEMPLATE = (args: CommonTemplateArgs & CreateSecretArgs): Secret => {
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
  });
};

export class CreateSecret extends ResourceFetch {
  constructor(protected args: CreateResourceFetchArgs & CreateSecretArgs & { descriptor?: SecretDescriptor }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(this.args.descriptor ?? SECRET_TEMPLATE({ ...this.args }).toJSON());
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
