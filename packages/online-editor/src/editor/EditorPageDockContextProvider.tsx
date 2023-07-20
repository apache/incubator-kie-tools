/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useState, useRef } from "react";
import {
  NotificationsPanelDockToggle,
  NotificationsPanelDockToggleRef,
} from "./NotificationsPanel/NotificationsPanelDockToggle";
import { NotificationsPanel, NotificationsPanelRef } from "./NotificationsPanel/NotificationsPanel";
import { useOnlineI18n } from "../i18n";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceFile, WorkspacesContextType } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useExtendedServices } from "../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../extendedServices/ExtendedServicesStatus";
import { useFileValidation } from "./Validation";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";
import { DmnRunnerTable } from "../dmnRunner/DmnRunnerTable";
import { ErrorBoundary } from "../reactExt/ErrorBoundary";
import { DmnRunnerErrorBoundary } from "../dmnRunner/DmnRunnerErrorBoundary";

interface EditorPageDockContextType {
  panel: PanelId;
  isDisabled: boolean;
  disabledReason: string;
  onTogglePanel: (panelId: PanelId) => void;
  onOpenPanel: (panelId: PanelId) => void;
  setNotifications: (tabName: string, path: string, notifications: Notification[]) => void;
  addToggleItem: (panelId: PanelId, newItem: JSX.Element) => void;
  removeToggleItem: (panelId: PanelId) => void;
  toggleGroupItems: Map<PanelId, JSX.Element>;
  panelContent?: JSX.Element;
  notificationsPanel?: NotificationsPanelRef;
  error: boolean;
  setHasError: React.Dispatch<React.SetStateAction<boolean>>;
  errorBoundaryRef: React.MutableRefObject<ErrorBoundary | null>;
}

export const EditorPageDockContext = React.createContext<EditorPageDockContextType>({} as any);

export function useEditorDockContext() {
  return useContext(EditorPageDockContext);
}

export enum PanelId {
  DMN_RUNNER_TABLE = "dmn-runner-table",
  NOTIFICATIONS_PANEL = "notifications-panel",
  NONE = "",
}

interface Props {
  workspaceFile: WorkspaceFile;
  workspaces: WorkspacesContextType;
  dmnLanguageService?: DmnLanguageService;
  isEditorReady: boolean;
  editorValidate?: () => Promise<Notification[]>;
}

export function EditorPageDockContextProvider({
  children,
  dmnLanguageService,
  workspaces,
  workspaceFile,
  isEditorReady,
  editorValidate,
}: React.PropsWithChildren<Props>) {
  const { i18n } = useOnlineI18n();
  const [notificationsToggle, notificationsToggleRef] = useController<NotificationsPanelDockToggleRef>();
  const [notificationsPanel, notificationsPanelRef] = useController<NotificationsPanelRef>();
  const { status: extendedServicesStatus } = useExtendedServices();
  const errorBoundaryRef = useRef<ErrorBoundary>(null);
  const [error, setHasError] = useState<boolean>(false);
  const [panel, setPanel] = useState<PanelId>(PanelId.NONE);
  const [toggleGroupItems, setToggleGroupItem] = useState(
    new Map([
      [
        PanelId.NOTIFICATIONS_PANEL,
        <NotificationsPanelDockToggle key="notifications-toggle-item" ref={notificationsToggleRef} />,
      ],
    ])
  );

  const notificationsPanelTabNames = useMemo(() => {
    if (workspaceFile.extension.toLowerCase() === "dmn") {
      return [i18n.terms.validation, i18n.terms.execution];
    }
    return [i18n.terms.validation];
  }, [workspaceFile.extension, i18n.terms.validation, i18n.terms.execution]);

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

  const isDisabled = useMemo(() => {
    return (
      (workspaceFile.extension.toLowerCase() === "dmn" ||
        workspaceFile.extension.toLowerCase() === "bpmn" ||
        workspaceFile.extension.toLowerCase() === "bpmn2") &&
      extendedServicesStatus !== ExtendedServicesStatus.RUNNING
    );
  }, [extendedServicesStatus, workspaceFile.extension]);

  const disabledReason = useMemo(() => {
    if (
      (workspaceFile.extension.toLowerCase() === "dmn" ||
        workspaceFile.extension.toLowerCase() === "bpmn" ||
        workspaceFile.extension.toLowerCase() === "bpmn2") &&
      extendedServicesStatus !== ExtendedServicesStatus.RUNNING
    ) {
      return "In order to have access to Problems tab you need to use the Extended Services";
    }
    return "";
  }, [extendedServicesStatus, workspaceFile.extension]);

  useEffect(() => {
    if (
      (extendedServicesStatus === ExtendedServicesStatus.STOPPED ||
        extendedServicesStatus === ExtendedServicesStatus.NOT_RUNNING) &&
      panel === PanelId.DMN_RUNNER_TABLE
    ) {
      setPanel(PanelId.NONE);
    }
  }, [extendedServicesStatus, panel]);

  const onTogglePanel = useCallback((panelId: PanelId) => {
    setPanel((previousPanel) => {
      if (previousPanel !== panelId) {
        return panelId;
      }
      return PanelId.NONE;
    });
  }, []);

  const onOpenPanel = useCallback((panelId: PanelId) => {
    setPanel(panelId);
  }, []);

  useFileValidation(workspaces, workspaceFile, setNotifications, dmnLanguageService);

  const panelContent = useMemo(() => {
    switch (panel) {
      case PanelId.NOTIFICATIONS_PANEL:
        return <NotificationsPanel ref={notificationsPanelRef} tabNames={notificationsPanelTabNames} />;
      case PanelId.DMN_RUNNER_TABLE:
        return (
          <DmnRunnerErrorBoundary>
            <DmnRunnerTable />
          </DmnRunnerErrorBoundary>
        );
      default:
        return undefined;
    }
  }, [notificationsPanelRef, notificationsPanelTabNames, panel]);

  const addToggleItem = useCallback((panelId: PanelId, newItem: JSX.Element) => {
    setToggleGroupItem((previousToggleGroupItems) => {
      const newPreviousToggleItems = new Map(previousToggleGroupItems);
      newPreviousToggleItems.set(panelId, newItem);
      return newPreviousToggleItems;
    });
  }, []);

  const removeToggleItem = useCallback((panelId: PanelId) => {
    setToggleGroupItem((previousToggleGroupItems) => {
      const newPreviousToggleItems = new Map(previousToggleGroupItems);
      newPreviousToggleItems.delete(panelId);
      return newPreviousToggleItems;
    });
  }, []);

  // Required by PMML editor. Changing between files will not update the problems notifications tab;
  useEffect(() => {
    if (
      workspaceFile.extension === "dmn" ||
      workspaceFile.extension === "bpmn" ||
      workspaceFile.extension === "bpmn2" ||
      !isEditorReady
    ) {
      return;
    }

    //FIXME: Removing this timeout makes the notifications not work some times. Need to investigate.
    setTimeout(() => {
      editorValidate?.().then((notifications) => {
        setNotifications(
          i18n.terms.validation,
          "",
          // Removing the notification path so that we don't group it by path, as we're only validating one file.
          Array.isArray(notifications) ? notifications.map((n) => ({ ...n, path: "" })) : []
        );
      });
    }, 200);
  }, [workspaceFile, editorValidate, isEditorReady, i18n.terms.validation, setNotifications]);

  return (
    <EditorPageDockContext.Provider
      value={{
        panel,
        isDisabled,
        disabledReason,
        toggleGroupItems,
        panelContent,
        notificationsPanel,
        error,
        errorBoundaryRef,

        addToggleItem,
        removeToggleItem,
        onTogglePanel,
        onOpenPanel,
        setNotifications,
        setHasError,
      }}
    >
      {children}
    </EditorPageDockContext.Provider>
  );
}
