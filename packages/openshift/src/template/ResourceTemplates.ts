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

import { KNativeApiVersions, KubernetesApiVersions, OpenShiftLabelNames } from "../api/ApiConstants";
import {
  BuildConfigDescriptor,
  DeploymentDescriptor,
  ImageStreamDescriptor,
  KafkaSourceDescriptor,
  KNativeServiceDescriptor,
  RouteDescriptor,
  SecretDescriptor,
  ServiceDescriptor,
  Trigger,
} from "../api/types";
import {
  BUILD_IMAGE_TAG_VERSION,
  commonLabels,
  KAFKA_SOURCE_FINALIZER,
  ResourceLabelNames,
  runtimeLabels,
} from "./TemplateConstants";
import {
  CommonTemplateArgs,
  CreateDeploymentArgs,
  CreateKafkaSourceArgs,
  CreateKNativeServiceArgs,
  CreateSecretArgs,
} from "./types";

export const BUILD_CONFIG_TEMPLATE = (args: CommonTemplateArgs): BuildConfigDescriptor => ({
  apiVersion: KubernetesApiVersions.BUILD_CONFIG,
  kind: "BuildConfig",
  metadata: {
    name: args.resourceName,
    namespace: args.namespace,
    labels: {
      ...commonLabels({ ...args }),
      ...runtimeLabels(),
    },
  },
  spec: {
    output: {
      to: {
        kind: "ImageStreamTag",
        name: `${args.resourceName}:${BUILD_IMAGE_TAG_VERSION}`,
      },
    },
    strategy: {
      type: "Docker",
    },
    source: {
      type: "Binary",
      binary: {},
    },
    resources: {
      limits: {
        memory: "4Gi",
      },
    },
  },
});

export const DEPLOYMENT_TEMPLATE = (args: CommonTemplateArgs & CreateDeploymentArgs): DeploymentDescriptor => {
  const annotations = {
    [ResourceLabelNames.URI]: args.uri,
    [ResourceLabelNames.WORKSPACE_NAME]: args.workspaceName,
  };

  return {
    apiVersion: KubernetesApiVersions.DEPLOYMENT,
    kind: "Deployment",
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
            },
          ],
        },
      },
    },
  };
};

export const IMAGE_STREAM_TEMPLATE = (args: CommonTemplateArgs): ImageStreamDescriptor => ({
  apiVersion: KubernetesApiVersions.IMAGE_STREAM,
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

export const ROUTE_TEMPLATE = (args: CommonTemplateArgs): RouteDescriptor => ({
  apiVersion: KubernetesApiVersions.ROUTE,
  kind: "Route",
  metadata: {
    name: args.resourceName,
    namespace: args.namespace,
    labels: {
      ...commonLabels({ ...args }),
      ...runtimeLabels(),
    },
  },
  spec: {
    to: {
      name: args.resourceName,
      kind: "Service",
    },
    port: {
      targetPort: "8080-tcp",
    },
    tls: {
      termination: "edge",
      insecureEdgeTerminationPolicy: "None",
    },
  },
});

export const SERVICE_TEMPLATE = (args: CommonTemplateArgs): ServiceDescriptor => ({
  apiVersion: KubernetesApiVersions.SERVICE,
  kind: "Service",
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
});

export const SECRET_TEMPLATE = (args: CommonTemplateArgs & CreateSecretArgs): SecretDescriptor => {
  const encodedData = Object.entries(args.data).reduce(
    (acc, [key, value]) => ({
      ...acc,
      [key]: btoa(value),
    }),
    {} as Record<string, string>
  );

  return {
    apiVersion: KubernetesApiVersions.SECRET,
    kind: "Secret",
    metadata: {
      name: args.resourceName,
      namespace: args.namespace,
      labels: commonLabels({ ...args }),
    },
    data: encodedData,
  };
};

export const KAFKA_SOURCE_TEMPLATE = (args: CommonTemplateArgs & CreateKafkaSourceArgs): KafkaSourceDescriptor => ({
  apiVersion: KNativeApiVersions.KAFKA_SOURCE,
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
        apiVersion: KNativeApiVersions.SERVICE,
        kind: "Service",
        name: args.sinkService,
      },
    },
    topics: args.topics,
  },
});

export const KNATIVE_SERVICE_TEMPLATE = (
  args: CommonTemplateArgs & CreateKNativeServiceArgs
): KNativeServiceDescriptor => {
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

  return {
    apiVersion: KNativeApiVersions.SERVICE,
    kind: "Service",
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
  };
};
