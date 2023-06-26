import { Meta } from "@kie-tools/xml-parser-ts";

export const root = {
  element: "KIE.xsd__ComponentsWidthsExtension",
  type: "KIE__kie:tComponentsWidthsExtension",
};

export const ns = new Map<string, string>([
  ["http://www.drools.org/kie/dmn/1.2", ""],
  ["", "http://www.drools.org/kie/dmn/1.2"],
]);

export const meta: Meta = {
  KIE__tComponentsWidthsExtension: {
    ComponentWidths: { type: "KIE__tComponentWidths", isArray: true, isOptional: true },
  },
  KIE__tComponentWidths: {
    "@_dmnElementRef": { type: "string", isArray: false, isOptional: true },
    width: { type: "float", isArray: true, isOptional: true },
  },
  KIE__tAttachment: {
    "@_url": { type: "string", isArray: false, isOptional: true },
    "@_name": { type: "string", isArray: false, isOptional: true },
  },
};
