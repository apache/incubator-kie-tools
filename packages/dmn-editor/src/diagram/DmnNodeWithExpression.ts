import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

export type DmnNodeWithExpression =
  | {
      type: "bkm";
      content: DMN14__tBusinessKnowledgeModel;
    }
  | {
      type: "decision";
      content: DMN14__tDecision;
    };
