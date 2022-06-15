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

import { HttpMethod, RESOURCE_CREATED_BY, ResourceArgs, ResourceFetch } from "./Resource";

const API_ENDPOINT = "apis/sources.knative.dev/v1beta1";

interface CreateKafkaSourceArgs {
  sinkService: string;
  bootstrapServer: string;
  topic: string;
  secret: {
    name: string;
    keyId: string;
    keySecret: string;
    keyMechanism: string;
  };
}

export class CreateKafkaSource extends ResourceFetch {
  public constructor(protected args: ResourceArgs & CreateKafkaSourceArgs) {
    super(args);
  }

  protected method(): HttpMethod {
    return "POST";
  }

  protected async requestBody(): Promise<string> {
    return `
    kind: KafkaSource
    apiVersion: sources.knative.dev/v1beta1
    metadata:
      name: ${this.args.resourceName}
      namespace: ${this.args.namespace}
      finalizers:
      - kafkasources.sources.knative.dev
      labels:
        app: ${this.args.resourceName}
        app.kubernetes.io/component: ${this.args.resourceName}
        app.kubernetes.io/instance: ${this.args.resourceName}
        app.kubernetes.io/part-of: ${this.args.resourceName}
        app.kubernetes.io/name: ${this.args.resourceName}
        ${RESOURCE_CREATED_BY}: ${this.args.createdBy}
    spec:
      bootstrapServers:
        - '${this.args.bootstrapServer}'
      consumerGroup: ${this.args.createdBy}
      net:
        tls:
          enable: true
        sasl:
          enable: true
          type:
            secretKeyRef:
              key: ${this.args.secret.keyMechanism}
              name: ${this.args.secret.name}
          password:
            secretKeyRef:
              key: ${this.args.secret.keySecret}
              name: ${this.args.secret.name}
          user:
            secretKeyRef:
              key: ${this.args.secret.keyId}
              name: ${this.args.secret.name}
      sink:
        ref:
          apiVersion: serving.knative.dev/v1
          kind: Service
          name: ${this.args.sinkService}
      topics:
        - ${this.args.topic}
  `;
  }

  public name(): string {
    return CreateKafkaSource.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/kafkasources`;
  }
}

export class DeleteKafkaSource extends ResourceFetch {
  protected method(): HttpMethod {
    return "DELETE";
  }

  public name(): string {
    return DeleteKafkaSource.name;
  }

  public url(): string {
    return `${this.args.host}/${API_ENDPOINT}/namespaces/${this.args.namespace}/kafkasources/${this.args.resourceName}`;
  }
}
