import * as React from "react";

import { useMemo } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { AngleRightIcon } from "@patternfly/react-icons/dist/js/icons/angle-right-icon";
import "./PropertiesPanelHeader.css";

export function PropertiesPanelHeader(props: {
  icon?: React.ReactNode;
  title: string | React.ReactNode;
  expands?: boolean;
  fixed?: boolean;
  isSectionExpanded?: boolean;
  toogleSectionExpanded?: () => void;
  action?: React.ReactNode;
}) {
  const propertiesPanelHeaderClass = useMemo(() => {
    const className = "kie-dmn-editor--properties-panel-header";
    if (props.fixed) {
      return `${className} kie-dmn-editor--properties-panel-header-fixed`;
    }
    return className;
  }, [props.fixed]);

  return (
    <div className={propertiesPanelHeaderClass}>
      <div className={"kie-dmn-editor--properties-panel-header-items"}>
        {props.expands && (
          <div style={{ width: "40px", flexShrink: 0 }}>
            <Button
              variant={ButtonVariant.plain}
              className={"kie-dmn-editor--documentation-link--row-expand-toogle"}
              onClick={() => props.toogleSectionExpanded?.()}
            >
              {(props.isSectionExpanded && <AngleDownIcon />) || <AngleRightIcon />}
            </Button>
          </div>
        )}
        {props.icon && <div style={{ width: "40px", height: "40px", marginRight: 0, flexShrink: 0 }}>{props.icon}</div>}
        <div style={{ marginLeft: "10px", flexGrow: 1 }}>
          {typeof props.title === "string" ? <Truncate content={props.title} /> : props.title}
        </div>
        {props.action && <div style={{ width: "40px", flexShrink: 0 }}>{props.action}</div>}
      </div>
    </div>
  );
}
