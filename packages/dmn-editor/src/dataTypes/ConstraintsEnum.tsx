import * as React from "react";
import { useMemo, useState, useCallback, useRef } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import PlusCircleIcon from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Draggable, DraggableContextProvider } from "../propertiesPanel/Draggable";
import TimesIcon from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { List, ListComponent, ListItem, OrderType } from "@patternfly/react-core/dist/js/components/List";

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
  onChange: (newValue: string | undefined, origin: KIE__tConstraintType) => void;
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
      onChange(newEnumValues.join(`${ENUM_SEPARATOR} `), "enumeration");
    },
    [enumValues, onChange]
  );

  const onAddNew = useCallback(() => {
    setAddNew(true);
  }, []);

  const reorder = useCallback((source: number, dest: number) => {
    // const reordened = [...(values ?? [])];
    // const [removed] = reordened.splice(source, 1);
    // reordened.splice(dest, 0, removed);
    // setExpandedUrls((prev) => {
    //   const newUrlExpanded = [...prev];
    //   const [removed] = newUrlExpanded.splice(source, 1);
    //   newUrlExpanded.splice(dest, 0, removed);
    //   return newUrlExpanded;
    // });
    // setValuesUuid((prev) => {
    //   const reordenedUuid = [...prev];
    //   const [removedUuid] = reordenedUuid.splice(source, 1);
    //   reordenedUuid.splice(dest, 0, removedUuid);
    //   return reordenedUuid;
    // });
    // onInternalChange(reordened);
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
                        isDisabled={isDisabled}
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
                  <li style={{ marginLeft: "10px" }}>
                    <EnumElement
                      isDisabled={isDisabled}
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
  inputType,
  isDisabled,
  initialValue,
  hovered,
  onChange,
  onRemove,
}: {
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
