/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Label, LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import * as React from "react";

const labelColors = new Map<string, { color: LabelProps["color"]; label: string }>([
  ["bpmn", { color: "green", label: "Workflow" }],
  ["bpmn2", { color: "green", label: "Workflow" }],
  ["dmn", { color: "blue", label: "Decision" }],
  ["pmml", { color: "purple", label: "Scorecard" }],
  ["json", { color: "orange", label: "Serverless Workflow" }],
  ["yml", { color: "orange", label: "Serverless Workflow" }],
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
