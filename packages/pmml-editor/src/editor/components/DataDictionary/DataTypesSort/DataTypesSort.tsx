import * as React from "react";
import { useEffect, useState } from "react";
import { SortableContainer, SortableElement } from "react-sortable-hoc";
import { Button, Flex, FlexItem, Label } from "@patternfly/react-core";
import { GripVerticalIcon } from "@patternfly/react-icons";
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
    <section className="editable-item__inner">
      <Flex alignItems={{ default: "alignItemsCenter" }}>
        <FlexItem spacer={{ default: "spacerXs" }}>
          <Button variant="plain" aria-label="Drag to sort" component={"span"}>
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
