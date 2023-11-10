import * as React from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Popover, PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import { useMemo } from "react";
import { DMN_LATEST_VERSION } from "@kie-tools/dmn-marshaller";

const latestChangelogHref = `https://www.omg.org/spec/DMN/1.5/Beta1/PDF/changebar`;

export function DmnVersionLabel(props: { version: string }) {
  const label = useMemo(
    () => (
      <Label
        style={{ cursor: "pointer", position: "absolute", bottom: "8px", left: "calc(50% - 34px)", zIndex: 100 }}
      >{`DMN ${DMN_LATEST_VERSION}`}</Label>
    ),
    []
  );

  if (props.version === DMN_LATEST_VERSION) {
    return <>{label}</>;
  }

  return (
    <Popover
      className={"kie-dmn-editor--version-popover"}
      aria-label="DMN version popover"
      position={PopoverPosition.top}
      headerContent={<div>Version upgraded!</div>}
      bodyContent={
        <div>
          This DMN was originally imported as DMN {props.version}, but was converted to DMN {DMN_LATEST_VERSION} to
          enable new features.
          <a href={latestChangelogHref} target={"_blank"}>
            &nbsp;{`See what's new on DMN ${DMN_LATEST_VERSION}`}.
          </a>
        </div>
      }
    >
      {label}
    </Popover>
  );
}
