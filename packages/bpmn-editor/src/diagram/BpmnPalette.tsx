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
import { useCallback, useState, useEffect, useRef, useMemo } from "react";
import * as RF from "reactflow";
import {
  BpmnNodeElement,
  BpmnNodeType,
  DEFAULT_NODE_SIZES,
  elementToNodeType,
  MIN_NODE_SIZES,
  NODE_TYPES,
} from "./BpmnDiagramDomain";
import {
  CallActivityIcon,
  DataObjectIcon,
  EndEventIcon,
  GatewayIcon,
  GroupIcon,
  IntermediateCatchEventIcon,
  IntermediateThrowEventIcon,
  LaneIcon,
  StartEventIcon,
  SubProcessIcon,
  TaskIcon,
  TextAnnotationIcon,
} from "./nodes/NodeIcons";
import { CodeIcon } from "@patternfly/react-icons/dist/js/icons/code-icon";
import { PeopleCarryIcon } from "@patternfly/react-icons/dist/js/icons/people-carry-icon";
import { ServicesIcon } from "@patternfly/react-icons/dist/js/icons/services-icon";
import { EllipsisHIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-h-icon";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../store/StoreContext";
import { BpmnDiagramLhsPanel } from "../store/Store";
import { addOrGetProcessAndDiagramElements } from "../mutations/addOrGetProcessAndDiagramElements";
import { Correlations } from "../propertiesPanel/correlations/Correlations";
import { Variables } from "../propertiesPanel/variables/Variables";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStateBody,
  EmptyStateActions,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { addVariable } from "../mutations/addVariable";
import { snapShapeDimensions } from "@kie-tools/xyflow-react-kie-diagram/dist/snapgrid/SnapGrid";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { NODE_LAYERS } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/Hooks";
import { getNewNodeDefaultName } from "../mutations/addStandaloneNode";
import { useCustomTasks } from "../customTasks/BpmnEditorCustomTasksContextProvider";
import { CustomTasksPalette } from "../customTasks/CustomTasksPalette";
import "./BpmnPalette.css";
import { PropertiesManager } from "../propertiesPanel/propertiesManager/PropertiesManager";
import { useBpmnEditorI18n } from "../i18n";

export const MIME_TYPE_FOR_BPMN_EDITOR_NEW_NODE_FROM_PALETTE = "application/kie-bpmn-editor--new-node-from-palette";

export const ELEMENT_TYPES = Object.fromEntries(Object.keys(elementToNodeType).map((k) => [k, k])) as {
  [K in keyof typeof elementToNodeType]: K;
};

const VIEWPORT_PADDING = 20;

function calculateVisibleIconCount(
  panelRect: DOMRect,
  paletteRect: DOMRect,
  viewportHeight: number,
  iconHeight: number,
  ellipsisHeight: number,
  totalIcons: number
): number {
  const effectiveViewportHeight = viewportHeight - VIEWPORT_PADDING;
  const currentPaletteHeight = paletteRect.height;
  const fullPaletteHeight = totalIcons * iconHeight;

  const heightDifference = fullPaletteHeight - currentPaletteHeight;
  const panelBottomIfAllVisible = panelRect.bottom + heightDifference;

  if (panelBottomIfAllVisible > effectiveViewportHeight) {
    const availableSpace = effectiveViewportHeight - (panelRect.bottom - currentPaletteHeight);
    const spaceForIcons = availableSpace - ellipsisHeight;

    const iconsThatFit = Math.floor(spaceForIcons / iconHeight);

    return Math.max(1, Math.min(totalIcons - 1, iconsThatFit));
  }

  return totalIcons;
}

export function BpmnPalette({ pulse }: { pulse: boolean }) {
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const { i18n } = useBpmnEditorI18n();

  const panelRef = useRef<HTMLDivElement>(null);
  const paletteRef = useRef<HTMLDivElement>(null);
  const submenuRef = useRef<HTMLDivElement>(null);
  const iconMeasureRef = useRef<HTMLDivElement>(null);
  const ellipsisButtonRef = useRef<HTMLButtonElement>(null);

  // Palette groups structure: groups with >2 items can have submenu, ≤2 items always visible
  const paletteGroups = useMemo(
    () => ({
      corePaletteGroup: {
        id: "corePaletteGroup",
        icons: [
          {
            id: 1,
            title: i18n.bpmnPalette.startEvents,
            className: "start-event",
            Icon: StartEventIcon,
            nodeType: NODE_TYPES.startEvent,
            element: ELEMENT_TYPES.startEvent,
          },
          {
            id: 2,
            title: i18n.bpmnPalette.intermediateCatchEvents,
            className: "intermediate-catch-event",
            Icon: IntermediateCatchEventIcon,
            nodeType: NODE_TYPES.intermediateCatchEvent,
            element: ELEMENT_TYPES.intermediateCatchEvent,
          },
          {
            id: 3,
            title: i18n.bpmnPalette.intermediateThrowEvents,
            className: "intermediate-throw-event",
            Icon: IntermediateThrowEventIcon,
            nodeType: NODE_TYPES.intermediateThrowEvent,
            element: ELEMENT_TYPES.intermediateThrowEvent,
          },
          {
            id: 4,
            title: i18n.bpmnPalette.endEvents,
            className: "end-event",
            Icon: EndEventIcon,
            nodeType: NODE_TYPES.endEvent,
            element: ELEMENT_TYPES.endEvent,
          },
          {
            id: 5,
            title: i18n.bpmnPalette.tasks,
            className: "task",
            Icon: TaskIcon,
            nodeType: NODE_TYPES.task,
            element: ELEMENT_TYPES.task,
          },
          {
            id: 6,
            title: i18n.bpmnPalette.callActivity,
            className: "callActivity",
            Icon: CallActivityIcon,
            nodeType: NODE_TYPES.task,
            element: ELEMENT_TYPES.callActivity,
          },
          {
            id: 7,
            title: i18n.bpmnPalette.subProcesses,
            className: "subProcess",
            Icon: SubProcessIcon,
            nodeType: NODE_TYPES.subProcess,
            element: ELEMENT_TYPES.subProcess,
          },
          {
            id: 8,
            title: i18n.bpmnPalette.gateways,
            className: "gateway",
            Icon: GatewayIcon,
            nodeType: NODE_TYPES.gateway,
            element: ELEMENT_TYPES.exclusiveGateway,
          },
          {
            id: 9,
            title: i18n.bpmnPalette.lanes,
            className: "lane",
            Icon: LaneIcon,
            nodeType: NODE_TYPES.lane,
            element: ELEMENT_TYPES.lane,
          },
        ],
      },
    }),
    [i18n]
  );

  // Flatten all group icons for current single-group rendering
  const paletteIcons = useMemo(() => Object.values(paletteGroups).flatMap((group) => group.icons), [paletteGroups]);
  const totalIcons = useMemo(() => paletteIcons.length, [paletteIcons]);

  const [visibleIconCount, setVisibleIconCount] = useState<number>(() => totalIcons);
  const [openGroupSubmenu, setOpenGroupSubmenu] = useState<string | null>(null);

  const iconHeightRef = useRef(40);
  const ellipsisHeightRef = useRef(40);

  useEffect(() => {
    if (iconMeasureRef.current) {
      const rect = iconMeasureRef.current.getBoundingClientRect();
      if (rect.height) {
        iconHeightRef.current = rect.height;
      }
    }

    if (ellipsisButtonRef.current) {
      const rect = ellipsisButtonRef.current.getBoundingClientRect();
      if (rect.height) {
        ellipsisHeightRef.current = rect.height;
      }
    }
  }, [visibleIconCount]);

  const showSubmenu = visibleIconCount < totalIcons;

  useEffect(() => {
    let animationFrameId: number | null = null;
    let lastCount: number = totalIcons;

    const updateVisibleIcons = () => {
      if (animationFrameId !== null) cancelAnimationFrame(animationFrameId);

      animationFrameId = requestAnimationFrame(() => {
        if (!panelRef.current || !paletteRef.current) {
          if (lastCount !== totalIcons) {
            setVisibleIconCount(totalIcons);
            lastCount = totalIcons;
          }
          return;
        }

        const newCount = calculateVisibleIconCount(
          panelRef.current.getBoundingClientRect(),
          paletteRef.current.getBoundingClientRect(),
          window.innerHeight,
          iconHeightRef.current,
          ellipsisHeightRef.current,
          totalIcons
        );

        if (newCount !== lastCount) {
          setVisibleIconCount(newCount);
          lastCount = newCount;
        }
      });
    };

    updateVisibleIcons();
    window.addEventListener("resize", updateVisibleIcons);

    return () => {
      window.removeEventListener("resize", updateVisibleIcons);
      if (animationFrameId !== null) cancelAnimationFrame(animationFrameId);
    };
  }, [totalIcons]);

  const onDragStart = useCallback(
    <T extends BpmnNodeType>(
      event: React.DragEvent,
      nodeType: T,
      element: keyof typeof elementToNodeType /** This type could be better, filtering only the elements matching `nodeType` */,
      bpmnElement?: BpmnNodeElement
    ) => {
      event.dataTransfer.setData(
        MIME_TYPE_FOR_BPMN_EDITOR_NEW_NODE_FROM_PALETTE,
        JSON.stringify({ nodeType, element })
      );
      event.dataTransfer.effectAllowed = "move";

      // Remove default effect of dragging elements.
      const transparentDiv = document.createElement("div");
      transparentDiv.style.width = "1px";
      transparentDiv.style.height = "1px";
      transparentDiv.style.opacity = "0";
      document.body.appendChild(transparentDiv);
      event.dataTransfer.setDragImage(transparentDiv, 0, 0);
      setTimeout(() => {
        document.body.removeChild(transparentDiv);
      }, 0);

      bpmnEditorStoreApi.setState((s) => {
        const snapGrid = s.xyFlowReactKieDiagram.snapGrid;
        const bpmnShape = {
          "@_id": generateUuid(),
          "dc:Bounds": {
            ...DEFAULT_NODE_SIZES[nodeType]({ snapGrid }),
            "@_x": 0,
            "@_y": 0,
          },
        };

        const position = { x: bpmnShape["dc:Bounds"]["@_x"], y: bpmnShape["dc:Bounds"]["@_y"] };
        const dimensions = snapShapeDimensions(snapGrid, bpmnShape, MIN_NODE_SIZES[nodeType]({ snapGrid }));

        s.xyFlowReactKieDiagram.newNodeProjection = {
          id: generateUuid(),
          type: nodeType,
          hidden: true,
          position,
          width: dimensions.width,
          height: dimensions.height,
          data: {
            parentXyFlowNode: undefined,
            shape: bpmnShape,
            shapeIndex: -1,
            bpmnElement: bpmnElement ?? {
              "@_id": generateUuid(),
              "@_name": getNewNodeDefaultName({ type: nodeType, element }),
              __$$element: element as any,
            },
          },
          zIndex: NODE_LAYERS.ATTACHED_NODES,
          style: {
            width: dimensions.width,
            height: dimensions.height,
            zIndex: NODE_LAYERS.ATTACHED_NODES,
          },
        };
      });
    },
    [bpmnEditorStoreApi]
  );

  const onDragEnd = useCallback(() => {
    // Makes sure there's no leftovers even when user pressed Esc during drag.
    bpmnEditorStoreApi.setState((s) => (s.xyFlowReactKieDiagram.newNodeProjection = undefined));
  }, [bpmnEditorStoreApi]);

  const nodesPalletePopoverRef = React.useRef<HTMLDivElement>(null);

  const openLhsPanel = useBpmnEditorStore((s) => s.diagram.openLhsPanel);

  const process = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement?.find((s) => s.__$$element === "process")
  );

  const { customTasks, customTasksPaletteIcon } = useCustomTasks();

  const renderPaletteIcon = useCallback(
    (icon: (typeof paletteIcons)[number]) => {
      const { Icon, title, className, nodeType, element } = icon;
      return (
        <div
          key={icon.id}
          title={title}
          className={`kie-bpmn-editor--palette-button dndnode ${className}`}
          onDragStart={(event) => onDragStart(event, nodeType, element)}
          onDragEnd={onDragEnd}
          draggable={true}
        >
          <Icon />
        </div>
      );
    },
    [onDragStart, onDragEnd]
  );

  return (
    <>
      <RF.Panel position={"top-left"} className={"kie-bpmn-editor--top-left-panel"}>
        <div
          ref={panelRef}
          style={{ position: "absolute", top: 0, left: 0, width: "100%", height: "100%", pointerEvents: "none" }}
        />
        <div ref={nodesPalletePopoverRef} style={{ position: "absolute", left: 0, height: 0, zIndex: -1 }} />
        <aside
          className={"kie-bpmn-editor--variables-panel-toggle"}
          style={{ position: "relative", pointerEvents: "all" }}
        >
          {openLhsPanel === BpmnDiagramLhsPanel.VARIABLES && (
            <div className={"kie-bpmn-editor--palette-nodes-popover variables"}>
              <Variables p={process} EmptyState={VariablesEmptyState} />
            </div>
          )}
          {openLhsPanel === BpmnDiagramLhsPanel.CUSTOM_TASKS && (
            <div className={"kie-bpmn-editor--palette-nodes-popover custom-tasks"}>
              <CustomTasksPalette onDragStart={onDragStart} />
            </div>
          )}
          <button
            title={i18n.bpmnPalette.processVariables}
            className={`kie-bpmn-editor--variables-panel-toggle-button ${openLhsPanel === BpmnDiagramLhsPanel.VARIABLES ? "active" : ""}`}
            onClick={() => {
              bpmnEditorStoreApi.setState((s) => {
                s.diagram.openLhsPanel =
                  s.diagram.openLhsPanel === BpmnDiagramLhsPanel.VARIABLES
                    ? BpmnDiagramLhsPanel.NONE
                    : BpmnDiagramLhsPanel.VARIABLES;
              });
            }}
          >
            <Icon size={"sm"}>
              <CodeIcon />
            </Icon>
          </button>
        </aside>

        <aside
          className={"kie-bpmn-editor--variables-panel-toggle"}
          style={{ position: "relative", pointerEvents: "all" }}
        >
          {openLhsPanel === BpmnDiagramLhsPanel.CORRELATIONS && (
            <div className={"kie-bpmn-editor--palette-nodes-popover correlations"}>
              <Correlations />
            </div>
          )}
          <button
            title={i18n.bpmnPalette.correlations}
            className={`kie-bpmn-editor--variables-panel-toggle-button ${openLhsPanel === BpmnDiagramLhsPanel.CORRELATIONS ? "active" : ""}`}
            onClick={() => {
              bpmnEditorStoreApi.setState((s) => {
                s.diagram.openLhsPanel =
                  s.diagram.openLhsPanel === BpmnDiagramLhsPanel.CORRELATIONS
                    ? BpmnDiagramLhsPanel.NONE
                    : BpmnDiagramLhsPanel.CORRELATIONS;
              });
            }}
          >
            <Icon size={"sm"}>
              <PeopleCarryIcon />
            </Icon>
          </button>
        </aside>

        <aside
          className={"kie-bpmn-editor--variables-panel-toggle"}
          style={{ position: "relative", pointerEvents: "all" }}
        >
          {openLhsPanel === BpmnDiagramLhsPanel.PROPERTIES_MANAGEMENT && (
            <div className={"kie-bpmn-editor--palette-nodes-popover properties-manager"}>
              <PropertiesManager p={process} />
            </div>
          )}
          <button
            title={i18n.bpmnPalette.propertiesManagement}
            className={`kie-bpmn-editor--variables-panel-toggle-button ${openLhsPanel === BpmnDiagramLhsPanel.PROPERTIES_MANAGEMENT ? "active" : ""}`}
            onClick={() => {
              bpmnEditorStoreApi.setState((s) => {
                s.diagram.openLhsPanel =
                  s.diagram.openLhsPanel === BpmnDiagramLhsPanel.PROPERTIES_MANAGEMENT
                    ? BpmnDiagramLhsPanel.NONE
                    : BpmnDiagramLhsPanel.PROPERTIES_MANAGEMENT;
              });
            }}
          >
            <Icon size={"sm"}>
              <ServicesIcon />
            </Icon>
          </button>
        </aside>

        <aside
          ref={paletteRef}
          className={`kie-bpmn-editor--palette ${pulse ? "pulse" : ""}`}
          style={{ position: "relative", pointerEvents: "all" }}
        >
          {paletteIcons.slice(0, visibleIconCount).map((icon, index) => {
            const element = renderPaletteIcon(icon);

            if (index === 0) {
              return (
                <div key={icon.id} ref={iconMeasureRef}>
                  {element}
                </div>
              );
            }

            return element;
          })}

          {showSubmenu && (
            <>
              {openGroupSubmenu === paletteGroups.corePaletteGroup.id && (
                <div
                  ref={submenuRef}
                  className={"kie-bpmn-editor--palette-nodes-popover kie-bpmn-editor--palette-more-items"}
                >
                  <div className={"kie-bpmn-editor--palette-more-items-grid"}>
                    {paletteIcons.slice(visibleIconCount).map(renderPaletteIcon)}
                  </div>
                </div>
              )}
              <button
                ref={ellipsisButtonRef}
                title={i18n.bpmnPalette.moreItems}
                className={`kie-bpmn-editor--palette-button kie-bpmn-editor--palette-ellipsis-button ${openGroupSubmenu === paletteGroups.corePaletteGroup.id ? "active" : ""}`}
                onClick={() =>
                  setOpenGroupSubmenu(
                    openGroupSubmenu === paletteGroups.corePaletteGroup.id ? null : paletteGroups.corePaletteGroup.id
                  )
                }
              >
                <EllipsisHIcon />
              </button>
            </>
          )}
        </aside>

        <aside className={`kie-bpmn-editor--palette ${pulse ? "pulse" : ""}`} style={{ pointerEvents: "all" }}>
          <div
            title={i18n.bpmnPalette.dataObject}
            className={"kie-bpmn-editor--palette-button dndnode data-object"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.dataObject, "dataObject")}
            onDragEnd={onDragEnd}
            draggable={true}
          >
            <DataObjectIcon />
          </div>
        </aside>

        <aside className={`kie-bpmn-editor--palette ${pulse ? "pulse" : ""}`} style={{ pointerEvents: "all" }}>
          <div
            title={i18n.bpmnPalette.group}
            className={"kie-bpmn-editor--palette-button dndnode group"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.group, "group")}
            onDragEnd={onDragEnd}
            draggable={true}
          >
            <GroupIcon />
          </div>
          <div
            title={i18n.bpmnPalette.textAnnotation}
            className={"kie-bpmn-editor--palette-button dndnode text-annotation"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.textAnnotation, "textAnnotation")}
            onDragEnd={onDragEnd}
            draggable={true}
          >
            <TextAnnotationIcon />
          </div>
        </aside>

        {customTasks && (
          <>
            <aside className={`kie-bpmn-editor--palette ${pulse ? "pulse" : ""}`} style={{ pointerEvents: "all" }}>
              <div
                title={i18n.bpmnPalette.customTasks}
                className={`kie-bpmn-editor--palette-button kie-bpmn-editor--palette-custom-tasks-button ${openLhsPanel === BpmnDiagramLhsPanel.CUSTOM_TASKS ? "active" : ""}`}
                onClick={() => {
                  bpmnEditorStoreApi.setState((s) => {
                    s.diagram.openLhsPanel =
                      s.diagram.openLhsPanel === BpmnDiagramLhsPanel.CUSTOM_TASKS
                        ? BpmnDiagramLhsPanel.NONE
                        : BpmnDiagramLhsPanel.CUSTOM_TASKS;
                  });
                }}
              >
                {customTasksPaletteIcon ?? <>{"🛠️"}</>}
              </div>
            </aside>
          </>
        )}
      </RF.Panel>
    </>
  );
}

function VariablesEmptyState({ addButton: _ }: { addButton: JSX.Element }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  return (
    <>
      <div className={"kie-bpmn-editor--correlations--empty-state"}>
        <Bullseye>
          <EmptyState>
            <EmptyStateIcon icon={CodeIcon} />
            <Title headingLevel="h4">
              {isReadOnly ? i18n.bpmnPalette.noVariables : i18n.bpmnPalette.noVariablesYet}
            </Title>
            <EmptyStateBody style={{ padding: "0 25%" }}>{i18n.bpmnPalette.emptyBpmnBody}</EmptyStateBody>
            <br />
            <EmptyStateActions>
              <Button
                variant="primary"
                onClick={() => {
                  bpmnEditorStoreApi.setState((s) => {
                    const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
                    addVariable({ definitions: s.bpmn.model.definitions, pId: process["@_id"] });
                  });
                }}
              >
                {i18n.bpmnPalette.addVariable}
              </Button>
            </EmptyStateActions>
          </EmptyState>
        </Bullseye>
      </div>
    </>
  );
}
