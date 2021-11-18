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
import { useCallback, useMemo, useState } from "react";
import { useOnlineI18n } from "../../i18n";
import { useDmnDevSandbox } from "../DmnDevSandbox/DmnDevSandboxContext";
import { useDmnDevSandboxDropdownItems } from "../DmnDevSandbox/DmnDevSandboxDropdownItems";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useDmnRunnerDispatch, useDmnRunnerState } from "../DmnRunner/DmnRunnerContext";
import { FeatureDependentOnKieToolingExtendedServices } from "../../kieToolingExtendedServices/FeatureDependentOnKieToolingExtendedServices";
import {
  DependentFeature,
  useKieToolingExtendedServices,
} from "../../kieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../../kieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { useSettings } from "../../settings/SettingsContext";
import { DmnRunnerMode } from "../DmnRunner/DmnRunnerStatus";
import { EditorPageDockDrawerRef, PanelId } from "../EditorPageDockDrawer";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";

interface Props {
  editorPageDock: EditorPageDockDrawerRef | undefined;
  workspace: ActiveWorkspace | undefined;
}

export function KieToolingExtendedServicesButtons(props: Props) {
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const dmnDevSandbox = useDmnDevSandbox();
  const dmnRunnerState = useDmnRunnerState();
  const dmnRunnerDispatch = useDmnRunnerDispatch();
  const settings = useSettings();
  const dmnDevSandboxDropdownItems = useDmnDevSandboxDropdownItems(props.workspace);

  const toggleDmnRunnerDrawer = useCallback(() => {
    if (kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING) {
      if (dmnRunnerState.mode === DmnRunnerMode.TABLE) {
        props.editorPageDock?.toggle(PanelId.DMN_RUNNER_TABULAR);
      } else {
        dmnRunnerDispatch.setExpanded((prev) => !prev);
      }
      return;
    }
    kieToolingExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_RUNNER);
    kieToolingExtendedServices.setModalOpen(true);
  }, [dmnRunnerState.mode, dmnRunnerDispatch, kieToolingExtendedServices, props.editorPageDock]);

  const toggleDmnDevSandboxDropdown = useCallback(
    (isOpen: boolean) => {
      if (kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING) {
        dmnDevSandbox.setDropdownOpen(isOpen);
        return;
      }
      kieToolingExtendedServices.setInstallTriggeredBy(DependentFeature.DMN_DEV_SANDBOX);
      kieToolingExtendedServices.setModalOpen(true);
    },
    [dmnDevSandbox, kieToolingExtendedServices]
  );

  const isDevSandboxEnabled = useMemo(() => {
    return (
      kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING &&
      settings.openshift.status === OpenShiftInstanceStatus.CONNECTED
    );
  }, [kieToolingExtendedServices.status, settings.openshift.status]);

  const [runModeOpen, setRunModeOpen] = useState<boolean>(false);

  return (
    <>
      <FeatureDependentOnKieToolingExtendedServices isLight={true} position="top">
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
      </FeatureDependentOnKieToolingExtendedServices>
      {"  "}
      <FeatureDependentOnKieToolingExtendedServices isLight={true} position="top">
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
                if (kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING) {
                  dmnRunnerDispatch.setMode(DmnRunnerMode.FORM);
                  props.editorPageDock?.close();
                  dmnRunnerDispatch.setExpanded(true);
                }
              }}
            >
              as Form
            </DropdownItem>,
            <DropdownItem
              key={"table-view"}
              component={"button"}
              icon={<TableIcon />}
              onClick={() => {
                if (kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING) {
                  dmnRunnerDispatch.setMode(DmnRunnerMode.TABLE);
                  props.editorPageDock?.open(PanelId.DMN_RUNNER_TABULAR);
                  dmnRunnerDispatch.setExpanded(true);
                }
              }}
            >
              as Table
            </DropdownItem>,
          ]}
        />
      </FeatureDependentOnKieToolingExtendedServices>
    </>
  );
}
