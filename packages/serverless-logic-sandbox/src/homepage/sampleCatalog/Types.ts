/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import { labelColors } from "../../workspace/components/FileLabel";
import { FileIcon, FolderIcon, MonitoringIcon } from "@patternfly/react-icons/dist/js/icons";

export enum SampleType {
  SW_YML = "sw.yml",
  SW_YAML = "sw.yaml",
  SW_JSON = "sw.json",
  SW_PROJECT = "sw.project",
  DASH_YML = "dash.yml",
}

export type Sample = {
  name: string;
  fileName: string;
  svg: React.FunctionComponent<React.SVGProps<SVGSVGElement>>;
  description: string;
  repoUrl?: string;
  type: SampleType;
};

export const tagMap: Record<SampleType, { label: string; icon: React.ComponentClass; color: LabelProps["color"] }> = {
  [SampleType.SW_YML]: {
    label: labelColors[SampleType.SW_YML].label,
    icon: FileIcon,
    color: labelColors[SampleType.SW_YML].color,
  },
  [SampleType.SW_YAML]: {
    label: labelColors[SampleType.SW_YAML].label,
    icon: FileIcon,
    color: labelColors[SampleType.SW_YAML].color,
  },
  [SampleType.SW_JSON]: {
    label: labelColors[SampleType.SW_JSON].label,
    icon: FileIcon,
    color: labelColors[SampleType.SW_JSON].color,
  },
  [SampleType.SW_PROJECT]: {
    label: "Serverless Project",
    icon: FolderIcon,
    color: "orange",
  },
  [SampleType.DASH_YML]: {
    label: labelColors[SampleType.DASH_YML].label,
    icon: MonitoringIcon,
    color: labelColors[SampleType.DASH_YML].color,
  },
};
