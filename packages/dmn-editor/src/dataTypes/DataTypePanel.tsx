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
import { DataType, DataTypesById, EditItemDefinition, AddItemComponent } from "./DataTypes";
import { DataTypeName } from "./DataTypeName";
import { ItemComponentsTable } from "./ItemComponentsTable";
import { getNewItemDefinition, isStruct } from "./DataTypeSpec";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { CopyIcon } from "@patternfly/react-icons/dist/js/icons/copy-icon";

export function DataTypePanel({
  isReadonly,
  dataType,
  dataTypesById,
  editItemDefinition,
}: {
  isReadonly: boolean;
  dataType: DataType;
  dataTypesById: DataTypesById;
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
        } else {
          itemDefinition.typeRef = DmnBuiltInDataType.Any;
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
        itemDefinition.typeRef = typeRef;
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
        itemDefinition.description = newDescription;
      });
    },
    [dataType.itemDefinition, editItemDefinition, isReadonly]
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
      if (isReadonly) {
        return;
      }

      editItemDefinition(id, (itemDefinition) => {
        const newItemDefinition = getNewItemDefinition({ "@_name": "New property", ...partial });
        itemDefinition.itemComponent ??= [];
        itemDefinition.itemComponent[how](newItemDefinition);
      });
    },
    [editItemDefinition, isReadonly]
  );

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const [dropdownOpenFor, setDropdownOpenFor] = useState<string | undefined>(undefined);
  const [topLevelDropdownOpen, setTopLevelDropdownOpen] = useState<boolean>(false);

  return (
    <PageSection>
      <div className={"kie-dmn-editor--data-type-parents"}>
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
            {p.itemDefinition["@_name"]}
          </Button>
        ))}
      </div>
      <Flex>
        {dataType.namespace !== thisDmnsNamespace && <Label>External</Label>}
        <div className={"kie-dmn-editor--data-types-title"}>
          <DataTypeName
            relativeToNamespace={thisDmnsNamespace}
            itemDefinition={dataType.itemDefinition}
            isActive={false}
            editMode={"hover"}
            isReadonly={dataType.namespace !== thisDmnsNamespace}
          />
        </div>
        <FlexItem>
          <Button variant={ButtonVariant.link}>Back</Button>
          <Button variant={ButtonVariant.link}>Forward</Button>
          <span>|</span>
          <Button variant={ButtonVariant.link}>View usages</Button>
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
                  navigator.clipboard.writeText(JSON.stringify(dataType.itemDefinition));
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
      <br />
      <TextArea
        isDisabled={isReadonly}
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
          <TypeRefSelector isDisabled={isReadonly} name={dataType.itemDefinition.typeRef} onChange={changeTypeRef} />
          <br />
          <br />
          <Title size={"md"} headingLevel="h4">
            Constraints
          </Title>
          Work in progress 🔧
          <br />
        </>
      )}
      {isStruct(dataType.itemDefinition) && (
        <ItemComponentsTable
          isReadonly={isReadonly}
          addItemComponent={addItemComponent}
          dataTypesById={dataTypesById}
          parent={dataType}
          editItemDefinition={editItemDefinition}
          dropdownOpenFor={dropdownOpenFor}
          setDropdownOpenFor={setDropdownOpenFor}
        />
      )}
    </PageSection>
  );
}
