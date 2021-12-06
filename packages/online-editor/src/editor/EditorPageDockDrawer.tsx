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
import { DmnRunnerTabular } from "./DmnRunner/DmnRunnerTabular";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { WorkspaceFile } from "../workspace/WorkspacesContext";
import { DecisionResult } from "@kogito-tooling/form/dist/dmn";
import {
  NotificationsPanelDockToggle,
  NotificationsPanelDockToggleRef,
} from "./NotificationsPanel/NotificationsPanelDockToggle";
import { DmnRunnerDockToggle } from "./DmnRunner/DmnRunnerDockToggle";
import { useController } from "../reactExt/Hooks";
import { Notification } from "@kie-tooling-core/notifications/dist/api";
import { useKieToolingExtendedServices } from "../kieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../kieToolingExtendedServices/KieToolingExtendedServicesStatus";

export enum PanelId {
  DMN_RUNNER_TABULAR = "dmn-runner-tabular",
  NOTIFICATIONS_PANEL = "notifications-panel",
  NONE = "",
}

interface EditorPageDockDrawerProps {
  isEditorReady?: boolean;
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

  const notificationsPanelTabNames = useMemo(() => {
    if (
      props.workspaceFile.extension.toLowerCase() === "dmn" &&
      dmnRunnerState.isExpanded &&
      dmnRunnerState.mode === DmnRunnerMode.FORM
    ) {
      return [i18n.terms.validation, i18n.terms.execution];
    }
    return [i18n.terms.validation];
  }, [
    props.workspaceFile.extension,
    dmnRunnerState.isExpanded,
    dmnRunnerState.mode,
    i18n.terms.validation,
    i18n.terms.execution,
  ]);

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

  const onToggle = useCallback((panel: PanelId) => {
    setPanel((currentPanel) => {
      if (currentPanel !== panel) {
        return panel;
      }
      return PanelId.NONE;
    });
  }, []);

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

  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const notificationsPanelIsDisabled = useMemo(() => {
    return (
      props.workspaceFile.extension.toLowerCase() === "bpmn" ||
      (props.workspaceFile.extension.toLowerCase() === "dmn" &&
        kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING)
    );
  }, [kieToolingExtendedServices.status, props.workspaceFile.extension]);

  const notificationsPanelDisabledReason = useMemo(() => {
    if (props.workspaceFile.extension.toLowerCase() === "bpmn") {
      return "BPMN Editor doesn't have access to Problems tab";
    }
    if (
      props.workspaceFile.extension.toLowerCase() === "dmn" &&
      kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING
    ) {
      return "In order to have access to Problems tab you need to use the KIE Tooling Extended Services";
    }
    return "";
  }, [kieToolingExtendedServices.status, props.workspaceFile.extension]);

  const isDmnTableMode = useMemo(
    () => dmnRunnerState.mode === DmnRunnerMode.TABLE && props.workspaceFile.extension.toLowerCase() === "dmn",
    [dmnRunnerState.mode, props.workspaceFile.extension]
  );

  useEffect(() => {
    setPanel(PanelId.NONE);
  }, [props.workspaceFile.relativePath]);

  return (
    <>
      <Drawer isInline={true} position={"bottom"} isExpanded={panel !== PanelId.NONE}>
        <DrawerContent
          panelContent={
            panel !== PanelId.NONE && (
              <DrawerPanelContent style={{ height: "100%" }} isResizable={true}>
                {props.isEditorReady && (
                  <>
                    {panel === PanelId.NOTIFICATIONS_PANEL && (
                      <NotificationsPanel ref={notificationsPanelRef} tabNames={notificationsPanelTabNames} />
                    )}
                    {panel === PanelId.DMN_RUNNER_TABULAR && isDmnTableMode && (
                      <DmnRunnerTabular
                        setPanelOpen={setPanel}
                        isReady={props.isEditorReady}
                        dmnRunnerResults={dmnRunnerResults}
                        setDmnRunnerResults={setDmnRunnerResults}
                      />
                    )}
                  </>
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
            <DmnRunnerDockToggle isSelected={panel === PanelId.DMN_RUNNER_TABULAR} onChange={(id) => onToggle(id)} />
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
