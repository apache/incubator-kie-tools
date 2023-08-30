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

import * as React from "react";
import { Label, LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import { FileTypes } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { ServerlessCategoryMap } from "./ServerlessCategoryMap";

type LabelColorType = { color: LabelProps["color"]; label: string };

export const labelColors: Record<string, LabelColorType> = {
  [FileTypes.SW_JSON]: ServerlessCategoryMap["serverless-workflow"],
  [FileTypes.SW_YML]: ServerlessCategoryMap["serverless-workflow"],
  [FileTypes.SW_YAML]: ServerlessCategoryMap["serverless-workflow"],
  [FileTypes.YARD_YML]: ServerlessCategoryMap["serverless-decision"],
  [FileTypes.YARD_YAML]: ServerlessCategoryMap["serverless-decision"],
  [FileTypes.DASH_YAML]: ServerlessCategoryMap["dashbuilder"],
  [FileTypes.DASH_YML]: ServerlessCategoryMap["dashbuilder"],
};

export function FileLabel(props: { style?: LabelProps["style"]; extension: string; labelProps?: LabelProps }) {
  const parsedExtension = props.extension.trim().length ? props.extension.toLowerCase() : "n/a";
  const labelColor = labelColors[parsedExtension as string];

  return (
    <Label
      {...props.labelProps}
      style={props.style ?? {}}
      color={labelColor?.color ?? "grey"}
      data-ouia-component-id="file-type-label"
    >
      {labelColor?.label ?? parsedExtension.toUpperCase()}
    </Label>
  );
}
