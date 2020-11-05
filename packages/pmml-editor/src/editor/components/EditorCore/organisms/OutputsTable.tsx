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
import {
  Button,
  DataList,
  DataListAction,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  Form
} from "@patternfly/react-core";
import { FieldName, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { OutputsTableEditRow, OutputsTableRow } from "../molecules";
import "./OutputsTable.scss";

import { TrashIcon } from "@patternfly/react-icons";
import { Operation } from "../../EditorScorecard";

interface OutputsTableProps {
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  outputs: OutputField[];
  validateName: (text: string | undefined) => boolean;
  deleteOutput: (index: number) => void;
  commit: (index: number | undefined, text: string | undefined) => void;
}

export const OutputsTable = (props: OutputsTableProps) => {
  const { activeOperation, setActiveOperation, outputs, deleteOutput, validateName, commit } = props;

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

  const onCommit = (index: number | undefined, name: string | undefined, dataType: string | undefined) => {
    //Avoid commits with no change
    let output: OutputField;
    if (index === undefined) {
      output = { name: "" as FieldName, dataType: "boolean" };
    } else {
      output = outputs[index];
    }
    if (output.name !== name || output.dataType !== dataType) {
      commit(index, name);
    }

    onCancel();
  };

  const onCancel = () => {
    setEditItemIndex(undefined);
    setActiveOperation(Operation.NONE);
  };

  return (
    <div>
      <DataList className="outputs__header" aria-label="outputs header">
        <DataListItem className="outputs__header__row" key={"none"} aria-labelledby="outputs-header">
          <DataListItemRow>
            <DataListItemCells
              dataListCells={[
                <DataListCell key="0" width={4}>
                  <div>Name</div>
                </DataListCell>,
                <DataListCell key="1" width={2}>
                  <div>Optype</div>
                </DataListCell>,
                <DataListCell key="2" width={2}>
                  <div>Data Type</div>
                </DataListCell>,
                <DataListCell key="3" width={2}>
                  <div>Target Field</div>
                </DataListCell>,
                <DataListCell key="4" width={2}>
                  <div>Feature</div>
                </DataListCell>,
                <DataListCell key="5" width={2}>
                  <div>Value</div>
                </DataListCell>,
                <DataListCell key="6" width={2}>
                  <div>Rank</div>
                </DataListCell>,
                <DataListCell key="7" width={2}>
                  <div>Rank Order</div>
                </DataListCell>,
                <DataListCell key="8" width={2}>
                  <div>Segment Id</div>
                </DataListCell>,
                <DataListCell key="9" width={2}>
                  <div>Final Result?</div>
                </DataListCell>,
                <DataListAction
                  id="delete-output-header"
                  aria-label="delete header"
                  aria-labelledby="delete-output-header"
                  key="103"
                  width={1}
                >
                  {/*This is a hack to ensure the column layout is correct*/}
                  <Button variant="link" icon={<TrashIcon />} isDisabled={true} style={{ visibility: "hidden" }}>
                    &nbsp;
                  </Button>
                </DataListAction>
              ]}
            />
          </DataListItemRow>
        </DataListItem>
      </DataList>
      <Form>
        <DataList aria-label="outputs list">
          {outputs.map((output, index) => {
            if (editItemIndex === index) {
              return (
                <OutputsTableEditRow
                  key={index}
                  index={index}
                  output={output}
                  validateName={validateName}
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
                validateName={validateName}
                onCommit={(_name, _dataType) => onCommit(undefined, _name, _dataType)}
                onCancel={() => onCancel()}
              />
            </div>
          )}
        </DataList>
      </Form>
    </div>
  );
};
