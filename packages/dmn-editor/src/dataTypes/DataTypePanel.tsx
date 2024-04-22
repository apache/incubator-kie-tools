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

import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { TypeRefSelector } from "./TypeRefSelector";
import {
  Dropdown,
  DropdownItem,
  DropdownSeparator,
  KebabToggle,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { DataType, DataTypeIndex, EditItemDefinition, AddItemComponent } from "./DataTypes";
import { DataTypeName } from "./DataTypeName";
import { ItemComponentsTable } from "./ItemComponentsTable";
import { getNewItemDefinition, isStruct } from "./DataTypeSpec";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { CopyIcon } from "@patternfly/react-icons/dist/js/icons/copy-icon";
import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { buildClipboardFromDataType } from "../clipboard/Clipboard";
import { ConstraintsFromAllowedValuesAttribute, ConstraintsFromTypeConstraintAttribute } from "./Constraints";
import { original } from "immer";
import { builtInFeelTypeNames } from "./BuiltInFeelTypes";
import { useDmnEditor } from "../DmnEditorContext";
import { useResolvedTypeRef } from "./useResolvedTypeRef";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert/Alert";

export function DataTypePanel({
  isReadonly,
  dataType,
  allDataTypesById,
  editItemDefinition,
}: {
  isReadonly: boolean;
  dataType: DataType;
  allDataTypesById: DataTypeIndex;
  editItemDefinition: EditItemDefinition;
}) {
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);

  const toggleStruct = useCallback(
    (isChecked: boolean) => {
      if (isReadonly) {
        return;
      }

      editItemDefinition(dataType.itemDefinition["@_id"]!, (itemDefinition) => {
        if (isChecked) {
          itemDefinition.typeRef = undefined;
          itemDefinition.itemComponent = [];
          itemDefinition.typeConstraint = undefined;
          itemDefinition.allowedValues = undefined;
        } else {
          itemDefinition.typeRef = { __$$text: DmnBuiltInDataType.Any };
          itemDefinition.itemComponent = undefined;
        }
      });
    },
    [dataType.itemDefinition, editItemDefinition, isReadonly]
  );

  const toggleCollection = useCallback(
    (isChecked: boolean) => {
      if (isReadonly) {
        return;
      }

      editItemDefinition(dataType.itemDefinition["@_id"]!, (itemDefinition) => {
        itemDefinition["@_isCollection"] = isChecked;
        if (isChecked === true) {
          itemDefinition.allowedValues = itemDefinition.typeConstraint
            ? {
                ...itemDefinition.typeConstraint,
              }
            : undefined;

          itemDefinition.typeConstraint = undefined;
        } else {
          itemDefinition.typeConstraint = itemDefinition.allowedValues
            ? {
                ...itemDefinition.allowedValues,
              }
            : undefined;
          itemDefinition.allowedValues = undefined;
        }
      });
    },
    [dataType.itemDefinition, editItemDefinition, isReadonly]
  );

  const changeTypeRef = useCallback(
    (typeRef: DmnBuiltInDataType) => {
      if (isReadonly) {
        return;
      }

      editItemDefinition(dataType.itemDefinition["@_id"]!, (itemDefinition) => {
        itemDefinition.typeRef = { __$$text: typeRef };
        const originalItemDefinition = original(itemDefinition);
        if (originalItemDefinition?.typeRef?.__$$text !== typeRef) {
          itemDefinition.typeConstraint = undefined;
          itemDefinition.allowedValues = undefined;
        }
      });
    },
    [dataType.itemDefinition, editItemDefinition, isReadonly]
  );

  const changeDescription = useCallback(
    (newDescription: string) => {
      if (isReadonly) {
        return;
      }

      editItemDefinition(dataType.itemDefinition["@_id"]!, (itemDefinition) => {
        itemDefinition.description = { __$$text: newDescription };
      });
    },
    [dataType.itemDefinition, editItemDefinition, isReadonly]
  );

  const parents = useMemo(() => {
    const parents: DataType[] = [];

    let cur = dataType;
    while (cur.parentId) {
      const p = allDataTypesById.get(cur.parentId)!;
      parents.unshift(p);
      cur = p;
    }

    return parents;
  }, [dataType, allDataTypesById]);

  const addItemComponent = useCallback<AddItemComponent>(
    (id, how, partial) => {
      if (isReadonly) {
        return;
      }

      editItemDefinition(id, (itemDefinition, items, index, all, state) => {
        const newItemDefinition = getNewItemDefinition(partial);
        itemDefinition.itemComponent ??= [];
        itemDefinition.itemComponent[how](newItemDefinition);
        state.focus.consumableId = newItemDefinition["@_id"];
      });
    },
    [editItemDefinition, isReadonly]
  );

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const [dropdownOpenFor, setDropdownOpenFor] = useState<string | undefined>(undefined);
  const [topLevelDropdownOpen, setTopLevelDropdownOpen] = useState<boolean>(false);
  const { externalModelsByNamespace } = useExternalModels();
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());
  const allTopLevelItemDefinitionUniqueNames = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allTopLevelItemDefinitionUniqueNames
  );

  const allUniqueNames = useMemo(
    () =>
      !dataType.parentId
        ? allTopLevelItemDefinitionUniqueNames
        : (allDataTypesById.get(dataType.parentId)!.itemDefinition.itemComponent ?? []).reduce<UniqueNameIndex>(
            (acc, s) => acc.set(s["@_name"], s["@_id"]!),
            new Map([...builtInFeelTypeNames].map((s) => [s, s]))
          ),
    [allDataTypesById, allTopLevelItemDefinitionUniqueNames, dataType.parentId]
  );

  const { dmnEditorRootElementRef } = useDmnEditor();

  const resolvedTypeRef = useResolvedTypeRef(dataType.itemDefinition.typeRef?.__$$text, dataType.namespace);

  return (
    <>
      <Flex
        className={`kie-dmn-editor--sticky-top-glass-header kie-dmn-editor--data-type-panel-header ${
          parents.length > 0 || dataType.namespace !== thisDmnsNamespace
            ? "kie-dmn-editor--data-type-panel-header-nested-or-external"
            : ""
        }`}
        justifyContent={{ default: "justifyContentSpaceBetween" }}
        direction={{ default: "row" }}
      >
        <FlexItem>
          <Flex direction={{ default: "column" }}>
            <FlexItem>
              <Flex direction={{ default: "row" }}>
                {dataType.namespace !== thisDmnsNamespace && (
                  <FlexItem>
                    <Label>External</Label>
                  </FlexItem>
                )}
                {parents.length > 0 && (
                  <FlexItem className={"kie-dmn-editor--data-type-parents"}>
                    {parents.map((p) => (
                      <Button
                        key={p.itemDefinition["@_id"]!}
                        variant={ButtonVariant.link}
                        onClick={() => {
                          dmnEditorStoreApi.setState((state) => {
                            state.dataTypesEditor.activeItemDefinitionId = p.itemDefinition["@_id"]!;
                          });
                        }}
                      >
                        {
                          buildFeelQNameFromNamespace({
                            namedElement: p.itemDefinition,
                            importsByNamespace,
                            namespace: p!.namespace,
                            relativeToNamespace: !p.parentId ? thisDmnsNamespace : p.namespace,
                          }).full
                        }
                      </Button>
                    ))}
                  </FlexItem>
                )}
              </Flex>
            </FlexItem>

            <FlexItem>
              <div className={"kie-dmn-editor--data-types-title"}>
                <DataTypeName
                  relativeToNamespace={!dataType.parentId ? thisDmnsNamespace : dataType.namespace}
                  itemDefinition={dataType.itemDefinition}
                  isActive={false}
                  editMode={"hover"}
                  isReadonly={dataType.namespace !== thisDmnsNamespace}
                  onGetAllUniqueNames={() => allUniqueNames}
                />
              </div>
            </FlexItem>
          </Flex>
        </FlexItem>
        <FlexItem>
          {/* <Button variant={ButtonVariant.link}>Back</Button>
            <Button variant={ButtonVariant.link}>Forward</Button>
            <span>|</span>
            <Button variant={ButtonVariant.link}>View usages</Button> */}
          <Dropdown
            toggle={<KebabToggle id={"toggle-kebab-top-level"} onToggle={setTopLevelDropdownOpen} />}
            onSelect={() => setTopLevelDropdownOpen(false)}
            isOpen={topLevelDropdownOpen}
            menuAppendTo={document.body}
            isPlain={true}
            position={"right"}
            dropdownItems={[
              <DropdownItem key={"id"} isDisabled={true} icon={<></>}>
                <div>
                  <b>ID: </b>
                  {dataType.itemDefinition["@_id"]}
                </div>
              </DropdownItem>,
              <DropdownSeparator key={"separator-1"} style={{ marginBottom: "8px" }} />,
              <DropdownItem
                key={"copy"}
                icon={<CopyIcon />}
                onClick={() => {
                  const clipboard = buildClipboardFromDataType(dataType, thisDmnsNamespace);
                  navigator.clipboard.writeText(JSON.stringify(clipboard));
                }}
              >
                Copy
              </DropdownItem>,
              <DropdownSeparator key="separator-2" />,
              <React.Fragment key={"remove-fragment"}>
                {!isReadonly && (
                  <DropdownItem
                    style={{ minWidth: "240px" }}
                    icon={<TrashIcon />}
                    onClick={() => {
                      if (isReadonly) {
                        return;
                      }

                      editItemDefinition(dataType.itemDefinition["@_id"]!, (_, items) => {
                        items?.splice(dataType.index, 1);
                      });
                      dmnEditorStoreApi.setState((state) => {
                        state.dataTypesEditor.activeItemDefinitionId =
                          dataType.parentId ?? state.dmn.model.definitions.itemDefinition?.[0]?.["@_id"];
                      });
                    }}
                  >
                    Remove
                  </DropdownItem>
                )}
              </React.Fragment>,
            ]}
          />
        </FlexItem>
      </Flex>
      {/* This padding was necessary because PF4 has a @media query that doesn't run inside iframes, for some reason. */}
      <PageSection style={{ padding: "24px" }}>
        <TextArea
          isDisabled={isReadonly}
          key={dataType.itemDefinition["@_id"]}
          value={dataType.itemDefinition.description?.__$$text}
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
        <Switch label={"Is struct?"} isChecked={isStruct(dataType.itemDefinition)} onChange={toggleStruct}></Switch>
        <br />
        <br />
        <Divider inset={{ default: "insetMd" }} />
        <br />
        {!isStruct(dataType.itemDefinition) && (
          <>
            <Title size={"md"} headingLevel="h4">
              Type
            </Title>
            <TypeRefSelector
              heightRef={dmnEditorRootElementRef}
              isDisabled={isReadonly}
              typeRef={resolvedTypeRef}
              onChange={changeTypeRef}
            />
            <br />
            <br />
            {dataType.itemDefinition["@_isCollection"] === true ? (
              <>
                <Title size={"md"} headingLevel="h4">
                  Collection constraint
                </Title>
                <ConstraintsFromTypeConstraintAttribute
                  isReadonly={isReadonly}
                  itemDefinition={dataType.itemDefinition}
                  editItemDefinition={editItemDefinition}
                  defaultsToAllowedValues={false}
                />
                <br />
                <br />
                <Title size={"md"} headingLevel="h4">
                  Collection item constraint
                </Title>
                <Alert variant="warning" isInline isPlain title="Deprecated">
                  <p>
                    Creating constraints for the collection items directly on the collection itself is deprecated since
                    DMN 1.5 and will possibly be removed in future versions. To prepare your DMN model for future
                    updates, please create a dedicated Data Type for the items of this list and add constraints there.
                  </p>
                </Alert>
                <br />

                <ConstraintsFromAllowedValuesAttribute
                  isReadonly={isReadonly}
                  itemDefinition={dataType.itemDefinition}
                  editItemDefinition={editItemDefinition}
                />
              </>
            ) : (
              <>
                <Title size={"md"} headingLevel="h4">
                  Constraints
                </Title>
                <ConstraintsFromTypeConstraintAttribute
                  isReadonly={isReadonly}
                  itemDefinition={dataType.itemDefinition}
                  editItemDefinition={editItemDefinition}
                  defaultsToAllowedValues={true}
                />
              </>
            )}
          </>
        )}
        {isStruct(dataType.itemDefinition) && (
          <ItemComponentsTable
            isReadonly={isReadonly}
            addItemComponent={addItemComponent}
            allDataTypesById={allDataTypesById}
            parent={dataType}
            editItemDefinition={editItemDefinition}
            dropdownOpenFor={dropdownOpenFor}
            setDropdownOpenFor={setDropdownOpenFor}
          />
        )}
      </PageSection>
    </>
  );
}
