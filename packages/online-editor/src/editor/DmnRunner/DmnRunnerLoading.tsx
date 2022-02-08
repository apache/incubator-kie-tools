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

import { useCallback, useLayoutEffect, useMemo, useState } from "react";
import * as React from "react";
import { useDmnRunnerDispatch, useDmnRunnerState } from "./DmnRunnerContext";

interface DmnRunnerLoading {
  children?: React.ReactNode;
}

export function DmnRunnerLoading(props: DmnRunnerLoading) {
  const dmnRunnerState = useDmnRunnerState();
  const dmnRunnerDispatch = useDmnRunnerDispatch();
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const onAnimationEnd = useCallback(
    (e: React.AnimationEvent<HTMLDivElement>) => {
      e.preventDefault();
      e.stopPropagation();
      setIsLoading(false);
      dmnRunnerDispatch.setInputRowsUpdated(false);
    },
    [dmnRunnerDispatch]
  );

  const loadingScreenClassName = useMemo(() => {
    if (dmnRunnerState.inputRowsUpdated) {
      return "loading";
    }
    return "";
  }, [dmnRunnerState.inputRowsUpdated]);

  useLayoutEffect(() => {
    if (dmnRunnerState.inputRowsUpdated) {
      setIsLoading(true);
    }
  }, [dmnRunnerState.inputRowsUpdated]);

  return (
    <>
      {isLoading ? (
        <div id="kie-tools--dmn-runner-loading-screen" className="kie-tools--dmn-runner-loading-screen">
          <div
            className={`kie-tools--loading-screen ${loadingScreenClassName}`}
            onAnimationEnd={onAnimationEnd}
            data-testid={"dmn-runner-loading-screen-div"}
          />
        </div>
      ) : (
        props.children
      )}
    </>
  );
}
