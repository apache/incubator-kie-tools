import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { InfrastructureIcon } from "@patternfly/react-icons/dist/js/icons/infrastructure-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { DataTypesEmptyState } from "./DataTypesEmptyState";
import { DataTypePanel } from "./DataTypePanel";
import { findDataTypeById, getNewItemDefinition, isStruct } from "./DataTypeSpec";
import { DataTypeName } from "./DataTypeName";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { SPEC } from "../Spec";
import { invalidInlineFeelNameStyle } from "../feel/InlineFeelNameInput";

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
export type AddTopLevelItemDefinition = (partial?: Partial<DMN15__tItemDefinition>) => void;

export type EditItemDefinition = (
  id: string,
  consumer: (
    itemDefinition: DMN15__tItemDefinition,
    items: DMN15__tItemDefinition[],
    index: number,
    all: DMN15__tItemDefinition[]
  ) => void
) => void;

export function DataTypes() {
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { activeItemDefinitionId } = useDmnEditorStore((s) => s.dataTypesEditor);

  const [filter, setFilter] = useState("");

  const { allDataTypesById, dataTypesTree, allTopLevelItemDefinitionUniqueNames } = useDmnEditorDerivedStore();

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
        consumer(itemDefinition, items, index, state.dmn.model.definitions.itemDefinition);
      });
    },
    [allDataTypesById, dmnEditorStoreApi]
  );

  const addTopLevelItemDefinition = useCallback<AddTopLevelItemDefinition>(
    (partial) => {
      dmnEditorStoreApi.setState((state) => {
        const newItemDefinition = getNewItemDefinition(partial);
        state.dmn.model.definitions.itemDefinition ??= [];
        state.dmn.model.definitions.itemDefinition?.unshift(newItemDefinition);
        state.dataTypesEditor.activeItemDefinitionId = newItemDefinition["@_id"];
      });
    },
    [dmnEditorStoreApi]
  );

  return (
    <>
      {(dataTypesTree.length <= 0 && <DataTypesEmptyState onAdd={addTopLevelItemDefinition} />) || (
        <Drawer isExpanded={true} isInline={true} position={"left"} className={"kie-dmn-editor--data-types-container"}>
          <DrawerContent
            panelContent={
              <DrawerPanelContent isResizable={true} minSize={"300px"} defaultSize={"400px"}>
                <Flex
                  justifyContent={{ default: "justifyContentSpaceBetween" }}
                  className={"kie-dmn-editor--data-types-filter kie-dmn-editor--sticky-top-glass-header"}
                >
                  <input value={filter} onChange={(e) => setFilter(e.target.value)} placeholder={"Filter..."} />
                  <Button onClick={() => addTopLevelItemDefinition()} variant={ButtonVariant.link}>
                    <PlusCircleIcon />
                  </Button>
                </Flex>
                <div className={`kie-dmn-editor--data-types-nav`}>
                  {filteredTree.map(({ namespace, itemDefinition, feelName }) => (
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
                      className={`kie-dmn-editor--data-types-nav-item ${
                        activeItemDefinitionId === itemDefinition["@_id"] ? "active" : ""
                      }`}
                    >
                      <InfrastructureIcon
                        style={{ display: "inline", opacity: isStruct(itemDefinition) ? 1 : 0, minWidth: "1em" }}
                      />
                      {(namespace === thisDmnsNamespace && (
                        <DataTypeName
                          relativeToNamespace={thisDmnsNamespace}
                          isReadonly={namespace !== thisDmnsNamespace}
                          itemDefinition={itemDefinition}
                          isActive={activeItemDefinitionId === itemDefinition["@_id"]}
                          editMode={"double-click"}
                          allUniqueNames={allTopLevelItemDefinitionUniqueNames}
                        />
                      )) || (
                        <>
                          <Label style={{ marginLeft: "8px" }}>External</Label>
                          <div
                            className={`kie-dmn-editor--editable-node-name-input top-left grow`}
                            style={
                              SPEC.namedElement.isValidName(
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
                  ))}
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
