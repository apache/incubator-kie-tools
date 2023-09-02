import * as React from "react";
import * as RF from "reactflow";
import { MIN_SIZE_FOR_NODES, snapPoint } from "../SnapGrid";
import { EDGE_TYPES } from "../edges/EdgeTypes";
import {
  AssociationPath,
  AuthorityRequirementPath,
  InformationRequirementPath,
  KnowledgeRequirementPath,
} from "../edges/Edges";
import { NODE_TYPES } from "../nodes/NodeTypes";
import { getPositionalHandlePosition } from "../maths/Maths";
import { DecisionNodeSvg, BkmNodeSvg, KnowledgeSourceNodeSvg, TextAnnotationNodeSvg } from "../nodes/NodeSvgs";
import { getNodeCenterPoint } from "../maths/DmnMaths";
import { NodeType, getDefaultEdgeTypeBetween } from "./graphStructure";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { DEFAULT_NODE_SIZES } from "../nodes/DefaultSizes";
import { useDmnEditorStore } from "../../store/Store";

export function ConnectionLine({ toX, toY, fromNode, fromHandle }: RF.ConnectionLineComponentProps) {
  const { "@_x": fromX, "@_y": fromY } = getNodeCenterPoint(fromNode);
  const diagram = useDmnEditorStore((s) => s.diagram);

  // Edges
  if (fromHandle?.id === EDGE_TYPES.informationRequirement) {
    return <InformationRequirementPath d={`M${fromX},${fromY} L${toX},${toY}`} />;
  } else if (fromHandle?.id === EDGE_TYPES.knowledgeRequirement) {
    return <KnowledgeRequirementPath d={`M${fromX},${fromY} L${toX},${toY}`} />;
  } else if (fromHandle?.id === EDGE_TYPES.authorityRequirement) {
    return <AuthorityRequirementPath d={`M${fromX},${fromY} L${toX},${toY}`} centerToConnectionPoint={true} />;
  } else if (fromHandle?.id === EDGE_TYPES.association) {
    return <AssociationPath d={`M${fromX},${fromY} L${toX},${toY}`} />;
  }
  // Nodes
  else {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint(diagram.snapGrid, { "@_x": toX, "@_y": toY });

    const minSize = MIN_SIZE_FOR_NODES(diagram.snapGrid);
    const [toXauto, toYauto] = getPositionalHandlePosition(
      { x: toXsnapped, y: toYsnapped, width: minSize.width, height: minSize.height },
      { x: fromX, y: fromY, width: 1, height: 1 }
    );

    const edge = getDefaultEdgeTypeBetween(fromNode?.type as NodeType, fromHandle?.id as NodeType);
    if (!edge) {
      throw new Error(`Invalid structure: ${fromNode?.type} --(any)--> ${fromHandle?.id}`);
    }

    const path = `M${fromX},${fromY} L${toXauto},${toYauto}`;

    const edgeSvg = switchExpression(edge, {
      [EDGE_TYPES.informationRequirement]: <InformationRequirementPath d={path} />,
      [EDGE_TYPES.knowledgeRequirement]: <KnowledgeRequirementPath d={path} />,
      [EDGE_TYPES.authorityRequirement]: <AuthorityRequirementPath d={path} centerToConnectionPoint={false} />,
      [EDGE_TYPES.association]: <AssociationPath d={path} />,
    });

    if (fromHandle?.id === NODE_TYPES.decision) {
      return (
        <g>
          {edgeSvg}
          <DecisionNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={DEFAULT_NODE_SIZES[NODE_TYPES.decision](diagram.snapGrid)["@_width"]}
            height={DEFAULT_NODE_SIZES[NODE_TYPES.decision](diagram.snapGrid)["@_height"]}
          />
        </g>
      );
    } else if (fromHandle?.id === NODE_TYPES.bkm) {
      return (
        <g className={"pulse"}>
          {edgeSvg}
          <BkmNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={DEFAULT_NODE_SIZES[NODE_TYPES.bkm](diagram.snapGrid)["@_width"]}
            height={DEFAULT_NODE_SIZES[NODE_TYPES.bkm](diagram.snapGrid)["@_height"]}
          />
        </g>
      );
    } else if (fromHandle?.id === NODE_TYPES.knowledgeSource) {
      return (
        <g>
          {edgeSvg}
          <KnowledgeSourceNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={DEFAULT_NODE_SIZES[NODE_TYPES.knowledgeSource](diagram.snapGrid)["@_width"]}
            height={DEFAULT_NODE_SIZES[NODE_TYPES.knowledgeSource](diagram.snapGrid)["@_height"]}
          />
        </g>
      );
    } else if (fromHandle?.id === NODE_TYPES.textAnnotation) {
      return (
        <g>
          {edgeSvg}
          <TextAnnotationNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={DEFAULT_NODE_SIZES[NODE_TYPES.textAnnotation](diagram.snapGrid)["@_width"]}
            height={DEFAULT_NODE_SIZES[NODE_TYPES.textAnnotation](diagram.snapGrid)["@_height"]}
          />
        </g>
      );
    }
  }

  throw new Error(`Unknown source of ConnectionLine '${fromHandle?.id}'.`);
}
