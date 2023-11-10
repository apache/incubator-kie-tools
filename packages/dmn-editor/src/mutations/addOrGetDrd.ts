import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function getDefaultDrdName({ drdIndex }: { drdIndex: number }) {
  return drdIndex === 0 ? "Default DRD" : "Unnamed DRD";
}

export function addOrGetDrd({ definitions, drdIndex }: { definitions: DMN15__tDefinitions; drdIndex: number }) {
  const _drdIndex = drdIndex ?? 0;
  const defaultName = getDefaultDrdName({ drdIndex: _drdIndex });

  // diagram
  definitions["dmndi:DMNDI"] ??= {};
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"] ??= [];
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][_drdIndex] ??= {};

  const defaultDiagram = definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][_drdIndex];
  defaultDiagram["@_name"] ??= defaultName;
  defaultDiagram["@_useAlternativeInputDataShape"] ??= false;
  defaultDiagram["dmndi:DMNDiagramElement"] ??= [];

  // extensions
  defaultDiagram["di:extension"] ??= {};
  defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"] ??= {};
  defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"]["kie:ComponentWidths"] ??= [{}];

  return {
    widthsExtension: defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"],
    widths: defaultDiagram["di:extension"]["kie:ComponentsWidthsExtension"]["kie:ComponentWidths"]!,
    diagram: defaultDiagram,
    diagramElements: defaultDiagram["dmndi:DMNDiagramElement"],
  };
}
