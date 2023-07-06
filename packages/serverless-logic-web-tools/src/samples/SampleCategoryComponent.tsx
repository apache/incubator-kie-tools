/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Label, LabelProps } from "@patternfly/react-core/dist/js/components/Label";
import { FileIcon, FolderIcon, MonitoringIcon } from "@patternfly/react-icons/dist/js/icons";
import * as React from "react";
import { useMemo } from "react";
import { SampleCategory } from "./types";

export const SampleCategoryMap: Record<
  SampleCategory,
  { label: string; icon: React.ComponentClass; color: LabelProps["color"] }
> = {
  ["serverless-workflow"]: {
    label: "Serverless Workflow",
    icon: FileIcon,
    color: "orange",
  },
  ["serverless-decision"]: {
    label: "Serverless Decision",
    icon: FolderIcon,
    color: "blue",
  },
  ["dashbuilder"]: {
    label: "Dashboard",
    icon: MonitoringIcon,
    color: "purple",
  },
};

export function SampleCategoryComponent(props: { category: SampleCategory }) {
  const categoryTag = useMemo(() => SampleCategoryMap[props.category], [props.category]);

  return (
    <Label color={categoryTag.color}>
      <categoryTag.icon />
      &nbsp;&nbsp;<b>{categoryTag.label}</b>
    </Label>
  );
}
