import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import * as React from "react";
import { useCallback, useMemo } from "react";

const latest = "1.4";

export function DmnVersionLabel(props: { version: string }) {
  const label = useMemo(
    () => <Label style={{ position: "absolute", bottom: "8px", left: "8px", zIndex: 100 }}>{`DMN ${latest}`}</Label>,
    []
  );

  if (props.version === latest) {
    return <>{label}</>;
  }

  return (
    <Popover
      aria-label="DMN version popover"
      headerContent={<div>Version upgraded!</div>}
      bodyContent={
        <div>
          This DMN was originally imported as DMN {props.version}, but was converted to DMN {latest} to enable new
          features. <a href="#">See what's new on DMN {latest}.</a>
        </div>
      }
    >
      {label}
    </Popover>
  );
}
