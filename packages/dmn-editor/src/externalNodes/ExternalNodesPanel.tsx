import * as React from "react";
import { useCallback } from "react";
import { DmnEditorTab, useDmnEditorStoreApi } from "../store/Store";
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
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { dmnShapesByHref, externalDmnsByNamespace, importsByNamespace } = useDmnEditorDerivedStore();

  const onDragStart = useCallback((event: React.DragEvent, externalNode: ExternalNode) => {
    event.dataTransfer.setData(
      MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS,
      JSON.stringify(externalNode)
    );
    event.dataTransfer.effectAllowed = "move";
  }, []);

  return (
    <>
      {externalDmnsByNamespace.size === 0 && (
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
                Included model...
              </Button>
            </EmptyStatePrimary>
          </EmptyState>
        </>
      )}
      {[...externalDmnsByNamespace.entries()].flatMap(([namespace, externalDmn]) => {
        const externalDmnDefinitions = externalDmn.model.definitions;
        const _import = importsByNamespace.get(namespace);
        if (!_import) {
          console.debug(
            `DMN EDITOR: Couldn't find import for namespace '${namespace}', although there's an external DMN referncing it.`
          );
          return [];
        }

        return (
          <div key={externalDmnDefinitions["@_id"]} className={"kie-dmn-editor--external-nodes-section"}>
            <div className={"kie-dmn-editor--external-nodes-section-title"}>
              <b>{`${externalDmnDefinitions["@_name"]}`}</b> {`(${_import["@_name"]})`}
              <small>
                <i>
                  <Truncate content={externalDmnsByNamespace.get(namespace)?.path ?? ""} />
                </i>
              </small>
            </div>
            {externalDmnDefinitions.drgElement?.map((dmnObject) => (
              <div
                key={dmnObject["@_id"]}
                className={"kie-dmn-editor--external-nodes-list-item"}
                draggable={true}
                onDragStart={(event) =>
                  onDragStart(event, {
                    externalDrgElementNamespace: namespace,
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
                    dmnObjectHref={buildXmlHref({ namespace, id: dmnObject["@_id"]! })}
                    dmnObject={dmnObject}
                    namespace={namespace}
                    relativeToNamespace={namespace}
                  />
                  {dmnShapesByHref.has(buildXmlHref({ namespace, id: dmnObject["@_id"]! })) ? (
                    <small>
                      <div>&nbsp;&nbsp;✓</div>
                    </small>
                  ) : (
                    <></>
                  )}
                </Flex>
              </div>
            ))}
          </div>
        );
      })}
    </>
  );
}
