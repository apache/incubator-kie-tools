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

import * as React from "react";
import { useEffect } from "react";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { applyDOMSupervisor } from "./dom";
import "./ResizerSupervisor.css";

export interface ResizerSupervisorProps {
  children?: React.ReactElement;
  isRunnerTable: boolean;
}

export const ResizerSupervisor: React.FunctionComponent<ResizerSupervisorProps> = ({ children, isRunnerTable }) => {
  const boxedExpressionEditor = useBoxedExpressionEditor();

  useEffect(() => {
    const id = setTimeout(() => {
      if (boxedExpressionEditor.editorRef.current !== null) {
        applyDOMSupervisor(isRunnerTable, boxedExpressionEditor.editorRef.current);
      }
    }, 0);
    return () => clearTimeout(id);
  }, [isRunnerTable, boxedExpressionEditor.supervisorHash, boxedExpressionEditor.editorRef]);

  return <div className={"react-resizable-supervisor"}>{children}</div>;
};
