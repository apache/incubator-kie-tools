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
  parseK8sResourceYaml,
  buildK8sApiServerEndpointsByResourceKind,
  callK8sApiServer,
  K8sApiServerEndpointByResourceKind,
  interpolateK8sResourceYamls,
  TokenMap,
} from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { getNamespaceApiPath } from "./resources/kubernetes/Namespace";
import { createSelfSubjectAccessReviewYaml } from "./resources/kubernetes/SelfSubjectAccessReview";
import { DeploymentState } from "./common";

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

export const EMPTY_KUBERNETES_CONNECTION = {
  namespace: "",
  host: "",
  token: "",
  insecurelyDisableTlsCertificateValidation: false,
};

export enum KubernetesConnectionStatus {
  CONNECTED = "CONNECTED",
  ERROR = "ERROR",
  MISSING_PERMISSIONS = "MISSING_PERMISSIONS",
  NAMESPACE_NOT_FOUND = "NAMESPACE_NOT_FOUND",
}

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
    return await buildK8sApiServerEndpointsByResourceKind(baseUrl, args.connection.token);
  }

  public static getBaseUrl(args: Omit<KubernetesServiceArgs, "k8sApiServerEndpointsByResourceKind">) {
    return args.proxyUrl ? `${args.proxyUrl}/${new URL(args.connection.host).host}` : args.connection.host;
  }

  public kubernetesFetch(path: string, init?: RequestInit): Promise<Response> {
    return fetch(`${this.baseUrl}/${path}`, init);
  }

  public composeDeploymentUrlFromIngress(ingress: any): string {
    return `${new URL(this.args.connection.host).origin}/${ingress.metadata.name}`;
  }

  public extractDeploymentState(args: { deployment?: any }): DeploymentState {
    if (!args.deployment || !args.deployment.status) {
      // Deployment still being created
      return DeploymentState.IN_PROGRESS;
    }

    if (!args.deployment.status.replicas) {
      // Deployment with no replicas is down
      return DeploymentState.DOWN;
    }

    const progressingCondition = args.deployment.status.conditions?.find(
      (condition: any) => condition.type === "Progressing"
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

  public async getNamespace() {
    return this.kubernetesFetch(`/api/v1/namespaces/${this.args.connection.namespace}`);
  }

  public async isConnectionEstablished(
    connection: KubernetesConnection,
    requiredResources: string[] = ["deployments", "services", "ingresses"],
    skipNamespaceCheck = false
  ): Promise<KubernetesConnectionStatus> {
    if (!skipNamespaceCheck) {
      try {
        try {
          await this.kubernetesFetch(getNamespaceApiPath(this.args.connection.namespace), { method: "GET" });
        } catch (e) {
          if (e.cause.status === 404) {
            return KubernetesConnectionStatus.NAMESPACE_NOT_FOUND;
          }
          throw e;
        }
        const permissionsResultMap = await callK8sApiServer({
          k8sApiServerEndpointsByResourceKind: this.args.k8sApiServerEndpointsByResourceKind,
          k8sResourceYamls: parseK8sResourceYaml(
            requiredResources.map((resource) =>
              interpolateK8sResourceYamls(createSelfSubjectAccessReviewYaml, {
                namespace: this.args.connection.namespace,
                resource,
              })
            )
          ),
          k8sApiServerUrl: KubernetesService.getBaseUrl({ ...this.args, connection }),
          k8sNamespace: connection.namespace,
          k8sServiceAccountToken: connection.token,
        }).then((results) =>
          results.map((result) => ({
            resource: result.spec.resourceAttributes.resource,
            allowed: result.status.allowed,
          }))
        );

        console.log(permissionsResultMap);

        if (permissionsResultMap.some((permissionResult) => !permissionResult.allowed)) {
          return KubernetesConnectionStatus.MISSING_PERMISSIONS;
        }

        return KubernetesConnectionStatus.CONNECTED;
      } catch (error) {
        console.error(error);
        return KubernetesConnectionStatus.ERROR;
      }
    }
    return KubernetesConnectionStatus.CONNECTED;
  }

  public async applyResourceYamls(k8sResourceYamls: string[], tokens?: TokenMap) {
    const interpolatedYamls = tokens
      ? k8sResourceYamls.map((yamlContent) => interpolateK8sResourceYamls(yamlContent, tokens))
      : k8sResourceYamls;
    return await callK8sApiServer({
      k8sApiServerEndpointsByResourceKind: this.args.k8sApiServerEndpointsByResourceKind,
      k8sResourceYamls: parseK8sResourceYaml(interpolatedYamls),
      k8sApiServerUrl: this.args.connection.host,
      k8sNamespace: this.args.connection.namespace,
      k8sServiceAccountToken: this.args.connection.token,
    });
  }

  public newResourceName(prefix: string): string {
    const randomPart = Math.random().toString(36).substring(2, 9);
    const milliseconds = new Date().getMilliseconds();
    const suffix = `${randomPart}${milliseconds}`;
    return `${prefix}-${suffix}`;
  }
}
