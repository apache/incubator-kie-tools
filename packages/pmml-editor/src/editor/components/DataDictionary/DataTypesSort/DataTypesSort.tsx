import * as React from "react";
import { useState } from "react";
import { SortableContainer, SortableElement } from "react-sortable-hoc";
import { Button, Flex, FlexItem, Label } from "@patternfly/react-core";
import { GripVerticalIcon } from "@patternfly/react-icons";
import { DataType } from "../DataDictionaryContainer/DataDictionaryContainer";
import ConstraintsLabel from "../ConstraintsLabel/ConstraintsLabel";
import "./DataTypesSort.scss";

interface DataTypesSortProps {
  dataTypes: DataType[];
  onSort: (dataTypes: DataType[]) => void;
}

const DataTypesSort = ({ dataTypes, onSort }: DataTypesSortProps) => {
  const [state, setState] = useState<DataType[]>(dataTypes);

  const onSortEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    const newOrder = reorder(state, oldIndex, newIndex);
    setState(newOrder);
    onSort(newOrder);
  };

  return <SortableList items={state} onSortEnd={onSortEnd} lockAxis="y" />;
};

export default DataTypesSort;

const SortableList = SortableContainer(({ items }: { items: DataType[] }) => {
  return (
    <ul className="data-types-sorting">
      {items.map((item, index) => (
        <SortableItem key={`item-${item.name}`} index={index} item={item} />
      ))}
    </ul>
  );
});

const SortableItem = SortableElement(({ item }: { item: DataType }) => (
  <li className="data-type-item data-type-item__sortable">
    <Flex alignItems={{ default: "alignItemsCenter" }} style={{ height: "100%" }}>
      <FlexItem spacer={{ default: "spacerXs" }}>
        <Button variant="plain" aria-label="Drag to sort" component={"span"}>
          <GripVerticalIcon />
        </Button>
      </FlexItem>
      <FlexItem>
        <strong>{item.name}</strong>
      </FlexItem>
      <FlexItem>
        <Label color="blue">{item.type}</Label>
        {item.list && (
          <>
            {" "}
            <Label color="cyan">List</Label>
          </>
        )}
        {item.constraints !== undefined && (
          <>
            {" "}
            <ConstraintsLabel constraints={item.constraints} />
          </>
        )}
      </FlexItem>
    </Flex>
  </li>
));

const reorder = (list: DataType[], startIndex: number, endIndex: number) => {
  const result = [...list];
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};
