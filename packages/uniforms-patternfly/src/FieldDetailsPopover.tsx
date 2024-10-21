import { PopoverProps } from "@patternfly/react-core";
import { FunctionComponent } from "react";
import FieldHintPopover from "./FieldHintPopover";
import FieldDeprecatedPopover from "./FieldDeprecatedPopover";

interface FieldDetailsPopoverProps {
  default?: any;
  description?: PopoverProps["bodyContent"];
  deprecated?: boolean;
}
/**
 * Returns label description and deprecation icons with tooltip for the form or empty element if the field is not deprecated and has no description
 * @returns
 * @param props
 */
const FieldDetailsPopover: FunctionComponent<FieldDetailsPopoverProps> = (props) => {
  return (
    <>
      <FieldHintPopover default={props.default} description={props.description} />
      <FieldDeprecatedPopover deprecated={props.deprecated} />
    </>
  );
};

export default FieldDetailsPopover;
