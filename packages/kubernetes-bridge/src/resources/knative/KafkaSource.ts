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
import { ResourceDataSource, ResourceDescriptor, commonLabels } from "../common";
import { HttpMethod } from "../../fetch/FetchConstants";
import { KAFKA_SOURCE_FINALIZER, KnativeApiVersions } from "./api";

export interface SecretKeyRef {
  key: string;
  name: string;
}

export interface SinkRef {
  apiVersion: string;
  kind: string;
  name: string;
}

export interface KafkaSourceSpec {
  bootstrapServers: string[];
  consumerGroup: string;
  net: {
    tls: {
      enable: boolean;
    };
    sasl: {
      enable: boolean;
      type: {
        secretKeyRef: SecretKeyRef;
      };
      password: {
        secretKeyRef: SecretKeyRef;
      };
      user: {
        secretKeyRef: SecretKeyRef;
      };
    };
  };
  sink: {
    ref: SinkRef;
  };
  topics: string[];
}

export interface KafkaSourceDescriptor extends ResourceDescriptor {
  spec: KafkaSourceSpec;
}

export interface CreateKafkaSourceTemplateArgs {
  sinkService: string;
  bootstrapServers: string[];
  topics: string[];
  secret: {
    name: string;
    keyId: string;
    keySecret: string;
    keyMechanism: string;
  };
  resourceDataSource: ResourceDataSource.TEMPLATE;
}

export type CreateKafkaSourceArgs = CreateResourceFetchArgs &
  (
    | CreateKafkaSourceTemplateArgs
    | { descriptor: KafkaSourceDescriptor; resourceDataSource: ResourceDataSource.PROVIDED }
  );

export const KAFKA_SOURCE_TEMPLATE = (
  args: CreateResourceFetchArgs & CreateKafkaSourceTemplateArgs
): KafkaSourceDescriptor => ({
  apiVersion: KnativeApiVersions.KAFKA_SOURCE,
  kind: "KafkaSource",
  metadata: {
    name: args.resourceName,
    namespace: args.namespace,
    finalizers: [KAFKA_SOURCE_FINALIZER],
    labels: commonLabels({ ...args }),
  },
  spec: {
    bootstrapServers: args.bootstrapServers,
    consumerGroup: args.createdBy,
    net: {
      tls: {
        enable: true,
      },
      sasl: {
        enable: true,
        type: {
          secretKeyRef: {
            name: args.secret.name,
            key: args.secret.keyMechanism,
          },
        },
        user: {
          secretKeyRef: {
            name: args.secret.name,
            key: args.secret.keyId,
          },
        },
        password: {
          secretKeyRef: {
            name: args.secret.name,
            key: args.secret.keySecret,
          },
        },
      },
    },
    sink: {
      ref: {
        apiVersion: KnativeApiVersions.SERVICE,
        kind: "Service",
        name: args.sinkService,
      },
    },
    topics: args.topics,
  },
});

export class CreateKafkaSource extends ResourceFetch {
  constructor(protected args: CreateKafkaSourceArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.POST;
  }

  public body(): string {
    return JSON.stringify(
      this.args.resourceDataSource === ResourceDataSource.PROVIDED
        ? this.args.descriptor
        : KAFKA_SOURCE_TEMPLATE({ ...this.args })
    );
  }

  public endpoint(): string {
    return `/apis/${KnativeApiVersions.KAFKA_SOURCE}/namespaces/${this.args.namespace}/kafkasources`;
  }
}

export class DeleteKafkaSource extends ResourceFetch {
  constructor(protected args: UniqueResourceFetchArgs) {
    super(args);
  }

  public method(): HttpMethod {
    return HttpMethod.DELETE;
  }

  public endpoint(): string {
    return `/apis/${KnativeApiVersions.KAFKA_SOURCE}/namespaces/${this.args.namespace}/kafkasources/${this.args.resourceName}`;
  }
}
