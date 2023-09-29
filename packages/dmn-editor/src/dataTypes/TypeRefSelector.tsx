import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { TypeRefLabel } from "./TypeRefLabel";
import { ArrowUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-up-icon";
import { DmnEditorTab, useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { DataType } from "./DataTypes";
import { builtInFeelTypes } from "./BuiltInFeelTypes";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";

export type OnTypeRefChange = (newDataType: DmnBuiltInDataType) => void;

export function TypeRefSelector(props: {
  isDisabled?: boolean;
  typeRef: string | undefined;
  onChange: OnTypeRefChange;
  menuAppendTo?: "parent";
}) {
  const [isOpen, setOpen] = useState(false);

  const { allTopLevelDataTypesByFeelName } = useDmnEditorDerivedStore();

  const selectedDataType = useMemo(() => {
    return props.typeRef ? allTopLevelDataTypesByFeelName.get(props.typeRef) : undefined;
  }, [allTopLevelDataTypesByFeelName, props.typeRef]);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);

  const { customDataTypes, externalDataTypes } = useMemo(() => {
    const customDataTypes: DataType[] = [];
    const externalDataTypes: DataType[] = [];

    [...allTopLevelDataTypesByFeelName.values()].forEach((s) => {
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
  }, [allTopLevelDataTypesByFeelName, thisDmnsNamespace]);

  return (
    <Flex
      justifyContent={{ default: "justifyContentFlexStart" }}
      flexWrap={{ default: "nowrap" }}
      spaceItems={{ default: "spaceItemsNone" }}
    >
      {selectedDataType?.itemDefinition && (
        <Tooltip content="Jump to definition">
          <Button
            variant={ButtonVariant.control}
            onClick={(e) =>
              dmnEditorStoreApi.setState((state) => {
                state.navigation.tab = DmnEditorTab.DATA_TYPES;
                state.dataTypesEditor.activeItemDefinitionId = selectedDataType?.itemDefinition?.["@_id"];
              })
            }
          >
            <ArrowUpIcon />
          </Button>
        </Tooltip>
      )}
      <Select
        style={{ flexGrow: 1 }}
        isDisabled={props.isDisabled}
        variant={SelectVariant.typeahead}
        typeAheadAriaLabel={DmnBuiltInDataType.Undefined}
        onToggle={setOpen}
        onSelect={(e, v) => {
          setOpen(false);
          props.onChange(v as DmnBuiltInDataType);
        }}
        selections={props.typeRef}
        isOpen={isOpen}
        aria-labelledby={"Data types selector"}
        placeholderText={"Select a data type..."}
        isGrouped={true}
        menuAppendTo={props.menuAppendTo}

        // isCreatable={true} // FIXME: Tiago --> Maybe this is a good idea?
      >
        <SelectGroup label="Built-in" key="builtin">
          {builtInFeelTypes.map((dt) => (
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
                <TypeRefLabel
                  typeRef={dt.itemDefinition.typeRef}
                  relativeToNamespace={dt.namespace}
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
                <TypeRefLabel
                  typeRef={dt.itemDefinition.typeRef}
                  relativeToNamespace={dt.namespace}
                  isCollection={dt.itemDefinition?.["@_isCollection"]}
                />
              </SelectOption>
            ))}
          </SelectGroup>
        )) || <React.Fragment key={"empty-external"}></React.Fragment>}
      </Select>
    </Flex>
  );
}
