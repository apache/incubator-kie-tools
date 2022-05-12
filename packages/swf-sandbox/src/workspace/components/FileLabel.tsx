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

type LabelColorType = { color: LabelProps["color"]; label: string };

const swfLabel: LabelColorType = { color: "green", label: "Serverless Workflow" };
const sdLabel: LabelColorType = { color: "blue", label: "Serverless Decision" };
const dashboardLabel: LabelColorType = { color: "purple", label: "Dashboard" };

const labelColors = new Map<string, LabelColorType>([
  ["sw.json", swfLabel],
  ["sw.yml", swfLabel],
  ["sw.yaml", swfLabel],
  ["yard.json", sdLabel],
  ["yard.yml", sdLabel],
  ["yard.yaml", sdLabel],
  ["dash.yaml", dashboardLabel],
  ["dash.yml", dashboardLabel],
]);

export function FileLabel(props: { style?: LabelProps["style"]; extension: string }) {
  return (
    <>
      {props.extension && (
        <Label style={props.style ?? {}} color={labelColors.get(props.extension)?.color ?? "grey"}>
          {labelColors.get(props.extension.toLowerCase())?.label ?? props.extension.toUpperCase()}
        </Label>
      )}
    </>
  );
}
