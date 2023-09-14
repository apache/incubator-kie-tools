import * as React from "react";
import { useCallback } from "react";
import { useDmnEditorDependencies } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorStore } from "../store/Store";

export type ExternalNode = {
  externalDrgElementNamespace: string;
  externalDrgElementId: string;
};

export const MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS =
  "kie-dmn-editor--external-node-from-included-models";

export function ExternalNodesPanel() {
  const dmn = useDmnEditorStore((s) => s.dmn);
  const { dependenciesByNamespace } = useDmnEditorDependencies();

  const onDragStart = useCallback((event: React.DragEvent, externalNode: ExternalNode) => {
    event.dataTransfer.setData(
      MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS,
      JSON.stringify(externalNode)
    );
    event.dataTransfer.effectAllowed = "move";
  }, []);

  return (
    <>
      <br />
      <br />
      {(dmn.model.definitions.import ?? []).flatMap((i) => {
        const definitions = dependenciesByNamespace[i["@_namespace"]]?.model.definitions;
        if (!definitions) {
          console.warn(
            `DMN DIAGRAM: Can't determine External Nodes for model with namespace '${i["@_namespace"]}' because it doesn't exist on the dependencies object'.`
          );
          return [];
        } else {
          return (
            <div key={definitions["@_id"]}>
              <span>
                <b>{`${definitions["@_name"]} (${i["@_name"]})`}</b>
              </span>
              {definitions.drgElement?.map((e) => (
                <div
                  draggable={true}
                  onDragStart={(event) =>
                    onDragStart(event, {
                      externalDrgElementNamespace: i["@_namespace"],
                      externalDrgElementId: e["@_id"]!,
                    })
                  }
                  key={e["@_id"]}
                  style={{
                    border: "1px solid black",
                    borderRadius: "8px",
                    padding: "4px",
                    marginBottom: "4px",
                  }}
                >{`${e.__$$element}: ${e["@_name"]}`}</div>
              ))}
              <br />
            </div>
          );
        }
      })}
    </>
  );
}
