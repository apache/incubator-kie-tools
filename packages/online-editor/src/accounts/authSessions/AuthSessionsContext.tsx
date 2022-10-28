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

import { type } from "os";
import * as React from "react";
import { createContext, PropsWithChildren, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { LfsFsCache } from "../../companionFs/LfsFsCache";
import { LfsStorageFile, LfsStorageService } from "../../companionFs/LfsStorageService";
import { decoder, encoder } from "../../workspace/encoderdecoder/EncoderDecoder";

export const AUTH_SESSION_NONE: AuthSession = {
  id: "none",
  name: "Anonymous",
  type: "none",
  login: "Anonymous",
};

export type GitAuthSession = {
  type: "git";
  id: string;
  token: string;
  login: string;
  email?: string;
  name?: string;
  authProviderId: string;
};

export type NoneAuthSession = {
  type: "none";
  name: "Anonymous";
  id: "none";
  login: "Anonymous";
};

export type AuthSession = GitAuthSession | NoneAuthSession;

export type AuthSessionsContextType = {
  authSessions: Map<string, AuthSession>;
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
  const [authSessions, setAuthSessions] = useState<Map<string, AuthSession>>();

  const add = useCallback((authSession: AuthSession) => {
    setAuthSessions((prev) => new Map(prev?.entries() ?? []).set(authSession.id, authSession));
  }, []);

  const remove = useCallback((authSession: AuthSession) => {
    setAuthSessions((prev) => {
      prev?.delete(authSession.id);
      return new Map(prev?.entries() ?? []);
    });
  }, []);

  const persistAuthSessions = useCallback((map: Map<string, AuthSession>) => {
    const fs = fsCache.getOrCreateFs(AUTH_SESSIONS_FS_NAME);
    fsService.createOrOverwriteFile(
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
        persistAuthSessions(new Map());
      }

      const content = await (await fsService.getFile(fs, AUTH_SESSIONS_FILE_PATH))?.getFileContents();
      setAuthSessions(new Map(JSON.parse(decoder.decode(content))));

      // TODO: Tiago -> BROADCAST CHANNEL EVENT TO NOTIFY OTHER TABS PLEASE
    }

    run();
  }, [persistAuthSessions]);

  useEffect(() => {
    if (!authSessions) {
      return;
    }

    persistAuthSessions(authSessions ?? new Map());
  }, [persistAuthSessions, authSessions]);

  return (
    <>
      {authSessions && (
        <AuthSessionsContext.Provider value={{ authSessions }}>
          <AuthSessionsDispatchContext.Provider value={{ add, remove }}>
            {props.children}
          </AuthSessionsDispatchContext.Provider>
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
