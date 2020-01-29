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
import { FileStatusOnPr } from "./FileStatusOnPr";

export function PrToolbar(props: {
  onSeeAsDiagram: () => void;
  toggleOriginal: () => void;
  closeDiagram: () => void;
  textMode: boolean;
  showOriginalChangesToggle: boolean;
  originalDiagram: boolean;
  fileStatusOnPr: FileStatusOnPr;
}) {
  const closeDiagram = (e: any) => {
    e.preventDefault();
    props.closeDiagram();
  };

  const seeAsDiagram = (e: any) => {
    e.preventDefault();
    props.onSeeAsDiagram();
  };

  const toggleOriginal = (e: any) => {
    e.preventDefault();
    props.toggleOriginal();
  };

  return (
    <>
      {!props.textMode && (
        <button disabled={props.textMode} className={"btn btn-sm kogito-button"} onClick={closeDiagram}>
          Close diagram
        </button>
      )}

      {props.textMode && (
        <button className={"btn btn-sm kogito-button"} onClick={seeAsDiagram}>
          See as diagram
        </button>
      )}

      {!props.textMode && props.fileStatusOnPr === FileStatusOnPr.CHANGED && props.showOriginalChangesToggle && (
        <div className="BtnGroup mr-1">
          <button
            disabled={props.originalDiagram}
            className={"btn btn-sm BtnGroup-item " + (props.originalDiagram ? "disabled" : "")}
            type={"button"}
            onClick={toggleOriginal}
          >
            Original
          </button>
          <button
            disabled={!props.originalDiagram}
            className={"btn btn-sm BtnGroup-item " + (!props.originalDiagram ? "disabled" : "")}
            type={"button"}
            onClick={toggleOriginal}
          >
            Changes
          </button>
        </div>
      )}
    </>
  );
}
