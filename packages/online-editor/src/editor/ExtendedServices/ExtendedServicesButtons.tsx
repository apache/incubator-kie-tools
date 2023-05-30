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

import {
  Dropdown,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
  DropdownToggleAction,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import * as React from "react";
import { useCallback, useMemo, useRef, useState } from "react";
import { useOnlineI18n } from "../../i18n";
import { useDevDeployments } from "../../devDeployments/DevDeploymentsContext";
import { useDevDeploymentsDeployDropdownItems } from "../../devDeployments/DevDeploymentsDeployDropdownItems";
import { useDmnRunnerDispatch, useDmnRunnerState } from "../../dmnRunner/DmnRunnerContext";
import { FeatureDependentOnExtendedServices } from "../../extendedServices/FeatureDependentOnExtendedServices";
import { DependentFeature, useExtendedServices } from "../../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../../extendedServices/ExtendedServicesStatus";
import { DmnRunnerMode } from "../../dmnRunner/DmnRunnerStatus";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DownloadIcon } from "@patternfly/react-icons/dist/js/icons/download-icon";
import { UploadIcon } from "@patternfly/react-icons/dist/js/icons/upload-icon";
import { DeleteDropdownWithConfirmation } from "../DeleteDropdownWithConfirmation";
import { useDmnRunnerPersistenceDispatch } from "../../dmnRunnerPersistence/DmnRunnerPersistenceDispatchContext";
import { DmnRunnerProviderActionType } from "../../dmnRunner/DmnRunnerTypes";
import { PanelId, useEditorDockContext } from "../EditorPageDockContextProvider";

interface Props {
  workspace: ActiveWorkspace | undefined;
  workspaceFile: WorkspaceFile;
}

export function ExtendedServicesButtons(props: Props) {
  const { i18n } = useOnlineI18n();
  const extendedServices = useExtendedServices();
  const devDeployments = useDevDeployments();
  const { onTogglePanel, onOpenPanel } = useEditorDockContext();
  const { isExpanded, mode } = useDmnRunnerState();
  const { setDmnRunnerMode, setDmnRunnerContextProviderState } = useDmnRunnerDispatch();
  const devDeploymentsDropdownItems = useDevDeploymentsDeployDropdownItems(props.workspace);
  const { onDeleteDmnRunnerPersistenceJson, onDownloadDmnRunnerPersistenceJson, onUploadDmnRunnerPersistenceJson } =
    useDmnRunnerPersistenceDispatch();
  const downloadDmnRunnerInputsRef = useRef<HTMLAnchorElement>(null);
  const uploadDmnRunnerInputsRef = useRef<HTMLInputElement>(null);

  const toggleDmnRunnerDrawer = useCallback(() => {
    if (extendedServices.status === ExtendedServicesStatus.RUNNING) {
      if (mode === DmnRunnerMode.TABLE) {
        onTogglePanel(PanelId.DMN_RUNNER_TABLE);
        return;
      }
      setDmnRunnerContextProviderState({ type: DmnRunnerProviderActionType.TOGGLE_EXPANDED });
      return;
    }
    extendedServices.setInstallTriggeredBy(DependentFeature.DMN_RUNNER);
    extendedServices.setModalOpen(true);
  }, [setDmnRunnerContextProviderState, extendedServices, mode, onTogglePanel]);

  const toggleDevDeploymentsDropdown = useCallback(
    (isOpen: boolean) => {
      if (extendedServices.status === ExtendedServicesStatus.RUNNING) {
        devDeployments.setDeployDropdownOpen(isOpen);
        return;
      }
      extendedServices.setInstallTriggeredBy(DependentFeature.DEV_DEPLOYMENTS);
      extendedServices.setModalOpen(true);
    },
    [devDeployments, extendedServices]
  );

  const isExtendedServicesRunning = useMemo(() => {
    return extendedServices.status === ExtendedServicesStatus.RUNNING;
  }, [extendedServices.status]);

  const [runModeOpen, setRunModeOpen] = useState<boolean>(false);

  const handleDmnRunnerInputsDownload = useCallback(async () => {
    if (downloadDmnRunnerInputsRef.current) {
      const fileBlob = await onDownloadDmnRunnerPersistenceJson(props.workspaceFile);
      if (fileBlob) {
        downloadDmnRunnerInputsRef.current.download = props.workspaceFile.name.split(".")[0] + ".json";
        downloadDmnRunnerInputsRef.current.href = URL.createObjectURL(fileBlob);
        downloadDmnRunnerInputsRef.current?.click();
      }
    }
  }, [props.workspaceFile, onDownloadDmnRunnerPersistenceJson]);

  const handleDmnRunnerInputsUpload = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        onUploadDmnRunnerPersistenceJson(props.workspaceFile, file);
      }
    },
    [onUploadDmnRunnerPersistenceJson, props.workspaceFile]
  );

  return (
    <>
      <FeatureDependentOnExtendedServices isLight={true} position="top">
        <Dropdown
          className={isExtendedServicesRunning ? "pf-m-active" : ""}
          onSelect={() => devDeployments.setDeployDropdownOpen(false)}
          toggle={
            <DropdownToggle
              id="dmn-dev-deployment-dropdown-button"
              onToggle={toggleDevDeploymentsDropdown}
              data-testid="dmn-dev-deployment-button"
            >
              Deploy
            </DropdownToggle>
          }
          isOpen={devDeployments.isDeployDropdownOpen}
          position={DropdownPosition.right}
          dropdownItems={devDeploymentsDropdownItems}
        />
      </FeatureDependentOnExtendedServices>
      {"  "}
      <FeatureDependentOnExtendedServices isLight={true} position="top">
        <Dropdown
          onSelect={() => setRunModeOpen(!runModeOpen)}
          toggle={
            <DropdownToggle
              splitButtonItems={[
                <DropdownToggleAction
                  key={"dmn-runner-run-button"}
                  id="dmn-runner-button"
                  onClick={toggleDmnRunnerDrawer}
                  className={isExpanded ? "pf-m-active" : ""}
                  data-testid={"dmn-runner-button"}
                >
                  {i18n.terms.run}
                </DropdownToggleAction>,
              ]}
              splitButtonVariant="action"
              onToggle={(isOpen) => {
                setRunModeOpen(isOpen);
              }}
            />
          }
          isOpen={runModeOpen}
          dropdownItems={[
            <DropdownItem
              key={"form-view"}
              component={"button"}
              icon={<ListIcon />}
              onClick={() => {
                if (extendedServices.status === ExtendedServicesStatus.RUNNING) {
                  setDmnRunnerMode(DmnRunnerMode.FORM);
                  setDmnRunnerContextProviderState({
                    type: DmnRunnerProviderActionType.DEFAULT,
                    newState: { isExpanded: true },
                  });
                }
              }}
            >
              As Form
            </DropdownItem>,
            <DropdownItem
              key={"table-view"}
              component={"button"}
              icon={<TableIcon />}
              onClick={() => {
                if (extendedServices.status === ExtendedServicesStatus.RUNNING) {
                  setDmnRunnerMode(DmnRunnerMode.TABLE);
                  onOpenPanel(PanelId.DMN_RUNNER_TABLE);
                  setDmnRunnerContextProviderState({
                    type: DmnRunnerProviderActionType.DEFAULT,
                    newState: { isExpanded: true },
                  });
                }
              }}
            >
              As Table
            </DropdownItem>,
            <React.Fragment key={"dmn-runner-inputs"}>
              <Divider />
              <DropdownItem
                component={"button"}
                icon={<DownloadIcon />}
                onClick={() => handleDmnRunnerInputsDownload()}
              >
                Download inputs
              </DropdownItem>
            </React.Fragment>,
            <DropdownItem
              key={"dmn-runner--upload-inputs"}
              component={"button"}
              icon={<UploadIcon />}
              onClick={() => uploadDmnRunnerInputsRef.current?.click()}
            >
              Load inputs
            </DropdownItem>,
            <React.Fragment key={"dmn-runner--delete-inputs"}>
              <Divider />
              <DropdownItem component={"div"} style={{ padding: "4px" }}>
                <DeleteDropdownWithConfirmation
                  onDelete={() => {
                    setDmnRunnerContextProviderState({
                      type: DmnRunnerProviderActionType.DEFAULT,
                      newState: { currentInputIndex: 0 },
                    });
                    onDeleteDmnRunnerPersistenceJson(props.workspaceFile);
                  }}
                  item={`Delete DMN Runner inputs`}
                  label={" Delete inputs"}
                  isHoverable={false}
                />
              </DropdownItem>
            </React.Fragment>,
          ]}
        />
      </FeatureDependentOnExtendedServices>
      <a ref={downloadDmnRunnerInputsRef} />
      <input
        ref={uploadDmnRunnerInputsRef}
        type="file"
        style={{ display: "none" }}
        onChange={handleDmnRunnerInputsUpload}
        accept={".json"}
        onClick={(event: any) => {
          event.target.value = null;
        }}
      />
    </>
  );
}
