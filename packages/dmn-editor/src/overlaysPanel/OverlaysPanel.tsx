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
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { useLayoutEffect, useRef } from "react";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { useDmnEditorI18n } from "../i18n";

const MIN_SNAP = 5;
const MAX_SNAP = 50;
const SNAP_STEP = 5;
const BOTTOM_MARGIN = 10;

interface OverlaysPanelProps {
  availableHeight?: number;
}

export function OverlaysPanel({ availableHeight }: OverlaysPanelProps) {
  const { i18n } = useDmnEditorI18n();
  const diagram = useDmnEditorStore((s) => s.diagram);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const overlayPanelContainer = useRef<HTMLDivElement>(null);
  const { isEvaluationHighlightsSupported } = useSettings();
  useLayoutEffect(() => {
    if (overlayPanelContainer.current && availableHeight) {
      if (overlayPanelContainer.current.scrollHeight <= availableHeight) {
        overlayPanelContainer.current.style.overflowY = "hidden";
        overlayPanelContainer.current.style.height = "auto";
      } else if (
        overlayPanelContainer.current.style.height !== availableHeight - BOTTOM_MARGIN + "px" &&
        overlayPanelContainer.current.style.height !== "auto"
      ) {
        overlayPanelContainer.current.style.height = availableHeight - BOTTOM_MARGIN + "px";
        overlayPanelContainer.current.style.overflowY = "auto";
      }
    }
  }, [availableHeight]);

  return (
    <div ref={overlayPanelContainer}>
      <Form
        onKeyDown={(e) => e.stopPropagation()} // Prevent ReactFlow KeyboardShortcuts from triggering when editing stuff on Overlays Panel
      >
        <FormGroup label={i18n.overlaysPanel.snapping}>
          <Switch
            aria-label={"Snapping"}
            isChecked={diagram.snapGrid.isEnabled}
            onChange={(_event, newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.snapGrid.isEnabled = newValue;
              })
            }
          />
        </FormGroup>
        <FormGroup label={i18n.overlaysPanel.horizontal}>
          <Slider
            data-testid={"kie-tools--dmn-editor--horizontal-snapping-control"}
            className={"kie-dmn-editor--snap-slider"}
            isDisabled={!diagram.snapGrid.isEnabled}
            value={diagram.snapGrid.x}
            min={MIN_SNAP}
            max={MAX_SNAP}
            isInputVisible={true}
            inputValue={diagram.snapGrid.x}
            step={SNAP_STEP}
            showTicks={true}
            hasTooltipOverThumb={true}
            onChange={(_event, newSliderValue, newInputValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.snapGrid.x = Math.min(MAX_SNAP, Math.max(MIN_SNAP, newInputValue ?? newSliderValue));
              })
            }
          />
        </FormGroup>
        <FormGroup label={i18n.overlaysPanel.vertical}>
          <Slider
            data-testid={"kie-tools--dmn-editor--vertical-snapping-control"}
            className={"kie-dmn-editor--snap-slider"}
            isDisabled={!diagram.snapGrid.isEnabled}
            value={diagram.snapGrid.y}
            min={MIN_SNAP}
            max={MAX_SNAP}
            isInputVisible={true}
            inputValue={diagram.snapGrid.y}
            step={SNAP_STEP}
            showTicks={true}
            hasTooltipOverThumb={true}
            onChange={(_event, newSliderValue, newInputValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.snapGrid.y = Math.min(MAX_SNAP, Math.max(MIN_SNAP, newInputValue ?? newSliderValue));
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
        <FormGroup label={i18n.overlaysPanel.highlightSelectedNode}>
          <Switch
            aria-label={"Highlight selected node(s) hierarchy"}
            isChecked={diagram.overlays.enableNodeHierarchyHighlight}
            onChange={(_event, newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableNodeHierarchyHighlight = newValue;
              })
            }
          />
        </FormGroup>
        <FormGroup label={i18n.overlaysPanel.showDataTypeToolbar}>
          <Switch
            aria-label={"Show data type toolbar on nodes"}
            isChecked={diagram.overlays.enableDataTypesToolbarOnNodes}
            onChange={(_event, newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableDataTypesToolbarOnNodes = newValue;
              })
            }
          />
        </FormGroup>
        <FormGroup label={i18n.overlaysPanel.enableStyles}>
          <Switch
            aria-label={"Show data type toolbar on nodes"}
            isChecked={diagram.overlays.enableCustomNodeStyles}
            onChange={(_event, newValue) =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.overlays.enableCustomNodeStyles = newValue;
              })
            }
          />
        </FormGroup>
        {isEvaluationHighlightsSupported && (
          <FormGroup
            label={i18n.overlaysPanel.enableEvaluationHighlights}
            labelIcon={
              <Tooltip content={i18n.overlaysPanel.enableHighlightingDecisionTable}>
                <Icon size="sm" status="info">
                  <HelpIcon />
                </Icon>
              </Tooltip>
            }
          >
            <Switch
              data-testid={"kie-tools--dmn-editor--evaluation-highlights-control"}
              isChecked={diagram.overlays.enableEvaluationHighlights}
              onChange={(_event, newValue) =>
                dmnEditorStoreApi.setState((state) => {
                  state.diagram.overlays.enableEvaluationHighlights = newValue;
                })
              }
            />
          </FormGroup>
        )}
      </Form>
    </div>
  );
}
