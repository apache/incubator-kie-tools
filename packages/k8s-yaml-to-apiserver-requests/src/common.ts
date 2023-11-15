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

export type K8sResourceYamlMetadata = {
  name?: string;
  namespace?: string;
};

export type K8sResourceYaml = {
  apiVersion: string;
  kind: string;
  metadata?: K8sResourceYamlMetadata;
};

export type K8sApiServerEndpointByResourceKind = Map<
  string,
  Map<string, { url: { namespaced?: string; global: string }; path: { namespaced?: string; global: string } }>
>;

export function isValidK8sResource(content: any): content is K8sResourceYaml {
  return (
    "apiVersion" in content &&
    typeof content.apiVersion == "string" &&
    "kind" in content &&
    typeof content.kind == "string" &&
    (!("metadata" in content) || ("metadata" in content && typeof content === "object"))
  );
}

export function consoleDebugMessage(message: any) {
  console.debug("K8S YAML TO APISERVER:", message);
}
