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
import { useOnViewportChange, Viewport } from "reactflow";
import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DC__Bounds,
  DC__Dimension,
  DMN15__tDecisionService,
  DMN15__tDefinitions,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized, normalize } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { buildXmlHref, parseXmlHref, xmlHrefToQName } from "@kie-tools/dmn-marshaller/dist/xml";
import { buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  EmptyStateActions,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { BlueprintIcon } from "@patternfly/react-icons/dist/js/icons/blueprint-icon";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { MousePointerIcon } from "@patternfly/react-icons/dist/js/icons/mouse-pointer-icon";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { VirtualMachineIcon } from "@patternfly/react-icons/dist/js/icons/virtual-machine-icon";
import { useDmnEditor } from "../DmnEditorContext";
import { AutolayoutButton } from "../autolayout/AutolayoutButton";
import { getDefaultColumnWidth } from "@kie-tools/boxed-expression-component/dist/resizing/WidthsToFitData";
import { getDefaultBoxedExpression } from "../boxedExpressions/getDefaultBoxedExpression";
import {
  ExternalNode,
  MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS,
} from "../externalNodes/ExternalNodesPanel";
import { nodeNatures } from "../mutations/NodeNature";
import { addConnectedNode } from "../mutations/addConnectedNode";
import { addDecisionToDecisionService } from "../mutations/addDecisionToDecisionService";
import { addEdge } from "../mutations/addEdge";
import { addShape } from "../mutations/addShape";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { deleteDecisionFromDecisionService } from "../mutations/deleteDecisionFromDecisionService";
import { deleteEdge, EdgeDeletionMode } from "../mutations/deleteEdge";
import { deleteNode, NodeDeletionMode } from "../mutations/deleteNode";
import { repositionNode } from "../mutations/repositionNode";
import { resizeNode } from "../mutations/resizeNode";
import { updateExpression } from "../mutations/updateExpression";
import { OverlaysPanel } from "../overlaysPanel/OverlaysPanel";
import { DiagramLhsPanel, SnapGrid, State } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { Unpacked } from "../tsExt/tsExt";
import { DiagramContainerContextProvider } from "./DiagramContainerContext";
import { MIME_TYPE_FOR_DMN_EDITOR_DRG_NODE } from "./DrgNodesPanel";
import { MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE, Palette } from "./Palette";
import { offsetShapePosition, snapShapeDimensions, snapShapePosition } from "./SnapGrid";
import { ConnectionLine } from "./connections/ConnectionLine";
import { PositionalNodeHandleId } from "./connections/PositionalNodeHandles";
import { containment, EdgeType, getDefaultEdgeTypeBetween, NodeType } from "./connections/graphStructure";
import { checkIsValidConnection } from "./connections/isValidConnection";
import { EdgeMarkers } from "./edges/EdgeMarkers";
import { EDGE_TYPES } from "./edges/EdgeTypes";
import {
  AssociationEdge,
  AuthorityRequirementEdge,
  DmnDiagramEdgeData,
  InformationRequirementEdge,
  KnowledgeRequirementEdge,
} from "./edges/Edges";
import { buildHierarchy } from "./graph/graph";
import {
  getContainmentRelationship,
  getDmnBoundsCenterPoint,
  getHandlePosition,
  getNodeTypeFromDmnObject,
} from "./maths/DmnMaths";
import { DEFAULT_NODE_SIZES, MIN_NODE_SIZES } from "./nodes/DefaultSizes";
import { NODE_TYPES } from "./nodes/NodeTypes";
import {
  BkmNode,
  DecisionNode,
  DecisionServiceNode,
  DmnDiagramNodeData,
  GroupNode,
  InputDataNode,
  KnowledgeSourceNode,
  TextAnnotationNode,
  UnknownNode,
} from "./nodes/Nodes";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import {
  addAutoGeneratedDecisionServiceToDrd,
  addConflictingDecisionServiceToDrd,
  addExistingDecisionServiceToDrd,
  getDecisionServicePropertiesRelativeToThisDmn,
  getStrategyToAddExistingDecisionServiceToDrd,
  StrategyForAddingDecisionServiceToDrd,
} from "../mutations/addExistingDecisionServiceToDrd";
import { updateExpressionWidths } from "../mutations/updateExpressionWidths";
import { DiagramCommands } from "./DiagramCommands";
import { getAutoLayoutedInfo } from "../autolayout/autoLayoutInfo";
import { autoGenerateDrd } from "../normalization/autoGenerateDrd";
import OptimizeIcon from "@patternfly/react-icons/dist/js/icons/optimize-icon";
import { applyAutoLayoutToDrd } from "../mutations/applyAutoLayoutToDrd";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { EvaluationHighlightsBadge } from "../evaluationHighlights/EvaluationHighlightsBadge";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";

const isFirefox = typeof (window as any).InstallTrigger !== "undefined"; // See https://stackoverflow.com/questions/9847580/how-to-detect-safari-chrome-ie-firefox-and-opera-browsers

const PAN_ON_DRAG = [1, 2];

const FIT_VIEW_OPTIONS: RF.FitViewOptions = { maxZoom: 1, minZoom: 0.1, duration: 400 };

export const DEFAULT_VIEWPORT = { x: 100, y: 100, zoom: 1 };

const DELETE_NODE_KEY_CODES = ["Backspace", "Delete"];

const AREA_ABOVE_OVERLAYS_PANEL = 120;

const nodeTypes: Record<NodeType, any> = {
  [NODE_TYPES.decisionService]: DecisionServiceNode,
  [NODE_TYPES.group]: GroupNode,
  [NODE_TYPES.inputData]: InputDataNode,
  [NODE_TYPES.decision]: DecisionNode,
  [NODE_TYPES.bkm]: BkmNode,
  [NODE_TYPES.knowledgeSource]: KnowledgeSourceNode,
  [NODE_TYPES.textAnnotation]: TextAnnotationNode,
  [NODE_TYPES.unknown]: UnknownNode,
};

const edgeTypes: Record<EdgeType, any> = {
  [EDGE_TYPES.informationRequirement]: InformationRequirementEdge,
  [EDGE_TYPES.authorityRequirement]: AuthorityRequirementEdge,
  [EDGE_TYPES.knowledgeRequirement]: KnowledgeRequirementEdge,
  [EDGE_TYPES.association]: AssociationEdge,
};

export type DiagramRef = {
  getReactFlowInstance: () => RF.ReactFlowInstance | undefined;
};

export const Diagram = React.forwardRef<DiagramRef, { container: React.RefObject<HTMLElement> }>(
  ({ container }, ref) => {
    // Contexts

    const dmnEditorStoreApi = useDmnEditorStoreApi();
    const { externalModelsByNamespace } = useExternalModels();
    const snapGrid = useDmnEditorStore((s) => s.diagram.snapGrid);
    const thisDmn = useDmnEditorStore((s) => s.dmn);
    const settings = useSettings();
    const { dmnModelBeforeEditingRef } = useDmnEditor();

    // State

    const [reactFlowInstance, setReactFlowInstance] = useState<
      RF.ReactFlowInstance<DmnDiagramNodeData, DmnDiagramEdgeData> | undefined
    >(undefined);

    const viewport = useDmnEditorStore((s) => s.diagram.viewport);

    // Refs
    React.useImperativeHandle(
      ref,
      () => ({
        getReactFlowInstance: () => {
          return reactFlowInstance;
        },
      }),
      [reactFlowInstance]
    );

    const nodeIdBeingDraggedRef = useRef<string | null>(null);

    // Memos

    const rfSnapGrid = useMemo<[number, number]>(
      () => (snapGrid.isEnabled ? [snapGrid.x, snapGrid.y] : [1, 1]),
      [snapGrid.isEnabled, snapGrid.x, snapGrid.y]
    );

    // Callbacks

    const onConnect = useCallback<RF.OnConnect>(
      ({ source, target, sourceHandle, targetHandle }) => {
        console.debug("DMN DIAGRAM: `onConnect`: ", { source, target, sourceHandle, targetHandle });
        dmnEditorStoreApi.setState((state) => {
          const sourceNode = state.computed(state).getDiagramData(externalModelsByNamespace).nodesById.get(source!);
          const targetNode = state.computed(state).getDiagramData(externalModelsByNamespace).nodesById.get(target!);
          if (!sourceNode || !targetNode) {
            throw new Error("Cannot create connection without target and source nodes!");
          }

          const sourceBounds = sourceNode.data.shape["dc:Bounds"];
          const targetBounds = targetNode.data.shape["dc:Bounds"];
          if (!sourceBounds || !targetBounds) {
            throw new Error("Cannot create connection without source and target bounds!");
          }

          // --------- This is where we draw the line between the diagram and the model.

          addEdge({
            definitions: state.dmn.model.definitions,
            drdIndex: state.computed(state).getDrdIndex(),
            edge: {
              type: sourceHandle as EdgeType,
              targetHandle: targetHandle as PositionalNodeHandleId,
              sourceHandle: PositionalNodeHandleId.Center,
              autoPositionedEdgeMarker: undefined,
            },
            sourceNode: {
              type: sourceNode.type as NodeType,
              data: sourceNode.data,
              href: sourceNode.id,
              bounds: sourceBounds,
              shapeId: sourceNode.data.shape["@_id"],
            },
            targetNode: {
              type: targetNode.type as NodeType,
              href: targetNode.id,
              data: targetNode.data,
              bounds: targetBounds,
              index: targetNode.data.index,
              shapeId: targetNode.data.shape["@_id"],
            },
            keepWaypoints: false,
            externalModelsByNamespace,
          });
        });
      },
      [dmnEditorStoreApi, externalModelsByNamespace]
    );

    const getFirstNodeFittingBounds = useCallback(
      (
        nodeIdToIgnore: string,
        bounds: DC__Bounds,
        minSizes: (args: { snapGrid: SnapGrid; isAlternativeInputDataShape: boolean }) => DC__Dimension,
        snapGrid: SnapGrid
      ) =>
        reactFlowInstance
          ?.getNodes()
          .reverse() // Respect the nodes z-index.
          .find(
            (node) =>
              node.id !== nodeIdToIgnore && // don't ever use the node being dragged
              getContainmentRelationship({
                bounds: bounds!,
                container: node.data.shape["dc:Bounds"]!,
                snapGrid,
                isAlternativeInputDataShape: dmnEditorStoreApi
                  .getState()
                  .computed(dmnEditorStoreApi.getState())
                  .isAlternativeInputDataShape(),
                containerMinSizes: MIN_NODE_SIZES[node.type as NodeType],
                boundsMinSizes: minSizes,
              }).isInside
          ),
      [reactFlowInstance, dmnEditorStoreApi]
    );

    const onDragOver = useCallback((e: React.DragEvent) => {
      if (
        !e.dataTransfer.types.find(
          (t) =>
            t === MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE ||
            t === MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS ||
            t === MIME_TYPE_FOR_DMN_EDITOR_DRG_NODE
        )
      ) {
        return;
      }

      e.preventDefault();
      e.dataTransfer.dropEffect = "move";
    }, []);

    const onDrop = useCallback(
      async (e: React.DragEvent) => {
        e.preventDefault();

        if (!container.current || !reactFlowInstance) {
          return;
        }

        // we need to remove the wrapper bounds, in order to get the correct position
        const dropPoint = reactFlowInstance.screenToFlowPosition({
          x: e.clientX,
          y: e.clientY,
        });

        if (e.dataTransfer.getData(MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE)) {
          const typeOfNewNodeFromPalette = e.dataTransfer.getData(
            MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE
          ) as NodeType;
          e.stopPropagation();

          // --------- This is where we draw the line between the diagram and the model.

          dmnEditorStoreApi.setState((state) => {
            const { id, href: newNodeId } = addStandaloneNode({
              definitions: state.dmn.model.definitions,
              drdIndex: state.computed(state).getDrdIndex(),
              newNode: {
                type: typeOfNewNodeFromPalette,
                bounds: {
                  "@_x": dropPoint.x,
                  "@_y": dropPoint.y,
                  "@_width": DEFAULT_NODE_SIZES[typeOfNewNodeFromPalette]({
                    snapGrid: state.diagram.snapGrid,
                    isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
                  })["@_width"],
                  "@_height": DEFAULT_NODE_SIZES[typeOfNewNodeFromPalette]({
                    snapGrid: state.diagram.snapGrid,
                    isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
                  })["@_height"],
                },
              },
              externalModelsByNamespace,
            });
            state.diagram._selectedNodes = [newNodeId];
            state.focus.consumableId = newNodeId;
          });
        } else if (e.dataTransfer.getData(MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS)) {
          e.stopPropagation();
          const externalNode = JSON.parse(
            e.dataTransfer.getData(MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS)
          ) as ExternalNode;

          // --------- This is where we draw the line between the diagram and the model.

          const state = dmnEditorStoreApi.getState();
          const externalDmnsIndex = state
            .computed(state)
            .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns;

          const externalNodeDmn = externalDmnsIndex.get(externalNode.externalDrgElementNamespace);
          const externalDrgElement = (externalNodeDmn?.model.definitions.drgElement ?? []).find(
            (e) => e["@_id"] === externalNode.externalDrgElementId
          );
          if (!externalNodeDmn || !externalDrgElement) {
            throw new Error(
              `Can't find DRG element with id '${externalNode.externalDrgElementId}' on/or model with namespace '${externalNode.externalDrgElementNamespace}'.`
            );
          }

          const externalNodeType = getNodeTypeFromDmnObject(externalDrgElement)!;

          const defaultExternalNodeDimensions = DEFAULT_NODE_SIZES[externalNodeType]({
            snapGrid: state.diagram.snapGrid,
            isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
          });

          const externalNodeHref = buildXmlHref({
            namespace: externalNode.externalDrgElementNamespace,
            id: externalNode.externalDrgElementId,
          });

          if (externalDrgElement.__$$element === "decisionService") {
            // Create a dereferencedState to enables us to edit the object without changing the original state
            // After we finish applying the changes it will be used to set the new state.
            const { computed, ...internalState } = state;
            const dereferencedState: State = { computed, ...JSON.parse(JSON.stringify(internalState)) };
            const drdIndex = dereferencedState.computed(dereferencedState).getDrdIndex();

            const {
              strategyForAddingDecisionServiceToDrd,
              indexedDrdContainingDecisionServiceDepiction,
              decisionServiceHrefRelativeToThisDmn,
              containedDecisionHrefsRelativeToThisDmn,
            } = getStrategyToAddExistingDecisionServiceToDrd({
              __readonly_definitions: dereferencedState.dmn.model.definitions,
              __readonly_drgElement: externalDrgElement,
              __readonly_decisionServiceNamespace: externalNodeDmn.model.definitions["@_namespace"],
              __readonly_drdIndex: drdIndex,
              __readonly_externalDmnsIndex: externalDmnsIndex,
              __readonly_indexedDrd: dereferencedState.computed(dereferencedState).indexedDrd(),
              __readonly_namespace: dereferencedState.dmn.model.definitions["@_namespace"],
            });

            if (strategyForAddingDecisionServiceToDrd === StrategyForAddingDecisionServiceToDrd.CONFLICT) {
              addConflictingDecisionServiceToDrd({
                definitions: dereferencedState.dmn.model.definitions,
                __readonly_drdIndex: drdIndex,
                __readonly_dropPoint: dropPoint,
                __readonly_decisionServiceHrefRelativeToThisDmn: decisionServiceHrefRelativeToThisDmn,
              });
            } else if (strategyForAddingDecisionServiceToDrd === StrategyForAddingDecisionServiceToDrd.AUTO_GENERATE) {
              await addAutoGeneratedDecisionServiceToDrd({
                state: dereferencedState,
                __readonly_decisionServiceNamespace: externalNodeDmn.model.definitions["@_namespace"],
                __readonly_drdIndex: drdIndex,
                __readonly_externalDmnsIndex: externalDmnsIndex,
                __readonly_containedDecisionHrefsRelativeToThisDmn: containedDecisionHrefsRelativeToThisDmn,
                __readonly_decisionServiceHrefRelativeToThisDmn: decisionServiceHrefRelativeToThisDmn,
                __readonly_snapGrid: dereferencedState.diagram.snapGrid,
                __readonly_dropPoint: dropPoint,
                __readonly_externalModelsByNamespace: externalModelsByNamespace,
                __readonly_isAlternativeInputDataShape: dereferencedState
                  .computed(dereferencedState)
                  .isAlternativeInputDataShape(),
              });
            } else if (
              strategyForAddingDecisionServiceToDrd === StrategyForAddingDecisionServiceToDrd.COPY_FROM_ANOTHER_DRD
            ) {
              addExistingDecisionServiceToDrd({
                definitions: dereferencedState.dmn.model.definitions,
                __readonly_decisionServiceNamespace: externalNodeDmn.model.definitions["@_namespace"],
                __readonly_drdIndex: drdIndex,
                __readonly_externalDmnsIndex: dereferencedState
                  .computed(dereferencedState)
                  .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns,
                __readonly_indexedDrd: dereferencedState.computed(dereferencedState).indexedDrd(),
                __readonly_indexedDrdContainingDecisionServiceDepiction: indexedDrdContainingDecisionServiceDepiction!,
                __readonly_containedDecisionHrefsRelativeToThisDmn: containedDecisionHrefsRelativeToThisDmn,
                __readonly_decisionServiceHrefRelativeToThisDmn: decisionServiceHrefRelativeToThisDmn,
                __readonly_dropPoint: dropPoint,
                __readonly_namespace: dereferencedState.dmn.model.definitions["@_namespace"],
              });
            }
            dmnEditorStoreApi.setState((state) => {
              state.dmn.model = JSON.parse(JSON.stringify(dereferencedState.dmn.model));
              state.diagram._selectedNodes = [externalNodeHref];
            });
          } else {
            dmnEditorStoreApi.setState((state) => {
              const externalNodeType = getNodeTypeFromDmnObject(externalDrgElement)!;
              addShape({
                definitions: state.dmn.model.definitions,
                drdIndex: state.computed(state).getDrdIndex(),
                nodeType: externalNodeType,
                shape: {
                  "@_id": generateUuid(),
                  "@_dmnElementRef": xmlHrefToQName(externalNodeHref, state.dmn.model.definitions),
                  "dc:Bounds": {
                    "@_x": dropPoint.x,
                    "@_y": dropPoint.y,
                    "@_width": defaultExternalNodeDimensions["@_width"],
                    "@_height": defaultExternalNodeDimensions["@_height"],
                  },
                },
              });
              state.diagram._selectedNodes = [externalNodeHref];
            });
          }
          console.debug(`DMN DIAGRAM: Adding external node`, JSON.stringify(externalNode));
        } else if (e.dataTransfer.getData(MIME_TYPE_FOR_DMN_EDITOR_DRG_NODE)) {
          const drgElement = JSON.parse(e.dataTransfer.getData(MIME_TYPE_FOR_DMN_EDITOR_DRG_NODE)) as Unpacked<
            Normalized<DMN15__tDefinitions>["drgElement"]
          >;

          const nodeType = getNodeTypeFromDmnObject(drgElement);
          if (nodeType === undefined) {
            throw new Error("DMN DIAGRAM: It wasn't possible to determine the node type");
          }

          if (drgElement.__$$element === "decisionService") {
            const { computed, ...state } = dmnEditorStoreApi.getState();
            const dereferencedState: State = { computed, ...JSON.parse(JSON.stringify(state)) };

            const drdIndex = dereferencedState.computed(dereferencedState).getDrdIndex();
            const externalDmnsIndex = dereferencedState
              .computed(dereferencedState)
              .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns;

            const {
              strategyForAddingDecisionServiceToDrd,
              indexedDrdContainingDecisionServiceDepiction,
              decisionServiceHrefRelativeToThisDmn,
              containedDecisionHrefsRelativeToThisDmn,
            } = getStrategyToAddExistingDecisionServiceToDrd({
              __readonly_definitions: dereferencedState.dmn.model.definitions,
              __readonly_drgElement: drgElement,
              __readonly_decisionServiceNamespace: dereferencedState.dmn.model.definitions["@_namespace"],
              __readonly_drdIndex: drdIndex,
              __readonly_externalDmnsIndex: externalDmnsIndex,
              __readonly_indexedDrd: dereferencedState.computed(dereferencedState).indexedDrd(),
              __readonly_namespace: dereferencedState.dmn.model.definitions["@_namespace"],
            });
            if (strategyForAddingDecisionServiceToDrd === StrategyForAddingDecisionServiceToDrd.CONFLICT) {
              addConflictingDecisionServiceToDrd({
                definitions: dereferencedState.dmn.model.definitions,
                __readonly_drdIndex: drdIndex,
                __readonly_dropPoint: dropPoint,
                __readonly_decisionServiceHrefRelativeToThisDmn: decisionServiceHrefRelativeToThisDmn,
              });
            } else if (strategyForAddingDecisionServiceToDrd === StrategyForAddingDecisionServiceToDrd.AUTO_GENERATE) {
              await addAutoGeneratedDecisionServiceToDrd({
                state: dereferencedState,
                __readonly_decisionServiceNamespace: dereferencedState.dmn.model.definitions["@_namespace"],
                __readonly_drdIndex: drdIndex,
                __readonly_externalDmnsIndex: externalDmnsIndex,
                __readonly_containedDecisionHrefsRelativeToThisDmn: containedDecisionHrefsRelativeToThisDmn,
                __readonly_decisionServiceHrefRelativeToThisDmn: decisionServiceHrefRelativeToThisDmn,
                __readonly_snapGrid: dereferencedState.diagram.snapGrid,
                __readonly_dropPoint: dropPoint,
                __readonly_externalModelsByNamespace: externalModelsByNamespace,
                __readonly_isAlternativeInputDataShape: dereferencedState
                  .computed(dereferencedState)
                  .isAlternativeInputDataShape(),
              });
            } else if (
              strategyForAddingDecisionServiceToDrd === StrategyForAddingDecisionServiceToDrd.COPY_FROM_ANOTHER_DRD
            ) {
              addExistingDecisionServiceToDrd({
                definitions: dereferencedState.dmn.model.definitions,
                __readonly_decisionServiceNamespace: dereferencedState.dmn.model.definitions["@_namespace"],
                __readonly_drdIndex: drdIndex,
                __readonly_externalDmnsIndex: dereferencedState
                  .computed(dereferencedState)
                  .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns,
                __readonly_indexedDrd: dereferencedState.computed(dereferencedState).indexedDrd(),
                __readonly_indexedDrdContainingDecisionServiceDepiction: indexedDrdContainingDecisionServiceDepiction!,
                __readonly_containedDecisionHrefsRelativeToThisDmn: containedDecisionHrefsRelativeToThisDmn,
                __readonly_decisionServiceHrefRelativeToThisDmn: decisionServiceHrefRelativeToThisDmn,
                __readonly_dropPoint: dropPoint,
                __readonly_namespace: dereferencedState.dmn.model.definitions["@_namespace"],
              });
            }
            dmnEditorStoreApi.setState((state) => {
              state.dmn.model = JSON.parse(JSON.stringify(dereferencedState.dmn.model));
            });
          } else {
            dmnEditorStoreApi.setState((state) => {
              const nodeType = getNodeTypeFromDmnObject(drgElement)!;

              const defaultNodeDimensions = DEFAULT_NODE_SIZES[nodeType]({
                snapGrid: state.diagram.snapGrid,
                isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
              });

              addShape({
                definitions: state.dmn.model.definitions,
                drdIndex: state.computed(state).getDrdIndex(),
                nodeType,
                shape: {
                  "@_id": generateUuid(),
                  "@_dmnElementRef": buildXmlQName({
                    type: "xml-qname",
                    localPart: drgElement["@_id"]!,
                  }),
                  "@_isCollapsed": false,
                  "dc:Bounds": {
                    "@_x": dropPoint.x,
                    "@_y": dropPoint.y,
                    "@_width": defaultNodeDimensions["@_width"],
                    "@_height": defaultNodeDimensions["@_height"],
                  },
                },
              });
            });
          }
          console.debug(`DMN DIAGRAM: Adding DRG node`, JSON.stringify(drgElement));
        }
      },
      [container, dmnEditorStoreApi, externalModelsByNamespace, reactFlowInstance]
    );

    const ongoingConnection = useDmnEditorStore((s) => s.diagram.ongoingConnection);
    useEffect(() => {
      const edgeUpdaterSource = document.querySelectorAll(
        ".react-flow__edgeupdater-source, .react-flow__edgeupdater-target"
      );
      if (ongoingConnection) {
        edgeUpdaterSource.forEach((e) => e.classList.add("hidden"));
      } else {
        edgeUpdaterSource.forEach((e) => e.classList.remove("hidden"));
      }
    }, [ongoingConnection]);

    const onConnectStart = useCallback<RF.OnConnectStart>(
      (e, newConnection) => {
        console.debug("DMN DIAGRAM: `onConnectStart`");
        dmnEditorStoreApi.setState((state) => {
          state.diagram.ongoingConnection = newConnection;
        });
      },
      [dmnEditorStoreApi]
    );

    const onConnectEnd = useCallback(
      (e: MouseEvent) => {
        console.debug("DMN DIAGRAM: `onConnectEnd`");

        dmnEditorStoreApi.setState((state) => {
          const targetIsPane = (e.target as Element | null)?.classList?.contains("react-flow__pane");
          if (!targetIsPane || !container.current || !state.diagram.ongoingConnection || !reactFlowInstance) {
            return;
          }

          const dropPoint = reactFlowInstance.screenToFlowPosition({
            x: e.clientX,
            y: e.clientY,
          });

          // only try to create node if source handle is compatible
          if (!Object.values(NODE_TYPES).find((n) => n === state.diagram.ongoingConnection!.handleId)) {
            return;
          }

          if (!state.diagram.ongoingConnection.nodeId) {
            return;
          }

          const sourceNode = state
            .computed(state)
            .getDiagramData(externalModelsByNamespace)
            .nodesById.get(state.diagram.ongoingConnection.nodeId);
          if (!sourceNode) {
            return;
          }

          const sourceNodeBounds = state.computed(state).indexedDrd().dmnShapesByHref.get(sourceNode.id)?.["dc:Bounds"];
          if (!sourceNodeBounds) {
            return;
          }

          const newNodeType = state.diagram.ongoingConnection.handleId as NodeType;
          const sourceNodeType = sourceNode.type as NodeType;

          const edgeType = getDefaultEdgeTypeBetween(sourceNodeType as NodeType, newNodeType);
          if (!edgeType) {
            throw new Error(`DMN DIAGRAM: Invalid structure: ${sourceNodeType} --(any)--> ${newNodeType}`);
          }

          // --------- This is where we draw the line between the diagram and the model.

          const { id, href: newDmnObejctHref } = addConnectedNode({
            definitions: state.dmn.model.definitions,
            drdIndex: state.computed(state).getDrdIndex(),
            edgeType,
            sourceNode: {
              href: sourceNode.id,
              type: sourceNodeType as NodeType,
              bounds: sourceNodeBounds,
              shapeId: sourceNode.data.shape["@_id"],
            },
            newNode: {
              type: newNodeType,
              bounds: {
                "@_x": dropPoint.x,
                "@_y": dropPoint.y,
                "@_width": DEFAULT_NODE_SIZES[newNodeType]({
                  snapGrid: state.diagram.snapGrid,
                  isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
                })["@_width"],
                "@_height": DEFAULT_NODE_SIZES[newNodeType]({
                  snapGrid: state.diagram.snapGrid,
                  isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
                })["@_height"],
              },
            },
            externalModelsByNamespace,
          });

          state.diagram._selectedNodes = [newDmnObejctHref];
          state.focus.consumableId = newDmnObejctHref;
        });

        // Indepdent of what happens in the state mutation above, we always need to reset the `ongoingConnection` at the end here.
        dmnEditorStoreApi.setState((state) => {
          state.diagram.ongoingConnection = undefined;
        });
      },
      [dmnEditorStoreApi, container, reactFlowInstance, externalModelsByNamespace]
    );

    const isValidConnection = useCallback<RF.IsValidConnection>(
      (edgeOrConnection) => {
        const state = dmnEditorStoreApi.getState();
        const edgeId = state.diagram.edgeIdBeingUpdated;
        const edgeType = edgeId ? (reactFlowInstance?.getEdge(edgeId)?.type as EdgeType) : undefined;

        const ongoingConnectionHierarchy = buildHierarchy({
          nodeId: state.diagram.ongoingConnection?.nodeId,
          edges: state.computed(state).getDiagramData(externalModelsByNamespace).drgEdges,
        });

        return (
          // Reflexive edges are not allowed for DMN
          edgeOrConnection.source !== edgeOrConnection.target &&
          // Matches DMNs structure.
          checkIsValidConnection(
            state.computed(state).getDiagramData(externalModelsByNamespace).nodesById,
            edgeOrConnection,
            edgeType
          ) &&
          // Does not form cycles.
          !!edgeOrConnection.target &&
          !ongoingConnectionHierarchy.dependencies.has(edgeOrConnection.target) &&
          !!edgeOrConnection.source &&
          !ongoingConnectionHierarchy.dependents.has(edgeOrConnection.source)
        );
      },
      [dmnEditorStoreApi, externalModelsByNamespace, reactFlowInstance]
    );

    const onNodesChange = useCallback<RF.OnNodesChange>(
      (changes) => {
        dmnEditorStoreApi.setState((state) => {
          const controlWaypointsByEdge = new Map<number, Set<number>>();

          for (const change of changes) {
            switch (change.type) {
              case "add":
                console.debug(`DMN DIAGRAM: 'onNodesChange' --> add '${change.item.id}'`);
                state.dispatch(state).diagram.setNodeStatus(change.item.id, { selected: true });
                break;
              case "dimensions":
                console.debug(`DMN DIAGRAM: 'onNodesChange' --> dimensions '${change.id}'`);
                state.dispatch(state).diagram.setNodeStatus(change.id, { resizing: change.resizing });
                if (change.dimensions) {
                  const node = state
                    .computed(state)
                    .getDiagramData(externalModelsByNamespace)
                    .nodesById.get(change.id)!;
                  // We only need to resize the node if its snapped dimensions change, as snapping is non-destructive.
                  const snappedShape = snapShapeDimensions(
                    state.diagram.snapGrid,
                    node.data.shape,
                    MIN_NODE_SIZES[node.type as NodeType]({
                      snapGrid: state.diagram.snapGrid,
                      isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
                    })
                  );
                  if (
                    snappedShape.width !== change.dimensions.width ||
                    snappedShape.height !== change.dimensions.height
                  ) {
                    resizeNode({
                      definitions: state.dmn.model.definitions,
                      drdIndex: state.computed(state).getDrdIndex(),
                      __readonly_dmnShapesByHref: state.computed(state).indexedDrd().dmnShapesByHref,
                      snapGrid: state.diagram.snapGrid,
                      __readonly_dmnObjectNamespace: node.data.dmnObjectNamespace,
                      __readonly_externalDmnsIndex: state
                        .computed(state)
                        .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns,
                      change: {
                        isExternal: !!node.data.dmnObjectQName.prefix,
                        nodeType: node.type as NodeType,
                        index: node.data.index,
                        shapeIndex: node.data.shape.index,
                        sourceEdgeIndexes: state
                          .computed(state)
                          .getDiagramData(externalModelsByNamespace)
                          .edges.flatMap((e) =>
                            e.source === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                          ),
                        targetEdgeIndexes: state
                          .computed(state)
                          .getDiagramData(externalModelsByNamespace)
                          .edges.flatMap((e) =>
                            e.target === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                          ),
                        dimension: {
                          "@_width": change.dimensions?.width ?? 0,
                          "@_height": change.dimensions?.height ?? 0,
                        },
                      },
                    });
                  }
                }
                break;
              case "position":
                console.debug(`DMN DIAGRAM: 'onNodesChange' --> position '${change.id}'`);
                state.dispatch(state).diagram.setNodeStatus(change.id, { dragging: change.dragging });
                if (change.positionAbsolute) {
                  const node = state
                    .computed(state)
                    .getDiagramData(externalModelsByNamespace)
                    .nodesById.get(change.id)!;
                  const { delta } = repositionNode({
                    definitions: state.dmn.model.definitions,
                    drdIndex: state.computed(state).getDrdIndex(),
                    controlWaypointsByEdge,
                    change: {
                      type: "absolute",
                      nodeType: node.type as NodeType,
                      selectedEdges: [
                        ...state.computed(state).getDiagramData(externalModelsByNamespace).selectedEdgesById.keys(),
                      ],
                      shapeIndex: node.data.shape.index,
                      sourceEdgeIndexes: state
                        .computed(state)
                        .getDiagramData(externalModelsByNamespace)
                        .edges.flatMap((e) =>
                          e.source === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                        ),
                      targetEdgeIndexes: state
                        .computed(state)
                        .getDiagramData(externalModelsByNamespace)
                        .edges.flatMap((e) =>
                          e.target === change.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                        ),
                      position: change.positionAbsolute,
                    },
                  });

                  // Update contained Decisions of Decision Service if in expanded form
                  if (node.type === NODE_TYPES.decisionService && !(node.data.shape["@_isCollapsed"] ?? false)) {
                    const decisionService = node.data.dmnObject as Normalized<DMN15__tDecisionService>;

                    const { containedDecisionHrefsRelativeToThisDmn } = getDecisionServicePropertiesRelativeToThisDmn({
                      thisDmnsNamespace: state.dmn.model.definitions["@_namespace"],
                      decisionService,
                      decisionServiceNamespace:
                        node.data.dmnObjectNamespace ?? state.dmn.model.definitions["@_namespace"],
                    });

                    for (let i = 0; i < containedDecisionHrefsRelativeToThisDmn.length; i++) {
                      const diagramData = state.computed(state).getDiagramData(externalModelsByNamespace);
                      const nestedNode = diagramData.nodesById.get(containedDecisionHrefsRelativeToThisDmn[i]);
                      if (nestedNode?.data?.shape === undefined) {
                        // In case we have an incomple depiction of the decision service in the current DRD
                        continue;
                      }
                      const snappedNestedNodeShapeWithAppliedDelta = snapShapePosition(
                        state.diagram.snapGrid,
                        offsetShapePosition(nestedNode.data.shape, delta)
                      );
                      repositionNode({
                        definitions: state.dmn.model.definitions,
                        drdIndex: state.computed(state).getDrdIndex(),
                        controlWaypointsByEdge,
                        change: {
                          type: "absolute",
                          nodeType: nestedNode.type as NodeType,
                          selectedEdges: diagramData.edges.map((e) => e.id),
                          shapeIndex: nestedNode.data.shape.index,
                          sourceEdgeIndexes: diagramData.edges.flatMap((e) =>
                            e.source === nestedNode.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                          ),
                          targetEdgeIndexes: diagramData.edges.flatMap((e) =>
                            e.target === nestedNode.id && e.data?.dmnEdge ? [e.data.dmnEdge.index] : []
                          ),
                          position: snappedNestedNodeShapeWithAppliedDelta,
                        },
                      });
                    }
                  }
                }
                break;
              case "remove":
                console.debug(`DMN DIAGRAM: 'onNodesChange' --> remove '${change.id}'`);
                const node = state.computed(state).getDiagramData(externalModelsByNamespace).nodesById.get(change.id)!;
                deleteNode({
                  definitions: state.dmn.model.definitions,
                  __readonly_drgEdges: state.computed(state).getDiagramData(externalModelsByNamespace).drgEdges,
                  __readonly_drdIndex: state.computed(state).getDrdIndex(),
                  __readonly_dmnObjectNamespace:
                    node.data.dmnObjectNamespace ?? state.dmn.model.definitions["@_namespace"],
                  __readonly_dmnObjectQName: node.data.dmnObjectQName,
                  __readonly_dmnObjectId: node.data.dmnObject?.["@_id"],
                  __readonly_nodeNature: nodeNatures[node.type as NodeType],
                  __readonly_mode: NodeDeletionMode.FROM_DRG_AND_ALL_DRDS,
                  __readonly_externalDmnsIndex: state
                    .computed(state)
                    .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns,
                  __readonly_externalModelsByNamespace: externalModelsByNamespace,
                });
                state.dispatch(state).diagram.setNodeStatus(node.id, {
                  selected: false,
                  dragging: false,
                  resizing: false,
                });
                break;
              case "reset":
                state.dispatch(state).diagram.setNodeStatus(change.item.id, {
                  selected: false,
                  dragging: false,
                  resizing: false,
                });
                break;
              case "select":
                state.dispatch(state).diagram.setNodeStatus(change.id, { selected: change.selected });
                break;
            }
          }
        });
      },
      [dmnEditorStoreApi, externalModelsByNamespace]
    );

    const resetToBeforeEditingBegan = useCallback(() => {
      dmnEditorStoreApi.setState((state) => {
        state.dmn.model = normalize(dmnModelBeforeEditingRef.current);
        state.diagram.draggingNodes = [];
        state.diagram.draggingWaypoints = [];
        state.diagram.resizingNodes = [];
        state.diagram.dropTargetNode = undefined;
        state.diagram.edgeIdBeingUpdated = undefined;
      });
    }, [dmnEditorStoreApi, dmnModelBeforeEditingRef]);

    const onNodeDrag = useCallback<RF.NodeDragHandler>(
      (e, node: RF.Node<DmnDiagramNodeData>) => {
        nodeIdBeingDraggedRef.current = node.id;
        dmnEditorStoreApi.setState((state) => {
          state.diagram.dropTargetNode = getFirstNodeFittingBounds(
            node.id,
            {
              // We can't use node.data.dmnObject because it hasn't been updated at this point yet.
              "@_x": node.positionAbsolute?.x ?? 0,
              "@_y": node.positionAbsolute?.y ?? 0,
              "@_width": node.width ?? 0,
              "@_height": node.height ?? 0,
            },
            MIN_NODE_SIZES[node.type as NodeType],
            state.diagram.snapGrid
          );
        });
      },
      [dmnEditorStoreApi, getFirstNodeFittingBounds]
    );

    const onNodeDragStart = useCallback<RF.NodeDragHandler>(
      (e, node: RF.Node<DmnDiagramNodeData>, nodes) => {
        dmnModelBeforeEditingRef.current = thisDmn.model;
        onNodeDrag(e, node, nodes);
      },
      [thisDmn.model, dmnModelBeforeEditingRef, onNodeDrag]
    );

    const onNodeDragStop = useCallback<RF.NodeDragHandler>(
      (e, node: RF.Node<DmnDiagramNodeData>) => {
        try {
          dmnEditorStoreApi.setState((state) => {
            console.debug("DMN DIAGRAM: `onNodeDragStop`");
            const nodeBeingDragged = state
              .computed(state)
              .getDiagramData(externalModelsByNamespace)
              .nodesById.get(nodeIdBeingDraggedRef.current!);
            nodeIdBeingDraggedRef.current = null;
            if (!nodeBeingDragged) {
              return;
            }

            // Validate
            const dropTargetNode = dmnEditorStoreApi.getState().diagram.dropTargetNode;
            if (
              dropTargetNode &&
              containment.has(dropTargetNode.type as NodeType) &&
              !state.computed(state).isDropTargetNodeValidForSelection
            ) {
              console.debug(
                `DMN DIAGRAM: Invalid containment: '${[
                  ...state.computed(state).getDiagramData(externalModelsByNamespace).selectedNodeTypes,
                ].join("', '")}' inside '${dropTargetNode.type}'. Ignoring nodes dropped.`
              );
              resetToBeforeEditingBegan();
              return;
            }

            const selectedNodes = [
              ...state.computed(state).getDiagramData(externalModelsByNamespace).selectedNodesById.values(),
            ];

            state.diagram.dropTargetNode = undefined;

            if (!node.dragging) {
              return;
            }

            // Un-parent
            if (nodeBeingDragged.data.parentRfNode) {
              const p = state
                .computed(state)
                .getDiagramData(externalModelsByNamespace)
                .nodesById.get(nodeBeingDragged.data.parentRfNode.id);
              if (p?.type === NODE_TYPES.decisionService && nodeBeingDragged.type === NODE_TYPES.decision) {
                for (let i = 0; i < selectedNodes.length; i++) {
                  deleteDecisionFromDecisionService({
                    definitions: state.dmn.model.definitions,
                    decisionHref: selectedNodes[i].id, // We can assume that all selected nodes are Decisions because the contaiment was validated above.
                    decisionServiceId: p.data.dmnObject!["@_id"]!,
                    externalModelsByNamespace,
                  });
                }
              } else {
                console.debug(
                  `DMN DIAGRAM: Ignoring '${nodeBeingDragged.type}' with parent '${dropTargetNode?.type}' dropping somewhere..`
                );
              }
            }

            // Parent
            if (dropTargetNode?.type === NODE_TYPES.decisionService) {
              for (let i = 0; i < selectedNodes.length; i++) {
                addDecisionToDecisionService({
                  definitions: state.dmn.model.definitions,
                  drdIndex: state.computed(state).getDrdIndex(),
                  decisionHref: selectedNodes[i].id, // We can assume that all selected nodes are Decisions because the contaiment was validated above.
                  decisionServiceId: state
                    .computed(state)
                    .getDiagramData(externalModelsByNamespace)
                    .nodesById.get(dropTargetNode.id)!.data.dmnObject!["@_id"]!,
                  snapGrid: state.diagram.snapGrid,
                  externalModelsByNamespace,
                });
              }
            } else {
              console.debug(
                `DMN DIAGRAM: Ignoring '${nodeBeingDragged.type}' dropped on top of '${dropTargetNode?.type}'`
              );
            }
          });
        } catch (e) {
          console.error(e);
          resetToBeforeEditingBegan();
        }
      },
      [dmnEditorStoreApi, externalModelsByNamespace, resetToBeforeEditingBegan]
    );

    const onEdgesChange = useCallback<RF.OnEdgesChange>(
      (changes) => {
        dmnEditorStoreApi.setState((state) => {
          for (const change of changes) {
            switch (change.type) {
              case "select":
                console.debug(`DMN DIAGRAM: 'onEdgesChange' --> select '${change.id}'`);
                state.dispatch(state).diagram.setEdgeStatus(change.id, { selected: change.selected });
                break;
              case "remove":
                console.debug(`DMN DIAGRAM: 'onEdgesChange' --> remove '${change.id}'`);
                const edge = state.computed(state).getDiagramData(externalModelsByNamespace).edgesById.get(change.id);
                if (edge?.data) {
                  deleteEdge({
                    definitions: state.dmn.model.definitions,
                    drdIndex: state.computed(state).getDrdIndex(),
                    edge: { id: change.id, dmnObject: edge.data.dmnObject },
                    mode: EdgeDeletionMode.FROM_DRG_AND_ALL_DRDS,
                    externalModelsByNamespace,
                  });
                  state.dispatch(state).diagram.setEdgeStatus(change.id, {
                    selected: false,
                    draggingWaypoint: false,
                  });
                }
                break;
              case "add":
              case "reset":
                console.debug(`DMN DIAGRAM: 'onEdgesChange' --> add/reset '${change.item.id}'. Ignoring`);
            }
          }
        });
      },
      [dmnEditorStoreApi, externalModelsByNamespace]
    );

    const onEdgeUpdate = useCallback<RF.OnEdgeUpdateFunc<DmnDiagramEdgeData>>(
      (oldEdge, newConnection) => {
        console.debug("DMN DIAGRAM: `onEdgeUpdate`", oldEdge, newConnection);

        dmnEditorStoreApi.setState((state) => {
          const sourceNode = state
            .computed(state)
            .getDiagramData(externalModelsByNamespace)
            .nodesById.get(newConnection.source!);
          const targetNode = state
            .computed(state)
            .getDiagramData(externalModelsByNamespace)
            .nodesById.get(newConnection.target!);
          if (!sourceNode || !targetNode) {
            throw new Error("Cannot create connection without target and source nodes!");
          }

          const sourceBounds = sourceNode.data.shape["dc:Bounds"];
          const targetBounds = targetNode.data.shape["dc:Bounds"];
          if (!sourceBounds || !targetBounds) {
            throw new Error("Cannot create connection without target bounds!");
          }

          // --------- This is where we draw the line between the diagram and the model.

          const lastWaypoint = oldEdge.data?.dmnEdge
            ? oldEdge.data!.dmnEdge!["di:waypoint"]![oldEdge.data!.dmnEdge!["di:waypoint"]!.length - 1]!
            : getDmnBoundsCenterPoint(targetBounds);
          const firstWaypoint = oldEdge.data?.dmnEdge
            ? oldEdge.data!.dmnEdge!["di:waypoint"]![0]!
            : getDmnBoundsCenterPoint(sourceBounds);

          const { newDmnEdge } = addEdge({
            definitions: state.dmn.model.definitions,
            drdIndex: state.computed(state).getDrdIndex(),
            edge: {
              autoPositionedEdgeMarker: undefined,
              type: oldEdge.type as EdgeType,
              targetHandle: ((newConnection.targetHandle as PositionalNodeHandleId) ??
                getHandlePosition({ shapeBounds: targetBounds, waypoint: lastWaypoint })
                  .handlePosition) as PositionalNodeHandleId,
              sourceHandle: ((newConnection.sourceHandle as PositionalNodeHandleId) ??
                getHandlePosition({ shapeBounds: sourceBounds, waypoint: firstWaypoint })
                  .handlePosition) as PositionalNodeHandleId,
            },
            sourceNode: {
              type: sourceNode.type as NodeType,
              href: sourceNode.id,
              data: sourceNode.data,
              bounds: sourceBounds,
              shapeId: sourceNode.data.shape["@_id"],
            },
            targetNode: {
              type: targetNode.type as NodeType,
              href: targetNode.id,
              data: targetNode.data,
              bounds: targetBounds,
              index: targetNode.data.index,
              shapeId: targetNode.data.shape["@_id"],
            },
            keepWaypoints: true,
            externalModelsByNamespace,
          });

          // The DMN Edge changed nodes, so we need to delete the old one, but keep the waypoints on the same DRD.
          if (newDmnEdge["@_dmnElementRef"] !== oldEdge.id) {
            const { deletedDmnEdgeOnCurrentDrd } = deleteEdge({
              definitions: state.dmn.model.definitions,
              drdIndex: state.computed(state).getDrdIndex(),
              edge: { id: oldEdge.id, dmnObject: oldEdge.data!.dmnObject },
              mode: EdgeDeletionMode.FROM_DRG_AND_ALL_DRDS,
              externalModelsByNamespace,
            });

            const deletedWaypoints = deletedDmnEdgeOnCurrentDrd?.["di:waypoint"];

            if (oldEdge.source !== newConnection.source && deletedWaypoints) {
              newDmnEdge["di:waypoint"] = [newDmnEdge["di:waypoint"]![0], ...deletedWaypoints.slice(1)];
            }

            if (oldEdge.target !== newConnection.target && deletedWaypoints) {
              newDmnEdge["di:waypoint"] = [
                ...deletedWaypoints.slice(0, deletedWaypoints.length - 1),
                newDmnEdge["di:waypoint"]![newDmnEdge["di:waypoint"]!.length - 1],
              ];
            }
          }

          // Keep the updated edge selected
          state.diagram._selectedEdges = [newDmnEdge["@_dmnElementRef"]!];

          // Finish edge update atomically.
          state.diagram.ongoingConnection = undefined;
          state.diagram.edgeIdBeingUpdated = undefined;
        });
      },
      [dmnEditorStoreApi, externalModelsByNamespace]
    );

    const onEdgeUpdateStart = useCallback(
      (e: React.MouseEvent | React.TouchEvent, edge: RF.Edge, handleType: RF.HandleType) => {
        console.debug("DMN DIAGRAM: `onEdgeUpdateStart`");
        dmnEditorStoreApi.setState((state) => {
          state.diagram.edgeIdBeingUpdated = edge.id;
        });
      },
      [dmnEditorStoreApi]
    );

    const onEdgeUpdateEnd = useCallback(
      (e: MouseEvent | TouchEvent, edge: RF.Edge, handleType: RF.HandleType) => {
        console.debug("DMN DIAGRAM: `onEdgeUpdateEnd`");

        // Needed for when the edge update operation doesn't change anything.
        dmnEditorStoreApi.setState((state) => {
          state.diagram.ongoingConnection = undefined;
          state.diagram.edgeIdBeingUpdated = undefined;
        });
      },
      [dmnEditorStoreApi]
    );

    // Override Reactflow's behavior by intercepting the keydown event using its `capture` variant.
    const handleRfKeyDownCapture = useCallback(
      (e: React.KeyboardEvent) => {
        const s = dmnEditorStoreApi.getState();

        if (e.key === "Escape") {
          if (s.computed(s).isDiagramEditingInProgress() && dmnModelBeforeEditingRef.current) {
            console.debug(
              "DMN DIAGRAM: Intercepting Escape pressed and preventing propagation. Reverting DMN model to what it was before editing began."
            );

            e.stopPropagation();
            e.preventDefault();

            resetToBeforeEditingBegan();
          } else if (!s.diagram.ongoingConnection) {
            dmnEditorStoreApi.setState((state) => {
              if (
                state.computed(s).getDiagramData(externalModelsByNamespace).selectedNodesById.size > 0 ||
                state.computed(s).getDiagramData(externalModelsByNamespace).selectedEdgesById.size > 0
              ) {
                console.debug("DMN DIAGRAM: Esc pressed. Desselecting everything.");
                state.diagram._selectedNodes = [];
                state.diagram._selectedEdges = [];
                e.stopPropagation();
                e.preventDefault();
              } else if (
                state.computed(s).getDiagramData(externalModelsByNamespace).selectedNodesById.size <= 0 &&
                state.computed(s).getDiagramData(externalModelsByNamespace).selectedEdgesById.size <= 0
              ) {
                console.debug("DMN DIAGRAM: Esc pressed. Closing all open panels.");
                state.diagram.propertiesPanel.isOpen = false;
                state.diagram.overlaysPanel.isOpen = false;
                state.diagram.openLhsPanel = DiagramLhsPanel.NONE;
                e.stopPropagation();
                e.preventDefault();
              } else {
                // Let the
              }
            });
          } else {
            // Let the KeyboardShortcuts handle it.
          }
        }
      },
      [dmnEditorStoreApi, dmnModelBeforeEditingRef, externalModelsByNamespace, resetToBeforeEditingBegan]
    );

    const [showEmptyState, setShowEmptyState] = useState(true);

    const nodes = useDmnEditorStore((s) => s.computed(s).getDiagramData(externalModelsByNamespace).nodes);
    const edges = useDmnEditorStore((s) => s.computed(s).getDiagramData(externalModelsByNamespace).edges);
    const drgElementsWithoutVisualRepresentationOnCurrentDrdLength = useDmnEditorStore(
      (s) =>
        s.computed(s).getDiagramData(externalModelsByNamespace).drgElementsWithoutVisualRepresentationOnCurrentDrd
          .length
    );

    const isEmptyStateShowing =
      showEmptyState && nodes.length === 0 && drgElementsWithoutVisualRepresentationOnCurrentDrdLength === 0;

    const canAutoGenerateDrd = useDmnEditorStore((s) => s.diagram.autoLayout.canAutoGenerateDrd);
    const drdsLength = useDmnEditorStore((s) => s.dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.length);

    const showAutoGenerateDrdModal = useMemo(() => {
      return canAutoGenerateDrd && drdsLength === undefined;
    }, [canAutoGenerateDrd, drdsLength]);

    return (
      <>
        {showAutoGenerateDrdModal && <DmnDiagramWithoutDrd />}
        {isEmptyStateShowing && !canAutoGenerateDrd && (
          <DmnDiagramEmptyState setShowEmptyState={setShowEmptyState} isReadOnly={settings.isReadOnly} />
        )}
        <DiagramContainerContextProvider container={container}>
          <svg style={{ position: "absolute", top: 0, left: 0 }}>
            <EdgeMarkers />
          </svg>

          <RF.ReactFlow
            connectionMode={RF.ConnectionMode.Loose} // Allow target handles to be used as source. This is very important for allowing the positional handles to be updated for the base of an edge.
            onKeyDownCapture={handleRfKeyDownCapture} // Override Reactflow's keyboard listeners.
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onEdgeUpdateStart={onEdgeUpdateStart}
            onEdgeUpdateEnd={onEdgeUpdateEnd}
            onEdgeUpdate={onEdgeUpdate}
            onlyRenderVisibleElements={true}
            zoomOnDoubleClick={false}
            elementsSelectable={true}
            panOnScroll={true}
            zoomOnScroll={false}
            preventScrolling={true}
            selectionOnDrag={true}
            panOnDrag={PAN_ON_DRAG}
            selectionMode={RF.SelectionMode.Full} // For selections happening inside Groups/DecisionServices it's better to leave it as "Full"
            isValidConnection={isValidConnection}
            connectionLineComponent={ConnectionLine}
            onConnect={onConnect}
            onConnectStart={onConnectStart}
            onConnectEnd={onConnectEnd}
            // (begin)
            // 'Starting to drag' and 'dragging' should have the same behavior. Otherwise,
            // clicking a node and letting it go, without moving, won't work properly, and
            // Decisions will be removed from Decision Services.
            onNodeDragStart={onNodeDragStart}
            onNodeDrag={onNodeDrag}
            // (end)
            onNodeDragStop={onNodeDragStop}
            nodesDraggable={!settings.isReadOnly}
            nodeTypes={nodeTypes}
            edgeTypes={edgeTypes}
            snapToGrid={true}
            snapGrid={rfSnapGrid}
            defaultViewport={viewport}
            fitView={false}
            fitViewOptions={FIT_VIEW_OPTIONS}
            attributionPosition={"bottom-right"}
            onInit={setReactFlowInstance}
            deleteKeyCode={settings.isReadOnly ? [] : DELETE_NODE_KEY_CODES}
            // (begin)
            // Used to make the Palette work by dropping nodes on the Reactflow Canvas
            onDrop={onDrop}
            onDragOver={onDragOver}
            // (end)
          >
            <SelectionStatus />
            <Palette pulse={isEmptyStateShowing} />
            <TopRightCornerPanels availableHeight={container.current?.offsetHeight} />
            <DiagramCommands />
            {!isFirefox && <RF.Background />}
            <RF.Controls fitViewOptions={FIT_VIEW_OPTIONS} position={"bottom-right"} />
            <SetConnectionToReactFlowStore />
            <ViewportWatcher />
          </RF.ReactFlow>
        </DiagramContainerContextProvider>
      </>
    );
  }
);

function DmnDiagramWithoutDrd() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const settings = useSettings();
  const { externalModelsByNamespace } = useExternalModels();

  return (
    <Bullseye
      style={{
        position: "absolute",
        width: "100%",
        pointerEvents: "none",
        zIndex: 1,
        height: "auto",
        marginTop: "120px",
      }}
    >
      <div className={"kie-dmn-editor--diagram-empty-state"}>
        <Button
          title={"Close"}
          style={{
            position: "absolute",
            top: "8px",
            right: 0,
          }}
          variant={ButtonVariant.plain}
          icon={<TimesIcon />}
          onClick={() => {
            dmnEditorStoreApi.setState((s) => {
              s.diagram.autoLayout.canAutoGenerateDrd = false;
            });
          }}
        />

        <EmptyState variant={EmptyStateVariant.sm}>
          <EmptyStateHeader
            titleText="Empty Diagram"
            icon={<EmptyStateIcon icon={BlueprintIcon} />}
            headingLevel={"h4"}
          />
          <EmptyStateBody>
            The current DMN does not have any Diagram associated with it. Do you want to auto-generate it?
          </EmptyStateBody>
          <EmptyStateFooter>
            <EmptyStateActions>
              <Button
                variant={ButtonVariant.link}
                isDisabled={settings.isReadOnly}
                icon={<OptimizeIcon />}
                onClick={async () => {
                  const { computed, ...state } = dmnEditorStoreApi.getState();
                  const dereferencedState: State = { computed, ...JSON.parse(JSON.stringify(state)) };

                  const externalModelTypesByNamespace = dereferencedState
                    .computed(dereferencedState)
                    .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace);

                  autoGenerateDrd({
                    model: dereferencedState.dmn.model,
                    diagram: dereferencedState.diagram,
                    externalModelsByNamespace,
                    externalModelTypesByNamespace,
                  });

                  const snapGrid = dereferencedState.diagram.snapGrid;
                  const nodesById = dereferencedState
                    .computed(dereferencedState)
                    .getDiagramData(externalModelsByNamespace).nodesById;
                  const edgesById = dereferencedState
                    .computed(dereferencedState)
                    .getDiagramData(externalModelsByNamespace).edgesById;
                  const nodes = dereferencedState
                    .computed(dereferencedState)
                    .getDiagramData(externalModelsByNamespace).nodes;
                  const edges = dereferencedState
                    .computed(dereferencedState)
                    .getDiagramData(externalModelsByNamespace).edges;
                  const drgEdges = dereferencedState
                    .computed(dereferencedState)
                    .getDiagramData(externalModelsByNamespace).drgEdges;
                  const isAlternativeInputDataShape = dereferencedState
                    .computed(dereferencedState)
                    .isAlternativeInputDataShape();
                  const dmnShapesByHref = dereferencedState.computed(dereferencedState).indexedDrd().dmnShapesByHref;

                  // Auto layout the new DRD
                  const { __readonly_autoLayoutedInfo, __readonly_parentNodesById } = await getAutoLayoutedInfo({
                    __readonly_snapGrid: snapGrid,
                    __readonly_nodesById: nodesById,
                    __readonly_edgesById: edgesById,
                    __readonly_nodes: nodes,
                    __readonly_drgEdges: drgEdges,
                    __readonly_isAlternativeInputDataShape: isAlternativeInputDataShape,
                  });

                  dmnEditorStoreApi.setState((s) => {
                    s.diagram.autoLayout.canAutoGenerateDrd = false;
                    applyAutoLayoutToDrd({
                      state: dereferencedState,
                      __readonly_dmnShapesByHref: dmnShapesByHref,
                      __readonly_edges: edges,
                      __readonly_edgesById: edgesById,
                      __readonly_nodesById: nodesById,
                      __readonly_autoLayoutedInfo,
                      __readonly_parentNodesById,
                      __readonly_drdIndex: dereferencedState.computed(dereferencedState).getDrdIndex(),
                      __readonly_dmnObjectNamespace: dereferencedState.dmn.model.definitions["@_namespace"],
                      __readonly_externalDmnsIndex: dereferencedState
                        .computed(dereferencedState)
                        .getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns,
                      __readonly_externalModelsByNamespace: externalModelsByNamespace,
                    });
                    s.dmn.model = dereferencedState.dmn.model;
                  });
                }}
              >
                Auto-generate Diagram
              </Button>
            </EmptyStateActions>

            <br />
            <EmptyStateBody style={{ fontSize: "12px", wordBreak: "break-word" }}>
              Auto generating the diagram will automatically place the nodes with the default size and shape. You can
              also manually build your diagram using the &quot;DRG Nodes&quot; option from the palette.
            </EmptyStateBody>
          </EmptyStateFooter>
        </EmptyState>
      </div>
    </Bullseye>
  );
}

function DmnDiagramEmptyState({
  setShowEmptyState,
  isReadOnly,
}: {
  setShowEmptyState: React.Dispatch<React.SetStateAction<boolean>>;
  isReadOnly?: boolean;
}) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();
  const externalDmnModelsByNamespaceMap = useDmnEditorStore((s) =>
    s.computed(s).getExternalDmnModelsByNamespaceMap(externalModelsByNamespace)
  );
  return (
    <Bullseye
      style={{
        position: "absolute",
        width: "100%",
        pointerEvents: "none",
        zIndex: 1,
        height: "auto",
        marginTop: "120px",
      }}
    >
      <div className={"kie-dmn-editor--diagram-empty-state"}>
        <Button
          title={"Close"}
          style={{
            position: "absolute",
            top: "8px",
            right: 0,
          }}
          variant={ButtonVariant.plain}
          icon={<TimesIcon />}
          onClick={() => setShowEmptyState(false)}
        />

        <EmptyState>
          <EmptyStateHeader icon={<EmptyStateIcon icon={MousePointerIcon} />} />
          <EmptyStateFooter>
            <EmptyStateHeader titleText={<>{`This DMN's Diagram is empty`}</>} headingLevel={"h4"} />
            {isReadOnly ? (
              <>
                <EmptyStateBody>Make sure the DMN has nodes or try opening another file</EmptyStateBody>
              </>
            ) : (
              <>
                <EmptyStateBody>Start by dragging nodes from the Palette</EmptyStateBody>
                <br />
                <EmptyStateBody>or</EmptyStateBody>
                <EmptyStateFooter>
                  <EmptyStateActions>
                    <div>
                      <Button
                        variant={ButtonVariant.link}
                        icon={<TableIcon />}
                        onClick={() => {
                          dmnEditorStoreApi.setState((state) => {
                            const { href: decisionNodeHref } = addStandaloneNode({
                              definitions: state.dmn.model.definitions,
                              drdIndex: state.computed(state).getDrdIndex(),
                              newNode: {
                                type: NODE_TYPES.decision,
                                bounds: {
                                  "@_x": 100,
                                  "@_y": 100,
                                  "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.decision]({
                                    snapGrid: state.diagram.snapGrid,
                                  })["@_width"],
                                  "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.decision]({
                                    snapGrid: state.diagram.snapGrid,
                                  })["@_height"],
                                },
                              },
                              externalModelsByNamespace,
                            });

                            const drgElementIndex = (state.dmn.model.definitions.drgElement ?? []).length - 1;

                            const defaultWidthsById = new Map<string, number[]>();
                            const defaultExpression = getDefaultBoxedExpression({
                              logicType: "decisionTable",
                              allTopLevelDataTypesByFeelName: new Map(),
                              typeRef: undefined,
                              getDefaultColumnWidth,
                              widthsById: defaultWidthsById,
                            });

                            updateExpression({
                              definitions: state.dmn.model.definitions,
                              drgElementIndex,
                              expression: {
                                ...defaultExpression,
                                "@_label": "New Decision",
                              },
                              externalDmnModelsByNamespaceMap,
                            });

                            updateExpressionWidths({
                              definitions: state.dmn.model.definitions,
                              drdIndex: state.computed(state).getDrdIndex(),
                              widthsById: defaultWidthsById,
                            });

                            state.dispatch(state).boxedExpressionEditor.open(parseXmlHref(decisionNodeHref).id);
                          });
                        }}
                      >
                        New Decision Table...
                      </Button>
                      <br />
                      <Button
                        variant={ButtonVariant.link}
                        icon={<BlueprintIcon />}
                        onClick={() => {
                          dmnEditorStoreApi.setState((state) => {
                            const inputDataNodeBounds: DC__Bounds = {
                              "@_x": 100,
                              "@_y": 300,
                              "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.inputData]({
                                snapGrid: state.diagram.snapGrid,
                                isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
                              })["@_width"],
                              "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.inputData]({
                                snapGrid: state.diagram.snapGrid,
                                isAlternativeInputDataShape: state.computed(state).isAlternativeInputDataShape(),
                              })["@_height"],
                            };

                            const { href: inputDataNodeHref, shapeId: inputDataShapeId } = addStandaloneNode({
                              definitions: state.dmn.model.definitions,
                              drdIndex: state.computed(state).getDrdIndex(),
                              newNode: {
                                type: NODE_TYPES.inputData,
                                bounds: inputDataNodeBounds,
                              },
                              externalModelsByNamespace,
                            });

                            const { href: decisionNodeHref } = addConnectedNode({
                              definitions: state.dmn.model.definitions,
                              drdIndex: state.computed(state).getDrdIndex(),
                              edgeType: EDGE_TYPES.informationRequirement,
                              sourceNode: {
                                href: inputDataNodeHref,
                                type: NODE_TYPES.inputData,
                                bounds: inputDataNodeBounds,
                                shapeId: inputDataShapeId,
                              },
                              newNode: {
                                type: NODE_TYPES.decision,
                                bounds: {
                                  "@_x": 100,
                                  "@_y": 100,
                                  "@_width": DEFAULT_NODE_SIZES[NODE_TYPES.decision]({
                                    snapGrid: state.diagram.snapGrid,
                                  })["@_width"],
                                  "@_height": DEFAULT_NODE_SIZES[NODE_TYPES.decision]({
                                    snapGrid: state.diagram.snapGrid,
                                  })["@_height"],
                                },
                              },
                              externalModelsByNamespace,
                            });

                            state.diagram._selectedNodes = [decisionNodeHref];
                            state.diagram.propertiesPanel.isOpen = true;
                          });
                        }}
                      >
                        New Decision with Input Data...
                      </Button>
                    </div>
                  </EmptyStateActions>
                </EmptyStateFooter>
              </>
            )}
          </EmptyStateFooter>
        </EmptyState>
      </div>
    </Bullseye>
  );
}

export function ViewportWatcher() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  useOnViewportChange({
    onChange: (viewport: Viewport) => {
      dmnEditorStoreApi.setState((state) => {
        state.diagram.viewport = {
          x: viewport.x,
          y: viewport.y,
          zoom: viewport.zoom,
        };
      });
    },
  });
  return <></>;
}

export function SetConnectionToReactFlowStore(props: {}) {
  const ongoingConnection = useDmnEditorStore((s) => s.diagram.ongoingConnection);
  const rfStoreApi = RF.useStoreApi();
  useEffect(() => {
    rfStoreApi.setState({
      connectionHandleId: ongoingConnection?.handleId,
      connectionHandleType: ongoingConnection?.handleType,
      connectionNodeId: ongoingConnection?.nodeId,
    });
  }, [ongoingConnection?.handleId, ongoingConnection?.handleType, ongoingConnection?.nodeId, rfStoreApi]);

  return <></>;
}

interface TopRightCornerPanelsProps {
  availableHeight?: number | undefined;
}

export function TopRightCornerPanels({ availableHeight }: TopRightCornerPanelsProps) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const settings = useSettings();

  const togglePropertiesPanel = useCallback(() => {
    dmnEditorStoreApi.setState((state) => {
      state.diagram.propertiesPanel.isOpen = !state.diagram.propertiesPanel.isOpen;
    });
  }, [dmnEditorStoreApi]);

  const toggleOverlaysPanel = useCallback(() => {
    dmnEditorStoreApi.setState((state) => {
      state.diagram.overlaysPanel.isOpen = !state.diagram.overlaysPanel.isOpen;
    });
  }, [dmnEditorStoreApi]);

  useLayoutEffect(() => {
    dmnEditorStoreApi.setState((state) => {
      if (state.diagram.overlaysPanel.isOpen) {
        // This is necessary to make sure that the Popover is open at the correct position.
        setTimeout(() => {
          dmnEditorStoreApi.setState((state) => {
            state.diagram.overlaysPanel.isOpen = true;
          });
        }, 300); // That's the animation duration to open/close the properties panel.
      }
      state.diagram.overlaysPanel.isOpen = false;
    });
  }, [dmnEditorStoreApi, diagram.propertiesPanel.isOpen]);

  return (
    <>
      <RF.Panel position={"top-right"}>
        <Flex>
          {diagram.overlays.enableEvaluationHighlights && <EvaluationHighlightsBadge />}
          {!settings.isReadOnly && (
            <aside className={"kie-dmn-editor--autolayout-panel-toggle"}>
              <AutolayoutButton />
            </aside>
          )}
          <aside className={"kie-dmn-editor--overlays-panel-toggle"}>
            <Popover
              className={"kie-dmn-editor--overlay-panel-popover"}
              key={`${diagram.overlaysPanel.isOpen}`}
              aria-label="Overlays Panel"
              position={"bottom-end"}
              enableFlip={false}
              flipBehavior={["bottom-end"]}
              hideOnOutsideClick={false}
              showClose={false}
              isVisible={diagram.overlaysPanel.isOpen}
              bodyContent={<OverlaysPanel availableHeight={(availableHeight ?? 0) - AREA_ABOVE_OVERLAYS_PANEL} />}
            >
              <button
                className={"kie-dmn-editor--overlays-panel-toggle-button"}
                onClick={toggleOverlaysPanel}
                title={"Overlays"}
              >
                <VirtualMachineIcon />
              </button>
            </Popover>
          </aside>
          {!diagram.propertiesPanel.isOpen && (
            <aside className={"kie-dmn-editor--properties-panel-toggle"}>
              <button
                className={"kie-dmn-editor--properties-panel-toggle-button"}
                onClick={togglePropertiesPanel}
                title={"Properties panel"}
              >
                <InfoIcon />
              </button>
            </aside>
          )}
        </Flex>
      </RF.Panel>
    </>
  );
}

export function SelectionStatus() {
  const rfStoreApi = RF.useStoreApi();

  const { externalModelsByNamespace } = useExternalModels();
  const selectedNodesCount = useDmnEditorStore(
    (s) => s.computed(s).getDiagramData(externalModelsByNamespace).selectedNodesById.size
  );
  const selectedEdgesCount = useDmnEditorStore(
    (s) => s.computed(s).getDiagramData(externalModelsByNamespace).selectedEdgesById.size
  );
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  useEffect(() => {
    if (selectedNodesCount >= 2) {
      rfStoreApi.setState({ nodesSelectionActive: true });
    }
  }, [rfStoreApi, selectedNodesCount]);

  const onClose = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      dmnEditorStoreApi.setState((state) => {
        state.diagram._selectedNodes = [];
        state.diagram._selectedEdges = [];
      });
    },
    [dmnEditorStoreApi]
  );

  return (
    <>
      {(selectedNodesCount + selectedEdgesCount >= 2 && (
        <RF.Panel position={"top-center"}>
          <Label style={{ paddingLeft: "24px" }} onClose={onClose}>
            {(selectedEdgesCount === 0 && `${selectedNodesCount} nodes selected`) ||
              (selectedNodesCount === 0 && `${selectedEdgesCount} edges selected`) ||
              `${selectedNodesCount} node${selectedNodesCount === 1 ? "" : "s"}, ${selectedEdgesCount} edge${
                selectedEdgesCount === 1 ? "" : "s"
              } selected`}
          </Label>
        </RF.Panel>
      )) || <></>}
    </>
  );
}
