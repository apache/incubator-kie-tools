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
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { OffIcon, OnIcon } from "@patternfly/react-icons/dist/js/icons";

export function EvaluationHighlightsBadge() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const isEvaluationHighlightsEnabled = useDmnEditorStore((s) => s.diagram.overlays.enableEvaluationHighlights);

  return (
    <aside className={"kie-dmn-editor--evaluation-highlights-panel-toggle"}>
      <Label
        icon={isEvaluationHighlightsEnabled ? <OnIcon /> : <OffIcon />}
        color={isEvaluationHighlightsEnabled ? "purple" : "grey"}
        onClick={() => {
          dmnEditorStoreApi.setState((state) => {
            state.diagram.overlays.enableEvaluationHighlights = !state.diagram.overlays.enableEvaluationHighlights;
          });
        }}
        title={"Evaluation highlights"}
      >
        Evaluation highlights: {dmnEditorStoreApi.getState().diagram.overlays.enableEvaluationHighlights ? "on" : "off"}
      </Label>
    </aside>
  );
}
