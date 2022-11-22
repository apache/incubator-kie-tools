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
  email?: string;
  name?: string;
  authProviderId: string;
  createdAtDateISO: string;
};

export type OpenShiftAuthSession = {
  type: "openshift";
  id: string;
  authProviderId: string;
  createdAtDateISO: string;
  token: string;
  namespace: string;
  host: string;
};

export enum AuthSessionStatus {
  VALID,
  INVALID,
}

export type AuthSession = GitAuthSession | OpenShiftAuthSession | NoneAuthSession;
