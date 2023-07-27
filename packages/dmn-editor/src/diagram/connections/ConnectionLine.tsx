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

export function ConnectionLine({ toX, toY, fromNode, fromHandle }: RF.ConnectionLineComponentProps) {
  const { "@_x": fromX, "@_y": fromY } = getNodeCenterPoint(fromNode);

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
  else {
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint({ "@_x": toX, "@_y": toY });
    const [toXauto, toYauto] = getPositionalHandlePosition(
      { x: toXsnapped, y: toYsnapped, width: MIN_SIZE_FOR_NODES.width, height: MIN_SIZE_FOR_NODES.height },
      { x: fromX, y: fromY, width: 1, height: 1 }
    );

    if (fromHandle?.id === NODE_TYPES.decision) {
      return (
        <g>
          <InformationRequirementPath d={`M${fromX},${fromY} L${toXauto},${toYauto}`} />
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
      return (
        <g className={"pulse"}>
          <KnowledgeRequirementPath d={`M${fromX},${fromY} L${toXauto},${toYauto}`} />
          <BkmNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={MIN_SIZE_FOR_NODES.width}
            height={MIN_SIZE_FOR_NODES.height}
          />
        </g>
      );
    }
    //
    else if (fromHandle?.id === NODE_TYPES.knowledgeSource) {
      return (
        <g>
          <AuthorityRequirementPath d={`M${fromX},${fromY} L${toXauto},${toYauto}`} centerToConnectionPoint={false} />
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
      return (
        <g>
          <AssociationPath d={`M${fromX},${fromY} L${toXauto},${toYauto}`} />
          <TextAnnotationNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={MIN_SIZE_FOR_NODES.width}
            height={MIN_SIZE_FOR_NODES.height}
          />
        </g>
      );
    }
  }

  throw new Error(`Unknown source of ConnectionLine '${fromHandle?.id}'.`);
}
