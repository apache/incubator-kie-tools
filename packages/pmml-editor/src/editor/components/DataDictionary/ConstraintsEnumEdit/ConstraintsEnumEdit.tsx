import * as React from "react";
import { useContext, useEffect, useRef, useState } from "react";
import { SortableContainer, SortableElement } from "react-sortable-hoc";
import {
  Button,
  ButtonVariant,
  Flex,
  FlexItem,
  Stack,
  StackItem,
  Text,
  TextContent,
  TextInput,
  TextVariants
} from "@patternfly/react-core";
import { GripVerticalIcon, TrashIcon } from "@patternfly/react-icons";
import { useValidationService } from "../../../validation";
import "./ConstraintsEnumEdit.scss";

interface ConstraintsEnumEditProps {
  dataFieldIndex: number | undefined;
  enumerations: string[];
  onAdd: () => void;
  onChange: (value: string, index: number) => void;
  onDelete: (index: number) => void;
  onSort: (oldIndex: number, newIndex: number) => void;
}

const ConstraintsEnumEdit = (props: ConstraintsEnumEditProps) => {
  const { dataFieldIndex, enumerations, onAdd, onChange, onDelete, onSort } = props;
  const [enums, setEnums] = useState(enumerations);
  const [addedEnum, setAddedEnum] = useState<number>();

  const handleChange = (value: string, index: number) => {
    onChange(value, index);
  };

  const handleDelete = (index: number) => {
    onDelete(index);
  };

  const addOne = () => {
    onAdd();
    setAddedEnum(enums.length);
  };

  const handleTab = () => {
    console.log("tab detected");
  };

  const onSortEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    onSort(oldIndex, newIndex);
  };

  useEffect(() => {
    setEnums(enumerations);
  }, [enumerations]);

  return (
    <EnumConstraintsContext.Provider
      value={{
        addedEnum: addedEnum,
        updateAddedEnum: position => {
          setAddedEnum(position);
        },
        dataFieldIndex: dataFieldIndex
      }}
    >
      <section className="constraints-enum">
        <Stack hasGutter={true}>
          <StackItem>
            <TextContent>
              <Text component={TextVariants.small}>
                Add constraints values to limit and define valid inputs for the data type.
              </Text>
            </TextContent>
          </StackItem>
          <StackItem>
            <EnumsList
              items={enums}
              onUpdate={handleChange}
              onTab={handleTab}
              onDelete={handleDelete}
              onSortEnd={onSortEnd}
              lockAxis="y"
              distance={10}
            />
          </StackItem>
          <StackItem>
            <Button variant={ButtonVariant.secondary} onClick={addOne}>
              Add another value
            </Button>
          </StackItem>
        </Stack>
      </section>
    </EnumConstraintsContext.Provider>
  );
};

export default ConstraintsEnumEdit;

const EnumsList = SortableContainer(
  ({
    items,
    onUpdate,
    onTab,
    onDelete
  }: {
    items: string[];
    onUpdate: (value: string, index: number) => void;
    onTab: () => void;
    onDelete: (index: number) => void;
  }) => {
    return (
      <ul className="constraints-enum__list" aria-label="Compact data list example">
        {items.map((item, index) => (
          <EnumItem
            key={index + item}
            enumValue={item}
            index={index}
            position={index}
            onUpdate={onUpdate}
            onTab={onTab}
            onDelete={onDelete}
            enumsCount={items.length}
            disabled={items.length === 1}
          />
        ))}
      </ul>
    );
  }
);

interface EnumItemProps {
  enumValue: string;
  enumsCount: number;
  position: number;
  onUpdate: (value: string, index: number) => void;
  onTab: () => void;
  onDelete: (index: number) => void;
}

const EnumItem = SortableElement(({ enumValue, enumsCount, position, onUpdate, onTab, onDelete }: EnumItemProps) => {
  const [enumeration, setEnumeration] = useState(enumValue);
  const { dataFieldIndex, addedEnum, updateAddedEnum } = useContext(EnumConstraintsContext);

  const handleChange = (value: string) => {
    setEnumeration(value);
  };
  const handleSave = () => {
    onUpdate(enumeration, position);
  };
  const handleDelete = () => {
    onDelete(position);
  };

  const handleTabNavigation = (event: React.KeyboardEvent) => {
    if (event.key === "Tab") {
      onTab();
    }
  };

  const { service } = useValidationService();
  const validations = useRef(service.get(`DataDictionary.DataField[${dataFieldIndex}].Value[${position}]`));
  useEffect(() => {
    validations.current = service.get(`DataDictionary.DataField[${dataFieldIndex}].Value[${position}]`);
  }, [position, enumValue]);

  const enumRef = useRef<HTMLLIElement | null>(null);
  useEffect(() => {
    if (enumRef.current && addedEnum === position) {
      const container = document.querySelector(".data-dictionary__properties-edit__form .constraints__form");
      container?.scroll({ top: container?.scrollHeight, behavior: "smooth" });
      updateAddedEnum(undefined);
    }
  }, [addedEnum, enumRef.current, position]);

  return (
    <li
      className={`constraints-enum__item ${enumsCount === 1 ? "constraints-enum__item--sort-disabled" : ""}`}
      tabIndex={20 + position}
      ref={enumRef}
    >
      <Flex>
        <FlexItem>
          <Button variant="plain" aria-label="Drag to sort" component={"span"} isDisabled={enumsCount === 1}>
            <GripVerticalIcon />
          </Button>
        </FlexItem>
        <FlexItem>
          <TextInput
            className={
              validations.current.length > 0 ? "constraints-enum__field pf-m-warning" : "constraints-enum__field"
            }
            type="text"
            id={`enum-value-${position}`}
            name={`enum-value-${position}`}
            placeholder="Please enter a value"
            value={enumeration}
            onChange={handleChange}
            onBlur={handleSave}
            onKeyDown={handleTabNavigation}
            autoComplete="off"
          />
        </FlexItem>
        <FlexItem align={{ default: "alignRight" }}>
          <Button variant={ButtonVariant.plain} onClick={handleDelete} isDisabled={enumsCount === 1}>
            <TrashIcon />
          </Button>
        </FlexItem>
      </Flex>
    </li>
  );
});

interface AddedEnumConstraints {
  addedEnum: number | undefined;
  updateAddedEnum: (position: number | undefined) => void;
  dataFieldIndex: number | undefined;
}

const EnumConstraintsContext = React.createContext<AddedEnumConstraints>({
  addedEnum: undefined,
  updateAddedEnum: () => null,
  dataFieldIndex: undefined
});
