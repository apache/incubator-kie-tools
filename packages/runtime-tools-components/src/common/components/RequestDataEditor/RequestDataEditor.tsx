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
import { useEffect, useImperativeHandle, useMemo, useRef } from "react";
import {
  RequestDataEditorController,
  RequestDataEditorApi,
  RequestDataEditorOperation,
} from "./RequestDataEditorController";
import { EditorTheme } from "@kie-tools-core/editor/dist/api";
import "../styles.css";

interface Props {
  content: string;
  onContentChange: (args: { content: string; operation: RequestDataEditorOperation }) => void;
  isReadOnly: boolean;
}

const RefForwardingRequestDataEditor: React.ForwardRefRenderFunction<RequestDataEditorApi | undefined, Props> = (
  { content, onContentChange, isReadOnly },
  forwardedRef
) => {
  const container = useRef<HTMLDivElement>(null);

  const controller: RequestDataEditorApi = useMemo<RequestDataEditorApi>(() => {
    return new RequestDataEditorController(content, onContentChange, isReadOnly);
  }, [content, onContentChange, isReadOnly]);

  /*useEffect(() => {
    controller.forceRedraw();
  }, [controller]);*/

  useEffect(() => {
    if (container.current) {
      controller.show(container.current, EditorTheme.LIGHT);
    }

    return () => {
      controller.dispose();
    };
  }, [content, controller]);

  useImperativeHandle(forwardedRef, () => controller, [controller]);

  return <div className={"kogito-request-data-editor-container"} ref={container} />;
};

export const RequestDataEditor = React.forwardRef(RefForwardingRequestDataEditor);
