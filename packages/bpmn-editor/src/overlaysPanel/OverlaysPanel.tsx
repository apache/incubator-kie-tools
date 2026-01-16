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
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Slider } from "@patternfly/react-core/dist/js/components/Slider";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../store/StoreContext";
import { useLayoutEffect, useRef } from "react";
import { useBpmnEditorI18n } from "../i18n";

const MIN_SNAP = 5;
const MAX_SNAP = 50;
const SNAP_STEP = 5;
const BOTTOM_MARGIN = 10;

interface OverlaysPanelProps {
  availableHeight?: number;
}

export function OverlaysPanel({ availableHeight }: OverlaysPanelProps) {
  const { i18n } = useBpmnEditorI18n();
  const snapGrid = useBpmnEditorStore((s) => s.xyFlowReactKieDiagram.snapGrid);
  const diagram = useBpmnEditorStore((s) => s.diagram);
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const overlayPanelContainer = useRef<HTMLDivElement>(null);
  useLayoutEffect(() => {
    if (overlayPanelContainer.current && availableHeight) {
      const bounds = overlayPanelContainer.current.getBoundingClientRect();
      const currentHeight = bounds.height;
      const yPos = bounds.y;
      if (currentHeight + yPos >= availableHeight) {
        overlayPanelContainer.current.style.height = availableHeight - BOTTOM_MARGIN + "px";
        overlayPanelContainer.current.style.overflowY = "scroll";
      } else {
        overlayPanelContainer.current.style.overflowY = "visible";
      }
    }
  });

  return (
    <div ref={overlayPanelContainer}>
      <Form
        onKeyDown={(e) => e.stopPropagation()} // Prevent ReactFlow KeyboardShortcuts from triggering when editing stuff on Overlays Panel
        style={{ width: "300px" }}
      >
        <FormGroup label={i18n.overlaysPanel.snapping}>
          <Switch
            aria-label={"Snapping"}
            isChecked={snapGrid.isEnabled}
            onChange={(e, newValue) =>
              bpmnEditorStoreApi.setState((state) => {
                state.xyFlowReactKieDiagram.snapGrid.isEnabled = newValue;
              })
            }
          />
        </FormGroup>
        <FormGroup label={i18n.overlaysPanel.horizontal}>
          <Slider
            data-testid={"kie-tools--bpmn-editor--horizontal-snapping-control"}
            className={"kie-bpmn-editor--snap-slider"}
            isDisabled={!snapGrid.isEnabled}
            value={snapGrid.x}
            min={MIN_SNAP}
            max={MAX_SNAP}
            isInputVisible={true}
            inputValue={snapGrid.x}
            step={SNAP_STEP}
            showTicks={true}
            hasTooltipOverThumb={true}
            onChange={(newSliderValue, newInputValue) =>
              bpmnEditorStoreApi.setState((state) => {
                state.xyFlowReactKieDiagram.snapGrid.x = Math.min(
                  MAX_SNAP,
                  Math.max(MIN_SNAP, newInputValue ?? newSliderValue)
                );
              })
            }
          />
        </FormGroup>
        <FormGroup label={i18n.overlaysPanel.vertical}>
          <Slider
            data-testid={"kie-tools--bpmn-editor--vertical-snapping-control"}
            className={"kie-bpmn-editor--snap-slider"}
            isDisabled={!snapGrid.isEnabled}
            value={snapGrid.y}
            min={MIN_SNAP}
            max={MAX_SNAP}
            isInputVisible={true}
            inputValue={snapGrid.y}
            step={SNAP_STEP}
            showTicks={true}
            hasTooltipOverThumb={true}
            onChange={(newSliderValue, newInputValue) =>
              bpmnEditorStoreApi.setState((state) => {
                state.xyFlowReactKieDiagram.snapGrid.y = Math.min(
                  MAX_SNAP,
                  Math.max(MIN_SNAP, newInputValue ?? newSliderValue)
                );
              })
            }
          />
        </FormGroup>
      </Form>
      <br />
      <Divider inset={{ default: "insetMd" }} />
      <br />
      <Form
        onKeyDown={(e) => e.stopPropagation()} // Prevent ReactFlow KeyboardShortcuts from triggering when editing stuff on Overlays Panel
      >
        <FormGroup label={i18n.enableStyles}>
          <Switch
            aria-label={"Enable styles"}
            isChecked={diagram.overlays.enableCustomNodeStyles}
            onChange={(e, newValue) =>
              bpmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableCustomNodeStyles = newValue;
              })
            }
          />
        </FormGroup>
      </Form>
    </div>
  );
}
