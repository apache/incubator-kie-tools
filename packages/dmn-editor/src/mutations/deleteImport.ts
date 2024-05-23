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

import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getXmlNamespaceDeclarationName } from "../xml/xmlNamespaceDeclarations";
import { Normalized } from "../normalization/normalize";
import { computeDiagramData } from "../store/computed/computeDiagramData";
import { deleteNode, NodeDeletionMode } from "./deleteNode";
import { nodeNatures } from "./NodeNature";
import { NodeType } from "../diagram/connections/graphStructure";
import { ExternalDmnsIndex } from "../DmnEditor";

export function deleteImport({
  definitions,
  index,
  drgEdges,
  externalNodesByNamespace,
  externalDmnsIndex,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  index: number;
  drgEdges: ReturnType<typeof computeDiagramData>["drgEdges"];
  externalNodesByNamespace: ReturnType<typeof computeDiagramData>["externalNodesByNamespace"];
  externalDmnsIndex: ExternalDmnsIndex;
}) {
  definitions.import ??= [];
  const [deleted] = definitions.import.splice(index, 1);

  const namespaceName = getXmlNamespaceDeclarationName({
    rootElement: definitions,
    namespace: deleted["@_namespace"],
  });

  externalNodesByNamespace.get(deleted["@_namespace"])?.forEach((node) => {
    deleteNode({
      definitions,
      drgEdges: drgEdges,
      drdIndex: 0,
      nodeNature: nodeNatures[node.type! as NodeType],
      dmnObjectId: node.data.dmnObject?.["@_id"],
      dmnObjectQName: node.data.dmnObjectQName,
      dmnObjectNamespace: node.data.dmnObjectNamespace!, // ?? state.dmn.model.definitions["@_namespace"],
      externalDmnsIndex,
      mode: NodeDeletionMode.FROM_DRG_AND_ALL_DRDS,
    });
  });

  if (namespaceName) {
    delete definitions[`@_xmlns:${namespaceName}`];
  }
}
