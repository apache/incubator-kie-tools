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
import { useEffect, useRef, useState } from "react";
import { Bullseye, Form } from "@patternfly/react-core";
import { DataType, FieldName, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputsTableEditRow, OutputsTableRow } from "../molecules";
import "./OutputsTable.scss";
import { Operation } from "../../EditorScorecard";
import { EmptyStateNoOutput } from "./EmptyStateNoOutput";

interface OutputsTableProps {
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  outputs: OutputField[];
  addOutput: () => void;
  validateOutputName: (index: number | undefined, name: string | undefined) => boolean;
  deleteOutput: (index: number) => void;
  commit: (index: number | undefined, name: FieldName | undefined, dataType: DataType | undefined) => void;
}

export const OutputsTable = (props: OutputsTableProps) => {
  const { activeOperation, setActiveOperation, outputs, addOutput, validateOutputName, deleteOutput, commit } = props;

  const [editItemIndex, setEditItemIndex] = useState<number | undefined>(undefined);
  const addOutputRowRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (activeOperation === Operation.CREATE_OUTPUT && addOutputRowRef.current) {
      addOutputRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  const onEdit = (index: number | undefined) => {
    setEditItemIndex(index);
    setActiveOperation(Operation.UPDATE);
  };

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      deleteOutput(index);
    }
  };

  const onValidateOutputName = (index: number | undefined, name: string | undefined): boolean => {
    return validateOutputName(index, name);
  };

  const onCommit = (index: number | undefined, name: FieldName | undefined, dataType: DataType | undefined) => {
    //Avoid commits with no change
    let output: OutputField;
    if (index === undefined) {
      output = { name: "" as FieldName, dataType: "boolean" };
    } else {
      output = outputs[index];
    }
    if (output.name !== name || output.dataType !== dataType) {
      commit(index, name, dataType);
    }

    onCancel();
  };

  const onCancel = () => {
    setEditItemIndex(undefined);
    setActiveOperation(Operation.NONE);
  };

  return (
    <Form>
      <section>
        {outputs.map((output, index) => {
          if (editItemIndex === index) {
            return (
              <OutputsTableEditRow
                key={index}
                index={index}
                output={output}
                validateOutputName={_name => onValidateOutputName(index, _name)}
                onCommit={(_name, _dataType) => onCommit(index, _name, _dataType)}
                onCancel={() => onCancel()}
              />
            );
          } else {
            return (
              <OutputsTableRow
                key={index}
                index={index}
                output={output}
                onEdit={() => onEdit(index)}
                onDelete={() => onDelete(index)}
                isDisabled={
                  !(editItemIndex === undefined || editItemIndex === index) || activeOperation !== Operation.NONE
                }
              />
            );
          }
        })}
        {activeOperation === Operation.CREATE_OUTPUT && (
          <div key={undefined} ref={addOutputRowRef}>
            <OutputsTableEditRow
              key={"add"}
              index={undefined}
              output={{ name: "" as FieldName, dataType: "boolean" }}
              validateOutputName={_name => onValidateOutputName(undefined, _name)}
              onCommit={(_name, _dataType) => onCommit(undefined, _name, _dataType)}
              onCancel={() => onCancel()}
            />
          </div>
        )}
      </section>
      {outputs.length === 0 && activeOperation !== Operation.CREATE_OUTPUT && (
        <Bullseye>
          <EmptyStateNoOutput addOutput={addOutput} />
        </Bullseye>
      )}
    </Form>
  );
};
