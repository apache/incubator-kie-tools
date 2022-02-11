/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { Specification } from "@severlessworkflow/sdk-typescript";
import { MermaidState } from "./mermaidState";

export class MermaidDiagram {
  constructor(private workflow: Specification.Workflow) {}

  sourceCode() {
    const mermaidStateDiagramVersion = "stateDiagram-v2";
    return (
      mermaidStateDiagramVersion +
      "\n" +
      this.workflow.states
        .map((state, index) => {
          const isFirstState = index === 0;
          return new MermaidState(state, isFirstState).sourceCode();
        })
        .join("\n\n")
    );
  }
}
