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
import * as React from "react";
import { useContext, useState, useCallback, useMemo } from "react";
import {
  buildEndpoints,
  DevModeEndpoints,
  DevModeUploadResult,
  resolveDevModeId,
  UploadApiResponseError,
  UploadApiResponseSuccess,
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
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { useGlobalAlert } from "../../alerts/GlobalAlertsContext";
import { WebToolsOpenShiftDeployedModel } from "../deploy/types";
import { DevModeDeploymentLoaderPipeline } from "../pipelines/DevModeDeploymentLoaderPipeline";
import { useEnv } from "../../env/EnvContext";

export interface DevModeContextType {
  isEnabled: boolean;
  endpoints: DevModeEndpoints | undefined;
}

export interface DevModeDispatchContextType {
  upload(args: { targetSwfFile: WorkspaceFile; allFiles: WorkspaceFile[] }): Promise<DevModeUploadResult>;
  checkHealthReady(): Promise<boolean>;
  restart(): Promise<void>;
  loadDeployments(): Promise<WebToolsOpenShiftDeployedModel[]>;
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

  const devModeDeploymentLoaderPipeline = useMemo(() => {
    if (!isEnabled) {
      return;
    }
    return new DevModeDeploymentLoaderPipeline({
      devModeId: resolveDevModeId(),
      version: env.SERVERLESS_LOGIC_WEB_TOOLS_VERSION,
      namespace: settings.openshift.config.namespace,
      openShiftService: settingsDispatch.openshift.service,
    });
  }, [env, isEnabled, settings.openshift.config.namespace, settingsDispatch.openshift.service]);

  const devModeCreatedSuccessAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="info"
          title={"Your Dev Mode deployment has been created and will be available shortly"}
          aria-live="polite"
          data-testid="alert-dev-mode-created"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const spinUpDevModeErrorAlert = useGlobalAlert<{ message: string }>(
    useCallback(({ close }, { message }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="warning"
          title={
            <>
              Something went wrong while spinning up the Dev Mode.
              <br />
              {`Reason: ${message}`}
            </>
          }
          aria-live="polite"
          data-testid="alert-upload-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        setEnabled(false);
        setEndpoints(undefined);

        if (settings.openshift.status !== OpenShiftInstanceStatus.CONNECTED || !settings.openshift.isDevModeEnabled) {
          return;
        }

        const spinUpDevModePipeline = new SpinUpDevModePipeline({
          devModeId: resolveDevModeId(),
          version: env.SERVERLESS_LOGIC_WEB_TOOLS_VERSION,
          namespace: settings.openshift.config.namespace,
          openShiftService: settingsDispatch.openshift.service,
        });

        spinUpDevModePipeline
          .execute()
          .then((response) => {
            if (canceled.get()) {
              return;
            }
            if (response.isCompleted) {
              setEnabled(true);
              setEndpoints(buildEndpoints(response.routeUrl));
              if (response.isNew) {
                devModeCreatedSuccessAlert.show();
              }
            } else {
              spinUpDevModeErrorAlert.show({ message: response.reason });
            }
          })
          .catch((e) => {
            spinUpDevModeErrorAlert.show({ message: e });
          });
      },
      // Adding alerts to the dependency array causes an infinite loop
      // eslint-disable-next-line react-hooks/exhaustive-deps
      [
        env,
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
          messages: ["Route URL for Dev Mode deployment not available"],
        };
      }

      if (!(await checkHealthReady())) {
        return {
          success: false,
          messages: ["Dev Mode deployment not ready yet"],
        };
      }

      if (!isOfKind("sw", args.targetSwfFile.relativePath)) {
        return {
          success: false,
          messages: [`File is not Serverless Workflow: ${args.targetSwfFile.relativePath}`],
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
            if (response.ok) {
              const json = (await response.json()) as UploadApiResponseSuccess;
              return {
                success: true,
                uploadedPaths: json.paths,
              };
            }

            if (response.status >= 400 && response.status <= 599) {
              const json = (await response.json()) as UploadApiResponseError;
              if (json.errors) {
                return {
                  success: false,
                  messages: json.errors,
                };
              }
            }

            return {
              success: false,
              messages: ["Unexpected error, please check your OpenShift instance"],
            };
          })
          .catch((error) => ({
            success: false,
            messages: [error],
          }))) as DevModeUploadResult;
      } catch (error) {
        return {
          success: false,
          messages: [error],
        };
      }
    },
    [checkHealthReady, endpoints]
  );

  const restart = useCallback(async () => {
    const restartDevModePipeline = new RestartDevModePipeline({
      devModeId: resolveDevModeId(),
      version: env.SERVERLESS_LOGIC_WEB_TOOLS_VERSION,
      namespace: settings.openshift.config.namespace,
      openShiftService: settingsDispatch.openshift.service,
    });
    restartDevModePipeline.execute().catch((e) => console.error(e));
  }, [env, settings.openshift.config.namespace, settingsDispatch.openshift.service]);

  const loadDeployments = useCallback(
    async () => devModeDeploymentLoaderPipeline?.execute() ?? [],
    [devModeDeploymentLoaderPipeline]
  );

  const value = useMemo(() => ({ isEnabled, endpoints }), [isEnabled, endpoints]);
  const dispatch = useMemo(
    () => ({ upload, checkHealthReady, restart, loadDeployments }),
    [upload, checkHealthReady, restart, loadDeployments]
  );

  return (
    <DevModeContext.Provider value={value}>
      <DevModeDispatchContext.Provider value={dispatch}>{props.children}</DevModeDispatchContext.Provider>
    </DevModeContext.Provider>
  );
}
