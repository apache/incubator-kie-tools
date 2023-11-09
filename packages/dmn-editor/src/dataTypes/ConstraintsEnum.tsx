import * as React from "react";
import { useMemo, useState, useCallback, useRef } from "react";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Draggable, DraggableContextProvider } from "../propertiesPanel/Draggable";
import TimesIcon from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { TypeHelper } from "./Constraints";

export const ENUM_SEPARATOR = ",";

export function ConstraintsEnum({
  isReadonly,
  value,
  savedValue,
  type,
  typeHelper,
  onChange,
  isDisabled,
  setConstraintValidity,
}: {
  isReadonly: boolean;
  value?: string;
  savedValue?: string;
  type: DmnBuiltInDataType;
  typeHelper: TypeHelper;
  onChange: (newValue: string | undefined) => void;
  isDisabled: boolean;
  setConstraintValidity: (isValid: boolean) => void;
}) {
  const enumValues = useMemo(() => value?.split(ENUM_SEPARATOR)?.map((e) => e.trim()) ?? [], [value]);
  const [addNew, setAddNew] = useState<boolean>(() => ((enumValues ?? []).length === 0 ? true : false));
  const actualEnumValues = useRef([...enumValues]);
  const [valuesUuid, setValuesUuid] = useState((actualEnumValues.current ?? [])?.map((_) => generateUuid()));
  const [isItemValid, setItemValid] = useState<boolean[]>(() => {
    return actualEnumValues.current.map((value, i, array) => {
      return array.filter((e) => e === value).length <= 1;
    });
  });
  const [focusOwner, setFocusOwner] = useState("");

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
    setItemValid((prev) => {
      const newIsItemValid = [...prev];
      newIsItemValid[actualEnumValues.current.length] = true;
      return newIsItemValid;
    });
    setFocusOwner("");
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

      onChange(actualEnumValues.current.join(`${ENUM_SEPARATOR} `));
      setConstraintValidity(isEnumerationValid());
    },
    [isEnumerationValid, onChange, setConstraintValidity]
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

  const onChangeNew = useCallback(
    (newValue: string) => {
      setAddNew(false);
      actualEnumValues.current[actualEnumValues.current.length] = typeHelper.transform(newValue);
      setValuesUuid((prev) => {
        if (prev[actualEnumValues.current.length - 1] === undefined) {
          const newValuesUuid = [...prev];
          newValuesUuid[actualEnumValues.current.length - 1] = generateUuid();
          return newValuesUuid;
        }
        return prev;
      });
      onInternalChange(newValue);
      setConstraintValidity(isEnumerationValid());
      setItemValid((prev) => {
        const newIsItemValid = [...prev];
        newIsItemValid[actualEnumValues.current.length - 1] =
          actualEnumValues.current.filter((e) => e === actualEnumValues.current[actualEnumValues.current.length - 1])
            .length <= 1;
        return newIsItemValid;
      });
    },
    [isEnumerationValid, onInternalChange, setConstraintValidity, typeHelper]
  );

  const onChangeItem = useCallback(
    (newValue, index) => {
      actualEnumValues.current[index] = typeHelper.transform(newValue);
      onInternalChange(newValue);
      setConstraintValidity(isEnumerationValid());

      setItemValid((prev) => {
        const newIsItemValid = [...prev];
        newIsItemValid[index] =
          actualEnumValues.current.filter((e) => e === actualEnumValues.current[index]).length <= 1;
        return newIsItemValid;
      });
    },
    [isEnumerationValid, onInternalChange, setConstraintValidity, typeHelper]
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
                          initialValue={typeHelper.recover(value ?? "")}
                          onChange={(newValue) => onChangeItem(newValue, index)}
                          hovered={hovered}
                          onRemove={() => onRemove(index)}
                          isValid={isItemValid[index]}
                          focusOwner={focusOwner}
                          setFocusOwner={setFocusOwner}
                          typeHelper={typeHelper}
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
                      initialValue={""}
                      onChange={onChangeNew}
                      hovered={true}
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
        onClick={() => onAddNew()}
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
  hovered,
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
  hovered: boolean;
  isValid: boolean;
  typeHelper: TypeHelper;
  focusOwner: string;
  setFocusOwner: React.SetStateAction<React.Dispatch<string>>;
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
    <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between" }}>
      {typeHelper.component({
        autoFocus: true,
        onChange: onInternalChange,
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

export function isEnum(value: string, typeCheck: (e: string) => boolean): string[] | undefined {
  const enumValues = value.split(ENUM_SEPARATOR).map((e) => e.trim());

  if (enumValues.reduce((isEnum, value) => isEnum && typeCheck(value), true)) {
    return enumValues;
  }

  return undefined;
}
