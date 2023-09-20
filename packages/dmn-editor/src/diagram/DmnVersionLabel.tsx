import * as React from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Popover, PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import { useMemo } from "react";
import { DmnMarshaller } from "@kie-tools/dmn-marshaller";

const latest: DmnMarshaller["version"] = "1.5";
const latestChangelogHref = `https://www.omg.org/spec/DMN/1.5/Beta1/PDF/changebar`;

export function DmnVersionLabel(props: { version: string }) {
  const label = useMemo(
    () => (
      <Label
        style={{ cursor: "pointer", position: "absolute", bottom: "8px", left: "8px", zIndex: 100 }}
      >{`DMN ${latest}`}</Label>
    ),
    []
  );

  if (props.version === latest) {
    return <>{label}</>;
  }

  return (
    <Popover
      aria-label="DMN version popover"
      position={PopoverPosition.top}
      headerContent={<div>Version upgraded!</div>}
      bodyContent={
        <div>
          This DMN was originally imported as DMN {props.version}, but was converted to DMN {latest} to enable new
          features.
          <a href={latestChangelogHref} target={"_blank"}>
            {`See what's new on DMN ${latest}`}.
          </a>
        </div>
      }
    >
      {label}
    </Popover>
  );
}
