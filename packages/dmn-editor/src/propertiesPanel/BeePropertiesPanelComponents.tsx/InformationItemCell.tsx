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
import { NameField, TextAreaField, TextInputField, TypeRefField } from "./Fields";
import { BeeMap, DeepPartial, getDmnObject } from "../../boxedExpressions/getBeeMap";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tInformationItem,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";
import { useDmnEditor } from "../../DmnEditorContext";
import { buildXmlHref } from "../../xml/xmlHrefs";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";

/**
 * This component implements a form to change an object with the DMN15__tInformationItem type
 * It's used for: ContextExpressionVariableCell, InvocationExpressionParametersCell and RelationExpressionHeaderCell
 */
export function InformationItemCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { selectedObjectId, activeDrgElementId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { allFeelVariableUniqueNames, nodesById } = useDmnEditorDerivedStore();
  const { dmnEditorRootElementRef } = useDmnEditor();
  const node = useMemo(
    () => (activeDrgElementId ? nodesById.get(buildXmlHref({ id: activeDrgElementId })) : undefined),
    [activeDrgElementId, nodesById]
  );
  const selectedObjectInfos = useMemo(
    () => props.beeMap?.get(selectedObjectId ?? ""),
    [props.beeMap, selectedObjectId]
  );

  const updateDmnObject = useCallback(
    (dmnObject: DMN15__tInformationItem, newContent: DeepPartial<DMN15__tInformationItem>) => {
      if (newContent?.["@_name"]) {
        dmnObject["@_name"] = newContent["@_name"];
      }
      if (newContent?.["@_typeRef"]) {
        dmnObject["@_typeRef"] = newContent["@_typeRef"];
      }
      if (newContent?.["@_label"]) {
        dmnObject["@_label"] = newContent["@_label"];
      }
      if (newContent.description?.__$$text) {
        dmnObject.description ??= { __$$text: "" };
        dmnObject.description = newContent.description as { __$$text: string };
      }
    },
    []
  );

  const updateBee = useCallback(
    (newContent: DeepPartial<DMN15__tInformationItem>, expressionPath = selectedObjectInfos?.expressionPath) => {
      dmnEditorStoreApi.setState((state) => {
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "businessKnowledgeModel") {
          const dmnObject = getDmnObject(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tBusinessKnowledgeModel)
              ?.encapsulatedLogic?.expression
          );
          dmnObject && updateDmnObject(dmnObject as DMN15__tInformationItem, newContent);
        }
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "decision") {
          const dmnObject = getDmnObject(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tDecision)?.expression
          );
          dmnObject && updateDmnObject(dmnObject as DMN15__tInformationItem, newContent);
        }
      });
    },
    [dmnEditorStoreApi, node?.data.index, selectedObjectInfos?.expressionPath, updateDmnObject]
  );

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tInformationItem, [selectedObjectInfos?.cell]);

  return (
    <>
      <NameField
        isReadonly={props.isReadonly}
        id={cell["@_id"] ?? ""}
        name={cell["@_name"] ?? ""}
        allUniqueNames={allFeelVariableUniqueNames}
        onChange={(newName: string) => updateBee({ "@_name": newName })}
      />
      <TypeRefField
        isReadonly={props.isReadonly}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        typeRef={cell["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
        onChange={(newTypeRef: string) => updateBee({ "@_typeRef": newTypeRef })}
      />
      <TextInputField
        title={"Label"}
        isReadonly={props.isReadonly}
        initialValue={cell["@_label"] ?? ""}
        onChange={(newLabel: string) => updateBee({ "@_label": newLabel })}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
      />
      <TextAreaField
        title={"Description"}
        isReadonly={props.isReadonly}
        initialValue={cell.description?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newDescription: string) => updateBee({ description: { __$$text: newDescription } })}
      />
    </>
  );
}
