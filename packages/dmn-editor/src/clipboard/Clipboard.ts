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
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tDefinitions,
  DMN15__tItemDefinition,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Unpacked } from "../tsExt/tsExt";
import * as RF from "reactflow";
import { State } from "../store/Store";
import { NodeNature, nodeNatures } from "../mutations/NodeNature";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { NodeType } from "../diagram/connections/graphStructure";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { KIE, Namespaced } from "@kie-tools/dmn-marshaller/dist/kie-extensions";
import { KIE__tComponentWidths } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { DataType } from "../dataTypes/DataTypes";
import { parseXmlHref } from "../xml/xmlHrefs";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";

export const DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE = "application/json+kie-dmn-editor--diagram" as const;
export const DMN_EDITOR_BOXED_EXPRESSION_CLIPBOARD_MIME_TYPE =
  "application/json+kie-dmn-editor--boxed-expression" as const;
export const DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE = "application/json+kie-dmn-editor--data-types" as const;

export type DmnEditorDataTypesClipboard = {
  mimeType: typeof DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE;
  namespaceWhereClipboardWasCreatedFrom: string;
  namespace: string;
  itemDefinitions: DMN15__tItemDefinition[];
};

export type DmnEditorDiagramClipboard = {
  mimeType: typeof DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE;
  namespaceWhereClipboardWasCreatedFrom: string;
  drgElements: NonNullable<Unpacked<DMN15__tDefinitions["drgElement"]>>[];
  artifacts: NonNullable<Unpacked<DMN15__tDefinitions["artifact"]>>[];
  widths: Namespaced<KIE, KIE__tComponentWidths>[];
  shapes: DMNDI15__DMNShape[];
  edges: DMNDI15__DMNEdge[];
};

export function buildClipboardFromDiagram(rfState: RF.ReactFlowState, dmnEditorState: State) {
  const copiedEdgesById = new Map<string, RF.Edge<DmnDiagramEdgeData>>();
  const copiedNodesById = new Map<string, RF.Node<DmnDiagramNodeData>>();
  const danglingEdgesById = new Map<string, RF.Edge<DmnDiagramEdgeData>>();

  const nodesById = rfState
    .getNodes()
    .reduce((acc, n) => acc.set(n.id, n), new Map<string, RF.Node<DmnDiagramNodeData>>());

  const selectedNodesById = rfState
    .getNodes()
    .reduce((acc, n) => (n.selected ? acc.set(n.id, n) : acc), new Map<string, RF.Node<DmnDiagramNodeData>>());

  const clipboard = [...selectedNodesById.values()].reduce<DmnEditorDiagramClipboard>(
    (acc, _node: RF.Node<DmnDiagramNodeData>) => {
      function accNode(node: RF.Node<DmnDiagramNodeData>) {
        const nodeNature = nodeNatures[node.type as NodeType];

        // Groups and TextAnnotations.. Associations are treated after.
        if (nodeNature === NodeNature.ARTIFACT) {
          acc.artifacts.unshift(node.data.dmnObject as any);
        }
        // DRG Elements
        else if (nodeNature === NodeNature.DRG_ELEMENT) {
          if (_node.data.dmnObjectQName.prefix) {
            // External node. Will not go in the Clipboard.
            return;
          }

          const dmnObject = JSON.parse(JSON.stringify(node.data.dmnObject)) as DMN15__tDecision; // Casting to `DMN15__tDecision` because it has all requirement types.

          // This is going to get repopulated when this data is pasted somewhere.
          if (node.data.dmnObject?.__$$element === "decisionService") {
            (dmnObject as DMN15__tDecisionService).inputData = [];
            (dmnObject as DMN15__tDecisionService).inputDecision = [];
          }

          if (dmnObject.authorityRequirement) {
            dmnObject.authorityRequirement = dmnObject.authorityRequirement.filter(
              (s) =>
                (s.requiredInput &&
                  selectedNodesById.has(s.requiredInput["@_href"]) &&
                  !selectedNodesById.get(s.requiredInput["@_href"])?.data?.dmnObjectQName.prefix) ||
                (s.requiredDecision &&
                  selectedNodesById.has(s.requiredDecision["@_href"]) &&
                  !selectedNodesById.get(s.requiredDecision["@_href"])?.data?.dmnObjectQName.prefix) ||
                (s.requiredAuthority &&
                  selectedNodesById.has(s.requiredAuthority["@_href"]) &&
                  !selectedNodesById.get(s.requiredAuthority["@_href"])?.data?.dmnObjectQName.prefix)
            );
          }

          if (dmnObject.knowledgeRequirement) {
            dmnObject.knowledgeRequirement = dmnObject.knowledgeRequirement.filter(
              (s) =>
                selectedNodesById.has(s.requiredKnowledge["@_href"]) &&
                !selectedNodesById.get(s.requiredKnowledge["@_href"])?.data?.dmnObjectQName.prefix
            );
          }

          if (dmnObject.informationRequirement) {
            dmnObject.informationRequirement = dmnObject.informationRequirement.filter(
              (s) =>
                (s.requiredInput &&
                  selectedNodesById.has(s.requiredInput["@_href"]) &&
                  !selectedNodesById.get(s.requiredInput["@_href"])?.data?.dmnObjectQName.prefix) ||
                (s.requiredDecision &&
                  selectedNodesById.has(s.requiredDecision["@_href"]) &&
                  !selectedNodesById.get(s.requiredDecision["@_href"])?.data?.dmnObjectQName.prefix)
            );
          }
          acc.drgElements.unshift(dmnObject as any);
        } else if (nodeNature === NodeNature.UNKNOWN) {
          // Ignore.
        } else {
          throw new Error(`Unknwon node nature '${nodeNature}'`);
        }

        copiedNodesById.set(node.id, node);

        const { index, ...dmnShape } = node.data.shape;
        acc.shapes.push(dmnShape);
      }

      if (!_node.selected) {
        return acc;
      }

      // When a Decision Service is selected, we treat all its contained Decisions as if they were too, making them part of the cut/copy/paste operations.
      if (_node.data.dmnObject?.__$$element === "decisionService") {
        for (const decision of [
          ...(_node.data.dmnObject.outputDecision ?? []),
          ...(_node.data.dmnObject.encapsulatedDecision ?? []),
        ]) {
          if (parseXmlHref(decision["@_href"]).namespace) {
            continue; // External decision relative to this DMN. Will not go in the Clipboard.
          }

          const decisionNode = nodesById.get(decision["@_href"]);
          if (!decisionNode) {
            // Decision Service has a reference to an unknown Decision. Ignoring.
            continue;
          }

          accNode(decisionNode);
        }
      }

      accNode(_node);

      return acc;
    },
    {
      mimeType: DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE,
      namespaceWhereClipboardWasCreatedFrom: dmnEditorState.dmn.model.definitions["@_namespace"],
      widths: [],
      drgElements: [],
      artifacts: [],
      shapes: [],
      edges: [],
    }
  );

  const idsOnDrgElementTrees = getNewDmnIdRandomizer()
    .ack({ json: clipboard.drgElements, type: "DMN15__tDefinitions", attr: "drgElement" })
    .getOriginalIds();

  clipboard.widths = (
    dmnEditorState.dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[dmnEditorState.diagram.drdIndex][
      "di:extension"
    ]?.["kie:ComponentsWidthsExtension"]?.["kie:ComponentWidths"] ?? []
  ).filter((w: KIE__tComponentWidths) => idsOnDrgElementTrees.has(w["@_dmnElementRef"]!));

  const artifacts = dmnEditorState.dmn.model.definitions.artifact ?? [];

  clipboard.edges = rfState.edges.flatMap((edge: RF.Edge<DmnDiagramEdgeData>) => {
    if (copiedNodesById.has(edge.source) && !copiedNodesById.has(edge.target)) {
      danglingEdgesById.set(edge.id, edge); // Edges that point to nodes that are not part of the clipboard need to be removed when 'cut' is executed.
    }

    if (copiedNodesById.has(edge.source) && copiedNodesById.has(edge.target)) {
      if (!edge.data?.dmnEdge) {
        return [];
      }

      copiedEdgesById.set(edge.id, edge);
      const { index, ...dmnEdge } = edge.data!.dmnEdge!;
      if (edge.data?.dmnObject.requirementType === "association") {
        clipboard.artifacts.push(artifacts[edge.data.dmnObject.index]);
      }
      return dmnEdge ?? [];
    } else {
      return [];
    }
  });

  return { clipboard, copiedEdgesById, copiedNodesById, danglingEdgesById };
}

export function buildClipboardFromDataType(dataType: DataType, thisDmnsNamespace: string): DmnEditorDataTypesClipboard {
  return {
    namespaceWhereClipboardWasCreatedFrom: thisDmnsNamespace,
    namespace: dataType.namespace,
    mimeType: DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE,
    itemDefinitions: [dataType.itemDefinition],
  };
}

export function getClipboard<T extends { mimeType: string }>(text: string, mimeType: string): T | undefined {
  let potentialClipboard: T | undefined;
  try {
    potentialClipboard = JSON.parse(text);
  } catch (e) {
    console.debug("DMN DIAGRAM: Ignoring pasted content. Not a valid JSON.");
    return undefined;
  }

  if (!potentialClipboard || potentialClipboard.mimeType !== mimeType) {
    console.debug("DMN DIAGRAM: Ignoring pasted content. MIME type doesn't match.");
    return undefined;
  }

  return potentialClipboard;
}
