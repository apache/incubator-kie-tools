/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function getDefaultDrdName({ drdIndex }: { drdIndex: number }) {
  return drdIndex === 0 ? "Default DRD" : "Unnamed DRD";
}

export function addOrGetDrd({ definitions, drdIndex }: { definitions: DMN15__tDefinitions; drdIndex: number }) {
  const defaultName = getDefaultDrdName({ drdIndex });

  // diagram
  definitions["dmndi:DMNDI"] ??= {};
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"] ??= [];
  definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][drdIndex] ??= { "@_id": generateUuid() };

  const defaultDiagram = definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][drdIndex];
  defaultDiagram["@_id"] ??= generateUuid();
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
