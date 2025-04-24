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

import {
  parseK8sResourceYamls,
  buildK8sApiServerEndpointsByResourceKind,
  callK8sApiServer,
  K8sApiServerEndpointByResourceKind,
  interpolateK8sResourceYaml,
  TokenMap,
  K8sResourceYaml,
  patchK8sResourceYaml,
  appendK8sResourceYaml,
} from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import Path from "path";
import { DeploymentState } from "./common";
import { ResourceActions } from "./types";
import { CorsProxyHeaderKeys } from "@kie-tools/cors-proxy-api";

export interface KubernetesConnection {
  namespace: string;
  host: string;
  token: string;
  insecurelyDisableTlsCertificateValidation: boolean;
}

export interface KubernetesServiceArgs {
  connection: KubernetesConnection;
  k8sApiServerEndpointsByResourceKind: K8sApiServerEndpointByResourceKind;
  proxyUrl?: string;
}

export enum KubernetesConnectionStatus {
  CONNECTED = "CONNECTED",
  ERROR = "ERROR",
  MISSING_PERMISSIONS = "MISSING_PERMISSIONS",
  NAMESPACE_NOT_FOUND = "NAMESPACE_NOT_FOUND",
}

export const kubernetesResourcesApi = {
  namespace: {
    kind: "Namespace",
    apiVersion: "v1",
  },
  deployment: {
    kind: "Deployment",
    apiVersion: "apps/v1",
  },
  service: {
    kind: "Service",
    apiVersion: "v1",
  },
  ingress: {
    kind: "Ingress",
    apiVersion: "networking.k8s.io/v1",
  },
} as const;

export type ResourceMetadata = {
  annotations?: Record<string, string>;
  labels?: Record<string, string>;
  name: string;
  namespace: string;
  creationTimestamp: string;
  uid: string;
};

export type Condition = {
  type: string;
  status: string;
  reason: string;
  message: string;
  lastTransitionTime: string;
  lastUpdateTime: string;
};

export type IngressResource = K8sResourceYaml & {
  kind: "Ingress";
  apiVersion: "networking.k8s.io/v1";
  metadata: ResourceMetadata;
  spec: {
    rules: {
      http: {
        paths: {
          backend: {
            service: {
              name: string;
              port: {
                number: number;
              };
            };
            path: string;
            pathType: string;
          };
        }[];
      };
    };
  };
  status: {
    loadBalancer: {
      ingress: {
        hostname: string;
      }[];
    };
  };
};

export type ServiceResource = K8sResourceYaml & {
  kind: "Service";
  apiVersion: "v1";
  metadata: ResourceMetadata;
  spec: any;
};

export type DeploymentResource = K8sResourceYaml & {
  kind: "Deployment";
  apiVersion: "apps/v1";
  metadata: ResourceMetadata;
  spec: {
    replicas: number;
    selector: {
      matchLabels: string;
    };
    template: {
      spec: {
        containers: {
          env: { name: string; value: string }[];
          image: string;
          imagePullPolicy: string;
          name: string;
        }[];
      };
    };
  };
  status: {
    availableReplicas: number;
    readyReplicas: number;
    replicas: number;
    updatedReplicas: number;
    conditions: Condition[];
  };
};

export const isKubernetesConnectionValid = (connection: KubernetesConnection) =>
  isNamespaceValid(connection.namespace) && isHostValid(connection.host) && isTokenValid(connection.token);

export const isNamespaceValid = (namespace: string) => namespace.trim().length > 0;
export const isTokenValid = (token: string) => token.trim().length > 0;
export const isHostValid = (host: string) => {
  if (host.trim().length === 0) {
    return false;
  }
  try {
    new URL(host);
    return true;
  } catch (_) {
    return false;
  }
};

export class KubernetesService {
  baseUrl: string;

  constructor(readonly args: KubernetesServiceArgs) {
    this.args = args;
    this.baseUrl = KubernetesService.getBaseUrl(args);
  }

  public static async getK8sApiServerEndpointsMap(
    args: Omit<KubernetesServiceArgs, "k8sApiServerEndpointsByResourceKind">
  ) {
    const baseUrl = KubernetesService.getBaseUrl(args);
    return await buildK8sApiServerEndpointsByResourceKind(
      baseUrl,
      args.connection.insecurelyDisableTlsCertificateValidation,
      args.connection.token
    );
  }

  public static getBaseUrl(args: Omit<KubernetesServiceArgs, "k8sApiServerEndpointsByResourceKind">) {
    return args.proxyUrl ? `${args.proxyUrl}/${new URL(args.connection.host).host}` : args.connection.host;
  }

  public async kubernetesFetch(path: string, init?: RequestInit): Promise<Response> {
    const url = new URL(Path.join(this.baseUrl, path));
    const headers = {
      Authorization: `Bearer ${this.args.connection.token}`,
      ...(this.args.connection.insecurelyDisableTlsCertificateValidation
        ? {
            [CorsProxyHeaderKeys.INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION]: Boolean(
              this.args.connection.insecurelyDisableTlsCertificateValidation
            ).toString(),
          }
        : {}),
      ...init?.headers,
    };
    return await fetch(url, {
      headers,
      ...init,
    });
  }

  public async applyResourceYamls(args: {
    k8sResourceYamls: string[];
    actions?: ResourceActions[];
    tokens?: TokenMap;
    parametersTokens?: TokenMap;
  }) {
    const processedYamls = args.k8sResourceYamls.map((yamlContent) => {
      let resultYaml = yamlContent;

      args.actions?.forEach(({ appendYamls }) => {
        if (appendYamls) {
          resultYaml = appendYamls.reduce(
            (yaml, yamlToAppend) => appendK8sResourceYaml(yaml, yamlToAppend),
            resultYaml
          );
        }
      });

      args.actions?.forEach(({ resourcePatches }) => {
        if (resourcePatches) {
          resultYaml = patchK8sResourceYaml(resultYaml, resourcePatches, args.parametersTokens);
        }
      });

      resultYaml = interpolateK8sResourceYaml(resultYaml, args.tokens);

      return resultYaml;
    });

    return await callK8sApiServer({
      k8sApiServerEndpointsByResourceKind: this.args.k8sApiServerEndpointsByResourceKind,
      k8sResourceYamls: parseK8sResourceYamls(processedYamls),
      k8sApiServerUrl: this.args.connection.host,
      k8sNamespace: this.args.connection.namespace,
      k8sServiceAccountToken: this.args.connection.token,
      insecurelyDisableTlsCertificateValidation: this.args.connection.insecurelyDisableTlsCertificateValidation,
    });
  }

  public static newResourceName(prefix: string, suffix?: string): string {
    if (suffix) {
      return `${prefix}-${suffix}`;
    }
    const randomPart = Math.random().toString(36).substring(2, 9);
    const milliseconds = new Date().getMilliseconds();
    const randomSuffix = `${randomPart}${milliseconds}`;
    return `${prefix}-${randomSuffix}`;
  }

  public extractDeploymentState(args: { deployment?: DeploymentResource }): DeploymentState {
    if (!args.deployment || !args.deployment.status) {
      // Deployment still being created
      return DeploymentState.IN_PROGRESS;
    }

    if (!args.deployment.status.replicas) {
      // Deployment with no replicas is down
      return DeploymentState.DOWN;
    }

    const progressingCondition = args.deployment.status.conditions?.find(
      (condition: Condition) => condition.type === "Progressing"
    );

    if (!progressingCondition || progressingCondition.status !== "True") {
      // Without `Progressing` condition, the deployment will never be up
      return DeploymentState.DOWN;
    }

    if (!args.deployment.status.readyReplicas) {
      // Deployment is progressing but no replicas are ready yet
      return DeploymentState.IN_PROGRESS;
    }

    return DeploymentState.UP;
  }

  public async getResource<ResourceType extends K8sResourceYaml>(args: {
    kind: string;
    apiVersion: string;
    resourceId: string;
  }): Promise<ResourceType> {
    const rawApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get(args.kind)?.get(args.apiVersion);
    const apiPath = rawApiUrl?.path.namespaced ?? rawApiUrl?.path.global;

    if (apiPath) {
      try {
        return await this.kubernetesFetch(
          `${apiPath.replace(":namespace", this.args.connection.namespace)}/${args.resourceId}`
        )
          .then((data) => data.json())
          .then(
            (jsonData) =>
              ({
                ...jsonData,
                kind: args.kind,
                apiVersion: args.apiVersion,
              }) as ResourceType
          );
      } catch (e) {
        console.error(`Failed to fetch ${args.kind} resource with id ${args.resourceId}: ${e}`);
        throw new Error(`Failed to fetch ${args.kind} resource with id ${args.resourceId}: ${e}`);
      }
    } else {
      console.error(`Failed to find resource of kind ${args.kind} and apiVersion ${args.apiVersion} on endpoints map.`);
      throw new Error(
        `Failed to find resource of kind ${args.kind} and apiVersion ${args.apiVersion} on endpoints map.`
      );
    }
  }

  public async listResources<ResourceType extends K8sResourceYaml>(args: {
    kind: string;
    apiVersion: string;
    queryParams?: string[];
  }): Promise<ResourceType[]> {
    const rawApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get(args.kind)?.get(args.apiVersion);
    const apiPath = rawApiUrl?.path.namespaced ?? rawApiUrl?.path.global;

    if (apiPath) {
      try {
        const resources = await this.kubernetesFetch(
          `${apiPath.replace(":namespace", this.args.connection.namespace)}${
            args.queryParams ? `?${args.queryParams.join("&")}` : ""
          }`
        ).then((data) => data.json());
        return resources.items.map(
          (item: K8sResourceYaml) =>
            ({
              ...item,
              kind: args.kind,
              apiVersion: args.apiVersion,
            }) as ResourceType
        );
      } catch (e) {
        console.error(`Failed to fetch list of ${args.kind}: ${e}`);
        throw new Error(`Failed to fetch list of ${args.kind}: ${e}`);
      }
    } else {
      console.error(`Failed to find resource of kind ${args.kind} and apiVersion ${args.apiVersion} on endpoints map.`);
      throw new Error(
        `Failed to find resource of kind ${args.kind} and apiVersion ${args.apiVersion} on endpoints map.`
      );
    }
  }

  public async deleteResource(args: { kind: string; apiVersion: string; resourceId: string }): Promise<any> {
    const rawApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get(args.kind)?.get(args.apiVersion);
    const apiPath = rawApiUrl?.path.namespaced ?? rawApiUrl?.path.global;

    if (apiPath) {
      try {
        return await this.kubernetesFetch(
          `${apiPath.replace(":namespace", this.args.connection.namespace)}/${args.resourceId}`,
          {
            method: "DELETE",
          }
        ).then((data) => data.json());
      } catch (e) {
        console.error(`Failed to delete ${args.kind} resource with id ${args.resourceId}: ${e}`);
        throw new Error(`Failed to fetch ${args.kind} resource with id ${args.resourceId}: ${e}`);
      }
    } else {
      console.error(`Failed to find resource of kind ${args.kind} and apiVersion ${args.apiVersion} on endpoints map.`);
      throw new Error(
        `Failed to find resource of kind ${args.kind} and apiVersion ${args.apiVersion} on endpoints map.`
      );
    }
  }
}
