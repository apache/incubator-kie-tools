import { NodeType, containment } from "./graphStructure";

export function isValidContainment({ nodeTypes, inside }: { nodeTypes: Set<NodeType>; inside: NodeType }) {
  const allowedNodesInside = containment.get(inside);
  return [...nodeTypes].every((nodeType) => allowedNodesInside?.has(nodeType));
}
