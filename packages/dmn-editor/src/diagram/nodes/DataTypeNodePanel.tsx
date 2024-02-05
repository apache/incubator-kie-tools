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
import {
  DMN15__tInformationItem,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditorStore } from "../../store/StoreContext";
import { OnCreateDataType, OnToggle, OnTypeRefChange, TypeRefSelector } from "../../dataTypes/TypeRefSelector";
import { useDmnEditor } from "../../DmnEditorContext";
import { useResolvedTypeRef } from "../../dataTypes/useResolvedTypeRef";

function stopPropagation(e: React.MouseEvent | React.KeyboardEvent) {
  e.stopPropagation();
}

export function DataTypeNodePanel(props: {
  isVisible: boolean;
  variable: DMN15__tInformationItem | undefined;
  shape: DMNDI15__DMNShape | undefined;
  onChange: OnTypeRefChange;
  onCreate?: OnCreateDataType;
  onToggle?: OnToggle;
  dmnObjectNamespace: string | undefined;
}) {
  const enableDataTypesToolbarOnNodes = useDmnEditorStore((s) => s.diagram.overlays.enableDataTypesToolbarOnNodes);

  const { dmnEditorRootElementRef } = useDmnEditor();

  const resolvedTypeRef = useResolvedTypeRef(
    props.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
    props.dmnObjectNamespace
  );

  const isExternalNode = !!props.dmnObjectNamespace;

  return (
    <>
      {props.isVisible && enableDataTypesToolbarOnNodes && (
        <div
          className={"kie-dmn-editor--data-type-node-panel"}
          // Do not allow any events to go to the node itself...
          onMouseDownCapture={stopPropagation}
          onKeyDownCapture={stopPropagation}
          onClick={stopPropagation}
          onDoubleClick={stopPropagation}
          onMouseLeave={stopPropagation}
        >
          <div style={{ background: isExternalNode ? "rgb(240, 240, 240)" : undefined }}>
            <TypeRefSelector
              zoom={0.8}
              heightRef={dmnEditorRootElementRef}
              typeRef={resolvedTypeRef}
              onChange={props.onChange}
              onCreate={props.onCreate}
              onToggle={props.onToggle}
              menuAppendTo={"parent"}
              isDisabled={isExternalNode}
            />
          </div>
        </div>
      )}
    </>
  );
}
