/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useContext, useEffect, useRef, useState } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  DragDrop,
  Draggable,
  DraggableItemPosition,
  Droppable,
} from "@patternfly/react-core/dist/js/components/DragDrop";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { GripVerticalIcon } from "@patternfly/react-icons/dist/js/icons/grip-vertical-icon";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import "./ConstraintsEnumEdit.scss";

interface ConstraintsEnumEditProps {
  dataFieldIndex: number | undefined;
  enumerations: string[];
  onAdd: () => void;
  onChange: (value: string, index: number) => void;
  onDelete: (index: number) => void;
  onSort: (oldIndex: number, newIndex: number) => void;
}

const ConstraintsEnumEdit: React.FC<ConstraintsEnumEditProps> = ({
  dataFieldIndex,
  enumerations,
  onAdd,
  onChange,
  onDelete,
  onSort,
}) => {
  const [enums, setEnums] = useState(enumerations);
  const [addedEnum, setAddedEnum] = useState<number>();

  const handleChange = useCallback(
    (value: string, index: number) => {
      onChange(value, index);
    },
    [enums]
  );

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

  useEffect(() => {
    setEnums(enumerations);
  }, [enumerations]);

  return (
    <EnumConstraintsContext.Provider
      value={{
        addedEnum: addedEnum,
        updateAddedEnum: (position) => {
          setAddedEnum(position);
        },
        dataFieldIndex: dataFieldIndex,
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
              onSortEnd={onSort}
            />
          </StackItem>
          <StackItem>
            <Button variant={ButtonVariant.secondary} onClick={addOne} ouiaId="add-another-value">
              Add another value
            </Button>
          </StackItem>
        </Stack>
      </section>
    </EnumConstraintsContext.Provider>
  );
};

export default ConstraintsEnumEdit;

type EnumsListProps = {
  items: string[];
  onUpdate: (value: string, index: number) => void;
  onTab: () => void;
  onDelete: (index: number) => void;
  onSortEnd: (oldIndex: number, newIndex: number) => void;
};

const EnumsList: React.FC<EnumsListProps> = ({ items, onUpdate, onTab, onDelete, onSortEnd }) => {
  const onDrop = useCallback(
    (source: DraggableItemPosition, dest?: DraggableItemPosition) => {
      if (!dest || source.index === dest.index) {
        return false;
      }

      onSortEnd(source.index, dest.index);
      return true;
    },
    [items]
  );

  return (
    <div className="constraints-enum__list">
      <DragDrop onDrop={onDrop}>
        <Droppable>
          {items.map((item, index) => (
            <Draggable key={`${index}_${item}`}>
              <EnumItem
                enumValue={item}
                position={index}
                onUpdate={onUpdate}
                onTab={onTab}
                onDelete={onDelete}
                enumsCount={items.length}
              />
            </Draggable>
          ))}
        </Droppable>
      </DragDrop>
    </div>
  );
};

interface EnumItemProps {
  enumValue: string;
  enumsCount: number;
  position: number;
  onUpdate: (value: string, index: number) => void;
  onTab: () => void;
  onDelete: (index: number) => void;
}

const EnumItem: React.FC<EnumItemProps> = ({ enumValue, enumsCount, position, onUpdate, onTab, onDelete }) => {
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

  const { validationRegistry } = useValidationRegistry();
  const validations = useRef(
    validationRegistry.get(Builder().forDataDictionary().forDataField(dataFieldIndex).forValue(position).build())
  );
  useEffect(() => {
    validations.current = validationRegistry.get(
      Builder().forDataDictionary().forDataField(dataFieldIndex).forValue(position).build()
    );
  }, [position, enumValue]);

  const enumRef = useRef<HTMLDivElement | null>(null);
  useEffect(() => {
    if (enumRef.current && addedEnum === position) {
      const container = document.querySelector(".data-dictionary__properties-edit__form .constraints__form");
      container?.scroll({ top: container?.scrollHeight, behavior: "smooth" });
      updateAddedEnum(undefined);
    }
  }, [addedEnum, enumRef.current, position]);

  return (
    <div
      className={`constraints-enum__item ${enumsCount === 1 ? "constraints-enum__item--sort-disabled" : ""}`}
      ref={enumRef}
      data-ouia-component-id={`val-${position}`}
    >
      <Flex>
        <FlexItem>
          <Button
            variant="plain"
            aria-label="Drag to sort"
            component={"span"}
            isDisabled={enumsCount === 1}
            ouiaId="drag-it"
          >
            <GripVerticalIcon />
          </Button>
        </FlexItem>
        <FlexItem>
          <TextInput
            className="constraints-enum__field"
            type="text"
            id={`enum-value-${position}`}
            name={`enum-value-${position}`}
            placeholder="Please enter a value"
            value={enumeration}
            onChange={handleChange}
            onBlur={handleSave}
            onKeyDown={handleTabNavigation}
            autoComplete="off"
            validated={validations.current.length > 0 ? "warning" : "default"}
            data-ouia-component-type="value-name"
          />
        </FlexItem>
        <FlexItem align={{ default: "alignRight" }}>
          <Button
            variant={ButtonVariant.plain}
            onClick={handleDelete}
            isDisabled={enumsCount === 1}
            ouiaId="delete-item"
          >
            <TrashIcon />
          </Button>
        </FlexItem>
      </Flex>
    </div>
  );
};

interface AddedEnumConstraints {
  addedEnum: number | undefined;
  updateAddedEnum: (position: number | undefined) => void;
  dataFieldIndex: number | undefined;
}

const EnumConstraintsContext = React.createContext<AddedEnumConstraints>({
  addedEnum: undefined,
  updateAddedEnum: () => null,
  dataFieldIndex: undefined,
});
