import { Button, Popover, PopoverProps, Text, TextVariants } from "@patternfly/react-core";
import { HelpIcon } from "@patternfly/react-icons";
import { FunctionComponent } from "react";
import "./FieldHintPopover.scss";

interface FieldHintPopoverProps {
  default?: any;
  description?: PopoverProps["bodyContent"];
}

/**
 * Returns a label tooltip element for the form or undefined if the field has no description
 * @returns
 * @param props
 */
const FieldHintPopover: FunctionComponent<FieldHintPopoverProps> = (props) => {
  if (!props.description) {
    return null;
  }

  return (
    <Popover
      aria-label="Property description"
      bodyContent={props.description}
      data-testid="property-description-popover"
      footerContent={<Text component={TextVariants.small}>Default: {props.default ?? <i>No default value</i>}</Text>}
      className="pf-v5-u-my-0"
      triggerAction="hover"
      withFocusTrap={false}
    >
      <Button
        variant="plain"
        type="button"
        aria-label="More info for field"
        className="field-hint-button"
        data-testid="field-hint-button"
      >
        <HelpIcon />
      </Button>
    </Popover>
  );
};

export default FieldHintPopover;
