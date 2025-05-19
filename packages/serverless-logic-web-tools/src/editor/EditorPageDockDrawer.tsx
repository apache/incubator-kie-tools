/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { ToggleGroup } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import * as React from "react";
import { PropsWithChildren, useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { useAppI18n } from "../i18n";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { NotificationsPanel, NotificationsPanelRef } from "./NotificationsPanel/NotificationsPanel";
import {
  NotificationsPanelDockToggle,
  NotificationsPanelDockToggleRef,
} from "./NotificationsPanel/NotificationsPanelDockToggle";

export enum PanelId {
  NOTIFICATIONS_PANEL = "notifications-panel",
  NONE = "",
}

interface EditorPageDockDrawerProps {
  isEditorReady?: boolean;
  onNotificationClick?: (notification: Notification) => void;
  workspaceFile: WorkspaceFile;
  isDisabled: boolean;
}

export interface EditorPageDockDrawerRef {
  open: (panelId: PanelId) => void;
  toggle: (panelId: PanelId) => void;
  close: () => void;
  getNotificationsPanel: () => NotificationsPanelRef | undefined;
  setNotifications: (
    tabName: string,
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    notifications: Notification[]
  ) => void;
}

export const EditorPageDockDrawer = React.forwardRef<
  EditorPageDockDrawerRef,
  PropsWithChildren<EditorPageDockDrawerProps>
>((props, forwardRef) => {
  const { i18n } = useAppI18n();
  const [panel, setPanel] = useState<PanelId>(PanelId.NONE);
  const [notificationsToggle, notificationsToggleRef] = useController<NotificationsPanelDockToggleRef>();
  const [notificationsPanel, notificationsPanelRef] = useController<NotificationsPanelRef>();

  const notificationsPanelTabNames = useMemo(() => [i18n.terms.validation], [i18n.terms.validation]);

  useEffect(() => {
    if (!notificationsPanelTabNames.includes(i18n.terms.evaluation)) {
      notificationsToggle?.deleteNotificationsFromTab(i18n.terms.evaluation);
    }
    if (notificationsPanel && notificationsToggle) {
      const notifications = notificationsToggle.getNotifications();
      notifications.forEach((value, tabName) => {
        notificationsPanel.getTab(tabName)?.kogitoNotifications_setNotifications(value.path, value.notifications);
      });
    }
  }, [i18n.terms.evaluation, notificationsPanel, notificationsPanelTabNames, notificationsToggle]);

  const onToggle = useCallback((panel: PanelId) => {
    setPanel((currentPanel) => {
      if (currentPanel !== panel) {
        return panel;
      }
      return PanelId.NONE;
    });
  }, []);

  const setNotifications = useCallback(
    (tabName: string, normalizedPosixPathRelativeToTheWorkspaceRoot: string, notifications: Notification[]) => {
      notificationsToggle?.setNewNotifications(tabName, {
        path: normalizedPosixPathRelativeToTheWorkspaceRoot,
        notifications,
      });
      notificationsPanel
        ?.getTab(tabName)
        ?.kogitoNotifications_setNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot, notifications);
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

  const notificationsPanelDisabledReason = useMemo(() => {
    if (props.isDisabled) {
      return "This tab is not supported for this editor";
    }
    return "";
  }, [props.isDisabled]);

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
                      <NotificationsPanel
                        ref={notificationsPanelRef}
                        tabNames={notificationsPanelTabNames}
                        onNotificationClick={props.onNotificationClick}
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
          <NotificationsPanelDockToggle
            isDisabled={props.isDisabled}
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
