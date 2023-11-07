import * as React from "react";
import { useMemo, useState, useCallback, useRef } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Draggable, DraggableContextProvider } from "../propertiesPanel/Draggable";
import TimesIcon from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

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
  const [addNew, setAddNew] = useState<boolean>(false);
  const enumValues = useMemo(() => value?.split(ENUM_SEPARATOR)?.map((e) => e.trim()) ?? [], [value]);

  const onInternalChange = useCallback(
    (newValue: string, index: number) => {
      if (newValue === "") {
        return;
      }
      const newEnumValues = [...(enumValues ?? [])];
      newEnumValues[index] = newValue;
      onChange(newEnumValues.join(`${ENUM_SEPARATOR} `));
    },
    [enumValues, onChange]
  );

  const onAddNew = useCallback(() => {
    setAddNew(true);
  }, []);

  const reorder = useCallback((source: number, dest: number) => {
    // should reorder
  }, []);

  return (
    <>
      <>
        <DraggableContextProvider reorder={reorder}>
          <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
            <ol>
              {enumValues?.map((value, index) => (
                <Draggable key={index} index={index} style={{ alignItems: "center" }}>
                  {(hovered) => (
                    <li style={{ marginLeft: "10px" }}>
                      <EnumElement
                        id={`enum-element-${index}`}
                        isDisabled={isReadonly || isDisabled}
                        inputType={inputType}
                        initialValue={value}
                        onChange={(newValue) => onInternalChange(newValue, index)}
                        hovered={hovered}
                        onRemove={() => {}}
                      />
                    </li>
                  )}
                </Draggable>
              ))}

              {addNew && (
                <div style={{ display: "flex", flexDirection: "row", alignItems: "center" }}>
                  <span style={{ width: "40px", height: "18px " }}>&nbsp;</span>
                  <li style={{ marginLeft: "10px", flexGrow: 1 }}>
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
            </ol>
          </div>
        </DraggableContextProvider>
      </>
      <Button onClick={() => onAddNew()} variant={ButtonVariant.link} icon={<PlusCircleIcon />}>
        Add new
      </Button>
      <br />
      <ConstraintsExpression isReadonly={true} value={value ?? ""} />
    </>
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
        style={{ borderColor: "transparent", backgroundColor: "transparent" }}
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
