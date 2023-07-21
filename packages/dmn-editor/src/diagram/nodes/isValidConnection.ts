import { Connection, Edge, Node } from "reactflow";
import { outgoing } from "../edges/OutgoingHandleIds";

export function checkIsValidConnection(nodesById: Map<string, Node>, edge: Edge | Connection) {
  if (!edge.source || !edge.target) {
    return false;
  }

  const sourceNode = nodesById.get(edge.source);
  const targetNode = nodesById.get(edge.target);

  return _checkIsValidConnection(sourceNode, targetNode, edge.sourceHandle);
}

export function _checkIsValidConnection(
  sourceNode: { type?: string } | undefined,
  targetNode: { type?: string } | undefined,
  sourceHandleId: string | null | undefined
) {
  if (!sourceNode?.type || !targetNode?.type || !sourceHandleId) {
    return false;
  }

  return outgoing.get(sourceNode.type)?.get(sourceHandleId)?.has(targetNode.type) ?? false;
}
