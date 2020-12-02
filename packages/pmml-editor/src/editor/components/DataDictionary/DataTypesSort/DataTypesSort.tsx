import * as React from "react";
import { useState } from "react";
import { SortableContainer, SortableElement } from "react-sortable-hoc";
import { Button, Flex, FlexItem, Label } from "@patternfly/react-core";
import { GripVerticalIcon } from "@patternfly/react-icons";
import { DataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import ConstraintsLabel from "../ConstraintsLabel/ConstraintsLabel";
import "./DataTypesSort.scss";

interface DataTypesSortProps {
  dataTypes: DataField[];
  onSort: (dataTypes: DataField[]) => void;
}

const DataTypesSort = ({ dataTypes, onSort }: DataTypesSortProps) => {
  const [state, setState] = useState<DataField[]>(dataTypes);

  const onSortEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    const newOrder = reorder(state, oldIndex, newIndex);
    setState(newOrder);
    onSort(newOrder);
  };

  const getHelperClass = () => {
    if (state.length >= 8) {
      return "data-type-item__sortable--sm-size";
    }
    if (state.length >= 5) {
      return "data-type-item__sortable--md-size";
    }
  };

  return <SortableList items={state} onSortEnd={onSortEnd} lockAxis="y" helperClass={getHelperClass()} />;
};

export default DataTypesSort;

const SortableList = SortableContainer(({ items }: { items: DataField[] }) => {
  return (
    <ul className="data-types-sorting">
      {items.map((item, index) => (
        <SortableItem key={`item-${item.name}`} index={index} item={item} />
      ))}
    </ul>
  );
});

const SortableItem = SortableElement(({ item }: { item: DataField }) => (
  <li className="editable-item data-type-item__sortable">
    <section className="editable-item__inner">
      {/*abstract data field component from DataDictionaryContainer and reuse it here*/}
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
          <Label color="blue">{item.type}</Label>
          {item.constraints !== undefined && (
            <>
              {" "}
              <ConstraintsLabel constraints={item.constraints} />
            </>
          )}
        </FlexItem>
      </Flex>
    </section>
  </li>
));

const reorder = (list: DataField[], startIndex: number, endIndex: number) => {
  const result = [...list];
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};
