/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Label, LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import * as React from "react";

type LabelColorType = { color: LabelProps["color"]; label: string };

const bpmnLabel: LabelColorType = { color: "green", label: "Workflow" };
const dmnLabel: LabelColorType = { color: "blue", label: "Decision" };
const pmmlLabel: LabelColorType = { color: "purple", label: "Scorecard" };

const labelColors = new Map<string, LabelColorType>([
  ["bpmn", bpmnLabel],
  ["bpmn2", bpmnLabel],
  ["dmn", dmnLabel],
  ["pmml", pmmlLabel],
]);

export function FileLabel(props: { style?: LabelProps["style"]; extension: string }) {
  const label = labelColors.get(props.extension.toLowerCase());
  return (
    <>
      {(label && (
        <Label style={props.style ?? {}} color={label.color ?? "grey"}>
          {label.label}
        </Label>
      )) || (
        <Label style={{ visibility: "hidden" }}>
          {/* This is here to make this component's height constant and avoid UI jumps. */}
          {props.extension || "-"}
        </Label>
      )}
    </>
  );
}
