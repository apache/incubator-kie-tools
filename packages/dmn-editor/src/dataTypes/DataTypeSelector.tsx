import * as React from "react";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { useCallback, useMemo, useState } from "react";
import { useDmnEditorStore } from "../store/Store";
import { DmnBuiltInDataType, DmnDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDataTypes } from "./Hooks";

export function DataTypeSelector(props: {
  name: string | undefined;
  onChange: (newDataType: DmnBuiltInDataType) => void;
}) {
  const [isOpen, setOpen] = useState(false);
  const onToggleDataTypeSelect = useCallback((isOpen: boolean) => {
    setOpen(isOpen);
  }, []);

  const dmn = useDmnEditorStore((s) => s.dmn);

  const { builtInDataTypes, customDataTypes, importedDataTypes } = useDataTypes(dmn.model.definitions);

  return (
    <Select
      variant={SelectVariant.typeahead}
      typeAheadAriaLabel={DmnBuiltInDataType.Undefined}
      onToggle={onToggleDataTypeSelect}
      onClear={() => props.onChange(DmnBuiltInDataType.Undefined)}
      onSelect={(e, v) => props.onChange(v as DmnBuiltInDataType)}
      selections={props.name}
      isOpen={isOpen}
      aria-labelledby={"Data types selector"}
      placeholderText={DmnBuiltInDataType.Undefined}
      isGrouped={true}
      isCreatable={true}
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
            </SelectOption>
          ))}
        </SelectGroup>
      )) || <React.Fragment key={"empty-custom"}></React.Fragment>}
      {(importedDataTypes.length > 0 && (
        <React.Fragment key="imported">
          <Divider key={"d2"} />
          <SelectGroup label="Imported">
            {importedDataTypes.map((dt) => (
              <SelectOption key={dt.name} value={dt.name}>
                {dt.name}
              </SelectOption>
            ))}
          </SelectGroup>
        </React.Fragment>
      )) || <React.Fragment key={"empty-imported"}></React.Fragment>}
    </Select>
  );
}
