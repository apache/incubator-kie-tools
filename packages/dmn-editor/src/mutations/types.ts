import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";

export enum NodeNature {
  DRG_ELEMENT = "DRG_ELEMENT",
  ARTIFACT = "ARTIFACT",
}

export const nodeNatures: Record<NodeType, NodeNature> = {
  [NODE_TYPES.inputData]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.decision]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.bkm]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.knowledgeSource]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.decisionService]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.textAnnotation]: NodeNature.ARTIFACT,
  [NODE_TYPES.group]: NodeNature.ARTIFACT,
};
