import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";

interface KieDiagramDictionary
  extends ReferenceDictionary<{
    diagram: {
      nodesSelected: (selectedNodesCount: number) => string;
      edgesSelected: (selectedEdgesCount: number) => string;
      nodeSelected: (nodeCount: number) => string;
      edgeSelected: (edgeCount: number) => string;
      nodes: (nodeCount: number) => string;
      edges: (edgeCount: number) => string;
      selected: string;
      doubleClickToChange: string;
      expandCollapse: (name: string) => string;
    };
  }> {}

export interface KieDiagramI18n extends KieDiagramDictionary, CommonI18n {}
