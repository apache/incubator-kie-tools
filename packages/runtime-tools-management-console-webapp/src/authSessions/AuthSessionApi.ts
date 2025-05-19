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

import { LfsFsCache } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsFsCache";
import { LfsStorageService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import { IDToken, ServerMetadata, TokenEndpointResponse } from "openid-client";

export const authSessionFsCache = new LfsFsCache();
export const authSessionFsService = new LfsStorageService();
export const authSessionBroadcastChannel = new BroadcastChannel("auth_sessions");

export const AUTH_SESSIONS_FILE_PATH = "/authSessions.json";
export const AUTH_SESSIONS_FS_NAME = "auth_sessions";
export const AUTH_SESSIONS_VERSION_NUMBER = 2;
export const AUTH_SESSIONS_FS_NAME_WITH_VERSION = `${AUTH_SESSIONS_FS_NAME}_v${AUTH_SESSIONS_VERSION_NUMBER.toString()}`;

export const AUTH_SESSION_TEMP_OPENID_AUTH_DATA_STORAGE_KEY = "temporaryOpenIdAuthData";
export const AUTH_SESSION_RUNTIME_AUTH_SERVER_URL_ENDPOINT = "q/oidc";
export const AUTH_SESSION_RUNTIME_AUTH_SERVER_OPENID_CONFIGURATION_PATH = ".well-known/openid-configuration";
export const AUTH_SESSION_OIDC_DEFAULT_SCOPES = "openid email profile";

export function mapSerializer(_: string, value: any) {
  if (value instanceof Map) {
    return {
      __$$jsClassName: "Map",
      value: Array.from(value.entries()),
    };
  }
  return value;
}

export function mapDeSerializer(_: string, value: any) {
  if (typeof value === "object" && value) {
    if (value.__$$jsClassName === "Map") {
      return new Map(value.value);
    }
  }
  return value;
}

export type OpenIDConfiguration = {
  issuer: string;
  token_endpoint: string;
  authorization_endpoint: string;
  userinfo_endpoint: string;
  token_endpoint_auth_methods_supported?: string[];
  jwks_uri: string;
  response_types_supported?: string[];
  grant_types_supported?: string[];
  token_endpoint_auth_signing_alg_values_supported?: string[];
  response_modes_supported?: string[];
  id_token_signing_alg_values_supported?: string[];
  revocation_endpoint: string;
  subject_types_supported?: string[];
  end_session_endpoint: string;
  introspection_endpoint: string;
};

export enum AuthSessionType {
  OPENID_CONNECT = "oidc",
  UNAUTHENTICATED = "unauthenticated",
}

export type OpenIDConnectAuthSession = {
  id: string;
  type: AuthSessionType.OPENID_CONNECT;
  version: number;
  name: string;
  tokens: TokenEndpointResponse;
  username?: string;
  roles?: string[];
  impersonator?: boolean;
  claims: IDToken;
  issuer: string;
  clientId: string;
  clientSecret?: string;
  audience?: string;
  scope: string;
  runtimeUrl: string;
  status: AuthSessionStatus;
  createdAtDateISO: string;
  tokensRefreshedAtDateISO: string;
};

export type UnauthenticatedAuthSession = {
  id: string;
  type: AuthSessionType.UNAUTHENTICATED;
  version: number;
  name: string;
  impersonator: true;
  runtimeUrl: string;
  status: AuthSessionStatus;
  createdAtDateISO: string;
};

export type AuthSession = OpenIDConnectAuthSession | UnauthenticatedAuthSession;

export type OidcAuthUrlParameters = {
  redirect_uri: string;
  scope: string;
  code_verifier: string;
  code_challenge: string;
  code_challenge_method: string;
  nonce?: string;
  prompt?: string;
  state?: string;
  audience?: string;
};

export type TemporaryAuthSessionData =
  | {
      isAuthenticationRequired: true;
      runtimeUrl: string;
      clientId: string;
      name: string;
      parameters: OidcAuthUrlParameters;
      serverMetadata: ServerMetadata;
    }
  | {
      isAuthenticationRequired: false;
      runtimeUrl: string;
      name: string;
    };

export enum AuthSessionStatus {
  VALID,
  INVALID,
}

export function isOpenIdConnectAuthSession(authSession?: AuthSession): authSession is OpenIDConnectAuthSession {
  return authSession?.type === AuthSessionType.OPENID_CONNECT;
}

export function isUnauthenticatedAuthSession(authSession?: AuthSession): authSession is UnauthenticatedAuthSession {
  return authSession?.type === AuthSessionType.UNAUTHENTICATED;
}

export function getAuthSessionDisplayInfo(authSession: undefined | AuthSession) {
  authSession ??= {
    id: "unknwon",
    type: AuthSessionType.UNAUTHENTICATED,
    version: 0,
    name: "Unknown",
    impersonator: true,
    runtimeUrl: "",
    status: AuthSessionStatus.INVALID,
    createdAtDateISO: new Date().toISOString(),
  };

  // username @ http://runtime.url
  const shortDisplayName = `${isOpenIdConnectAuthSession(authSession) ? `${authSession.username ?? "Unknown user"} @ ` : "Unknown user @ "}${authSession.runtimeUrl}`;

  // Session Name (username @ http://runtime.url)
  const fullDisplayName = `${authSession.name} (${shortDisplayName})`;

  // username @ Auth Session Name
  const userFriendlyName = `${isOpenIdConnectAuthSession(authSession) ? `${authSession.username ?? "Unknown user"} @ ` : "Unknown user @ "}${authSession.name}`;

  // OpenID Connect | Unauthenticated
  const type = isOpenIdConnectAuthSession(authSession) ? "OpenID Connect" : "Unauthenticated";

  const username = `${isOpenIdConnectAuthSession(authSession) ? `${authSession.username ?? "Unknown user"}` : "Unknown user"}`;

  return {
    shortDisplayName,
    fullDisplayName,
    userFriendlyName,
    type,
    username,
  };
}

export function parseJwtToken(token: string) {
  const base64Url = token.split(".")[1];
  const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  const jsonPayload = decodeURIComponent(
    window
      .atob(base64)
      .split("")
      .map((c) => {
        return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join("")
  );

  return JSON.parse(jsonPayload);
}
