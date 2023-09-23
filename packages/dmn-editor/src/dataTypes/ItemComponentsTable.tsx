import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import CopyIcon from "@patternfly/react-icons/dist/js/icons/copy-icon";
import CutIcon from "@patternfly/react-icons/dist/js/icons/cut-icon";
import PasteIcon from "@patternfly/react-icons/dist/js/icons/paste-icon";
import LinkIcon from "@patternfly/react-icons/dist/js/icons/link-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import * as React from "react";
import { useMemo } from "react";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { DataTypeSelector } from "./DataTypeSelector";
import {
  Dropdown,
  DropdownItem,
  DropdownSeparator,
  KebabToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { ArrowUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-up-icon";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { AngleRightIcon } from "@patternfly/react-icons/dist/js/icons/angle-right-icon";
import { DataType, EditItemDefinition, AddItemComponent, DataTypesById } from "./DataTypes";
import { DataTypeName } from "./DataTypeName";
import { isStruct, canHaveConstraints, reassignIds, getNewItemDefinition } from "./DataTypeSpec";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export const BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL = 5;
export const STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE = 95;

const addItemComponentButtonWidthInPxs = 24;
const expandButtonWidthInPxs = 24;
const expandButtonoHorizontalMarginInPxs = 12;
const leftGutterForStructsInPxs =
  addItemComponentButtonWidthInPxs + expandButtonWidthInPxs + expandButtonoHorizontalMarginInPxs * 2;
const rowPaddingRight = 16;

export function ItemComponentsTable({
  parent,
  editItemDefinition,
  addItemComponent,
  dropdownOpenFor,
  dataTypesById,
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
  const { expandedItemComponentIds, activeItemId } = useDmnEditorStore((s) => s.dataTypesEditor);

  const expandedItemComponentIdsSet = useMemo(() => {
    return new Set(expandedItemComponentIds);
  }, [expandedItemComponentIds]);

  const dataTypes = parent.children;

  const flatTree = useMemo(() => {
    const ret: DataType[] = [];
    function traverse(d: DataType) {
      ret.push(d);
      for (let i = 0; i < (d.children?.length ?? 0); i++) {
        traverse(d.children![i]);
      }
    }

    for (let i = 0; i < (dataTypes?.length ?? 0); i++) {
      traverse(dataTypes![i]);
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
        {flatTree.map((dt, i) => {
          const nextDt = flatTree[Math.min(i + 1, flatTree.length - 1)];
          const lastDt = flatTree[Math.max(i - 1, 0)];

          const { lastIsUpper, nextIsUpper } = getNestingRelationship({ nextDt, lastDt, currentDt: dt });

          const shouldShowRow =
            dt.parentId === activeItemId ||
            (isStruct(dataTypesById.get(dt.parentId!)!.itemDefinition) &&
              [...dt.parents].every((s) => expandedItemComponentIdsSet.has(s) || s === activeItemId));

          const level = dt.parents.size - parent.parents.size - 1;

          const brigthnessPercentage =
            STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE - level * BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL;

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
                                  // FIXME: Tiago --> Expand/collapse recursively when alt is pressed.
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
                        {isStruct(dt.itemDefinition) && (
                          <Button
                            variant={ButtonVariant.link}
                            style={{ padding: "0 8px 0 0" }}
                            onClick={() => {
                              addItemComponent(dt.itemDefinition["@_id"]!, "unshift");
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
                          editMode={"hover"}
                          editItemDefinition={editItemDefinition}
                          isActive={false}
                          itemDefinition={dt.itemDefinition}
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
                          } else {
                            itemDefinition.typeRef = DmnBuiltInDataType.Any;
                            itemDefinition.itemComponent = undefined;
                          }
                        });
                      }}
                    />
                  </td>
                  <td>
                    {!isStruct(dt.itemDefinition) && (
                      <DataTypeSelector
                        name={dt.itemDefinition.typeRef}
                        onChange={(newDataType) => {
                          editItemDefinition(dt.itemDefinition["@_id"]!, (itemDefinition, items) => {
                            itemDefinition.typeRef = newDataType;
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
                        });
                      }}
                    />
                  </td>
                  <td>
                    {canHaveConstraints(dt.itemDefinition) && (
                      <Button
                        variant={ButtonVariant.link}
                        onClick={() => {
                          dmnEditorStoreApi.setState((state) => {
                            state.dataTypesEditor.activeItemId = dt.itemDefinition["@_id"]!;
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
                        editItemDefinition(dt.parentId!, (itemDefinition) => {
                          itemDefinition.itemComponent?.splice(dt.index, 1);
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
                          key={"extract-to-top-level"}
                          icon={<ArrowUpIcon />}
                          style={{ minWidth: "240px" }}
                          onClick={() => {
                            editItemDefinition(dt.itemDefinition["@_id"]!, (itemDefinition, _, __, itemDefinitions) => {
                              const newItemDefinition = reassignIds(
                                getNewItemDefinition({
                                  ...dt.itemDefinition,
                                  typeRef: undefined,
                                  "@_name": `t${dt.itemDefinition["@_name"]}`,
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
                            navigator.clipboard.writeText(JSON.stringify(dt.itemDefinition));
                          }}
                        >
                          Copy
                        </DropdownItem>,
                        <DropdownItem
                          key={"cut"}
                          icon={<CutIcon />}
                          onClick={() => {
                            navigator.clipboard.writeText(JSON.stringify(dt.itemDefinition));
                            editItemDefinition(dt.parentId!, (itemDefinition) => {
                              itemDefinition.itemComponent?.splice(dt.index, 1);
                            });
                          }}
                        >
                          Cut
                        </DropdownItem>,
                        <React.Fragment key={"copy-fragment"}>
                          {isStruct(dt.itemDefinition) && (
                            <DropdownItem
                              key={"paste"}
                              icon={<PasteIcon />}
                              onClick={() => {
                                navigator.clipboard.readText().then((t) => {
                                  const pastedItemDefinition = JSON.parse(t) as DMN15__tItemDefinition;
                                  // FIXME: Tiago --> Validate
                                  addItemComponent(dt.itemDefinition["@_id"]!, "unshift", {
                                    ...reassignIds(pastedItemDefinition, "itemComponent"),
                                    typeRef: pastedItemDefinition.typeRef ?? undefined,
                                  });
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

function getNestingRelationship({
  currentDt,
  nextDt,
  lastDt,
}: {
  nextDt: DataType;
  currentDt: DataType;
  lastDt: DataType;
}): {
  lastIsUpper: boolean;
  nextIsUpper: boolean;
} {
  // FIXME: Tiago --> This is not really good, we shouldn't have to calculate the brightness level to know the relationship...
  const brigthnessPercentage =
    STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE -
    currentDt.parents.size * BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL;

  const lastBrigthnessPercentage =
    STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE -
    lastDt.parents.size * BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL;

  const nextBrigthnessPercentage =
    STARTING_BRIGHTNESS_LEVEL_IN_PERCENTAGE -
    nextDt.parents.size * BRIGHTNESS_DECREASE_STEP_IN_PERCENTAGE_PER_NESTING_LEVEL;

  const nextIsUpper = nextBrigthnessPercentage > brigthnessPercentage;
  const lastIsUpper = lastBrigthnessPercentage > brigthnessPercentage;

  return { nextIsUpper, lastIsUpper };
}
