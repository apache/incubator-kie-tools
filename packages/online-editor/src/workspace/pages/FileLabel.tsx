import { Label } from "@patternfly/react-core/dist/js/components/Label";
import * as React from "react";

const labelColors = new Map<string, string>([
  ["bpmn", "green"],
  ["dmn", "blue"],
  ["pmml", "purple"],
]);

export function FileLabel(props: { extension: string }) {
  return (
    <>
      <Label color={(labelColors.get(props.extension) as any) ?? "grey"}>{props.extension}</Label>
    </>
  );
}
