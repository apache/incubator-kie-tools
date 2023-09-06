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

export const AUTH_SESSION_NONE = {
  id: "none",
  name: "Unauthenticated",
  type: "none",
  login: "Unauthenticated",
} as const;

export type NoneAuthSession = typeof AUTH_SESSION_NONE;

export type GitAuthSession = {
  type: "git";
  id: string;
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
}

export type OpenShiftAuthSession = {
  type: CloudAuthSessionType.OpenShift;
  id: string;
  authProviderId: string;
  createdAtDateISO: string;
  token: string;
  namespace: string;
  host: string;
  insecurelyDisableTlsCertificateValidation: boolean;
};

export type KubernetesAuthSession = {
  type: CloudAuthSessionType.Kubernetes;
  id: string;
  authProviderId: string;
  createdAtDateISO: string;
  token: string;
  namespace: string;
  host: string;
  insecurelyDisableTlsCertificateValidation: boolean;
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
