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
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
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
import { FeatureDependentExtendedServices } from "../../extendedServices/FeatureDependentOnExtendedServices";
import { DependentFeature, useExtendedServices } from "../../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../../extendedServices/ExtendedServicesStatus";
import { useOpenShift } from "../../openshift/OpenShiftContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useSettings } from "../../settings/SettingsContext";
import { useVirtualServiceRegistryDependencies } from "../../virtualServiceRegistry/hooks/useVirtualServiceRegistryDependencies";
import { FileLabel } from "../../workspace/components/FileLabel";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useDevMode, useDevModeDispatch } from "../../openshift/swfDevMode/DevModeContext";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { useEnv } from "../../env/EnvContext";
import { useGlobalAlert } from "../../alerts/GlobalAlertsContext";
import { useEditor } from "../hooks/EditorContext";
import { isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { useHistory } from "react-router";
import { routes } from "../../navigation/Routes";

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
  const extendedServices = useExtendedServices();
  const openshift = useOpenShift();
  const [canContentBeDeployed, setCanContentBeDeployed] = useState(true);
  const { needsDependencyDeployment } = useVirtualServiceRegistryDependencies({
    workspace: props.workspace,
  });
  const history = useHistory();

  useEffect(() => {
    props.workspaceFile.getFileContentsAsString().then((content) => {
      setCanContentBeDeployed(content.trim().length > 0 && !notifications.some((d) => d.severity === "ERROR"));
    });
  }, [notifications, props.workspaceFile]);

  const devModeUploadingAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="info"
          title={
            <>
              <Spinner size={"sm"} />
              &nbsp;&nbsp; Uploading files to Dev Mode...
            </>
          }
          aria-live="polite"
          data-testid="alert-dev-mode-uploading"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const devModeReadyAlert = useGlobalAlert<{ routeUrl: string; filePaths: string[] }>(
    useCallback(({ close }, { routeUrl, filePaths }) => {
      return (
        <Alert
          isExpandable
          className="pf-u-mb-md"
          variant="success"
          title={"Your Dev Mode has been successfully updated"}
          aria-live="polite"
          data-testid="alert-dev-mode-ready"
          actionClose={<AlertActionCloseButton onClose={close} />}
          actionLinks={
            <AlertActionLink onClick={() => window.open(routeUrl, "_blank")}>
              {"Go to Serverless Workflow Dev UI ↗"}
            </AlertActionLink>
          }
        >
          <>
            <Text component="p" style={{ marginBottom: "4px" }}>
              Files that have been uploaded:
            </Text>
            <List>
              {filePaths.map((p) => (
                <ListItem key={`uploaded-file-path-${p}`}>{p}</ListItem>
              ))}
            </List>
          </>
        </Alert>
      );
    }, [])
  );

  const uploadToDevModeSuccessAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="info"
          title={
            <>
              <Spinner size={"sm"} />
              &nbsp;&nbsp; Updating the Dev Mode deployment...
            </>
          }
          aria-live="polite"
          data-testid="alert-dev-mode-updating"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const uploadToDevModeErrorAlert = useGlobalAlert<{ messages: string[] }>(
    useCallback(({ close }, { messages }) => {
      return (
        <Alert
          isExpandable
          className="pf-u-mb-md"
          variant="warning"
          title={"Something went wrong while uploading to the Dev Mode."}
          aria-live="polite"
          data-testid="alert-upload-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        >
          {messages.length > 1 ? (
            <List>
              {messages.map((p) => (
                <ListItem key={`error-message-upload-${p}`}>{p}</ListItem>
              ))}
            </List>
          ) : (
            <Text component="p">{messages[0]}</Text>
          )}
        </Alert>
      );
    }, [])
  );

  const uploadToDevModeTimeoutErrorAlert = useGlobalAlert(
    useCallback(({ close }) => {
      return (
        <Alert
          className="pf-u-mb-md"
          variant="warning"
          title={
            <>
              Something went wrong while uploading to the Dev Mode.
              <br />
              Please check your Dev Mode deployment logs for more details.
            </>
          }
          aria-live="polite"
          data-testid="alert-upload-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const isExtendedServicesRunning = useMemo(
    () => extendedServices.status === ExtendedServicesStatus.RUNNING,
    [extendedServices.status]
  );

  const isOpenShiftConnected = useMemo(
    () => settings.openshift.status === OpenShiftInstanceStatus.CONNECTED,
    [settings.openshift.status]
  );

  const isUploadToDevModeEnabled = useMemo(
    () => devMode.isEnabled && isOfKind("sw", props.workspaceFile.name),
    [devMode.isEnabled, props.workspaceFile.name]
  );

  const onSetup = useCallback(() => {
    history.push(routes.settings.openshift.path({}));
  }, [history]);

  const onDeploy = useCallback(() => {
    if (isExtendedServicesRunning) {
      openshift.setConfirmDeployModalOpen(true);
      return;
    }
    extendedServices.setInstallTriggeredBy(DependentFeature.OPENSHIFT);
    extendedServices.setModalOpen(true);
  }, [isExtendedServicesRunning, extendedServices, openshift]);

  const onUploadDevMode = useCallback(async () => {
    if (isExtendedServicesRunning) {
      devModeUploadingAlert.show();
      const result = await devModeDispatch.upload({
        targetSwfFile: props.workspaceFile,
        allFiles: props.workspace.files,
      });
      devModeUploadingAlert.close();

      if (result.success) {
        uploadToDevModeSuccessAlert.show();

        let attemptsLeft = 15;
        const fetchDevModeDeploymentTask = window.setInterval(async () => {
          const isReady = await devModeDispatch.checkHealthReady();
          attemptsLeft--;
          if (attemptsLeft === 0) {
            uploadToDevModeSuccessAlert.close();
            uploadToDevModeTimeoutErrorAlert.show();
            window.clearInterval(fetchDevModeDeploymentTask);
            return;
          }
          if (!isReady) {
            return;
          }
          uploadToDevModeSuccessAlert.close();
          devModeReadyAlert.show({ routeUrl: devMode.endpoints!.swfDevUi, filePaths: result.uploadedPaths });
          window.clearInterval(fetchDevModeDeploymentTask);
        }, FETCH_DEV_MODE_DEPLOYMENT_POLLING_TIME);
      } else {
        uploadToDevModeErrorAlert.show({ messages: result.messages });
      }
    } else {
      extendedServices.setInstallTriggeredBy(DependentFeature.OPENSHIFT);
      extendedServices.setModalOpen(true);
    }
  }, [
    isExtendedServicesRunning,
    devModeUploadingAlert,
    devModeDispatch,
    props.workspaceFile,
    props.workspace.files,
    uploadToDevModeSuccessAlert,
    devModeReadyAlert,
    devMode.endpoints,
    uploadToDevModeErrorAlert,
    extendedServices,
    uploadToDevModeTimeoutErrorAlert,
  ]);

  return useMemo(() => {
    return [
      <React.Fragment key={"deploy-dropdown-items"}>
        {props.workspace && (
          <FeatureDependentExtendedServices isLight={false} position="left">
            {isUploadToDevModeEnabled && (
              <DropdownItem
                icon={<UploadIcon />}
                id="upload-dev-mode-button"
                key={`dropdown-upload-dev-mode`}
                component={"button"}
                onClick={onUploadDevMode}
                isDisabled={isExtendedServicesRunning && (!isOpenShiftConnected || !canContentBeDeployed)}
                ouiaId={"upload-to-openshift-dev-mode-dropdown-button"}
              >
                {props.workspace.files.length > 1 && (
                  <Flex flexWrap={{ default: "nowrap" }}>
                    <FlexItem>
                      Upload <b>{`"${props.workspace.descriptor.name}"`}</b> to Dev Mode
                    </FlexItem>
                  </Flex>
                )}
                {props.workspace.files.length === 1 && (
                  <Flex flexWrap={{ default: "nowrap" }}>
                    <FlexItem>
                      Upload <b>{`"${props.workspace.files[0].nameWithoutExtension}"`}</b> to Dev Mode
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
            <DropdownItem
              icon={<OpenshiftIcon />}
              id="deploy-your-model-button"
              key={`dropdown-deploy`}
              component={"button"}
              onClick={onDeploy}
              isDisabled={isExtendedServicesRunning && (!isOpenShiftConnected || !canContentBeDeployed)}
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
                    "Models with errors or empty ones cannot be deployed. Check the Problems tab for more information."
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
          </FeatureDependentExtendedServices>
        )}
        {!isOpenShiftConnected && isExtendedServicesRunning && (
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
    onDeploy,
    isExtendedServicesRunning,
    isOpenShiftConnected,
    canContentBeDeployed,
    isUploadToDevModeEnabled,
    onUploadDevMode,
    needsDependencyDeployment,
    i18n.deployments.virtualServiceRegistry.dependencyWarningTooltip,
    onSetup,
  ]);
}
