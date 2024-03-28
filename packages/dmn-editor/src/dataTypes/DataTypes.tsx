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
import { useCallback, useMemo, useState } from "react";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { InfrastructureIcon } from "@patternfly/react-icons/dist/js/icons/infrastructure-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { State } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { DataTypesEmptyState } from "./DataTypesEmptyState";
import { DataTypePanel } from "./DataTypePanel";
import { findDataTypeById, isStruct } from "./DataTypeSpec";
import { DataTypeName } from "./DataTypeName";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { invalidInlineFeelNameStyle } from "../feel/InlineFeelNameInput";
import {
  Dropdown,
  DropdownItem,
  DropdownPosition,
  DropdownToggle,
  DropdownToggleAction,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { PasteIcon } from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { SearchInput } from "@patternfly/react-core/dist/js/components/SearchInput";
import {
  DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE,
  DmnEditorDataTypesClipboard,
  getClipboard,
} from "../clipboard/Clipboard";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";
import { addTopLevelItemDefinition as _addTopLevelItemDefinition } from "../mutations/addTopLevelItemDefinition";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";

export type DataType = {
  itemDefinition: DMN15__tItemDefinition;
  parentId: string | undefined;
  parents: Set<string>;
  index: number;
  namespace: string;
  feelName: string;
  children?: DataType[];
};
export type DataTypeTreeViewDataItem = {};
export type DataTypeIndex = Map<string, DataType>;

export type AddItemComponent = (id: string, how: "unshift" | "push", partial?: Partial<DMN15__tItemDefinition>) => void;
export type AddTopLevelItemDefinition = (partial: Partial<DMN15__tItemDefinition>) => void;

export type EditItemDefinition = (
  id: string,
  consumer: (
    itemDefinition: DMN15__tItemDefinition,
    items: DMN15__tItemDefinition[],
    index: number,
    all: DMN15__tItemDefinition[],
    state: State
  ) => void
) => void;

export function DataTypes() {
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { activeItemDefinitionId } = useDmnEditorStore((s) => s.dataTypesEditor);

  const [filter, setFilter] = useState("");
  const { externalModelsByNamespace } = useExternalModels();
  const allTopLevelItemDefinitionUniqueNames = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allTopLevelItemDefinitionUniqueNames
  );
  const allDataTypesById = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allDataTypesById
  );
  const dataTypesTree = useDmnEditorStore((s) => s.computed(s).getDataTypes(externalModelsByNamespace).dataTypesTree);

  const activeDataType = useMemo(() => {
    return activeItemDefinitionId ? allDataTypesById.get(activeItemDefinitionId) : undefined;
  }, [activeItemDefinitionId, allDataTypesById]);

  const filteredTree = useMemo(
    () =>
      dataTypesTree.filter(({ itemDefinition: dataType }) =>
        dataType["@_name"].toLowerCase().includes(filter.toLowerCase())
      ),
    [filter, dataTypesTree]
  );

  const editItemDefinition = useCallback<EditItemDefinition>(
    (id, consumer) => {
      dmnEditorStoreApi.setState((state) => {
        const { itemDefinition, items, index } = findDataTypeById({
          definitions: state.dmn.model.definitions,
          itemDefinitionId: id,
          allDataTypesById,
        });

        state.dmn.model.definitions.itemDefinition ??= [];
        consumer(itemDefinition, items, index, state.dmn.model.definitions.itemDefinition, state);
      });
    },
    [allDataTypesById, dmnEditorStoreApi]
  );

  const addTopLevelItemDefinition = useCallback<AddTopLevelItemDefinition>(
    (partial) => {
      dmnEditorStoreApi.setState((state) => {
        const newItemDefinition = _addTopLevelItemDefinition({
          definitions: state.dmn.model.definitions,
          partial,
        });
        state.dataTypesEditor.activeItemDefinitionId = newItemDefinition["@_id"];
        state.focus.consumableId = newItemDefinition["@_id"];
      });
    },
    [dmnEditorStoreApi]
  );

  const pasteTopLevelItemDefinition = useCallback(() => {
    navigator.clipboard.readText().then((text) => {
      const clipboard = getClipboard<DmnEditorDataTypesClipboard>(text, DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE);
      if (!clipboard) {
        return;
      }

      getNewDmnIdRandomizer()
        .ack({
          json: clipboard.itemDefinitions,
          type: "DMN15__tDefinitions",
          attr: "itemDefinition",
        })
        .randomize();

      for (const itemDefinition of clipboard.itemDefinitions) {
        addTopLevelItemDefinition(itemDefinition);
      }
    });
  }, [addTopLevelItemDefinition]);

  const [isAddDataTypeDropdownOpen, setAddDataTypeDropdownOpen] = useState(false);

  // Using this object because DropdownToggleAction's props doesn't accept a 'title'.
  const extraPropsForDropdownToggleAction = { title: "New Data Type" };

  return (
    <>
      {(dataTypesTree.length <= 0 && (
        <DataTypesEmptyState
          onAdd={() => addTopLevelItemDefinition({ typeRef: { __$$text: DmnBuiltInDataType.Undefined } })}
          onPaste={pasteTopLevelItemDefinition}
        />
      )) || (
        <Drawer isExpanded={true} isInline={true} position={"left"} className={"kie-dmn-editor--data-types-container"}>
          <DrawerContent
            panelContent={
              <DrawerPanelContent isResizable={true} minSize={"300px"} defaultSize={"400px"}>
                <Flex
                  justifyContent={{ default: "justifyContentSpaceBetween" }}
                  alignItems={{ default: "alignItemsCenter" }}
                  className={"kie-dmn-editor--data-types-filter kie-dmn-editor--sticky-top-glass-header"}
                >
                  <InputGroup>
                    <SearchInput
                      placeholder="Filter..."
                      value={filter}
                      onChange={(_event, value) => setFilter(value)}
                      onClear={() => setFilter("")}
                    />

                    <Dropdown
                      onSelect={() => setAddDataTypeDropdownOpen(false)}
                      menuAppendTo={document.body}
                      toggle={
                        <DropdownToggle
                          id="add-data-type-toggle"
                          splitButtonItems={[
                            <DropdownToggleAction
                              {...extraPropsForDropdownToggleAction}
                              key="add-data-type-action"
                              aria-label="Add Data Type"
                              onClick={() =>
                                addTopLevelItemDefinition({ typeRef: { __$$text: DmnBuiltInDataType.Undefined } })
                              }
                            >
                              <PlusCircleIcon />
                            </DropdownToggleAction>,
                          ]}
                          splitButtonVariant="action"
                          onToggle={setAddDataTypeDropdownOpen}
                        />
                      }
                      position={DropdownPosition.right}
                      isOpen={isAddDataTypeDropdownOpen}
                      dropdownItems={[
                        <DropdownItem
                          key={"paste"}
                          onClick={() => pasteTopLevelItemDefinition()}
                          style={{ minWidth: "240px" }}
                          icon={<PasteIcon />}
                        >
                          Paste
                        </DropdownItem>,
                      ]}
                    />
                  </InputGroup>
                </Flex>
                <div className={`kie-dmn-editor--data-types-nav`}>
                  {filteredTree.map(({ namespace, itemDefinition, feelName }) => {
                    const isActive =
                      itemDefinition["@_id"] === activeDataType?.itemDefinition["@_id"] ||
                      (activeDataType?.parents.has(itemDefinition["@_id"]!) ?? false);

                    return (
                      <Flex
                        key={itemDefinition["@_id"]}
                        flexWrap={{ default: "nowrap" }}
                        spaceItems={{ default: "spaceItemsNone" }}
                        onClick={() =>
                          dmnEditorStoreApi.setState((state) => {
                            state.dataTypesEditor.activeItemDefinitionId = itemDefinition["@_id"]!;
                          })
                        }
                        justifyContent={{ default: "justifyContentFlexStart" }}
                        alignItems={{ default: "alignItemsCenter" }}
                        className={`kie-dmn-editor--data-types-nav-item ${isActive ? "active" : ""}`}
                      >
                        <InfrastructureIcon
                          style={{ display: "inline", opacity: isStruct(itemDefinition) ? 1 : 0, minWidth: "1em" }}
                        />
                        {(namespace === thisDmnsNamespace && (
                          <DataTypeName
                            relativeToNamespace={thisDmnsNamespace}
                            isReadonly={namespace !== thisDmnsNamespace}
                            itemDefinition={itemDefinition}
                            isActive={isActive}
                            editMode={"double-click"}
                            enableAutoFocusing={false} // Let the auto-focusing mechanism go for the title component
                            onGetAllUniqueNames={() => allTopLevelItemDefinitionUniqueNames}
                          />
                        )) || (
                          <>
                            <Label style={{ marginLeft: "8px" }}>External</Label>
                            <div
                              className={`kie-dmn-editor--editable-node-name-input top-left grow`}
                              style={
                                DMN15_SPEC.namedElement.isValidName(
                                  itemDefinition["@_id"]!,
                                  feelName,
                                  allTopLevelItemDefinitionUniqueNames
                                )
                                  ? {}
                                  : invalidInlineFeelNameStyle
                              }
                            >
                              {`${feelName}`}
                            </div>
                          </>
                        )}
                      </Flex>
                    );
                  })}
                </div>
              </DrawerPanelContent>
            }
          >
            <DrawerContentBody>
              {activeDataType && (
                <DataTypePanel
                  isReadonly={activeDataType.namespace !== thisDmnsNamespace}
                  dataType={activeDataType}
                  allDataTypesById={allDataTypesById}
                  editItemDefinition={editItemDefinition}
                />
              )}
            </DrawerContentBody>
          </DrawerContent>
        </Drawer>
      )}
    </>
  );
}
