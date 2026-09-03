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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { NodeType } from "./connections/graphStructure";
import { NODE_TYPES } from "./nodes/NodeTypes";
import { DiagramLhsPanel } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { CONTAINER_NODES_DESIRABLE_PADDING, getBounds } from "./maths/DmnMaths";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { ExternalNodesPanel } from "../externalNodes/ExternalNodesPanel";
import { MigrationIcon } from "@patternfly/react-icons/dist/js/icons/migration-icon";
import {
  AlternativeInputDataIcon,
  BkmIcon,
  DecisionIcon,
  DecisionServiceIcon,
  GroupIcon,
  InputDataIcon,
  KnowledgeSourceIcon,
  TextAnnotationIcon,
} from "../icons/Icons";
import { DrdSelectorPanel } from "./DrdSelectorPanel";
import { addOrGetDrd, getDefaultDrdName } from "../mutations/addOrGetDrd";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { BarsIcon } from "@patternfly/react-icons/dist/js/icons/bars-icon";
import { EllipsisHIcon } from "@patternfly/react-icons/dist/js/icons/ellipsis-h-icon";
import { DrgNodesPanel } from "./DrgNodesPanel";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { useInViewSelect } from "../responsiveness/useInViewSelect";
import { useDmnEditor } from "../DmnEditorContext";
import { getDrdId } from "./drd/drdId";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { useDmnEditorI18n } from "../i18n";

export const MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE = "application/kie-dmn-editor--new-node-from-palette";

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

export function Palette({ pulse }: { pulse: boolean }) {
  const { i18n } = useDmnEditorI18n();
  const onDragStart = useCallback((event: React.DragEvent, nodeType: NodeType) => {
    event.dataTransfer.setData(MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE, nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  const { dmnEditorRootElementRef } = useDmnEditor();
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const diagram = useDmnEditorStore((s) => s.diagram);
  const thisDmn = useDmnEditorStore((s) => s.dmn.model);
  const rfStoreApi = RF.useStoreApi();
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());
  const drdIndex = useDmnEditorStore((s) => s.computed(s).getDrdIndex());
  const { externalModelsByNamespace } = useExternalModels();
  const settings = useSettings();

  const groupNodes = useCallback(() => {
    dmnEditorStoreApi.setState((state) => {
      const selectedNodes = rfStoreApi
        .getState()
        .getNodes()
        .filter((s) => s.selected);

      if (selectedNodes.length <= 0) {
        return;
      }

      const { href: newNodeId } = addStandaloneNode({
        definitions: state.dmn.model.definitions,
        drdIndex: state.computed(state).getDrdIndex(),
        newNode: {
          type: NODE_TYPES.group,
          bounds: getBounds({
            nodes: selectedNodes,
            padding: CONTAINER_NODES_DESIRABLE_PADDING,
          }),
        },
        externalModelsByNamespace,
      });

      state.dispatch(state).diagram.setNodeStatus(newNodeId, { selected: true });
    });
  }, [dmnEditorStoreApi, externalModelsByNamespace, rfStoreApi]);

  const drd = thisDmn.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[drdIndex];

  const drdSelectorPopoverRef = React.useRef<HTMLDivElement>(null);
  const nodesPalletePopoverRef = React.useRef<HTMLDivElement>(null);

  const { maxHeight } = useInViewSelect(dmnEditorRootElementRef, nodesPalletePopoverRef);

  const panelRef = useRef<HTMLDivElement>(null);
  const paletteRef = useRef<HTMLDivElement>(null);
  const submenuRef = useRef<HTMLDivElement>(null);
  const iconMeasureRef = useRef<HTMLDivElement>(null);
  const ellipsisButtonRef = useRef<HTMLButtonElement>(null);

  const primaryIcons = useMemo(
    () => [
      { nodeType: NODE_TYPES.inputData as NodeType, title: i18n.nodes.inputData, className: "input-data" },
      { nodeType: NODE_TYPES.decision as NodeType, title: i18n.nodes.decision, className: "decision" },
      { nodeType: NODE_TYPES.bkm as NodeType, title: i18n.nodes.businessKnowledgeModel, className: "bkm" },
      {
        nodeType: NODE_TYPES.knowledgeSource as NodeType,
        title: i18n.nodes.knowledgeSource,
        className: "knowledge-source",
      },
      {
        nodeType: NODE_TYPES.decisionService as NodeType,
        title: i18n.nodes.decisionService,
        className: "decision-service",
      },
    ],
    [i18n]
  );

  const totalIcons = primaryIcons.length;
  const [visibleIconCount, setVisibleIconCount] = useState<number>(totalIcons);
  const [submenuOpen, setSubmenuOpen] = useState(false);

  const showEllipsis = visibleIconCount < totalIcons;

  const iconHeightRef = useRef(48);
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

  useEffect(() => {
    let animationFrameId: number | null = null;
    let lastCount = totalIcons;

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

  const renderPaletteIcon = useCallback(
    (icon: (typeof primaryIcons)[number]) => {
      const isInputData = icon.nodeType === NODE_TYPES.inputData;
      const iconContent = isInputData ? (
        isAlternativeInputDataShape ? (
          <AlternativeInputDataIcon />
        ) : (
          <InputDataIcon />
        )
      ) : icon.nodeType === NODE_TYPES.decision ? (
        <DecisionIcon />
      ) : icon.nodeType === NODE_TYPES.bkm ? (
        <BkmIcon />
      ) : icon.nodeType === NODE_TYPES.knowledgeSource ? (
        <KnowledgeSourceIcon />
      ) : (
        <DecisionServiceIcon />
      );

      return (
        <div
          key={icon.nodeType}
          title={icon.title}
          className={`kie-dmn-editor--palette-button dndnode ${icon.className}`}
          onDragStart={(event) => onDragStart(event, icon.nodeType)}
          draggable={true}
        >
          {iconContent}
        </div>
      );
    },
    [isAlternativeInputDataShape, onDragStart]
  );

  const clearCurrentFocusToAllowDraggingNewNode = useCallback(() => {
    (document.activeElement as any)?.blur?.();
  }, []);

  return (
    <>
      <RF.Panel position={"top-left"}>
        <aside
          data-testid={"kie-tools--dmn-editor--drd-selector"}
          className={"kie-dmn-editor--drd-selector"}
          style={{ position: "relative" }}
        >
          <div ref={drdSelectorPopoverRef} style={{ position: "absolute", left: "56px", height: "100%", zIndex: -1 }} />
          <InlineFeelNameInput
            validate={() => true}
            allUniqueNames={() => new Map()}
            name={drd?.["@_name"] ?? ""}
            prefix={`${drdIndex + 1}.`}
            id={getDrdId({ drdIndex: drdIndex })}
            onRenamed={(newName) => {
              dmnEditorStoreApi.setState((state) => {
                const drd = addOrGetDrd({
                  definitions: state.dmn.model.definitions,
                  drdIndex: state.computed(state).getDrdIndex(),
                });
                drd.diagram["@_name"] = newName;
              });
            }}
            placeholder={getDefaultDrdName({ drdIndex: drdIndex })}
            isReadOnly={settings.isReadOnly}
            isPlain={true}
            shouldCommitOnBlur={true}
          />
          <Popover
            className={"kie-dmn-editor--drd-selector-popover"}
            key={DiagramLhsPanel.DRD_SELECTOR}
            aria-label={"DRD Selector Popover"}
            isVisible={diagram.openLhsPanel === DiagramLhsPanel.DRD_SELECTOR}
            triggerRef={() => drdSelectorPopoverRef.current!}
            shouldClose={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openLhsPanel = DiagramLhsPanel.NONE;
              });
            }}
            showClose={false}
            position={"bottom-start"}
            hideOnOutsideClick={false}
            bodyContent={<DrdSelectorPanel />}
          />
          <button
            title={i18n.nodes.selectEditDrd}
            onClick={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openLhsPanel =
                  state.diagram.openLhsPanel === DiagramLhsPanel.DRD_SELECTOR
                    ? DiagramLhsPanel.NONE
                    : DiagramLhsPanel.DRD_SELECTOR;
              });
            }}
          >
            <CaretDownIcon />
          </button>
        </aside>
      </RF.Panel>
      {!settings.isReadOnly && (
        <RF.Panel
          position={"top-left"}
          style={{ marginTop: "78px" }}
          onMouseDownCapture={clearCurrentFocusToAllowDraggingNewNode}
        >
          <div
            ref={panelRef}
            style={{ position: "absolute", top: 0, left: 0, width: "100%", height: "100%", pointerEvents: "none" }}
          />
          <div ref={nodesPalletePopoverRef} style={{ position: "absolute", left: 0, height: 0, zIndex: -1 }} />
          <aside
            ref={paletteRef}
            className={`kie-dmn-editor--palette ${pulse ? "pulse" : ""}`}
            style={{ position: "relative" }}
          >
            {primaryIcons.slice(0, visibleIconCount).map((icon, index) => {
              const element = renderPaletteIcon(icon);

              return index === 0 ? (
                <div key={icon.nodeType} ref={iconMeasureRef}>
                  {element}
                </div>
              ) : (
                element
              );
            })}

            {showEllipsis && (
              <>
                {submenuOpen && (
                  <div
                    ref={submenuRef}
                    data-testid={"kie-tools--dmn-editor--palette-more-items"}
                    className={"kie-dmn-editor--palette-more-items"}
                  >
                    <div className={"kie-dmn-editor--palette-more-items-grid"}>
                      {primaryIcons.slice(visibleIconCount).map(renderPaletteIcon)}
                    </div>
                  </div>
                )}
                <button
                  ref={ellipsisButtonRef}
                  title={i18n.nodes.moreItems}
                  className={`kie-dmn-editor--palette-button kie-dmn-editor--palette-ellipsis-button ${submenuOpen ? "active" : ""}`}
                  onClick={() => setSubmenuOpen((v) => !v)}
                >
                  <EllipsisHIcon />
                </button>
              </>
            )}
          </aside>

          <aside className={`kie-dmn-editor--palette ${pulse ? "pulse" : ""}`}>
            <div
              title={i18n.nodes.group}
              className={"kie-dmn-editor--palette-button dndnode group"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.group)}
              draggable={true}
              onClick={groupNodes}
            >
              <GroupIcon />
            </div>
            <div
              title={i18n.nodes.textAnnotation}
              className={"kie-dmn-editor--palette-button dndnode text-annotation"}
              onDragStart={(event) => onDragStart(event, NODE_TYPES.textAnnotation)}
              draggable={true}
            >
              <TextAnnotationIcon />
            </div>
          </aside>

          <aside className={"kie-dmn-editor--drg-panel-toggle"}>
            {diagram.openLhsPanel === DiagramLhsPanel.DRG_NODES && (
              <div
                data-testid={"kie-tools--dmn-editor--palette-nodes-popover"}
                className={"kie-dmn-editor--palette-nodes-popover"}
                style={{ maxHeight }}
              >
                <DrgNodesPanel />
              </div>
            )}
            <button
              title={i18n.nodes.drgnodes}
              className={`kie-dmn-editor--drg-panel-toggle-button ${
                diagram.openLhsPanel === DiagramLhsPanel.DRG_NODES ? "active" : ""
              }`}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.diagram.openLhsPanel =
                    state.diagram.openLhsPanel === DiagramLhsPanel.DRG_NODES
                      ? DiagramLhsPanel.NONE
                      : DiagramLhsPanel.DRG_NODES;
                });
              }}
            >
              <Icon>
                <BarsIcon />
              </Icon>
            </button>
          </aside>

          <aside className={"kie-dmn-editor--external-nodes-panel-toggle"}>
            {diagram.openLhsPanel === DiagramLhsPanel.EXTERNAL_NODES && (
              <div
                className={"kie-dmn-editor--palette-nodes-popover"}
                style={{ maxHeight }}
                data-testid={"kie-tools--dmn-editor--external-nodes-popover"}
              >
                <ExternalNodesPanel />
              </div>
            )}

            <button
              title={i18n.nodes.externalNodes}
              className={`kie-dmn-editor--external-nodes-panel-toggle-button ${
                diagram.openLhsPanel === DiagramLhsPanel.EXTERNAL_NODES ? "active" : ""
              }`}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.diagram.openLhsPanel =
                    state.diagram.openLhsPanel === DiagramLhsPanel.EXTERNAL_NODES
                      ? DiagramLhsPanel.NONE
                      : DiagramLhsPanel.EXTERNAL_NODES;
                });
              }}
            >
              <Icon>
                {" "}
                <MigrationIcon />
              </Icon>
            </button>
          </aside>
        </RF.Panel>
      )}
    </>
  );
}
