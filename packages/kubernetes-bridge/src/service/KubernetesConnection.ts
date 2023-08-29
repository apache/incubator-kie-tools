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

export interface KubernetesConnection {
  namespace: string;
  host: string;
  token: string;
  insecurelyDisableTlsCertificateValidation: boolean;
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
