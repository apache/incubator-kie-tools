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
import { Label, Split, SplitItem } from "@patternfly/react-core";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputFieldRowAction, OutputLabels } from "../atoms";
import "./OutputFieldRow.scss";

interface OutputFieldRowProps {
  activeOutputFieldIndex: number;
  activeOutputField: OutputField;
  onEditOutputField: () => void;
  onDeleteOutputField: () => void;
  isDisabled: boolean;
}

export const OutputFieldRow = (props: OutputFieldRowProps) => {
  const { activeOutputFieldIndex, activeOutputField, onEditOutputField, onDeleteOutputField, isDisabled } = props;

  return (
    <article className={`output-item output-item-n${activeOutputFieldIndex}`}>
      <Split hasGutter={true} style={{ height: "100%" }}>
        <SplitItem>
          <strong>{activeOutputField.name}</strong>
        </SplitItem>
        <SplitItem isFilled={true}>
          <Label color="blue" className="output-item__type-label">
            {activeOutputField.dataType}
          </Label>
          <OutputLabels activeOutputField={activeOutputField} />
        </SplitItem>
        <SplitItem>
          <OutputFieldRowAction
            onEditDataField={onEditOutputField}
            onDeleteDataField={onDeleteOutputField}
            disabled={isDisabled}
          />
        </SplitItem>
      </Split>
    </article>
  );
};
