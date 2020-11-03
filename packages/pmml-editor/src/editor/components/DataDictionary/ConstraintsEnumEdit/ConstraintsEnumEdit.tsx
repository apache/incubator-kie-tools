import * as React from "react";
import { useEffect, useState } from "react";
import { SortableContainer, SortableElement } from "react-sortable-hoc";
import {
  Alert,
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
import { EnumConstraint } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./ConstraintsEnumEdit.scss";
import { FormValidation } from "../ConstraintsEdit/ConstraintsEdit";

interface ConstraintsEnumEditProps {
  enumerations: EnumConstraint[];
  onAdd: () => void;
  onChange: (value: string, index: number) => void;
  onDelete: (index: number) => void;
  onSort: (oldIndex: number, newIndex: number) => void;
  validation: FormValidation;
}

const ConstraintsEnumEdit = ({
  enumerations,
  onAdd,
  onChange,
  onDelete,
  onSort,
  validation
}: ConstraintsEnumEditProps) => {
  const [enums, setEnums] = useState(enumerations);
  const handleChange = (value: string, index: number) => {
    onChange(value, index);
  };

  const handleDelete = (index: number) => {
    onDelete(index);
  };

  const addOne = () => {
    onAdd();
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
    <section className="constraints-enum">
      <Stack hasGutter={true}>
        {validation.form === "error" && (
          <StackItem>
            <Alert variant="danger" isInline={true} title="Please enter at least one enumeration." />
          </StackItem>
        )}
        <StackItem>
          <TextContent>
            <Text component={TextVariants.p}>
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
    items: EnumConstraint[];
    onUpdate: (value: string, index: number) => void;
    onTab: () => void;
    onDelete: (index: number) => void;
  }) => {
    return (
      <ul className="constraints-enum__list" aria-label="Compact data list example" style={{ width: 550 }}>
        {items.map((item, index) => (
          <EnumItem
            key={`enum-${item.id}`}
            enumValue={item.value}
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

  return (
    <li
      className={`constraints-enum__item ${enumsCount === 1 ? "constraints-enum__item--sort-disabled" : ""}`}
      tabIndex={20 + position}
    >
      <Flex>
        <FlexItem>
          <Button variant="plain" aria-label="Drag to sort" component={"span"} isDisabled={enumsCount === 1}>
            <GripVerticalIcon />
          </Button>
        </FlexItem>
        <FlexItem>
          <TextInput
            type="text"
            id={`enum-value-${position}`}
            name={`enum-value-${position}`}
            value={enumeration}
            onChange={handleChange}
            onBlur={handleSave}
            onKeyDown={handleTabNavigation}
            placeholder="Insert a value"
            style={{ width: 300 }}
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
