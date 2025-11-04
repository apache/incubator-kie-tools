import { KieDiagramI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";

export const en: KieDiagramI18n = {
  ...en_common,
  diagram: {
    nodesSelected: (selectedNodesCount: number): string => `${selectedNodesCount} nodes selected`,
    edgesSelected: (selectedEdgesCount: number): string => `${selectedEdgesCount} edges selected`,
    nodeSelected: (nodeCount: number): string => `${nodeCount} node`,
    edgeSelected: (edgeCount: number): string => `${edgeCount} edge`,
    nodes: (nodeCount: number): string => `${nodeCount} nodes`,
    edges: (edgeCount: number): string => `${edgeCount} edges`,
    selected: "selected",
    doubleClickToChange: `Double-click to change`,
    expandCollapse: (name: string) => `Expand / collapse ${name}`,
  },
};
