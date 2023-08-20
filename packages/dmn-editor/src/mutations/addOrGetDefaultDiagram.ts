import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

export function addOrGetDefaultDiagram({ definitions }: { definitions: DMN14__tDefinitions }) {
  // diagram
  definitions["dmndi:DMNDI"] ??= {};
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"] ??= [];
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0] ??= {};
  const defaultDiagram = definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0];
  defaultDiagram["dmndi:DMNDiagramElement"] ??= [];

  // extensions
  defaultDiagram["di:extension"] ??= {};
  defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"] ??= {};
  defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"]["kie:ComponentWidths"] ??= [];

  return {
    widthsExtension: defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"],
    widths: defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"]["kie:ComponentWidths"],
    diagram: defaultDiagram,
    diagramElements: defaultDiagram["dmndi:DMNDiagramElement"],
  };
}
