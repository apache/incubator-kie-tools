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

import * as React from "react";
import { useMemo, useState, useCallback, useRef } from "react";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Draggable, DragAndDrop, DraggableReorderFunction, useDraggableItemContext } from "../draggable/Draggable";
import TimesIcon from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ConstraintComponentProps, TypeHelper } from "./Constraints";

export const ENUM_SEPARATOR = ",";

export function ConstraintsEnum({
  isReadonly,
  value,
  expressionValue,
  type,
  typeHelper,
  onSave,
  isDisabled,
  renderOnPropertiesPanel,
}: ConstraintComponentProps) {
  const enumValues = useMemo(() => isEnum(value, typeHelper.check) ?? [""], [typeHelper.check, value]);
  const [valuesUuid, setValuesUuid] = useState((enumValues ?? [""])?.map((_) => generateUuid()));
  const isItemValid = useMemo(
    () => enumValues.map((value, i, array) => array.filter((e) => e === value).length <= 1),
    [enumValues]
  );
  const [focusOwner, setFocusOwner] = useState("");

  const onAdd = useCallback(() => {
    setValuesUuid((prev) => {
      if (prev[enumValues.length] === undefined) {
        const newValuesUuid = [...prev];
        newValuesUuid[enumValues.length] = generateUuid();
        return newValuesUuid;
      }
      return prev;
    });
    onSave(enumValues.join(`${ENUM_SEPARATOR} `) + ENUM_SEPARATOR);
    setFocusOwner("");
  }, [onSave, enumValues]);

  const onRemove = useCallback(
    (index: number) => {
      const newValues = [...enumValues];
      newValues.splice(index, 1);

      setValuesUuid((prev) => {
        const newUuids = [...prev];
        newUuids.splice(index, 1);
        return newUuids;
      });
      onSave(newValues.join(`${ENUM_SEPARATOR} `));
    },
    [enumValues, onSave]
  );

  const onDragEnd = useCallback(
    (source: number, dest: number) => {
      const reordened = [...enumValues];
      const [removed] = reordened.splice(source, 1);
      reordened.splice(dest, 0, removed);
      onSave(reordened.join(`${ENUM_SEPARATOR} `));
    },
    [enumValues, onSave]
  );

  const reorder: DraggableReorderFunction = useCallback((source: number, dest: number) => {
    setValuesUuid((prev) => {
      const reordenedUuid = [...prev];
      const [removedUuid] = reordenedUuid.splice(source, 1);
      reordenedUuid.splice(dest, 0, removedUuid);

      return reordenedUuid;
    });
  }, []);

  const onChangeItem = useCallback(
    (newValue, index) => {
      const newValues = [...enumValues];
      newValues[index] = typeHelper.transform(newValue);
      onSave(newValues.join(`${ENUM_SEPARATOR} `));
    },
    [enumValues, onSave, typeHelper]
  );

  const draggableItem = useCallback(
    (value, index) => {
      return (
        <Draggable
          key={valuesUuid[index]}
          index={index}
          style={{ alignItems: "center" }}
          handlerStyle={{ margin: "0px 10px" }}
          isDisabled={isReadonly || isDisabled}
        >
          <li style={{ marginLeft: "20px", listStyleType: "initial" }}>
            <EnumElement
              id={`enum-element-${index}`}
              isDisabled={isReadonly || isDisabled}
              initialValue={typeHelper.recover(value) ?? ""}
              onChange={(newValue) => onChangeItem(newValue, index)}
              onRemove={() => onRemove(index)}
              isValid={isItemValid[index]}
              focusOwner={focusOwner}
              setFocusOwner={setFocusOwner}
              typeHelper={typeHelper}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  onAdd();
                }
                if (e.key === ENUM_SEPARATOR) {
                  e.preventDefault();
                }
              }}
            />
          </li>
        </Draggable>
      );
    },
    [focusOwner, isDisabled, isItemValid, isReadonly, onAdd, onChangeItem, onRemove, typeHelper, valuesUuid]
  );

  return (
    <div>
      <div>
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            gap: "10px",
            border: "solid 1px lightgray",
            borderRadius: "4px",
          }}
        >
          <ul>
            <DragAndDrop
              reorder={reorder}
              onDragEnd={onDragEnd}
              values={enumValues}
              draggableItem={draggableItem}
              isDisabled={isDisabled || isReadonly}
            />
          </ul>
        </div>
      </div>
      {!(isDisabled || isReadonly) && (
        <>
          <Button
            title={"Add enum value"}
            onClick={() => onAdd()}
            variant={ButtonVariant.link}
            icon={<PlusCircleIcon />}
            style={{ paddingTop: "10px", paddingBottom: 0, paddingLeft: 0, paddingRight: 0 }}
          >
            Add value
          </Button>
        </>
      )}
      {!renderOnPropertiesPanel && (
        <>
          <br />
          <br />
          <ConstraintsExpression isReadonly={true} value={expressionValue ?? ""} type={type} />
        </>
      )}
    </div>
  );
}

function EnumElement({
  id,
  isDisabled,
  initialValue,
  isValid,
  typeHelper,
  focusOwner,
  setFocusOwner,
  onChange,
  onRemove,
  onKeyDown,
}: {
  id: string;
  isDisabled: boolean;
  initialValue: string;
  isValid: boolean;
  typeHelper: TypeHelper;
  focusOwner: string;
  setFocusOwner: React.SetStateAction<React.Dispatch<string>>;
  onChange: (newValue: string) => void;
  onRemove: () => void;
  onKeyDown?: (e: React.KeyboardEvent<HTMLElement>) => void;
}) {
  const value = useMemo<string>(() => initialValue, [initialValue]);
  const removeButtonRef = useRef(null);
  const { hovered } = useDraggableItemContext();

  return (
    <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between" }}>
      {typeHelper.component({
        autoFocus: true,
        onChange: (newValue: string) => onChange(newValue),
        id,
        isDisabled,
        style: {
          borderColor: "transparent",
          backgroundColor: "transparent",
          outline: "none",
        },
        value,
        focusOwner,
        setFocusOwner,
        isValid,
        onKeyDown,
      })}

      <Button
        title={"Remove enum value"}
        ref={removeButtonRef}
        style={{ opacity: hovered ? "100%" : "0" }}
        className={"kie-dmn-editor--documentation-link--row-remove"}
        variant={"plain"}
        icon={<TimesIcon />}
        onClick={() => onRemove()}
      />
      {hovered && <Tooltip content={"Remove"} reference={removeButtonRef} />}
    </div>
  );
}

export function isEnum(value?: string, typeCheck?: (value: string) => boolean): string[] | undefined {
  if (value === undefined) {
    return undefined;
  }

  if (value === "") {
    return undefined;
  }

  const enumValues = value.split(ENUM_SEPARATOR).map((e) => e.trim());
  if (enumValues.reduce((isEnum, value) => isEnum && typeCheck?.(value), true)) {
    return enumValues;
  }

  return undefined;
}
