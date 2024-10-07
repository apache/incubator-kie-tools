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

import { DMN15__tDefinitions, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { parseXmlHref, xmlHrefToQName } from "@kie-tools/dmn-marshaller/dist/xml";
import { getContainmentRelationship, getDecisionServiceDividerLineLocalY } from "../diagram/maths/DmnMaths";
import { addOrGetDrd } from "./addOrGetDrd";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { SnapGrid } from "../store/Store";
import { MIN_NODE_SIZES } from "../diagram/nodes/DefaultSizes";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { ExternalModelsIndex } from "../DmnEditor";

export function addDecisionToDecisionService({
  definitions,
  decisionHref,
  decisionServiceId,
  drdIndex,
  snapGrid,
  externalModelsByNamespace,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  decisionHref: string;
  decisionServiceId: string;
  drdIndex: number;
  snapGrid: SnapGrid;
  externalModelsByNamespace: ExternalModelsIndex | undefined;
}) {
  console.debug(`DMN MUTATION: Adding Decision '${decisionHref}' to Decision Service '${decisionServiceId}'`);

  const href = parseXmlHref(decisionHref);

  if (href.namespace) {
    const externalModel = externalModelsByNamespace?.[href.namespace];
    if (!externalModel) {
      throw new Error(`DMN MUTATION: Namespace '${href.namespace}' not found.`);
    }

    if (externalModel?.type !== "dmn") {
      throw new Error(`DMN MUTATION: External model with namespace ${href.namespace} is not a DMN.`);
    }

    const externalDrgs = externalModel.model.definitions.drgElement;
    const decision = externalDrgs?.find((drgElement) => drgElement["@_id"] === href.id);
    if (decision?.__$$element !== "decision") {
      throw new Error(
        `DMN MUTATION: DRG Element with id '${href.id}' is either not a Decision or doesn't exist in the external model '${href.namespace}'`
      );
    }
  } else {
    const decision = definitions.drgElement?.find((s) => s["@_id"] === href.id);
    if (decision?.__$$element !== "decision") {
      throw new Error(`DMN MUTATION: DRG Element with id '${href.id}' is either not a Decision or doesn't exist.`);
    }
  }

  const decisionService = definitions.drgElement?.find((s) => s["@_id"] === decisionServiceId);
  if (decisionService?.__$$element !== "decisionService") {
    throw new Error(
      `DMN MUTATION: DRG Element with id '${decisionServiceId}' is either not a Decision Service or doesn't exist.`
    );
  }

  const diagram = addOrGetDrd({ definitions, drdIndex });
  const dmnElementRef = xmlHrefToQName(decisionHref, definitions);

  const decisionShape = diagram.diagramElements.find(
    (s) => s["@_dmnElementRef"] === dmnElementRef && s.__$$element === "dmndi:DMNShape"
  ) as Normalized<DMNDI15__DMNShape>;

  const decisionServiceShape = diagram.diagramElements.find(
    (s) => s["@_dmnElementRef"] === decisionServiceId && s.__$$element === "dmndi:DMNShape"
  ) as Normalized<DMNDI15__DMNShape>;

  const section = getSectionForDecisionInsideDecisionService({ decisionShape, decisionServiceShape, snapGrid });
  if (section === "encapsulated") {
    decisionService.encapsulatedDecision ??= [];
    decisionService.encapsulatedDecision.push({ "@_href": `${decisionHref}` });
  } else if (section === "output") {
    decisionService.outputDecision ??= [];
    decisionService.outputDecision.push({ "@_href": `${decisionHref}` });
  } else {
    throw new Error(`DMN MUTATION: Invalid section to add decision to: '${section}' `);
  }

  repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService, externalModelsByNamespace });
}

export function getSectionForDecisionInsideDecisionService({
  decisionShape,
  decisionServiceShape,
  snapGrid,
}: {
  decisionShape: Normalized<DMNDI15__DMNShape>;
  decisionServiceShape: Normalized<DMNDI15__DMNShape>;
  snapGrid: SnapGrid;
}): "output" | "encapsulated" {
  if (!decisionShape?.["dc:Bounds"] || !decisionServiceShape?.["dc:Bounds"]) {
    throw new Error(
      `DMN MUTATION: Can't determine Decision Service section for Decision '${decisionShape["@_dmnElementRef"]}' because it doens't have a DMNShape.`
    );
  }

  const contaimentRelationship = getContainmentRelationship({
    bounds: decisionShape["dc:Bounds"],
    container: decisionServiceShape["dc:Bounds"],
    divingLineLocalY: getDecisionServiceDividerLineLocalY(decisionServiceShape),
    snapGrid,
    isAlternativeInputDataShape: false,
    containerMinSizes: MIN_NODE_SIZES[NODE_TYPES.decisionService],
    boundsMinSizes: MIN_NODE_SIZES[NODE_TYPES.decision],
  });

  if (!contaimentRelationship.isInside) {
    throw new Error(
      `DMN MUTATION: Decision '${decisionShape["@_dmnElementRef"]}' can't be added to Decision Service '${decisionServiceShape["@_dmnElementRef"]}' because its shape is not visually contained by the Decision Service's shape.`
    );
  }

  return contaimentRelationship.section === "upper" ? "output" : "encapsulated";
}
