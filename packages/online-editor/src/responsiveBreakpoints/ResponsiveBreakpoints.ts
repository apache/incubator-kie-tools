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

import global_breakpoint_xs from "@patternfly/react-tokens/dist/esm/global_breakpoint_xs";
import global_breakpoint_sm from "@patternfly/react-tokens/dist/esm/global_breakpoint_sm";
import global_breakpoint_md from "@patternfly/react-tokens/dist/esm/global_breakpoint_md";
import global_breakpoint_lg from "@patternfly/react-tokens/dist/esm/global_breakpoint_lg";
import global_breakpoint_xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_xl";
import global_breakpoint_2xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_2xl";

export type Breakpoint = "2xl" | "xl" | "lg" | "md" | "sm" | "xs";

export enum RelationToBreakpoint {
  Above,
  Below,
}

const getPxValue = ({ value }: { value: string }) => {
  return parseInt(value.replace("px", ""), 10);
};

export const responsiveBreakpoints: Record<Breakpoint, number> = {
  xs: getPxValue(global_breakpoint_xs),
  sm: getPxValue(global_breakpoint_sm),
  md: getPxValue(global_breakpoint_md),
  lg: getPxValue(global_breakpoint_lg),
  xl: getPxValue(global_breakpoint_xl),
  "2xl": getPxValue(global_breakpoint_2xl),
};
