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

import * as React from "react";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { LfsFsCache } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsFsCache";
import { LfsStorageFile, LfsStorageService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import { createContext, PropsWithChildren, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useEnv } from "../env/hooks/EnvContext";
import { QueryParams } from "../navigation/Routes";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { SettingsTabs } from "./SettingsModalBody";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";

export type SettingsContextType = {
  settings: {
    version: number;
    corsProxy: {
      url: string;
    };
    extendedServices: {
      host: string;
      port: string;
    };
    editors: {
      useLegacyDmnEditor: boolean;
    };
  };
  isModalOpen: boolean;
  tab: SettingsTabs;
};

export type SettingsDispatchContextType = {
  set(patch: (settings: SettingsContextType["settings"]) => void): void;
  open: (activeTab?: SettingsTabs) => void;
  close: () => void;
};

const SettingsContext = createContext<SettingsContextType>({} as any);
const SettingsDispatchContext = createContext<SettingsDispatchContextType>({} as any);

export function useSettings() {
  return useContext(SettingsContext);
}

export function useSettingsDispatch() {
  return useContext(SettingsDispatchContext);
}

const fsCache = new LfsFsCache();
const fsService = new LfsStorageService();
const broadcastChannel = new BroadcastChannel("settings");

const SETTINGS_FILE_PATH = "/settings.json";
const SETTINGS_FS_NAME = "settings";

const SETTINGS_FILE_LATEST_VERSION = 0;

export function SettingsContextProvider(props: PropsWithChildren<{}>) {
  const { env } = useEnv();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();

  const [settings, setSettings] = useState<SettingsContextType["settings"]>();

  const defaultSettingsTab = useMemo(() => {
    return editorEnvelopeLocator.hasMappingFor("*.dmn")
      ? SettingsTabs.EDITORS
      : SettingsTabs.KIE_SANDBOX_EXTENDED_SERVICES;
  }, [editorEnvelopeLocator]);

  const refresh = useCallback(async (args?: { canceled: Holder<boolean> }) => {
    const fs = fsCache.getOrCreateFs(SETTINGS_FS_NAME);
    const content = await (await fsService.getFile(fs, SETTINGS_FILE_PATH))?.getFileContents();
    if (args?.canceled.get()) {
      return;
    }

    setSettings(JSON.parse(decoder.decode(content)));
  }, []);

  const persistSettings = useCallback(
    async (settings: SettingsContextType["settings"]) => {
      const fs = fsCache.getOrCreateFs(SETTINGS_FS_NAME);
      await fsService.createOrOverwriteFile(
        fs,
        new LfsStorageFile({
          path: SETTINGS_FILE_PATH,
          getFileContents: async () => encoder.encode(JSON.stringify(settings)),
        })
      );

      // This goes to other broadcast channel instances, on other tabs
      broadcastChannel.postMessage("UPDATE_SETTINGS");

      // This updates this tab
      refresh();
    },
    [refresh]
  );

  // Update after persisted
  useCancelableEffect(
    useCallback(
      (canceled) => {
        broadcastChannel.onmessage = () => refresh(canceled);
      },
      [refresh]
    )
  );

  // Init
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        async function run() {
          const fs = fsCache.getOrCreateFs(SETTINGS_FS_NAME);
          if (!(await fsService.exists(fs, SETTINGS_FILE_PATH))) {
            if (canceled.get()) {
              return;
            }

            const envExtendedServicesUrl = new URL(env.KIE_SANDBOX_EXTENDED_SERVICES_URL);
            // 0.0.0.0 is "equivalent" to localhost, but browsers don't like having mixed http/https urls with the exception of localhost
            const envExtendedServicesHost = `${envExtendedServicesUrl.protocol}//${
              envExtendedServicesUrl.hostname === "0.0.0.0" ? "localhost" : envExtendedServicesUrl.hostname
            }`;
            const envExtendedServicesPort = envExtendedServicesUrl.port;

            await persistSettings({
              version: SETTINGS_FILE_LATEST_VERSION,
              corsProxy: {
                url: env.KIE_SANDBOX_CORS_PROXY_URL,
              },
              extendedServices: {
                host: envExtendedServicesHost,
                port: envExtendedServicesPort,
              },
              editors: {
                useLegacyDmnEditor: false,
              },
            });
          } else {
            refresh();
          }
        }

        run();
      },
      [env.KIE_SANDBOX_CORS_PROXY_URL, env.KIE_SANDBOX_EXTENDED_SERVICES_URL, persistSettings, refresh]
    )
  );

  const queryParams = useQueryParams();
  const navigate = useNavigate();
  const [tab, setTab] = useState(defaultSettingsTab);
  const [isModalOpen, setModalOpen] = useState(false);

  useEffect(() => {
    setModalOpen(queryParams.has(QueryParams.SETTINGS));
    setTab((queryParams.get(QueryParams.SETTINGS) as SettingsTabs) ?? defaultSettingsTab);
  }, [defaultSettingsTab, queryParams]);

  const dispatch = useMemo<SettingsDispatchContextType>(() => {
    return {
      open: (tab) => {
        navigate(
          {
            search: queryParams.with(QueryParams.SETTINGS, tab ?? defaultSettingsTab).toString(),
          },
          { replace: true }
        );
      },
      close: () => {
        navigate(
          {
            search: queryParams.without(QueryParams.SETTINGS).toString(),
          },
          { replace: true }
        );
      },
      set: (patch) => {
        setSettings((s) => {
          const copy = JSON.parse(JSON.stringify(s));
          patch(copy);
          persistSettings(copy);
          return s;
        });
      },
    };
  }, [defaultSettingsTab, navigate, persistSettings, queryParams]);

  const value = useMemo(() => {
    if (!settings) {
      return undefined;
    }

    return {
      settings,
      isModalOpen,
      tab,
    };
  }, [isModalOpen, settings, tab]);

  return (
    <>
      {value && (
        <SettingsContext.Provider value={value}>
          <SettingsDispatchContext.Provider value={dispatch}>{props.children}</SettingsDispatchContext.Provider>
        </SettingsContext.Provider>
      )}
    </>
  );
}
