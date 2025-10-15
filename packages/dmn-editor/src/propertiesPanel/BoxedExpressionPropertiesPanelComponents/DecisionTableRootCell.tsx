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
import { useMemo } from "react";
import { DescriptionField, TextField, TextFieldType } from "../Fields";
import { BoxedExpressionIndex } from "../../boxedExpressions/boxedExpressionIndex";
import { DMN_LATEST__tDecisionTable } from "@kie-tools/dmn-marshaller";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { useBoxedExpressionUpdater } from "./useBoxedExpressionUpdater";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { useDmnEditorStore } from "../../store/StoreContext";
import { useDmnEditorI18n } from "../../i18n";

type DecisionTableRoot = Pick<
  Normalized<DMN_LATEST__tDecisionTable>,
  "@_label" | "description" | "@_typeRef" | "@_outputLabel" | "@_aggregation" | "@_hitPolicy" | "@_id"
>;

export function DecisionTableRootCell(props: { boxedExpressionIndex?: BoxedExpressionIndex; isReadOnly: boolean }) {
  const { i18n } = useDmnEditorI18n();
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);
  const selectedObjectInfos = useMemo(
    () => props.boxedExpressionIndex?.get(selectedObjectId ?? ""),
    [props.boxedExpressionIndex, selectedObjectId]
  );

  const updater = useBoxedExpressionUpdater<DecisionTableRoot>(selectedObjectInfos?.expressionPath ?? []);

  const cell = useMemo(() => selectedObjectInfos?.cell as DecisionTableRoot, [selectedObjectInfos?.cell]);

  return (
    <>
      <FormGroup label={i18n.propertiesPanel.id}>
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      <TextField
        type={TextFieldType.TEXT_INPUT}
        title={i18n.propertiesPanel.hitPolicy}
        isReadOnly={true}
        initialValue={cell["@_hitPolicy"] ?? ""}
      />
      {cell["@_hitPolicy"] === "COLLECT" && (
        <TextField
          type={TextFieldType.TEXT_INPUT}
          title={i18n.propertiesPanel.aggregation}
          isReadOnly={true}
          initialValue={cell["@_aggregation"] ?? "<None>"}
        />
      )}
      <TextField
        type={TextFieldType.TEXT_INPUT}
        title={i18n.propertiesPanel.outputLabel}
        placeholder={i18n.propertiesPanel.outputLabelPlaceholder}
        isReadOnly={props.isReadOnly}
        initialValue={cell["@_outputLabel"] ?? ""}
        onChange={(newOutputLabel: string) =>
          updater((dmnObject) => {
            dmnObject["@_outputLabel"] = newOutputLabel;
          })
        }
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
      />
      <DescriptionField
        isReadOnly={props.isReadOnly}
        initialValue={cell.description?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newDescription: string) =>
          updater((dmnObject) => {
            dmnObject.description ??= { __$$text: "" };
            dmnObject.description.__$$text = newDescription;
          })
        }
      />
    </>
  );
}
