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
import {
  PropsWithChildren,
  useCallback,
  useEffect,
  useImperativeHandle,
  useMemo,
  useState,
  useLayoutEffect,
} from "react";
import { ToggleGroup } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { DmnRunnerMode } from "../dmnRunner/DmnRunnerStatus";
import { useDmnRunnerState, useDmnRunnerDispatch } from "../dmnRunner/DmnRunnerContext";
import { useOnlineI18n } from "../i18n";
import { NotificationsPanel, NotificationsPanelRef } from "./NotificationsPanel/NotificationsPanel";
import { DmnRunnerTable } from "../dmnRunner/DmnRunnerTable";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DecisionResult } from "@kie-tools/form-dmn";
import {
  NotificationsPanelDockToggle,
  NotificationsPanelDockToggleRef,
} from "./NotificationsPanel/NotificationsPanelDockToggle";
import { DmnRunnerDockToggle } from "../dmnRunner/DmnRunnerDockToggle";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { DmnRunnerProviderActionType } from "../dmnRunner/DmnRunnerProvider";

export enum PanelId {
  DMN_RUNNER_TABLE = "dmn-runner-table",
  NOTIFICATIONS_PANEL = "notifications-panel",
  NONE = "",
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
  const { isExpanded, mode } = useDmnRunnerState();
  const { dmnRunnerDispatcher } = useDmnRunnerDispatch();
  const [panel, setPanel] = useState<PanelId>(PanelId.NONE);
  const [notificationsToggle, notificationsToggleRef] = useController<NotificationsPanelDockToggleRef>();
  const [notificationsPanel, notificationsPanelRef] = useController<NotificationsPanelRef>();
  const extendedServices = useExtendedServices();

  const notificationsPanelTabNames = useMemo(() => {
    if (
      (props.workspaceFile.extension.toLowerCase() === "dmn" && isExpanded && mode === DmnRunnerMode.FORM) ||
      mode === DmnRunnerMode.TABLE
    ) {
      return [i18n.terms.validation, i18n.terms.execution];
    }
    return [i18n.terms.validation];
  }, [props.workspaceFile.extension, i18n.terms.validation, i18n.terms.execution, mode, isExpanded]);

  const isDmnTableMode = useMemo(
    () => mode === DmnRunnerMode.TABLE && props.workspaceFile.extension.toLowerCase() === "dmn",
    [mode, props.workspaceFile.extension]
  );

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
  }, [i18n, notificationsPanel, notificationsPanelTabNames, notificationsToggle]);

  const setNotifications = useCallback(
    (tabName: string, path: string, notifications: Notification[]) => {
      notificationsToggle?.setNewNotifications(tabName, { path, notifications });
      notificationsPanel?.getTab(tabName)?.kogitoNotifications_setNotifications(path, notifications);
    },
    [notificationsPanel, notificationsToggle]
  );

  const onToggle = useCallback(
    (panelId: PanelId) => {
      setPanel((previousPanel) => {
        if (previousPanel !== panelId) {
          if (panelId === PanelId.DMN_RUNNER_TABLE && !isExpanded) {
            dmnRunnerDispatcher({ type: DmnRunnerProviderActionType.DEFAULT, newState: { isExpanded: true } });
          }
          return panelId;
        }
        return PanelId.NONE;
      });
    },
    [isExpanded, dmnRunnerDispatcher]
  );

  useLayoutEffect(() => {
    if (mode === DmnRunnerMode.FORM && panel === PanelId.DMN_RUNNER_TABLE) {
      setPanel(PanelId.NONE);
    }
  }, [mode, panel]);

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

  const dockIsDisabled = useMemo(() => {
    return (
      (props.workspaceFile.extension.toLowerCase() === "dmn" ||
        props.workspaceFile.extension.toLowerCase() === "bpmn" ||
        props.workspaceFile.extension.toLowerCase() === "bpmn2") &&
      extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING
    );
  }, [extendedServices.status, props.workspaceFile.extension]);

  const dockIsDisabledReason = useMemo(() => {
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

  const toggleGroupItems = useMemo(() => {
    const items = [];

    if (isDmnTableMode) {
      items.push(
        <DmnRunnerDockToggle
          key="dmn-runner-toggle-item"
          isDisabled={dockIsDisabled}
          disabledReason={dockIsDisabledReason}
          isSelected={panel === PanelId.DMN_RUNNER_TABLE}
          onChange={(id) => onToggle(id)}
        />
      );
    }
    items.push(
      <NotificationsPanelDockToggle
        key="notifications-toggle-item"
        isDisabled={dockIsDisabled}
        disabledReason={dockIsDisabledReason}
        ref={notificationsToggleRef}
        isSelected={panel === PanelId.NOTIFICATIONS_PANEL}
        onChange={(id) => onToggle(id)}
      />
    );

    return items;
  }, [isDmnTableMode, dockIsDisabled, panel, dockIsDisabledReason, notificationsToggleRef, onToggle]);

  useEffect(() => {
    if (
      (extendedServices.status === KieSandboxExtendedServicesStatus.STOPPED ||
        extendedServices.status === KieSandboxExtendedServicesStatus.NOT_RUNNING) &&
      panel === PanelId.DMN_RUNNER_TABLE
    ) {
      setPanel(PanelId.NONE);
    }
  }, [extendedServices.status, panel, props.workspaceFile.relativePath]);

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
                {panel === PanelId.DMN_RUNNER_TABLE && isDmnTableMode && isExpanded && <DmnRunnerTable />}
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
        <ToggleGroup>{toggleGroupItems}</ToggleGroup>
      </div>
    </>
  );
});
