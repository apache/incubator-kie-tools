import "./schemas/dmn-1_4/ts-gen/types";
import "./schemas/dmn-1_3/ts-gen/types";
import "./schemas/dmn-1_2/ts-gen/types";

declare module "./schemas/dmn-1_2/ts-gen/types" {
  export interface DMN12__tDefinitions {
    myCustomProperty: string;
  }
}

declare module "./schemas/dmn-1_3/ts-gen/types" {
  export interface DMN13__tDefinitions {
    myCustomProperty: string;
  }
}

declare module "./schemas/dmn-1_4/ts-gen/types" {
  export interface DMN14__tDefinitions {
    myCustomProperty: string;
  }
}
