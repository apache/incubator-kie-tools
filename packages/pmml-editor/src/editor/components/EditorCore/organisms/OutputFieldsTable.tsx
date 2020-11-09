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
import { OutputFieldEditRow, OutputFieldRow } from "../molecules";
import "./OutputFieldsTable.scss";
import { Operation } from "../../EditorScorecard";
import { EmptyStateNoOutput } from "./EmptyStateNoOutput";

interface OutputFieldsTableProps {
  activeOperation: Operation;
  onAddOutputField: () => void;
  onEditOutputField: (index: number) => void;
  onDeleteOutputField: (index: number) => void;
  activeOutputFieldIndex: number | undefined;
  activeOutputField: OutputField;
  setActiveOutputField: (_output: OutputField) => void;
  outputs: OutputField[];
  validateOutputFieldName: (index: number | undefined, name: string | undefined) => boolean;
  viewExtendedProperties: () => void;
  onCommit: () => void;
  onCancel: () => void;
}

export const OutputFieldsTable = (props: OutputFieldsTableProps) => {
  const {
    activeOperation,
    onAddOutputField,
    onEditOutputField,
    onDeleteOutputField,
    activeOutputFieldIndex,
    activeOutputField,
    setActiveOutputField,
    outputs,
    validateOutputFieldName,
    viewExtendedProperties,
    onCommit,
    onCancel
  } = props;

  const addOutputRowRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (activeOperation === Operation.CREATE_OUTPUT && addOutputRowRef.current) {
      addOutputRowRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [activeOperation]);

  const onDelete = (index: number | undefined) => {
    if (index !== undefined) {
      onDeleteOutputField(index);
    }
  };

  const onValidateOutputFieldName = (index: number | undefined, name: string | undefined): boolean => {
    return validateOutputFieldName(index, name);
  };

  return (
    <Form>
      <section>
        {outputs.map((o, index) => {
          if (activeOutputFieldIndex === index) {
            return (
              <OutputFieldEditRow
                key={index}
                activeOutputFieldIndex={index}
                activeOutputField={activeOutputField}
                setActiveOutputField={setActiveOutputField}
                validateOutputName={_name => onValidateOutputFieldName(index, _name)}
                viewExtendedProperties={viewExtendedProperties}
                onCommit={onCommit}
                onCancel={onCancel}
              />
            );
          } else {
            return (
              <OutputFieldRow
                key={index}
                activeOutputFieldIndex={index}
                activeOutputField={o}
                onEditOutputField={() => onEditOutputField(index)}
                onDeleteOutputField={() => onDelete(index)}
                isDisabled={
                  !(activeOutputFieldIndex === undefined || activeOutputFieldIndex === index) ||
                  activeOperation !== Operation.NONE
                }
              />
            );
          }
        })}
        {activeOperation === Operation.CREATE_OUTPUT && (
          <div key={undefined} ref={addOutputRowRef}>
            <OutputFieldEditRow
              key={"add"}
              activeOutputFieldIndex={undefined}
              activeOutputField={activeOutputField}
              setActiveOutputField={setActiveOutputField}
              validateOutputName={_name => onValidateOutputFieldName(undefined, _name)}
              viewExtendedProperties={viewExtendedProperties}
              onCommit={onCommit}
              onCancel={onCancel}
            />
          </div>
        )}
      </section>
      {outputs.length === 0 && activeOperation !== Operation.CREATE_OUTPUT && (
        <Bullseye>
          <EmptyStateNoOutput onAddOutputField={onAddOutputField} />
        </Bullseye>
      )}
    </Form>
  );
};
