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
import { useDmnDevSandbox } from "../DmnDevSandbox/DmnDevSandboxContext";
import { useDmnDevSandboxDropdownItems } from "../DmnDevSandbox/DmnDevSandboxDropdownItems";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useDmnRunnerDispatch, useDmnRunnerState } from "../DmnRunner/DmnRunnerContext";
import { FeatureDependentOnKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/FeatureDependentOnKieSandboxExtendedServices";
import {
  DependentFeature,
  useKieSandboxExtendedServices,
} from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useSettings } from "../../settings/SettingsContext";
import { DmnRunnerMode } from "../DmnRunner/DmnRunnerStatus";
import { EditorPageDockDrawerRef, PanelId } from "../EditorPageDockDrawer";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { DownloadIcon } from "@patternfly/react-icons/dist/js/icons/download-icon";
import { UploadIcon } from "@patternfly/react-icons/dist/js/icons/upload-icon";
import { DeleteDropdownWithConfirmation } from "../DeleteDropdownWithConfirmation";
import { useDmnRunnerInputsDispatch } from "../../dmnRunnerInputs/DmnRunnerInputsDispatchContext";

interface Props {
  editorPageDock: EditorPageDockDrawerRef | undefined;
  workspace: ActiveWorkspace | undefined;
  workspaceFile: WorkspaceFile;
}

export function KieSandboxExtendedServicesButtons(props: Props) {
  const { i18n } = useOnlineI18n();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const dmnDevSandbox = useDmnDevSandbox();
  const dmnRunnerState = useDmnRunnerState();
  const dmnRunnerDispatch = useDmnRunnerDispatch();
  const settings = useSettings();
  const dmnDevSandboxDropdownItems = useDmnDevSandboxDropdownItems(props.workspace);
  const workspacesDmnRunner = useDmnRunnerInputsDispatch();
  const downloadDmnRunnerInputsRef = useRef<HTMLAnchorElement>(null);
  const uploadDmnRunnerInputsRef = useRef<HTMLInputElement>(null);

  const toggleDmnRunnerDrawer = useCallback(() => {
    if (kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING) {
      if (dmnRunnerState.mode === DmnRunnerMode.TABLE) {
        props.editorPageDock?.toggle(PanelId.DMN_RUNNER_TABULAR);
      } else {
        dmnRunnerDispatch.setExpanded((prev) => !prev);
      }
      return;
    }
    kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_RUNNER);
    kieSandboxExtendedServices.setModalOpen(true);
  }, [dmnRunnerState.mode, dmnRunnerDispatch, kieSandboxExtendedServices, props.editorPageDock]);

  const toggleDmnDevSandboxDropdown = useCallback(
    (isOpen: boolean) => {
      if (kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING) {
        dmnDevSandbox.setDropdownOpen(isOpen);
        return;
      }
      kieSandboxExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_DEV_SANDBOX);
      kieSandboxExtendedServices.setModalOpen(true);
    },
    [dmnDevSandbox, kieSandboxExtendedServices]
  );

  const isDevSandboxEnabled = useMemo(() => {
    return (
      kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING &&
      settings.openshift.status === OpenShiftInstanceStatus.CONNECTED
    );
  }, [kieSandboxExtendedServices.status, settings.openshift.status]);

  const [runModeOpen, setRunModeOpen] = useState<boolean>(false);

  const handleDmnRunnerInputsDownload = useCallback(async () => {
    if (downloadDmnRunnerInputsRef.current) {
      const fileBlob = await workspacesDmnRunner.getInputRowsForDownload(props.workspaceFile);
      if (fileBlob) {
        downloadDmnRunnerInputsRef.current.download = props.workspaceFile.name.split(".")[0] + ".json";
        downloadDmnRunnerInputsRef.current.href = URL.createObjectURL(fileBlob);
        downloadDmnRunnerInputsRef.current?.click();
      }
    }
  }, [props.workspaceFile, workspacesDmnRunner]);

  const handleDmnRunnerInputsUpload = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        workspacesDmnRunner.uploadInputRows(props.workspaceFile, file);
      }
    },
    [workspacesDmnRunner, props.workspaceFile]
  );

  return (
    <>
      <FeatureDependentOnKieSandboxExtendedServices isLight={true} position="top">
        <Dropdown
          className={isDevSandboxEnabled ? "pf-m-active" : ""}
          onSelect={() => dmnDevSandbox.setDropdownOpen(false)}
          toggle={
            <DropdownToggle
              id="dmn-dev-sandbox-dropdown-button"
              onToggle={toggleDmnDevSandboxDropdown}
              data-testid="dmn-dev-sandbox-button"
            >
              Try on OpenShift
            </DropdownToggle>
          }
          isOpen={dmnDevSandbox.isDropdownOpen}
          position={DropdownPosition.right}
          dropdownItems={dmnDevSandboxDropdownItems}
        />
      </FeatureDependentOnKieSandboxExtendedServices>
      {"  "}
      <FeatureDependentOnKieSandboxExtendedServices isLight={true} position="top">
        <Dropdown
          onSelect={() => setRunModeOpen(!runModeOpen)}
          toggle={
            <DropdownToggle
              splitButtonItems={[
                <DropdownToggleAction
                  key={"dmn-runner-run-button"}
                  id="dmn-runner-button"
                  onClick={toggleDmnRunnerDrawer}
                  className={dmnRunnerState.isExpanded ? "pf-m-active" : ""}
                  data-testid={"dmn-runner-button"}
                >
                  {i18n.terms.run}
                </DropdownToggleAction>,
              ]}
              splitButtonVariant="action"
              onToggle={(isOpen) => setRunModeOpen(isOpen)}
            />
          }
          isOpen={runModeOpen}
          dropdownItems={[
            <DropdownItem
              key={"form-view"}
              component={"button"}
              icon={<ListIcon />}
              onClick={() => {
                if (kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING) {
                  dmnRunnerDispatch.setMode(DmnRunnerMode.FORM);
                  props.editorPageDock?.close();
                  dmnRunnerDispatch.setExpanded(true);
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
                if (kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING) {
                  dmnRunnerDispatch.setMode(DmnRunnerMode.TABLE);
                  props.editorPageDock?.open(PanelId.DMN_RUNNER_TABULAR);
                  dmnRunnerDispatch.setExpanded(true);
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
              <DropdownItem component={"button"} style={{ padding: "4px" }}>
                <DeleteDropdownWithConfirmation
                  onDelete={() => workspacesDmnRunner.deletePersistedInputRows(props.workspaceFile)}
                  item={`Delete DMN Runner inputs`}
                  label={" Delete inputs"}
                  isHoverable={false}
                />
              </DropdownItem>
            </React.Fragment>,
          ]}
        />
      </FeatureDependentOnKieSandboxExtendedServices>
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
