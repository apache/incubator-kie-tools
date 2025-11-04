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
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { VirtualMachineIcon } from "@patternfly/react-icons/dist/js/icons/virtual-machine-icon";
import { useCallback, useLayoutEffect } from "react";
import * as RF from "reactflow";
import { OverlaysPanel } from "../overlaysPanel/OverlaysPanel";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../store/StoreContext";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { useBpmnEditorI18n } from "../i18n";

const AREA_ABOVE_OVERLAYS_PANEL = 120;

export interface TopRightCornerPanelsProps {
  availableHeight?: number | undefined;
}

export function TopRightCornerPanels({ availableHeight }: TopRightCornerPanelsProps) {
  const { i18n } = useBpmnEditorI18n();
  const diagram = useBpmnEditorStore((s) => s.diagram);
  const propertiesPanel = useBpmnEditorStore((s) => s.propertiesPanel);
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const togglePropertiesPanel = useCallback(() => {
    bpmnEditorStoreApi.setState((state) => {
      state.propertiesPanel.isOpen = !state.propertiesPanel.isOpen;
    });
  }, [bpmnEditorStoreApi]);

  const toggleOverlaysPanel = useCallback(() => {
    bpmnEditorStoreApi.setState((state) => {
      state.diagram.overlaysPanel.isOpen = !state.diagram.overlaysPanel.isOpen;
    });
  }, [bpmnEditorStoreApi]);

  useLayoutEffect(() => {
    bpmnEditorStoreApi.setState((state) => {
      if (state.diagram.overlaysPanel.isOpen) {
        // This is necessary to make sure that the Popover is open at the correct position.
        setTimeout(() => {
          bpmnEditorStoreApi.setState((state) => {
            state.diagram.overlaysPanel.isOpen = true;
          });
        }, 300); // That's the animation duration to open/close the properties panel.
      }
      state.diagram.overlaysPanel.isOpen = false;
    });
  }, [bpmnEditorStoreApi, propertiesPanel.isOpen]);

  return (
    <>
      <RF.Panel position={"top-right"} style={{ display: "flex" }}>
        <aside className={"kie-bpmn-editor--overlays-panel-toggle"}>
          <Popover
            className={"kie-bpmn-editor--overlay-panel-popover"}
            key={`${diagram.overlaysPanel.isOpen}`}
            aria-label="Overlays Panel"
            position={"bottom-end"}
            enableFlip={false}
            flipBehavior={["bottom-end"]}
            hideOnOutsideClick={false}
            shouldClose={toggleOverlaysPanel}
            isVisible={diagram.overlaysPanel.isOpen}
            bodyContent={<OverlaysPanel availableHeight={(availableHeight ?? 0) - AREA_ABOVE_OVERLAYS_PANEL} />}
          >
            <button
              className={"kie-bpmn-editor--overlays-panel-toggle-button"}
              onClick={toggleOverlaysPanel}
              title={i18n.bpmnTopRightPanels.overlays}
            >
              <Icon size={"sm"}>
                <VirtualMachineIcon />
              </Icon>
            </button>
          </Popover>
        </aside>
        {!propertiesPanel.isOpen && (
          <aside className={"kie-bpmn-editor--properties-panel-toggle"}>
            <button
              className={"kie-bpmn-editor--properties-panel-toggle-button"}
              onClick={togglePropertiesPanel}
              title={i18n.bpmnTopRightPanels.propertiesPanel}
            >
              <Icon size={"sm"}>
                <InfoIcon />
              </Icon>
            </button>
          </aside>
        )}
      </RF.Panel>
    </>
  );
}
