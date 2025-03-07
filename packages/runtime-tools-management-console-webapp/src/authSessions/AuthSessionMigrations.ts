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

import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import {
  AUTH_SESSION_OIDC_DEFAULT_SCOPES,
  AUTH_SESSIONS_FILE_PATH,
  AUTH_SESSIONS_FS_NAME,
  AUTH_SESSIONS_VERSION_NUMBER,
  AuthSession,
  authSessionFsCache,
  authSessionFsService,
  mapDeSerializer,
} from "./AuthSessionApi";

export async function getAuthSessionsFromVersion(version?: number) {
  const authSessionFsName = version && version >= 0 ? `${AUTH_SESSIONS_FS_NAME}_v${version}` : AUTH_SESSIONS_FS_NAME;
  const fs = authSessionFsCache.getOrCreateFs(authSessionFsName);
  const authSessionsFile = await authSessionFsService.getFile(fs, AUTH_SESSIONS_FILE_PATH);
  if (!authSessionsFile) {
    return [];
  }
  const content = await authSessionsFile.getFileContents();
  const parsedAuthSessions = Array.from(JSON.parse(decoder.decode(content), mapDeSerializer));
  return parsedAuthSessions;
}

export async function getAllAuthSessions() {
  let allAuthSessions: any[] = [];
  for (let i = AUTH_SESSIONS_VERSION_NUMBER; i >= 0; i--) {
    const authSessions = await getAuthSessionsFromVersion(i);
    allAuthSessions = allAuthSessions.concat(authSessions);
  }
  return allAuthSessions;
}

export async function deleteOlderAuthSessionsStorage() {
  for (let i = AUTH_SESSIONS_VERSION_NUMBER - 1; i >= 0; i--) {
    const authSessionFsName = i && i >= 0 ? `${AUTH_SESSIONS_FS_NAME}_v${i}` : AUTH_SESSIONS_FS_NAME;
    const fs = authSessionFsCache.getOrCreateFs(authSessionFsName);
    if (await authSessionFsService.exists(fs, AUTH_SESSIONS_FILE_PATH)) {
      await authSessionFsService.deleteFile(fs, AUTH_SESSIONS_FILE_PATH);
    }
    await fs.deactivate();
    indexedDB.deleteDatabase(authSessionFsName);
  }
}

export async function migrateAuthSessions() {
  const olderAuthSessions = await getAllAuthSessions();
  const migratedAuthSessions = new Map<string, AuthSession>();
  for (const [key, authSession] of olderAuthSessions) {
    try {
      const migratedAuthSession = await applyAuthSessionMigrations(authSession);
      migratedAuthSessions.set(key, migratedAuthSession);
    } catch (e) {
      console.error("Failed to apply migrations to auth session", {
        id: authSession.id,
        authProvider: authSession.authProviderId,
        host: authSession.host,
      });
    }
  }
  return migratedAuthSessions;
}

export async function applyAuthSessionMigrations(authSession: any): Promise<AuthSession> {
  if (authSession.version && authSession.version > AUTH_SESSIONS_VERSION_NUMBER) {
    throw new Error(
      `Failed to apply migration script to AuthSession: ${authSession.id}. Version is greater than current version.`
    );
  }
  const newAuthSession = {
    ...authSession,
  };

  switch (authSession.version) {
    case undefined:
    case 1:
      newAuthSession.version = 2;
      newAuthSession.scope = AUTH_SESSION_OIDC_DEFAULT_SCOPES;
    case 2:
    // Already at current version. Nothing to do.
    default:
      break;
  }

  return newAuthSession;
}
