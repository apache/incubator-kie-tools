/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useEffect, useMemo } from "react";
import { useDmnRunnerDispatch, useDmnRunnerState } from "./DmnRunnerContext";

interface DmnRunnerLoading {
  children?: React.ReactNode;
}

export function DmnRunnerLoading(props: DmnRunnerLoading) {
  const dmnRunnerState = useDmnRunnerState();
  const dmnRunnerDispatch = useDmnRunnerDispatch();

  const onAnimationEnd = useCallback(
    (e: React.AnimationEvent<HTMLDivElement>) => {
      e.preventDefault();
      e.stopPropagation();
      dmnRunnerDispatch.setDidUpdateInputRows(false);
      dmnRunnerDispatch.setDidUpdateOutputRows(false);
    },
    [dmnRunnerDispatch]
  );

  const loadingScreenClassName = useMemo(() => {
    if (dmnRunnerState.didUpdateInputRows && dmnRunnerState.didUpdateOutputRows) {
      return "kie-tools--dmn-runner-loading";
    }
    return "";
  }, [dmnRunnerState.didUpdateInputRows, dmnRunnerState.didUpdateOutputRows]);

  // Reset outputRowsUpdate when inputRowsUpdate is false
  useEffect(() => {
    if (!dmnRunnerState.didUpdateInputRows && dmnRunnerState.didUpdateOutputRows) {
      dmnRunnerDispatch.setDidUpdateOutputRows(false);
    }
  }, [dmnRunnerDispatch, dmnRunnerState.didUpdateInputRows, dmnRunnerState.didUpdateOutputRows]);

  return (
    <>
      {dmnRunnerState.didUpdateInputRows && dmnRunnerState.didUpdateOutputRows && (
        <div id="kie-tools--dmn-runner-loading-screen" className="kie-tools--dmn-runner-loading-screen">
          <div
            className={`kie-tools--dmn-runner-loading-screen ${loadingScreenClassName}`}
            onAnimationEnd={onAnimationEnd}
          />
        </div>
      )}
      <React.Fragment key={loadingScreenClassName}>{props.children}</React.Fragment>
    </>
  );
}
