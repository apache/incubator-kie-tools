import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { NODE_TYPES } from "./nodes/NodeTypes";

export type DmnNodeWithExpression =
  | {
      type: typeof NODE_TYPES.bkm;
      content: DMN14__tBusinessKnowledgeModel;
    }
  | {
      type: typeof NODE_TYPES.decision;
      content: DMN14__tDecision;
    };
