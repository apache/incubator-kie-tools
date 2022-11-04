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

import { baseEndpoint, KNativeApiVersions } from "../ApiConstants";
import { HttpMethod } from "../../fetch/FetchConstants";
import {
  CreateResourceFetchArgs,
  ResourceFetch,
  ResourceFetchArgs,
  UniqueResourceFetchArgs,
} from "../../fetch/ResourceFetch";
import { KNATIVE_SERVICE_TEMPLATE } from "../../template/ResourceTemplates";
import { KNativeServiceDescriptor } from "../types";
import { CreateKNativeServiceArgs } from "../../template/types";

export class CreateKNativeService extends ResourceFetch {
  constructor(
    protected args: CreateResourceFetchArgs & CreateKNativeServiceArgs & { descriptor?: KNativeServiceDescriptor }
  ) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(this.args.descriptor ?? KNATIVE_SERVICE_TEMPLATE({ ...this.args }));
  }

  public endpoint(): string {
    return `/${baseEndpoint(KNativeApiVersions.SERVICE)}/namespaces/${this.args.namespace}/services`;
  }
}

export class ListKNativeServices extends ResourceFetch {
  constructor(protected args: ResourceFetchArgs & { labelSelector?: string }) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    const selector = this.args.labelSelector ? `?labelSelector=${this.args.labelSelector}` : "";
    return `/${baseEndpoint(KNativeApiVersions.SERVICE)}/namespaces/${this.args.namespace}/services${selector}`;
  }
}

export class DeleteKNativeService extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/${baseEndpoint(KNativeApiVersions.SERVICE)}/namespaces/${this.args.namespace}/services/${
      this.args.resourceName
    }`;
  }
}

export class GetKNativeService extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.GET;
  }

  public endpoint(): string {
    return `/${baseEndpoint(KNativeApiVersions.SERVICE)}/namespaces/${this.args.namespace}/services/${
      this.args.resourceName
    }`;
  }
}
