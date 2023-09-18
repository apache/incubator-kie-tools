import { XmlQName } from "../../xml/xmlQNames";
import { NodeType, containment } from "./graphStructure";

export function isValidContainment({
  nodeTypes,
  inside,
  dmnObjectQName,
}: {
  nodeTypes: Set<NodeType>;
  inside: NodeType;
  dmnObjectQName: XmlQName;
}) {
  // Can't put anything inside external nodes;
  if (dmnObjectQName.prefix) {
    return false;
  }

  const allowedNodesInside = containment.get(inside);
  return [...nodeTypes].every((nodeType) => allowedNodesInside?.has(nodeType));
}
