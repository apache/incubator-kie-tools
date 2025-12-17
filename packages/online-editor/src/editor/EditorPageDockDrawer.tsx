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

import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { ToggleGroup } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { NotificationsPanelRef } from "./NotificationsPanel/NotificationsPanel";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { PanelId, useEditorDockContext } from "./EditorPageDockContextProvider";
import { ErrorBoundary } from "../reactExt/ErrorBoundary";
import { DMN_RUNNER_TABLE_ROW_HEIGHT_IN_PX } from "../dmnRunner/DmnRunnerTable";

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

const PATTERNFLY_DRAWER_RESIZE_HANDLE_SIZE_IN_PX = 10;

export function EditorPageDockDrawer({ children }: React.PropsWithChildren<{}>) {
  const { panel, toggleGroupItems, panelContent, panelContentHeight, error, setHasError, errorBoundaryRef } =
    useEditorDockContext();

  const toggleGroup = useMemo(() => {
    return [...toggleGroupItems.entries()].map(([key, value]) => value).reverse();
  }, [toggleGroupItems]);

  const { maxSize, minSize, defaultSize, increment } = useMemo(() => {
    if (panel === PanelId.DMN_RUNNER_TABLE && panelContentHeight !== undefined) {
      const maxSize = 2 + panelContentHeight + PATTERNFLY_DRAWER_RESIZE_HANDLE_SIZE_IN_PX;
      return {
        maxSize: `${maxSize}px`,
        minSize: `${dmnRunnerTableSizeInPx({ rows: 3 })}px`,
        defaultSize: `${Math.min(maxSize, dmnRunnerTableSizeInPx({ rows: 6 }))}px`,
        increment: DMN_RUNNER_TABLE_ROW_HEIGHT_IN_PX,
      };
    } else {
      return {
        maxSize: "600px",
        minSize: "110px",
        defaultSize: "210px",
        increment: undefined,
      };
    }
  }, [panel, panelContentHeight]);

  return (
    <>
      <Drawer isInline={true} position={"bottom"} isExpanded={panel !== PanelId.NONE}>
        <DrawerContent
          panelContent={
            panelContent ? (
              <DrawerPanelContent
                isResizable={true}
                defaultSize={defaultSize}
                maxSize={maxSize}
                minSize={minSize}
                increment={increment}
              >
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

function dmnRunnerTableSizeInPx({ rows }: { rows: number }) {
  return 4 + PATTERNFLY_DRAWER_RESIZE_HANDLE_SIZE_IN_PX + DMN_RUNNER_TABLE_ROW_HEIGHT_IN_PX * rows; // 2px for border
}
