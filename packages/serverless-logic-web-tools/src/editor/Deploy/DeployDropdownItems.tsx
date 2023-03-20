/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { RegistryIcon, ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons";
import { OpenshiftIcon } from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import { UploadIcon } from "@patternfly/react-icons/dist/js/icons/upload-icon";
import * as React from "react";
import { useCallback, useMemo, useState, useEffect } from "react";
import { useAppI18n } from "../../i18n";
import { FeatureDependentOnKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/FeatureDependentOnKieSandboxExtendedServices";
import {
  DependentFeature,
  useKieSandboxExtendedServices,
} from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { SettingsTabs } from "../../settings/SettingsModalBody";
import { useVirtualServiceRegistryDependencies } from "../../virtualServiceRegistry/hooks/useVirtualServiceRegistryDependencies";
import { FileLabel } from "../../workspace/components/FileLabel";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useDevMode, useDevModeDispatch } from "../../openshift/devMode/DevModeContext";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { isServerlessWorkflow } from "../../extension";
import { useEnv } from "../../env/EnvContext";
import { useGlobalAlert } from "../../alerts/GlobalAlertsContext";
import { useEditor } from "../hooks/EditorContext";
import { AppDistributionMode } from "../../AppConstants";

const FETCH_DEV_MODE_DEPLOYMENT_POLLING_TIME = 2000;

interface Props {
  workspace: ActiveWorkspace;
  workspaceFile: WorkspaceFile;
}

export function useDeployDropdownItems(props: Props) {
  const { env } = useEnv();
  const { notifications } = useEditor();
  const { i18n } = useAppI18n();
  const devMode = useDevMode();
  const devModeDispatch = useDevModeDispatch();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const openshift = useOpenShift();
  const [canContentBeDeployed, setCanContentBeDeployed] = useState(true);
  const { needsDependencyDeployment } = useVirtualServiceRegistryDependencies({
    workspace: props.workspace,
  });

  useEffect(() => {
    props.workspaceFile.getFileContentsAsString().then((content) => {
      setCanContentBeDeployed(
        content.trim().length > 0 && !notifications.some((d) => ["ERROR", "WARNING"].includes(d.severity))
      );
    });
  }, [notifications, props.workspaceFile]);

  const devModeUploadingAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        return (
          <Alert
            className="pf-u-mb-md"
            variant="info"
            title={
              <>
                <Spinner size={"sm"} />
                &nbsp;&nbsp; {`Uploading '${props.workspaceFile.nameWithoutExtension}'...`}
              </>
            }
            aria-live="polite"
            data-testid="alert-dev-mode-uploading"
            actionClose={<AlertActionCloseButton onClose={close} />}
          />
        );
      },
      [props.workspaceFile.nameWithoutExtension]
    )
  );

  const devModeReadyAlert = useGlobalAlert<{ routeUrl: string }>(
    useCallback(({ close }, { routeUrl }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="success"
          title={`Your Dev Mode has been updated.`}
          aria-live="polite"
          data-testid="alert-dev-mode-ready"
          actionClose={<AlertActionCloseButton onClose={close} />}
          actionLinks={
            <AlertActionLink onClick={() => window.open(routeUrl, "_blank")}>Dev Mode Swagger UI</AlertActionLink>
          }
        />
      );
    }, [])
  );

  const uploadToDevModeSuccessAlert = useGlobalAlert(
    useCallback(
      ({ close }) => {
        return (
          <Alert
            className="pf-u-mb-md"
            variant="info"
            title={
              <>
                <Spinner size={"sm"} />
                &nbsp;&nbsp; {`Updating Dev Mode deployment with '${props.workspaceFile.nameWithoutExtension}'...`}
              </>
            }
            aria-live="polite"
            data-testid="alert-dev-mode-updating"
            actionClose={<AlertActionCloseButton onClose={close} />}
          />
        );
      },
      [props.workspaceFile.nameWithoutExtension]
    )
  );

  const uploadToDevModeErrorAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="danger"
          title={"Something went wrong while uploading to the Dev Mode deployment. Please try again in a few moments."}
          aria-live="polite"
          data-testid="alert-upload-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const uploadToDevModeNotReadyAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="warning"
          title={"Looks like the Dev Mode deployment is not ready yet. Please try again in a few moments."}
          aria-live="polite"
          data-testid="alert-dev-mode-not-ready"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const isKieSandboxExtendedServicesRunning = useMemo(
    () => kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING,
    [kieSandboxExtendedServices.status]
  );

  const isOpenShiftConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  const isDevModeEnabled = useMemo(
    () =>
      env.FEATURE_FLAGS.MODE === AppDistributionMode.OPERATE_FIRST && isServerlessWorkflow(props.workspaceFile.name),
    [env.FEATURE_FLAGS.MODE, props.workspaceFile.name]
  );

  const onSetup = useCallback(() => {
    settingsDispatch.open(SettingsTabs.OPENSHIFT);
  }, [settingsDispatch]);

  const onDeploy = useCallback(() => {
    if (isKieSandboxExtendedServicesRunning) {
      openshift.setConfirmDeployModalOpen(true);
      return;
    }
    kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.OPENSHIFT);
    kieSandboxExtendedServices.setModalOpen(true);
  }, [isKieSandboxExtendedServicesRunning, kieSandboxExtendedServices, openshift]);

  const onUploadDevMode = useCallback(async () => {
    if (isKieSandboxExtendedServicesRunning) {
      devModeUploadingAlert.show();
      // TODO CAPONETTO: only the current file is uploaded for now
      const result = await devModeDispatch.upload([props.workspaceFile]);
      devModeUploadingAlert.close();

      if (result.success) {
        uploadToDevModeSuccessAlert.show();

        const fetchDevModeDeploymentTask = window.setInterval(async () => {
          const isReady = await devModeDispatch.checkHealthReady();
          if (!isReady) {
            return;
          }
          uploadToDevModeSuccessAlert.close();
          devModeReadyAlert.show({ routeUrl: devMode.endpoints!.swaggerUi });
          window.clearInterval(fetchDevModeDeploymentTask);
        }, FETCH_DEV_MODE_DEPLOYMENT_POLLING_TIME);
      } else {
        if (result.reason === "NOT_READY") {
          uploadToDevModeNotReadyAlert.show();
        } else {
          uploadToDevModeErrorAlert.show();
        }
      }
    } else {
      kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.OPENSHIFT);
      kieSandboxExtendedServices.setModalOpen(true);
    }
  }, [
    isKieSandboxExtendedServicesRunning,
    devModeUploadingAlert,
    devModeDispatch,
    props.workspaceFile,
    uploadToDevModeSuccessAlert,
    devModeReadyAlert,
    devMode.endpoints,
    uploadToDevModeNotReadyAlert,
    uploadToDevModeErrorAlert,
    kieSandboxExtendedServices,
  ]);

  return useMemo(() => {
    return [
      <React.Fragment key={"deploy-dropdown-items"}>
        {props.workspace && (
          <FeatureDependentOnKieSandboxExtendedServices isLight={false} position="left">
            {env.FEATURE_FLAGS.MODE === AppDistributionMode.COMMUNITY && (
              <DropdownItem
                icon={<OpenshiftIcon />}
                id="deploy-your-model-button"
                key={`dropdown-deploy`}
                component={"button"}
                onClick={onDeploy}
                isDisabled={isKieSandboxExtendedServicesRunning && (!isOpenShiftConnected || !canContentBeDeployed)}
                ouiaId={"deploy-to-openshift-dropdown-button"}
              >
                {props.workspace.files.length > 1 && (
                  <Flex flexWrap={{ default: "nowrap" }}>
                    <FlexItem>
                      Deploy models in <b>{`"${props.workspace.descriptor.name}"`}</b>
                    </FlexItem>
                  </Flex>
                )}
                {props.workspace.files.length === 1 && (
                  <Flex flexWrap={{ default: "nowrap" }}>
                    <FlexItem>
                      Deploy <b>{`"${props.workspace.files[0].nameWithoutExtension}"`}</b>
                    </FlexItem>
                    <FlexItem>
                      <b>
                        <FileLabel extension={props.workspace.files[0].extension} />
                      </b>
                    </FlexItem>
                  </Flex>
                )}
              </DropdownItem>
            )}
            {isDevModeEnabled && (
              <DropdownItem
                icon={<UploadIcon />}
                id="upload-dev-mode-button"
                key={`dropdown-upload-dev-mode`}
                component={"button"}
                onClick={onUploadDevMode}
                isDisabled={isKieSandboxExtendedServicesRunning && (!isOpenShiftConnected || !canContentBeDeployed)}
                ouiaId={"upload-to-openshift-dev-mode-dropdown-button"}
              >
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Upload <b>{`"${props.workspace.files[0].nameWithoutExtension}"`}</b> to Dev Mode
                  </FlexItem>
                </Flex>
              </DropdownItem>
            )}
            {needsDependencyDeployment && (
              <>
                <Divider />
                <Tooltip content={i18n.deployments.virtualServiceRegistry.dependencyWarningTooltip} position="bottom">
                  <DropdownItem icon={<RegistryIcon color="var(--pf-global--warning-color--100)" />} isDisabled>
                    <Flex flexWrap={{ default: "nowrap" }}>
                      <FlexItem>
                        <Text component="small" style={{ color: "var(--pf-global--warning-color--200)" }}>
                          This model has foreign workspace dependencies
                        </Text>
                      </FlexItem>
                    </Flex>
                  </DropdownItem>
                </Tooltip>
              </>
            )}
            {!canContentBeDeployed && (
              <>
                <Divider />
                <Tooltip
                  content={
                    "Models with errors/warnings or empty ones cannot be deployed. Check the Problems tab for more information."
                  }
                  position="bottom"
                >
                  <DropdownItem icon={<ExclamationCircleIcon color="var(--pf-global--danger-color--100)" />} isDisabled>
                    <Flex flexWrap={{ default: "nowrap" }}>
                      <FlexItem>
                        <Text component="small" style={{ color: "var(--pf-global--danger-color--300)" }}>
                          This model cannot be deployed
                        </Text>
                      </FlexItem>
                    </Flex>
                  </DropdownItem>
                </Tooltip>
              </>
            )}
          </FeatureDependentOnKieSandboxExtendedServices>
        )}
        {!isOpenShiftConnected && isKieSandboxExtendedServicesRunning && (
          <>
            <Divider />
            <DropdownItem
              id="deploy-setup-button"
              key={`dropdown-deploy-setup`}
              onClick={onSetup}
              ouiaId={"setup-deploy-dropdown-button"}
            >
              <Button isInline={true} variant={ButtonVariant.link}>
                Setup...
              </Button>
            </DropdownItem>
          </>
        )}
      </React.Fragment>,
    ];
  }, [
    props.workspace,
    canContentBeDeployed,
    onDeploy,
    isKieSandboxExtendedServicesRunning,
    isOpenShiftConnected,
    isDevModeEnabled,
    onUploadDevMode,
    needsDependencyDeployment,
    i18n.deployments.virtualServiceRegistry,
    onSetup,
  ]);
}
