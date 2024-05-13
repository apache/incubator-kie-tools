/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import * as React from "react";
import { useCallback, useRef, useState } from "react";
import { TypeRefLabel } from "./TypeRefLabel";
import { ArrowUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-up-icon";
import { DmnEditorTab } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { DataType } from "./DataTypes";
import { builtInFeelTypeNames, builtInFeelTypes } from "./BuiltInFeelTypes";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useInViewSelect } from "../responsiveness/useInViewSelect";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";

export type OnTypeRefChange = (newDataType: string | undefined) => void;
export type OnCreateDataType = (newDataTypeName: string) => void;
export type OnToggle = (isExpanded: boolean) => void;

export function TypeRefSelector({
  zoom,
  heightRef,
  onChange,
  typeRef,
  isDisabled,
  menuAppendTo,
  onCreate,
  onToggle,
  removeDataTypes,
}: {
  zoom?: number;
  heightRef: React.RefObject<HTMLElement>;
  isDisabled?: boolean;
  typeRef: string | undefined;
  onChange: OnTypeRefChange;
  onCreate?: OnCreateDataType;
  onToggle?: OnToggle;
  menuAppendTo?: "parent";
  removeDataTypes?: DataType[];
}) {
  const [isOpen, setOpen] = useState(false);
  const { externalModelsByNamespace } = useExternalModels();
  const selectedDataType = useDmnEditorStore((s) =>
    typeRef
      ? s.computed(s).getDataTypes(externalModelsByNamespace).allTopLevelDataTypesByFeelName.get(typeRef)
      : undefined
  );

  const _onToggle = useCallback(
    (isExpanded: boolean) => {
      onToggle?.(isExpanded);
      setOpen(isExpanded);
    },
    [onToggle]
  );

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const { customDataTypes, externalDataTypes } = useDmnEditorStore((state) => {
    const customDataTypes: DataType[] = [];
    const externalDataTypes: DataType[] = [];

    [...state.computed(state).getDataTypes(externalModelsByNamespace).allTopLevelDataTypesByFeelName.values()].forEach(
      (s) => {
        if (s.parentId) {
          return; // Not top-level.
        }

        if (s.namespace === state.dmn.model.definitions["@_namespace"]) {
          if ((removeDataTypes ?? []).findIndex((removeDataType) => removeDataType.feelName === s.feelName) < 0) {
            customDataTypes.push(s);
          }
        } else {
          externalDataTypes.push(s);
        }
      }
    );

    return { customDataTypes, externalDataTypes };
  });

  const exists = selectedDataType || (typeRef && builtInFeelTypeNames.has(typeRef));

  const id = generateUuid();

  const toggleRef = useRef<HTMLButtonElement>(null);

  const { maxHeight, direction } = useInViewSelect(heightRef ?? { current: document.body }, toggleRef, zoom ?? 1);

  return (
    <Flex
      id={id}
      justifyContent={{ default: "justifyContentFlexStart" }}
      flexWrap={{ default: "nowrap" }}
      spaceItems={{ default: "spaceItemsNone" }}
    >
      {selectedDataType?.itemDefinition && (
        <Button
          title={"Jump to definition"}
          className={"kie-dmn-editor--data-type-jump-to-definition"}
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
      )}
      <Select
        toggleRef={toggleRef}
        className={!exists && typeRef ? "kie-dmn-editor--type-ref-selector-invalid-value" : undefined}
        isDisabled={isDisabled}
        variant={SelectVariant.typeahead}
        typeAheadAriaLabel={DmnBuiltInDataType.Undefined}
        onToggle={_onToggle}
        onSelect={(e, v) => {
          _onToggle(false);
          onChange(v === DmnBuiltInDataType.Undefined ? undefined : (v as string));
        }}
        selections={typeRef ?? DmnBuiltInDataType.Undefined}
        isOpen={isOpen}
        aria-labelledby={"Data types selector"}
        placeholderText={"Select a data type..."}
        isCreatable={!!onCreate}
        isCreateOptionOnTop={false}
        onCreateOption={onCreate}
        isGrouped={true}
        menuAppendTo={menuAppendTo ?? document.body}
        maxHeight={maxHeight}
        direction={direction}
        onWheelCapture={(e) => e.stopPropagation()} // Necessary so that Reactflow doesn't mess with this event.
      >
        <SelectGroup label="Built-in" key="builtin" style={{ minWidth: "300px" }}>
          {builtInFeelTypes.map((dt) => (
            <SelectOption key={dt.name} value={dt.name}>
              {dt.name}
            </SelectOption>
          ))}
        </SelectGroup>
        <SelectGroup label="Custom" key="custom" style={{ minWidth: "300px" }}>
          {(customDataTypes.length > 0 &&
            customDataTypes.map((dt) => (
              <SelectOption key={dt.feelName} value={dt.feelName}>
                {dt.feelName}
                &nbsp;
                <TypeRefLabel
                  typeRef={dt.itemDefinition.typeRef?.__$$text}
                  relativeToNamespace={dt.namespace}
                  isCollection={dt.itemDefinition?.["@_isCollection"]}
                />
              </SelectOption>
            ))) || <SelectOption key={"None"} value={"None"} isDisabled={true} />}
        </SelectGroup>
        <SelectGroup label="External" key="external" style={{ minWidth: "300px" }}>
          {(externalDataTypes.length > 0 &&
            externalDataTypes.map((dt) => (
              <SelectOption key={dt.feelName} value={dt.feelName}>
                {dt.feelName}
                &nbsp;
                <TypeRefLabel
                  typeRef={dt.itemDefinition.typeRef?.__$$text}
                  relativeToNamespace={dt.namespace}
                  isCollection={dt.itemDefinition?.["@_isCollection"]}
                />
              </SelectOption>
            ))) || <SelectOption key={"None"} value={"None"} isDisabled={true} />}
        </SelectGroup>
      </Select>
    </Flex>
  );
}
