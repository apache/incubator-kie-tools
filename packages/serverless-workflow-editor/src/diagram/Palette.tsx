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

import * as RF from "reactflow";
import * as React from "react";
import { useCallback } from "react";
import { NodeType } from "./connections/graphStructure";
import { NODE_TYPES } from "./nodes/SwfNodeTypes";
import { useSwfEditorStoreApi } from "../store/StoreContext";
import {
  EventStateIcon,
  OperationStateIcon,
  SwitchStateIcon,
  SleepStateIcon,
  ParallelStateIcon,
  InjectStateIcon,
  ForEachStateIcon,
  CallbackStateIcon,
} from "../icons/Icons";
import { useSettings } from "../settings/SwfEditorSettingsContext";

export const MIME_TYPE_FOR_SWF_EDITOR_NEW_NODE_FROM_PALETTE = "application/kie-swf-editor--new-node-from-palette";

export function Palette({ pulse }: { pulse: boolean }) {
  const onDragStart = useCallback((event: React.DragEvent, nodeType: NodeType) => {
    event.dataTransfer.setData(MIME_TYPE_FOR_SWF_EDITOR_NEW_NODE_FROM_PALETTE, nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  const settings = useSettings();
  const nodesPalletePopoverRef = React.useRef<HTMLDivElement>(null);

  const clearCurrentFocusToAllowDraggingNewNode = useCallback(() => {
    (document.activeElement as any)?.blur?.();
  }, []);

  return (
    <>
      {!settings.isReadOnly && (
        <RF.Panel
          position={"top-left"}
          style={{ marginTop: "15px" }}
          onMouseDownCapture={clearCurrentFocusToAllowDraggingNewNode}
        >
          <div ref={nodesPalletePopoverRef} style={{ position: "absolute", left: 0, height: 0, zIndex: -1 }} />
          <aside className={`kie-swf-editor--palette ${pulse ? "pulse" : ""}`}>
            <div
              title={"Inject State"}
              className={"kie-swf-editor--palette-button dndnode inject-state"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.injectState)}
              draggable={true}
            >
              <InjectStateIcon />
            </div>
            <div
              title={"Operation State"}
              className={"kie-swf-editor--palette-button dndnode operation-state"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.operationState)}
              draggable={true}
            >
              <OperationStateIcon />
            </div>
            <div
              title={"Event State"}
              className={"kie-swf-editor--palette-button dndnode event-state"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.eventState)}
              draggable={true}
            >
              <EventStateIcon />
            </div>
            <div
              title={"Parallel State"}
              className={"kie-swf-editor--palette-button dndnode parallel-state"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.parallelState)}
              draggable={true}
            >
              <ParallelStateIcon />
            </div>
            <div
              title={"Callback State"}
              className={"kie-swf-editor--palette-button dndnode callback-state"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.callbackState)}
              draggable={true}
            >
              <CallbackStateIcon />
            </div>
            <div
              title={"ForEach State"}
              className={"kie-swf-editor--palette-button dndnode foreach-state"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.foreachState)}
              draggable={true}
            >
              <ForEachStateIcon />
            </div>
            <div
              title={"Sleep State"}
              className={"kie-swf-editor--palette-button dndnode sleep-state"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.sleepState)}
              draggable={true}
            >
              <SleepStateIcon />
            </div>
            <div
              title={"Switch State"}
              className={"kie-swf-editor--palette-button dndnode switch-state"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.switchState)}
              draggable={true}
            >
              <SwitchStateIcon />
            </div>
          </aside>
          <br />
        </RF.Panel>
      )}
    </>
  );
}
