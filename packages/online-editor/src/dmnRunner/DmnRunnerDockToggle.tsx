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

import { ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import * as React from "react";
import { PanelId } from "../editor/EditorPageDockDrawer";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";
import { EditorPageDockToggleItem } from "../editor/EditorPageDockToggleItem";

interface Props {
  isSelected: boolean;
  isDisabled: boolean;
  disabledReason: string;
  onChange: (id: PanelId) => void;
}

export function DmnRunnerDockToggle(props: Props) {
  return (
    <EditorPageDockToggleItem isDisabled={props.isDisabled} disabledReason={props.disabledReason}>
      <ToggleGroupItem
        style={{
          borderLeft: "solid 1px",
          borderRadius: 0,
          borderColor: "rgb(211, 211, 211)",
          padding: "1px",
        }}
        isDisabled={props.isDisabled}
        buttonId={PanelId.DMN_RUNNER_TABLE}
        isSelected={props.isSelected}
        onChange={() => {
          if (!props.isDisabled) {
            props.onChange(PanelId.DMN_RUNNER_TABLE);
          }
        }}
        text={
          <div style={{ display: "flex" }}>
            <div style={{ paddingRight: "5px", width: "30px" }}>
              <TableIcon />
            </div>
            Run
          </div>
        }
      />
    </EditorPageDockToggleItem>
  );
}
