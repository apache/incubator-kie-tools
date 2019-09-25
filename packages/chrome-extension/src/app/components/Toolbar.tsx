/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { useContext } from "react";
import { GlobalContext } from "./GlobalContext";

export function Toolbar() {
  const [globalState, setGlobalState] = useContext(GlobalContext);

  const goFullScreen = (e: any) => {
    e.preventDefault();
    setGlobalState({ ...globalState, fullscreen: true });
  };

  const seeAsText = (e: any) => {
    e.preventDefault();
    setGlobalState({ ...globalState, textMode: true });
  };

  const seeAsKogito = (e: any) => {
    e.preventDefault();
    setGlobalState({ ...globalState, textMode: false });
  };

  return (
    <>
      {!globalState.textMode && (
        <button disabled={!globalState.textModeEnabled} className={"btn btn-sm kogito-button"} onClick={seeAsText}>
          See as source
        </button>
      )}
      {globalState.textMode && (
        <button className={"btn btn-sm kogito-button"} onClick={seeAsKogito}>
          See as diagram
        </button>
      )}
      {!globalState.textMode && (
        <button className={"btn btn-sm kogito-button"} onClick={goFullScreen}>
          Full screen
        </button>
      )}
    </>
  );
}
