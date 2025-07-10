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
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { useDmnEditorStoreApi } from "../../store/StoreContext";

export function InfoNodePanel(props: { isVisible: boolean }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  return (
    <>
      {props.isVisible && (
        <div className={"kie-dmn-editor--info-node-panel"}>
          <div
            onClick={() =>
              dmnEditorStoreApi.setState((state) => {
                state.diagram.propertiesPanel.isOpen = true;
              })
            }
          >
            <Label className={"kie-dmn-editor--info-label"}>
              <InfoIcon style={{ width: "0.7em", height: "0.7em" }} />
            </Label>
          </div>
        </div>
      )}
    </>
  );
}
