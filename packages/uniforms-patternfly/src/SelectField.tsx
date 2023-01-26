import * as React from "react";
import { SelectDirection, SelectProps } from "@patternfly/react-core/dist/js/components/Select";
import { connectField, FieldProps } from "uniforms";
import SelectCheckboxField from "./SelectCheckboxField";
import SelectInputsField from "./SelectInputsField";
import { CheckboxProps } from "@patternfly/react-core/dist/js/components/Checkbox";
import { RadioProps } from "@patternfly/react-core/dist/js/components/Radio";

export type CheckboxesProps = FieldProps<
  string | string[],
  CheckboxProps | RadioProps,
  {
    checkboxes: true;
    fieldType?: typeof Array | any;
    onChange: (value?: string | string[]) => void;
    transform?: (value?: string) => string;
    allowedValues: string[];
    id?: string;
    disabled?: boolean;
  }
>;

export type SelectInputProps = FieldProps<
  string | string[],
  SelectProps,
  {
    checkboxes: false;
    required?: boolean;
    fieldType?: typeof Array | any;
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

type SelectFieldProps = CheckboxesProps | SelectInputProps;

function SelectField(props: SelectFieldProps) {
  if (props.checkboxes) {
    return <SelectCheckboxField data-testid={"select-checkbox-field"} {...props} />;
  }
  return <SelectInputsField data-testid={"select-input-field"} {...props} />;
}

export default connectField<SelectFieldProps>(SelectField as any);
