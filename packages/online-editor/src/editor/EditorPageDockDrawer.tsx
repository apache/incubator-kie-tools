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
import { useMemo } from "react";
import { ToggleGroup } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { NotificationsPanelRef } from "./NotificationsPanel/NotificationsPanel";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { PanelId, useEditorDockContext } from "./EditorPageDockContextProvider";
import { ErrorBoundary } from "../reactExt/ErrorBoundary";

export interface EditorPageDockDrawerRef {
  open: (panelId: PanelId) => void;
  toggle: (panelId: PanelId) => void;
  close: () => void;
  getNotificationsPanel: () => NotificationsPanelRef | undefined;
  setNotifications: (tabName: string, path: string, notifications: Notification[]) => void;
}

export function EditorPageDockDrawer({ children }: React.PropsWithChildren<{}>) {
  const { panel, toggleGroupItems, panelContent, error, setHasError, errorBoundaryRef } = useEditorDockContext();

  const toggleGroup = useMemo(() => {
    return [...toggleGroupItems.entries()].map(([key, value]) => value).reverse();
  }, [toggleGroupItems]);

  return (
    <>
      <Drawer isInline={true} position={"bottom"} isExpanded={panel !== PanelId.NONE}>
        <DrawerContent
          panelContent={
            panelContent ? (
              <DrawerPanelContent style={{ height: "100%" }} isResizable={true}>
                <ErrorBoundary ref={errorBoundaryRef} error={error} setHasError={setHasError}>
                  {panelContent}
                </ErrorBoundary>
              </DrawerPanelContent>
            ) : undefined
          }
        >
          {children}
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
        <ToggleGroup>{toggleGroup}</ToggleGroup>
      </div>
    </>
  );
}
