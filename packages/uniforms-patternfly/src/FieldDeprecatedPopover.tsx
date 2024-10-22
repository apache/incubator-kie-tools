import { Button, Popover, Icon } from "@patternfly/react-core";
import { FunctionComponent } from "react";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
// import "./FieldDeprecatedPopover.scss";

interface FieldDeprecatedPopoverProps {
  deprecated?: any;
}

/**
 * Returns a label tooltip element for the form or undefined if the field has no description
 * @returns
 * @param props
 */
const FieldDeprecatedPopover: FunctionComponent<FieldDeprecatedPopoverProps> = (props) => {
  if (!props.deprecated) {
    return null;
  }

  return (
    <Popover
      aria-label="Property deprecation details"
      bodyContent="Deprecated"
      data-testid="property-deprecated-popover"
      className="pf-v5-u-my-0"
      triggerAction="hover"
    >
      <Button
        variant="plain"
        type="button"
        aria-label="More info for field"
        className="field-deprecated-button"
        data-testid="field-deprecated-button"
      >
        <Icon status="warning">
          <ExclamationTriangleIcon />
        </Icon>
      </Button>
    </Popover>
  );
};

export default FieldDeprecatedPopover;
