import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function addOrGetDefaultDiagram({ definitions }: { definitions: DMN15__tDefinitions }) {
  // diagram
  definitions["dmndi:DMNDI"] ??= {};
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"] ??= [];
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0] ??= {};
  const defaultDiagram = definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0];
  defaultDiagram["dmndi:DMNDiagramElement"] ??= [];

  // extensions
  defaultDiagram["di:extension"] ??= {};
  defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"] ??= {};
  defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"]["kie:ComponentWidths"] ??= [{}]; // FIXME: Tiago --> Immer is throwing an error at this line for empty DMNs.

  return {
    widthsExtension: defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"],
    widths: defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"]["kie:ComponentWidths"]!,
    diagram: defaultDiagram,
    diagramElements: defaultDiagram["dmndi:DMNDiagramElement"],
  };
}
