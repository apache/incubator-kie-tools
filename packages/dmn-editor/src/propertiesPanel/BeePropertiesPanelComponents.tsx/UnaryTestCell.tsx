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
import { useCallback, useMemo } from "react";
import {
  ContentField,
  DescriptionField,
  ExpressionLanguageField,
  KieConstraintTypeField,
  TypeRefField,
} from "./Fields";
import { BeeMap, ExpressionPath } from "../../boxedExpressions/getBeeMap";
import { DMN15__tUnaryTests } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useDmnEditorStore } from "../../store/Store";
import { useDmnEditor } from "../../DmnEditorContext";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useUpdateBee } from "./useUpdateBee";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";

export function UnaryTestCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const { selectedObjectId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { dmnEditorRootElementRef } = useDmnEditor();

  const selectedObjectInfos = useMemo(
    () => props.beeMap?.get(selectedObjectId ?? ""),
    [props.beeMap, selectedObjectId]
  );

  const updateBee = useUpdateBee<DMN15__tUnaryTests>(
    useCallback((dmnObject, newContent) => {
      if (newContent?.["@_expressionLanguage"] !== undefined) {
        dmnObject["@_expressionLanguage"] = newContent["@_expressionLanguage"];
      }
      if (newContent.text?.__$$text !== undefined) {
        dmnObject.text ??= { __$$text: "" };
        dmnObject.text = newContent.text as { __$$text: string };
      }
      if (newContent.description?.__$$text !== undefined) {
        dmnObject.description ??= { __$$text: "" };
        dmnObject.description = newContent.description as { __$$text: string };
      }
    }, []),
    props.beeMap
  );

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tUnaryTests, [selectedObjectInfos?.cell]);

  return (
    <>
      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      <TypeRefField
        isReadonly={true}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        typeRef={cell["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
      />
      <ExpressionLanguageField
        isReadonly={props.isReadonly}
        initialValue={cell["@_expressionLanguage"] ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newExpressionLanguage: string, expressionPath: ExpressionPath[]) =>
          updateBee({ "@_expressionLanguage": newExpressionLanguage }, expressionPath)
        }
      />
      <ContentField
        isReadonly={props.isReadonly}
        initialValue={cell.text?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newText: string, expressionPath: ExpressionPath[]) =>
          updateBee({ text: { __$$text: newText } }, expressionPath)
        }
      />
      <DescriptionField
        isReadonly={props.isReadonly}
        initialValue={cell.description?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newDescription: string, expressionPath: ExpressionPath[]) =>
          updateBee({ description: { __$$text: newDescription } }, expressionPath)
        }
      />
      <KieConstraintTypeField />
    </>
  );
}
