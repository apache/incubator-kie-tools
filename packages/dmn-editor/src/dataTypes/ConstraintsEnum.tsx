import * as React from "react";
import { useMemo, useState, useCallback, useRef } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Draggable, DraggableContextProvider } from "../propertiesPanel/Draggable";
import TimesIcon from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";

export const ENUM_SEPARATOR = ",";

export function ConstraintsEnum({
  isReadonly,
  inputType,
  value,
  onChange,
  isDisabled,
}: {
  isReadonly: boolean;
  inputType: "text" | "number";
  value?: string;
  onChange: (newValue: string | undefined) => void;
  isDisabled: boolean;
}) {
  const enumValues = useMemo(() => value?.split(ENUM_SEPARATOR)?.map((e) => e.trim()) ?? [], [value]);
  const [valuesUuid, setValuesUuid] = useState((enumValues ?? [])?.map((_) => generateUuid()));
  const [addNew, setAddNew] = useState<boolean>(() => ((enumValues ?? []).length === 0 ? true : false));

  const onInternalChange = useCallback(
    (newValue: string, index: number) => {
      if (newValue === "") {
        return;
      }

      setValuesUuid((prev) => {
        if (prev[index] === undefined) {
          const newValuesUuid = [...prev];
          newValuesUuid[index] = generateUuid();
          return newValuesUuid;
        }
        return prev;
      });

      const newEnumValues = [...(enumValues ?? [])];
      newEnumValues[index] = newValue;
      onChange(newEnumValues.join(`${ENUM_SEPARATOR} `));
    },
    [enumValues, onChange]
  );

  const onAddNew = useCallback(() => {
    setAddNew(true);
  }, []);

  const onRemove = useCallback(
    (index: number) => {
      const newValues = [...(enumValues ?? [])];
      newValues.splice(index, 1);

      setValuesUuid((prev) => {
        const newUuids = [...prev];
        newUuids.splice(index, 1);
        return newUuids;
      });

      onChange(newValues.join(`${ENUM_SEPARATOR} `));
    },
    [enumValues, onChange]
  );

  const onDragEnd = useCallback(
    (source: number, dest: number) => {
      const reordened = [...(enumValues ?? [])];
      const [removed] = reordened.splice(source, 1);
      reordened.splice(dest, 0, removed);
      onChange(reordened.join(`${ENUM_SEPARATOR} `));
    },
    [enumValues, onChange]
  );

  const reorder = useCallback((source: number, dest: number) => {
    setValuesUuid((prev) => {
      const reordenedUuid = [...prev];
      const [removedUuid] = reordenedUuid.splice(source, 1);
      reordenedUuid.splice(dest, 0, removedUuid);
      return reordenedUuid;
    });
  }, []);

  return (
    <div>
      <p style={{ paddingTop: "10px" }}>
        The enumeration constraint creates an expression that will limit the value to be equal to one of the given
        values.
      </p>
      <br />
      <div>
        <DraggableContextProvider reorder={reorder} onDragEnd={onDragEnd}>
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
              {enumValues?.map((value, index) => (
                <Draggable
                  key={valuesUuid[index]}
                  index={index}
                  style={{ alignItems: "center" }}
                  handlerStyle={{ margin: "0px 10px" }}
                >
                  {(hovered) => (
                    <li style={{ marginLeft: "20px", listStyleType: "initial" }}>
                      <EnumElement
                        id={`enum-element-${index}`}
                        isDisabled={isReadonly || isDisabled}
                        inputType={inputType}
                        initialValue={value}
                        onChange={(newValue) => onInternalChange(newValue, index)}
                        hovered={hovered}
                        onRemove={() => onRemove(index)}
                      />
                    </li>
                  )}
                </Draggable>
              ))}

              {addNew && (
                <div style={{ display: "flex", flexDirection: "row", alignItems: "center" }}>
                  <span style={{ width: "38px", height: "18px " }}>&nbsp;</span>
                  <li style={{ marginLeft: "20px", flexGrow: 1, listStyleType: "initial" }}>
                    <EnumElement
                      id={`enum-element-${enumValues.length}`}
                      isDisabled={isReadonly || isDisabled}
                      inputType={inputType}
                      initialValue={""}
                      onChange={(newValue) => {
                        setAddNew(false);
                        onInternalChange(newValue, enumValues.length);
                      }}
                      hovered={true}
                      onRemove={() => setAddNew(false)}
                    />
                  </li>
                </div>
              )}
            </ul>
          </div>
        </DraggableContextProvider>
      </div>
      <Button
        onClick={() => onAddNew()}
        variant={ButtonVariant.link}
        icon={<PlusCircleIcon />}
        style={{ paddingTop: "10px", paddingBottom: 0, paddingLeft: 0, paddingRight: 0 }}
      >
        Add new value
      </Button>
      <br />
      <br />
      <ConstraintsExpression isReadonly={true} value={value ?? ""} />
    </div>
  );
}

function EnumElement({
  id,
  inputType,
  isDisabled,
  initialValue,
  hovered,
  onChange,
  onRemove,
}: {
  id: string;
  inputType: "text" | "number";
  isDisabled: boolean;
  initialValue: string;
  hovered: boolean;
  onChange: (newValue: string) => void;
  onRemove: () => void;
}) {
  const [value, setValue] = useState<string>(initialValue);
  const removeButtonRef = useRef(null);

  const onInternalChange = useCallback(
    (newValue: string) => {
      setValue(newValue);
      if (newValue !== "") {
        onChange(newValue);
      }
    },
    [onChange]
  );

  return (
    <div style={{ display: "flex", flexDirection: "row", flexGrow: 1 }}>
      <TextInput
        id={id}
        style={{ borderColor: "transparent", backgroundColor: "transparent", outline: "none" }}
        autoFocus={true}
        type={inputType}
        value={value.trim()}
        onChange={onInternalChange}
        isDisabled={isDisabled}
      />
      <Button
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

export function isEnum(value: string, typeCheck: (e: string) => boolean): string[] | undefined {
  const enumValues = value.split(ENUM_SEPARATOR).map((e) => e.trim());

  if (enumValues.reduce((isEnum, value) => isEnum && typeCheck(value), true)) {
    return enumValues;
  }

  return undefined;
}
