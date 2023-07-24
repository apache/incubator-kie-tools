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
import { BkmNodeSvg, DecisionNodeSvg, KnowledgeSourceNodeSvg, TextAnnotationNodeSvg } from "../nodes/Nodes";

export function ConnectionLine({ toX, toY, fromNode, fromHandle }: RF.ConnectionLineComponentProps) {
  const { "@_x": fromX, "@_y": fromY } = snapPoint({
    "@_x": (fromNode?.position.x ?? 0) + (fromNode?.width ?? 0) / 2,
    "@_y": (fromNode?.position.y ?? 0) + (fromNode?.height ?? 0) / 2,
  });

  if (fromHandle?.id === EDGE_TYPES.informationRequirement) {
    return <InformationRequirementPath d={`M${fromX},${fromY} L${toX},${toY}`} />;
  }
  //
  else if (fromHandle?.id === EDGE_TYPES.knowledgeRequirement) {
    return <KnowledgeRequirementPath d={`M${fromX},${fromY} L${toX},${toY}`} />;
  }
  //
  else if (fromHandle?.id === EDGE_TYPES.authorityRequirement) {
    return <AuthorityRequirementPath d={`M${fromX},${fromY} L${toX},${toY}`} centerToConnectionPoint={true} />;
  }
  //
  else if (fromHandle?.id === EDGE_TYPES.association) {
    return <AssociationPath d={`M${fromX},${fromY} L${toX},${toY}`} />;
  }
  //
  else if (fromHandle?.id === NODE_TYPES.decision) {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    return (
      <g>
        <InformationRequirementPath d={`M${fromX},${fromY} L${toXsnapped},${toYsnapped}`} />
        <DecisionNodeSvg
          x={toXsnapped}
          y={toYsnapped}
          width={MIN_SIZE_FOR_NODES.width}
          height={MIN_SIZE_FOR_NODES.height}
        />
      </g>
    );
  }
  //
  else if (fromHandle?.id === NODE_TYPES.bkm) {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    return (
      <g className={"pulse"}>
        <KnowledgeRequirementPath d={`M${fromX},${fromY} L${toXsnapped},${toYsnapped}`} />
        <BkmNodeSvg x={toXsnapped} y={toYsnapped} width={MIN_SIZE_FOR_NODES.width} height={MIN_SIZE_FOR_NODES.height} />
      </g>
    );
  }
  //
  else if (fromHandle?.id === NODE_TYPES.knowledgeSource) {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    return (
      <g>
        <AuthorityRequirementPath
          d={`M${fromX},${fromY} L${toXsnapped},${toYsnapped}`}
          centerToConnectionPoint={false}
        />
        <KnowledgeSourceNodeSvg
          x={toXsnapped}
          y={toYsnapped}
          width={MIN_SIZE_FOR_NODES.width}
          height={MIN_SIZE_FOR_NODES.height}
        />
      </g>
    );
  }
  //
  else if (fromHandle?.id === NODE_TYPES.textAnnotation) {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    return (
      <g>
        <AssociationPath d={`M${fromX},${fromY} L${toXsnapped},${toYsnapped}`} />
        <TextAnnotationNodeSvg
          x={toXsnapped}
          y={toYsnapped}
          width={MIN_SIZE_FOR_NODES.width}
          height={MIN_SIZE_FOR_NODES.height}
        />
      </g>
    );
  }
  //
  else {
    throw new Error(`Unknown source of ConnectionLine '${fromHandle?.id}'.`);
  }
}
