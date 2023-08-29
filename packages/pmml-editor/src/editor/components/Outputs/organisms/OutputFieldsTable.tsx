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
import { useEffect, useRef, useState } from "react";
import { Form } from "@patternfly/react-core/dist/js/components/Form";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { OutputField } from "@kie-tools/pmml-editor-marshaller";
import { Operation, useOperation } from "../../EditorScorecard";
import { EmptyStateNoOutput } from "./EmptyStateNoOutput";
import OutputFieldRow from "../molecules/OutputFieldRow";
import OutputFieldEditRow from "../molecules/OutputFieldEditRow";
import "./OutputFieldsTable.scss";
import { Interaction } from "../../../types";

interface OutputFieldsTableProps {
  modelIndex: number;
  outputs: OutputField[];
  selectedOutputIndex: number | undefined;
  setSelectedOutputIndex: (index: number | undefined) => void;
  validateOutputFieldName: (index: number | undefined, name: string) => boolean;
  viewExtendedProperties: () => void;
  onAddOutputField: () => void;
  onDeleteOutputField: (index: number) => void;
  onCommitAndClose: () => void;
  onCommit: (partial: Partial<OutputField>) => void;
  onCancel: () => void;
}

const OutputFieldsTable = (props: OutputFieldsTableProps) => {
  const {
    modelIndex,
    outputs,
    selectedOutputIndex,
    setSelectedOutputIndex,
    validateOutputFieldName,
    viewExtendedProperties,
    onAddOutputField,
    onDeleteOutputField,
    onCommitAndClose,
    onCommit,
    onCancel,
  } = props;

  const addOutputRowRef = useRef<HTMLDivElement | null>(null);

  const [outputFieldFocusIndex, setOutputFieldFocusIndex] = useState<number | undefined>(undefined);

  const { activeOperation, setActiveOperation } = useOperation();

  useEffect(() => {
    if (activeOperation === Operation.UPDATE_OUTPUT && addOutputRowRef.current) {
      addOutputRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  //Exit "edit mode" when the User adds a new entry and then immediately undoes it.
  useEffect(() => {
    if (selectedOutputIndex === outputs.length) {
      setSelectedOutputIndex(undefined);
      setActiveOperation(Operation.NONE);
    }
  }, [outputs, selectedOutputIndex]);

  //Set the focus on a OutputField as required
  useEffect(() => {
    if (outputFieldFocusIndex !== undefined) {
      document.querySelector<HTMLElement>(`#output-field-n${outputFieldFocusIndex}`)?.focus();
    }
  }, [outputs, outputFieldFocusIndex]);

  const onEdit = (index: number | undefined) => {
    setSelectedOutputIndex(index);
    setActiveOperation(Operation.UPDATE_OUTPUT);
  };

  const handleDelete = (index: number, interaction: Interaction) => {
    onDelete(index);
    if (interaction === "mouse") {
      //If the OutputField was deleted by clicking on the delete icon we need to blur
      //the element otherwise the CSS :focus-within persists on the deleted element.
      //See https://issues.redhat.com/browse/FAI-570 for the root cause.
      if (document.activeElement instanceof HTMLElement) {
        document.activeElement?.blur();
      }
    } else if (interaction === "keyboard") {
      //If the OutputField was deleted by pressing enter on the delete icon when focused
      //we need to set the focus to the next OutputField. The index of the _next_ item
      //is identical to the index of the deleted item.
      setOutputFieldFocusIndex(index);
    }
    setSelectedOutputIndex(undefined);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      onDeleteOutputField(index);
    }
  };

  const onValidateOutputFieldName = (index: number | undefined, nameToValidate: string): boolean => {
    return validateOutputFieldName(index, nameToValidate);
  };

  return (
    <Form
      data-testid="output-fields-table"
      onSubmit={(e) => {
        e.stopPropagation();
        e.preventDefault();
      }}
    >
      <section>
        {outputs.map((o, index) => {
          const isRowInEditMode = selectedOutputIndex === index && activeOperation === Operation.UPDATE_OUTPUT;
          return (
            <article
              key={index}
              className={`editable-item output-item-n${index} ${isRowInEditMode ? "editable-item--editing" : ""}`}
            >
              {isRowInEditMode && (
                <div ref={addOutputRowRef}>
                  <OutputFieldEditRow
                    modelIndex={modelIndex}
                    outputField={o}
                    outputFieldIndex={index}
                    validateOutputName={(_name) => onValidateOutputFieldName(index, _name)}
                    viewExtendedProperties={viewExtendedProperties}
                    onCommitAndClose={onCommitAndClose}
                    onCommit={onCommit}
                    onCancel={onCancel}
                  />
                </div>
              )}
              {!isRowInEditMode && (
                <OutputFieldRow
                  modelIndex={modelIndex}
                  outputField={o}
                  outputFieldIndex={index}
                  onEditOutputField={() => onEdit(index)}
                  onDeleteOutputField={(interaction) => handleDelete(index, interaction)}
                />
              )}
            </article>
          );
        })}
      </section>
      {outputs.length === 0 && (
        <Bullseye>
          <EmptyStateNoOutput onAddOutputField={onAddOutputField} />
        </Bullseye>
      )}
    </Form>
  );
};

export default OutputFieldsTable;
