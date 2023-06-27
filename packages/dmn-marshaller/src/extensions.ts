import "./schemas/dmn-1_4/ts-gen/types";
import "./schemas/dmn-1_3/ts-gen/types";
import "./schemas/dmn-1_2/ts-gen/types";
import { KIE__tAttachment, KIE__tComponentsWidthsExtension } from "./schemas/kie-1_0/ts-gen/types";

declare module "./schemas/dmn-1_2/ts-gen/types" {
  export interface DMNDI12__DMNDiagram__extension {
    ComponentWidthsExtension?: KIE__tComponentsWidthsExtension;
  }
  export interface DMN12__tKnowledgeSource__extensionElements {
    attachment?: KIE__tAttachment;
  }
}

declare module "./schemas/dmn-1_3/ts-gen/types" {
  export interface DMNDI13__DMNDiagram__extension {
    ComponentWidthsExtension?: KIE__tComponentsWidthsExtension;
  }
  export interface DMN13__tKnowledgeSource__extensionElements {
    attachment?: KIE__tAttachment;
  }
}

declare module "./schemas/dmn-1_4/ts-gen/types" {
  export interface DMNDI13__DMNDiagram__extension {
    ComponentWidthsExtension?: KIE__tComponentsWidthsExtension;
  }

  export interface DMN14__tKnowledgeSource__extensionElements {
    attachment?: KIE__tAttachment;
  }
}
