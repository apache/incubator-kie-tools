import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import {
  DMN15__tDecisionService,
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { TargetHandleId } from "../diagram/connections/PositionalTargetNodeHandles";
import { NodeType } from "../diagram/connections/graphStructure";
import { getDecisionServiceDividerLineLocalY, getHandlePosition } from "../diagram/maths/DmnMaths";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { repositionNode } from "./repositionNode";
import { getCentralizedDecisionServiceDividerLine } from "./updateDecisionServiceDividerLine";

export function resizeNode({
  definitions,
  dmnShapesByHref,
  change,
}: {
  definitions: DMN15__tDefinitions;
  dmnShapesByHref: Map<string, DMNDI15__DMNShape & { index: number }>;
  change: {
    nodeType: NodeType;
    isExternal: boolean;
    index: number;
    shapeIndex: number;
    dimension: { "@_width": number; "@_height": number };
    sourceEdgeIndexes: number[];
    targetEdgeIndexes: number[];
  };
}) {
  const edgeIndexesAlreadyUpdated = new Set<number>();

  const { diagramElements } = addOrGetDefaultDiagram({ definitions });

  const shape = diagramElements?.[change.shapeIndex] as DMNDI15__DMNShape | undefined;
  const shapeBounds = shape?.["dc:Bounds"];
  if (!shapeBounds) {
    throw new Error("Cannot resize non-existent shape bounds");
  }

  const deltaWidth = change.dimension["@_width"] - (shapeBounds?.["@_width"] ?? 0);
  const deltaHeight = change.dimension["@_height"] - (shapeBounds?.["@_height"] ?? 0);

  const offsetByPosition = (position: TargetHandleId | undefined) => {
    return switchExpression(position, {
      [TargetHandleId.TargetCenter]: { x: deltaWidth / 2, y: deltaHeight / 2 },
      [TargetHandleId.TargetTop]: { x: deltaWidth / 2, y: 0 },
      [TargetHandleId.TargetRight]: { x: deltaWidth, y: deltaHeight / 2 },
      [TargetHandleId.TargetBottom]: { x: deltaWidth / 2, y: deltaHeight },
      [TargetHandleId.TargetLeft]: { x: 0, y: deltaHeight / 2 },
    });
  };

  const offsetEdges = (args: { edgeIndexes: number[]; waypointSelector: "last" | "first" }) => {
    for (const edgeIndex of args.edgeIndexes) {
      if (edgeIndexesAlreadyUpdated.has(edgeIndex)) {
        continue;
      }

      edgeIndexesAlreadyUpdated.add(edgeIndex);

      const edge = diagramElements[edgeIndex] as DMNDI15__DMNEdge | undefined;
      if (!edge || !edge["di:waypoint"]) {
        throw new Error("Cannot reposition non-existent edge");
      }

      const waypoint = switchExpression(args.waypointSelector, {
        first: edge["di:waypoint"][0],
        last: edge["di:waypoint"][edge["di:waypoint"].length - 1],
      });

      const offset = offsetByPosition(getHandlePosition({ shapeBounds, waypoint }).handlePosition);
      waypoint["@_x"] += offset.x;
      waypoint["@_y"] += offset.y;
    }
  };

  if (change.nodeType === NODE_TYPES.decisionService) {
    const ds = definitions.drgElement![change.index] as DMN15__tDecisionService;

    shape["dmndi:DMNDecisionServiceDividerLine"] ??= getCentralizedDecisionServiceDividerLine(shapeBounds);
    const dividerLinePoints = shape["dmndi:DMNDecisionServiceDividerLine"]["di:waypoint"]!;

    const dividerLinelocalY = getDecisionServiceDividerLineLocalY(shape);
    const encapsulatedDecisionsOffset = (dividerLinelocalY * deltaHeight) / shapeBounds["@_height"]; // We proportionally increase the Encapuslated Decisions area, based on where the Divider Line is positioned.
    const outputDecisionsOffset = deltaHeight - encapsulatedDecisionsOffset;

    // Don't need to assign to itself. Here just for "completeness".
    // dividerLinePoints[0]["@_x"] = dividerLinePoints[0]["@_x"];
    dividerLinePoints[0]["@_y"] = dividerLinePoints[0]["@_y"] + encapsulatedDecisionsOffset;

    dividerLinePoints[1]["@_x"] = dividerLinePoints[1]["@_x"] + deltaWidth / 2;
    dividerLinePoints[1]["@_y"] = dividerLinePoints[1]["@_y"] + encapsulatedDecisionsOffset;

    // We ignore handling the contents of the Decision Service when it is external
    if (!change.isExternal) {
      const controlWaypointsByEdge = new Map<number, Set<number>>();

      // Encapsulated Decisions should be moved together with the node, as they're in the lower portion of the Decision Service.
      ds.encapsulatedDecision?.forEach((ed) => {
        repositionNode({
          definitions,
          controlWaypointsByEdge,
          change: {
            nodeType: NODE_TYPES.decision,
            type: "offset",
            offset: { deltaX: 0, deltaY: Math.round(encapsulatedDecisionsOffset) },
            selectedEdges: [], // FIXME: Tiago --> Waypoints are not being moved. We need to get the intenral sub-graph of the Encapsulated Decisions on the Decision Service and move those edges too.
            shapeIndex: dmnShapesByHref.get(ed["@_href"])!.index,
            sourceEdgeIndexes: [], // FIXME: Tiago --> This is wrong, as edge tips won't be updated, causing connections to fallback to automatic continous positioning
            targetEdgeIndexes: [], // FIXME: Tiago --> This is wrong, as edge tips won't be updated, causing connections to fallback to automatic continous positioning
          },
        });
      });

      // if (outputDecisionsOffset < 0) {
      //   ds.outputDecision?.forEach((od) => {
      //     const shape = dmnShapesByHref.get(od["@_href"]);
      //     const potentialNewY = shape!["dc:Bounds"]!["@_y"]! + outputDecisionsOffset;
      //     repositionNode({
      //       definitions,
      //       edgeIndexesAlreadyUpdated: new Set(),
      //       change: {
      //         nodeType: NODE_TYPES.decision,
      //         type: "absolute",
      //         position: {
      //           x: shape!["dc:Bounds"]!["@_x"]!,
      //           y: potentialNewY < shapeBounds["@_y"] ? shapeBounds["@_y"] : potentialNewY,
      //         },
      //         selectedEdges: [], // FIXME: Tiago --> Waypoints are not being moved. We need to get the intenral sub-graph of the Encapsulated Decisions on the Decision Service and move those edges too.
      //         shapeIndex: shape!.index,
      //         sourceEdgeIndexes: [], // FIXME: Tiago --> This is wrong, as edge tips won't be updated, causing connections to fallback to automatic continous positioning
      //         targetEdgeIndexes: [], // FIXME: Tiago --> This is wrong, as edge tips won't be updated, causing connections to fallback to automatic continous positioning
      //       },
      //     });
      //   });
      // }

      // const getBoundsFromDmnElementRefs = (refs: DMN15__tDMNElementReference[]) =>
      //   getBounds({
      //     padding: 0,
      //     nodes: refs.map((ref) => {
      //       const bounds = dmnShapesByHref.get(ref["@_href"])!["dc:Bounds"]!;
      //       return {
      //         width: bounds["@_width"],
      //         height: bounds["@_height"],
      //         position: { x: bounds["@_x"], y: bounds["@_y"] },
      //         selected: true,
      //       };
      //     }),
      //   });

      // const outputDecisionBounds = getBoundsFromDmnElementRefs(ds.outputDecision ?? []);
      // const encapsulatedDecisionBounds = getBoundsFromDmnElementRefs(ds.encapsulatedDecision ?? []);

      // const outputDecisionsContaiment = getContainmentRelationship({
      //   bounds: outputDecisionBounds,
      //   container: { ...shapeBounds, ...change.dimension },
      //   divingLineLocalY: getDecisionServiceDividerLineLocalY(shape),
      // });
      // const encapsulatedDecisionsContainment = getContainmentRelationship({
      //   bounds: encapsulatedDecisionBounds,
      //   container: { ...shapeBounds, ...change.dimension },
      //   divingLineLocalY: getDecisionServiceDividerLineLocalY(shape),
      // });

      // if (
      //   !(outputDecisionsContaiment.isInside && outputDecisionsContaiment.section === "upper") ||
      //   !(encapsulatedDecisionsContainment.isInside && encapsulatedDecisionsContainment.section === "lower")
      // ) {
      //   throw new Error("Can't resize anymore. Output and Encapsulated Decisions won't fit!");
      // }
    }
  }

  // Reposition edges after resizing
  offsetEdges({ edgeIndexes: change.sourceEdgeIndexes, waypointSelector: "first" });
  offsetEdges({ edgeIndexes: change.targetEdgeIndexes, waypointSelector: "last" });

  // Update at the end because we need the original shapeBounds value to correctly identify the position of the edges
  shapeBounds["@_width"] = change.dimension["@_width"];
  shapeBounds["@_height"] = change.dimension["@_height"];
}
