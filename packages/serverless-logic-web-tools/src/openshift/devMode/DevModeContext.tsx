/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { v4 as uuid } from "uuid";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import * as React from "react";
import { useContext, useState, useCallback, useMemo } from "react";
import {
  buildEndpoints,
  DevModeEndpoints,
  DevModeUploadResult,
  WEB_TOOLS_ID_KEY,
  ZIP_FILE_NAME,
  ZIP_FILE_PART_KEY,
} from "./DevModeConstants";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { OpenShiftInstanceStatus } from "../OpenShiftInstanceStatus";
import { SpinUpDevModePipeline } from "../pipelines/SpinUpDevModePipeline";
import { fetchWithTimeout } from "../../fetch";
import { zipFiles } from "../../zip";
import { isApplicationProperties, isSupportingFileForDevMode } from "../../extension";
import { RestartDevModePipeline } from "../pipelines/RestartDevModePipeline";
import { isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";

export interface UploadApiResponseError {
  error: string;
}

export interface UploadApiResponseSuccess {
  paths: string[];
}

export const resolveWebToolsId = () => {
  const webToolsId = localStorage.getItem(WEB_TOOLS_ID_KEY) ?? uuid();
  localStorage.setItem(WEB_TOOLS_ID_KEY, webToolsId);
  return webToolsId;
};

export const resolveDevModeResourceName = (webToolsId: string) => {
  const sanitizedVersion = process.env.WEBPACK_REPLACE__version!.replace(/\./g, "-");
  return `devmode-${webToolsId}-${sanitizedVersion}`;
};

export interface DevModeContextType {
  isEnabled: boolean;
  endpoints: DevModeEndpoints | undefined;
}

export interface DevModeDispatchContextType {
  upload(args: { targetSwfFile: WorkspaceFile; allFiles: WorkspaceFile[] }): Promise<DevModeUploadResult>;
  checkHealthReady(): Promise<boolean>;
  restart(): Promise<void>;
}

export const DevModeContext = React.createContext<DevModeContextType>({} as any);
export const DevModeDispatchContext = React.createContext<DevModeDispatchContextType>({} as any);

export function useDevMode() {
  return useContext(DevModeContext);
}

export function useDevModeDispatch() {
  return useContext(DevModeDispatchContext);
}

export function DevModeContextProvider(props: React.PropsWithChildren<{}>) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [isEnabled, setEnabled] = useState(false);
  const [endpoints, setEndpoints] = useState<DevModeEndpoints | undefined>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        setEnabled(false);
        setEndpoints(undefined);

        if (settings.openshift.status !== OpenShiftInstanceStatus.CONNECTED || !settings.openshift.isDevModeEnabled) {
          return;
        }

        try {
          const spinUpDevModePipeline = new SpinUpDevModePipeline({
            webToolsId: resolveWebToolsId(),
            namespace: settings.openshift.config.namespace,
            openShiftService: settingsDispatch.openshift.service,
          });

          spinUpDevModePipeline
            .execute()
            .then((routeUrl) => {
              if (canceled.get()) {
                return;
              }
              if (routeUrl) {
                setEnabled(true);
                setEndpoints(buildEndpoints(routeUrl));
              }
            })
            .catch((e) => console.error(e));
        } catch (e) {
          console.error(e);
        }
      },
      [
        settings.openshift.config.namespace,
        settings.openshift.isDevModeEnabled,
        settings.openshift.status,
        settingsDispatch.openshift.service,
      ]
    )
  );

  const checkHealthReady = useCallback(async () => {
    if (!endpoints) {
      return false;
    }

    try {
      const readyResponse = await fetchWithTimeout(endpoints.health.ready, { timeout: 2000 });
      return readyResponse.ok;
    } catch (e) {
      console.debug(e);
    }
    return false;
  }, [endpoints]);

  const upload = useCallback(
    async (args: { targetSwfFile: WorkspaceFile; allFiles: WorkspaceFile[] }): Promise<DevModeUploadResult> => {
      if (!endpoints) {
        return {
          success: false,
          message: "Route URL for Dev Mode deployment not available.",
        };
      }

      if (!(await checkHealthReady())) {
        return {
          success: false,
          message: "Dev Mode deployment not ready yet.",
        };
      }

      if (!isOfKind("sw", args.targetSwfFile.relativePath)) {
        return {
          success: false,
          message: `File is not Serverless Workflow: ${args.targetSwfFile.relativePath}`,
        };
      }

      try {
        const filesToUpload = [args.targetSwfFile];

        const applicationPropertiesFile = args.allFiles.find((f) => isApplicationProperties(f.relativePath));
        if (applicationPropertiesFile) {
          filesToUpload.push(applicationPropertiesFile);
        }

        const supportingFiles = args.allFiles.filter((f) =>
          isSupportingFileForDevMode({ path: f.relativePath, targetFolder: args.targetSwfFile.relativeDirPath })
        );
        if (supportingFiles.length > 0) {
          filesToUpload.push(...supportingFiles);
        }

        const zipBlob = await zipFiles(filesToUpload);

        const formData = new FormData();
        formData.append(ZIP_FILE_PART_KEY, zipBlob, ZIP_FILE_NAME);

        return (await fetch(endpoints.upload, { method: "POST", body: formData })
          .then(async (response) => {
            if ([400, 500].includes(response.status)) {
              const json = (await response.json()) as UploadApiResponseError;
              return {
                success: false,
                message: json.error,
              };
            }

            const json = (await response.json()) as UploadApiResponseSuccess;
            return {
              success: true,
              uploadedPaths: json.paths,
            };
          })
          .catch((error) => ({
            success: false,
            message: error,
          }))) as DevModeUploadResult;
      } catch (e) {
        return {
          success: false,
          message: e,
        };
      }
    },
    [checkHealthReady, endpoints]
  );

  const restart = useCallback(async () => {
    const restartDevModePipeline = new RestartDevModePipeline({
      webToolsId: resolveWebToolsId(),
      namespace: settings.openshift.config.namespace,
      openShiftService: settingsDispatch.openshift.service,
    });
    restartDevModePipeline.execute().catch((e) => console.error(e));
  }, [settings.openshift.config.namespace, settingsDispatch.openshift.service]);

  const value = useMemo(() => ({ isEnabled, endpoints }), [isEnabled, endpoints]);
  const dispatch = useMemo(() => ({ upload, checkHealthReady, restart }), [upload, checkHealthReady, restart]);

  return (
    <DevModeContext.Provider value={value}>
      <DevModeDispatchContext.Provider value={dispatch}>{props.children}</DevModeDispatchContext.Provider>
    </DevModeContext.Provider>
  );
}
