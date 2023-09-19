import * as React from "react";
import { useCallback } from "react";
import { useDmnEditorDependencies } from "../includedModels/DmnEditorDependenciesContext";
import { DmnEditorTab, useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { NodeIcon } from "../icons/Icons";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { DataTypeLabel } from "../dataTypes/DataTypeLabel";
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

export type ExternalNode = {
  externalDrgElementNamespace: string;
  externalDrgElementId: string;
};

export const MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS =
  "kie-dmn-editor--external-node-from-included-models";

export function ExternalNodesPanel() {
  const dmn = useDmnEditorStore((s) => s.dmn);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { dependenciesByNamespace } = useDmnEditorDependencies();
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
      {(dmn.model.definitions.import ?? []).length === 0 && (
        <>
          <EmptyState>
            <EmptyStateIcon icon={CubesIcon} />
            <Title size={"md"} headingLevel={"h4"}>
              No external nodes available
            </Title>
            <EmptyStateBody>Maybe the included models have no exported nodes</EmptyStateBody>
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
      {(dmn.model.definitions.import ?? []).flatMap((i) => {
        const definitions = dependenciesByNamespace[i["@_namespace"]]?.model.definitions;
        if (!definitions) {
          console.warn(
            `DMN DIAGRAM: Can't determine External Nodes for model with namespace '${i["@_namespace"]}' because it doesn't exist on the dependencies object'.`
          );
          return [];
        } else {
          return (
            <div key={definitions["@_id"]} className={"kie-dmn-editor--external-nodes-section"}>
              <div className={"kie-dmn-editor--external-nodes-section-title"}>
                <b>{`${definitions["@_name"]}`}</b> {`(${i["@_name"]})`}
                <small>
                  <i>
                    <Truncate content={dependenciesByNamespace[i["@_namespace"]]?.path ?? ""} />
                  </i>
                </small>
              </div>
              {definitions.drgElement?.map((e) => {
                const Icon = NodeIcon(getNodeTypeFromDmnObject(e));
                return (
                  <div
                    key={e["@_id"]}
                    className={"kie-dmn-editor--external-nodes-list-item"}
                    draggable={true}
                    onDragStart={(event) =>
                      onDragStart(event, {
                        externalDrgElementNamespace: i["@_namespace"],
                        externalDrgElementId: e["@_id"]!,
                      })
                    }
                  >
                    <Flex
                      alignItems={{ default: "alignItemsCenter" }}
                      justifyContent={{ default: "justifyContentFlexStart" }}
                      spaceItems={{ default: "spaceItemsNone" }}
                    >
                      <div style={{ width: "40px", height: "40px", marginRight: 0 }}>
                        <Icon />
                      </div>
                      <div>{`${e["@_name"]}`}</div>
                      <div>
                        {e.__$$element !== "knowledgeSource" ? (
                          <DataTypeLabel typeRef={e.variable?.["@_typeRef"]} namespace={i["@_namespace"]} />
                        ) : (
                          <></>
                        )}
                      </div>
                      {dmnShapesByHref.has(buildXmlHref({ namespace: i["@_namespace"], id: e["@_id"]! })) ? (
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
