/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { HttpMethod, JAVA_RUNTIME_VERSION, ResourceFetch } from "./Resource";

const API_ENDPOINT = "apis/route.openshift.io/v1";

export class CreateRoute extends ResourceFetch {
  protected method(): HttpMethod {
    return "POST";
  }

  protected requestBody(): string | undefined {
    return `
      kind: Route
      apiVersion: route.openshift.io/v1
      metadata:
        name: ${this.args.resourceName}
        namespace: ${this.args.namespace}
        labels:
          app: ${this.args.resourceName}
          app.kubernetes.io/component: ${this.args.resourceName}
          app.kubernetes.io/instance: ${this.args.resourceName}
          app.kubernetes.io/part-of: ${this.args.resourceName}
          app.kubernetes.io/name: java
          app.openshift.io/runtime: quarkus
          app.openshift.io/runtime-version: ${JAVA_RUNTIME_VERSION}
      spec:
        to:
          kind: Service
          name: ${this.args.resourceName}
        port:
          targetPort: 8080-tcp
        tls:
          termination: edge
          insecureEdgeTerminationPolicy: None
    `;
  }

  public name(): string {
    return CreateRoute.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/routes`;
  }
}

export class ListRoutes extends ResourceFetch {
  protected method(): HttpMethod {
    return "GET";
  }

  protected requestBody(): string | undefined {
    return;
  }

  public name(): string {
    return ListRoutes.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/routes`;
  }
}

export class DeleteRoute extends ResourceFetch {
  protected method(): HttpMethod {
    return "DELETE";
  }

  protected requestBody(): string | undefined {
    return;
  }

  public name(): string {
    return DeleteRoute.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/routes/${this.args.resourceName}`;
  }
}
