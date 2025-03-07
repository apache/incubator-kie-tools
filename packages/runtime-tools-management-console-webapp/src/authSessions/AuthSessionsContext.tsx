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
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { LfsStorageFile } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import {
  AuthSession,
  AuthSessionStatus,
  authSessionFsCache,
  authSessionFsService,
  mapDeSerializer,
  AUTH_SESSIONS_FILE_PATH,
  AUTH_SESSIONS_FS_NAME_WITH_VERSION,
  mapSerializer,
  authSessionBroadcastChannel,
  isOpenIdConnectAuthSession,
  isUnauthenticatedAuthSession,
} from "./AuthSessionApi";
import { deleteOlderAuthSessionsStorage, migrateAuthSessions } from "./AuthSessionMigrations";
import { AuthSessionsService } from "./AuthSessionsService";

export type AuthSessionsContextType = {
  authSessions: Map<string, AuthSession>;
  isNewAuthSessionModalOpen: boolean;
  isAuthSessionsReady: boolean;
  currentAuthSession?: AuthSession;
  onSelectAuthSession?: (authSession: AuthSession) => void;
};

export type AuthSessionsDispatchContextType = {
  reauthAllAndUpdateStatus: () => void;
  add: (authSession: AuthSession) => Promise<void>;
  remove: (authSession: AuthSession) => void;
  setIsNewAuthSessionModalOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setCurrentAuthSession: React.Dispatch<React.SetStateAction<AuthSession | undefined>>;
  setOnSelectAuthSession: React.Dispatch<React.SetStateAction<((authSession: AuthSession) => void) | undefined>>;
};

const AuthSessionsContext = createContext<AuthSessionsContextType>({} as AuthSessionsContextType);
const AuthSessionsDispatchContext = createContext<AuthSessionsDispatchContextType>(
  {} as AuthSessionsDispatchContextType
);

export function useAuthSessions() {
  return useContext(AuthSessionsContext);
}

export function useAuthSessionsDispatch() {
  return useContext(AuthSessionsDispatchContext);
}

export function AuthSessionsContextProvider(props: PropsWithChildren<{}>) {
  const [authSessions, setAuthSessions] = useState<Map<string, AuthSession>>(new Map<string, AuthSession>());
  const [isNewAuthSessionModalOpen, setIsNewAuthSessionModalOpen] = useState(false);
  const [isAuthSessionsReady, setIsAuthSessionsReady] = useState<boolean>(false);
  const [currentAuthSession, setCurrentAuthSession] = useState<AuthSession>();
  const [onSelectAuthSession, setOnSelectAuthSession] = useState<(authSession: AuthSession) => void>();

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
      await refresh();
    },
    [refresh]
  );

  const add = useCallback(
    async (authSession: AuthSession) => {
      const n = new Map(authSessions?.entries() ?? []);
      n.forEach((existingAuthSession) => {
        if (isOpenIdConnectAuthSession(authSession)) {
          if (
            isOpenIdConnectAuthSession(existingAuthSession) &&
            existingAuthSession.runtimeUrl === authSession.runtimeUrl &&
            existingAuthSession.username === authSession.username
          ) {
            console.log("Authenticated AuthSession with same runtimeUrl and username found. Replacing it!");
            authSession.id = existingAuthSession.id;
          }
        } else {
          if (
            isUnauthenticatedAuthSession(existingAuthSession) &&
            existingAuthSession.runtimeUrl === authSession.runtimeUrl
          ) {
            console.log("Unauthenticated AuthSession with same runtimeUrl found. Replacing it!");
            authSession.id = existingAuthSession.id;
          }
        }
      });
      n?.set(authSession.id, authSession);
      await persistAuthSessions(n);
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
    authSessionBroadcastChannel.onmessage = refresh;
  }, [refresh]);

  const reauthSessionsAndCalculateStatus = useCallback(async (authSessions: Map<string, AuthSession>) => {
    const updatedSessions = await Promise.all(
      [...(authSessions?.values() ?? [])].map(async (authSession) => {
        try {
          if (isOpenIdConnectAuthSession(authSession)) {
            const newAuthSessionData = await AuthSessionsService.reauthenticate({
              authSession,
            });
            return {
              ...authSession,
              ...newAuthSessionData,
            };
          } else {
            return {
              ...authSession,
              status: AuthSessionStatus.VALID,
            };
          }
        } catch (e) {
          return { ...authSession, status: AuthSessionStatus.INVALID };
        }
      })
    );

    return new Map(updatedSessions.map((authSession) => [authSession.id, authSession]));
  }, []);

  // Init
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const run = async () => {
          const migratedAuthSessions = await migrateAuthSessions();
          if (canceled.get()) {
            return;
          }

          const updatedAuthSessions = await reauthSessionsAndCalculateStatus(migratedAuthSessions);

          if (canceled.get()) {
            return;
          }

          await persistAuthSessions(updatedAuthSessions);
          await deleteOlderAuthSessionsStorage();
        };
        run().then(() => {
          setIsAuthSessionsReady(true);
        });
      },
      [persistAuthSessions, reauthSessionsAndCalculateStatus]
    )
  );

  const reauthAllAndUpdateStatus = useCallback(async () => {
    const updatedAuthSessions = await reauthSessionsAndCalculateStatus(authSessions);
    await persistAuthSessions(updatedAuthSessions);
  }, [authSessions, persistAuthSessions, reauthSessionsAndCalculateStatus]);

  const dispatch = useMemo(() => {
    return {
      add,
      remove,
      reauthAllAndUpdateStatus,
      setIsNewAuthSessionModalOpen,
      setCurrentAuthSession,
      setOnSelectAuthSession,
    };
  }, [add, reauthAllAndUpdateStatus, remove, setOnSelectAuthSession]);

  const value = useMemo(() => {
    return {
      authSessions,
      isNewAuthSessionModalOpen,
      isAuthSessionsReady,
      currentAuthSession,
      onSelectAuthSession,
    };
  }, [authSessions, isNewAuthSessionModalOpen, isAuthSessionsReady, currentAuthSession, onSelectAuthSession]);

  return (
    <>
      {value && isAuthSessionsReady && (
        <AuthSessionsContext.Provider value={value}>
          <AuthSessionsDispatchContext.Provider value={dispatch}>{props.children}</AuthSessionsDispatchContext.Provider>
        </AuthSessionsContext.Provider>
      )}
    </>
  );
}

export function useAuthSession(authSessionId?: string) {
  const { authSessions } = useAuthSessions();

  const authSession = useMemo(() => {
    if (!authSessionId) {
      return undefined;
    } else {
      return authSessions.get(authSessionId);
    }
  }, [authSessionId, authSessions]);
  return { authSession };
}
