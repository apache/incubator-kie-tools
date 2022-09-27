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

import * as React from "react";
import { Label, LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import { FileTypes } from "../../extension";

type LabelColorType = { color: LabelProps["color"]; label: string };

const swfLabel: LabelColorType = { color: "green", label: "Serverless Workflow" };
const sdLabel: LabelColorType = { color: "blue", label: "Serverless Decision" };
const dashboardLabel: LabelColorType = { color: "purple", label: "Dashboard" };

export const labelColors: Record<FileTypes, LabelColorType> = {
  [FileTypes.SW_JSON]: swfLabel,
  [FileTypes.SW_YML]: swfLabel,
  [FileTypes.SW_YAML]: swfLabel,
  [FileTypes.YARD_JSON]: sdLabel,
  [FileTypes.YARD_YML]: sdLabel,
  [FileTypes.YARD_YAML]: sdLabel,
  [FileTypes.DASH_YAML]: dashboardLabel,
  [FileTypes.DASH_YML]: dashboardLabel,
};

export function FileLabel(props: { style?: LabelProps["style"]; extension: string }) {
  const parsedExtension = props.extension.toLowerCase();
  const labelColor = labelColors[parsedExtension as FileTypes];

  return (
    <>
      {props.extension && (
        <Label style={props.style ?? {}} color={labelColor?.color ?? "grey"} data-ouia-component-id="file-type-label">
          {labelColor?.label ?? props.extension.toUpperCase()}
        </Label>
      )}
    </>
  );
}
