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

import React, { createContext, PropsWithChildren, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { LfsFsCache } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsFsCache";
import { LfsStorageFile, LfsStorageService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { fetchAuthenticatedBitbucketUser, fetchAuthenticatedGitHubUser } from "../accounts/git/ConnectToGitSection";
import { AuthSession, AuthSessionStatus, AUTH_SESSION_NONE } from "./AuthSessionApi";
import { KieSandboxOpenShiftService } from "../devDeployments/services/KieSandboxOpenShiftService";
import { isGitAuthProvider, isSupportedGitAuthProviderType } from "../authProviders/AuthProvidersApi";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { KubernetesConnectionStatus } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { useEnv } from "../env/hooks/EnvContext";
import { KieSandboxKubernetesService } from "../devDeployments/services/KieSandboxKubernetesService";
import { applyAuthSessionMigrations } from "./AuthSessionMigrations";

export type AuthSessionsContextType = {
  authSessions: Map<string, AuthSession>;
  authSessionStatus: Map<string, AuthSessionStatus>;
};

export type AuthSessionsDispatchContextType = {
  recalculateAuthSessionStatus: () => void;
  add: (authSession: AuthSession) => void;
  remove: (authSession: AuthSession) => void;
};

const AuthSessionsContext = createContext<AuthSessionsContextType>({} as any);
const AuthSessionsDispatchContext = createContext<AuthSessionsDispatchContextType>({} as any);

export function useAuthSessions() {
  return useContext(AuthSessionsContext);
}

export function useAuthSessionsDispatch() {
  return useContext(AuthSessionsDispatchContext);
}

const fsCache = new LfsFsCache();
const fsService = new LfsStorageService();
const broadcastChannel = new BroadcastChannel("auth_sessions");

const AUTH_SESSIONS_FILE_PATH = "/authSessions.json";
const AUTH_SESSIONS_FS_NAME = "auth_sessions";

function mapSerializer(_: string, value: any) {
  if (value instanceof Map) {
    return {
      __$$jsClassName: "Map",
      value: Array.from(value.entries()),
    };
  }
  return value;
}

function mapDeSerializer(_: string, value: any) {
  if (typeof value === "object" && value) {
    if (value.__$$jsClassName === "Map") {
      return new Map(value.value);
    }
  }
  return value;
}

export function AuthSessionsContextProvider(props: PropsWithChildren<{}>) {
  const authProviders = useAuthProviders();
  const { env } = useEnv();
  const [authSessions, setAuthSessions] = useState<Map<string, AuthSession>>();
  const [authSessionStatus, setAuthSessionStatus] = useState<Map<string, AuthSessionStatus>>();

  const getAuthSessionsFromFile = useCallback(async () => {
    const fs = fsCache.getOrCreateFs(AUTH_SESSIONS_FS_NAME);
    const content = await (await fsService.getFile(fs, AUTH_SESSIONS_FILE_PATH))?.getFileContents();
    const parsedAuthSessions = JSON.parse(decoder.decode(content), mapDeSerializer);
    return parsedAuthSessions;
  }, []);

  const refresh = useCallback(async () => {
    setAuthSessions(await getAuthSessionsFromFile());
  }, [getAuthSessionsFromFile]);

  const persistAuthSessions = useCallback(
    async (map: Map<string, AuthSession>) => {
      const fs = fsCache.getOrCreateFs(AUTH_SESSIONS_FS_NAME);
      await fsService.createOrOverwriteFile(
        fs,
        new LfsStorageFile({
          path: AUTH_SESSIONS_FILE_PATH,
          getFileContents: async () => encoder.encode(JSON.stringify(map, mapSerializer)),
        })
      );

      // This goes to other broadcast channel instances, on other tabs
      broadcastChannel.postMessage("UPDATE_AUTH_SESSIONS");

      // This updates this tab
      refresh();
    },
    [refresh]
  );

  const add = useCallback(
    (authSession: AuthSession) => {
      const n = new Map(authSessions?.entries() ?? []);
      n?.set(authSession.id, authSession);
      persistAuthSessions(n);
    },
    [authSessions, persistAuthSessions]
  );

  const remove = useCallback(
    (authSession: AuthSession) => {
      const n = new Map(authSessions?.entries() ?? []);
      n?.delete(authSession.id);
      persistAuthSessions(n);
    },
    [authSessions, persistAuthSessions]
  );

  // Update after persisted
  useEffect(() => {
    broadcastChannel.onmessage = refresh;
  }, [refresh]);

  // Init
  useEffect(() => {
    async function run() {
      const fs = fsCache.getOrCreateFs(AUTH_SESSIONS_FS_NAME);
      if (!(await fsService.exists(fs, AUTH_SESSIONS_FILE_PATH))) {
        await persistAuthSessions(new Map());
      } else {
        const parsedAuthSessions = await getAuthSessionsFromFile();
        const migratedAuthSessions = new Map<string, AuthSession>();
        if (parsedAuthSessions instanceof Map || parsedAuthSessions instanceof Array) {
          for (const [key, authSession] of parsedAuthSessions) {
            const migratedAuthSession = await applyAuthSessionMigrations(authSession);
            migratedAuthSessions.set(key, migratedAuthSession);
          }
        }
        await persistAuthSessions(migratedAuthSessions);
      }
    }

    run();
  }, [persistAuthSessions, refresh, getAuthSessionsFromFile]);

  const recalculateAuthSessionStatus = useCallback(
    (args?: { canceled: Holder<boolean> }) => {
      async function run() {
        const newAuthSessionStatus: [string, AuthSessionStatus][] = await Promise.all(
          [...(authSessions?.values() ?? [])].map(async (authSession) => {
            if (authSession.type === "git") {
              const authProvider = authProviders.find(({ id }) => id === authSession.authProviderId);
              if (isGitAuthProvider(authProvider) && isSupportedGitAuthProviderType(authProvider.type)) {
                try {
                  const fetchUser = switchExpression(authProvider?.type, {
                    bitbucket: () =>
                      fetchAuthenticatedBitbucketUser(
                        env.KIE_SANDBOX_APP_NAME,
                        authSession.login,
                        authSession.token,
                        authProvider?.domain,
                        env.KIE_SANDBOX_CORS_PROXY_URL,
                        authProvider?.insecurelyDisableTlsCertificateValidation
                      ),
                    github: () =>
                      fetchAuthenticatedGitHubUser(
                        authSession.token,
                        authProvider?.domain,
                        env.KIE_SANDBOX_CORS_PROXY_URL,
                        authProvider?.insecurelyDisableTlsCertificateValidation
                      ),
                  });
                  await fetchUser();
                  return [authSession.id, AuthSessionStatus.VALID];
                } catch (e) {
                  return [authSession.id, AuthSessionStatus.INVALID];
                }
              } else {
                return [authSession.id, AuthSessionStatus.VALID];
              }
            } else if (authSession.type === "openshift") {
              try {
                if (
                  (await new KieSandboxOpenShiftService({
                    connection: authSession,
                    k8sApiServerEndpointsByResourceKind: authSession.k8sApiServerEndpointsByResourceKind,
                    proxyUrl: env.KIE_SANDBOX_CORS_PROXY_URL,
                  }).isConnectionEstablished()) === KubernetesConnectionStatus.CONNECTED
                ) {
                  return [authSession.id, AuthSessionStatus.VALID];
                } else {
                  return [authSession.id, AuthSessionStatus.INVALID];
                }
              } catch (e) {
                return [authSession.id, AuthSessionStatus.INVALID];
              }
            } else if (authSession.type === "kubernetes") {
              try {
                if (
                  (await new KieSandboxKubernetesService({
                    connection: authSession,
                    k8sApiServerEndpointsByResourceKind: authSession.k8sApiServerEndpointsByResourceKind,
                  }).isConnectionEstablished()) === KubernetesConnectionStatus.CONNECTED
                ) {
                  return [authSession.id, AuthSessionStatus.VALID];
                } else {
                  return [authSession.id, AuthSessionStatus.INVALID];
                }
              } catch (e) {
                return [authSession.id, AuthSessionStatus.INVALID];
              }
            } else {
              return [authSession.id, AuthSessionStatus.VALID];
            }
          })
        );

        if (args?.canceled.get()) {
          return;
        }

        setAuthSessionStatus(new Map(newAuthSessionStatus));
      }
      run();
    },
    [authProviders, authSessions, env.KIE_SANDBOX_APP_NAME, env.KIE_SANDBOX_CORS_PROXY_URL]
  );

  useCancelableEffect(recalculateAuthSessionStatus);

  const dispatch = useMemo(() => {
    return { add, remove, recalculateAuthSessionStatus };
  }, [add, remove, recalculateAuthSessionStatus]);

  const value = useMemo(() => {
    return authSessions && authSessionStatus ? { authSessions, authSessionStatus } : undefined;
  }, [authSessionStatus, authSessions]);

  return (
    <>
      {value && (
        <AuthSessionsContext.Provider value={value}>
          <AuthSessionsDispatchContext.Provider value={dispatch}>{props.children}</AuthSessionsDispatchContext.Provider>
        </AuthSessionsContext.Provider>
      )}
    </>
  );
}

export interface AuthInfo {
  username: string;
  uuid?: string;
  password: string;
}

export interface GitConfig {
  name: string;
  email: string;
}

export function useAuthSession(authSessionId: string | undefined): {
  authSession: AuthSession | undefined;
  authInfo: AuthInfo | undefined;
  gitConfig: GitConfig | undefined;
} {
  const { authSessions } = useAuthSessions();

  const authSession = useMemo(() => {
    if (!authSessionId) {
      return undefined;
    } else if (authSessionId == AUTH_SESSION_NONE.id) {
      return AUTH_SESSION_NONE;
    } else {
      return authSessions.get(authSessionId);
    }
  }, [authSessionId, authSessions]);

  const gitConfig = useMemo(() => {
    if (authSession?.type !== "git") {
      return undefined;
    }
    return (
      authSession && {
        name: authSession.name ?? "",
        email: authSession.email ?? "",
      }
    );
  }, [authSession]);

  const authInfo = useMemo(() => {
    if (authSession?.type !== "git") {
      return undefined;
    }
    return (
      authSession && {
        username: authSession.login,
        uuid: authSession.uuid,
        password: authSession.token,
      }
    );
  }, [authSession]);

  return { authSession, gitConfig, authInfo };
}
