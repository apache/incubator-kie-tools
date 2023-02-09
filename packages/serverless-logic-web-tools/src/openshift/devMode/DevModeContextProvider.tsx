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

import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import JSZip from "jszip";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { AppDeploymentMode, useEnv } from "../../env/EnvContext";
import { fetchWithTimeout } from "../../fetch";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { OpenShiftInstanceStatus } from "../OpenShiftInstanceStatus";
import { SpinUpDevModePipeline } from "../pipelines/SpinUpDevModePipeline";
import {
  buildEndpoints,
  DevModeEndpoints,
  DevModeUploadResult,
  ZIP_FILE_NAME,
  ZIP_FILE_PART_KEY,
} from "./DevModeConstants";
import { DevModeContext, resolveWebToolsId } from "./DevModeContext";

interface Props {
  children: React.ReactNode;
}

export function DevModeContextProvider(props: Props) {
  const { env } = useEnv();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const [endpoints, setEndpoints] = useState<DevModeEndpoints | undefined>();

  useEffect(() => {
    if (
      settings.openshift.status !== OpenShiftInstanceStatus.CONNECTED ||
      env.FEATURE_FLAGS.MODE !== AppDeploymentMode.OPERATE_FIRST
    ) {
      setEndpoints(undefined);
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
            setEndpoints(buildEndpoints(routeUrl));
          }
        })
        .catch((e) => console.debug(e));
    } catch (e) {
      console.debug(e);
    }
  }, [
    settings.openshift.status,
    settings.openshift.config.namespace,
    settingsDispatch.openshift.service,
    env.FEATURE_FLAGS.MODE,
  ]);

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
    async (files: WorkspaceFile[]): Promise<DevModeUploadResult> => {
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

      try {
        const filesToZip = await Promise.all(
          files.map(async (file) => ({
            relativePath: file.relativePath,
            content: await file.getFileContentsAsString(),
          }))
        );

        const zip = new JSZip();
        for (const file of filesToZip) {
          zip.file(file.relativePath, file.content);
        }

        const zipBlob = await zip.generateAsync({ type: "blob" });

        const formData = new FormData();
        formData.append(ZIP_FILE_PART_KEY, zipBlob, ZIP_FILE_NAME);

        await fetch(endpoints.upload, { method: "POST", body: formData });

        return { success: true };
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

  const value = useMemo(() => ({ upload, endpoints, checkHealthReady }), [endpoints, upload, checkHealthReady]);

  return <DevModeContext.Provider value={value}>{props.children}</DevModeContext.Provider>;
}
