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

import * as jsYaml from "js-yaml";
import { K8sApiServerEndpointByResourceKind, K8sResourceYaml, consoleDebugMessage } from "./common";
import { CorsProxyHeaderKeys } from "@kie-tools/cors-proxy-api";

export async function callK8sApiServer(args: {
  k8sApiServerEndpointsByResourceKind: K8sApiServerEndpointByResourceKind;
  k8sResourceYamls: K8sResourceYaml[];
  k8sApiServerUrl: string;
  k8sNamespace: string;
  k8sServiceAccountToken: string;
  insecurelyDisableTlsCertificateValidation?: boolean;
}) {
  const apiCalls = args.k8sResourceYamls.map((yamlDocument) => {
    const rawEndpoints = args.k8sApiServerEndpointsByResourceKind
      .get(yamlDocument.kind)
      ?.get(yamlDocument.apiVersion ?? "v1");
    if (!rawEndpoints) {
      throw new Error(
        `Can't create '${yamlDocument.kind}' because there's no matching API for it registered on '${args.k8sApiServerUrl}'`
      );
    }

    const rawEndpoint = rawEndpoints?.url.namespaced ?? rawEndpoints?.url.global;

    return {
      kind: yamlDocument.kind,
      yaml: yamlDocument,
      rawEndpoint: rawEndpoint,
    };
  });
  consoleDebugMessage("Done.");
  consoleDebugMessage("");

  // Simulate actual API calls
  consoleDebugMessage("Start calling API endpoints for each parsed YAML...");

  const results = [];
  for (const apiCall of apiCalls) {
    const endpointUrl = new URL(apiCall.rawEndpoint);
    const interpolatedPathname = endpointUrl.pathname.replace(
      ":namespace",
      apiCall.yaml.metadata?.namespace ?? args.k8sNamespace
    );
    endpointUrl.pathname = interpolatedPathname;

    consoleDebugMessage(`Creating '${apiCall.kind}' with POST ${endpointUrl.toString()}`);
    results.push(
      await fetch(endpointUrl.toString(), {
        headers: {
          Authorization: `Bearer ${args.k8sServiceAccountToken}`,
          "Content-Type": "application/yaml",
          ...(args.insecurelyDisableTlsCertificateValidation
            ? {
                [CorsProxyHeaderKeys.INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION]: Boolean(
                  args.insecurelyDisableTlsCertificateValidation
                ).toString(),
              }
            : {}),
        },
        method: "POST",
        body: jsYaml.dump(apiCall.yaml),
      }).then((response) => response.json())
    );
  }
  consoleDebugMessage("Done.");

  return results;
}
