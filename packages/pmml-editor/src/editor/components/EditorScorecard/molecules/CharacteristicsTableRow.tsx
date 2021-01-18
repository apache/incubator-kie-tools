/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { Split, SplitItem } from "@patternfly/react-core";
import { CharacteristicLabels, CharacteristicsTableAction } from "../atoms";
import { IndexedCharacteristic } from "../organisms";
import "./CharacteristicsTableRow.scss";
import "../../EditorScorecard/templates/ScorecardEditorPage.scss";
import { DataField } from "@kogito-tooling/pmml-editor-marshaller";

interface CharacteristicsTableRowProps {
  characteristic: IndexedCharacteristic;
  areReasonCodesUsed: boolean;
  isBaselineScoreRequired: boolean;
  dataFields: DataField[];
  onEdit: () => void;
  onDelete: () => void;
}

export const CharacteristicsTableRow = (props: CharacteristicsTableRowProps) => {
  const { characteristic, areReasonCodesUsed, isBaselineScoreRequired, dataFields, onEdit, onDelete } = props;

  return (
    <article
      className={"editable-item__inner"}
      tabIndex={0}
      onClick={onEdit}
      onKeyDown={e => {
        if (e.key === "Enter") {
          e.preventDefault();
          e.stopPropagation();
          onEdit();
        }
      }}
    >
      <Split hasGutter={true} style={{ height: "100%" }}>
        <SplitItem>
          <strong>{characteristic.characteristic.name}</strong>
        </SplitItem>
        <SplitItem isFilled={true}>
          <CharacteristicLabels
            activeCharacteristic={characteristic.characteristic}
            areReasonCodesUsed={areReasonCodesUsed}
            isBaselineScoreRequired={isBaselineScoreRequired}
            dataFields={dataFields}
          />
        </SplitItem>
        <SplitItem>
          <CharacteristicsTableAction onDelete={onDelete} />
        </SplitItem>
      </Split>
    </article>
  );
};
