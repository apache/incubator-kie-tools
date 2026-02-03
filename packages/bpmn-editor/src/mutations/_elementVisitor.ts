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

import {
  BPMN20__tDefinitions,
  BPMN20__tLane,
  BPMN20__tProcess,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { Normalized } from "../normalization/normalize";

export type FoundElement<F> = {
  owner: ElementOwner;
  element: F;
  array: ElementVisitorArgs["element"][];
  index: number;
};

type ElementOwner = Normalized<
  | ElementFilter<Unpacked<NonNullable<BPMN20__tDefinitions["rootElement"]>>, "process">
  | ElementFilter<
      Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
      "subProcess" | "adHocSubProcess" | "transaction"
    >
>;

type ElementVisitorArgs = {
  element: Normalized<Unpacked<NonNullable<BPMN20__tProcess["flowElement"] | BPMN20__tProcess["artifact"]>>>;
  index: number;
  array: ElementVisitorArgs["element"][];
  owner: ElementOwner;
};

type LaneOwner = Normalized<
  Unpacked<ElementFilter<Unpacked<NonNullable<BPMN20__tDefinitions["rootElement"]>>, "process">["laneSet"]>
>;

type LaneVisitorArgs = {
  lane: BPMN20__tLane;
  index: number;
  array: LaneVisitorArgs["lane"][];
  owner: LaneOwner;
};

/**
 * Recursive method that will visit flowElements and artifacts inside root and/or deeply nested sub processes.
 *
 * Returning `false` on the `visitor` callback will abort the visiting. Returning nothing, `undefined`, `true` will proceed normally.
 */
export function visitFlowElementsAndArtifacts(
  process: ElementOwner,
  visitor: (args: ElementVisitorArgs) => boolean | void
) {
  for (let i = 0; i < (process.flowElement ?? []).length; i++) {
    const f = process.flowElement![i];
    const ret = visitor({ element: f, index: i, owner: process, array: process.flowElement! });
    if (ret === false) {
      break;
    }
    if (f.__$$element === "subProcess" || f.__$$element === "adHocSubProcess" || f.__$$element === "transaction") {
      visitFlowElementsAndArtifacts(f, visitor);
    }
  }

  for (let i = 0; i < (process.artifact ?? []).length; i++) {
    visitor({ element: process.artifact![i], index: i, owner: process, array: process.artifact! });
  }
}

export function visitLanes(process: ElementOwner, visitor: (args: LaneVisitorArgs) => boolean | void) {
  for (let i = 0; i < (process.laneSet ?? []).length; i++) {
    const f = process.laneSet![i];
    for (let j = 0; j < (f.lane ?? []).length; j++) {
      const ret = visitor({ lane: f!.lane![j], index: i, owner: f, array: f.lane! });
      if (ret === false) {
        break;
      }
    }
  }
}
