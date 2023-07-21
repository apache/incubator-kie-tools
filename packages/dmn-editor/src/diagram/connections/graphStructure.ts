import { NODE_TYPES } from "../nodes/NodeTypes";
import { EDGE_TYPES } from "../edges/EdgeTypes";

type Values<T> = T[keyof T];

export type NodeType = Values<typeof NODE_TYPES>;
export type EdgeType = Values<typeof EDGE_TYPES>;

export const graphStructure: Map<NodeType, Map<EdgeType, Set<NodeType>>> = new Map([
  [
    NODE_TYPES.inputData,
    new Map<EdgeType, Set<NodeType>>([
      [EDGE_TYPES.informationRequirement, new Set([NODE_TYPES.decision])],
      [EDGE_TYPES.authorityRequirement, new Set([NODE_TYPES.knowledgeSource])],
      [EDGE_TYPES.association, new Set([NODE_TYPES.textAnnotation])],
    ]),
  ],
  [
    NODE_TYPES.decision,
    new Map<EdgeType, Set<NodeType>>([
      [EDGE_TYPES.informationRequirement, new Set([NODE_TYPES.decision])],
      [EDGE_TYPES.authorityRequirement, new Set([NODE_TYPES.knowledgeSource])],
      [EDGE_TYPES.association, new Set([NODE_TYPES.textAnnotation])],
    ]),
  ],
  [
    NODE_TYPES.bkm,
    new Map<EdgeType, Set<NodeType>>([
      [EDGE_TYPES.knowledgeRequirement, new Set([NODE_TYPES.decision, NODE_TYPES.bkm])],
      [EDGE_TYPES.association, new Set([NODE_TYPES.textAnnotation])],
    ]),
  ],
  [
    NODE_TYPES.decisionService,
    new Map<EdgeType, Set<NodeType>>([
      [EDGE_TYPES.knowledgeRequirement, new Set([NODE_TYPES.decision, NODE_TYPES.bkm])],
      [EDGE_TYPES.association, new Set([NODE_TYPES.textAnnotation])],
    ]),
  ],
  [
    NODE_TYPES.knowledgeSource,
    new Map<EdgeType, Set<NodeType>>([
      [EDGE_TYPES.authorityRequirement, new Set([NODE_TYPES.decision, NODE_TYPES.bkm, NODE_TYPES.knowledgeSource])],
    ]),
  ],
  [
    NODE_TYPES.textAnnotation,
    new Map<EdgeType, Set<NodeType>>([
      [
        EDGE_TYPES.association,
        new Set([
          NODE_TYPES.inputData,
          NODE_TYPES.decision,
          NODE_TYPES.bkm,
          NODE_TYPES.decisionService,
          NODE_TYPES.knowledgeSource,
        ]),
      ],
    ]),
  ],
  [NODE_TYPES.group, new Map<EdgeType, Set<NodeType>>()],
]);

export const outgoing = {
  inputData: {
    nodes: outgoingNodes(NODE_TYPES.inputData),
    edges: outgoingEdges(NODE_TYPES.inputData),
  },
  decision: {
    nodes: outgoingNodes(NODE_TYPES.decision),
    edges: outgoingEdges(NODE_TYPES.decision),
  },
  bkm: {
    nodes: outgoingNodes(NODE_TYPES.bkm),
    edges: outgoingEdges(NODE_TYPES.bkm),
  },
  decisionService: {
    nodes: outgoingNodes(NODE_TYPES.decisionService),
    edges: outgoingEdges(NODE_TYPES.decisionService),
  },
  knowledgeSource: {
    nodes: outgoingNodes(NODE_TYPES.knowledgeSource),
    edges: outgoingEdges(NODE_TYPES.knowledgeSource),
  },
  textAnnotation: {
    nodes: [],
    edges: outgoingEdges(NODE_TYPES.textAnnotation),
  },
};

function outgoingNodes(srcNodeType: NodeType) {
  return Array.from((graphStructure.get(srcNodeType) ?? new Map()).values()).flatMap((tgt) => [...tgt]);
}

function outgoingEdges(srcNodeType: NodeType) {
  return Array.from((graphStructure.get(srcNodeType) ?? new Map()).keys());
}
