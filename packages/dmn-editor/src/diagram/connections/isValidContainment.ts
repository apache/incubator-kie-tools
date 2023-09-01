import { NodeType, containment } from "./graphStructure";

export function isValidContainment({ node, inside }: { node: NodeType; inside: NodeType }) {
  return containment.get(inside)?.has(node);
}
