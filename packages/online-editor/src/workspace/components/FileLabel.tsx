import { Label, LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import * as React from "react";

const labelColors = new Map<string, { color: LabelProps["color"]; label: string }>([
  ["bpmn", { color: "green", label: "Workflow" }],
  ["bpmn2", { color: "green", label: "Workflow" }],
  ["dmn", { color: "blue", label: "Decision" }],
  ["pmml", { color: "purple", label: "Scorecard" }],
]);

export function FileLabel(props: { style?: LabelProps["style"]; extension: string }) {
  return (
    <>
      {props.extension && (
        <Label style={props.style ?? {}} color={labelColors.get(props.extension)?.color ?? "grey"}>
          {labelColors.get(props.extension)?.label ?? props.extension.toUpperCase()}
        </Label>
      )}
    </>
  );
}
