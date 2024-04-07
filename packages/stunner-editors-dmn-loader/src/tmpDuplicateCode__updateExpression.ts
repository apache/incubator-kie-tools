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

//////////////////////////////////////////////////////////////////////
// TEMPORARILY COPIED FROM @kie-tools/dmn-editor/src/mutations/updateExpression.ts
//////////////////////////////////////////////////////////////////////

import { BoxedExpression } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DMN15__tDefinitions,
  DMN15__tFunctionDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { renameDrgElement } from "./tmpDuplicateCode__renameNode";

export function updateExpression({
  definitions,
  expression,
  drgElementIndex,
}: {
  definitions: DMN15__tDefinitions;
  expression: BoxedExpression;
  drgElementIndex: number;
}): void {
  const drgElement = definitions.drgElement?.[drgElementIndex];
  if (!drgElement) {
    throw new Error("DMN MUTATION: Can't update expression for drgElement that doesn't exist.");
  }

  renameDrgElement({
    definitions,
    newName: expression?.["@_label"] ?? drgElement!["@_name"]!,
    index: drgElementIndex,
  });

  if (drgElement?.__$$element === "decision") {
    drgElement.expression = expression;
    drgElement.variable!["@_typeRef"] = expression?.["@_typeRef"] ?? drgElement.variable!["@_typeRef"];
  } else if (drgElement?.__$$element === "businessKnowledgeModel") {
    if (expression.__$$element !== "functionDefinition") {
      throw new Error("DMN MUTATION: Can't have an expression on a BKM that is not a Function.");
    }

    if (!expression?.__$$element) {
      throw new Error("DMN MUTATION: Can't determine expression type without its __$$element property.");
    }

    // We remove the __$$element here, because otherwise the "functionDefinition" element name will be used in the final XML.
    const { __$$element, ..._updateExpression } = expression;
    drgElement.encapsulatedLogic = _updateExpression as DMN15__tFunctionDefinition;
    drgElement.variable!["@_typeRef"] = _updateExpression?.["@_typeRef"] ?? drgElement.variable!["@_typeRef"];
  } else {
    throw new Error("DMN MUTATION: Can't update expression for drgElement that is not a Decision or a BKM.");
  }
}
