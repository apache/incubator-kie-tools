import * as React from "react";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { useCallback, useMemo, useState } from "react";
import { useDmnEditorStore } from "../store/Store";
import { DmnBuiltInDataType, DmnDataType } from "@kie-tools/boxed-expression-component/dist/api";

export function DataTypeSelector(props: {
  name: string | undefined;
  onChange: (newDataType: DmnBuiltInDataType) => void;
}) {
  const [isOpen, setOpen] = useState(false);
  const onToggleDataTypeSelect = useCallback((isOpen: boolean) => {
    setOpen(isOpen);
  }, []);

  const { dmn } = useDmnEditorStore();

  const builtInDataTypes = useMemo<DmnDataType[]>(
    () =>
      Object.keys(DmnBuiltInDataType).map((k) => ({
        isCustom: false,
        typeRef: (DmnBuiltInDataType as any)[k],
        name: (DmnBuiltInDataType as any)[k],
      })),
    []
  );

  const customDataTypes = useMemo<DmnDataType[]>(
    () =>
      (dmn.model.definitions.itemDefinition ?? []).map((item) => ({
        isCustom: true,
        typeRef: item.typeRef!,
        name: item["@_name"]!,
      })),
    [dmn.model.definitions.itemDefinition]
  );

  const importedDataTypes = useMemo<DmnDataType[]>(() => {
    // FIXME: Tiago --> Implement this and make it generic so we don't have to duplicate the code here ando on BoxedExpressions.
    return [];
  }, []);

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
      <SelectGroup label="Custom" key="custom">
        {customDataTypes.map((dt) => (
          <SelectOption key={dt.name} value={dt.name}>
            {dt.name}
          </SelectOption>
        ))}
      </SelectGroup>
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
      )) || <React.Fragment key={"empty"}></React.Fragment>}
    </Select>
  );
}
