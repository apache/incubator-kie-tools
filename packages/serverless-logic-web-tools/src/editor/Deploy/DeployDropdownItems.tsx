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
import { RegistryIcon } from "@patternfly/react-icons/dist/js/icons";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons";
import { OpenshiftIcon } from "@patternfly/react-icons/dist/js/icons/openshift-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
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
import { useDevMode } from "../../openshift/devMode/DevModeContext";
import { AlertsController, useAlert } from "../../alerts/Alerts";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { isServerlessWorkflow } from "../../extension";
import { AppDeploymentMode, useEnv } from "../../env/EnvContext";

interface Props {
  alerts: AlertsController | undefined;
  workspace: ActiveWorkspace;
  workspaceFile: WorkspaceFile;
  canContentBeDeployed: boolean;
}

// TOOD CAPONETTO: Alerts can be moved to a context
export function useDeployDropdownItems(props: Props) {
  const env = useEnv();
  const { i18n } = useAppI18n();
  const devMode = useDevMode();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const openshift = useOpenShift();
  const { needsDependencyDeployment } = useVirtualServiceRegistryDependencies({
    workspace: props.workspace,
  });

  const devModeUploadingAlert = useAlert(
    props.alerts,
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

  const devModeReadyAlert = useAlert<{ routeUrl: string }>(
    props.alerts,
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

  const uploadToDevModeSuccessAlert = useAlert(
    props.alerts,
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

  const uploadToDevModeErrorAlert = useAlert(
    props.alerts,
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

  const uploadToDevModeNotReadyAlert = useAlert(
    props.alerts,
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

  const isDevModeEnabled = useMemo(() => isServerlessWorkflow(props.workspaceFile.name), [props.workspaceFile.name]);

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

  const onDeployDevMode = useCallback(async () => {
    if (isKieSandboxExtendedServicesRunning) {
      devModeUploadingAlert.show();
      const result = await devMode.upload([props.workspaceFile]);
      devModeUploadingAlert.close();
      if (result.success && devMode.endpoints) {
        uploadToDevModeSuccessAlert.show();

        const fetchDevModeDeploymentTask = window.setInterval(async () => {
          const isReady = await devMode.checkHealthReady();
          if (!isReady) {
            return;
          }
          uploadToDevModeSuccessAlert.close();
          devModeReadyAlert.show({ routeUrl: devMode.endpoints!.swaggerUi });
          window.clearInterval(fetchDevModeDeploymentTask);
        }, 2000);
      }

      if (!result.success) {
        if (result.reason === "ERROR") {
          uploadToDevModeErrorAlert.show();
        } else if (result.reason === "NOT_READY") {
          uploadToDevModeNotReadyAlert.show();
        }
      }
    } else {
      kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.OPENSHIFT);
      kieSandboxExtendedServices.setModalOpen(true);
    }
  }, [
    devMode,
    devModeReadyAlert,
    isKieSandboxExtendedServicesRunning,
    kieSandboxExtendedServices,
    props.workspaceFile,
    devModeUploadingAlert,
    uploadToDevModeErrorAlert,
    uploadToDevModeNotReadyAlert,
    uploadToDevModeSuccessAlert,
  ]);

  return useMemo(() => {
    return [
      <React.Fragment key={"deploy-dropdown-items"}>
        {props.workspace && (
          <FeatureDependentOnKieSandboxExtendedServices isLight={false} position="left">
            {env.vars.FEATURE_FLAGS.MODE === AppDeploymentMode.COMMUNITY && (
              <DropdownItem
                icon={<OpenshiftIcon />}
                id="deploy-your-model-button"
                key={`dropdown-deploy`}
                component={"button"}
                onClick={onDeploy}
                isDisabled={isKieSandboxExtendedServicesRunning && !isOpenShiftConnected}
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
                icon={<OpenshiftIcon />}
                id="deploy-dev-mode-button"
                key={`dropdown-deploy-dev-mode`}
                component={"button"}
                onClick={onDeployDevMode}
                isDisabled={
                  isKieSandboxExtendedServicesRunning && (!isOpenShiftConnected || !props.canContentBeDeployed)
                }
                ouiaId={"deploy-to-openshift-dev-mode-dropdown-button"}
              >
                <Flex flexWrap={{ default: "nowrap" }}>
                  <FlexItem>
                    Upload <b>{`"${props.workspace.files[0].nameWithoutExtension}"`}</b> to Dev Mode
                  </FlexItem>
                </Flex>
              </DropdownItem>
            )}
            {needsDependencyDeployment && env.vars.FEATURE_FLAGS.MODE === AppDeploymentMode.COMMUNITY && (
              <>
                <Divider />
                <Tooltip content={i18n.deployments.virtualServiceRegistry.dependencyWarningTooltip} position="bottom">
                  <DropdownItem icon={<RegistryIcon color="var(--pf-global--warning-color--100)" />} isDisabled>
                    <Flex flexWrap={{ default: "nowrap" }}>
                      <FlexItem>
                        <Text component="small" style={{ color: "var(--pf-global--warning-color--200)" }}>
                          {i18n.deployments.virtualServiceRegistry.dependencyWarning}
                        </Text>
                      </FlexItem>
                    </Flex>
                  </DropdownItem>
                </Tooltip>
              </>
            )}
            {!props.canContentBeDeployed && (
              <>
                <Divider />
                <DropdownItem icon={<ExclamationCircleIcon />} isDisabled>
                  <Flex flexWrap={{ default: "nowrap" }}>
                    <FlexItem>
                      <Text component="small">Models with errors/warnings cannot be uploaded</Text>
                    </FlexItem>
                  </Flex>
                </DropdownItem>
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
    props.canContentBeDeployed,
    env.vars.FEATURE_FLAGS.MODE,
    onDeploy,
    isKieSandboxExtendedServicesRunning,
    isOpenShiftConnected,
    isDevModeEnabled,
    onDeployDevMode,
    needsDependencyDeployment,
    i18n.deployments.virtualServiceRegistry,
    onSetup,
  ]);
}
