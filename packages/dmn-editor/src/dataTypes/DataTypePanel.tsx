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
import { useDmnEditorStoreApi } from "../store/Store";
import { DataTypeSelector } from "./DataTypeSelector";
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
                state.dataTypesEditor.activeItemId = p.itemDefinition["@_id"]!;
              });
            }}
          >
            {p.itemDefinition["@_name"]}
          </Button>
        ))}
      </div>
      <Flex>
        <div className={"kie-dmn-editor--data-types-title"}>
          <DataTypeName
            itemDefinition={dataType.itemDefinition}
            editItemDefinition={editItemDefinition}
            isActive={false}
            editMode={"hover"}
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
                <div>ID: {dataType.itemDefinition["@_id"]}</div>
              </DropdownItem>,
              <DropdownSeparator key={"separator-1"} />,
              <DropdownItem
                key={"remove"}
                style={{ minWidth: "240px" }}
                icon={<TrashIcon />}
                onClick={() => {
                  editItemDefinition(dataType.itemDefinition["@_id"]!, (_, items) => {
                    items?.splice(dataType.index, 1);
                  });
                  dmnEditorStoreApi.setState((state) => {
                    state.dataTypesEditor.activeItemId =
                      dataType.parentId ?? state.dmn.model.definitions.itemDefinition?.[0]?.["@_id"];
                  });
                }}
              >
                Remove
              </DropdownItem>,
            ]}
          />
        </FlexItem>
      </Flex>
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
          <DataTypeSelector name={dataType.itemDefinition.typeRef} onChange={changeTypeRef} />
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
