import * as React from "react";
import { SelectDirection, SelectProps } from "@patternfly/react-core/dist/js/components/Select";
import { connectField, FieldProps } from "uniforms";
import SelectCheckboxField from "./SelectCheckboxField";
import SelectInputsField from "./SelectInputsField";
import { CheckboxProps } from "@patternfly/react-core/dist/js/components/Checkbox";
import { RadioProps } from "@patternfly/react-core/dist/js/components/Radio";

export type SelectCheckboxProps = FieldProps<
  string | string[],
  CheckboxProps | RadioProps,
  {
    onChange: (value?: string | string[]) => void;
    transform?: (value?: string) => string;
    allowedValues: string[];
    id?: string;
    fieldType?: typeof Array;
    disabled?: boolean;
  }
>;

export type SelectInputProps = FieldProps<
  string | string[],
  SelectProps,
  {
    checkboxes?: boolean;
    required?: boolean;
    fieldType?: typeof Array;
    onChange: (value?: string | string[] | number | number[]) => void;
    placeholder?: string;
    allowedValues?: (string | number)[];
    disabled?: boolean;
    error?: boolean;
    transform?: (value?: string | number) => string | number;
    direction?: SelectDirection;
    menuAppendTo?: HTMLElement;
  }
>;

type SelectFieldProps = SelectCheckboxProps | SelectInputProps;

function isSelectCheckboxProps(toBeDetermined: SelectFieldProps): toBeDetermined is SelectCheckboxProps {
  return (toBeDetermined as SelectInputProps).checkboxes === true;
}

function SelectField(props: SelectFieldProps) {
  if (isSelectCheckboxProps(props)) {
    return <SelectCheckboxField {...props} />;
  }
  return <SelectInputsField {...props} />;
}

export default connectField<SelectFieldProps>(SelectField);
