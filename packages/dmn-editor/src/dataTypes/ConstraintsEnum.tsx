import * as React from "react";
import { useMemo, useState, useCallback, useRef, useEffect } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Draggable, DraggableContextProvider } from "../propertiesPanel/Draggable";
import TimesIcon from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { invalidInlineFeelNameStyle } from "../feel/InlineFeelNameInput";

export const ENUM_SEPARATOR = ",";

export function ConstraintsEnum({
  isReadonly,
  inputType,
  value,
  type,
  typeParser,
  onChange,
  isDisabled,
}: {
  isReadonly: boolean;
  inputType: "text" | "number";
  value?: string;
  type: DmnBuiltInDataType;
  typeParser: (value: string) => any;
  onChange: (newValue: string | undefined) => void;
  isDisabled: boolean;
}) {
  const enumValues = useMemo(() => value?.split(ENUM_SEPARATOR)?.map((e) => e.trim()) ?? [], [value]);
  const [addNew, setAddNew] = useState<boolean>(() => ((enumValues ?? []).length === 0 ? true : false));
  const actualEnumValues = useRef([...enumValues]);
  const [valuesUuid, setValuesUuid] = useState((actualEnumValues.current ?? [])?.map((_) => generateUuid()));

  const isEnumerationValid = useCallback(() => {
    return new Set(actualEnumValues.current).size === actualEnumValues.current.length;
  }, []);

  const onInternalChange = useCallback(
    (newValue: string) => {
      if (newValue === "") {
        return;
      }
      onChange(actualEnumValues.current.join(`${ENUM_SEPARATOR} `));
    },
    [onChange]
  );

  const onAddNew = useCallback(() => {
    setAddNew(true);
    setValuesUuid((prev) => {
      if (prev[actualEnumValues.current.length] === undefined) {
        const newValuesUuid = [...prev];
        newValuesUuid[actualEnumValues.current.length] = generateUuid();
        return newValuesUuid;
      }
      return prev;
    });
  }, []);

  const onRemove = useCallback(
    (index: number) => {
      actualEnumValues.current.splice(index, 1);
      if (actualEnumValues.current.length === 0) {
        setAddNew(true);
      }

      setValuesUuid((prev) => {
        const newUuids = [...prev];
        newUuids.splice(index, 1);
        return newUuids;
      });

      if (isEnumerationValid()) {
        onChange(actualEnumValues.current.join(`${ENUM_SEPARATOR} `));
      }
    },
    [isEnumerationValid, onChange]
  );

  const onDragEnd = useCallback(
    (source: number, dest: number) => {
      const reordened = [...(actualEnumValues.current ?? [])];
      const [removed] = reordened.splice(source, 1);
      reordened.splice(dest, 0, removed);
      onChange(reordened.join(`${ENUM_SEPARATOR} `));
    },
    [onChange]
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
              {actualEnumValues.current?.map((value, index) => (
                <Draggable
                  key={valuesUuid[index]}
                  index={index}
                  style={{ alignItems: "center" }}
                  handlerStyle={{ margin: "0px 10px" }}
                >
                  {(hovered) => {
                    return (
                      <li style={{ marginLeft: "20px", listStyleType: "initial" }}>
                        <EnumElement
                          id={`enum-element-${index}`}
                          isDisabled={isReadonly || isDisabled}
                          inputType={inputType}
                          initialValue={value}
                          onChange={(newValue) => {
                            actualEnumValues.current[index] = newValue;
                            if (isEnumerationValid()) {
                              onInternalChange(newValue);
                            } else {
                              onChange("");
                            }
                          }}
                          hovered={hovered}
                          onRemove={() => onRemove(index)}
                          isValid={isEnumerationValid()}
                        />
                      </li>
                    );
                  }}
                </Draggable>
              ))}

              {addNew && (
                <div style={{ display: "flex", flexDirection: "row", alignItems: "center" }}>
                  <span style={{ width: "38px", height: "18px " }}>&nbsp;</span>
                  <li style={{ marginLeft: "20px", flexGrow: 1, listStyleType: "initial" }}>
                    <EnumElement
                      id={`enum-element-${actualEnumValues.current.length}`}
                      isDisabled={isReadonly || isDisabled}
                      inputType={inputType}
                      initialValue={""}
                      onChange={(newValue) => {
                        setAddNew(false);
                        actualEnumValues.current[actualEnumValues.current.length] = newValue;
                        setValuesUuid((prev) => {
                          if (prev[actualEnumValues.current.length - 1] === undefined) {
                            const newValuesUuid = [...prev];
                            newValuesUuid[actualEnumValues.current.length - 1] = generateUuid();
                            return newValuesUuid;
                          }
                          return prev;
                        });
                        if (isEnumerationValid()) {
                          onInternalChange(newValue);
                        } else {
                          onChange("");
                        }
                      }}
                      hovered={true}
                      onRemove={() => setAddNew(false)}
                      isValid={true}
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
      <ConstraintsExpression isReadonly={true} value={value ?? ""} type={type} />
    </div>
  );
}

function EnumElement({
  id,
  inputType,
  isDisabled,
  initialValue,
  hovered,
  isValid,
  onChange,
  onRemove,
}: {
  id: string;
  inputType: "text" | "number";
  isDisabled: boolean;
  initialValue: string;
  hovered: boolean;
  isValid: boolean;
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
        style={{
          borderColor: "transparent",
          backgroundColor: "transparent",
          outline: "none",
          ...(isValid ? {} : invalidInlineFeelNameStyle),
        }}
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
