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

import * as React from "react";
import { useCallback, useState } from "react";
import { buildXmlHref } from "@kie-tools/dmn-marshaller/dist/xml/xmlHrefs";
import { DiagramLhsPanel, DmnEditorTab } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateActions,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { DmnObjectListItem } from "./DmnObjectListItem";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { EMPTY_IMPORT_NAME_NAMESPACE_IDENTIFIER } from "../includedModels/IncludedModels";
import { useDmnEditor } from "../DmnEditorContext";
import { SearchInput } from "@patternfly/react-core/dist/js/components/SearchInput";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorI18n } from "../i18n";

export type ExternalNode = {
  externalDrgElementNamespace: string;
  externalDrgElementId: string;
};

export const MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS =
  "kie-dmn-editor--external-node-from-included-models";

export function ExternalNodesPanel() {
  const { i18n } = useDmnEditorI18n();
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());
  const { externalModelsByNamespace } = useExternalModels();
  const externalDmnsByNamespace = useDmnEditorStore(
    (s) => s.computed(s).getDirectlyIncludedExternalModelsByNamespace(externalModelsByNamespace).dmns
  );
  const dmnShapesByHref = useDmnEditorStore((s) => s.computed(s).indexedDrd().dmnShapesByHref);
  const { onRequestToResolvePath } = useDmnEditor();

  const onDragStart = useCallback((event: React.DragEvent, externalNode: ExternalNode) => {
    event.dataTransfer.setData(
      MIME_TYPE_FOR_DMN_EDITOR_EXTERNAL_NODES_FROM_INCLUDED_MODELS,
      JSON.stringify(externalNode)
    );
    event.dataTransfer.effectAllowed = "move";
  }, []);

  const [filter, setFilter] = useState("");

  return (
    <>
      {externalDmnsByNamespace.size === 0 && (
        <>
          <EmptyState>
            <EmptyStateHeader
              titleText={i18n.externalNodes.noExternalNodesAvailable}
              icon={<EmptyStateIcon icon={CubesIcon} />}
              headingLevel={"h4"}
            />
            <EmptyStateBody>{i18n.externalNodes.IncludedModelsHaveNoExportedNodes}</EmptyStateBody>
            <EmptyStateFooter>
              <br />
              <EmptyStateActions>
                <Button
                  variant={ButtonVariant.link}
                  onClick={() =>
                    dmnEditorStoreApi.setState((state) => {
                      state.navigation.tab = DmnEditorTab.INCLUDED_MODELS;
                    })
                  }
                >
                  {i18n.includedModels.includeModel}
                </Button>
              </EmptyStateActions>
            </EmptyStateFooter>
          </EmptyState>
        </>
      )}
      {externalDmnsByNamespace.size > 0 && (
        <>
          <div className="kie-dmn-editor--sticky-top-glass-header" style={{ padding: "12px" }}>
            <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
              <TextContent>
                <Text component="h3">{i18n.externalNodes.externalNodesTitle}</Text>
              </TextContent>
              <Button
                title={i18n.close}
                variant={ButtonVariant.plain}
                onClick={() =>
                  dmnEditorStoreApi.setState((state) => {
                    state.diagram.openLhsPanel = DiagramLhsPanel.NONE;
                  })
                }
              >
                <TimesIcon />
              </Button>
            </Flex>

            <Divider style={{ marginBottom: "12px" }} />

            <SearchInput
              style={{ marginBottom: "12px", height: "36px" }}
              onKeyDown={(e) => e.stopPropagation()}
              autoFocus={true}
              placeholder={i18n.filter}
              value={filter}
              onChange={(_event, value) => setFilter(value)}
              onClear={() => setFilter("")}
            />
          </div>

          <div style={{ padding: "12px" }}>
            {[...externalDmnsByNamespace.entries()].flatMap(([namespace, externalDmn]) => {
              const externalDmnDefinitions = externalDmn.model.definitions;
              const _import = importsByNamespace.get(namespace);
              if (!_import) {
                console.debug(
                  `DMN EDITOR: Couldn't find import for namespace '${namespace}', although there's an external DMN referencing it.`
                );
                return [];
              }

              const nodes = externalDmnDefinitions.drgElement
                ?.filter((drgElement) => drgElement["@_name"].toLowerCase().includes(filter.toLowerCase()))
                .map((drgElement) => {
                  const dmnObjectHref = buildXmlHref({ namespace, id: drgElement["@_id"]! });
                  const isAlreadyIncluded = dmnShapesByHref.has(dmnObjectHref);

                  return (
                    <div
                      key={drgElement["@_id"]}
                      className={"kie-dmn-editor--external-nodes-list-item"}
                      draggable={!isAlreadyIncluded}
                      style={{ opacity: isAlreadyIncluded ? "0.4" : undefined }}
                      onDragStart={(event) =>
                        onDragStart(event, {
                          externalDrgElementNamespace: namespace,
                          externalDrgElementId: drgElement["@_id"]!,
                        })
                      }
                      data-testid={`kie-tools--dmn-editor--external-node-${_import["@_name"] === "" ? _import["@_id"] : _import["@_name"]}-${drgElement["@_name"]}`}
                    >
                      <Flex
                        alignItems={{ default: "alignItemsCenter" }}
                        justifyContent={{ default: "justifyContentFlexStart" }}
                        spaceItems={{ default: "spaceItemsNone" }}
                      >
                        <DmnObjectListItem
                          dmnObjectHref={dmnObjectHref}
                          dmnObject={drgElement}
                          namespace={namespace}
                          relativeToNamespace={namespace}
                        />
                      </Flex>
                    </div>
                  );
                });

              if ((nodes?.length ?? 0) <= 0) {
                return [];
              }

              return (
                <div key={externalDmnDefinitions["@_id"]} className={"kie-dmn-editor--external-nodes-section"}>
                  <div className={"kie-dmn-editor--external-nodes-section-title"}>
                    <b>{`${externalDmnDefinitions["@_name"]}`}</b> {`(`}
                    {_import["@_name"] || <i style={{ color: "gray" }}>{EMPTY_IMPORT_NAME_NAMESPACE_IDENTIFIER}</i>}
                    {`)`}
                    <br />
                    <small>
                      <i>
                        {onRequestToResolvePath?.(externalDmn.normalizedPosixPathRelativeToTheOpenFile) ??
                          externalDmn.normalizedPosixPathRelativeToTheOpenFile ??
                          ""}
                      </i>
                    </small>
                  </div>
                  {nodes}
                </div>
              );
            })}
          </div>
        </>
      )}
    </>
  );
}
