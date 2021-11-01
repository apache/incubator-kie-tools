import {
  BoolField,
  DateField,
  ListField,
  NestField,
  NumField,
  RadioField,
  SelectField,
  TextField,
} from "uniforms-patternfly";
import { createAutoField } from "uniforms";

export const AutoField = createAutoField((props) => {
  if (props.allowedValues) {
    return props.checkboxes && props.fieldType !== Array ? RadioField : SelectField;
  }

  switch (props.fieldType) {
    case Array:
      return ListField;
    case Boolean:
      return BoolField;
    case Date:
      return DateField;
    case Number:
      return NumField;
    case Object:
      return NestField;
    case String:
    default:
      return TextField;
  }
});
