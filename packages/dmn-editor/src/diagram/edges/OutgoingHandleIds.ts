import { NODE_TYPES, EDGE_TYPES } from "../nodes/NodeTypes";

export const outgoing = new Map<string, Map<string, Set<string>>>([
  [
    NODE_TYPES.inputData,
    new Map([
      [EDGE_TYPES.informationRequirement, new Set([NODE_TYPES.decision])],
      [EDGE_TYPES.authorityRequirement, new Set([NODE_TYPES.knowledgeSource])],
      [EDGE_TYPES.association, new Set([NODE_TYPES.textAnnotation])],
    ]),
  ],
  [
    NODE_TYPES.decision,
    new Map([
      [EDGE_TYPES.informationRequirement, new Set([NODE_TYPES.decision])],
      [EDGE_TYPES.authorityRequirement, new Set([NODE_TYPES.knowledgeSource])],
      [EDGE_TYPES.association, new Set([NODE_TYPES.textAnnotation])],
    ]),
  ],
  [
    NODE_TYPES.bkm,
    new Map([
      [EDGE_TYPES.knowledgeRequirement, new Set([NODE_TYPES.decision, NODE_TYPES.bkm])],
      [EDGE_TYPES.association, new Set([NODE_TYPES.textAnnotation])],
    ]),
  ],
  [
    NODE_TYPES.decisionService,
    new Map([
      [EDGE_TYPES.knowledgeRequirement, new Set([NODE_TYPES.decision, NODE_TYPES.bkm])],
      [EDGE_TYPES.association, new Set([NODE_TYPES.textAnnotation])],
    ]),
  ],
  [
    NODE_TYPES.knowledgeSource,
    new Map([
      [EDGE_TYPES.authorityRequirement, new Set([NODE_TYPES.decision, NODE_TYPES.bkm, NODE_TYPES.knowledgeSource])],
    ]),
  ],
  [
    NODE_TYPES.textAnnotation,
    new Map([
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
  [NODE_TYPES.group, new Map([[EDGE_TYPES.association, new Set([])]])],
]);

function outgoingNodes(srcNodeType: string) {
  return Array.from(outgoing.get(srcNodeType)!.values()).flatMap((tgt) => [...tgt]);
}

function outgoingEdges(srcNodeType: string) {
  return Array.from(outgoing.get(srcNodeType)!.keys());
}

export const inputDataOutgoing = {
  nodes: outgoingNodes(NODE_TYPES.inputData),
  edges: outgoingEdges(NODE_TYPES.inputData),
};
export const decisionOutgoing = {
  nodes: outgoingNodes(NODE_TYPES.decision),
  edges: outgoingEdges(NODE_TYPES.decision),
};
export const bkmOutgoing = {
  nodes: outgoingNodes(NODE_TYPES.bkm),
  edges: outgoingEdges(NODE_TYPES.bkm),
};
export const decisionServiceOutgoing = {
  nodes: outgoingNodes(NODE_TYPES.decisionService),
  edges: outgoingEdges(NODE_TYPES.decisionService),
};
export const knowledgeSourceOutgoing = {
  nodes: outgoingNodes(NODE_TYPES.knowledgeSource),
  edges: outgoingEdges(NODE_TYPES.knowledgeSource),
};
export const textAnnotationOutgoing = {
  nodes: [],
  edges: outgoingEdges(NODE_TYPES.textAnnotation),
};
