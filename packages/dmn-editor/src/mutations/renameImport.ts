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

import {
  DMN15__tDefinitions,
  DMN15__tFunctionDefinition,
  DMN15__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import {
  traverseExpressionsInExpressionHolders,
  traverseItemDefinitions,
  traverseTypeRefedInExpressionHolders,
} from "../dataTypes/DataTypeSpec";
import { buildFeelQName, parseFeelQName } from "../feel/parseFeelQName";
import { DataTypeIndex } from "../dataTypes/DataTypes";
import { DMN15__tContext } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { IdentifiersRefactor } from "@kie-tools/dmn-language-service";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";

export function renameImport({
  definitions,
  newName,
  allTopLevelDataTypesByFeelName,
  index,
  externalModelsByNamespaceMap,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  allTopLevelDataTypesByFeelName: DataTypeIndex;
  newName: string;
  index: number;
  externalModelsByNamespaceMap: Map<string, Normalized<DmnLatestModel>>;
}) {
  const trimmedNewName = newName.trim();

  const identifiersRefactor = new IdentifiersRefactor({
    writeableDmnDefinitions: definitions,
    _readonly_externalDmnModelsByNamespaceMap:
      externalModelsByNamespaceMap ?? new Map<string, Normalized<DmnLatestModel>>(),
  });

  const _import = definitions.import![index];

  traverseItemDefinitions(definitions.itemDefinition ?? [], (item) => {
    if (item.typeRef) {
      const feelQName = parseFeelQName(item.typeRef.__$$text);
      if (allTopLevelDataTypesByFeelName.get(item.typeRef.__$$text)?.namespace === _import["@_namespace"]) {
        item.typeRef = {
          __$$text: buildFeelQName({
            type: "feel-qname",
            importName: trimmedNewName,
            localPart: feelQName.localPart,
          }),
        };
      }
    }
  });

  definitions.drgElement ??= [];
  for (let i = 0; i < definitions.drgElement.length; i++) {
    const element = definitions.drgElement[i];
    if (
      element.__$$element === "inputData" ||
      element.__$$element === "decision" ||
      element.__$$element === "businessKnowledgeModel" ||
      element.__$$element === "decisionService"
    ) {
      if (element.variable?.["@_typeRef"]) {
        if (allTopLevelDataTypesByFeelName.get(element.variable?.["@_typeRef"])?.namespace === _import["@_namespace"]) {
          const feelQName = parseFeelQName(element.variable["@_typeRef"]);
          element.variable["@_typeRef"] = buildFeelQName({
            type: "feel-qname",
            importName: trimmedNewName,
            localPart: feelQName.localPart,
          });
        }
      }

      if (element.__$$element === "decision" || element.__$$element === "businessKnowledgeModel") {
        traverseExpressionsInExpressionHolders(element, (expression, __$$element) => {
          if (__$$element === "functionDefinition") {
            const e = expression as Normalized<DMN15__tFunctionDefinition>;
            if (e["@_kind"] === "PMML") {
              const pmmlDocument = (e.expression as Normalized<DMN15__tContext>).contextEntry?.find(
                ({ variable }) => variable?.["@_name"] === DMN15_SPEC.BOXED.FUNCTION.PMML.documentFieldName
              );

              const pmmlDocumentLiteralExpression = pmmlDocument?.expression as
                | Normalized<DMN15__tLiteralExpression>
                | undefined;
              if (pmmlDocumentLiteralExpression?.text?.__$$text === _import["@_name"]) {
                pmmlDocumentLiteralExpression.text = { __$$text: trimmedNewName };
              }
            }
          }
        });

        traverseTypeRefedInExpressionHolders(element, (typeRefed) => {
          if (typeRefed["@_typeRef"]) {
            if (allTopLevelDataTypesByFeelName.get(typeRefed["@_typeRef"])?.namespace === _import["@_namespace"]) {
              const feelQName = parseFeelQName(typeRefed["@_typeRef"]);
              typeRefed["@_typeRef"] = buildFeelQName({
                type: "feel-qname",
                importName: trimmedNewName,
                localPart: feelQName.localPart,
              });
            }
          }
        });
      }
    }
  }

  // TODO: Tiago --> Update the "document" entry of PMML functions that were pointing to the renamed included PMML model.

  identifiersRefactor.renameImport({ oldName: _import["@_name"], newName: trimmedNewName });

  _import["@_name"] = trimmedNewName;
}
