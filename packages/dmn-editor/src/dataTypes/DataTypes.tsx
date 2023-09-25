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
import InfrastructureIcon from "@patternfly/react-icons/dist/js/icons/infrastructure-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { DataTypesEmptyState } from "./DataTypesEmptyState";
import { DataTypePanel } from "./DataTypePanel";
import { findDataTypeById, getNewItemDefinition, isStruct } from "./DataTypeSpec";
import { DataTypeName } from "./DataTypeName";

export type DataType = {
  itemDefinition: DMN15__tItemDefinition;
  name: string;
  parentId: string | undefined;
  parents: Set<string>;
  index: number;
  children?: DataType[];
};
export type DataTypeTreeViewDataItem = {};
export type DataTypesById = Map<string, DataType>;

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
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { activeItemDefinitionId } = useDmnEditorStore((s) => s.dataTypesEditor);

  const [filter, setFilter] = useState("");

  const { tree, dataTypesById } = useMemo(() => {
    const tree: DataType[] = [];
    const dataTypesById: DataTypesById = new Map();
    const items = thisDmn.model.definitions.itemDefinition ?? [];

    for (let i = 0; i < items.length; i++) {
      const item = items[i];
      tree.push(buildTree(item, undefined, dataTypesById, i, new Set()));
    }

    return { tree, dataTypesById };
  }, [thisDmn.model.definitions.itemDefinition]);

  const active = useMemo(() => {
    return activeItemDefinitionId ? dataTypesById.get(activeItemDefinitionId) : undefined;
  }, [activeItemDefinitionId, dataTypesById]);

  const filteredTree = useMemo(
    () =>
      tree.filter(({ itemDefinition: dataType }) => dataType["@_name"].toLowerCase().includes(filter.toLowerCase())),
    [filter, tree]
  );

  const editItemDefinition = useCallback<EditItemDefinition>(
    (id, consumer) => {
      dmnEditorStoreApi.setState((state) => {
        const { itemDefinition, items, index } = findDataTypeById({
          definitions: state.dmn.model.definitions,
          dataTypeId: id,
          dataTypesById,
        });

        state.dmn.model.definitions.itemDefinition ??= [];
        consumer(itemDefinition, items, index, state.dmn.model.definitions.itemDefinition);
      });
    },
    [dataTypesById, dmnEditorStoreApi]
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

  const nav = useCallback(
    (e: React.KeyboardEvent) => {
      if (!active) {
        return;
      }
      dmnEditorStoreApi.setState((state) => {
        if (e.key === "ArrowDown") {
          state.dataTypesEditor.activeItemDefinitionId =
            tree[(active?.index + 1) % filteredTree.length].itemDefinition["@_id"];
        } else if (e.key === "ArrowUp") {
          state.dataTypesEditor.activeItemDefinitionId =
            tree[
              (active?.index - 1 === -1 ? filteredTree.length - 1 : active?.index - 1) % filteredTree.length
            ].itemDefinition["@_id"];
        }
      });
    },
    [active, dmnEditorStoreApi, filteredTree.length, tree]
  );

  return (
    <>
      {(tree.length <= 0 && <DataTypesEmptyState onAdd={addTopLevelItemDefinition} />) || (
        <Drawer isExpanded={true} isInline={true} position={"left"} className={"kie-dmn-editor--data-types-container"}>
          <DrawerContent
            panelContent={
              <DrawerPanelContent isResizable={true} minSize={"300px"} defaultSize={"400px"}>
                <Flex
                  justifyContent={{ default: "justifyContentSpaceBetween" }}
                  className={"kie-dmn-editor--data-types-filter"}
                >
                  <input value={filter} onChange={(e) => setFilter(e.target.value)} placeholder={"Filter..."} />
                  <Button onClick={() => addTopLevelItemDefinition()} variant={ButtonVariant.plain}>
                    <PlusCircleIcon />
                  </Button>
                </Flex>
                <div className={`kie-dmn-editor--data-types-nav`} onKeyDown={nav}>
                  {filteredTree.map(({ itemDefinition }) => (
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
                      <DataTypeName
                        itemDefinition={itemDefinition}
                        isActive={activeItemDefinitionId === itemDefinition["@_id"]}
                        editItemDefinition={editItemDefinition}
                        editMode={"double-click"}
                      />
                    </Flex>
                  ))}
                </div>
              </DrawerPanelContent>
            }
          >
            <DrawerContentBody>
              {active && (
                <DataTypePanel
                  dataType={active}
                  dataTypesById={dataTypesById}
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

function buildTree(
  itemDefinition: DMN15__tItemDefinition,
  parentId: string | undefined,
  dataTypesById: DataTypesById,
  index: number,
  parents: Set<string>
): DataType {
  const dataType = {
    itemDefinition,
    name: itemDefinition["@_name"],
    index,
    parentId,
    parents,
    children: itemDefinition.itemComponent?.map((component, componentIndex) =>
      buildTree(
        component,
        itemDefinition["@_id"],
        dataTypesById,
        componentIndex,
        new Set([...parents, itemDefinition["@_id"]!])
      )
    ),
  };

  dataTypesById.set(itemDefinition["@_id"]!, dataType);

  return dataType;
}
