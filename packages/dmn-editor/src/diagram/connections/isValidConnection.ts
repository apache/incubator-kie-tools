import { Connection, Edge, Node } from "reactflow";
import { EdgeType, NodeType, graphStructure } from "./graphStructure";

export function checkIsValidConnection(nodesById: Map<string, Node>, edgeOrConnection: Edge | Connection) {
  if (!edgeOrConnection.source || !edgeOrConnection.target) {
    return false;
  }

  const sourceNode = nodesById.get(edgeOrConnection.source);
  const targetNode = nodesById.get(edgeOrConnection.target);

  return _checkIsValidConnection(sourceNode, targetNode, edgeOrConnection.sourceHandle);
}

export function _checkIsValidConnection(
  sourceNode: { type?: string } | undefined,
  targetNode: { type?: string } | undefined,
  edgeType: string | null | undefined
) {
  if (!sourceNode?.type || !targetNode?.type || !edgeType) {
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
