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
import { useEffect, useState } from "react";
import { SortableContainer, SortableElement } from "react-sortable-hoc";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { GripVerticalIcon } from "@patternfly/react-icons/dist/js/icons/grip-vertical-icon";
import { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./DataTypesSort.scss";

interface DataTypesSortProps {
  dataTypes: DDDataField[];
  onReorder: (oldIndex: number, newIndex: number) => void;
}

const DataTypesSort = ({ dataTypes, onReorder }: DataTypesSortProps) => {
  const [state, setState] = useState<DDDataField[]>(dataTypes);

  const onSortEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    // still updating the internal state before calling the callback to avoid flickering
    const newOrder = reorder(state, oldIndex, newIndex);
    setState(newOrder);
    onReorder(oldIndex, newIndex);
  };

  const getHelperClass = () => {
    if (state.length >= 8) {
      return "data-type-item__sortable--sm-size";
    }
    if (state.length >= 5) {
      return "data-type-item__sortable--md-size";
    }
  };

  useEffect(() => {
    setState(dataTypes);
  }, [dataTypes]);

  return <SortableList items={state} onSortEnd={onSortEnd} lockAxis="y" helperClass={getHelperClass()} />;
};

export default DataTypesSort;

const SortableList = SortableContainer(({ items }: { items: DDDataField[] }) => {
  return (
    <ul className="data-types-sorting">
      {items.map((item, index) => (
        <SortableItem key={`item-${item.name}`} index={index} item={item} />
      ))}
    </ul>
  );
});

const SortableItem = SortableElement(({ item }: { item: DDDataField }) => (
  <li className="editable-item data-type-item__sortable">
    <section className="editable-item__inner" data-ouia-component-id={item.name}>
      <Flex alignItems={{ default: "alignItemsCenter" }}>
        <FlexItem spacer={{ default: "spacerXs" }}>
          <Button variant="plain" aria-label="Drag to sort" component={"span"} ouiaId="drag-it">
            <GripVerticalIcon />
          </Button>
        </FlexItem>
        <FlexItem>
          <strong>{item.name}</strong>
        </FlexItem>
        <FlexItem>
          <Label color="blue">{item.type}</Label> <Label color="blue">{item.optype}</Label>
        </FlexItem>
      </Flex>
    </section>
  </li>
));

const reorder = (list: DDDataField[], startIndex: number, endIndex: number) => {
  const result = [...list];
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};
