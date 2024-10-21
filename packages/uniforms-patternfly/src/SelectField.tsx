import { connectField } from "uniforms";
import SelectCheckboxField from "./SelectCheckboxField";
import { SelectCheckboxProps, SelectInputProps } from "./SelectField.types";
import SelectInputsField from "./SelectInputsField";

export type SelectFieldProps = SelectCheckboxProps | SelectInputProps;

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
