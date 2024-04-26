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

import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import { CopyIcon } from "@patternfly/react-icons/dist/js/icons/copy-icon";
import { CutIcon } from "@patternfly/react-icons/dist/js/icons/cut-icon";
import { PasteIcon } from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { TypeRefSelector } from "./TypeRefSelector";
import {
  Dropdown,
  DropdownItem,
  DropdownSeparator,
  KebabToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { ImportIcon } from "@patternfly/react-icons/dist/js/icons/import-icon";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { AngleRightIcon } from "@patternfly/react-icons/dist/js/icons/angle-right-icon";
import { EyeIcon } from "@patternfly/react-icons/dist/js/icons/eye-icon";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { DataType, EditItemDefinition, AddItemComponent, DataTypeIndex } from "./DataTypes";
import { DataTypeName } from "./DataTypeName";
import { isStruct, canHaveConstraints, getNewItemDefinition } from "./DataTypeSpec";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import {
  DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE,
  DmnEditorDataTypesClipboard,
  buildClipboardFromDataType,
  getClipboard,
} from "../clipboard/Clipboard";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";
import { isEnum } from "./ConstraintsEnum";
import { isRange } from "./ConstraintsRange";
import { constraintTypeHelper } from "./Constraints";
import { builtInFeelTypeNames } from "./BuiltInFeelTypes";
import { useDmnEditor } from "../DmnEditorContext";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { resolveTypeRef } from "./resolveTypeRef";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";

export const BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL = 5;
export const STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE = 95;

const addItemComponentButtonWidthInPxs = 24;
const expandButtonWidthInPxs = 24;
const expandButtonoHorizontalMarginInPxs = 12;
const leftGutterForStructsInPxs =
  addItemComponentButtonWidthInPxs + expandButtonWidthInPxs + expandButtonoHorizontalMarginInPxs * 2;
const rowPaddingRight = 16;

export function ItemComponentsTable({
  isReadonly,
  parent,
  editItemDefinition,
  addItemComponent,
  dropdownOpenFor,
  allDataTypesById,
  setDropdownOpenFor,
}: {
  isReadonly: boolean;
  parent: DataType;
  editItemDefinition: EditItemDefinition;
  addItemComponent: AddItemComponent;
  allDataTypesById: DataTypeIndex;
  dropdownOpenFor: string | undefined;
  setDropdownOpenFor: React.Dispatch<React.SetStateAction<string | undefined>>;
}) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const { externalModelsByNamespace } = useExternalModels();
  const expandedItemComponentIds = useDmnEditorStore((s) => s.dataTypesEditor.expandedItemComponentIds);
  const allTopLevelDataTypesByFeelName = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allTopLevelDataTypesByFeelName
  );
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());

  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);

  const expandedItemComponentIdsSet = useMemo(() => {
    return new Set(expandedItemComponentIds);
  }, [expandedItemComponentIds]);

  const dataTypes = parent.children;

  const flatTree = useMemo(() => {
    const ret: { dataType: DataType; allUniqueNamesAtLevel: UniqueNameIndex }[] = [];
    function traverse(dataType: DataType[], allUniqueNamesAtLevel: UniqueNameIndex) {
      for (let i = 0; i < (dataType?.length ?? 0); i++) {
        ret.push({ dataType: dataType[i], allUniqueNamesAtLevel });
        traverse(
          dataType[i].children ?? [],
          (dataType[i].itemDefinition.itemComponent ?? []).reduce<UniqueNameIndex>(
            (acc, s) => acc.set(s["@_name"], s["@_id"]!),
            new Map([...builtInFeelTypeNames].map((s) => [s, s]))
          )
        );
      }
    }

    traverse(
      dataTypes ?? [],
      (parent.itemDefinition.itemComponent ?? []).reduce<UniqueNameIndex>(
        (acc, s) => acc.set(s["@_name"], s["@_id"]!),
        new Map([...builtInFeelTypeNames].map((s) => [s, s]))
      )
    );
    return ret;
  }, [dataTypes, parent.itemDefinition.itemComponent]);

  const expandAll = useCallback(() => {
    dmnEditorStoreApi.setState((state) => {
      state.dataTypesEditor.expandedItemComponentIds = flatTree.flatMap((s) =>
        isStruct(s.dataType.itemDefinition) ? s.dataType.itemDefinition["@_id"]! : []
      );
    });
  }, [dmnEditorStoreApi, flatTree]);

  const collapseAll = useCallback(() => {
    dmnEditorStoreApi.setState((state) => {
      state.dataTypesEditor.expandedItemComponentIds = [];
    });
  }, [dmnEditorStoreApi]);

  const { dmnEditorRootElementRef } = useDmnEditor();

  return (
    <>
      <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
        <FlexItem>
          <Title size={"md"} headingLevel={"h4"}>
            {`Properties in '${parent.itemDefinition["@_name"]}'`}
            {!isReadonly && (
              <Button
                title={"Add item component (at the top)"}
                variant={ButtonVariant.link}
                onClick={() =>
                  addItemComponent(parent.itemDefinition["@_id"]!, "unshift", {
                    "@_name": "New property",
                    typeRef: { __$$text: DmnBuiltInDataType.Undefined },
                  })
                }
              >
                <PlusCircleIcon />
              </Button>
            )}
          </Title>
        </FlexItem>
        <FlexItem>
          <Button variant={ButtonVariant.link} onClick={expandAll}>
            Expand all
          </Button>
          <Button variant={ButtonVariant.link} onClick={collapseAll}>
            Collapse all
          </Button>
          {!isReadonly && (
            <Dropdown
              toggle={
                <KebabToggle
                  id={"toggle-kebab-properties-table"}
                  onToggle={(isOpen) => setDropdownOpenFor(isOpen ? parent.itemDefinition["@_id"] : undefined)}
                />
              }
              onSelect={() => setDropdownOpenFor(undefined)}
              isOpen={dropdownOpenFor === parent.itemDefinition["@_id"]}
              menuAppendTo={document.body}
              isPlain={true}
              position={"right"}
              dropdownItems={[
                <DropdownItem
                  key={"paste-property"}
                  style={{ minWidth: "240px" }}
                  icon={<PasteIcon />}
                  onClick={() => {
                    navigator.clipboard.readText().then((text) => {
                      const clipboard = getClipboard<DmnEditorDataTypesClipboard>(
                        text,
                        DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE
                      );
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
                        addItemComponent(parent.itemDefinition["@_id"]!, "unshift", itemDefinition);
                      }
                    });
                  }}
                >
                  Paste property
                </DropdownItem>,
              ]}
            />
          )}
        </FlexItem>
      </Flex>
      {flatTree.length <= 0 && (
        <div className={"kie-dmn-editor--data-type-properties-table--empty-state"}>
          {isReadonly ? "None" : "None yet"}
        </div>
      )}
      {flatTree.length > 0 && (
        <table className={"kie-dmn-editor--data-type-properties-table"}>
          <thead>
            <tr>
              <th style={{ minWidth: "200px", width: "67%" }}>Name</th>
              <th style={{ minWidth: "140px", maxWidth: "140px" }}>Is struct?</th>
              <th style={{ minWidth: "280px", width: "33%" }}>Type</th>
              <th style={{ minWidth: "140px", maxWidth: "140px" }}>Is collection?</th>
              <th style={{ minWidth: "200px", maxWidth: "200px" }}>Constraints</th>
              <th>{/** Actions */}</th>
            </tr>
          </thead>
          <tbody>
            {flatTree.map(({ dataType: dt, allUniqueNamesAtLevel }, i) => {
              const nextDt = flatTree[Math.min(i + 1, flatTree.length - 1)].dataType;
              const lastDt = flatTree[Math.max(i - 1, 0)].dataType;

              const nextIsUpper = nextDt.parents.size < dt.parents.size;
              const lastIsUpper = lastDt.parents.size < dt.parents.size;

              let areAllParentsExpanded = true;
              for (const p of [...dt.parents].reverse()) {
                if (p === parent.itemDefinition["@_id"]) {
                  break; // The top-level ItemDefinition open, so this is where we stop checking.
                } else if (!expandedItemComponentIdsSet.has(p)) {
                  areAllParentsExpanded = false; // If one of the parents are not
                }
              }

              const shouldShowRow =
                dt.parentId === parent.itemDefinition["@_id"] ||
                (isStruct(allDataTypesById.get(dt.parentId!)!.itemDefinition) && areAllParentsExpanded);

              const level = dt.parents.size - parent.parents.size - 1;

              const brigthnessPercentage =
                STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE -
                level * BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL;

              const constraintLabel = () => {
                if (
                  dt.itemDefinition.typeConstraint?.["@_kie:constraintType"] === "enumeration" ||
                  dt.itemDefinition.allowedValues?.["@_kie:constraintType"] === "enumeration"
                ) {
                  return <>Enumeration</>;
                }
                if (
                  dt.itemDefinition.typeConstraint?.["@_kie:constraintType"] === "expression" ||
                  dt.itemDefinition.allowedValues?.["@_kie:constraintType"] === "expression"
                ) {
                  return <>Expression</>;
                }
                if (
                  dt.itemDefinition.typeConstraint?.["@_kie:constraintType"] === "range" ||
                  dt.itemDefinition.allowedValues?.["@_kie:constraintType"] === "range"
                ) {
                  return <>Range</>;
                }

                const constraintValue = dt.itemDefinition.allowedValues?.text.__$$text;
                const typeRef =
                  (dt.itemDefinition.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined;
                if (constraintValue === undefined) {
                  return <>None</>;
                }
                if (isEnum(constraintValue, constraintTypeHelper(typeRef).check)) {
                  return <>Enumeration</>;
                }
                if (isRange(constraintValue, constraintTypeHelper(typeRef).check)) {
                  return <>Range</>;
                }
                return <>Expression</>;
              };

              return (
                <React.Fragment key={dt.itemDefinition["@_id"]}>
                  {shouldShowRow && (
                    <tr
                      style={{ backdropFilter: `brightness(${brigthnessPercentage}%)` }}
                      className={`${nextIsUpper ? "last-nested-at-level" : ""} ${
                        lastIsUpper ? "first-nested-at-level" : ""
                      }`}
                    >
                      <td
                        style={{
                          paddingLeft: `${rowPaddingRight + level * leftGutterForStructsInPxs}px`,
                        }}
                      >
                        <div style={{ display: "flex" }}>
                          <div
                            style={{
                              width: `${expandButtonWidthInPxs}px`,
                              margin: `0 ${expandButtonoHorizontalMarginInPxs}px`,
                            }}
                          >
                            {isStruct(dt.itemDefinition) && (
                              <Button
                                title={"Expand / collapse item component"}
                                variant={ButtonVariant.link}
                                style={{ padding: "0 8px 0 0" }}
                                onClick={(e) =>
                                  dmnEditorStoreApi.setState((state) => {
                                    if (expandedItemComponentIdsSet.has(dt.itemDefinition["@_id"]!)) {
                                      state.dataTypesEditor.expandedItemComponentIds =
                                        state.dataTypesEditor.expandedItemComponentIds.filter(
                                          (s) => s !== dt.itemDefinition["@_id"]!
                                        );
                                    } else {
                                      state.dataTypesEditor.expandedItemComponentIds.push(dt.itemDefinition["@_id"]!);
                                    }
                                  })
                                }
                              >
                                {(expandedItemComponentIdsSet.has(dt.itemDefinition["@_id"]!) && <AngleDownIcon />) || (
                                  <AngleRightIcon />
                                )}
                              </Button>
                            )}
                          </div>
                          <div style={{ width: `${addItemComponentButtonWidthInPxs}px` }}>
                            {!isReadonly && isStruct(dt.itemDefinition) && (
                              <Button
                                title={"Add item component"}
                                variant={ButtonVariant.link}
                                style={{ padding: "0 8px 0 0" }}
                                onClick={() => {
                                  addItemComponent(dt.itemDefinition["@_id"]!, "unshift", {
                                    "@_name": "New property",
                                    typeRef: { __$$text: DmnBuiltInDataType.Undefined },
                                  });
                                  dmnEditorStoreApi.setState((state) => {
                                    state.dataTypesEditor.expandedItemComponentIds.push(dt.itemDefinition["@_id"]!);
                                  });
                                }}
                              >
                                <PlusCircleIcon />
                              </Button>
                            )}
                          </div>
                          <div style={{ flexGrow: 1 }}>
                            <DataTypeName
                              relativeToNamespace={dt.namespace}
                              editMode={"hover"}
                              isActive={false}
                              itemDefinition={dt.itemDefinition}
                              isReadonly={dt.namespace !== thisDmnsNamespace}
                              onGetAllUniqueNames={() => allUniqueNamesAtLevel}
                            />
                          </div>
                        </div>
                      </td>
                      <td>
                        <Switch
                          aria-label={"Is struct?"}
                          isChecked={isStruct(dt.itemDefinition)}
                          onChange={(isChecked) => {
                            editItemDefinition(dt.itemDefinition["@_id"]!, (itemDefinition, items) => {
                              if (isChecked) {
                                itemDefinition.typeRef = undefined;
                                itemDefinition.itemComponent = [];
                                itemDefinition.typeConstraint = undefined;
                                itemDefinition.allowedValues = undefined;
                              } else {
                                itemDefinition.typeRef = { __$$text: DmnBuiltInDataType.Any };
                                itemDefinition.typeConstraint = undefined;
                                itemDefinition.allowedValues = undefined;
                              }
                            });
                          }}
                        />
                      </td>
                      <td>
                        {!isStruct(dt.itemDefinition) && (
                          <TypeRefSelector
                            heightRef={dmnEditorRootElementRef}
                            isDisabled={isReadonly}
                            typeRef={resolveTypeRef({
                              typeRef: dt.itemDefinition.typeRef?.__$$text,
                              namespace: parent.namespace,
                              allTopLevelDataTypesByFeelName,
                              externalModelsByNamespace,
                              thisDmnsImportsByNamespace: importsByNamespace,
                              relativeToNamespace: thisDmnsNamespace,
                            })}
                            onChange={(newDataType) => {
                              editItemDefinition(dt.itemDefinition["@_id"]!, (itemDefinition, items) => {
                                itemDefinition.typeRef = { __$$text: newDataType };
                                if (itemDefinition.typeRef?.__$$text !== newDataType) {
                                  itemDefinition.typeConstraint = undefined;
                                  itemDefinition.allowedValues = undefined;
                                }
                              });
                            }}
                          />
                        )}
                      </td>
                      <td>
                        <Switch
                          aria-label={"Is collection?"}
                          isChecked={dt.itemDefinition["@_isCollection"] ?? false}
                          onChange={(isChecked) => {
                            editItemDefinition(dt.itemDefinition["@_id"]!, (itemDefinition, items) => {
                              itemDefinition["@_isCollection"] = isChecked;
                              itemDefinition.typeConstraint = undefined;
                              itemDefinition.allowedValues = undefined;
                            });
                          }}
                        />
                      </td>
                      <td>
                        {canHaveConstraints(dt.itemDefinition) ? (
                          <Button
                            variant={ButtonVariant.link}
                            onClick={() => {
                              dmnEditorStoreApi.setState((state) => {
                                state.dataTypesEditor.activeItemDefinitionId = dt.itemDefinition["@_id"]!;
                              });
                            }}
                          >
                            {constraintLabel()}
                          </Button>
                        ) : (
                          <p style={{ paddingLeft: "16px", paddingRight: "16px" }}>-</p>
                        )}
                      </td>
                      <td>
                        <Dropdown
                          toggle={
                            <KebabToggle
                              id={"toggle-kebab-" + dt.itemDefinition["@_id"]}
                              onToggle={(isOpen) => setDropdownOpenFor(isOpen ? dt.itemDefinition["@_id"] : undefined)}
                            />
                          }
                          onSelect={() => setDropdownOpenFor(undefined)}
                          isOpen={dropdownOpenFor === dt.itemDefinition["@_id"]}
                          menuAppendTo={document.body}
                          isPlain={true}
                          position={"right"}
                          dropdownItems={[
                            <DropdownItem
                              key={"view-type"}
                              icon={<EyeIcon />}
                              onClick={() => {
                                dmnEditorStoreApi.setState((state) => {
                                  state.dataTypesEditor.activeItemDefinitionId = dt.itemDefinition["@_id"]!;
                                });
                              }}
                            >
                              View
                            </DropdownItem>,
                            <DropdownSeparator key="view-separator" />,
                            <DropdownItem
                              key={"extract-to-top-level"}
                              icon={<ImportIcon style={{ transform: "scale(-1, -1)" }} />}
                              style={{ minWidth: "240px" }}
                              onClick={() => {
                                editItemDefinition(
                                  dt.itemDefinition["@_id"]!,
                                  (itemDefinition, _, __, itemDefinitions) => {
                                    const newItemDefinition = getNewItemDefinition({
                                      ...dt.itemDefinition,
                                      typeRef: dt.itemDefinition.typeRef,
                                      "@_name": `t${dt.itemDefinition["@_name"]}`,
                                      "@_isCollection": false,
                                    });

                                    const newItemDefinitionCopy: DMN15__tItemDefinition = JSON.parse(
                                      JSON.stringify(newItemDefinition)
                                    ); // Necessary because idRandomizer will mutate this object.

                                    getNewDmnIdRandomizer()
                                      .ack({
                                        json: [newItemDefinitionCopy],
                                        type: "DMN15__tDefinitions",
                                        attr: "itemDefinition",
                                      })
                                      .randomize();

                                    itemDefinitions.unshift(newItemDefinitionCopy);

                                    // Creating a new type is fine, but only update the current type if it is not readOnly
                                    if (!isReadonly) {
                                      itemDefinition["@_id"] = generateUuid();
                                      itemDefinition.typeRef = { __$$text: newItemDefinitionCopy["@_name"] };
                                      itemDefinition.itemComponent = undefined;
                                    }
                                  }
                                );
                              }}
                            >
                              Extract data type
                            </DropdownItem>,
                            <DropdownSeparator key="extract-data-type-separator" />,
                            <DropdownItem
                              key={"copy-item"}
                              icon={<CopyIcon />}
                              onClick={() => {
                                const clipboard = buildClipboardFromDataType(dt, thisDmnsNamespace);
                                navigator.clipboard.writeText(JSON.stringify(clipboard));
                              }}
                            >
                              Copy
                            </DropdownItem>,
                            <React.Fragment key={"cut-fragment"}>
                              {!isReadonly && (
                                <DropdownItem
                                  key={"cut-item"}
                                  icon={<CutIcon />}
                                  onClick={() => {
                                    const clipboard = buildClipboardFromDataType(dt, thisDmnsNamespace);
                                    navigator.clipboard.writeText(JSON.stringify(clipboard)).then(() => {
                                      editItemDefinition(dt.parentId!, (itemDefinition) => {
                                        itemDefinition.itemComponent?.splice(dt.index, 1);
                                      });
                                    });
                                  }}
                                >
                                  Cut
                                </DropdownItem>
                              )}
                            </React.Fragment>,
                            <React.Fragment key={"remove-fragment"}>
                              {!isReadonly && (
                                <DropdownItem
                                  key={"remove-item"}
                                  icon={<TrashIcon />}
                                  onClick={() => {
                                    editItemDefinition(dt.parentId!, (itemDefinition) => {
                                      itemDefinition.itemComponent?.splice(dt.index, 1);
                                    });
                                  }}
                                >
                                  Remove
                                </DropdownItem>
                              )}
                            </React.Fragment>,
                            !isReadonly && isStruct(dt.itemDefinition) ? (
                              <React.Fragment key="paste-property-fragment">
                                <DropdownSeparator />
                                <React.Fragment>
                                  <DropdownItem
                                    icon={<PasteIcon />}
                                    onClick={() => {
                                      navigator.clipboard.readText().then((text) => {
                                        const clipboard = getClipboard<DmnEditorDataTypesClipboard>(
                                          text,
                                          DMN_EDITOR_DATA_TYPES_CLIPBOARD_MIME_TYPE
                                        );
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
                                          addItemComponent(dt.itemDefinition["@_id"]!, "unshift", itemDefinition);
                                        }
                                      });
                                    }}
                                  >
                                    Paste property
                                  </DropdownItem>
                                </React.Fragment>
                              </React.Fragment>
                            ) : (
                              <React.Fragment key="paste-property-empty-fragment"></React.Fragment>
                            ),
                          ]}
                        />
                      </td>
                    </tr>
                  )}
                </React.Fragment>
              );
            })}
          </tbody>
          {!isReadonly && (
            <tfoot>
              <tr>
                <td colSpan={5}>
                  <Button
                    variant={ButtonVariant.link}
                    onClick={() =>
                      addItemComponent(parent.itemDefinition["@_id"]!, "push", {
                        "@_name": "New property",
                        typeRef: { __$$text: DmnBuiltInDataType.Undefined },
                      })
                    }
                    style={{ paddingLeft: 0 }}
                  >
                    <PlusCircleIcon />
                    &nbsp;&nbsp;{`Add property to '${parent.itemDefinition["@_name"]}'`}
                  </Button>
                </td>
              </tr>
            </tfoot>
          )}
        </table>
      )}
    </>
  );
}
