import * as React from "react";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { useCallback, useState } from "react";

export function DataTypeSelector(props: { typeRef: string | undefined }) {
  const [isOpen, setOpen] = useState(false);
  const onToggleDataTypeSelect = useCallback((isOpen: boolean) => {
    setOpen(isOpen);
  }, []);

  const setDataType = useCallback((dataType: string) => {
    console.log(`TIAGO WRITE: Set data type --> ${dataType}`);
  }, []);

  return (
    <Select
      variant={SelectVariant.typeahead}
      typeAheadAriaLabel="<Undefined>"
      onToggle={onToggleDataTypeSelect}
      onClear={() => setDataType("")}
      onSelect={(e, v) => setDataType(v as string)}
      selections={props.typeRef}
      isOpen={isOpen}
      aria-labelledby={"Data types selector"}
      placeholderText="<Undefined>"
      isGrouped={true}
      isCreatable={true}
    >
      <SelectGroup label="Built-in" key="built-in">
        <SelectOption key={"Any"} value="Any" />
      </SelectGroup>
      <Divider key="divider" />
      <SelectGroup label="Custom" key="custom">
        <SelectOption key={"tPerson"} value="tPerson" />
      </SelectGroup>
    </Select>
  );
}
