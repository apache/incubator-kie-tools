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
import { useMemo, useRef } from "react";
import { ProcessAutomationIcon } from "@patternfly/react-icons/dist/js/icons/process-automation-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { MorphingAction } from "./MorphingAction";
import { useBpmnEditorStore } from "../../../store/StoreContext";
import "./NodeMorphingPanel.css";

export function NodeMorphingPanel<A extends MorphingAction>({
  isToggleVisible,
  isExpanded,
  setExpanded,
  actions,
  selectedActionId,
  primaryColor,
  secondaryColor,
  disabledActionIds,
}: {
  isToggleVisible: boolean;
  isExpanded: boolean;
  setExpanded: React.Dispatch<React.SetStateAction<boolean>>;
  primaryColor: string;
  secondaryColor: string;
  disabledActionIds: Set<string>;
  actions: A[];
  selectedActionId: A["id"];
}) {
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const ref = useRef<HTMLDivElement>(null);

  const buttonStyle = useMemo(
    () => ({ background: secondaryColor, color: primaryColor }),
    [primaryColor, secondaryColor]
  );

  const badgeStyle = useMemo(() => ({ background: primaryColor }), [primaryColor]);

  const toggle = React.useCallback(() => {
    setExpanded((prev) => !prev);
  }, [setExpanded]);

  return (
    <>
      {!isReadOnly && isToggleVisible && (
        <>
          <div className={`kie-bpmn-editor--node-morphing-panel-toggle`}>
            <div className={`${isExpanded ? "expanded" : ""}`} onClick={toggle}>
              <>{isExpanded ? <TimesIcon /> : <ProcessAutomationIcon />}</>
            </div>
          </div>
        </>
      )}
      {!isReadOnly && isToggleVisible && isExpanded && (
        <div ref={ref} className={"kie-bpmn-editor--node-morphing-panel"}>
          <div>
            {actions.map(({ id, key, action, icon, title }) => {
              const disabled = disabledActionIds.has(id) || selectedActionId === id;
              return (
                <div
                  key={id}
                  onClick={disabled ? undefined : action}
                  title={title}
                  style={buttonStyle}
                  className={`${selectedActionId === id ? "selected" : ""} ${disabled ? "disabled" : ""}`}
                >
                  {icon}
                  {!disabled && key && <div style={badgeStyle}>{key}</div>}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </>
  );
}
