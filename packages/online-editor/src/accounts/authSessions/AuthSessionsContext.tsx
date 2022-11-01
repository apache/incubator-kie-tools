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

import * as React from "react";
import { createContext, PropsWithChildren, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { LfsFsCache } from "../../companionFs/LfsFsCache";
import { LfsStorageFile, LfsStorageService } from "../../companionFs/LfsStorageService";
import { getGithubInstanceApiUrl } from "../../github/Hooks";
import { useCancelableEffect } from "../../reactExt/Hooks";
import { decoder, encoder } from "../../workspace/encoderdecoder/EncoderDecoder";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { fetchAuthenticatedGitHubUser } from "../ConnectToGitHubSection";

export const AUTH_SESSION_NONE: AuthSession = {
  id: "none",
  name: "Unauthenticated",
  type: "none",
  login: "Unauthenticated",
};

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

export enum AuthSessionStatus {
  VALID,
  INVALID,
}

export type NoneAuthSession = {
  type: "none";
  name: "Unauthenticated";
  id: "none";
  login: "Unauthenticated";
};

export type AuthSession = GitAuthSession | NoneAuthSession;

export type AuthSessionsContextType = {
  authSessions: Map<string, AuthSession>;
  authSessionStatus: Map<string, AuthSessionStatus>;
};

export type AuthSessionsDispatchContextType = {
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

const AUTH_SESSIONS_FILE_PATH = "/authSessions.json";
const AUTH_SESSIONS_FS_NAME = "auth_sessions";

export function AuthSessionsContextProvider(props: PropsWithChildren<{}>) {
  const authProviders = useAuthProviders();
  const [authSessions, setAuthSessions] = useState<Map<string, AuthSession>>();
  const [authSessionStatus, setAuthSessionStatus] = useState<Map<string, AuthSessionStatus>>();

  const add = useCallback((authSession: AuthSession) => {
    setAuthSessions((prev) => new Map(prev?.entries() ?? []).set(authSession.id, authSession));
  }, []);

  const remove = useCallback((authSession: AuthSession) => {
    setAuthSessions((prev) => {
      prev?.delete(authSession.id);
      return new Map(prev?.entries() ?? []);
    });
  }, []);

  const persistAuthSessions = useCallback(async (map: Map<string, AuthSession>) => {
    const fs = fsCache.getOrCreateFs(AUTH_SESSIONS_FS_NAME);
    await fsService.createOrOverwriteFile(
      fs,
      new LfsStorageFile({
        path: AUTH_SESSIONS_FILE_PATH,
        getFileContents: async () => encoder.encode(JSON.stringify([...map.entries()])),
      })
    );
  }, []);

  useEffect(() => {
    async function run() {
      const fs = fsCache.getOrCreateFs(AUTH_SESSIONS_FS_NAME);
      if (!(await fsService.exists(fs, AUTH_SESSIONS_FILE_PATH))) {
        await persistAuthSessions(new Map());
      }

      const content = await (await fsService.getFile(fs, AUTH_SESSIONS_FILE_PATH))?.getFileContents();
      setAuthSessions(new Map(JSON.parse(decoder.decode(content))));

      // TODO: Tiago -> BROADCAST CHANNEL EVENT TO NOTIFY OTHER TABS PLEASE
    }

    run();
  }, [persistAuthSessions]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        async function run() {
          const newAuthSessionStatus: [string, AuthSessionStatus][] = await Promise.all(
            [...(authSessions?.values() ?? [])].map(async (authSession) => {
              if (authSession.type === "git") {
                const authProvider = authProviders.find(({ id }) => id === authSession.authProviderId);
                if (authProvider?.type === "github") {
                  try {
                    await fetchAuthenticatedGitHubUser(authSession.token, getGithubInstanceApiUrl(authProvider.domain));
                    return [authSession.id, AuthSessionStatus.VALID];
                  } catch (e) {
                    return [authSession.id, AuthSessionStatus.INVALID];
                  }
                } else {
                  return [authSession.id, AuthSessionStatus.VALID];
                }
              } else {
                return [authSession.id, AuthSessionStatus.VALID];
              }
            })
          );

          if (canceled.get()) {
            return;
          }

          setAuthSessionStatus(new Map(newAuthSessionStatus));
        }
        run();
      },
      [authProviders, authSessions]
    )
  );

  useEffect(() => {
    if (!authSessions) {
      return;
    }

    persistAuthSessions(authSessions ?? new Map());
  }, [persistAuthSessions, authSessions]);

  const dispatch = useMemo(() => {
    return { add, remove };
  }, [add, remove]);

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
        password: authSession.token,
      }
    );
  }, [authSession]);

  return { authSession, gitConfig, authInfo };
}
