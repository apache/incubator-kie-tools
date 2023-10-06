import {
  DMN15__tDecision,
  DMN15__tDefinitions,
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

export const DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE = "application/json+kie-dmn-editor--diagram" as const;
export const DMN_EDITOR_BOXED_EXPRESSION_CLIPBOARD_MIME_TYPE =
  "application/json+kie-dmn-editor--boxed-expression" as const;
export const DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE = "application/json+kie-dmn-editor--dataTypes" as const;

export type DmnEditorDiagramClipboard = {
  mimeType: typeof DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE;
  originNamespace: string;
  drgElements: NonNullable<Unpacked<DMN15__tDefinitions["drgElement"]>>[];
  artifacts: NonNullable<Unpacked<DMN15__tDefinitions["artifact"]>>[];
  widths: Namespaced<KIE, KIE__tComponentWidths>[];
  shapes: DMNDI15__DMNShape[];
  edges: DMNDI15__DMNEdge[];
};

export function buildClipboardFromDiagram(rfState: RF.ReactFlowState, dmnEditorState: State) {
  const copiedEdgesById = new Map<string, RF.Edge<DmnDiagramEdgeData>>();
  const danglingEdgesById = new Map<string, RF.Edge<DmnDiagramEdgeData>>();

  const copiedNodesById = rfState
    .getNodes()
    .reduce((acc, n) => (n.selected ? acc.set(n.id, n) : acc), new Map<string, RF.Node<DmnDiagramNodeData>>());

  const clipboard = [...copiedNodesById.values()].reduce<DmnEditorDiagramClipboard>(
    (acc, node: RF.Node<DmnDiagramNodeData>) => {
      const nodeNature = nodeNatures[node.type as NodeType];

      // Groups and TextAnnotations.. Associations are treated after.
      if (nodeNature === NodeNature.ARTIFACT) {
        acc.artifacts.unshift(node.data.dmnObject as any);
      }
      // DRG Elements
      else if (nodeNature === NodeNature.DRG_ELEMENT) {
        const dmnObject = JSON.parse(JSON.stringify(node.data.dmnObject)) as DMN15__tDecision; // Casting to `DMN15__tDecision` because it has all requirement types.

        if (dmnObject.authorityRequirement) {
          dmnObject.authorityRequirement = dmnObject.authorityRequirement.filter(
            (s) =>
              (s.requiredInput && copiedNodesById.has(s.requiredInput["@_href"])) ||
              (s.requiredDecision && copiedNodesById.has(s.requiredDecision["@_href"])) ||
              (s.requiredAuthority && copiedNodesById.has(s.requiredAuthority["@_href"]))
          );
        }

        if (dmnObject.knowledgeRequirement) {
          dmnObject.knowledgeRequirement = dmnObject.knowledgeRequirement.filter((s) =>
            copiedNodesById.has(s.requiredKnowledge["@_href"])
          );
        }

        if (dmnObject.informationRequirement) {
          dmnObject.informationRequirement = dmnObject.informationRequirement.filter(
            (s) =>
              (s.requiredInput && copiedNodesById.has(s.requiredInput["@_href"])) ||
              (s.requiredDecision && copiedNodesById.has(s.requiredDecision["@_href"]))
          );
        }
        acc.drgElements.unshift(dmnObject as any);
      } else {
        throw new Error(`Unknwon node nature '${nodeNature}'`);
      }

      const { index, ...dmnShape } = node.data.shape;
      acc.shapes.push(dmnShape);
      return acc;
    },
    {
      mimeType: DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE,
      originNamespace: dmnEditorState.dmn.model.definitions["@_namespace"],
      widths: [],
      drgElements: [],
      artifacts: [],
      shapes: [],
      edges: [],
    }
  );

  // FIXME: Tiago --> Populate `widths` from the copied Boxed Expressions inside Nodes.

  const artifacts = dmnEditorState.dmn.model.definitions.artifact ?? [];

  clipboard.edges = rfState.edges.flatMap((edge: RF.Edge<DmnDiagramEdgeData>) => {
    if (copiedNodesById.has(edge.source) && !copiedNodesById.has(edge.target)) {
      danglingEdgesById.set(edge.id, edge); // Edges that point to nodes that are not part of the clipboard need to be removed when 'cut' is executed.
    }

    if (copiedNodesById.has(edge.source) && copiedNodesById.has(edge.target)) {
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
