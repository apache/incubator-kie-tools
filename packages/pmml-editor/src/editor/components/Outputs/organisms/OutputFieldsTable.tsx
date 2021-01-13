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
import { useEffect, useRef } from "react";
import { Bullseye, Form } from "@patternfly/react-core";
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import "./OutputFieldsTable.scss";
import { Operation } from "../../EditorScorecard";
import { EmptyStateNoOutput } from "./EmptyStateNoOutput";
import OutputFieldRow from "../molecules/OutputFieldRow";
import OutputFieldEditRow from "../molecules/OutputFieldEditRow";
import { OperationContext } from "../../../PMMLEditor";

interface OutputFieldsTableProps {
  modelIndex: number;
  outputs: OutputField[];
  selectedOutputIndex: number | undefined;
  setSelectedOutputIndex: (index: number | undefined) => void;
  validateOutputFieldName: (index: number | undefined, name: string | undefined) => boolean;
  viewExtendedProperties: () => void;
  onAddOutputField: () => void;
  onDeleteOutputField: (index: number) => void;
  onCommitAndClose: () => void;
  onCommit: (partial: Partial<OutputField>) => void;
  onCancel: () => void;
}

const OutputFieldsTable = (props: OutputFieldsTableProps) => {
  const {
    outputs,
    selectedOutputIndex,
    setSelectedOutputIndex,
    validateOutputFieldName,
    viewExtendedProperties,
    onAddOutputField,
    onDeleteOutputField,
    onCommitAndClose,
    onCommit,
    onCancel
  } = props;

  const addOutputRowRef = useRef<HTMLDivElement | null>(null);

  const { activeOperation, setActiveOperation } = React.useContext(OperationContext);

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

  const onEdit = (index: number | undefined) => {
    setSelectedOutputIndex(index);
    setActiveOperation(Operation.UPDATE_OUTPUT);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      onDeleteOutputField(index);
    }
  };

  const onValidateOutputFieldName = (index: number | undefined, nameToValidate: string | undefined): boolean => {
    return validateOutputFieldName(index, nameToValidate);
  };

  return (
    <Form
      onSubmit={e => {
        e.stopPropagation();
        e.preventDefault();
      }}
    >
      <section>
        {outputs.map((o, index) => (
          <article
            key={index}
            className={`editable-item output-item-n${selectedOutputIndex} ${
              selectedOutputIndex === index ? "editable-item--editing" : ""
            }`}
          >
            {selectedOutputIndex === index && (
              <div ref={addOutputRowRef}>
                <OutputFieldEditRow
                  outputField={o}
                  validateOutputName={_name => onValidateOutputFieldName(index, _name)}
                  viewExtendedProperties={viewExtendedProperties}
                  onCommitAndClose={onCommitAndClose}
                  onCommit={onCommit}
                  onCancel={onCancel}
                />
              </div>
            )}
            {selectedOutputIndex !== index && (
              <OutputFieldRow
                outputField={o}
                onEditOutputField={() => onEdit(index)}
                onDeleteOutputField={() => onDelete(index)}
              />
            )}
          </article>
        ))}
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
