/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useEffect, useCallback, useState, createRef } from "react";

export interface KieServerContainer {
  id: string;
  processes: string[];
}

interface SelectedValue {
  container: KieServerContainer;
  process: string;
}

export interface ProcessSelectorProps {
  containers: KieServerContainer[];
  onContainerProcessSelected: (container: string, process: string) => void;
  selectedContainer?: string;
  selectedProcess?: string;
}

export function ProcessSelector(props: ProcessSelectorProps) {
  const containerSelectRef = createRef<HTMLSelectElement>();
  const titleRef = createRef<HTMLDivElement>();
  const bodyRef = createRef<HTMLDivElement>();
  const processSelectRef = createRef<HTMLSelectElement>();
  const [selectedValue, setSelectedValue] = useState<SelectedValue>();

  const onTitleClicked = useCallback((e: any) => {
    titleRef.current?.classList.toggle("active");
    const bodyRefEl = bodyRef.current;
    if (bodyRefEl) {
      const bodyHidden = bodyRefEl.style.display === "none";
      bodyRefEl.style.display = bodyHidden ? "block" : "none";
    }
  }, [titleRef, bodyRef]);

  const onContainerSelected = useCallback(
    (e: any) => {
      const containerName = containerSelectRef.current?.value;
      const selectedContainer = props.containers.filter(c => c.id === containerName)[0];
      props.onContainerProcessSelected(selectedContainer.id, selectedContainer.processes[0]);
      setSelectedValue({ container: selectedContainer, process: selectedContainer.processes[0] });
    },
    [selectedValue, containerSelectRef.current]
  );
  const onProcessSelected = useCallback(
    (e: any) => props.onContainerProcessSelected(selectedValue?.container.id!, processSelectRef.current?.value!),
    [selectedValue, processSelectRef.current]
  );
  useEffect(() => {
    const containers = props.containers;
    if (containers?.length > 0) {
      const selectedContainer = props.selectedContainer
        ? containers.filter(c => c.id === props.selectedContainer)[0]
        : containers[0];
      setSelectedValue({
        container: selectedContainer,
        process: props.selectedProcess
          ? selectedContainer.processes.filter(p => p === props.selectedProcess)[0]
          : selectedContainer.processes[0]
      });
    }
  }, [props.containers]);

  return (
    <div className="processSelectorContainer">
      <div className="container">
        <h5 className="cardTitle collapsible" onClick={onTitleClicked} ref={titleRef}>
          <b>Process Selector</b>
        </h5>
        <div className="cardBody" ref={bodyRef}>
          {props.containers && props.containers.length > 0 && (
            <fieldset>
              <legend>Container</legend>
              <select
                className="containerSelector"
                onChange={onContainerSelected}
                ref={containerSelectRef}
                defaultValue={props.selectedContainer || props.containers[0]?.id}
              >
                {props.containers.map((c: KieServerContainer) => (
                  <option key={c.id} value={c.id}>
                    {c.id}
                  </option>
                ))}
              </select>
            </fieldset>
          )}

          {selectedValue?.container?.processes?.length! > 0 && (
            <fieldset>
              <legend>Process</legend>
              <select
                onChange={onProcessSelected}
                ref={processSelectRef}
                defaultValue={props.selectedProcess || selectedValue?.container?.processes[0]}
              >
                {selectedValue?.container?.processes.map(p => (
                  <option key={p} value={p}>
                    {p}
                  </option>
                ))}
              </select>
            </fieldset>
          )}
        </div>
      </div>
    </div>
  );
}
