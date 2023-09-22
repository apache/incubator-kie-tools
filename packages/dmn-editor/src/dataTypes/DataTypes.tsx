import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DMN15__tDefinitions,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import CopyIcon from "@patternfly/react-icons/dist/js/icons/copy-icon";
import CutIcon from "@patternfly/react-icons/dist/js/icons/cut-icon";
import PasteIcon from "@patternfly/react-icons/dist/js/icons/paste-icon";
import LinkIcon from "@patternfly/react-icons/dist/js/icons/link-icon";
import InfrastructureIcon from "@patternfly/react-icons/dist/js/icons/infrastructure-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";

import * as React from "react";
import { useCallback, useMemo, useRef, useState } from "react";
import { SPEC } from "../Spec";
import { EditableNodeLabel, useEditableNodeLabel } from "../diagram/nodes/EditableNodeLabel";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { DataTypeLabel } from "./DataTypeLabel";
import { DataTypeSelector } from "./DataTypeSelector";
import { DataTypesEmptyState } from "./DataTypesEmptyState";
import {
  Dropdown,
  DropdownItem,
  DropdownSeparator,
  KebabToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { ArrowUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-up-icon";

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
  const { activeItemId } = useDmnEditorStore((s) => s.dataTypesEditor);

  const [filter, setFilter] = useState("");

  const { tree, dataTypesById } = useMemo(() => {
    const tree: DataType[] = [];
    const dataTypesById: DataTypesById = new Map();
    const items = thisDmn.model.definitions.itemDefinition ?? [];

    for (let i = 0; i < items.length; i++) {
      const dataType = items[i];
      tree.push(buildTree(dataType, undefined, dataTypesById, i, new Set()));
    }

    return { tree, dataTypesById };
  }, [thisDmn.model.definitions.itemDefinition]);

  const active = useMemo(() => {
    return activeItemId ? dataTypesById.get(activeItemId) : undefined;
  }, [activeItemId, dataTypesById]);

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
        state.dataTypesEditor.activeItemId = newItemDefinition["@_id"];
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
          state.dataTypesEditor.activeItemId = tree[(active?.index + 1) % filteredTree.length].itemDefinition["@_id"];
        } else if (e.key === "ArrowUp") {
          state.dataTypesEditor.activeItemId =
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
                          state.dataTypesEditor.activeItemId = itemDefinition["@_id"]!;
                        })
                      }
                      justifyContent={{ default: "justifyContentFlexStart" }}
                      alignItems={{ default: "alignItemsCenter" }}
                      className={`kie-dmn-editor--data-types-nav-item ${
                        activeItemId === itemDefinition["@_id"] ? "active" : ""
                      }`}
                    >
                      <InfrastructureIcon style={{ display: "inline", opacity: itemDefinition.typeRef ? 0 : 1 }} />
                      <DataTypeName
                        itemDefinition={itemDefinition}
                        isActive={activeItemId === itemDefinition["@_id"]}
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
        new Set([...parents, ...(parentId ? [parentId] : [])])
      )
    ),
  };

  dataTypesById.set(itemDefinition["@_id"]!, dataType);

  return dataType;
}

function DataTypeName({
  itemDefinition,
  isActive,
  editItemDefinition,
  editMode,
}: {
  editMode: "hover" | "double-click";
  itemDefinition: DMN15__tItemDefinition;
  isActive: boolean;
  editItemDefinition: EditItemDefinition;
}) {
  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel();

  const onRenamed = useCallback(
    (newName: string | undefined) => {
      if (!newName?.trim()) {
        return;
      }
      editItemDefinition(itemDefinition["@_id"]!, (itemComponent) => {
        itemComponent["@_name"] = newName;
      });
    },
    [editItemDefinition, itemDefinition]
  );

  const inputRef = useRef<HTMLInputElement>(null);

  const previouslyFocusedElement = useRef<Element | undefined>();

  const restoreFocus = useCallback(() => {
    // We only restore the focus to the previously focused element if we're still holding focus. If focus has changed, we let it be.
    setTimeout(() => {
      if (document.activeElement === inputRef.current) {
        (previouslyFocusedElement.current as any)?.focus?.();
      }
    }, 0);
  }, []);

  return (
    <>
      {editMode === "hover" && (
        <div>
          <input
            ref={inputRef}
            key={itemDefinition["@_id"] + itemDefinition["@_name"]}
            style={{
              border: 0,
              flexGrow: 1,
              outline: "none",
              display: "inline",
              background: "transparent",
              width: "100%",
            }}
            defaultValue={itemDefinition["@_name"]}
            onFocus={(e) => {
              previouslyFocusedElement.current = document.activeElement ?? undefined; // Save potential focused element.
            }}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                e.stopPropagation();
                e.preventDefault();
                onRenamed(e.currentTarget.value);
              } else if (e.key === "Escape") {
                e.stopPropagation();
                e.preventDefault();
                e.currentTarget.value = itemDefinition["@_name"];
                e.currentTarget.blur();
              }
            }}
            onBlur={(e) => {
              onRenamed(e.currentTarget.value);
              restoreFocus();
            }}
          />
        </div>
      )}
      {editMode === "double-click" && (
        <Flex
          tabIndex={-1}
          style={isEditingLabel ? { flexGrow: 1 } : {}}
          flexWrap={{ default: "nowrap" }}
          spaceItems={{ default: "spaceItemsNone" }}
          justifyContent={{ default: "justifyContentFlexStart" }}
          alignItems={{ default: "alignItemsCenter" }}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          <EditableNodeLabel
            grow={true}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            onChange={onRenamed}
            saveOnBlur={true}
            value={itemDefinition["@_name"]}
            key={itemDefinition["@_id"]}
            position={"top-left"}
            namedElement={itemDefinition}
            namedElementQName={{ type: "xml-qname", localPart: itemDefinition["@_name"] }}
          />
          {!isStruct(itemDefinition) && !isEditingLabel && (
            <DataTypeLabel typeRef={itemDefinition.typeRef} isCollection={itemDefinition["@_isCollection"]} />
          )}
        </Flex>
      )}
    </>
  );
}

function findDataTypeById({
  definitions,
  dataTypeId,
  dataTypesById,
}: {
  dataTypesById: DataTypesById;
  dataTypeId: string;
  definitions: DMN15__tDefinitions;
}) {
  const indexesPath: number[] = [];
  let current = dataTypesById.get(dataTypeId);
  do {
    indexesPath.unshift(current!.index);
    current = current!.parentId ? dataTypesById.get(current!.parentId) : undefined;
  } while (current);

  const last = indexesPath.pop()!; // Since we're using do-while, it's guaranteed we'll have at least one element on the `indexesPath` array.

  definitions.itemDefinition ??= [];
  let items = definitions.itemDefinition;
  for (const i of indexesPath) {
    items = items![i].itemComponent!;
  }
  const itemDefinition = items![last];
  return { items, itemDefinition, index: last };
}

export function DataTypePanel({
  dataType,
  dataTypesById,
  editItemDefinition,
}: {
  dataType: DataType;
  dataTypesById: DataTypesById;
  editItemDefinition: EditItemDefinition;
}) {
  const toggleStruct = useCallback(
    (isChecked: boolean) => {
      editItemDefinition(dataType.itemDefinition["@_id"]!, (itemDefinition) => {
        if (isChecked) {
          itemDefinition.typeRef = undefined;
          itemDefinition.itemComponent = [];
        } else {
          itemDefinition.typeRef = DmnBuiltInDataType.Any;
          itemDefinition.itemComponent = undefined;
        }
      });
    },
    [dataType.itemDefinition, editItemDefinition]
  );

  const toggleCollection = useCallback(
    (isChecked: boolean) => {
      editItemDefinition(dataType.itemDefinition["@_id"]!, (itemDefinition) => {
        itemDefinition["@_isCollection"] = isChecked;
      });
    },
    [dataType.itemDefinition, editItemDefinition]
  );

  const changeTypeRef = useCallback(
    (typeRef: DmnBuiltInDataType) => {
      editItemDefinition(dataType.itemDefinition["@_id"]!, (itemDefinition) => {
        itemDefinition.typeRef = typeRef;
      });
    },
    [dataType.itemDefinition, editItemDefinition]
  );

  const changeDescription = useCallback(
    (newDescription: string) => {
      editItemDefinition(dataType.itemDefinition["@_id"]!, (itemDefinition) => {
        itemDefinition.description = newDescription;
      });
    },
    [dataType.itemDefinition, editItemDefinition]
  );

  const parents = useMemo(() => {
    const parents: DataType[] = [];

    let cur = dataType;
    while (cur.parentId) {
      const p = dataTypesById.get(cur.parentId)!;
      parents.unshift(p);
      cur = p;
    }

    return parents;
  }, [dataType, dataTypesById]);

  const addItemComponent = useCallback<AddItemComponent>(
    (id, how, partial) => {
      editItemDefinition(id, (itemDefinition) => {
        const newItemDefinition = getNewItemDefinition({ "@_name": "New property", ...partial });
        itemDefinition.itemComponent ??= [];
        itemDefinition.itemComponent[how](newItemDefinition);
      });
    },
    [editItemDefinition]
  );

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const [dropdownOpenFor, setDropdownOpenFor] = useState<string | undefined>(undefined);

  return (
    <PageSection>
      <div className={"kie-dmn-editor--data-type-parents"}>
        {parents.map((p) => (
          <Button
            key={p.itemDefinition["@_id"]!}
            variant={ButtonVariant.link}
            onClick={() => {
              dmnEditorStoreApi.setState((state) => {
                state.dataTypesEditor.activeItemId = p.itemDefinition["@_id"]!;
              });
            }}
          >
            {p.itemDefinition["@_name"]}
          </Button>
        ))}
      </div>
      <div style={{ fontSize: "2em" }} className={"kie-dmn-editor--data-types-title"}>
        <DataTypeName
          itemDefinition={dataType.itemDefinition}
          editItemDefinition={editItemDefinition}
          isActive={false}
          editMode={"hover"}
        />
      </div>
      <br />
      <TextArea
        key={dataType.itemDefinition["@_id"]}
        value={dataType.itemDefinition.description}
        onChange={changeDescription}
        placeholder={"Enter a description..."}
        resizeOrientation={"vertical"}
        aria-label={"Data type description"}
      />
      <br />
      <br />
      <Divider inset={{ default: "insetMd" }} />
      <br />
      <Switch
        label={"Is collection?"}
        isChecked={!!dataType.itemDefinition["@_isCollection"]}
        onChange={toggleCollection}
      />
      <br />
      <br />
      <Switch label={"Is struct?"} isChecked={!!dataType.itemDefinition.itemComponent} onChange={toggleStruct}></Switch>
      <br />
      <br />
      <Divider inset={{ default: "insetMd" }} />
      <br />
      {dataType.itemDefinition.typeRef && (
        <>
          <Title size={"md"} headingLevel="h4">
            Type
          </Title>
          <DataTypeSelector name={dataType.itemDefinition.typeRef} onChange={changeTypeRef} />
          <br />
          <br />
          <Title size={"md"} headingLevel="h4">
            Constraints
          </Title>
          <br />
        </>
      )}
      {!dataType.itemDefinition.typeRef && (
        <>
          <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
            <FlexItem>
              <Title size={"md"} headingLevel="h4">
                {`Properties in '${dataType.itemDefinition["@_name"]}'`}
                <Button
                  variant={ButtonVariant.link}
                  onClick={() => addItemComponent(dataType.itemDefinition["@_id"]!, "unshift")}
                >
                  <PlusCircleIcon />
                </Button>
              </Title>
            </FlexItem>
            <FlexItem>
              <Dropdown
                toggle={
                  <KebabToggle
                    id="toggle-kebab"
                    onToggle={(isOpen) => setDropdownOpenFor(isOpen ? dataType.itemDefinition["@_id"] : undefined)}
                  />
                }
                onSelect={() => setDropdownOpenFor(undefined)}
                isOpen={dropdownOpenFor === dataType.itemDefinition["@_id"]}
                menuAppendTo={document.body}
                isPlain={true}
                position={"right"}
                dropdownItems={[
                  <React.Fragment key={"copy-fragment"}>
                    {isStruct(dataType.itemDefinition) && (
                      <DropdownItem
                        key={"paste"}
                        style={{ minWidth: "240px" }}
                        icon={<PasteIcon />}
                        onClick={() => {
                          navigator.clipboard.readText().then((t) => {
                            const pastedItemDefinition = JSON.parse(t) as DMN15__tItemDefinition;
                            // FIXME: Tiago --> Validate
                            addItemComponent(
                              dataType.itemDefinition["@_id"]!,
                              "unshift",
                              reassignIds(pastedItemDefinition, "itemComponent")
                            );
                          });
                        }}
                      >
                        Paste
                      </DropdownItem>
                    )}
                  </React.Fragment>,
                ]}
              />
            </FlexItem>
          </Flex>
          <ItemComponentsTable
            addItemComponent={addItemComponent}
            dataTypesById={dataTypesById}
            parent={dataType}
            editItemDefinition={editItemDefinition}
            dropdownOpenFor={dropdownOpenFor}
            setDropdownOpenFor={setDropdownOpenFor}
          />
        </>
      )}
    </PageSection>
  );
}

const BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL = 5;
const STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE = 95;

function ItemComponentsTable({
  parent,
  editItemDefinition,
  addItemComponent,
  dropdownOpenFor,
  setDropdownOpenFor,
}: {
  parent: DataType;
  editItemDefinition: EditItemDefinition;
  addItemComponent: AddItemComponent;
  dataTypesById: DataTypesById;
  dropdownOpenFor: string | undefined;
  setDropdownOpenFor: React.Dispatch<React.SetStateAction<string | undefined>>;
}) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { expandedItemComponentIds } = useDmnEditorStore((s) => s.dataTypesEditor);

  const expandedItemComponentIdsSet = new Set(expandedItemComponentIds);

  const dataTypes = parent.children;

  const flat = useMemo(() => {
    const ret: DataType[] = [];
    function b(d: DataType) {
      ret.push(d);
      for (let i = 0; i < (d.children?.length ?? 0); i++) {
        b(d.children![i]);
      }
    }

    for (let i = 0; i < (dataTypes?.length ?? 0); i++) {
      b(dataTypes![i]);
    }

    return ret;
  }, [dataTypes]);

  return (
    <table className={"kie-dmn-editor--data-type-properties-table"}>
      <thead>
        <tr>
          <th style={{ minWidth: "200px", width: "67%" }}>Name</th>
          <th style={{ minWidth: "140px", maxWidth: "140px" }}>Is struct?</th>
          <th style={{ minWidth: "280px", width: "33%" }}>Type</th>
          <th style={{ minWidth: "140px", maxWidth: "140px" }}>Is collection?</th>
          <th style={{ minWidth: "160px", maxWidth: "160px" }}></th>
          <th style={{ minWidth: "160px", maxWidth: "160px" }}></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {flat.map((ic, i) => {
          const nextIc = flat[Math.min(i + 1, flat.length - 1)];
          const lastIc = flat[Math.max(i - 1, 0)];

          const brigthnessPercentage =
            STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE -
            ic.parents.size * BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL;

          const lastBrigthnessPercentage =
            STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE -
            lastIc.parents.size * BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL;

          const nextBrigthnessPercentage =
            STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE -
            nextIc.parents.size * BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL;

          const nextIsUpper = nextBrigthnessPercentage > brigthnessPercentage;
          const lastIsUpper = lastBrigthnessPercentage > brigthnessPercentage;

          return (
            <React.Fragment key={ic.itemDefinition["@_id"]}>
              {/* {(ic.parents.size === 0 || [...ic.parents].some((s) => expandedItemComponentIdsSet.has(s))) && ( */}
              {true && (
                <tr
                  style={{ backdropFilter: `brightness(${brigthnessPercentage}%)` }}
                  className={`${nextIsUpper ? "last-nested-at-level" : ""} ${
                    lastIsUpper ? "first-nested-at-level" : ""
                  }`}
                >
                  <td style={{ paddingLeft: `${16 + ic.parents.size * 2 * 24}px` }}>
                    <div style={{ display: "flex" }}>
                      <div style={{ width: "24px" }}>
                        {isStruct(ic.itemDefinition) && (
                          <Button
                            variant={ButtonVariant.link}
                            style={{ padding: "0 8px 0 0" }}
                            onClick={() => addItemComponent(ic.itemDefinition["@_id"]!, "unshift")}
                          >
                            <PlusCircleIcon />
                          </Button>
                        )}
                      </div>
                      <DataTypeName
                        editMode={"hover"}
                        editItemDefinition={editItemDefinition}
                        isActive={false}
                        itemDefinition={ic.itemDefinition}
                      />
                    </div>
                  </td>
                  <td>
                    <Switch
                      aria-label={"Is struct?"}
                      isChecked={isStruct(ic.itemDefinition)}
                      onChange={(isChecked) => {
                        editItemDefinition(ic.itemDefinition["@_id"]!, (itemDefinition, items) => {
                          if (isChecked) {
                            itemDefinition.typeRef = undefined;
                            itemDefinition.itemComponent = [];
                          } else {
                            itemDefinition.typeRef = DmnBuiltInDataType.Any;
                            itemDefinition.itemComponent = undefined;
                          }
                        });
                      }}
                    />
                  </td>
                  <td>
                    {!isStruct(ic.itemDefinition) && (
                      <DataTypeSelector
                        name={ic.itemDefinition.typeRef}
                        onChange={(newDataType) => {
                          editItemDefinition(ic.itemDefinition["@_id"]!, (itemDefinition, items) => {
                            itemDefinition.typeRef = newDataType;
                          });
                        }}
                      />
                    )}
                  </td>
                  <td>
                    <Switch
                      aria-label={"Is collection?"}
                      isChecked={ic.itemDefinition["@_isCollection"] ?? false}
                      onChange={(isChecked) => {
                        editItemDefinition(ic.itemDefinition["@_id"]!, (itemDefinition, items) => {
                          itemDefinition["@_isCollection"] = isChecked;
                        });
                      }}
                    />
                  </td>
                  <td>
                    {canHaveConstraints(ic.itemDefinition) && (
                      <Button
                        variant={ButtonVariant.link}
                        onClick={() => {
                          dmnEditorStoreApi.setState((state) => {
                            state.dataTypesEditor.activeItemId = ic.itemDefinition["@_id"]!;
                          });
                        }}
                      >
                        <LinkIcon />
                        &nbsp;Constraints
                      </Button>
                    )}
                  </td>
                  <td>
                    <Button
                      variant={ButtonVariant.link}
                      onClick={() => {
                        editItemDefinition(ic.parentId!, (itemDefinition) => {
                          itemDefinition.itemComponent?.splice(ic.index, 1);
                        });
                      }}
                    >
                      Remove
                    </Button>
                  </td>
                  <td>
                    <Dropdown
                      toggle={
                        <KebabToggle
                          id="toggle-kebab"
                          onToggle={(isOpen) => setDropdownOpenFor(isOpen ? ic.itemDefinition["@_id"] : undefined)}
                        />
                      }
                      onSelect={() => setDropdownOpenFor(undefined)}
                      isOpen={dropdownOpenFor === ic.itemDefinition["@_id"]}
                      menuAppendTo={document.body}
                      isPlain={true}
                      position={"right"}
                      dropdownItems={[
                        <DropdownItem
                          key={"extract-to-top-level"}
                          icon={<ArrowUpIcon />}
                          style={{ minWidth: "240px" }}
                          onClick={() => {
                            editItemDefinition(ic.itemDefinition["@_id"]!, (itemDefinition, _, __, itemDefinitions) => {
                              const newItemDefinition = reassignIds(
                                getNewItemDefinition({
                                  ...ic.itemDefinition,
                                  typeRef: undefined,
                                  "@_name": `t${ic.itemDefinition["@_name"]}`,
                                  "@_isCollection": false,
                                }),
                                "itemComponent"
                              );

                              itemDefinitions.unshift(newItemDefinition);

                              itemDefinition["@_id"] = generateUuid();
                              itemDefinition.typeRef = newItemDefinition["@_name"];
                              itemDefinition.itemComponent = undefined;
                            });
                          }}
                        >
                          Extract data type
                        </DropdownItem>,
                        <DropdownSeparator key="separator-1" />,
                        <DropdownItem
                          key={"copy"}
                          icon={<CopyIcon />}
                          onClick={() => {
                            navigator.clipboard.writeText(JSON.stringify(ic.itemDefinition));
                          }}
                        >
                          Copy
                        </DropdownItem>,
                        <DropdownItem
                          key={"cut"}
                          icon={<CutIcon />}
                          onClick={() => {
                            navigator.clipboard.writeText(JSON.stringify(ic.itemDefinition));
                            editItemDefinition(ic.parentId!, (itemDefinition) => {
                              itemDefinition.itemComponent?.splice(ic.index, 1);
                            });
                          }}
                        >
                          Cut
                        </DropdownItem>,
                        <React.Fragment key={"copy-fragment"}>
                          {isStruct(ic.itemDefinition) && (
                            <DropdownItem
                              key={"paste"}
                              icon={<PasteIcon />}
                              onClick={() => {
                                navigator.clipboard.readText().then((t) => {
                                  const pastedItemDefinition = JSON.parse(t);
                                  // FIXME: Tiago --> Validate
                                  addItemComponent(
                                    ic.itemDefinition["@_id"]!,
                                    "unshift",
                                    reassignIds(pastedItemDefinition, "itemComponent")
                                  );
                                });
                              }}
                            >
                              Paste
                            </DropdownItem>
                          )}
                        </React.Fragment>,
                      ]}
                    />
                  </td>
                </tr>
              )}
            </React.Fragment>
          );
        })}
      </tbody>
      <tfoot>
        <tr>
          <td>
            <Button
              variant={ButtonVariant.link}
              onClick={() => addItemComponent(parent.itemDefinition["@_id"]!, "push")}
              style={{ paddingLeft: 0 }}
            >
              <PlusCircleIcon />
              &nbsp;{`Add property to '${parent.name}'`}
            </Button>
          </td>
        </tr>
      </tfoot>
    </table>
  );
}

function getNewItemDefinition(partial?: Partial<DMN15__tItemDefinition>) {
  return {
    "@_id": generateUuid(),
    "@_name": "New data type",
    "@_isCollection": false,
    "@_typeLanguage": SPEC.typeLanguage.default,
    typeRef: DmnBuiltInDataType.Any,
    ...(partial ?? {}),
  };
}

function isStruct(itemDefinition: DMN15__tItemDefinition) {
  return !itemDefinition.typeRef && !!itemDefinition.itemComponent;
}

const constrainableBuiltInDataTypes = new Map<DmnBuiltInDataType, string[]>([
  [DmnBuiltInDataType.Any, ["expression"]],
  [DmnBuiltInDataType.Boolean, []],
  [DmnBuiltInDataType.Context, []],
  [DmnBuiltInDataType.Number, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.String, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.DateTimeDuration, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.YearsMonthsDuration, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.Date, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.Time, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.DateTime, ["expression", "enum", "range"]],
]);
function canHaveConstraints(itemDefinition: DMN15__tItemDefinition) {
  return (
    !isStruct(itemDefinition) &&
    (constrainableBuiltInDataTypes.get(itemDefinition.typeRef as DmnBuiltInDataType)?.length ?? 0) > 0
  );
}

function reassignIds<O extends { "@_id"?: string }, T extends keyof O>(obj: O, prop: T): O {
  obj = { ...obj, "@_id": generateUuid() };

  if (obj[prop]) {
    const newArr = [];
    for (const nested of obj[prop] as O[]) {
      newArr.push(reassignIds(nested, prop));
    }
    (obj[prop] as O[]) = newArr;
  }

  return obj;
}
