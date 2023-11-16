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
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
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
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { KIE_DMN_UNKNOWN_NAMESPACE, UniqueNameIndex } from "../Dmn15Spec";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { buildClipboardFromDataType } from "../clipboard/Clipboard";
import { Constraints } from "./Constraints";
import { original } from "immer";
import { builtInFeelTypeNames } from "./BuiltInFeelTypes";
import { useDmnEditor } from "../DmnEditorContext";
import { buildFeelQName, parseFeelQName } from "../feel/parseFeelQName";

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
  const thisDmn = useDmnEditorStore((s) => s.dmn);
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
        itemDefinition.typeConstraint = undefined;
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

      editItemDefinition(id, (itemDefinition) => {
        const newItemDefinition = getNewItemDefinition({
          ...partial,
          "@_name": "New property",
          typeRef: { __$$text: DmnBuiltInDataType.Undefined },
        });
        itemDefinition.itemComponent ??= [];
        itemDefinition.itemComponent[how](newItemDefinition);
      });
    },
    [editItemDefinition, isReadonly]
  );

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const [dropdownOpenFor, setDropdownOpenFor] = useState<string | undefined>(undefined);
  const [topLevelDropdownOpen, setTopLevelDropdownOpen] = useState<boolean>(false);

  const { importsByNamespace, allTopLevelItemDefinitionUniqueNames, allTopLevelDataTypesByFeelName } =
    useDmnEditorDerivedStore();

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

  const resolveTypeRef = useCallback(
    (typeRef: string | undefined) => {
      if (!typeRef) {
        return typeRef;
      }

      // Built-in types are not relative.
      if (builtInFeelTypeNames.has(typeRef)) {
        return typeRef;
      }

      const parsedTypeRefFeelQName = parseFeelQName(typeRef);

      // The absense of an `importName` from this FEEL QName means that the namespace is the same as `dataType.namespace`.
      // If there is an `importName`, we need to try and resolve it with thiDmn's imported models. If it fails, the namespace is `KIE_DMN_UNKNOWN_NAMESPACE`.
      const namespace =
        (parsedTypeRefFeelQName.importName
          ? thisDmn.model.definitions[`@_xmlns:${parsedTypeRefFeelQName.importName}`]
          : dataType.namespace) ?? KIE_DMN_UNKNOWN_NAMESPACE;

      // If it's a local data type, it's not relative.
      if (namespace === thisDmnsNamespace) {
        return typeRef;
      }

      const typeRefQName = buildFeelQName({
        type: "feel-qname",
        importName: importsByNamespace.get(namespace)?.["@_name"] ?? "?", // If the namespace is known to `thisDmn`, we'll have an importName, otherwise, we use `?`.
        localPart: parsedTypeRefFeelQName.localPart,
      });

      return allTopLevelDataTypesByFeelName.get(typeRefQName)?.feelName ?? typeRefQName;
    },
    [
      allTopLevelDataTypesByFeelName,
      dataType.namespace,
      importsByNamespace,
      thisDmn.model.definitions,
      thisDmnsNamespace,
    ]
  );

  const resolvedTypeRef = resolveTypeRef(dataType.itemDefinition.typeRef?.__$$text);

  return (
    <>
      <Flex
        className={"kie-dmn-editor--sticky-top-glass-header kie-dmn-editor--data-type-panel-header"}
        justifyContent={{ default: "justifyContentSpaceBetween" }}
        alignItems={{ default: "alignItemsCenter" }}
        direction={{ default: "row" }}
      >
        <FlexItem>
          <Flex direction={{ default: "row" }}>
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
            <FlexItem>{dataType.namespace !== thisDmnsNamespace && <Label>External</Label>}</FlexItem>
            <FlexItem>
              <div className={"kie-dmn-editor--data-types-title"}>
                <DataTypeName
                  relativeToNamespace={!dataType.parentId ? thisDmnsNamespace : dataType.namespace}
                  itemDefinition={dataType.itemDefinition}
                  isActive={false}
                  editMode={"hover"}
                  isReadonly={dataType.namespace !== thisDmnsNamespace}
                  allUniqueNames={allUniqueNames}
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
            <Title size={"md"} headingLevel="h4">
              Constraints
            </Title>
            <Constraints
              isReadonly={isReadonly}
              itemDefinition={dataType.itemDefinition}
              editItemDefinition={editItemDefinition}
            />
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
            resolveTypeRef={resolveTypeRef}
          />
        )}
      </PageSection>
    </>
  );
}
