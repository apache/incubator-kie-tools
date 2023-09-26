import * as React from "react";
import { useCallback } from "react";
import { useOtherDmns } from "../includedModels/DmnEditorDependenciesContext";
import { DmnEditorTab, useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { buildXmlHref } from "../xml/xmlHrefs";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStatePrimary,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { DmnObjectListItem } from "./DmnObjectListItem";

export type ExternalNode = {
  externalDrgElementNamespace: string;
  externalDrgElementId: string;
};

export const MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS =
  "kie-dmn-editor--external-node-from-included-models";

export function ExternalNodesPanel() {
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { otherDmnsByNamespace } = useOtherDmns();
  const { dmnShapesByHref } = useDmnEditorDerivedStore();

  const onDragStart = useCallback((event: React.DragEvent, externalNode: ExternalNode) => {
    event.dataTransfer.setData(
      MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS,
      JSON.stringify(externalNode)
    );
    event.dataTransfer.effectAllowed = "move";
  }, []);

  return (
    <>
      {(thisDmn.model.definitions.import ?? []).length === 0 && (
        <>
          <EmptyState>
            <EmptyStateIcon icon={CubesIcon} />
            <Title size={"md"} headingLevel={"h4"}>
              No external nodes available
            </Title>
            <EmptyStateBody>
              Maybe the included models have no exported nodes, or there are no included models.
            </EmptyStateBody>
            <br />
            <EmptyStatePrimary>
              <Button
                variant={ButtonVariant.link}
                onClick={() =>
                  dmnEditorStoreApi.setState((state) => {
                    state.navigation.tab = DmnEditorTab.INCLUDED_MODELS;
                  })
                }
              >
                Add an included model...
              </Button>
            </EmptyStatePrimary>
          </EmptyState>
        </>
      )}
      {(thisDmn.model.definitions.import ?? []).flatMap((i) => {
        const otherDmnDefinitions = otherDmnsByNamespace[i["@_namespace"]]?.model.definitions;
        if (!otherDmnDefinitions) {
          console.warn(
            `DMN DIAGRAM: Can't determine External Nodes for model with namespace '${i["@_namespace"]}' because it doesn't exist on the dependencies object'.`
          );
          return [];
        } else {
          return (
            <div key={otherDmnDefinitions["@_id"]} className={"kie-dmn-editor--external-nodes-section"}>
              <div className={"kie-dmn-editor--external-nodes-section-title"}>
                <b>{`${otherDmnDefinitions["@_name"]}`}</b> {`(${i["@_name"]})`}
                <small>
                  <i>
                    <Truncate content={otherDmnsByNamespace[i["@_namespace"]]?.path ?? ""} />
                  </i>
                </small>
              </div>
              {otherDmnDefinitions.drgElement?.map((dmnObject) => {
                return (
                  <div
                    key={dmnObject["@_id"]}
                    className={"kie-dmn-editor--external-nodes-list-item"}
                    draggable={true}
                    onDragStart={(event) =>
                      onDragStart(event, {
                        externalDrgElementNamespace: i["@_namespace"],
                        externalDrgElementId: dmnObject["@_id"]!,
                      })
                    }
                  >
                    <Flex
                      alignItems={{ default: "alignItemsCenter" }}
                      justifyContent={{ default: "justifyContentFlexStart" }}
                      spaceItems={{ default: "spaceItemsNone" }}
                    >
                      <DmnObjectListItem
                        dmnObjectHref={buildXmlHref({ namespace: i["@_namespace"], id: dmnObject["@_id"]! })}
                        dmnObject={dmnObject}
                        namespace={i["@_namespace"]}
                        relativeToNamespace={i["@_namespace"]}
                      />
                      {dmnShapesByHref.has(buildXmlHref({ namespace: i["@_namespace"], id: dmnObject["@_id"]! })) ? (
                        <small>
                          <div>&nbsp;&nbsp;✓</div>
                        </small>
                      ) : (
                        <></>
                      )}
                    </Flex>
                  </div>
                );
              })}
            </div>
          );
        }
      })}
    </>
  );
}
