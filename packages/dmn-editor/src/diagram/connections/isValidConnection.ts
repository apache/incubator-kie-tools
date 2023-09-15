import * as RF from "reactflow";
import { EdgeType, NodeType, graphStructure } from "./graphStructure";
import { DmnDiagramNodeData } from "../nodes/Nodes";

export function checkIsValidConnection(
  nodesById: Map<string, RF.Node<DmnDiagramNodeData>>,
  edgeOrConnection: RF.Edge | RF.Connection
) {
  if (!edgeOrConnection.source || !edgeOrConnection.target) {
    return false;
  }

  const sourceNode = nodesById.get(edgeOrConnection.source);
  const targetNode = nodesById.get(edgeOrConnection.target);

  return _checkIsValidConnection(sourceNode, targetNode, edgeOrConnection.sourceHandle);
}

export function _checkIsValidConnection(
  sourceNode: { type?: string; data: DmnDiagramNodeData } | undefined,
  targetNode: { type?: string; data: DmnDiagramNodeData } | undefined,
  edgeType: string | null | undefined
) {
  if (!sourceNode?.type || !targetNode?.type || !edgeType) {
    return false;
  }

  // External nodes cannot be targeted
  if (targetNode.data.dmnObjectQName.prefix) {
    return false;
  }

  const ret =
    (graphStructure
      .get(sourceNode.type as NodeType)
      ?.get(edgeType as EdgeType)
      ?.has(targetNode.type as NodeType) ??
      false) ||
    edgeType.startsWith("source-");

  return ret;
}
