import * as React from "react";
import { useMemo, useState, useCallback, useRef } from "react";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Draggable, DraggableContextProvider, useDraggableItemContext } from "../propertiesPanel/Draggable";
import TimesIcon from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ConstraintComponentProps, TypeHelper } from "./Constraints";

export const ENUM_SEPARATOR = ",";

export function ConstraintsEnum({
  isReadonly,
  value,
  savedValue,
  type,
  typeHelper,
  onSave,
  isDisabled,
}: ConstraintComponentProps) {
  const enumValues = useMemo(() => isEnum(value, typeHelper.check) ?? [], [typeHelper.check, value]);
  const [addNew, setAddNew] = useState<boolean>(() => (enumValues.length === 0 ? true : false));
  const [valuesUuid, setValuesUuid] = useState((enumValues ?? [])?.map((_) => generateUuid()));
  const [isItemValid, setItemValid] = useState<boolean[]>(
    enumValues.map((value, i, array) => array.filter((e) => e === value).length <= 1)
  );
  const [focusOwner, setFocusOwner] = useState("");

  const isEnumerationValid = useCallback((enumValues: string[]) => {
    return new Set(enumValues).size === enumValues.length;
  }, []);

  const onAdd = useCallback(() => {
    setAddNew(true);
    setValuesUuid((prev) => {
      if (prev[enumValues.length] === undefined) {
        const newValuesUuid = [...prev];
        newValuesUuid[enumValues.length] = generateUuid();
        return newValuesUuid;
      }
      return prev;
    });
    setItemValid((prev) => {
      if (prev[enumValues.length] === undefined) {
        const newIsItemValid = [...prev];
        newIsItemValid[enumValues.length] = true;
        return newIsItemValid;
      }
      return prev;
    });
    setFocusOwner("");
  }, [enumValues.length]);

  const onRemove = useCallback(
    (index: number) => {
      const newValues = [...enumValues];
      newValues.splice(index, 1);
      if (newValues.length === 0) {
        setAddNew(true);
      }

      setValuesUuid((prev) => {
        const newUuids = [...prev];
        newUuids.splice(index, 1);
        return newUuids;
      });
      onSave({ value: newValues.join(`${ENUM_SEPARATOR} `), isValid: isEnumerationValid(newValues) });
    },
    [enumValues, isEnumerationValid, onSave]
  );

  const onDragEnd = useCallback(
    (source: number, dest: number) => {
      const reordened = [...enumValues];
      const [removed] = reordened.splice(source, 1);
      reordened.splice(dest, 0, removed);
      onSave({ value: reordened.join(`${ENUM_SEPARATOR} `), isValid: isEnumerationValid(reordened) });
    },
    [enumValues, isEnumerationValid, onSave]
  );

  const reorder = useCallback((source: number, dest: number) => {
    setValuesUuid((prev) => {
      const reordenedUuid = [...prev];
      const [removedUuid] = reordenedUuid.splice(source, 1);
      reordenedUuid.splice(dest, 0, removedUuid);
      return reordenedUuid;
    });
  }, []);

  const onChangeNew = useCallback(
    (newValue: string) => {
      setAddNew(false);
      const newValues = [...enumValues];
      newValues[newValues.length] = typeHelper.transform(newValue);

      setValuesUuid((prev) => {
        if (prev[newValues.length - 1] === undefined) {
          const newValuesUuid = [...prev];
          newValuesUuid[newValues.length - 1] = generateUuid();
          return newValuesUuid;
        }
        return prev;
      });
      setItemValid(newValues.map((value, i, array) => array.filter((e) => e === value).length <= 1));
      onSave({ value: newValues.join(`${ENUM_SEPARATOR} `), isValid: isEnumerationValid(newValues) });
    },
    [enumValues, isEnumerationValid, onSave, typeHelper]
  );

  const onChangeItem = useCallback(
    (newValue, index) => {
      const newValues = [...enumValues];
      newValues[index] = typeHelper.transform(newValue);
      setItemValid(newValues.map((value, i, array) => array.filter((e) => e === value).length <= 1));
      onSave({ value: newValues.join(`${ENUM_SEPARATOR} `), isValid: isEnumerationValid(newValues) });
    },
    [enumValues, isEnumerationValid, onSave, typeHelper]
  );

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
                  <li style={{ marginLeft: "20px", listStyleType: "initial" }}>
                    <EnumElement
                      id={`enum-element-${index}`}
                      isDisabled={isReadonly || isDisabled}
                      initialValue={typeHelper.recover(value ?? "")}
                      onChange={(newValue) => onChangeItem(newValue, index)}
                      onRemove={() => onRemove(index)}
                      isValid={isItemValid[index]}
                      focusOwner={focusOwner}
                      setFocusOwner={setFocusOwner}
                      typeHelper={typeHelper}
                    />
                  </li>
                </Draggable>
              ))}
              {(addNew || enumValues.length === 0) && (
                <div style={{ display: "flex", flexDirection: "row", alignItems: "center" }}>
                  <span style={{ width: "38px", height: "18px " }}>&nbsp;</span>
                  <li style={{ marginLeft: "20px", flexGrow: 1, listStyleType: "initial" }}>
                    <EnumElement
                      id={`enum-element-${enumValues.length}`}
                      isDisabled={isReadonly || isDisabled}
                      initialValue={""}
                      onChange={onChangeNew}
                      onRemove={() => setAddNew(false)}
                      isValid={true}
                      focusOwner={focusOwner}
                      setFocusOwner={setFocusOwner}
                      typeHelper={typeHelper}
                    />
                  </li>
                </div>
              )}
            </ul>
          </div>
        </DraggableContextProvider>
      </div>
      <Button
        onClick={() => onAdd()}
        variant={ButtonVariant.link}
        icon={<PlusCircleIcon />}
        style={{ paddingTop: "10px", paddingBottom: 0, paddingLeft: 0, paddingRight: 0 }}
      >
        Add new value
      </Button>
      <br />
      <br />
      <ConstraintsExpression isReadonly={true} value={savedValue ?? ""} type={type} />
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
        value: value.trim(),
        focusOwner,
        setFocusOwner,
        isValid,
      })}

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
