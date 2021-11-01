import { Label } from "@patternfly/react-core/dist/js/components/Label";
import * as React from "react";

const labelColors = new Map<string, string>([
  ["bpmn", "green"],
  ["bpmn2", "green"],
  ["dmn", "blue"],
  ["pmml", "purple"],
]);

export function FileLabel(props: { extension: string }) {
  return (
    <>
      {props.extension && (
        <Label color={(labelColors.get(props.extension) as any) ?? "grey"}>{props.extension.toUpperCase()}</Label>
      )}
    </>
  );
}
