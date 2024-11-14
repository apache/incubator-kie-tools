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

import { BoxedExpression } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { renameDrgElement } from "./renameNode";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller/dist";
import { setDrgElementExpression } from "./setDrgElementExpression";
import { updateDrgElementType } from "./updateDrgElementType";

export function updateExpression({
  definitions,
  expression,
  drgElementIndex,
  externalDmnModelsByNamespaceMap,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  expression: Normalized<BoxedExpression>;
  drgElementIndex: number;
  externalDmnModelsByNamespaceMap: Map<string, Normalized<DmnLatestModel>>;
}): void {
  const drgElement = definitions.drgElement?.[drgElementIndex];
  if (!drgElement) {
    throw new Error("DMN MUTATION: Can't update expression for drgElement that doesn't exist.");
  }

  renameDrgElement({
    definitions,
    newName: expression?.["@_label"] ?? drgElement!["@_name"]!,
    index: drgElementIndex,
    externalDmnModelsByNamespaceMap,
    shouldRenameReferencedExpressions: false,
  });

  setDrgElementExpression({
    definitions,
    expression,
    drgElementIndex,
  });

  updateDrgElementType({
    definitions,
    expression,
    drgElementIndex,
  });
}
