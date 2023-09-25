import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { DataTypeLabel } from "./DataTypeLabel";
import { ArrowUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-up-icon";
import { DmnEditorTab, useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { useDataTypes as useBuiltinDataTypes } from "./Hooks";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { DataType } from "./DataTypes";

export function TypeRefSelector(props: {
  isDisabled?: boolean;
  name: string | undefined;
  onChange: (newDataType: DmnBuiltInDataType) => void;
}) {
  const [isOpen, setOpen] = useState(false);
  const onToggleDataTypeSelect = useCallback((isOpen: boolean) => {
    setOpen(isOpen);
  }, []);

  const { builtInDataTypes } = useBuiltinDataTypes();

  const { dataTypesByFeelName } = useDmnEditorDerivedStore();

  const selectedDt = useMemo(() => {
    return props.name ? dataTypesByFeelName.get(props.name) : undefined;
  }, [dataTypesByFeelName, props.name]);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);

  const { customDataTypes, externalDataTypes } = useMemo(() => {
    const customDataTypes: DataType[] = [];
    const externalDataTypes: DataType[] = [];

    [...dataTypesByFeelName.values()].forEach((s) => {
      if (s.parentId) {
        return; // Not top-level.
      }

      if (s.namespace === thisDmnsNamespace) {
        customDataTypes.push(s);
      } else {
        externalDataTypes.push(s);
      }
    });

    return { customDataTypes, externalDataTypes };
  }, [dataTypesByFeelName, thisDmnsNamespace]);

  return (
    <InputGroup>
      {selectedDt?.itemDefinition && (
        <Tooltip content="Jump to definition">
          <Button
            variant={ButtonVariant.control}
            onClick={(e) =>
              dmnEditorStoreApi.setState((state) => {
                state.navigation.tab = DmnEditorTab.DATA_TYPES;
                state.dataTypesEditor.activeItemDefinitionId = selectedDt?.itemDefinition?.["@_id"];
              })
            }
          >
            <ArrowUpIcon />
          </Button>
        </Tooltip>
      )}
      <Select
        isDisabled={props.isDisabled}
        variant={SelectVariant.typeahead}
        typeAheadAriaLabel={DmnBuiltInDataType.Undefined}
        onToggle={onToggleDataTypeSelect}
        onClear={() => {
          setOpen(false);
          props.onChange(DmnBuiltInDataType.Undefined);
        }}
        onSelect={(e, v) => {
          setOpen(false);
          props.onChange(v as DmnBuiltInDataType);
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
              <SelectOption key={dt.feelName} value={dt.feelName}>
                {dt.feelName}
                <DataTypeLabel
                  typeRef={dt.itemDefinition.typeRef}
                  namespace={dt.namespace}
                  isCollection={dt.itemDefinition?.["@_isCollection"]}
                />
              </SelectOption>
            ))}
          </SelectGroup>
        )) || <React.Fragment key={"empty-custom"}></React.Fragment>}
        {(externalDataTypes.length > 0 && (
          <SelectGroup label="External">
            {externalDataTypes.map((dt) => (
              <SelectOption key={dt.feelName} value={dt.feelName}>
                {dt.feelName}
                <DataTypeLabel
                  typeRef={dt.itemDefinition.typeRef}
                  namespace={dt.namespace}
                  isCollection={dt.itemDefinition?.["@_isCollection"]}
                />
              </SelectOption>
            ))}
          </SelectGroup>
        )) || <React.Fragment key={"empty-external"}></React.Fragment>}
      </Select>
    </InputGroup>
  );
}
