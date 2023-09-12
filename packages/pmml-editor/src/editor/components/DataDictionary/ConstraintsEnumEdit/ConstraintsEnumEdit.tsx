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

import * as React from "react";
import { useContext, useEffect, useRef, useState } from "react";
import { SortableContainer, SortableElement } from "react-sortable-hoc";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
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
              onSortEnd={onSortEnd}
              lockAxis="y"
              distance={10}
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

const EnumsList = SortableContainer(
  ({
    items,
    onUpdate,
    onTab,
    onDelete,
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

  const { validationRegistry } = useValidationRegistry();
  const validations = useRef(
    validationRegistry.get(Builder().forDataDictionary().forDataField(dataFieldIndex).forValue(position).build())
  );
  useEffect(() => {
    validations.current = validationRegistry.get(
      Builder().forDataDictionary().forDataField(dataFieldIndex).forValue(position).build()
    );
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
  dataFieldIndex: undefined,
});
