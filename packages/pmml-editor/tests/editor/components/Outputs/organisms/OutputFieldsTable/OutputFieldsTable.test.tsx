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
import { fireEvent, render } from "@testing-library/react";
import * as React from "react";
import { Operation, OperationContext } from "../../../../../../src/editor/components/EditorScorecard";
import OutputFieldsTable from "../../../../../../src/editor/components/Outputs/organisms/OutputFieldsTable";
import { OutputField } from "@kie-tools/pmml-editor-marshaller";
import { DataType } from "@kie-tools/pmml-editor-marshaller/src";

const setSelectedOutputIndex = jest.fn((index) => {});
const validateOutputFieldName = jest.fn((index, name) => true);
const viewExtendedProperties = jest.fn(() => {});
const onAddOutputField = jest.fn(() => {});
const onDeleteOutputField = jest.fn((index) => {});
const onCommitAndClose = jest.fn(() => {});
const onCommit = jest.fn((partial) => {});
const onCancel = jest.fn(() => {});

let outputs: OutputField[];

beforeEach(() => {
  setSelectedOutputIndex.mockReset();
  validateOutputFieldName.mockReset();
  viewExtendedProperties.mockReset();
  onAddOutputField.mockReset();
  onDeleteOutputField.mockReset();
  onCommitAndClose.mockReset();
  onCommit.mockReset();
  onCancel.mockReset();

  outputs = [
    {
      name: "output1",
      dataType: "string" as DataType,
    },
    {
      name: "output2",
      dataType: "string" as DataType,
    },
  ];
});

describe("OutputFieldsTable", () => {
  test("OutputField:Render", () => {
    const { getByTestId } = render(
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_OUTPUT,
          setActiveOperation: (operation) => {},
        }}
      >
        <OutputFieldsTable
          modelIndex={0}
          outputs={outputs}
          selectedOutputIndex={undefined}
          setSelectedOutputIndex={setSelectedOutputIndex}
          validateOutputFieldName={validateOutputFieldName}
          viewExtendedProperties={viewExtendedProperties}
          onAddOutputField={onAddOutputField}
          onDeleteOutputField={onDeleteOutputField}
          onCommitAndClose={onCommitAndClose}
          onCommit={onCommit}
          onCancel={onCancel}
        />
      </OperationContext.Provider>
    );
    const container = getByTestId("output-fields-table");
    expect(container).toMatchSnapshot();

    expect(getByTestId("output-field-n0")).not.toBeUndefined();
    expect(getByTestId("output-field-n1")).not.toBeUndefined();
  });

  test("OutputField:DeleteWithIconClick", () => {
    const onDeleteOutputFieldImpl = jest.fn((index) => {
      outputs = outputs.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {},
        }}
      >
        <OutputFieldsTable
          modelIndex={0}
          outputs={outputs}
          selectedOutputIndex={undefined}
          setSelectedOutputIndex={setSelectedOutputIndex}
          validateOutputFieldName={validateOutputFieldName}
          viewExtendedProperties={viewExtendedProperties}
          onAddOutputField={onAddOutputField}
          onDeleteOutputField={onDeleteOutputFieldImpl}
          onCommitAndClose={onCommitAndClose}
          onCommit={onCommit}
          onCancel={onCancel}
        />
      </OperationContext.Provider>
    );

    //Get the first OutputField and focus the row that will make the delete icon visible
    const outputField0 = getByTestId("output-field-n0");
    expect(outputField0).not.toBeUndefined();
    fireEvent.focus(outputField0, {});

    rerender(
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {},
        }}
      >
        <OutputFieldsTable
          modelIndex={0}
          outputs={outputs}
          selectedOutputIndex={undefined}
          setSelectedOutputIndex={setSelectedOutputIndex}
          validateOutputFieldName={validateOutputFieldName}
          viewExtendedProperties={viewExtendedProperties}
          onAddOutputField={onAddOutputField}
          onDeleteOutputField={onDeleteOutputFieldImpl}
          onCommitAndClose={onCommitAndClose}
          onCommit={onCommit}
          onCancel={onCancel}
        />
      </OperationContext.Provider>
    );

    const outputField0deleteIcon = getByTestId("output-field-n0__delete");
    expect(outputField0deleteIcon).not.toBeUndefined();
    fireEvent.click(outputField0deleteIcon, {});

    expect(onDeleteOutputFieldImpl).toBeCalledWith(0);

    rerender(
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {},
        }}
      >
        <OutputFieldsTable
          modelIndex={0}
          outputs={outputs}
          selectedOutputIndex={undefined}
          setSelectedOutputIndex={setSelectedOutputIndex}
          validateOutputFieldName={validateOutputFieldName}
          viewExtendedProperties={viewExtendedProperties}
          onAddOutputField={onAddOutputField}
          onDeleteOutputField={onDeleteOutputFieldImpl}
          onCommitAndClose={onCommitAndClose}
          onCommit={onCommit}
          onCancel={onCancel}
        />
      </OperationContext.Provider>
    );

    expect(getByTestId("output-field-n0")).not.toBeUndefined();
    expect(queryByTestId("output-field-n1")).toBeNull();
  });

  test("OutputField:DeleteWithIconKeyDown", () => {
    const onDeleteOutputFieldImpl = jest.fn((index) => {
      outputs = outputs.slice(index, 1);
    });

    const { getByTestId, queryByTestId, rerender } = render(
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {},
        }}
      >
        <OutputFieldsTable
          modelIndex={0}
          outputs={outputs}
          selectedOutputIndex={undefined}
          setSelectedOutputIndex={setSelectedOutputIndex}
          validateOutputFieldName={validateOutputFieldName}
          viewExtendedProperties={viewExtendedProperties}
          onAddOutputField={onAddOutputField}
          onDeleteOutputField={onDeleteOutputFieldImpl}
          onCommitAndClose={onCommitAndClose}
          onCommit={onCommit}
          onCancel={onCancel}
        />
      </OperationContext.Provider>
    );

    //Get the first OutputField and focus the row that will make the delete icon visible
    const outputField0 = getByTestId("output-field-n0");
    expect(outputField0).not.toBeUndefined();
    fireEvent.focus(outputField0, {});

    rerender(
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {},
        }}
      >
        <OutputFieldsTable
          modelIndex={0}
          outputs={outputs}
          selectedOutputIndex={undefined}
          setSelectedOutputIndex={setSelectedOutputIndex}
          validateOutputFieldName={validateOutputFieldName}
          viewExtendedProperties={viewExtendedProperties}
          onAddOutputField={onAddOutputField}
          onDeleteOutputField={onDeleteOutputFieldImpl}
          onCommitAndClose={onCommitAndClose}
          onCommit={onCommit}
          onCancel={onCancel}
        />
      </OperationContext.Provider>
    );

    const outputField0deleteIcon = getByTestId("output-field-n0__delete");
    expect(outputField0deleteIcon).not.toBeUndefined();
    fireEvent.keyDown(outputField0deleteIcon, { key: "Enter" });

    expect(onDeleteOutputFieldImpl).toBeCalledWith(0);

    rerender(
      <OperationContext.Provider
        value={{
          activeOperation: Operation.UPDATE_CORE,
          setActiveOperation: (operation) => {},
        }}
      >
        <OutputFieldsTable
          modelIndex={0}
          outputs={outputs}
          selectedOutputIndex={undefined}
          setSelectedOutputIndex={setSelectedOutputIndex}
          validateOutputFieldName={validateOutputFieldName}
          viewExtendedProperties={viewExtendedProperties}
          onAddOutputField={onAddOutputField}
          onDeleteOutputField={onDeleteOutputFieldImpl}
          onCommitAndClose={onCommitAndClose}
          onCommit={onCommit}
          onCancel={onCancel}
        />
      </OperationContext.Provider>
    );

    const outputField0Rendered = getByTestId("output-field-n0");
    expect(outputField0Rendered).not.toBeUndefined();
    expect(document.activeElement).toBe(outputField0Rendered);
    expect(queryByTestId("output-field-n1")).toBeNull();
  });
});
