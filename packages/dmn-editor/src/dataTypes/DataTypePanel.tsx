import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import PasteIcon from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useDmnEditorStoreApi } from "../store/Store";
import { DataTypeSelector } from "./DataTypeSelector";
import { Dropdown, DropdownItem, KebabToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import { DataType, DataTypesById, EditItemDefinition, AddItemComponent } from "./DataTypes";
import { DataTypeName } from "./DataTypeName";
import { ItemComponentsTable } from "./ItemComponentsTable";
import { getNewItemDefinition, isStruct, reassignIds } from "./DataTypeSpec";

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
                            addItemComponent(dataType.itemDefinition["@_id"]!, "unshift", {
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
