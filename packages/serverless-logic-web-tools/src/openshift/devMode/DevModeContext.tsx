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
import { useContext, useState, useEffect, useCallback, useMemo } from "react";
import {
  buildEndpoints,
  DevModeEndpoints,
  DevModeUploadResult,
  WEB_TOOLS_ID_KEY,
  ZIP_FILE_NAME,
  ZIP_FILE_PART_KEY,
} from "./DevModeConstants";
import { useEnv } from "../../env/EnvContext";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { OpenShiftInstanceStatus } from "../OpenShiftInstanceStatus";
import { SpinUpDevModePipeline } from "../pipelines/SpinUpDevModePipeline";
import { fetchWithTimeout } from "../../fetch";
import { zipFiles } from "../../zip";
import { isApplicationProperties, isSupportingFileForDevMode } from "../../extension";
import { RestartDevModePipeline } from "../pipelines/RestartDevModePipeline";
import { isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";

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
  const { env } = useEnv();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [isEnabled, setEnabled] = useState(false);
  const [endpoints, setEndpoints] = useState<DevModeEndpoints | undefined>();

  useEffect(() => {
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
          if (routeUrl) {
            setEnabled(true);
            setEndpoints(buildEndpoints(routeUrl));
          }
        })
        .catch((e) => console.debug(e));
    } catch (e) {
      console.debug(e);
    }
  }, [settings.openshift, env.FEATURE_FLAGS.MODE, settingsDispatch.openshift.service]);

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
        console.error("Route URL for Dev Mode deployment not available.");
        return {
          success: false,
          reason: "NOT_READY",
        };
      }

      if (!(await checkHealthReady())) {
        console.error("Dev Mode deployment is not ready");
        return {
          success: false,
          reason: "NOT_READY",
        };
      }

      if (!isOfKind("sw", args.targetSwfFile.relativePath)) {
        console.error(`File is not Serverless Workflow: ${args.targetSwfFile.relativePath}`);
        return {
          success: false,
          reason: "ERROR",
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

        await fetch(endpoints.upload, { method: "POST", body: formData });

        return { success: true, uploadedPaths: filesToUpload.map((f) => f.relativePath) };
      } catch (e) {
        console.debug(e);
      }
      return {
        success: false,
        reason: "ERROR",
      };
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
