import {
  DC__Bounds,
  DMN15__tDefinitions,
  DMNDI15__DMNDecisionServiceDividerLine,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function updateDecisionServiceDividerLine({ definitions }: { definitions: DMN15__tDefinitions }) {
  // FIXME: Tiago --> Implement
}

export function getCentralizedDecisionServiceDividerLine(bounds: DC__Bounds): DMNDI15__DMNDecisionServiceDividerLine {
  return {
    "di:waypoint": [
      { "@_x": bounds["@_x"], "@_y": bounds["@_y"] + bounds["@_height"] / 2 },
      {
        "@_x": bounds["@_x"] + bounds["@_height"],
        "@_y": bounds["@_y"] + bounds["@_height"] / 2,
      },
    ],
  };
}
