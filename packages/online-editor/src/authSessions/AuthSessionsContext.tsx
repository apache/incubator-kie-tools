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
import { LfsStorageFile } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import {
  fetchAuthenticatedBitbucketUser,
  fetchAuthenticatedGitHubUser,
  fetchAuthenticatedGitlabUser,
} from "../accounts/git/ConnectToGitSection";
import {
  AuthSession,
  AuthSessionStatus,
  AUTH_SESSION_NONE,
  authSessionFsCache,
  authSessionFsService,
  mapDeSerializer,
  AUTH_SESSIONS_FILE_PATH,
  AUTH_SESSIONS_FS_NAME_WITH_VERSION,
  mapSerializer,
  authSessionBroadcastChannel,
  isCloudAuthSession,
} from "./AuthSessionApi";
import { KieSandboxOpenShiftService } from "../devDeployments/services/openshift/KieSandboxOpenShiftService";
import { isGitAuthProvider, isSupportedGitAuthProviderType } from "../authProviders/AuthProvidersApi";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { KubernetesConnectionStatus } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { useEnv } from "../env/hooks/EnvContext";
import { KieSandboxKubernetesService } from "../devDeployments/services/kubernetes/KieSandboxKubernetesService";
import { deleteOlderAuthSessionsStorage, migrateAuthSessions } from "./AuthSessionMigrations";

export type AuthSessionsContextType = {
  authSessions: Map<string, AuthSession>;
  authSessionStatus: Map<string, AuthSessionStatus>;
};

export type AuthSessionsDispatchContextType = {
  recalculateAuthSessionStatus: () => void;
  add: (authSession: AuthSession) => void;
  remove: (authSession: AuthSession) => void;
  update: (authSession: AuthSession) => void;
};

const AuthSessionsContext = createContext<AuthSessionsContextType>({} as any);
const AuthSessionsDispatchContext = createContext<AuthSessionsDispatchContextType>({} as any);

export function useAuthSessions() {
  return useContext(AuthSessionsContext);
}

export function useAuthSessionsDispatch() {
  return useContext(AuthSessionsDispatchContext);
}

export function AuthSessionsContextProvider(props: PropsWithChildren<{}>) {
  const authProviders = useAuthProviders();
  const { env } = useEnv();
  const [authSessions, setAuthSessions] = useState<Map<string, AuthSession>>(new Map<string, AuthSession>());
  const [authSessionStatus, setAuthSessionStatus] = useState<Map<string, AuthSessionStatus>>(
    new Map<string, AuthSessionStatus>()
  );

  const getAuthSessionsFromFile = useCallback(async () => {
    const fs = authSessionFsCache.getOrCreateFs(AUTH_SESSIONS_FS_NAME_WITH_VERSION);
    if (await authSessionFsService.exists(fs, AUTH_SESSIONS_FILE_PATH)) {
      const content = await (await authSessionFsService.getFile(fs, AUTH_SESSIONS_FILE_PATH))?.getFileContents();
      const parsedAuthSessions = JSON.parse(decoder.decode(content), mapDeSerializer);
      return parsedAuthSessions;
    }
    return [];
  }, []);

  const refresh = useCallback(async () => {
    setAuthSessions(await getAuthSessionsFromFile());
  }, [getAuthSessionsFromFile]);

  const persistAuthSessions = useCallback(
    async (map: Map<string, AuthSession>) => {
      const fs = authSessionFsCache.getOrCreateFs(AUTH_SESSIONS_FS_NAME_WITH_VERSION);
      await authSessionFsService.createOrOverwriteFile(
        fs,
        new LfsStorageFile({
          path: AUTH_SESSIONS_FILE_PATH,
          getFileContents: async () => encoder.encode(JSON.stringify(map, mapSerializer)),
        })
      );

      // This goes to other broadcast channel instances, on other tabs
      authSessionBroadcastChannel.postMessage("UPDATE_AUTH_SESSIONS");

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

  const update = useCallback(
    (authSession: AuthSession) => {
      const n = new Map(authSessions?.entries() ?? []);
      if (!n.has(authSession.id)) {
        return;
      }
      n.set(authSession.id, authSession);
      persistAuthSessions(n);
    },
    [authSessions, persistAuthSessions]
  );

  // Update after persisted
  useEffect(() => {
    authSessionBroadcastChannel.onmessage = refresh;
  }, [refresh]);

  // Init
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const run = async () => {
          const migratedAuthSessions = await migrateAuthSessions();
          if (canceled.get()) {
            return;
          }
          await persistAuthSessions(migratedAuthSessions);
          await deleteOlderAuthSessionsStorage();
        };
        run();
      },
      [persistAuthSessions]
    )
  );

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
                        authProvider?.insecurelyDisableTlsCertificateValidation,
                        authProvider?.disableEncoding
                      ),
                    github: () =>
                      fetchAuthenticatedGitHubUser(
                        authSession.token,
                        authProvider?.domain,
                        env.KIE_SANDBOX_CORS_PROXY_URL,
                        authProvider?.insecurelyDisableTlsCertificateValidation,
                        authProvider?.disableEncoding
                      ),
                    gitlab: () =>
                      fetchAuthenticatedGitlabUser(
                        env.KIE_SANDBOX_APP_NAME,
                        authSession.token,
                        authProvider?.domain,
                        env.KIE_SANDBOX_CORS_PROXY_URL,
                        authProvider?.insecurelyDisableTlsCertificateValidation,
                        authProvider?.disableEncoding
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
    return { add, remove, recalculateAuthSessionStatus, update };
  }, [add, remove, recalculateAuthSessionStatus, update]);

  const value = useMemo(() => {
    return { authSessions, authSessionStatus };
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

export function useSyncCloudAuthSession(
  selectedAuthSession: AuthSession | undefined,
  setConnection: (connection: {
    host: string;
    namespace: string;
    token: string;
    insecurelyDisableTlsCertificateValidation: boolean;
  }) => void
) {
  useEffect(() => {
    if (selectedAuthSession && isCloudAuthSession(selectedAuthSession)) {
      setConnection({
        host: selectedAuthSession.host,
        namespace: selectedAuthSession.namespace,
        token: "",
        insecurelyDisableTlsCertificateValidation: selectedAuthSession.insecurelyDisableTlsCertificateValidation,
      });
    }
  }, [selectedAuthSession, setConnection]);
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
