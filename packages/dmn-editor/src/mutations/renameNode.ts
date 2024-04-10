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
  DMN15__tGroup,
  DMN15__tTextAnnotation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function renameDrgElement({
  definitions,
  newName,
  index,
}: {
  definitions: DMN15__tDefinitions;
  newName: string;
  index: number;
}) {
  const trimmedNewName = newName.trim();

  const drgElement = definitions.drgElement![index];

  drgElement["@_name"] = trimmedNewName;

  if (drgElement.__$$element !== "knowledgeSource") {
    drgElement.variable ??= { "@_name": trimmedNewName };
    drgElement.variable!["@_name"] = trimmedNewName;
  }

  if (drgElement.__$$element === "decision" && drgElement.expression) {
    drgElement.expression["@_label"] = trimmedNewName;
  }

  if (drgElement.__$$element === "businessKnowledgeModel" && drgElement.encapsulatedLogic) {
    drgElement.encapsulatedLogic["@_label"] = trimmedNewName;
  }

  // FIXME: Daniel --> Here we need to update all FEEL expression that were using this node's name as a variable.
}

export function renameGroupNode({
  definitions,
  newName,
  index,
}: {
  definitions: DMN15__tDefinitions;
  newName: string;
  index: number;
}) {
  (definitions.artifact![index] as DMN15__tGroup)["@_name"] = newName;
}

export function updateTextAnnotation({
  definitions,
  newText,
  index,
}: {
  definitions: DMN15__tDefinitions;
  newText: string;
  index: number;
}) {
  (definitions.artifact![index] as DMN15__tTextAnnotation).text = { __$$text: newText };
}
