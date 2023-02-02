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

import * as React from "react";
import { PropsWithChildren, useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { ToggleGroup } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { DmnRunnerMode } from "./DmnRunner/DmnRunnerStatus";
import { useDmnRunnerState } from "./DmnRunner/DmnRunnerContext";
import { useOnlineI18n } from "../i18n";
import { NotificationsPanel, NotificationsPanelRef } from "./NotificationsPanel/NotificationsPanel";
import { DmnRunnerTable } from "./DmnRunner/DmnRunnerTable";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DecisionResult } from "@kie-tools/form-dmn";
import {
  NotificationsPanelDockToggle,
  NotificationsPanelDockToggleRef,
} from "./NotificationsPanel/NotificationsPanelDockToggle";
import { DmnRunnerDockToggle } from "./DmnRunner/DmnRunnerDockToggle";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { QueryParams } from "../navigation/Routes";
import { useHistory } from "react-router";

export enum PanelId {
  DMN_RUNNER_TABLE = "dmn-runner-table",
  NOTIFICATIONS_PANEL = "notifications-panel",
  NONE = "",
}

enum DockQueryParams {
  PROBLEMS = "problems",
  EXECUTION = "execution",
}

interface EditorPageDockDrawerProps {
  workspaceFile: WorkspaceFile;
}

export interface EditorPageDockDrawerRef {
  open: (panelId: PanelId) => void;
  toggle: (panelId: PanelId) => void;
  close: () => void;
  getNotificationsPanel: () => NotificationsPanelRef | undefined;
  setNotifications: (tabName: string, path: string, notifications: Notification[]) => void;
}

export const EditorPageDockDrawer = React.forwardRef<
  EditorPageDockDrawerRef,
  PropsWithChildren<EditorPageDockDrawerProps>
>((props, forwardRef) => {
  const { i18n } = useOnlineI18n();
  const dmnRunnerState = useDmnRunnerState();
  const [panel, setPanel] = useState<PanelId>(PanelId.NONE);
  const [dmnRunnerResults, setDmnRunnerResults] = useState<Array<DecisionResult[] | undefined>>([]);
  const [notificationsToggle, notificationsToggleRef] = useController<NotificationsPanelDockToggleRef>();
  const [notificationsPanel, notificationsPanelRef] = useController<NotificationsPanelRef>();
  const queryParams = useQueryParams();
  const history = useHistory();
  const extendedServices = useExtendedServices();

  const notificationsPanelTabNames = useMemo(() => {
    if (
      props.workspaceFile.extension.toLowerCase() === "dmn" &&
      extendedServices.status === KieSandboxExtendedServicesStatus.RUNNING
    ) {
      return [i18n.terms.validation, i18n.terms.execution];
    }
    return [i18n.terms.validation];
  }, [props.workspaceFile.extension, extendedServices.status, i18n.terms.validation, i18n.terms.execution]);

  useEffect(() => {
    if (!notificationsPanelTabNames.includes(i18n.terms.execution)) {
      notificationsToggle?.deleteNotificationsFromTab(i18n.terms.execution);
    }
    if (notificationsPanel && notificationsToggle) {
      const notifications = notificationsToggle.getNotifications();
      notifications.forEach((value, tabName) => {
        notificationsPanel.getTab(tabName)?.kogitoNotifications_setNotifications(value.path, value.notifications);
      });
    }
  }, [i18n.terms.execution, notificationsPanel, notificationsPanelTabNames, notificationsToggle]);

  const onToggle = useCallback(
    (newPanel: PanelId) => {
      let query = queryParams.without(QueryParams.DOCK);
      if (query.getString(QueryParams.DMN_RUNNER) === DmnRunnerMode.TABLE) {
        query = query.without(QueryParams.DMN_RUNNER);
      }

      // Remove the problemsIsExpanded and add the dmnRunnerIsExpanded
      if (panel !== PanelId.DMN_RUNNER_TABLE && newPanel === PanelId.DMN_RUNNER_TABLE) {
        query = queryParams.with(QueryParams.DMN_RUNNER, DmnRunnerMode.TABLE).without(QueryParams.DOCK);
      }

      // Remove the dmnRunnerIsExpanded only if the DMN runner is in "table" view and add the problemsIsExpanded
      if (panel !== PanelId.NOTIFICATIONS_PANEL && newPanel === PanelId.NOTIFICATIONS_PANEL) {
        query = queryParams.with(QueryParams.DOCK, DockQueryParams.PROBLEMS);
        if (query.getString(QueryParams.DMN_RUNNER) === DmnRunnerMode.TABLE) {
          query = query.without(QueryParams.DMN_RUNNER);
        }
      }

      history.replace({ search: query.toString() });
    },
    [panel, history, queryParams]
  );

  const setNotifications = useCallback(
    (tabName: string, path: string, notifications: Notification[]) => {
      notificationsToggle?.setNewNotifications(tabName, { path, notifications });
      notificationsPanel?.getTab(tabName)?.kogitoNotifications_setNotifications(path, notifications);
    },
    [notificationsPanel, notificationsToggle]
  );

  useImperativeHandle(
    forwardRef,
    () => ({
      open: (panelId: PanelId) => setPanel(panelId),
      toggle: (panelId: PanelId) => onToggle(panelId),
      close: () => setPanel(PanelId.NONE),
      getNotificationsPanel: () => notificationsPanel,
      setNotifications,
    }),
    [notificationsPanel, onToggle, setNotifications]
  );

  const notificationsPanelIsDisabled = useMemo(() => {
    return (
      (props.workspaceFile.extension.toLowerCase() === "dmn" ||
        props.workspaceFile.extension.toLowerCase() === "bpmn" ||
        props.workspaceFile.extension.toLowerCase() === "bpmn2") &&
      extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING
    );
  }, [extendedServices.status, props.workspaceFile.extension]);

  const notificationsPanelDisabledReason = useMemo(() => {
    if (
      (props.workspaceFile.extension.toLowerCase() === "dmn" ||
        props.workspaceFile.extension.toLowerCase() === "bpmn" ||
        props.workspaceFile.extension.toLowerCase() === "bpmn2") &&
      extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING
    ) {
      return "In order to have access to Problems tab you need to use the KIE Sandbox Extended Services";
    }
    return "";
  }, [extendedServices.status, props.workspaceFile.extension]);

  const isDmnTableMode = useMemo(
    () => dmnRunnerState.mode === DmnRunnerMode.TABLE && props.workspaceFile.extension.toLowerCase() === "dmn",
    [dmnRunnerState.mode, props.workspaceFile.extension]
  );

  useEffect(() => {
    if (queryParams.has(QueryParams.DMN_RUNNER)) {
      const dmnRunnerMode = queryParams.getString(QueryParams.DMN_RUNNER);
      if (
        dmnRunnerMode === DmnRunnerMode.TABLE &&
        extendedServices.status === KieSandboxExtendedServicesStatus.RUNNING
      ) {
        setPanel(PanelId.DMN_RUNNER_TABLE);
        // remove dock param;
        return;
      }
      // wrong value; remove dmn_runner param;
    }

    if (queryParams.has(QueryParams.DOCK)) {
      const dockQueryParam = queryParams.getString(QueryParams.DOCK);
      if (dockQueryParam === DockQueryParams.PROBLEMS) {
        setPanel(PanelId.NOTIFICATIONS_PANEL);
        // setTab problems
        return;
      }
      if (dockQueryParam === DockQueryParams.EXECUTION) {
        if (extendedServices.status === KieSandboxExtendedServicesStatus.RUNNING) {
          setPanel(PanelId.NOTIFICATIONS_PANEL);
          // setTab execution
          return;
        }
      }
      // wrong value; remove dock param;
    }
    setPanel(PanelId.NONE);
  }, [extendedServices.status, history, queryParams]);

  return (
    <>
      <Drawer isInline={true} position={"bottom"} isExpanded={panel !== PanelId.NONE}>
        <DrawerContent
          panelContent={
            panel !== PanelId.NONE && (
              <DrawerPanelContent style={{ height: "100%" }} isResizable={true}>
                {panel === PanelId.NOTIFICATIONS_PANEL && (
                  <NotificationsPanel ref={notificationsPanelRef} tabNames={notificationsPanelTabNames} />
                )}
                {panel === PanelId.DMN_RUNNER_TABLE && isDmnTableMode && (
                  <DmnRunnerTable
                    workspaceFile={props.workspaceFile}
                    setPanelOpen={setPanel}
                    dmnRunnerResults={dmnRunnerResults}
                    setDmnRunnerResults={setDmnRunnerResults}
                  />
                )}
              </DrawerPanelContent>
            )
          }
        >
          {props.children}
        </DrawerContent>
      </Drawer>
      <div
        style={{
          borderTop: "rgb(221, 221, 221) solid 1px",
          width: "100%",
          display: "flex",
          justifyContent: "flex-end",
        }}
      >
        <ToggleGroup>
          {isDmnTableMode && (
            <DmnRunnerDockToggle isSelected={panel === PanelId.DMN_RUNNER_TABLE} onChange={(id) => onToggle(id)} />
          )}
          <NotificationsPanelDockToggle
            isDisabled={notificationsPanelIsDisabled}
            disabledReason={notificationsPanelDisabledReason}
            ref={notificationsToggleRef}
            isSelected={panel === PanelId.NOTIFICATIONS_PANEL}
            onChange={(id) => onToggle(id)}
          />
        </ToggleGroup>
      </div>
    </>
  );
});
