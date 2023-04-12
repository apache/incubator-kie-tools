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
import { useCallback, useImperativeHandle, useMemo, useState } from "react";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { EditorPageDockToggleItem } from "../EditorPageDockToggleItem";
import { PanelId, useEditorDockContext } from "../EditorPageDockContextProvider";

interface NotificationsWithPath {
  path: string;
  notifications: Notification[];
}

export interface NotificationsPanelDockToggleRef {
  getNotifications: () => Map<string, NotificationsWithPath>;
  setNewNotifications: (tabName: string, notificationsWithPath: NotificationsWithPath) => void;
  deleteNotificationsFromTab: (tabName: string) => void;
}

export const NotificationsPanelDockToggle = React.forwardRef<NotificationsPanelDockToggleRef, {}>(
  (props, forwardRef) => {
    const { isDisabled, panel, onTogglePanel } = useEditorDockContext();

    const [notificationsCount, setNotificationsCount] = useState<number>(0);
    const notifications = useMemo<Map<string, NotificationsWithPath>>(() => new Map(), []);

    const setNewNotifications = useCallback(
      (tabName: string, notificationsWithPath: NotificationsWithPath) => {
        notifications.set(tabName, notificationsWithPath);
        setNotificationsCount(
          Array.from(notifications.values()).reduce((count, { notifications }) => count + notifications.length, 0)
        );
      },
      [notifications]
    );

    const deleteNotificationsFromTab = useCallback(
      (tabName: string) => {
        notifications.delete(tabName);
        setNotificationsCount(
          Array.from(notifications.values()).reduce((count, { notifications }) => count + notifications.length, 0)
        );
      },
      [notifications]
    );

    useImperativeHandle(
      forwardRef,
      () => ({
        getNotifications: () => notifications,
        setNewNotifications,
        deleteNotificationsFromTab,
      }),
      [deleteNotificationsFromTab, notifications, setNewNotifications]
    );

    return (
      <EditorPageDockToggleItem>
        <NotificationsToggleItem
          notificationsCount={notificationsCount}
          isDisabled={isDisabled}
          isSelected={panel === PanelId.NOTIFICATIONS_PANEL}
          onChange={onTogglePanel}
        />
      </EditorPageDockToggleItem>
    );
  }
);

interface NotificationsToggleItemProps {
  notificationsCount: number;
  isSelected: boolean;
  onChange: (id: PanelId) => void;
  isDisabled: boolean;
}

function NotificationsToggleItem(props: NotificationsToggleItemProps) {
  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLElement>) => {
    e.preventDefault();
    e.stopPropagation();

    const updatedResult = document.getElementById(`total-notifications`);
    updatedResult?.classList.remove("kogito--editor__notifications-panel-error-count-updated");
  }, []);

  return (
    <ToggleGroupItem
      style={{
        borderLeft: "solid 1px",
        borderRadius: 0,
        borderColor: "rgb(211, 211, 211)",
        padding: "1px",
      }}
      isDisabled={props.isDisabled}
      buttonId={PanelId.NOTIFICATIONS_PANEL}
      isSelected={props.isSelected}
      onChange={() => {
        if (!props.isDisabled) {
          props.onChange(PanelId.NOTIFICATIONS_PANEL);
        }
      }}
      text={
        <div style={{ display: "flex" }}>
          <div style={{ paddingRight: "5px", width: "30px" }}>
            <ExclamationCircleIcon />
          </div>
          Problems
          <div style={{ paddingLeft: "5px", width: "30px" }}>
            {!props.isDisabled && (
              <span id={"total-notifications"} onAnimationEnd={onAnimationEnd}>
                {props.notificationsCount}
              </span>
            )}
          </div>
        </div>
      }
    />
  );
}
