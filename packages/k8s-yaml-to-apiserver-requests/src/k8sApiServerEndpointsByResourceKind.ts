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

import { K8sApiServerEndpointByResourceKind, consoleDebugMessage } from "./common";
import { CorsProxyHeaderKeys } from "@kie-tools/cors-proxy-api";

type K8sApiResourceList = {
  resources: Array<{
    verbs: string[];
    name: string;
    kind: string;
    namespaced: boolean;
  }>;
};

type K8sApiGroups = {
  groups: Array<{ versions: { groupVersion: string } }>;
};

export async function buildK8sApiServerEndpointsByResourceKind(
  kubeApiServerUrl: string,
  insecurelyDisableTlsCertificateValidation?: boolean,
  token?: string
) {
  const fetchOpts =
    token || insecurelyDisableTlsCertificateValidation // Optional, as local k8s won't require authentication...
      ? {
          headers: {
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(insecurelyDisableTlsCertificateValidation
              ? {
                  [CorsProxyHeaderKeys.INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION]: Boolean(
                    insecurelyDisableTlsCertificateValidation
                  ).toString(),
                }
              : {}),
          },
        }
      : {};

  // Resource kind --> API Group version --> URLs (global and namespaced)
  const map: K8sApiServerEndpointByResourceKind = new Map();

  function ackApiResourceList(apiResourceList: K8sApiResourceList, apiGroupEndpoint: string, apiGroupVersion: string) {
    for (const apiResource of apiResourceList.resources) {
      // We can't accept resources that don't allow being created. E.g., apps/v1/deployment/status
      if (!new Set(apiResource.verbs).has("create")) {
        continue;
      }

      const globalUrl = `${apiGroupEndpoint}/${apiResource.name}`;
      const globalPath = globalUrl.replace(kubeApiServerUrl, "");
      const namespacedUrl = `${apiGroupEndpoint}/namespaces/:namespace/${apiResource.name}`;
      const namespacedPath = namespacedUrl.replace(kubeApiServerUrl, "");

      // That's just for debugging purposes (begin)
      if (map.get(apiResource.kind)?.get(apiGroupVersion)) {
        consoleDebugMessage(`CONFLICT ON '${apiResource.kind}':`);
        consoleDebugMessage(map.get(apiResource.kind)?.get(apiGroupVersion));
        consoleDebugMessage(
          `${apiResource.namespaced}` === "true"
            ? {
                url: { namespaced: namespacedUrl, global: globalUrl },
                path: { namespaced: namespacedPath, global: globalPath },
              }
            : { url: { global: globalUrl }, path: { global: globalPath } }
        );
      }
      // That's just for debugging purposes (end)

      map.set(
        apiResource.kind,
        new Map([
          ...[...(map.get(apiResource.kind) ?? new Map()).entries()],
          [
            apiGroupVersion,
            `${apiResource.namespaced}` === "true"
              ? {
                  url: { namespaced: namespacedUrl, global: globalUrl },
                  path: { namespaced: namespacedPath, global: globalPath },
                }
              : { url: { global: globalUrl }, path: { global: globalPath } },
          ],
        ])
      );
    }
  }

  // Print k8s version
  consoleDebugMessage("Fetching Kubernetes version...");
  const version = await (await fetch(`${kubeApiServerUrl}/version`, fetchOpts)).json();
  consoleDebugMessage(version);
  consoleDebugMessage("");

  // Need to do this separately because the Core API (`/api/v1`) is not listed as part of `/apis`.
  const coreApiEndpoint = `${kubeApiServerUrl}/api/v1`;
  consoleDebugMessage(`Fetching Resources of '${coreApiEndpoint}'`);
  const coreApi: K8sApiResourceList = await (await fetch(`${coreApiEndpoint}`, fetchOpts)).json();
  ackApiResourceList(coreApi, `${coreApiEndpoint}`, "v1");

  // Now we list every API available on `kubeApiServerUrl`
  const apiGroups: K8sApiGroups = await (await fetch(`${kubeApiServerUrl}/apis`, fetchOpts)).json();
  await Promise.all(
    apiGroups.groups
      .flatMap((group) => group.versions)
      .map(async (version) => {
        const endpoint = `${kubeApiServerUrl}/apis/${version.groupVersion}`;
        consoleDebugMessage(`Fetching Resources of '${endpoint}'`);
        const apiResourceList: K8sApiResourceList = await (await fetch(`${endpoint}`, fetchOpts)).json();

        ackApiResourceList(apiResourceList, `${endpoint}`, version.groupVersion);
      })
  );

  return map;
}
