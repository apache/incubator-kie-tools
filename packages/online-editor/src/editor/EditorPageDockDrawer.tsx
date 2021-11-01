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
import { PropsWithChildren, useCallback, useContext, useImperativeHandle, useMemo, useState } from "react";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { KeyboardIcon } from "@patternfly/react-icons/dist/js/icons/keyboard-icon";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunner/DmnRunnerStatus";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { useDmnRunner } from "./DmnRunner/DmnRunnerContext";
import { useOnlineI18n } from "../common/i18n";
import { NotificationsPanel, NotificationsPanelController } from "./NotificationsPanel/NotificationsPanel";
import { DmnRunnerTabular } from "./DmnRunner/DmnRunnerTabular";
import { GlobalContext } from "../common/GlobalContext";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { WorkspaceFile } from "../workspace/WorkspacesContext";

export enum PanelId {
  DMN_RUNNER_TABULAR = "dmn-runner-tabular",
  NOTIFICATIONS_PANEL = "notifications-panel",
  NONE = "",
}

export enum PanelPosition {
  NOTIFICATIONS_PANEL = 0,
  DMN_RUNNER_TABULAR = 1,
}

interface EditorPageDockDrawerProps {
  isReady?: boolean;
  workspaceFile: WorkspaceFile;
  notificationsPanel: NotificationsPanelController | undefined;
  notificationsPanelRef: (controller: NotificationsPanelController) => void;
}

export interface EditorPageDockDrawerController {
  open: (panel: PanelId) => void;
  close: () => void;
}

export const EditorPageDockDrawer = React.forwardRef<
  EditorPageDockDrawerController,
  PropsWithChildren<EditorPageDockDrawerProps>
>((props, forwardRef) => {
  const { i18n } = useOnlineI18n();
  const context = useContext(GlobalContext);
  const dmnRunner = useDmnRunner();
  const [panel, setPanel] = useState<PanelId>(PanelId.NONE);

  useImperativeHandle(
    forwardRef,
    () => ({
      open: (panel: PanelId) => setPanel(panel),
      close: () => setPanel(PanelId.NONE),
      // getTab: (name: string) => tabs.get(name)?.current ?? undefined,
      // setActiveTab,
    }),
    []
  );

  const notificationsPanelTabNames = useMemo(() => {
    if (props.workspaceFile.extension.toLowerCase() === "dmn") {
      return [i18n.terms.validation, i18n.terms.execution];
    }
    return [i18n.terms.validation];
  }, [props.workspaceFile, i18n, dmnRunner.status]);

  const panels = useMemo(() => {
    const panelMap = new Map([
      [
        PanelId.NOTIFICATIONS_PANEL,
        <NotificationsPanel
          key={PanelId.NOTIFICATIONS_PANEL}
          ref={props.notificationsPanelRef}
          tabNames={notificationsPanelTabNames}
        />,
      ],
    ]);
    if (dmnRunner.mode === DmnRunnerMode.TABULAR) {
      panelMap.set(
        PanelId.DMN_RUNNER_TABULAR,
        <DmnRunnerTabular key={PanelId.DMN_RUNNER_TABULAR} setPanelOpen={setPanel} isReady={props.isReady} />
      );
    }
    return panelMap;
  }, [props.notificationsPanelRef, props.isReady, notificationsPanelTabNames, dmnRunner.mode]);

  return (
    <>
      <Drawer isInline={true} position={"bottom"} isExpanded={panel !== PanelId.NONE}>
        <DrawerContent
          panelContent={
            <DrawerPanelContent isResizable={true}>{props.isReady && panels.get(panel)}</DrawerPanelContent>
          }
        >
          {props.children}
        </DrawerContent>
      </Drawer>
      <EditorPageDock notificationsPanel={props.notificationsPanel} setPanel={setPanel} />
    </>
  );
});

export interface ResizablePanelProperties {
  title: string;
  onClick: () => void;
  icon?: React.ReactNode;
  info?: React.ReactNode;
  position: number;
}

interface EditorPageDockProps {
  notificationsPanel: NotificationsPanelController | undefined;
  setPanel: React.Dispatch<React.SetStateAction<PanelId>>;
}

export function EditorPageDock(props: EditorPageDockProps) {
  const [currentTab, setCurrentTab] = useState<string>();
  const dmnRunner = useDmnRunner();

  const envelopeKeyboardIcon = useMemo(() => {
    const envelope = document.getElementById("kogito-iframe");
    if (envelope) {
      return (envelope as HTMLIFrameElement).contentDocument?.getElementById(
        "keyboard-shortcuts-icon"
      ) as HTMLButtonElement;
    }
  }, []);

  const onKeyboardIconClick = useCallback(() => {
    envelopeKeyboardIcon?.click();
  }, [envelopeKeyboardIcon]);

  const onChange = useCallback((id: string, callback?: () => void) => {
    setCurrentTab((previous: any) => {
      if (previous === id) {
        return undefined;
      }
      return id;
    });
    callback?.();
  }, []);

  const renderToggleItem = useCallback(
    (id: string, properties: ResizablePanelProperties) => {
      return (
        <ToggleGroupItem
          style={{
            borderLeft: "solid 1px",
            borderRadius: 0,
            borderColor: "rgb(211, 211, 211)",
            padding: "1px",
          }}
          key={id}
          buttonId={id}
          isSelected={currentTab === id}
          onChange={() => onChange(id, properties.onClick)}
          text={
            <div style={{ display: "flex" }}>
              {properties.icon && <div style={{ paddingRight: "5px", width: "30px" }}>{properties.icon}</div>}
              {properties.title}
              {properties.info && <div style={{ paddingLeft: "5px", width: "30px" }}>{properties.info}</div>}
            </div>
          }
        />
      );
    },
    [currentTab, onChange]
  );

  const onNotificationsPanelClick = useCallback(() => {
    props.setPanel((previousPanel) => {
      if (previousPanel !== PanelId.NOTIFICATIONS_PANEL) {
        return PanelId.NOTIFICATIONS_PANEL;
      }
      return PanelId.NONE;
    });
  }, [props.setPanel]);

  const totalNotifications = useMemo(
    () => props.notificationsPanel?.getTotalNotificationsCount(),
    [props.notificationsPanel]
  );

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLElement>) => {
    e.preventDefault();
    e.stopPropagation();

    const updatedResult = document.getElementById(`total-notifications`);
    updatedResult?.classList.remove("kogito--editor__notifications-panel-error-count-updated");
  }, []);

  const notificationPanelIcon = useMemo(() => <ExclamationCircleIcon />, []);
  const notificationPanelInfo = useMemo(() => {
    return (
      <span id={"total-notifications"} onAnimationEnd={onAnimationEnd}>
        {totalNotifications}
      </span>
    );
  }, [totalNotifications, onAnimationEnd]);

  const onDmnRunnerTabularClick = useCallback(() => {
    props.setPanel((previousPanel) => {
      if (previousPanel !== PanelId.DMN_RUNNER_TABULAR) {
        return PanelId.DMN_RUNNER_TABULAR;
      }
      return PanelId.NONE;
    });
  }, []);

  const dockProperties = useMemo(() => {
    const dockMap = new Map<PanelId, ResizablePanelProperties>([
      [
        PanelId.NOTIFICATIONS_PANEL,
        {
          title: "Notifications",
          onClick: onNotificationsPanelClick,
          icon: notificationPanelIcon,
          info: notificationPanelInfo,
          position: PanelPosition.NOTIFICATIONS_PANEL,
        },
      ],
    ]);
    if (dmnRunner.mode === DmnRunnerMode.TABULAR) {
      dockMap.set(PanelId.DMN_RUNNER_TABULAR, {
        title: "DMN Runner",
        onClick: onDmnRunnerTabularClick,
        position: PanelPosition.DMN_RUNNER_TABULAR,
      });
    }
    return dockMap;
  }, [
    dmnRunner.mode,
    notificationPanelIcon,
    notificationPanelInfo,
    onDmnRunnerTabularClick,
    onNotificationsPanelClick,
  ]);

  return (
    dockProperties && (
      <>
        <div onClick={onKeyboardIconClick} className={"kogito-tooling--keyboard-shortcuts-icon"}>
          <KeyboardIcon />
        </div>
        <div
          style={{
            borderTop: "rgb(221, 221, 221) solid 1px",
            width: "100%",
            display: "flex",
            justifyContent: "flex-end",
          }}
        >
          <ToggleGroup>
            {Array.from(dockProperties.entries())
              .sort(([, a], [, b]) => b.position - a.position)
              .map(([keys, values]) => renderToggleItem(keys, values))}
          </ToggleGroup>
        </div>
      </>
    )
  );
}
