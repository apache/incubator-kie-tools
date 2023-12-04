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

import { K8sApiServerEndpointByResourceKind } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";

export const AUTH_SESSION_VERSION = 1;

export const AUTH_SESSION_NONE = {
  id: "none",
  name: "Unauthenticated",
  type: "none",
  login: "Unauthenticated",
  version: AUTH_SESSION_VERSION,
} as const;

export type NoneAuthSession = typeof AUTH_SESSION_NONE;

export type BaseAuthSession = {
  type: string;
  id: string;
  version: number;
};

export type GitAuthSession = BaseAuthSession & {
  type: "git";
  token: string;
  login: string;
  uuid?: string;
  email?: string;
  name?: string;
  authProviderId: string;
  createdAtDateISO: string;
};

export enum CloudAuthSessionType {
  OpenShift = "openshift",
  Kubernetes = "kubernetes",
  None = "none",
}

export type OpenShiftAuthSession = BaseAuthSession & {
  type: CloudAuthSessionType.OpenShift;
  authProviderId: string;
  createdAtDateISO: string;
  token: string;
  namespace: string;
  host: string;
  insecurelyDisableTlsCertificateValidation: boolean;
  k8sApiServerEndpointsByResourceKind: K8sApiServerEndpointByResourceKind;
};

export type KubernetesAuthSession = BaseAuthSession & {
  type: CloudAuthSessionType.Kubernetes;
  authProviderId: string;
  createdAtDateISO: string;
  token: string;
  namespace: string;
  host: string;
  insecurelyDisableTlsCertificateValidation: boolean;
  k8sApiServerEndpointsByResourceKind: K8sApiServerEndpointByResourceKind;
};

export type CloudAuthSession = OpenShiftAuthSession | KubernetesAuthSession;

export enum AuthSessionStatus {
  VALID,
  INVALID,
}

export type AuthSession = GitAuthSession | OpenShiftAuthSession | KubernetesAuthSession | NoneAuthSession;

export function isCloudAuthSession(authSession: AuthSession): authSession is CloudAuthSession {
  return ["openshift", "kubernetes"].includes(authSession.type);
}

export function isGitAuthSession(authSession: AuthSession): authSession is GitAuthSession {
  return authSession.type === "git";
}
