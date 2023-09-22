import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import * as React from "react";
import { useCallback, useState } from "react";
import { DataTypeLabel } from "./DataTypeLabel";
import { useDataTypes } from "./Hooks";

export function DataTypeSelector(props: {
  name: string | undefined;
  onChange: (newDataType: DmnBuiltInDataType) => void;
}) {
  const [isOpen, setOpen] = useState(false);
  const onToggleDataTypeSelect = useCallback((isOpen: boolean) => {
    setOpen(isOpen);
  }, []);

  const { builtInDataTypes, customDataTypes, externalDataTypes } = useDataTypes();

  return (
    <Select
      variant={SelectVariant.typeahead}
      typeAheadAriaLabel={DmnBuiltInDataType.Undefined}
      onToggle={onToggleDataTypeSelect}
      onClear={() => {
        setOpen(false);
        return props.onChange(DmnBuiltInDataType.Undefined);
      }}
      onSelect={(e, v) => {
        setOpen(false);
        return props.onChange(v as DmnBuiltInDataType);
      }}
      selections={props.name}
      isOpen={isOpen}
      aria-labelledby={"Data types selector"}
      placeholderText={"Select a data type..."}
      isGrouped={true}
      menuAppendTo={document.body}
      // isCreatable={true} // FIXME: Tiago --> Maybe this is a good idea?
    >
      <SelectGroup label="Built-in" key="builtin">
        {builtInDataTypes.map((dt) => (
          <SelectOption key={dt.name} value={dt.name}>
            {dt.name}
          </SelectOption>
        ))}
      </SelectGroup>
      <Divider key={"d1"} />
      {(customDataTypes.length > 0 && (
        <SelectGroup label="Custom" key="custom">
          {customDataTypes.map((dt) => (
            <SelectOption key={dt.name} value={dt.name}>
              {dt.name}
              {dt.typeRef && (
                <DataTypeLabel typeRef={dt.typeRef} namespace={dt.namespace} isCollection={dt.isCollection} />
              )}
            </SelectOption>
          ))}
        </SelectGroup>
      )) || <React.Fragment key={"empty-custom"}></React.Fragment>}
      {(externalDataTypes.length > 0 && (
        <SelectGroup label="External">
          {externalDataTypes.map((dt) => (
            <SelectOption key={dt.name} value={dt.name}>
              {dt.name}
              {dt.typeRef && (
                <DataTypeLabel typeRef={dt.typeRef} namespace={dt.namespace} isCollection={dt.isCollection} />
              )}
            </SelectOption>
          ))}
        </SelectGroup>
      )) || <React.Fragment key={"empty-external"}></React.Fragment>}
    </Select>
  );
}
