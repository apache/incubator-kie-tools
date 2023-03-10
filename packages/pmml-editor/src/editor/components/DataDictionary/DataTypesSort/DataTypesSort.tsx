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
import { useCallback, useEffect, useMemo, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  DragDrop,
  Draggable,
  DraggableItemPosition,
  Droppable,
} from "@patternfly/react-core/dist/js/components/DragDrop";
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

  const onSortEnd = useCallback(
    (oldIndex: number, newIndex: number) => {
      // still updating the internal state before calling the callback to avoid flickering
      const newOrder = reorder(state, oldIndex, newIndex);
      setState(newOrder);
      onReorder(oldIndex, newIndex);
    },
    [state]
  );

  useEffect(() => {
    setState(dataTypes);
  }, [dataTypes]);

  return <SortableList items={state} onSortEnd={onSortEnd} />;
};

export default DataTypesSort;

type SortableListProps = {
  items: DDDataField[];
  onSortEnd: (oldIndex: number, newIndex: number) => void;
};

const SortableList: React.FC<SortableListProps> = ({ items, onSortEnd }) => {
  const onDrop = useCallback(
    (source: DraggableItemPosition, dest?: DraggableItemPosition) => {
      if (!dest || source.index === dest.index) {
        return false;
      }

      onSortEnd(source.index, dest.index);
      return true;
    },
    [items, onSortEnd]
  );

  const helperClass = useMemo(() => {
    if (items.length >= 8) {
      return "data-type-item__sortable--sm-size";
    }
    if (items.length >= 5) {
      return "data-type-item__sortable--md-size";
    }
  }, [items]);

  return (
    <div className={`data-types-sorting ${helperClass}`}>
      <DragDrop onDrop={onDrop}>
        <Droppable>
          {items.map((item, index) => (
            <SortableItem key={`item-${item.name}`} item={item} />
          ))}
        </Droppable>
      </DragDrop>
    </div>
  );
};

type SortableItemProps = {
  item: DDDataField;
};

const SortableItem: React.FC<SortableItemProps> = ({ item }: SortableItemProps) => (
  <Draggable className="editable-item data-type-item__sortable">
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
  </Draggable>
);

const reorder = (list: DDDataField[], startIndex: number, endIndex: number) => {
  const result = [...list];
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};
